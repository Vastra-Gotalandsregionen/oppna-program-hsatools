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

import org.apache.commons.lang.StringUtils;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.svc.sitemap.Address.GeoCoordinates;
import se.vgregion.kivtools.svc.sitemap.AddressType;
import se.vgregion.kivtools.svc.sitemap.EAliasType;
import se.vgregion.kivtools.svc.sitemap.ObjectFactory;
import se.vgregion.kivtools.svc.sitemap.TelephoneType;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Maps information from a domain unit instance to a sitemap unit instance.
 */
public class SitemapUnitMapper extends AbstractSitemapMapper {
  private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

  /**
   * Maps information from a domain unit instance to a sitemap unit instance.
   * 
   * @param unit The domain unit instance to map information from.
   * @return a populated sitemap unit instance.
   */
  public static se.vgregion.kivtools.svc.sitemap.Unit map(Unit unit) {
    se.vgregion.kivtools.svc.sitemap.Unit result = OBJECT_FACTORY.createUnit();

    result.setHsaIdentity(unit.getHsaIdentity());
    result.setName(unit.getName());
    result.setMunicipalityCode(unit.getHsaMunicipalityCode());
    result.setMunicipalityName(unit.getHsaMunicipalityName());
    result.setTelephoneTime(StringUtil.concatenate(getWeekdayTimeStrings(unit.getHsaTelephoneTime())));
    result.setVisitingHours(StringUtil.concatenate(getWeekdayTimeStrings(unit.getHsaSurgeryHours())));
    result.setDropInHours(StringUtil.concatenate(getWeekdayTimeStrings(unit.getHsaDropInHours())));
    result.getInternalDescription().addAll(unit.getInternalDescription());
    result.getExternalDescription().addAll(unit.getDescription());
    if (StringUtils.isNotBlank(unit.getVgrTempInfoBody())) {
      result.getTemporaryInformation().add(unit.getVgrTempInfoBody());
    }
    result.getReferralInformation().add(unit.getVgrRefInfo());
    result.setVisitingRules(unit.getHsaVisitingRules());
    result.setAltText(unit.getHsaAltText());
    result.setAbout(unit.getHsaVpwInformation1());
    if (StringUtils.isNotBlank(unit.getHsaVpwInformation2())) {
      result.getTemporaryInformation().add(unit.getHsaVpwInformation2());
    }
    result.setLocality(unit.getLocality());

    result.getTelephone().addAll(mapPhoneNumbers(unit.getHsaPublicTelephoneNumber(), "Telefon", TelephoneType.FIXED));
    result.getTelephone().addAll(mapPhoneNumbers(unit.getHsaTelephoneNumber(), "Direkttelefon", TelephoneType.FIXED));
    mapPhoneNumberIfNotNull(unit.getHsaSedfSwitchboardTelephoneNo(), "Växel", TelephoneType.FIXED, result.getTelephone());
    mapPhoneNumberIfNotNull(unit.getHsaInternalPagerNumber(), "Personsökare", TelephoneType.OTHER, result.getTelephone());
    mapPhoneNumberIfNotNull(unit.getPagerTelephoneNumber(), "Minicall", TelephoneType.OTHER, result.getTelephone());
    mapPhoneNumberIfNotNull(unit.getHsaTextPhoneNumber(), "Texttelefon", TelephoneType.FIXED, result.getTelephone());
    mapPhoneNumberIfNotNull(unit.getMobileTelephoneNumber(), "Mobil", TelephoneType.MOBILE, result.getTelephone());
    // mapPhoneNumberIfNotNull(unit.getHsaSmsTelephoneNumber(), "SMS", TelephoneType.MOBILE, result.getTelephone());
    mapPhoneNumberIfNotNull(unit.getFacsimileTelephoneNumber(), "Fax", TelephoneType.FAX, result.getTelephone());

    mapEAliasIfNotNull(unit.getLabeledURI(), "Hemsida", EAliasType.URL, result.getEAlias());
    mapEAliasIfNotNull(unit.getMail(), "E-post", EAliasType.E_MAIL, result.getEAlias());

    GeoCoordinates geoCoordinates = mapGeoCoordinates(unit);

    mapAddressIfNotNull(unit.getHsaPostalAddress(), geoCoordinates, "Postadress", AddressType.POST, result.getAddress());
    mapAddressIfNotNull(unit.getHsaInternalAddress(), geoCoordinates, "Intern adress", AddressType.VISIT, result.getAddress());
    mapAddressIfNotNull(unit.getHsaStreetAddress(), geoCoordinates, "Besöksadress", AddressType.VISIT, result.getAddress());
    mapAddressIfNotNull(unit.getHsaSedfDeliveryAddress(), geoCoordinates, "Leveransadress", AddressType.DELIVERY, result.getAddress());
    mapAddressIfNotNull(unit.getHsaSedfInvoiceAddress(), geoCoordinates, "Fakturaadress", AddressType.BILLING, result.getAddress());
    mapAddressIfNotNull(unit.getHsaConsigneeAddress(), geoCoordinates, "Godsadress", AddressType.GOODS, result.getAddress());

    result.setMvkEnable(unit.getMvkCaseTypes().size() > 0);
    result.getMvkServices().addAll(unit.getMvkCaseTypes());

    result.setVardvalParticipant(unit.isVgrVardVal());
    if (unit.getBusinessClassificationCode() != null) {
      result.getBusinessClassificationCode().addAll(unit.getBusinessClassificationCode());
    }

    return result;
  }
}
