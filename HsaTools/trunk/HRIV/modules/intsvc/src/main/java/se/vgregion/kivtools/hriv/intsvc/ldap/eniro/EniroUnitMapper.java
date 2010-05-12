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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Hours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneHours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
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
   * Enumeration of type of address.
   */
  enum ADDRESS_TYPE {
    GOODS("goods"), DELIVERY("delivery"), POST("post"), BILLING("billing"), VISIT("visit");
    private String value;

    ADDRESS_TYPE(String value) {
      this.value = value;
    }
  }

  /**
   * Enumeration of type of hours.
   */
  enum HOURS_TYPE {
    OPEN("open"), VISIT("visit"), DROP_IN("dropin"), CLOSED("closed");
    private String value;

    HOURS_TYPE(String value) {
      this.value = value;
    }
  }

  /**
   * Enumeration of type of telephone hours.
   */
  enum TELEPHONE_HOURS_TYPE {
    PHONEOPEN("phoneopen"), PHONE("phone"), VOICEMAIL("voicemail");
    private String value;

    TELEPHONE_HOURS_TYPE(String value) {
      this.value = value;
    }
  }

  /**
   * Enumeration of type of phone.
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

  private TelephoneType getPublicTelephoneType(DirContextOperationsHelper context) {
    String publicTelephoneNumber = context.getString("hsaPublicTelephoneNumber");
    TelephoneType telephoneType = null;

    if (!StringUtil.isEmpty(publicTelephoneNumber)) {
      PhoneNumber phoneNumber = PhoneNumber.createPhoneNumber(publicTelephoneNumber);
      telephoneType = new TelephoneType();
      telephoneType.setType(PHONE_TYPE.FIXED.value);
      telephoneType.getTelephoneNumber().add(phoneNumber.getFormattedPhoneNumber().toString());
      TelephoneHourConverter telephoneHourConverter = new TelephoneHourConverter(TELEPHONE_HOURS_TYPE.PHONEOPEN.value, context.getStrings("hsaTelephoneTime"));
      telephoneType.getTelephoneHours().addAll(telephoneHourConverter.getResult());
    }
    return telephoneType;
  }

  private Address generateAddress(DirContextOperationsHelper context) {
    Address address = null;
    if (context.hasAttribute("hsaStreetAddress")) {
      address = createBaseAddress(context.getString("hsaStreetAddress"));
      HourConverter hourConverter = new HourConverter(HOURS_TYPE.VISIT.value, context.getStrings("hsaSurgeryHours"));
      address.getHours().addAll(hourConverter.getResult());
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

  /**
   * Helper-class for converting a list of strings to a list of Hour/TelephoneHours objects. The strings should follow the format [fromDay]-[toDay]#[fromHour]#[toHour], e.g 1-5#08:00#17:00.
   * 
   * @param <T> The type of object in the result list.
   */
  private abstract static class AbstractHourConverter<T> {
    private final String type;
    private final List<String> values;

    AbstractHourConverter(final String type, final List<String> values) {
      this.type = type;
      this.values = values;
    }

    List<T> getResult() {
      List<T> hoursList = new ArrayList<T>();

      for (String value : values) {
        if (!StringUtil.isEmpty(value)) {
          String[] hoursInfo = value.split("#");

          if (hoursInfo.length == 3) {
            String[] dayFromAndTo = hoursInfo[0].split("-");
            String dayFrom = dayFromAndTo[0];
            String dayTo;
            if (dayFromAndTo.length > 1) {
              dayTo = dayFromAndTo[1];
            } else {
              dayTo = dayFromAndTo[0];
            }
            String timeFrom = hoursInfo[1];
            String timeTo = hoursInfo[2];
            hoursList.add(buildResultEntry(type, dayFrom, dayTo, timeFrom, timeTo));
          }
        }
      }
      return hoursList;
    }

    /**
     * Method for building a result entry.
     * 
     * @param hourType The type of the result.
     * @param dayFrom The start-day.
     * @param dayTo The end-day.
     * @param timeFrom The start-time.
     * @param timeTo The end-time.
     * @return a populated result entry.
     */
    protected abstract T buildResultEntry(final String hourType, final String dayFrom, final String dayTo, final String timeFrom, final String timeTo);
  }

  /**
   * Implementation of AbstractHourConverter which generates Hours-objects.
   */
  private static class HourConverter extends AbstractHourConverter<Hours> {
    private Log logger = LogFactory.getLog(this.getClass());

    HourConverter(String type, List<String> values) {
      super(type, values);
    }

    @Override
    protected Hours buildResultEntry(String hourType, String dayFrom, String dayTo, String timeFrom, String timeTo) {
      Hours hours = new Hours();
      hours.setType(hourType);
      hours.setDayFrom(Integer.valueOf(dayFrom));
      hours.setDayTo(Integer.valueOf(dayTo));
      try {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar fromTimeGreg = datatypeFactory.newXMLGregorianCalendar("2000-01-20T" + timeFrom + ":00Z");
        XMLGregorianCalendar toTimeGreg = datatypeFactory.newXMLGregorianCalendar("2000-01-20T" + timeTo + ":00Z");
        hours.setTimeFrom(fromTimeGreg);
        hours.setTimeTo(toTimeGreg);
      } catch (DatatypeConfigurationException e) {
        logger.fatal("Unable to get a DatatypeFactory instance", e);
      }
      return hours;
    }
  }

  /**
   * Implementation of AbstractHourConverter which generates TelephoneHours-objects.
   */
  private static class TelephoneHourConverter extends AbstractHourConverter<TelephoneHours> {
    private Log logger = LogFactory.getLog(this.getClass());

    TelephoneHourConverter(String type, List<String> values) {
      super(type, values);
    }

    @Override
    protected TelephoneHours buildResultEntry(String hourType, String dayFrom, String dayTo, String timeFrom, String timeTo) {
      TelephoneHours hours = new TelephoneHours();
      hours.setType(hourType);
      hours.setDayFrom(Integer.valueOf(dayFrom));
      hours.setDayTo(Integer.valueOf(dayTo));
      try {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar fromTimeGreg = datatypeFactory.newXMLGregorianCalendar("2000-01-20T" + timeFrom + ":00Z");
        XMLGregorianCalendar toTimeGreg = datatypeFactory.newXMLGregorianCalendar("2000-01-20T" + timeTo + ":00Z");
        hours.setTimeFrom(fromTimeGreg);
        hours.setTimeTo(toTimeGreg);
      } catch (DatatypeConfigurationException e) {
        logger.fatal("Unable to get a DatatypeFactory instance", e);
      }
      return hours;
    }
  }
}
