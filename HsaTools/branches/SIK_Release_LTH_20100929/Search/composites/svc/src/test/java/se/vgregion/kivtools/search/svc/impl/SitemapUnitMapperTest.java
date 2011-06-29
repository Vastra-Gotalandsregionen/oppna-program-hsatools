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

package se.vgregion.kivtools.search.svc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.svc.sitemap.Address.GeoCoordinates;
import se.vgregion.kivtools.svc.sitemap.AddressType;
import se.vgregion.kivtools.svc.sitemap.EAliasType;
import se.vgregion.kivtools.svc.sitemap.TelephoneType;

public class SitemapUnitMapperTest {
  private final UnitBuilder builder = new UnitBuilder();

  @Test
  public void mapperIsInstantiable() {
    assertNotNull(new SitemapUnitMapper());
  }

  @Test
  public void hsaIdentityIsMapped() {
    Unit unit = this.builder.hsaIdentity("abc-123").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("hsaIdentity", "abc-123", result.getHsaIdentity());
  }

  @Test
  public void nameIsMapped() {
    Unit unit = this.builder.name("Vårdcentralen").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("name", "Vårdcentralen", result.getName());
  }

  @Test
  public void telephoneTimesAreMapped() throws InvalidFormatException {
    Unit unit = this.builder.telephoneTime(new WeekdayTime("1-4#08:30#10:00")).telephoneTime(new WeekdayTime("5-5#08:00#11:00")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("telephone time", "Måndag-Torsdag 08:30-10:00, Fredag 08:00-11:00", result.getTelephoneTime());
  }

  @Test
  public void municipalityIsMapped() {
    Unit unit = this.builder.municipality("Varberg").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("municipality", "Varberg", result.getMunicipalityName());
  }

  @Test
  public void internalDescriptionIsMapped() {
    Unit unit = this.builder.internalDescription("internal 1").internalDescription("internal 2").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("internal description", "[internal 1, internal 2]", result.getInternalDescription().toString());
  }

  @Test
  public void externalDescriptionIsMapped() {
    Unit unit = this.builder.externalDescription("external 1").externalDescription("external 2").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("external description", "[external 1, external 2]", result.getExternalDescription().toString());
  }

  @Test
  public void temporaryInformationIsMapped() {
    Unit unit = this.builder.temporaryInformation("tillfälligt stängt").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("temporary information", "[tillfälligt stängt]", result.getTemporaryInformation().toString());
  }

  @Test
  public void referralInformationIsMapped() {
    Unit unit = this.builder.referralInformation("besökare hänvisas till dörren på baksidan").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("referral information", "[besökare hänvisas till dörren på baksidan]", result.getReferralInformation().toString());
  }

  @Test
  public void publicTelephoneNumberIsMapped() {
    Unit unit = this.builder.publicTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).publicTelephoneNumber(PhoneNumber.createPhoneNumber("0340-67890")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("public telephone number", 2, result.getTelephone().size());
    assertEquals("first label", "Telefon", result.getTelephone().get(0).getLabel());
    assertEquals("first type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("first area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("first telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
    assertEquals("second label", "Telefon", result.getTelephone().get(1).getLabel());
    assertEquals("second type", TelephoneType.FIXED, result.getTelephone().get(1).getType());
    assertEquals("second area code", "0340", result.getTelephone().get(1).getAreaCode());
    assertEquals("second telephone number", "678 90", result.getTelephone().get(1).getTelephoneNumber());
  }

  @Test
  public void unparseablePhoneNumberStoredWithoutAreaCode() {
    Unit unit = this.builder.publicTelephoneNumber(PhoneNumber.createPhoneNumber("abcdef")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("public telephone number", 1, result.getTelephone().size());
    assertNull("area code", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "abcdef", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void switchboardTelephoneNumberIsMapped() {
    Unit unit = this.builder.switchboardTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("switchboard telephone number", 1, result.getTelephone().size());
    assertEquals("label", "Växel", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void internalPagerNumberIsMapped() {
    Unit unit = this.builder.internalPagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("internal pager number", 1, result.getTelephone().size());
    assertEquals("label", "Personsökare", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void pagerNumberIsMapped() {
    Unit unit = this.builder.pagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("pager number", 1, result.getTelephone().size());
    assertEquals("label", "Minicall", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void textTelephoneNumberIsMapped() {
    Unit unit = this.builder.textTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("text telephone number", 1, result.getTelephone().size());
    assertEquals("label", "Texttelefon", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void mobilePhoneNumberIsMapped() {
    Unit unit = this.builder.mobilePhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("mobile phone number", 1, result.getTelephone().size());
    assertEquals("label", "Mobil", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.MOBILE, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void smsPhoneNumberIsMapped() {
    Unit unit = this.builder.smsPhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("sms phone number", 1, result.getTelephone().size());
    assertEquals("label", "SMS", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.MOBILE, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void faxNumberIsMapped() {
    Unit unit = this.builder.faxNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("fax number", 1, result.getTelephone().size());
    assertEquals("label", "Fax", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FAX, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void directPhoneNumberIsMapped() {
    Unit unit = this.builder.directPhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).directPhoneNumber(PhoneNumber.createPhoneNumber("0340-67890")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("direct phone number", 2, result.getTelephone().size());
    assertEquals("first label", "Direkttelefon", result.getTelephone().get(0).getLabel());
    assertEquals("first type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("first area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("first telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
    assertEquals("second label", "Direkttelefon", result.getTelephone().get(1).getLabel());
    assertEquals("second type", TelephoneType.FIXED, result.getTelephone().get(1).getType());
    assertEquals("second area code", "0340", result.getTelephone().get(1).getAreaCode());
    assertEquals("second telephone number", "678 90", result.getTelephone().get(1).getTelephoneNumber());
  }

  @Test
  public void labeledUriIsMapped() {
    Unit unit = this.builder.labeledUri("http://uri").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("ealias", 1, result.getEAlias().size());
    assertEquals("alias", "http://uri", result.getEAlias().get(0).getAlias());
    assertEquals("label", "Hemsida", result.getEAlias().get(0).getLabel());
    assertEquals("type", EAliasType.URL, result.getEAlias().get(0).getType());
  }

  @Test
  public void emailIsMapped() {
    Unit unit = this.builder.email("a@b.c").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("ealias", 1, result.getEAlias().size());
    assertEquals("alias", "a@b.c", result.getEAlias().get(0).getAlias());
    assertEquals("label", "E-post", result.getEAlias().get(0).getLabel());
    assertEquals("type", EAliasType.E_MAIL, result.getEAlias().get(0).getType());
  }

  @Test
  public void postalAddressIsMapped() {
    Unit unit = this.builder.postalAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Postadress", address.getLabel());
    assertEquals("type", AddressType.POST, address.getType());
    assertFalse("concatenated", address.isConcatenated());
    assertEquals("street", "Storgatan 1", address.getStreet());
    assertEquals("post code", "412 63", address.getPostcode());
    assertEquals("city", "Göteborg", address.getCity());
  }

  @Test
  public void addressIsConcatenatedIfNotARealAddress() {
    Unit unit = this.builder.postalAddress(this.createAddress("Storgatan 1", "412 63")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertTrue("concatenated", address.isConcatenated());
    assertEquals("concatenated address", "Storgatan 1, 412 63", address.getConcatenatedAddress());
  }

  @Test
  public void geoCoordinatesAreSetOnAddressIfAvailableOnUnit() {
    Unit unit = this.builder.rt90(12345, 67890).wgs84(12.345, 67.890).postalAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    GeoCoordinates geoCoordinates = result.getAddress().get(0).getGeoCoordinates();
    assertNotNull("geo coordinates", geoCoordinates);
    assertEquals("rt90x", "12345", geoCoordinates.getRt90Xpos());
    assertEquals("rt90y", "67890", geoCoordinates.getRt90Ypos());
    assertEquals("wgs84 latitude", "12.345", geoCoordinates.getWgs84Latitude());
    assertEquals("wgs84 longitude", "67.89", geoCoordinates.getWgs84Longitude());
  }

  @Test
  public void internalAddressIsMapped() {
    Unit unit = this.builder.internalAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Intern adress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void streetAddressIsMapped() {
    Unit unit = this.builder.streetAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Besöksadress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void deliveryAddressIsMapped() {
    Unit unit = this.builder.deliveryAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Leveransadress", address.getLabel());
    assertEquals("type", AddressType.DELIVERY, address.getType());
  }

  @Test
  public void invoiceAddressIsMapped() {
    Unit unit = this.builder.invoiceAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Fakturaadress", address.getLabel());
    assertEquals("type", AddressType.BILLING, address.getType());
  }

  @Test
  public void consigneeAddressIsMapped() {
    Unit unit = this.builder.consigneeAddress(this.createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Godsadress", address.getLabel());
    assertEquals("type", AddressType.GOODS, address.getType());
  }

  @Test
  public void visitingHoursAreMapped() throws Exception {
    Unit unit = this.builder.visitingHours(new WeekdayTime("1-4#08:30#10:00")).visitingHours(new WeekdayTime("5-5#08:00#11:00")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("visiting hours", "Måndag-Torsdag 08:30-10:00, Fredag 08:00-11:00", result.getVisitingHours());
  }

  @Test
  public void dropInHoursIsMapped() throws Exception {
    Unit unit = this.builder.dropInHours(new WeekdayTime("1-4#08:30#10:00")).dropInHours(new WeekdayTime("5-5#08:00#11:00")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("visiting hours", "Måndag-Torsdag 08:30-10:00, Fredag 08:00-11:00", result.getDropInHours());
  }

  @Test
  public void visitingRulesAreMapped() {
    Unit unit = this.builder.visitingRules("Ingen parfym tack").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("visiting rules", "Ingen parfym tack", result.getVisitingRules());
  }

  @Test
  public void mvkEnableIsTrueIfAtLeastOneMvkCaseTypeIsPresentOnUnit() {
    Unit unit = this.builder.mvkCaseType("casetype 1").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertTrue("mvk enable", result.isMvkEnable());
  }

  @Test
  public void mvkEnableIsFalseIfNoMvkCaseTypesArePresentOnUnit() {
    Unit unit = this.builder.build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertFalse("mvk enable", result.isMvkEnable());
  }

  @Test
  public void mvkServicesAreMapped() {
    Unit unit = this.builder.mvkCaseType("casetype 1").mvkCaseType("casetype 2").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("mvk services", "[casetype 1, casetype 2]", result.getMvkServices().toString());
  }

  @Test
  public void hsaAltTextIsMapped() {
    Unit unit = this.builder.hsaAltText("alternativ text").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("alt text", "alternativ text", result.getAltText());
  }

  @Test
  public void hsaVpwInformation1IsMapped() {
    Unit unit = this.builder.hsaVpwInformation1("mer om").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("about", "mer om", result.getAbout());
  }

  @Test
  public void hsaVpwInformation2IsMapped() {
    Unit unit = this.builder.hsaVpwInformation2("tillfällig info").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("temporary information size", 1, result.getTemporaryInformation().size());
    assertEquals("temporary information", "tillfällig info", result.getTemporaryInformation().get(0));
  }

  @Test
  public void localityIsMapped() {
    Unit unit = this.builder.locality("Halmstad").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("locality", "Halmstad", result.getLocality());
  }

  private Address createAddress(String... addressLines) {
    return AddressHelper.convertToAddress(Arrays.asList(addressLines));
  }

  private static class UnitBuilder {
    private String hsaIdentity;
    private String name;
    private final List<WeekdayTime> telephoneTime = new ArrayList<WeekdayTime>();
    private String municipality;
    private final List<String> internalDescription = new ArrayList<String>();
    private final List<String> externalDescription = new ArrayList<String>();
    private String temporaryInformation;
    private String referralInformation;
    private final List<PhoneNumber> publicTelephoneNumbers = new ArrayList<PhoneNumber>();
    private PhoneNumber switchboardTelephoneNumber;
    private PhoneNumber internalPagerNumber;
    private PhoneNumber pagerNumber;
    private PhoneNumber textTelephoneNumber;
    private PhoneNumber mobilePhoneNumber;
    private PhoneNumber smsPhoneNumber;
    private PhoneNumber faxNumber;
    private final List<PhoneNumber> directPhoneNumbers = new ArrayList<PhoneNumber>();
    private String labeledUri;
    private String email;
    private Address postalAddress;
    private int rt90x;
    private int rt90y;
    private double latitude;
    private double longitude;
    private Address internalAddress;
    private Address streetAddress;
    private Address deliveryAddress;
    private Address invoiceAddress;
    private Address consigneeAddress;
    private final List<WeekdayTime> visitingHours = new ArrayList<WeekdayTime>();
    private final List<WeekdayTime> dropInHours = new ArrayList<WeekdayTime>();
    private final List<String> mvkCaseTypes = new ArrayList<String>();
    private String visitingRules;
    private String hsaAltText;
    private String hsaVpwInformation1;
    private String hsaVpwInformation2;
    private String locality;

    public Unit build() {
      Unit unit = new Unit();

      unit.setHsaIdentity(this.hsaIdentity);
      unit.setName(this.name);
      unit.setHsaMunicipalityName(this.municipality);
      unit.addHsaTelephoneTimes(this.telephoneTime);
      unit.addHsaDropInHours(this.dropInHours);
      unit.addHsaSurgeryHours(this.visitingHours);
      unit.setHsaVisitingRules(this.visitingRules);
      unit.addInternalDescription(this.internalDescription);
      unit.addDescription(this.externalDescription);
      unit.setVgrTempInfoBody(this.temporaryInformation);
      unit.setVgrRefInfo(this.referralInformation);
      for (PhoneNumber telephoneNumber : this.publicTelephoneNumbers) {
        unit.addHsaPublicTelephoneNumber(telephoneNumber);
      }
      unit.setHsaSedfSwitchboardTelephoneNo(this.switchboardTelephoneNumber);
      unit.setHsaInternalPagerNumber(this.internalPagerNumber);
      unit.setPagerTelephoneNumber(this.pagerNumber);
      unit.setHsaTextPhoneNumber(this.textTelephoneNumber);
      unit.setMobileTelephoneNumber(this.mobilePhoneNumber);
      unit.setHsaSmsTelephoneNumber(this.smsPhoneNumber);
      unit.setFacsimileTelephoneNumber(this.faxNumber);
      unit.addHsaTelephoneNumber(this.directPhoneNumbers);
      if (this.labeledUri != null) {
        unit.setLabeledURI(this.labeledUri);
      }
      unit.setMail(this.email);
      unit.setHsaPostalAddress(this.postalAddress);
      unit.setHsaInternalAddress(this.internalAddress);
      unit.setHsaStreetAddress(this.streetAddress);
      unit.setHsaSedfDeliveryAddress(this.deliveryAddress);
      unit.setHsaSedfInvoiceAddress(this.invoiceAddress);
      unit.setHsaConsigneeAddress(this.consigneeAddress);
      unit.setHsaAltText(this.hsaAltText);
      unit.setHsaVpwInformation1(this.hsaVpwInformation1);
      unit.setHsaVpwInformation2(this.hsaVpwInformation2);
      unit.setLocality(this.locality);

      unit.setRt90X(this.rt90x);
      unit.setRt90Y(this.rt90y);
      unit.setWgs84Lat(this.latitude);
      unit.setWgs84Long(this.longitude);

      for (String mvkCaseType : this.mvkCaseTypes) {
        unit.addMvkCaseType(mvkCaseType);
      }

      return unit;
    }

    public UnitBuilder locality(String locality) {
      this.locality = locality;
      return this;
    }

    public UnitBuilder hsaVpwInformation2(String hsaVpwInformation2) {
      this.hsaVpwInformation2 = hsaVpwInformation2;
      return this;
    }

    public UnitBuilder hsaVpwInformation1(String hsaVpwInformation1) {
      this.hsaVpwInformation1 = hsaVpwInformation1;
      return this;
    }

    public UnitBuilder hsaAltText(String hsaAltText) {
      this.hsaAltText = hsaAltText;
      return this;
    }

    public UnitBuilder visitingRules(String visitingRules) {
      this.visitingRules = visitingRules;
      return this;
    }

    public UnitBuilder mvkCaseType(String mvkCaseType) {
      this.mvkCaseTypes.add(mvkCaseType);
      return this;
    }

    public UnitBuilder consigneeAddress(Address consigneeAddress) {
      this.consigneeAddress = consigneeAddress;
      return this;
    }

    public UnitBuilder invoiceAddress(Address invoiceAddress) {
      this.invoiceAddress = invoiceAddress;
      return this;
    }

    public UnitBuilder deliveryAddress(Address deliveryAddress) {
      this.deliveryAddress = deliveryAddress;
      return this;
    }

    public UnitBuilder streetAddress(Address streetAddress) {
      this.streetAddress = streetAddress;
      return this;
    }

    public UnitBuilder internalAddress(Address internalAddress) {
      this.internalAddress = internalAddress;
      return this;
    }

    public UnitBuilder wgs84(double latitude, double longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
      return this;
    }

    public UnitBuilder rt90(int rt90x, int rt90y) {
      this.rt90x = rt90x;
      this.rt90y = rt90y;
      return this;
    }

    public UnitBuilder postalAddress(Address postalAddress) {
      this.postalAddress = postalAddress;
      return this;
    }

    public UnitBuilder email(String email) {
      this.email = email;
      return this;
    }

    public UnitBuilder labeledUri(String labeledUri) {
      this.labeledUri = labeledUri;
      return this;
    }

    public UnitBuilder switchboardTelephoneNumber(PhoneNumber phoneNumber) {
      this.switchboardTelephoneNumber = phoneNumber;
      return this;
    }

    public UnitBuilder internalPagerNumber(PhoneNumber phoneNumber) {
      this.internalPagerNumber = phoneNumber;
      return this;
    }

    public UnitBuilder pagerNumber(PhoneNumber phoneNumber) {
      this.pagerNumber = phoneNumber;
      return this;
    }

    public UnitBuilder textTelephoneNumber(PhoneNumber phoneNumber) {
      this.textTelephoneNumber = phoneNumber;
      return this;
    }

    public UnitBuilder mobilePhoneNumber(PhoneNumber phoneNumber) {
      this.mobilePhoneNumber = phoneNumber;
      return this;
    }

    public UnitBuilder smsPhoneNumber(PhoneNumber phoneNumber) {
      this.smsPhoneNumber = phoneNumber;
      return this;
    }

    public UnitBuilder faxNumber(PhoneNumber phoneNumber) {
      this.faxNumber = phoneNumber;
      return this;
    }

    public UnitBuilder directPhoneNumber(PhoneNumber phoneNumber) {
      this.directPhoneNumbers.add(phoneNumber);
      return this;
    }

    public UnitBuilder publicTelephoneNumber(PhoneNumber phoneNumber) {
      this.publicTelephoneNumbers.add(phoneNumber);
      return this;
    }

    public UnitBuilder referralInformation(String referralInformation) {
      this.referralInformation = referralInformation;
      return this;
    }

    public UnitBuilder internalDescription(String description) {
      this.internalDescription.add(description);
      return this;
    }

    public UnitBuilder externalDescription(String description) {
      this.externalDescription.add(description);
      return this;
    }

    public UnitBuilder temporaryInformation(String temporaryInformation) {
      this.temporaryInformation = temporaryInformation;
      return this;
    }

    public UnitBuilder municipality(String municipality) {
      this.municipality = municipality;
      return this;
    }

    public UnitBuilder telephoneTime(WeekdayTime telephoneTime) {
      this.telephoneTime.add(telephoneTime);
      return this;
    }

    public UnitBuilder visitingHours(WeekdayTime telephoneTime) {
      this.visitingHours.add(telephoneTime);
      return this;
    }

    public UnitBuilder dropInHours(WeekdayTime telephoneTime) {
      this.dropInHours.add(telephoneTime);
      return this;
    }

    public UnitBuilder name(String name) {
      this.name = name;
      return this;
    }

    public UnitBuilder hsaIdentity(String hsaIdentity) {
      this.hsaIdentity = hsaIdentity;
      return this;
    }
  }
}
