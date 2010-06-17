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

import static org.junit.Assert.*;

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
    Unit unit = builder.hsaIdentity("abc-123").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("hsaIdentity", "abc-123", result.getHsaIdentity());
  }

  @Test
  public void nameIsMapped() {
    Unit unit = builder.name("Vårdcentralen").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("name", "Vårdcentralen", result.getName());
  }

  @Test
  public void telephoneTimesAreMapped() throws InvalidFormatException {
    Unit unit = builder.telephoneTime(new WeekdayTime("1-4#08:30#10:00")).telephoneTime(new WeekdayTime("5-5#08:00#11:00")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("telephone time", "Måndag-Torsdag 08:30-10:00, Fredag 08:00-11:00", result.getTelephoneTime());
  }

  @Test
  public void municipalityIsMapped() {
    Unit unit = builder.municipality("Varberg").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("municipality", "Varberg", result.getMunicipalityName());
  }

  @Test
  public void internalDescriptionIsMapped() {
    Unit unit = builder.internalDescription("internal 1").internalDescription("internal 2").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("internal description", "[internal 1, internal 2]", result.getInternalDescription().toString());
  }

  @Test
  public void externalDescriptionIsMapped() {
    Unit unit = builder.externalDescription("external 1").externalDescription("external 2").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("external description", "[external 1, external 2]", result.getExternalDescription().toString());
  }

  @Test
  public void temporaryInformationIsMapped() {
    Unit unit = builder.temporaryInformation("tillfälligt stängt").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("temporary information", "[tillfälligt stängt]", result.getTemporaryInformation().toString());
  }

  @Test
  public void referralInformationIsMapped() {
    Unit unit = builder.referralInformation("besökare hänvisas till dörren på baksidan").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("referral information", "[besökare hänvisas till dörren på baksidan]", result.getReferralInformation().toString());
  }

  @Test
  public void publicTelephoneNumberIsMapped() {
    Unit unit = builder.publicTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).publicTelephoneNumber(PhoneNumber.createPhoneNumber("0340-67890")).build();
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
    Unit unit = builder.publicTelephoneNumber(PhoneNumber.createPhoneNumber("abcdef")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("public telephone number", 1, result.getTelephone().size());
    assertNull("area code", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "abcdef", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void switchboardTelephoneNumberIsMapped() {
    Unit unit = builder.switchboardTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("switchboard telephone number", 1, result.getTelephone().size());
    assertEquals("label", "Växel", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void internalPagerNumberIsMapped() {
    Unit unit = builder.internalPagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("internal pager number", 1, result.getTelephone().size());
    assertEquals("label", "Personsökare", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void pagerNumberIsMapped() {
    Unit unit = builder.pagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("pager number", 1, result.getTelephone().size());
    assertEquals("label", "Minicall", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void textTelephoneNumberIsMapped() {
    Unit unit = builder.textTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("text telephone number", 1, result.getTelephone().size());
    assertEquals("label", "Texttelefon", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void mobilePhoneNumberIsMapped() {
    Unit unit = builder.mobilePhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("mobile phone number", 1, result.getTelephone().size());
    assertEquals("label", "Mobil", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.MOBILE, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void smsPhoneNumberIsMapped() {
    Unit unit = builder.smsPhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("sms phone number", 1, result.getTelephone().size());
    assertEquals("label", "SMS", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.MOBILE, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void faxNumberIsMapped() {
    Unit unit = builder.faxNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("fax number", 1, result.getTelephone().size());
    assertEquals("label", "Fax", result.getTelephone().get(0).getLabel());
    assertEquals("type", TelephoneType.FAX, result.getTelephone().get(0).getType());
    assertEquals("area code", "0300", result.getTelephone().get(0).getAreaCode());
    assertEquals("telephone number", "123 45", result.getTelephone().get(0).getTelephoneNumber());
  }

  @Test
  public void directPhoneNumberIsMapped() {
    Unit unit = builder.directPhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).directPhoneNumber(PhoneNumber.createPhoneNumber("0340-67890")).build();
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
    Unit unit = builder.labeledUri("http://uri").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("ealias", 1, result.getEAlias().size());
    assertEquals("alias", "http://uri", result.getEAlias().get(0).getAlias());
    assertEquals("label", "Hemsida", result.getEAlias().get(0).getLabel());
    assertEquals("type", EAliasType.URL, result.getEAlias().get(0).getType());
  }

  @Test
  public void emailIsMapped() {
    Unit unit = builder.email("a@b.c").build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("ealias", 1, result.getEAlias().size());
    assertEquals("alias", "a@b.c", result.getEAlias().get(0).getAlias());
    assertEquals("label", "E-post", result.getEAlias().get(0).getLabel());
    assertEquals("type", EAliasType.E_MAIL, result.getEAlias().get(0).getType());
  }

  @Test
  public void postalAddressIsMapped() {
    Unit unit = builder.postalAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
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
    Unit unit = builder.postalAddress(createAddress("Storgatan 1", "412 63")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertTrue("concatenated", address.isConcatenated());
    assertEquals("concatenated address", "Storgatan 1, 412 63", address.getConcatenatedAddress());
  }

  @Test
  public void geoCoordinatesAreSetOnAddressIfAvailableOnUnit() {
    Unit unit = builder.rt90(12345, 67890).wgs84(12.345, 67.890).postalAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
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
    Unit unit = builder.internalAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Intern adress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void streetAddressIsMapped() {
    Unit unit = builder.streetAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Besöksadress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void deliveryAddressIsMapped() {
    Unit unit = builder.deliveryAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Leveransadress", address.getLabel());
    assertEquals("type", AddressType.DELIVERY, address.getType());
  }

  @Test
  public void invoiceAddressIsMapped() {
    Unit unit = builder.invoiceAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Fakturaadress", address.getLabel());
    assertEquals("type", AddressType.BILLING, address.getType());
  }

  @Test
  public void consigneeAddressIsMapped() {
    Unit unit = builder.consigneeAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    se.vgregion.kivtools.svc.sitemap.Unit result = SitemapUnitMapper.map(unit);
    assertEquals("address", 1, result.getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getAddress().get(0);
    assertEquals("label", "Godsadress", address.getLabel());
    assertEquals("type", AddressType.GOODS, address.getType());
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
    private List<PhoneNumber> publicTelephoneNumbers = new ArrayList<PhoneNumber>();
    private PhoneNumber switchboardTelephoneNumber;
    private PhoneNumber internalPagerNumber;
    private PhoneNumber pagerNumber;
    private PhoneNumber textTelephoneNumber;
    private PhoneNumber mobilePhoneNumber;
    private PhoneNumber smsPhoneNumber;
    private PhoneNumber faxNumber;
    private List<PhoneNumber> directPhoneNumbers = new ArrayList<PhoneNumber>();
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

    public Unit build() {
      Unit unit = new Unit();

      unit.setHsaIdentity(hsaIdentity);
      unit.setName(name);
      unit.setHsaMunicipalityName(municipality);
      unit.addHsaTelephoneTimes(telephoneTime);
      unit.addInternalDescription(internalDescription);
      unit.addDescription(externalDescription);
      unit.setVgrTempInfoBody(temporaryInformation);
      unit.setVgrRefInfo(referralInformation);
      for (PhoneNumber telephoneNumber : this.publicTelephoneNumbers) {
        unit.addHsaPublicTelephoneNumber(telephoneNumber);
      }
      unit.setHsaSedfSwitchboardTelephoneNo(switchboardTelephoneNumber);
      unit.setHsaInternalPagerNumber(internalPagerNumber);
      unit.setPagerTelephoneNumber(pagerNumber);
      unit.setHsaTextPhoneNumber(textTelephoneNumber);
      unit.setMobileTelephoneNumber(mobilePhoneNumber);
      unit.setHsaSmsTelephoneNumber(smsPhoneNumber);
      unit.setFacsimileTelephoneNumber(faxNumber);
      unit.addHsaTelephoneNumber(directPhoneNumbers);
      if (labeledUri != null) {
        unit.setLabeledURI(labeledUri);
      }
      unit.setMail(email);
      unit.setHsaPostalAddress(postalAddress);
      unit.setHsaInternalAddress(internalAddress);
      unit.setHsaStreetAddress(streetAddress);
      unit.setHsaSedfDeliveryAddress(deliveryAddress);
      unit.setHsaSedfInvoiceAddress(invoiceAddress);
      unit.setHsaConsigneeAddress(consigneeAddress);

      unit.setRt90X(rt90x);
      unit.setRt90Y(rt90y);
      unit.setWgs84Lat(latitude);
      unit.setWgs84Long(longitude);

      return unit;
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
