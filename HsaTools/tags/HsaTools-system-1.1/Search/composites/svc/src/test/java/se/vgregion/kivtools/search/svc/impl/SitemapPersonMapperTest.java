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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.svc.sitemap.AddressType;
import se.vgregion.kivtools.svc.sitemap.EAliasType;
import se.vgregion.kivtools.svc.sitemap.TelephoneNumber;
import se.vgregion.kivtools.svc.sitemap.TelephoneType;

public class SitemapPersonMapperTest {
  private final PersonBuilder personBuilder = new PersonBuilder();
  private final EmploymentBuilder employmentBuilder = new EmploymentBuilder();
  private final UnitCache unitCache = new UnitCache();

  @Before
  public void setUp() {
    Unit unit = new Unit();
    unit.setHsaIdentity("abc-123");
    unit.setDn(DN.createDNFromString("ou=Tandreglering Halmstad,ou=lthalland.se,o=lth"));
    unitCache.add(unit);
  }

  @Test
  public void mapperIsInstantiable() {
    assertNotNull(new SitemapPersonMapper());
  }

  @Test
  public void hsaIdentityIsMapped() {
    Person person = personBuilder.hsaIdentity("abc-123").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("hsaIdentity", "abc-123", result.getHsaIdentity());
  }

  @Test
  public void givenNameIsMapped() {
    Person person = personBuilder.givenName("Karl").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("given name", "Karl", result.getGivenName());
  }

  @Test
  public void nickNameIsMapped() {
    Person person = personBuilder.nickName("Kalle").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("nick name", "Kalle", result.getNickName());
  }

  @Test
  public void middleNameIsMapped() {
    Person person = personBuilder.middleName("Johansson").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("middle name", "Johansson", result.getMiddleName());
  }

  @Test
  public void surnameIsMapped() {
    Person person = personBuilder.surname("Silfverstråle").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("surname", "Silfverstråle", result.getSurname());
  }

  @Test
  public void userIdIsMapped() {
    Person person = personBuilder.userId("abc123").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("user id", "abc123", result.getUserId());
  }

  @Test
  public void titleIsMapped() {
    Person person = personBuilder.title("Läkare").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("title", "Läkare", result.getTitle());
  }

  @Test
  public void specialityIsMapped() {
    Person person = personBuilder.speciality("Fotsvamp").speciality("Visdomständer").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("speciality", 2, result.getSpeciality().size());
  }

  @Test
  public void employmentsAreMapped() {
    Employment employment = employmentBuilder.build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("employments", 1, result.getEmployment().size());
  }

  @Test
  public void employmentTitleIsMapped() {
    Employment employment = employmentBuilder.title("Systemarkitekt").build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("employment title", "Systemarkitekt", result.getEmployment().get(0).getTitle());
  }

  @Test
  public void employmentUnitIdentityIsMapped() {
    Employment employment = employmentBuilder.unitDn("ou=Tandreglering Halmstad,ou=lthalland.se,o=lth").build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("employment unit identity", "abc-123", result.getEmployment().get(0).getUnitIdentity());
  }

  @Test
  public void employmentDescriptionIsMapped() {
    Employment employment = employmentBuilder.description("Övergripande ansvar för systemdesign m.m.").description("Kodslav").build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("employment title", "Övergripande ansvar för systemdesign m.m., Kodslav", result.getEmployment().get(0).getDescription());
  }

