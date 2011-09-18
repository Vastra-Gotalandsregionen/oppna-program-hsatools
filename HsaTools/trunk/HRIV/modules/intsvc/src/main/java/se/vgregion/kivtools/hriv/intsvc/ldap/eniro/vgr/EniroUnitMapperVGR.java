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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro.vgr;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Hours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneHours;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class EniroUnitMapperVGR {
  private final List<String> nonCareCenter;
  private final String locality;

  /**
   * 
   * @param locality The locality to set on the found units.
   * @param businessClassificationCodes for select non care centers.
   */
  public EniroUnitMapperVGR(String locality, List<String> businessClassificationCodes) {
    this.locality = locality;
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

  public UnitComposition map(se.vgregion.kivtools.search.domain.Unit source) {
    UnitComposition unitComposition = new UnitComposition();
    // Set meta data
    this.setMetaAttributes(unitComposition, source);
    // Fill unit with data.
    Unit unit = unitComposition.getEniroUnit();
    unit.setId(source.getHsaIdentity());
    unit.setName(source.getName());
    unit.setRoute(StringUtil.concatenate(source.getHsaRoute()));
    unit.setLocality(this.locality);

    Address address = this.generateAddress(source);
    if (address != null) {
      unit.getTextOrImageOrAddress().add(address);
    }
    TelephoneType telephone = this.getPublicTelephoneType(source);
    if (telephone != null) {
      unit.getTextOrImageOrAddress().add(telephone);
    }
    if (source.getHsaBusinessClassificationCode() != null) {
      for (String code : source.getHsaBusinessClassificationCode()) {
        unit.getTextOrImageOrAddress().add(this.createBusinessClassification(code));
      }
    }
    return unitComposition;
  }

  private BusinessClassification createBusinessClassification(String code) {
    BusinessClassification businesClassification = new BusinessClassification();
    businesClassification.setBCCode(code);
    // TODO: implement functionality for getting bsName
    businesClassification.setBCName("");
    return businesClassification;
  }

  private TelephoneType getPublicTelephoneType(se.vgregion.kivtools.search.domain.Unit source) {
    List<PhoneNumber> publicTelephoneNumber = source.getHsaPublicTelephoneNumber();
    TelephoneType telephoneType = null;

    if (publicTelephoneNumber != null && !publicTelephoneNumber.isEmpty()) {
      PhoneNumber phoneNumber = publicTelephoneNumber.get(0);
      telephoneType = new TelephoneType();
      telephoneType.setType(PHONE_TYPE.FIXED.value);
      telephoneType.setTelephoneNumber(phoneNumber.getFormattedPhoneNumber().toString());
      TelephoneHourConverter telephoneHourConverter = new TelephoneHourConverter(TELEPHONE_HOURS_TYPE.PHONEOPEN.value, source.getHsaTelephoneTime());
      telephoneType.getTelephoneHours().addAll(telephoneHourConverter.getResult());
    }
    return telephoneType;
  }

  private Address generateAddress(se.vgregion.kivtools.search.domain.Unit source) {
    Address address = null;
    if (source.getHsaStreetAddressIsValid()) {
      address = this.createBaseAddress(source.getHsaStreetAddress());
      HourConverter hourConverter = new HourConverter(HOURS_TYPE.VISIT.value, source.getHsaSurgeryHours());
      address.getHours().addAll(hourConverter.getResult());
      // Set address type
      address.setType(ADDRESS_TYPE.VISIT.value);
      // Get geoCoordinates
      address.setGeoCoordinates(this.generateGeoCoordinatesObject(source.getHsaGeographicalCoordinates()));
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

  private void setMetaAttributes(UnitComposition unitComposition, se.vgregion.kivtools.search.domain.Unit source) {
    unitComposition.setDn(source.getDn().toString());
    List<String> bsCodes = source.getHsaBusinessClassificationCode();
    if (bsCodes != null) {
      for (String bsCode : bsCodes) {
        if (this.nonCareCenter.contains(bsCode)) {
          unitComposition.setCareType(UnitType.OTHER_CARE);
          break;
        } else {
          unitComposition.setCareType(UnitType.CARE_CENTER);
        }
      }
    }
  }

  private Address createBaseAddress(se.vgregion.kivtools.search.domain.values.Address source) {
    Address address = null;
    if (source != null) {
      address = new Address();
      address.setStreetName(source.getStreet());
      address.setPostCode(source.getZipCode().getFormattedZipCode().getZipCode());
      address.setCity(source.getCity());

      // Take out street name and street number.
      Pattern patternStreetName = Pattern.compile("\\D+");
      Matcher matcherStreetName = patternStreetName.matcher(source.getStreet());
      Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
      Matcher matcherStreetNb = patternStreetNb.matcher(source.getStreet());

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
    private final List<WeekdayTime> values;

    AbstractHourConverter(final String type, final List<WeekdayTime> list) {
      this.type = type;
      this.values = list;
    }

    List<T> getResult() {
      List<T> hoursList = new ArrayList<T>();

      for (WeekdayTime value : this.values) {
        String dayFrom = String.valueOf(value.getStartDay());
        String dayTo = String.valueOf(value.getEndDay());
        String timeFrom = this.formatTime(value.getStartHour(), value.getStartMin());
        String timeTo = this.formatTime(value.getEndHour(), value.getEndMin());
        hoursList.add(this.buildResultEntry(this.type, dayFrom, dayTo, timeFrom, timeTo));
      }
      return hoursList;
    }

    private String formatTime(int hour, int minute) {
      return String.format("%02d:%02d", hour, minute);
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

    HourConverter(String type, List<WeekdayTime> list) {
      super(type, list);
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
  }

  /**
   * Implementation of AbstractHourConverter which generates TelephoneHours-objects.
   */
  private static class TelephoneHourConverter extends AbstractHourConverter<TelephoneHours> {
    private final Log logger = LogFactory.getLog(this.getClass());

    TelephoneHourConverter(String type, List<WeekdayTime> list) {
      super(type, list);
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
  }
}
