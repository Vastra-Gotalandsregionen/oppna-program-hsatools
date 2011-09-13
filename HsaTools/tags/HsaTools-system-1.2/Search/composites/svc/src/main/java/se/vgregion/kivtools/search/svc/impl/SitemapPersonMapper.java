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

import java.util.List;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.svc.sitemap.AddressType;
import se.vgregion.kivtools.svc.sitemap.EAliasType;
import se.vgregion.kivtools.svc.sitemap.TelephoneType;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Maps information from a domain person instance to a sitemap person instance.
 */
public class SitemapPersonMapper extends AbstractSitemapMapper {
  /**
   * Maps information from a domain person instance to a sitemap person instance.
   * 
   * @param person The domain person instance to map information from.
   * @param unitCache The unit cache to find employing units for the person.
   * @return a populated sitemap person instance.
   */
  public static se.vgregion.kivtools.svc.sitemap.Person map(Person person, UnitCache unitCache) {
    se.vgregion.kivtools.svc.sitemap.Person result = new se.vgregion.kivtools.svc.sitemap.Person();

    result.setHsaIdentity(person.getHsaIdentity());
    result.setGivenName(person.getGivenName());
    result.setNickName(person.getHsaNickName());
    result.setMiddleName(person.getHsaMiddleName());
    result.setSurname(person.getSn());
    result.setUserId(person.getVgrId());
    result.setTitle(person.getHsaTitle());
    result.setAltText(person.getHsaAltText());
    result.setTitleName(person.getPaTitleName());

    mapEAliasIfNotNull(person.getMail(), "E-post", EAliasType.E_MAIL, result.getEAlias());

    if (person.getHsaSpecialityName() != null) {
      result.getSpeciality().addAll(person.getHsaSpecialityName());
    }
    mapEmployments(person.getEmployments(), result.getEmployment(), unitCache);

    return result;
  }

  private static void mapEmployments(List<Employment> employments, List<se.vgregion.kivtools.svc.sitemap.Employment> result, UnitCache unitCache) {
    if (employments != null) {
      for (Employment employment : employments) {
        result.add(mapEmployment(employment, unitCache));
      }
    }
  }

  private static se.vgregion.kivtools.svc.sitemap.Employment mapEmployment(Employment employment, UnitCache unitCache) {
    se.vgregion.kivtools.svc.sitemap.Employment result = new se.vgregion.kivtools.svc.sitemap.Employment();

    result.setTitle(employment.getTitle());
    if (employment.getVgrStrukturPerson() != null) {
      Unit unit = unitCache.getUnitByDnString(employment.getVgrStrukturPerson().toString());
      if (unit != null) {
        result.setUnitIdentity(unit.getHsaIdentity());
      }
    }
    result.setDescription(StringUtil.concatenate(employment.getDescription()));
    result.setLocality(employment.getLocality());

    result.getTelephone().addAll(mapPhoneNumbers(employment.getHsaTelephoneNumbers(), "Direkttelefon", TelephoneType.FIXED));
    mapPhoneNumberIfNotNull(employment.getHsaPublicTelephoneNumber(), "Telefon", TelephoneType.FIXED, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getHsaSedfSwitchboardTelephoneNo(), "Växel", TelephoneType.FIXED, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getHsaInternalPagerNumber(), "Personsökare", TelephoneType.OTHER, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getPagerTelephoneNumber(), "Minicall", TelephoneType.OTHER, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getHsaTextPhoneNumber(), "Texttelefon", TelephoneType.FIXED, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getMobileTelephoneNumber(), "Mobil", TelephoneType.MOBILE, result.getTelephone());
    mapPhoneNumberIfNotNull(employment.getFacsimileTelephoneNumber(), "Fax", TelephoneType.FAX, result.getTelephone());
    result.setTelephoneTime(StringUtil.concatenate(getWeekdayTimeStrings(employment.getHsaTelephoneTime())));

    mapAddressIfNotNull(employment.getHsaPostalAddress(), null, "Postadress", AddressType.POST, result.getAddress());
    mapAddressIfNotNull(employment.getHsaInternalAddress(), null, "Intern adress", AddressType.VISIT, result.getAddress());
    mapAddressIfNotNull(employment.getHsaStreetAddress(), null, "Besöksadress", AddressType.VISIT, result.getAddress());
    mapAddressIfNotNull(employment.getHsaSedfDeliveryAddress(), null, "Leveransadress", AddressType.DELIVERY, result.getAddress());
    mapAddressIfNotNull(employment.getHsaSedfInvoiceAddress(), null, "Fakturaadress", AddressType.BILLING, result.getAddress());
    mapAddressIfNotNull(employment.getHsaConsigneeAddress(), null, "Godsadress", AddressType.GOODS, result.getAddress());

    return result;
  }
}
