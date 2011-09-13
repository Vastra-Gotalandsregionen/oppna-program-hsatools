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
 */
package se.vgregion.kivtools.search.svc.impl;

import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.svc.sitemap.Address.GeoCoordinates;
import se.vgregion.kivtools.svc.sitemap.AddressType;
import se.vgregion.kivtools.svc.sitemap.EAlias;
import se.vgregion.kivtools.svc.sitemap.EAliasType;
import se.vgregion.kivtools.svc.sitemap.ObjectFactory;
import se.vgregion.kivtools.svc.sitemap.TelephoneNumber;
import se.vgregion.kivtools.svc.sitemap.TelephoneType;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Base-class for sitemap information mappers.
 */
public class AbstractSitemapMapper {
  private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

  protected static List<TelephoneNumber> mapPhoneNumbers(final List<PhoneNumber> phoneNumbers, final String label, final TelephoneType type) {
    List<TelephoneNumber> result = new ArrayList<TelephoneNumber>();

    for (PhoneNumber phoneNumber : phoneNumbers) {
      TelephoneNumber telephoneNumber = mapPhoneNumber(phoneNumber, label, type);
      result.add(telephoneNumber);
    }

    return result;
  }

  protected static TelephoneNumber mapPhoneNumber(PhoneNumber phoneNumber, final String label, final TelephoneType type) {
    TelephoneNumber telephoneNumber = OBJECT_FACTORY.createTelephoneNumber();
    telephoneNumber.setLabel(label);
    telephoneNumber.setType(type);
    String phoneNumberString = phoneNumber.getFormattedPhoneNumber().getPhoneNumber();
    if (isParseablePhoneNumber(phoneNumberString)) {
      int separatorIndex = phoneNumberString.indexOf(" - ");
      telephoneNumber.setAreaCode(phoneNumberString.substring(0, separatorIndex));
      telephoneNumber.setTelephoneNumber(phoneNumberString.substring(separatorIndex + 3));
    } else {
      telephoneNumber.setTelephoneNumber(phoneNumberString);
    }
    return telephoneNumber;
  }

  protected static boolean isParseablePhoneNumber(String phoneNumberString) {
    return phoneNumberString.indexOf(" - ") != -1;
  }

  protected static List<String> getWeekdayTimeStrings(List<WeekdayTime> weekdayTimes) {
    List<String> result = new ArrayList<String>();
    for (WeekdayTime weekdayTime : weekdayTimes) {
      result.add(weekdayTime.getDisplayValue());
    }
    return result;
  }

  protected static void mapPhoneNumberIfNotNull(final PhoneNumber phoneNumber, final String label, final TelephoneType type, final List<TelephoneNumber> phoneNumbers) {
    if (phoneNumber != null) {
      TelephoneNumber mappedPhoneNumber = mapPhoneNumber(phoneNumber, label, type);
      if (mappedPhoneNumber != null) {
        phoneNumbers.add(mappedPhoneNumber);
      }
    }
  }

  protected static void mapEAliasIfNotNull(String alias, String label, EAliasType type, List<EAlias> eAliases) {
    if (alias != null) {
      EAlias eAlias = OBJECT_FACTORY.createEAlias();
      eAlias.setAlias(alias);
      eAlias.setLabel(label);
      eAlias.setType(type);
      eAliases.add(eAlias);
    }
  }

  protected static void mapAddressIfNotNull(Address address, GeoCoordinates geoCoordinates, String label, AddressType type, List<se.vgregion.kivtools.svc.sitemap.Address> addresses) {
    if (address != null) {
      se.vgregion.kivtools.svc.sitemap.Address result = OBJECT_FACTORY.createAddress();

      result.setLabel(label);
      result.setType(type);

      if (StringUtil.isEmpty(address.getStreet())) {
        result.setConcatenated(true);
        result.setConcatenatedAddress(StringUtil.concatenate(address.getAdditionalInfo()));
      } else {
        result.setStreet(address.getStreet());
        result.setPostcode(address.getZipCode().toString());
        result.setCity(address.getCity());
      }

      result.setGeoCoordinates(geoCoordinates);

      addresses.add(result);
    }
  }

  protected static GeoCoordinates mapGeoCoordinates(Unit unit) {
    GeoCoordinates geoCoordinates = OBJECT_FACTORY.createAddressGeoCoordinates();
    geoCoordinates.setRt90Xpos(String.valueOf(unit.getRt90X()));
    geoCoordinates.setRt90Ypos(String.valueOf(unit.getRt90Y()));
    geoCoordinates.setWgs84Latitude(String.valueOf(unit.getWgs84LatRounded()));
    geoCoordinates.setWgs84Longitude(String.valueOf(unit.getWgs84LongRounded()));
    return geoCoordinates;
  }
}
