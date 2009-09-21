package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Hours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.AddressType.GeoCoordinates;

import com.domainlanguage.time.TimePoint;

/**
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class EniroUnitMapper implements ContextMapper {
  
  /**
   * 
   * @author David Bennehult & Joakim Olsson
   *
   */
  enum ADDRESS_TYPE {
    GOODS("goods"), DELIVERY("delivery"), POST("post"), BILLING("billing");
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
    Unit unit = unitComposition.getEniroUnit();
    unit.setId(dirContextOperations.getStringAttribute("hsaIdentity"));
    unit.setName(getUnitName(dirContextOperations));
    unit.getTextOrImageOrAddress().add(generateAddress(dirContextOperations));
    unit.getTextOrImageOrAddress().add(getPublicTelephoneType(dirContextOperations));
    return unitComposition;
  }

  private String getUnitName(DirContextOperations dirContextOperations) {
    String name = dirContextOperations.getStringAttribute("ou");
    // Is a function, name is the cn attribute instead.
    if (name == null || name.length() > 0) {
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
    return hours;
  }

  private TelephoneType getPublicTelephoneType(DirContextOperations dirContextOperations) {
    TelephoneType telephoneType = new TelephoneType();
    telephoneType.setType(PHONE_TYPE.FIXED.value);
    telephoneType.getHours().add(generateHoursObject(dirContextOperations.getStringAttribute("hsaTelephoneTime")));
    telephoneType.getTelephoneNumber().add(dirContextOperations.getStringAttribute("hsaPublicTelephoneNumber"));
    return telephoneType;
  }

  private Address generateAddress(DirContextOperations dirContextOperations) {
    Address address = new Address();
    address.getHours().add(generateHoursObject(dirContextOperations.getStringAttribute("hsaSurgeryHours")));
    // Set address type
    address.setType(ADDRESS_TYPE.DELIVERY.value);
    // Set address
    address.setStreetName(dirContextOperations.getStringAttribute(""));
    address.setStreetNumber(dirContextOperations.getStringAttribute(""));
    address.setCity(dirContextOperations.getStringAttribute(""));
    // Get geoCoordinates
    address.setGeoCoordinates(generateGeoCoordinatesObject(dirContextOperations.getStringAttribute("hsaGeographicalCoordinates")));
    return address;
  }

  private GeoCoordinates generateGeoCoordinatesObject(String value) {
    GeoCoordinates geoCoordinates = new GeoCoordinates();
    String[] xAndYValues = value.split(",");
    String xValue = xAndYValues[0].replaceAll("\\D", "");
    String yValue = xAndYValues[1].replaceAll("\\D", "");
    geoCoordinates.setXpos(Long.valueOf(xValue));
    geoCoordinates.setYpos(Long.valueOf(yValue));
    return geoCoordinates;
  }

  private void setMetaAttributes(UnitComposition unitComposition, DirContextOperations dirContextOperations) {
    unitComposition.setCreateTimePoint(createTimePoint(dirContextOperations.getStringAttribute("createTimeStamp")));
    unitComposition.setModifyTimePoint(createTimePoint(dirContextOperations.getStringAttribute("vgrModifyTimestamp")));
    unitComposition.setDn(dirContextOperations.getDn().toString());
  }

  private TimePoint createTimePoint(String dateValue) {
    TimePoint timePoint = null;
    if (dateValue != null && dateValue.length() > 0) {
      timePoint = TimePoint.parseFrom(dateValue, "yyyyMMddHHmmss", TimeZone.getDefault());
    }
    return timePoint;
  }
  

  // private void setStreetNameAndNumberForAddress(Address address, String hsaAddress) {
  // Pattern patternStreetName = Pattern.compile("\\D+");
  // Matcher matcherStreetName = patternStreetName.matcher(hsaAddress);
  // Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
  // Matcher matcherStreetNb = patternStreetNb.matcher(hsaAddress);
  //
  // if (matcherStreetName.find()) {
  // String streetName = matcherStreetName.group().trim();
  // address.setStreetName(streetName);
  // }
  // if (matcherStreetNb.find()) {
  // String streetNb = matcherStreetNb.group();
  // address.setStreetNumber(streetNb);
  // }
  // }
  //
  // private String stripEndingCommaAndSpace(String inputString) {
  // String result = inputString;
  // if (result.endsWith(", ")) {
  // result = result.substring(0, result.length() - 2);
  // }
  // return result;
  // }

}
