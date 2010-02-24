/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import java.util.ArrayList;
import java.util.Arrays;
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
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
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
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);
    // Set meta data
    setMetaAttributes(unitComposition, context);
    // Fill unit with data.
    Unit unit = unitComposition.getEniroUnit();
    unit.setId(context.getString("hsaIdentity"));
    unit.setName(getUnitName(context));
    unit.setRoute(StringUtil.concatenate(context.getStrings("hsaRoute")));

    Address address = generateAddress(context);
    if (address != null) {
      unit.getTextOrImageOrAddress().add(address);
    }
    TelephoneType telephone = getPublicTelephoneType(context);
    if (telephone != null) {
      unit.getTextOrImageOrAddress().add(telephone);
    }
    unit.getTextOrImageOrAddress().add(createBusinessClassification(context.getString("hsaBusinessClassificationCode")));
    return unitComposition;
  }

  private BusinessClassification createBusinessClassification(String businesClassificationCode) {
    BusinessClassification businesClassification = new BusinessClassification();
    businesClassification.setBCCode(businesClassificationCode);
    // TODO: implement functionality for getting bsName
    businesClassification.setBCName("");
    return businesClassification;
  }

  private String getUnitName(DirContextOperationsHelper context) {
    String name = context.getString("ou");
    // Is a function, name is the cn attribute instead.
    if (StringUtil.isEmpty(name)) {
      name = context.getString("cn");
    }
    return name;
  }

  /**
   * Generate a Hours object from string of type [fromDay]-[toDay]#[fromHour]#[toHour], e.g 1-5#08:00#17:00. Multiple hours are separated in the string with a dollar-sign.
   * 
   * @param value The hours-string in the above format.
   * @return The list of generated Hours objects.
   */
  private List<Hours> generateHoursObjects(List<String> values) {
    List<Hours> hoursList = new ArrayList<Hours>();

    for (String value : values) {
      Hours hours = new Hours();
      if (!StringUtil.isEmpty(value)) {
        String[] hoursInfo = value.split("#");

        if (hoursInfo.length == 3) {
          String[] dayFromAndTo = hoursInfo[0].split("-");
          hours.setType(HOURS_TYPE.VISIT.value);
          hours.setDayFrom(Integer.valueOf(dayFromAndTo[0]));
          if (dayFromAndTo.length > 1) {
            hours.setDayTo(Integer.valueOf(dayFromAndTo[1]));
          } else {
            hours.setDayTo(Integer.valueOf(dayFromAndTo[0]));
          }
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
        hoursList.add(hours);
      }
    }
    return hoursList;
  }

  private TelephoneType getPublicTelephoneType(DirContextOperationsHelper context) {
    String publicTelephoneNumber = context.getString("hsaPublicTelephoneNumber");
    TelephoneType telephoneType = null;

    if (!StringUtil.isEmpty(publicTelephoneNumber)) {
      telephoneType = new TelephoneType();
      telephoneType.setType(PHONE_TYPE.FIXED.value);
      telephoneType.getTelephoneNumber().add(publicTelephoneNumber);
      telephoneType.getHours().addAll(generateHoursObjects(context.getStrings("hsaTelephoneTime")));
    }
    return telephoneType;
  }

  private Address generateAddress(DirContextOperationsHelper context) {
    Address address = null;
    if (context.hasAttribute("hsaStreetAddress")) {
      address = createBaseAddress(context.getString("hsaStreetAddress"));
      address.getHours().addAll(generateHoursObjects(context.getStrings("hsaSurgeryHours")));
      // Set address type
      address.setType(ADDRESS_TYPE.VISIT.value);
      // Get geoCoordinates
      address.setGeoCoordinates(generateGeoCoordinatesObject(context.getString("hsaGeographicalCoordinates")));
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

  private void setMetaAttributes(UnitComposition unitComposition, DirContextOperationsHelper context) {
    // Not used at the moment (2009-10-02)
    // unitComposition.setCreateTimePoint(createTimePoint(dirContextOperations.getStringAttribute("createTimeStamp")));
    // unitComposition.setModifyTimePoint(createTimePoint(dirContextOperations.getStringAttribute("vgrModifyTimestamp")));
    unitComposition.setDn(context.getDnString());
    String bsCode = context.getString("hsaBusinessClassificationCode");
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
      se.vgregion.kivtools.search.domain.values.Address streetAddress = AddressHelper.convertToStreetAddress(Arrays.asList(addressFields));

      address = new Address();
      address.setStreetName(streetAddress.getStreet());
      address.getPostCode().add(streetAddress.getZipCode().getFormattedZipCode().getZipCode());
      address.setCity(streetAddress.getCity());

      // Take out street name and street number.
      Pattern patternStreetName = Pattern.compile("\\D+");
      Matcher matcherStreetName = patternStreetName.matcher(streetAddress.getStreet());
      Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
      Matcher matcherStreetNb = patternStreetNb.matcher(streetAddress.getStreet());

      if (matcherStreetName.find()) {
        String streetName = matcherStreetName.group().trim();
        address.setStreetName(streetName);
      }
      if (matcherStreetNb.find()) {
        String streetNb = matcherStreetNb.group();
        address.setStreetNumber(streetNb);
      }
    }
    return address;
  }
}
