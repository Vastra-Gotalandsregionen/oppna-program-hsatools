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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro.lth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
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
public class EniroUnitMapperLTH implements ContextMapper {
  private final List<String> nonCareCenter;
  private final String locality;

  /**
   * 
   * @param locality The locality to set on the found units.
   * @param businessClassificationCodes for select non care centers.
   */
  public EniroUnitMapperLTH(String locality) {
    this.locality = locality;
    this.nonCareCenter = Arrays.asList("1000", "1001", "1002", "1003", "1004", "1005", "1006", "1007", "1008", "1009", "1010", "1011", "1012", "1013", "1014", "1015", "1016", "1017", "1018", "1019",
        "1020", "1021", "1022", "1023", "1024", "1025", "1026", "1027", "1028", "1100", "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112", "1113", "1114",
        "1115", "1116", "1117", "1118", "1119", "1120", "1121", "1122", "1123", "1124", "1125", "1126", "1127", "1128", "1129", "1130", "1131", "1132", "1133", "1134", "1136", "1137", "1138", "1139",
        "1202", "1203", "1204", "1205", "1206", "1207", "1208", "1209", "1210", "1211", "1212", "1213", "1214", "1215", "1216", "1217", "1218", "1219", "1220", "1221", "1222", "1223", "1224", "1227",
        "1228", "1229", "1230", "1231", "1232", "1302", "1303", "1304", "1306", "1307", "1308", "1310", "1311", "1312", "1313", "1314", "1315", "1316", "1317", "1318", "1319", "1320", "1321", "1322",
        "1323", "1324", "1325", "1326", "1327", "1328", "1329", "1330", "1331", "1332", "1333", "1334", "1335", "1336", "1337", "1338", "1339", "1340", "1341", "1342", "1343", "1400", "1402", "1403",
        "1404", "1405", "1406", "1407", "1408", "1409", "1410", "1411", "1412", "1413", "1414", "1500", "1504", "1505", "1506", "1507", "1509", "1512", "1513", "1514", "1515", "1518", "1519", "1600",
        "1603", "1604", "1605", "1606", "1607", "1608", "1609", "1611", "1614", "1615", "1616", "1617", "1618", "1619", "1702", "1703", "1704", "1705", "1706", "1707", "1708", "1709", "1710", "1711",
        "1712", "1713", "1714", "1715", "1716", "1717", "1718", "1719", "1800", "1801", "1802", "1804", "1807", "1808", "1810", "1812", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008",
        "2009", "2010", "2011", "2012", "2016", "2017", "2018", "2019", "2020", "2021", "2102", "2103", "2106", "2107", "2108", "2202", "2203", "2204", "2205");
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
    this.setMetaAttributes(unitComposition, context);
    // Fill unit with data.
    Unit unit = unitComposition.getEniroUnit();
    unit.setId(context.getString("hsaIdentity"));
    unit.setName(this.getUnitName(context));
    unit.setRoute(StringUtil.concatenate(context.getStrings("route")));
    unit.setLocality(this.locality);

    Address address = this.generateAddress(context);
    if (address != null) {
      unit.getTextOrImageOrAddress().add(address);
    }
    TelephoneType telephone = this.getPublicTelephoneType(context);
    if (telephone != null) {
      unit.getTextOrImageOrAddress().add(telephone);
    }
    unit.getTextOrImageOrAddress().add(this.createBusinessClassification(context.getString("businessClassificationCode")));
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
    String publicTelephoneNumber = context.getString("lthTelephoneNumber");
    TelephoneType telephoneType = null;

    if (!StringUtil.isEmpty(publicTelephoneNumber)) {
      PhoneNumber phoneNumber = PhoneNumber.createPhoneNumber(publicTelephoneNumber).getFormattedPhoneNumber();
      telephoneType = new TelephoneType();
      telephoneType.setType(PHONE_TYPE.FIXED.value);
      telephoneType.getAreaCode().add(phoneNumber.getAreaCode());
      telephoneType.getTelephoneNumber().add(phoneNumber.getSubscriberNumber());
      TelephoneHourConverter telephoneHourConverter = new TelephoneHourConverter(TELEPHONE_HOURS_TYPE.PHONEOPEN.value, context.getStrings("telephoneHours"));
      telephoneType.getTelephoneHours().addAll(telephoneHourConverter.getResult());
    }
    return telephoneType;
  }

  private Address generateAddress(DirContextOperationsHelper context) {
    Address address = null;
    if (context.hasAttribute("street")) {
      String addressString = context.getString("street") + "$" + context.getString("postalCode") + " " + context.getString("l");
      address = this.createBaseAddress(addressString);
      HourConverter hourConverter = new HourConverter(HOURS_TYPE.VISIT.value, context.getStrings("surgeryHours"));
      address.getHours().addAll(hourConverter.getResult());
      // Set address type
      address.setType(ADDRESS_TYPE.VISIT.value);
      // Get geoCoordinates
      address.setGeoCoordinates(this.generateGeoCoordinatesObject(context.getString("geographicalCoordinates")));
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
    String bsCode = context.getString("businessClassificationCode");
    if (this.nonCareCenter.contains(bsCode)) {
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
    private final Comparator<T> comparator;

    AbstractHourConverter(final String type, final List<String> values, Comparator<T> comparator) {
      this.type = type;
      this.values = values;
      this.comparator = comparator;
    }

    List<T> getResult() {
      List<T> hoursList = new ArrayList<T>();

      for (String value : this.values) {
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
            hoursList.add(this.buildResultEntry(this.type, dayFrom, dayTo, timeFrom, timeTo));
          }
        }
      }
      Collections.sort(hoursList, this.comparator);
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
    private final Log logger = LogFactory.getLog(this.getClass());

    HourConverter(String type, List<String> values) {
      super(type, values, new HourComparator());
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
        this.logger.fatal("Unable to get a DatatypeFactory instance", e);
      }
      return hours;
    }

    private static class HourComparator implements Comparator<Hours> {
      @Override
      public int compare(Hours o1, Hours o2) {
        int result = o1.getDayFrom().compareTo(o2.getDayFrom()) * 1000;
        result += o1.getDayTo().compareTo(o2.getDayTo()) * 100;
        result += o1.getTimeFrom().compare(o2.getTimeFrom()) * 10;
        result += o1.getTimeTo().compare(o2.getTimeTo());
        return result;
      }
    }
  }

  /**
   * Implementation of AbstractHourConverter which generates TelephoneHours-objects.
   */
  private static class TelephoneHourConverter extends AbstractHourConverter<TelephoneHours> {
    private final Log logger = LogFactory.getLog(this.getClass());

    TelephoneHourConverter(String type, List<String> values) {
      super(type, values, new TelephoneHourComparator());
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
        this.logger.fatal("Unable to get a DatatypeFactory instance", e);
      }
      return hours;
    }

    private static class TelephoneHourComparator implements Comparator<TelephoneHours> {
      @Override
      public int compare(TelephoneHours o1, TelephoneHours o2) {
        int result = o1.getDayFrom().compareTo(o2.getDayFrom()) * 1000;
        result += o1.getDayTo().compareTo(o2.getDayTo()) * 100;
        result += o1.getTimeFrom().compare(o2.getTimeFrom()) * 10;
        result += o1.getTimeTo().compare(o2.getTimeTo());
        return result;
      }
    }
  }
}
