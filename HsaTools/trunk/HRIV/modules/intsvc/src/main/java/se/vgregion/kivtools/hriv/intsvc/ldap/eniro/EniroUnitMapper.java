package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Hours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class EniroUnitMapper implements ContextMapper {

  private List<String> nonCareCenter;

  /**
   * 
   * @param businessClassificationCodes for select non care centers.
   */
  public EniroUnitMapper(List<String> businessClassificationCodes) {
    this.nonCareCenter = businessClassificationCodes;
  }

  /**
   * 
   * @author David Bennehult & Joakim Olsson
   * 
   */
  enum ADDRESS_TYPE {
    GOODS("goods"), DELIVERY("delivery"), POST("post"), BILLING("billing"), VISIT("visit");
    private String value;

    ADDRESS_TYPE(String value) {
      this.value = value;
    }
  }

  /**
   * 
   * @author David Bennehult & Joakim Olsson
   * 
   */
  enum HOURS_TYPE {
    VISIT("visit"), DROP_IN("dropIn"), CLOSED("closed");
    private String value;

    HOURS_TYPE(String value) {
      this.value = value;
    }
  }

  /**
   * 
   * @author David Bennehult & Joakim Olsson
   * 
   */
  enum PHONE_TYPE {
    FIXED("fixed"), FAX("fax"), SWITCH("switch"), OTHER("other");
    private String value;

    PHONE_TYPE(String value) {
      this.value = value;
    }
  }

  @Override
  public Object mapFromContext(Object ctx) {
    UnitComposition unitComposition = new UnitComposition();
    DirContextOperations dirContextOperations = (DirContextOperations) ctx;
    // Set meta data
    setMetaAttributes(unitComposition, dirContextOperations);
    // Fill unit with data.
    Unit unit = unitComposition.getEniroUnit();
    unit.setId(dirContextOperations.getStringAttribute("hsaIdentity"));
    // unit.setLocality(dirContextOperations.getStringAttribute("l"));
    unit.setLocality("Göteborg");
    unit.setName(getUnitName(dirContextOperations));
    Address address = generateAddress(dirContextOperations);
    if (address != null) {
      unit.getTextOrImageOrAddress().add(address);
    }
    TelephoneType telephone = getPublicTelephoneType(dirContextOperations);
    if (telephone != null) {
      unit.getTextOrImageOrAddress().add(telephone);
    }
    unit.getTextOrImageOrAddress().add(createBusinessClassification(dirContextOperations.getStringAttribute("hsaBusinessClassificationCode")));
    return unitComposition;
  }

  private BusinessClassification createBusinessClassification(String businesClassificationCode) {
    BusinessClassification businesClassification = new BusinessClassification();
    businesClassification.setBCCode(businesClassificationCode);
    // TODO: implement functionality for getting bsName
    businesClassification.setBCName("");
    return businesClassification;
  }

  private String getUnitName(DirContextOperations dirContextOperations) {
    String name = dirContextOperations.getStringAttribute("ou");
    // Is a function, name is the cn attribute instead.
    if (StringUtil.isEmpty(name)) {
      name = dirContextOperations.getStringAttribute("cn");
    }
    return name;
  }

  /**
   * Generate a Hours object from string of type [fromDay]-[toDay]#[fromHour]#[toHour], e.g 1-5#08:00#17:00.
   * 
   * @param value - string
   * @return generated Hour object
   */
  private Hours generateHoursObject(String value) {
    Hours hours = new Hours();
    if (!StringUtil.isEmpty(value)) {
      String[] hoursInfo = value.split("#");

      if (hoursInfo.length == 3) {
        String[] dayFromAndTo = hoursInfo[0].split("-");
        hours.setType(HOURS_TYPE.VISIT.value);
        hours.setDayFrom(Integer.valueOf(dayFromAndTo[0]));
        hours.setDayTo(Integer.valueOf(dayFromAndTo[1]));
        try {
          String fromTime = hoursInfo[1];
          String toTime = hoursInfo[2];
          XMLGregorianCalendar fromTimeGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar("2000-01-20T" + fromTime + ":00Z");
          XMLGregorianCalendar toTimeGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar("2000-01-20T" + toTime + ":00Z");
          hours.setTimeFrom(fromTimeGreg);
          hours.setTimeTo(toTimeGreg);
        } catch (DatatypeConfigurationException e) {
          e.printStackTrace();
        }
      }
    }
    return hours;
  }

  private TelephoneType getPublicTelephoneType(DirContextOperations dirContextOperations) {
    String publicTelephoneNumber = dirContextOperations.getStringAttribute("hsaPublicTelephoneNumber");
    TelephoneType telephoneType = null;

    if (!StringUtil.isEmpty(publicTelephoneNumber)) {
      telephoneType = new TelephoneType();
      telephoneType.setType(PHONE_TYPE.FIXED.value);
      telephoneType.getTelephoneNumber().add(publicTelephoneNumber);
      telephoneType.getHours().add(generateHoursObject(dirContextOperations.getStringAttribute("hsaTelephoneTime")));
    }
    return telephoneType;
  }

  private Address generateAddress(DirContextOperations dirContextOperations) {
    Address address = createBaseAddress(dirContextOperations.getStringAttribute("hsaSedfDeliveryAddress"));
    if (address != null) {
      address.getHours().add(generateHoursObject(dirContextOperations.getStringAttribute("hsaSurgeryHours")));
      // Set address type
      address.setType(ADDRESS_TYPE.VISIT.value);
      // Get geoCoordinates
      address.setGeoCoordinates(generateGeoCoordinatesObject(dirContextOperations.getStringAttribute("hsaGeographicalCoordinates")));
    }
    return address;
  }

  private GeoCoordinates generateGeoCoordinatesObject(String value) {
    GeoCoordinates geoCoordinates = new GeoCoordinates();
    if (!StringUtil.isEmpty(value)) {
      String[] xAndYValues = value.split(",");
      String xValue = xAndYValues[0].replaceAll("\\D", "");
      String yValue = xAndYValues[1].replaceAll("\\D", "");
      geoCoordinates.setXpos(Long.valueOf(xValue));
      geoCoordinates.setYpos(Long.valueOf(yValue));
    }
    return geoCoordinates;
  }

  private void setMetaAttributes(UnitComposition unitComposition, DirContextOperations dirContextOperations) {
    // Not used at the moment (2009-10-02)
    // unitComposition.setCreateTimePoint(createTimePoint(dirContextOperations.getStringAttribute("createTimeStamp")));
    // unitComposition.setModifyTimePoint(createTimePoint(dirContextOperations.getStringAttribute("vgrModifyTimestamp")));
    unitComposition.setDn(dirContextOperations.getDn().toString());
    String bsCode = dirContextOperations.getStringAttribute("hsaBusinessClassificationCode");
    if (nonCareCenter.contains(bsCode)) {
      unitComposition.setCareType(UnitType.OTHER_CARE);
    } else {
      unitComposition.setCareType(UnitType.CARE_CENTER);
    }
  }

  // Not used at the moment (2009-10-02)
  // private TimePoint createTimePoint(String dateValue) {
  // TimePoint timePoint = null;
  // if (dateValue != null && dateValue.length() > 0) {
  // timePoint = TimePoint.parseFrom(dateValue, "yyyyMMddHHmmss", TimeZone.getDefault());
  // }
  // return timePoint;
  // }

  private Address createBaseAddress(String hsaAddress) {
    Address address = null;
    if (hsaAddress != null) {
      String[] addressFields = hsaAddress.split("\\$");
      address = new Address();
      if (addressFields.length == 6) {
        // Take out street name and street number.
        Pattern patternStreetName = Pattern.compile("\\D+");
        Matcher matcherStreetName = patternStreetName.matcher(addressFields[2]);
        Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
        Matcher matcherStreetNb = patternStreetNb.matcher(addressFields[2]);

        if (matcherStreetName.find()) {
          String streetName = matcherStreetName.group().trim();
          address.setStreetName(streetName);
        }
        if (matcherStreetNb.find()) {
          String streetNb = matcherStreetNb.group();
          address.setStreetNumber(streetNb);
        }

        address.getPostCode().add(addressFields[4]);
        address.setCity(addressFields[5]);
      }
    }
    return address;
  }
}