  @Test
  public void publicTelephoneNumberIsMapped() {
    Employment employment = employmentBuilder.publicTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("public telephone number", 1, telephone.size());
    assertEquals("first label", "Telefon", telephone.get(0).getLabel());
    assertEquals("first type", TelephoneType.FIXED, telephone.get(0).getType());
    assertEquals("first area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("first telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void directPhoneNumberIsMapped() {
    Employment employment = employmentBuilder.telephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).telephoneNumber(PhoneNumber.createPhoneNumber("0340-67890")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("direct phone number", 2, telephone.size());
    assertEquals("first label", "Direkttelefon", telephone.get(0).getLabel());
    assertEquals("first type", TelephoneType.FIXED, telephone.get(0).getType());
    assertEquals("first area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("first telephone number", "123 45", telephone.get(0).getTelephoneNumber());
    assertEquals("second label", "Direkttelefon", telephone.get(1).getLabel());
    assertEquals("second type", TelephoneType.FIXED, telephone.get(1).getType());
    assertEquals("second area code", "0340", telephone.get(1).getAreaCode());
    assertEquals("second telephone number", "678 90", telephone.get(1).getTelephoneNumber());
  }

  @Test
  public void switchboardTelephoneNumberIsMapped() {
    Employment employment = employmentBuilder.switchboardTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("switchboard telephone number", 1, telephone.size());
    assertEquals("label", "Växel", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void internalPagerNumberIsMapped() {
    Employment employment = employmentBuilder.internalPagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("internal pager number", 1, telephone.size());
    assertEquals("label", "Personsökare", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void pagerNumberIsMapped() {
    Employment employment = employmentBuilder.pagerNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("pager number", 1, telephone.size());
    assertEquals("label", "Minicall", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.OTHER, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void textTelephoneNumberIsMapped() {
    Employment employment = employmentBuilder.textTelephoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("text telephone number", 1, telephone.size());
    assertEquals("label", "Texttelefon", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.FIXED, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void mobilePhoneNumberIsMapped() {
    Employment employment = employmentBuilder.mobilePhoneNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("mobile phone number", 1, telephone.size());
    assertEquals("label", "Mobil", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.MOBILE, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void faxNumberIsMapped() {
    Employment employment = employmentBuilder.faxNumber(PhoneNumber.createPhoneNumber("0300-12345")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    List<TelephoneNumber> telephone = result.getEmployment().get(0).getTelephone();
    assertEquals("fax number", 1, telephone.size());
    assertEquals("label", "Fax", telephone.get(0).getLabel());
    assertEquals("type", TelephoneType.FAX, telephone.get(0).getType());
    assertEquals("area code", "0300", telephone.get(0).getAreaCode());
    assertEquals("telephone number", "123 45", telephone.get(0).getTelephoneNumber());
  }

  @Test
  public void telephoneTimesAreMapped() throws InvalidFormatException {
    Employment employment = employmentBuilder.telephoneTime(new WeekdayTime("1-4#08:30#10:00")).telephoneTime(new WeekdayTime("5-5#08:00#11:00")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("telephone time", "Måndag-Torsdag 08:30-10:00, Fredag 08:00-11:00", result.getEmployment().get(0).getTelephoneTime());
  }

  @Test
  public void postalAddressIsMapped() {
    Employment employment = employmentBuilder.postalAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Postadress", address.getLabel());
    assertEquals("type", AddressType.POST, address.getType());
    assertFalse("concatenated", address.isConcatenated());
    assertEquals("street", "Storgatan 1", address.getStreet());
    assertEquals("post code", "412 63", address.getPostcode());
    assertEquals("city", "Göteborg", address.getCity());
  }

  @Test
  public void internalAddressIsMapped() {
    Employment employment = employmentBuilder.internalAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Intern adress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void streetAddressIsMapped() {
    Employment employment = employmentBuilder.streetAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Besöksadress", address.getLabel());
    assertEquals("type", AddressType.VISIT, address.getType());
  }

  @Test
  public void deliveryAddressIsMapped() {
    Employment employment = employmentBuilder.deliveryAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Leveransadress", address.getLabel());
    assertEquals("type", AddressType.DELIVERY, address.getType());
  }

  @Test
  public void invoiceAddressIsMapped() {
    Employment employment = employmentBuilder.invoiceAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Fakturaadress", address.getLabel());
    assertEquals("type", AddressType.BILLING, address.getType());
  }

  @Test
  public void consigneeAddressIsMapped() {
    Employment employment = employmentBuilder.consigneeAddress(createAddress("Storgatan 1", "412 63", "Göteborg")).build();
    Person person = personBuilder.employment(employment).build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("address", 1, result.getEmployment().get(0).getAddress().size());
    se.vgregion.kivtools.svc.sitemap.Address address = result.getEmployment().get(0).getAddress().get(0);
    assertEquals("label", "Godsadress", address.getLabel());
    assertEquals("type", AddressType.GOODS, address.getType());
  }

  @Test
  public void emailIsMapped() {
    Person person = personBuilder.email("kalle.kula@lthalland.se").build();
    se.vgregion.kivtools.svc.sitemap.Person result = SitemapPersonMapper.map(person, unitCache);
    assertEquals("ealias", 1, result.getEAlias().size());
    assertEquals("alias", "kalle.kula@lthalland.se", result.getEAlias().get(0).getAlias());
    assertEquals("label", "E-post", result.getEAlias().get(0).getLabel());
    assertEquals("type", EAliasType.E_MAIL, result.getEAlias().get(0).getType());
  }

  private Address createAddress(String... addressLines) {
    return AddressHelper.convertToAddress(Arrays.asList(addressLines));
  }

  private static class EmploymentBuilder {
    private String title;
    private String unitDn;
    private List<String> description;
    private PhoneNumber publicTelephoneNumber;
    private List<PhoneNumber> telephoneNumber;
    private PhoneNumber switchboardTelephoneNumber;
    private PhoneNumber internalPagerNumber;
    private PhoneNumber pagerNumber;
    private PhoneNumber textTelephoneNumber;
    private PhoneNumber mobilePhoneNumber;
    private PhoneNumber faxNumber;
    private List<WeekdayTime> telephoneTime = new ArrayList<WeekdayTime>();
    private Address consigneeAddress;
    private Address invoiceAddress;
    private Address deliveryAddress;
    private Address streetAddress;
    private Address internalAddress;
    private Address postalAddress;

    public Employment build() {
      Employment employment = new Employment();

      employment.setTitle(title);
      if (unitDn != null) {
        employment.setVgrStrukturPerson(DN.createDNFromString(unitDn));
      }
      employment.setDescription(description);

      employment.setHsaPublicTelephoneNumber(publicTelephoneNumber);
      employment.addHsaTelephoneNumbers(telephoneNumber);
      employment.setHsaSedfSwitchboardTelephoneNo(switchboardTelephoneNumber);
      employment.setHsaInternalPagerNumber(internalPagerNumber);
      employment.setPagerTelephoneNumber(pagerNumber);
      employment.setHsaTextPhoneNumber(textTelephoneNumber);
      employment.setMobileTelephoneNumber(mobilePhoneNumber);
      employment.setFacsimileTelephoneNumber(faxNumber);
      employment.addHsaTelephoneTime(telephoneTime);

      employment.setHsaPostalAddress(postalAddress);
      employment.setHsaInternalAddress(internalAddress);
      employment.setHsaStreetAddress(streetAddress);
      employment.setHsaSedfDeliveryAddress(deliveryAddress);
      employment.setHsaSedfInvoiceAddress(invoiceAddress);
      employment.setHsaConsigneeAddress(consigneeAddress);

      return employment;
    }

    public EmploymentBuilder consigneeAddress(Address address) {
      this.consigneeAddress = address;
      return this;
    }

    public EmploymentBuilder invoiceAddress(Address address) {
      this.invoiceAddress = address;
      return this;
    }

    public EmploymentBuilder deliveryAddress(Address address) {
      this.deliveryAddress = address;
      return this;
    }

    public EmploymentBuilder streetAddress(Address address) {
      this.streetAddress = address;
      return this;
    }

    public EmploymentBuilder internalAddress(Address address) {
      this.internalAddress = address;
      return this;
    }

    public EmploymentBuilder postalAddress(Address address) {
      this.postalAddress = address;
      return this;
    }

    public EmploymentBuilder telephoneTime(WeekdayTime telephoneTime) {
      this.telephoneTime.add(telephoneTime);
      return this;
    }

    public EmploymentBuilder faxNumber(PhoneNumber phoneNumber) {
      this.faxNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder mobilePhoneNumber(PhoneNumber phoneNumber) {
      this.mobilePhoneNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder textTelephoneNumber(PhoneNumber phoneNumber) {
      this.textTelephoneNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder pagerNumber(PhoneNumber phoneNumber) {
      this.pagerNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder internalPagerNumber(PhoneNumber phoneNumber) {
      this.internalPagerNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder switchboardTelephoneNumber(PhoneNumber phoneNumber) {
      this.switchboardTelephoneNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder telephoneNumber(PhoneNumber phoneNumber) {
      if (this.telephoneNumber == null) {
        this.telephoneNumber = new ArrayList<PhoneNumber>();
      }
      this.telephoneNumber.add(phoneNumber);
      return this;
    }

    public EmploymentBuilder publicTelephoneNumber(PhoneNumber phoneNumber) {
      this.publicTelephoneNumber = phoneNumber;
      return this;
    }

    public EmploymentBuilder description(String description) {
      if (this.description == null) {
        this.description = new ArrayList<String>();
      }
      this.description.add(description);
      return this;
    }

    public EmploymentBuilder unitDn(String unitDn) {
      this.unitDn = unitDn;
      return this;
    }

    public EmploymentBuilder title(String title) {
      this.title = title;
      return this;
    }
  }

  private static class PersonBuilder {
    private String hsaIdentity;
    private String givenName;
    private String nickName;
    private String middleName;
    private String surname;
    private String userId;
    private String title;
    private List<String> speciality;
    private List<Employment> employments;
    private String email;

    public Person build() {
      Person person = new Person();

      person.setHsaIdentity(hsaIdentity);
      person.setGivenName(givenName);
      person.setHsaNickName(nickName);
      person.setHsaMiddleName(middleName);
      person.setSn(surname);
      person.setVgrId(userId);
      person.setHsaTitle(title);
      person.setHsaSpecialityName(speciality);
      person.setEmployments(employments);
      person.setMail(email);

      return person;
    }

    public PersonBuilder email(String email) {
      this.email = email;
      return this;
    }

    public PersonBuilder employment(Employment employment) {
      if (this.employments == null) {
        this.employments = new ArrayList<Employment>();
      }
      this.employments.add(employment);
      return this;
    }

    public PersonBuilder speciality(String speciality) {
      if (this.speciality == null) {
        this.speciality = new ArrayList<String>();
      }
      this.speciality.add(speciality);
      return this;
    }

    public PersonBuilder title(String title) {
      this.title = title;
      return this;
    }

    public PersonBuilder userId(String userId) {
      this.userId = userId;
      return this;
    }

    public PersonBuilder surname(String surname) {
      this.surname = surname;
      return this;
    }

    public PersonBuilder middleName(String middleName) {
      this.middleName = middleName;
      return this;
    }

    public PersonBuilder nickName(String nickName) {
      this.nickName = nickName;
      return this;
    }

    public PersonBuilder givenName(String givenName) {
      this.givenName = givenName;
      return this;
    }

    public PersonBuilder hsaIdentity(String hsaIdentity) {
      this.hsaIdentity = hsaIdentity;
      return this;
    }
  }
}
