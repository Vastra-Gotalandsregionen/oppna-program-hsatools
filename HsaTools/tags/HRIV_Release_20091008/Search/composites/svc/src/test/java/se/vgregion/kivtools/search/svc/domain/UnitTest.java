/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.svc.domain;

import static org.junit.Assert.*;
import geo.google.datamodel.GeoCoordinate;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;

public class UnitTest {
  private static final String TEST = "TEST";
  private static final String TEST2 = "TEST2";

  private Unit unit;
  private List<HealthcareType> healthcareTypes;

  @Before
  public void setUp() {
    unit = new Unit();
    healthcareTypes = new ArrayList<HealthcareType>();
    unit.setHealthcareTypes(healthcareTypes);
  }

  /*
   * The tests commented below should probably go into a unit test for UnitFactory TODO: Move into UnitFactoryTest.java
   * 
   * @Test public void testIsShowAgeIntervalAndVisitingRulesNoHealtcareType() { assertTrue("Age interval and visiting rules should be displayed for a unit without healtcare types BVC, VC and JC",
   * unit.isShowAgeIntervalAndVisitingRules()); }
   * 
   * @Test public void testIsShowAgeIntervalAndVisitingRulesBVCHealtcareType() { HealthcareType healthcareType = new HealthcareType(); healthcareType.setDisplayName("Barnavårdscentral");
   * healthcareTypes.add(healthcareType); assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type BVC", unit.isShowAgeIntervalAndVisitingRules()); }
   * 
   * @Test public void testIsShowAgeIntervalAndVisitingRulesVCHealtcareType() { HealthcareType healthcareType = new HealthcareType(); healthcareType.setDisplayName("Vårdcentral");
   * healthcareTypes.add(healthcareType); assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type VC", unit.isShowAgeIntervalAndVisitingRules()); }
   * 
   * @Test public void testIsShowAgeIntervalAndVisitingRulesJCHealtcareType() { HealthcareType healthcareType = new HealthcareType(); healthcareType.setDisplayName("Jourcentral");
   * healthcareTypes.add(healthcareType); assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type JC", unit.isShowAgeIntervalAndVisitingRules()); }
   * 
   * @Test public void testIsShowInVgrVardVal() { assertFalse("Unit not in VGR Vardval should not show info regarding vardval", unit.isShowInVgrVardVal());
   * 
   * unit.setVgrVardVal(true); assertFalse("Unit should not show info regarding vardval if it isn't of type Vårdcentral", unit.isShowInVgrVardVal());
   * 
   * HealthcareType healthcareType = new HealthcareType(); healthcareType.setDisplayName("Vårdcentral"); healthcareTypes.add(healthcareType);
   * assertTrue("Unit should show info regarding vardval if it is of type Vårdcentral and is part of VGR Vardval", unit.isShowInVgrVardVal()); }
   */
  @Test
  public void testGetDnBase64() throws UnsupportedEncodingException {
    DN dn = DN.createDNFromString("CN=Hedvig h Blomfrö,OU=Falkenbergsnämnden,OU=Förtroendevalda,OU=Landstinget  Halland,DC=hkat,DC=lthalland,DC=com");
    unit.setDn(dn);
    String expected = "Y249SGVkdmlnIGggQmxvbWZy9ixvdT1GYWxrZW5iZXJnc27kbW5kZW4sb3U9RvZydHJvZW5kZXZh\r\nbGRhLG91PUxhbmRzdGluZ2V0ICBIYWxsYW5kLGRjPWhrYXQsZGM9bHRoYWxsYW5kLGRjPWNvbQ==\r\n";
    String result = unit.getDnBase64();
    assertEquals("Unexpected value for Base64-encoded DN", expected, result);
  }

  @Test
  public void testGetHsaBusinessClassificationTextFormatted() {
    List<String> businessClassificationTexts = new ArrayList<String>();
    businessClassificationTexts.add("Test1");
    businessClassificationTexts.add("Test2");
    businessClassificationTexts.add("Test3");
    unit.setHsaBusinessClassificationText(businessClassificationTexts);

    String expected = "Test1, Test2, Test3";
    String result = unit.getHsaBusinessClassificationTextFormatted();
    assertEquals("Unexpected value for formatted business classification", expected, result);

    unit.setHsaBusinessClassificationText(null);
    expected = "";
    result = unit.getHsaBusinessClassificationTextFormatted();
    assertEquals("Unexpected value for formatted business classification", expected, result);
  }

  @Test
  public void testGetContentValidationOk() throws InvalidFormatException {
    assertFalse(unit.getContentValidationOk());

    unit.setName("Angered");
    assertFalse(unit.getContentValidationOk());

    unit.setHsaMunicipalityName("Göteborg");
    Address address = new Address();
    unit.setHsaStreetAddress(address);
    assertFalse(unit.getContentValidationOk());

    address.setStreet("Storgatan");
    assertFalse(unit.getContentValidationOk());

    ArrayList<String> routelist = new ArrayList<String>();
    routelist.add("Via centralen");
    unit.setHsaRoute(routelist);
    assertFalse(unit.getContentValidationOk());

    unit.setHsaGeographicalCoordinates("coords");
    assertFalse(unit.getContentValidationOk());

    List<WeekdayTime> surgeryHours = new ArrayList<WeekdayTime>();
    unit.setHsaSurgeryHours(surgeryHours);
    assertFalse(unit.getContentValidationOk());

    surgeryHours.add(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(unit.getContentValidationOk());

    List<WeekdayTime> dropInHours = new ArrayList<WeekdayTime>();
    unit.setHsaDropInHours(dropInHours);
    assertFalse(unit.getContentValidationOk());

    dropInHours.add(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(unit.getContentValidationOk());

    List<PhoneNumber> telephoneNumbers = new ArrayList<PhoneNumber>();
    unit.setHsaPublicTelephoneNumber(telephoneNumbers);
    assertFalse(unit.getContentValidationOk());

    telephoneNumbers.add(new PhoneNumber("031-12345"));
    assertFalse(unit.getContentValidationOk());

    List<WeekdayTime> telephoneTime = new ArrayList<WeekdayTime>();
    unit.setHsaTelephoneTime(telephoneTime);
    assertFalse(unit.getContentValidationOk());

    telephoneTime.add(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(unit.getContentValidationOk());

    unit.setLabeledURI("http://angered.vgregion.se");
    assertFalse(unit.getContentValidationOk());

    List<String> description = new ArrayList<String>();
    unit.setDescription(description);
    assertFalse(unit.getContentValidationOk());

    description.add("Beskrivning");
    assertFalse(unit.getContentValidationOk());

    unit.setHsaVisitingRuleAge("0-99");
    assertFalse(unit.getContentValidationOk());

    unit.setHsaVisitingRules("Besöksregler");
    assertFalse(unit.getContentValidationOk());

    unit.setHsaManagementText("Management text");
    assertTrue(unit.getContentValidationOk());
  }

  @Test
  public void testSetLabeledURI() {
    try {
      unit.setLabeledURI(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    unit.setLabeledURI("");
    assertEquals("", unit.getLabeledURI());

    unit.setLabeledURI("www.test.com");
    assertEquals("http://www.test.com", unit.getLabeledURI());

    unit.setLabeledURI("http://www.test2.com");
    assertEquals("http://www.test2.com", unit.getLabeledURI());

    unit.setLabeledURI("https://www.test2.com");
    assertEquals("https://www.test2.com", unit.getLabeledURI());
  }

  @Test
  public void testGetShouldVgrTempInfoBeShown() {
    assertFalse(unit.getShouldVgrTempInfoBeShown());

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    unit.setVgrTempInfoStart(calendar.getTime());
    assertFalse(unit.getShouldVgrTempInfoBeShown());

    unit.setVgrTempInfoEnd(calendar.getTime());
    assertFalse(unit.getShouldVgrTempInfoBeShown());

    calendar.add(Calendar.DAY_OF_YEAR, 3);
    unit.setVgrTempInfoEnd(calendar.getTime());
    assertTrue(unit.getShouldVgrTempInfoBeShown());
  }

  @Test
  public void testVgrVardval() {
    assertFalse(unit.isVgrVardVal());
    unit.setVgrVardVal(true);
    assertTrue(unit.isVgrVardVal());
    unit.setVgrVardVal(false);
    assertFalse(unit.isVgrVardVal());
  }

  @Test
  public void testDistanceToTarget() {
    assertNull(unit.getDistanceToTarget());
    unit.setDistanceToTarget(TEST);
    assertEquals(TEST, unit.getDistanceToTarget());
    unit.setDistanceToTarget(TEST2);
    assertEquals(TEST2, unit.getDistanceToTarget());
  }

  @Test
  public void testGeoCoordinate() {
    assertNull(unit.getGeoCoordinate());
    unit.setGeoCoordinate(new GeoCoordinate(0, 0, null));
    assertEquals(0.0, unit.getGeoCoordinate().getLatitude(), 0.0);
    unit.setGeoCoordinate(new GeoCoordinate(0, 123.0, null));
    assertEquals(123.0, unit.getGeoCoordinate().getLatitude(), 0.0);
  }

  @Test
  public void testHsaRouteConcatenated() {
    assertEquals("", unit.getHsaRouteConcatenated());
    unit.setHsaRouteConcatenated(TEST);
    assertEquals(TEST, unit.getHsaRouteConcatenated());
    unit.setHsaRouteConcatenated(TEST2);
    assertEquals(TEST2, unit.getHsaRouteConcatenated());
  }

  @Test
  public void testVgrEANCode() {
    assertNull(unit.getVgrEANCode());
    unit.setVgrEANCode(TEST);
    assertEquals(TEST, unit.getVgrEANCode());
    unit.setVgrEANCode(TEST2);
    assertEquals(TEST2, unit.getVgrEANCode());
  }

  @Test
  public void testHsaMunicipalityCode() {
    assertNull(unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityCode(TEST);
    assertEquals(TEST, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityCode(TEST2);
    assertEquals(TEST2, unit.getHsaMunicipalityCode());
  }

  @Test
  public void testHsaMunicipalitySectionName() {
    assertNull(unit.getHsaMunicipalitySectionName());
    unit.setHsaMunicipalitySectionName(TEST);
    assertEquals(TEST, unit.getHsaMunicipalitySectionName());
    unit.setHsaMunicipalitySectionName(TEST2);
    assertEquals(TEST2, unit.getHsaMunicipalitySectionName());
  }

  @Test
  public void testHsaMunicipalitySectionCode() {
    assertNull(unit.getHsaMunicipalitySectionCode());
    unit.setHsaMunicipalitySectionCode(TEST);
    assertEquals(TEST, unit.getHsaMunicipalitySectionCode());
    unit.setHsaMunicipalitySectionCode(TEST2);
    assertEquals(TEST2, unit.getHsaMunicipalitySectionCode());
  }

  @Test
  public void testHsaCountyCode() {
    assertNull(unit.getHsaCountyCode());
    unit.setHsaCountyCode(TEST);
    assertEquals(TEST, unit.getHsaCountyCode());
    unit.setHsaCountyCode(TEST2);
    assertEquals(TEST2, unit.getHsaCountyCode());
  }

  @Test
  public void testHsaCountyName() {
    assertNull(unit.getHsaCountyName());
    unit.setHsaCountyName(TEST);
    assertEquals(TEST, unit.getHsaCountyName());
    unit.setHsaCountyName(TEST2);
    assertEquals(TEST2, unit.getHsaCountyName());
  }

  @Test
  public void testHsaManagementCode() {
    assertNull(unit.getHsaManagementCode());
    unit.setHsaManagementCode(TEST);
    assertEquals(TEST, unit.getHsaManagementCode());
    unit.setHsaManagementCode(TEST2);
    assertEquals(TEST2, unit.getHsaManagementCode());
  }

  @Test
  public void testHsaManagementName() {
    assertNull(unit.getHsaManagementName());
    unit.setHsaManagementName(TEST);
    assertEquals(TEST, unit.getHsaManagementName());
    unit.setHsaManagementName(TEST2);
    assertEquals(TEST2, unit.getHsaManagementName());
  }

  @Test
  public void testHsaAdministrationForm() {
    assertNull(unit.getHsaAdministrationForm());
    unit.setHsaAdministrationForm(TEST);
    assertEquals(TEST, unit.getHsaAdministrationForm());
    unit.setHsaAdministrationForm(TEST2);
    assertEquals(TEST2, unit.getHsaAdministrationForm());
  }

  @Test
  public void testHsaAdministrationFormText() {
    assertNull(unit.getHsaAdministrationFormText());
    unit.setHsaAdministrationFormText(TEST);
    assertEquals(TEST, unit.getHsaAdministrationFormText());
    unit.setHsaAdministrationFormText(TEST2);
    assertEquals(TEST2, unit.getHsaAdministrationFormText());
  }

  @Test
  public void testDn() {
    assertNull(unit.getDn());
    unit.setDn(DN.createDNFromString("ou=a,ou=b,o=c"));
    assertEquals("ou=a,ou=b,o=c", unit.getDn().toString());
    unit.setDn(DN.createDNFromString("ou=d,ou=e,o=f"));
    assertEquals("ou=d,ou=e,o=f", unit.getDn().toString());
  }

  @Test
  public void testOrganizationalUnitNameShort() {
    assertNull(unit.getOrganizationalUnitNameShort());
    unit.setOrganizationalUnitNameShort(TEST);
    assertEquals(TEST, unit.getOrganizationalUnitNameShort());
    unit.setOrganizationalUnitNameShort(TEST2);
    assertEquals(TEST2, unit.getOrganizationalUnitNameShort());
  }

  @Test
  public void testLdapDistinguishedName() {
    assertNull(unit.getLdapDistinguishedName());
    unit.setLdapDistinguishedName(TEST);
    assertEquals(TEST, unit.getLdapDistinguishedName());
    unit.setLdapDistinguishedName(TEST2);
    assertEquals(TEST2, unit.getLdapDistinguishedName());
  }

  @Test
  public void testMail() {
    assertNull(unit.getMail());
    unit.setMail(TEST);
    assertEquals(TEST, unit.getMail());
    unit.setMail(TEST2);
    assertEquals(TEST2, unit.getMail());
  }

  @Test
  public void testLocality() {
    assertNull(unit.getLocality());
    unit.setLocality(TEST);
    assertEquals(TEST, unit.getLocality());
    unit.setLocality(TEST2);
    assertEquals(TEST2, unit.getLocality());
  }

  @Test
  public void testVgrInternalSedfInvoiceAddress() {
    assertNull(unit.getVgrInternalSedfInvoiceAddress());
    unit.setVgrInternalSedfInvoiceAddress(TEST);
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    unit.setVgrInternalSedfInvoiceAddress(TEST2);
    assertEquals(TEST2, unit.getVgrInternalSedfInvoiceAddress());
  }

  @Test
  public void testVgrCareType() {
    assertNull(unit.getVgrCareType());
    unit.setVgrCareType(TEST);
    assertEquals(TEST, unit.getVgrCareType());
    unit.setVgrCareType(TEST2);
    assertEquals(TEST2, unit.getVgrCareType());
  }

  @Test
  public void testVgrCareTypeText() {
    assertNull(unit.getVgrCareTypeText());
    unit.setVgrCareTypeText(TEST);
    assertEquals(TEST, unit.getVgrCareTypeText());
    unit.setVgrCareTypeText(TEST2);
    assertEquals(TEST2, unit.getVgrCareTypeText());
  }

  @Test
  public void testVgrAO3kod() {
    assertNull(unit.getVgrAO3kod());
    unit.setVgrAO3kod(TEST);
    assertEquals(TEST, unit.getVgrAO3kod());
    unit.setVgrAO3kod(TEST2);
    assertEquals(TEST2, unit.getVgrAO3kod());
  }

  @Test
  public void testVgrAO3kodText() {
    assertNull(unit.getVgrAO3kodText());
    unit.setVgrAO3kodText(TEST);
    assertEquals(TEST, unit.getVgrAO3kodText());
    unit.setVgrAO3kodText(TEST2);
    assertEquals(TEST2, unit.getVgrAO3kodText());
  }

  @Test
  public void testHsaIdentity() {
    assertNull(unit.getHsaIdentity());
    unit.setHsaIdentity(TEST);
    assertEquals(TEST, unit.getHsaIdentity());
    unit.setHsaIdentity(TEST2);
    assertEquals(TEST2, unit.getHsaIdentity());
  }

  @Test
  public void testHsaSedfSwitchboardTelephoneNo() {
    assertNull(unit.getHsaSedfSwitchboardTelephoneNo());
    unit.setHsaSedfSwitchboardTelephoneNo(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getHsaSedfSwitchboardTelephoneNo().toString());
    unit.setHsaSedfSwitchboardTelephoneNo(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getHsaSedfSwitchboardTelephoneNo().toString());
  }

  @Test
  public void testHsaInternalPagerNumber() {
    assertNull(unit.getHsaInternalPagerNumber());
    unit.setHsaInternalPagerNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getHsaInternalPagerNumber().toString());
    unit.setHsaInternalPagerNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getHsaInternalPagerNumber().toString());
  }

  @Test
  public void testPagerTelephoneNumber() {
    assertNull(unit.getPagerTelephoneNumber());
    unit.setPagerTelephoneNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getPagerTelephoneNumber().toString());
    unit.setPagerTelephoneNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getPagerTelephoneNumber().toString());
  }

  @Test
  public void testHsaTextPhoneNumber() {
    assertNull(unit.getHsaTextPhoneNumber());
    unit.setHsaTextPhoneNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getHsaTextPhoneNumber().toString());
    unit.setHsaTextPhoneNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getHsaTextPhoneNumber().toString());
  }

  @Test
  public void testMobileTelephoneNumber() {
    assertNull(unit.getMobileTelephoneNumber());
    unit.setMobileTelephoneNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getMobileTelephoneNumber().toString());
    unit.setMobileTelephoneNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getMobileTelephoneNumber().toString());
  }

  @Test
  public void testHsaSmsTelephoneNumber() {
    assertNull(unit.getHsaSmsTelephoneNumber());
    unit.setHsaSmsTelephoneNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getHsaSmsTelephoneNumber().toString());
    unit.setHsaSmsTelephoneNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getHsaSmsTelephoneNumber().toString());
  }

  @Test
  public void testFacsimileTelephoneNumber() {
    assertNull(unit.getFacsimileTelephoneNumber());
    unit.setFacsimileTelephoneNumber(new PhoneNumber(TEST));
    assertEquals(TEST, unit.getFacsimileTelephoneNumber().toString());
    unit.setFacsimileTelephoneNumber(new PhoneNumber(TEST2));
    assertEquals(TEST2, unit.getFacsimileTelephoneNumber().toString());
  }

  @Test
  public void testHsaTelephoneNumber() {
    assertNull(unit.getHsaTelephoneNumber());
    unit.setHsaTelephoneNumber(Arrays.asList(new PhoneNumber[] { new PhoneNumber(TEST) }));
    assertEquals("[" + TEST + "]", unit.getHsaTelephoneNumber().toString());
    unit.setHsaTelephoneNumber(Arrays.asList(new PhoneNumber[] { new PhoneNumber(TEST2) }));
    assertEquals("[" + TEST2 + "]", unit.getHsaTelephoneNumber().toString());
  }

  @Test
  public void testHsaUnitPrescriptionCode() {
    assertNull(unit.getHsaUnitPrescriptionCode());
    unit.setHsaUnitPrescriptionCode(TEST);
    assertEquals(TEST, unit.getHsaUnitPrescriptionCode());
    unit.setHsaUnitPrescriptionCode(TEST2);
    assertEquals(TEST2, unit.getHsaUnitPrescriptionCode());
  }

  @Test
  public void testOu() {
    assertNull(unit.getOu());
    unit.setOu(TEST);
    assertEquals(TEST, unit.getOu());
    unit.setOu(TEST2);
    assertEquals(TEST2, unit.getOu());
  }

  @Test
  public void testVgrEDICode() {
    assertNull(unit.getVgrEDICode());
    unit.setVgrEDICode(TEST);
    assertEquals(TEST, unit.getVgrEDICode());
    unit.setVgrEDICode(TEST2);
    assertEquals(TEST2, unit.getVgrEDICode());
  }

  @Test
  public void testHsaInternalAddress() {
    assertNull(unit.getHsaInternalAddress());
    unit.setHsaInternalAddress(new Address(TEST, null, TEST, null));
    assertEquals(TEST, unit.getHsaInternalAddress().getStreet());
    unit.setHsaInternalAddress(new Address(TEST2, null, TEST2, null));
    assertEquals(TEST2, unit.getHsaInternalAddress().getStreet());
  }

  @Test
  public void testHsaPostalAddress() {
    assertNull(unit.getHsaPostalAddress());
    unit.setHsaPostalAddress(new Address(TEST, null, TEST, null));
    assertEquals(TEST, unit.getHsaPostalAddress().getStreet());
    unit.setHsaPostalAddress(new Address(TEST2, null, TEST2, null));
    assertEquals(TEST2, unit.getHsaPostalAddress().getStreet());
  }

  @Test
  public void testHsaSedfDeliveryAddress() {
    assertNull(unit.getHsaSedfDeliveryAddress());
    unit.setHsaSedfDeliveryAddress(new Address(TEST, null, TEST, null));
    assertEquals(TEST, unit.getHsaSedfDeliveryAddress().getStreet());
    unit.setHsaSedfDeliveryAddress(new Address(TEST2, null, TEST2, null));
    assertEquals(TEST2, unit.getHsaSedfDeliveryAddress().getStreet());
  }

  @Test
  public void testHsaSedfInvoiceAddress() {
    assertNull(unit.getHsaSedfInvoiceAddress());
    unit.setHsaSedfInvoiceAddress(new Address(TEST, null, TEST, null));
    assertEquals(TEST, unit.getHsaSedfInvoiceAddress().getStreet());
    unit.setHsaSedfInvoiceAddress(new Address(TEST2, null, TEST2, null));
    assertEquals(TEST2, unit.getHsaSedfInvoiceAddress().getStreet());
  }

  @Test
  public void testVgrAnsvarsnummer() {
    assertNull(unit.getVgrAnsvarsnummer());
    unit.setVgrAnsvarsnummer(Arrays.asList(new String[] { TEST }));
    assertEquals("[" + TEST + "]", unit.getVgrAnsvarsnummer().toString());
    unit.setVgrAnsvarsnummer(Arrays.asList(new String[] { TEST2 }));
    assertEquals("[" + TEST2 + "]", unit.getVgrAnsvarsnummer().toString());
  }

  @Test
  public void testVgrOrganizationalRole() {
    assertNull(unit.getVgrOrganizationalRole());
    unit.setVgrOrganizationalRole(TEST);
    assertEquals(TEST, unit.getVgrOrganizationalRole());
    unit.setVgrOrganizationalRole(TEST2);
    assertEquals(TEST2, unit.getVgrOrganizationalRole());
  }

  @Test
  public void testHashCode() {
    int expected = 31;
    int result = unit.hashCode();
    assertEquals(expected, result);

    unit.setHsaIdentity("abc");
    expected = 96385;
    result = unit.hashCode();
    assertEquals(expected, result);
  }

  @Test
  public void testEquals() {
    assertTrue(unit.equals(unit));
    assertFalse(unit.equals(null));
    assertFalse(unit.equals(this));
    Unit other = new Unit();
    assertTrue(unit.equals(other));
    other.setHsaIdentity("abc");
    assertFalse(unit.equals(other));
    unit.setHsaIdentity("def");
    assertFalse(unit.equals(other));
    unit.setHsaIdentity("abc");
    assertTrue(unit.equals(other));
  }

  @Test
  public void testHsaVisitingRuleAge() {
    assertNull(unit.getHsaVisitingRuleAge());
    unit.setHsaVisitingRuleAge("0-99");
    assertEquals("Alla \u00E5ldrar", unit.getHsaVisitingRuleAge());
    unit.setHsaVisitingRuleAge("20-99");
    assertEquals("20 \u00E5r eller \u00E4ldre", unit.getHsaVisitingRuleAge());
    unit.setHsaVisitingRuleAge("20");
    assertEquals("20 \u00E5r", unit.getHsaVisitingRuleAge());
    unit.setHsaVisitingRuleAge("");
    assertEquals("", unit.getHsaVisitingRuleAge());
  }

  @Test
  public void testVgrTempInfo() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyyMMdd");

    assertNull(unit.getVgrTempInfo());
    unit.setVgrTempInfo("");
    assertEquals("", unit.getVgrTempInfo());
    unit.setVgrTempInfo("01234567890123456789");
    assertEquals("01234567890123456789", unit.getVgrTempInfo());
    unit.setVgrTempInfo("01234-56789");
    assertEquals("01234-56789", unit.getVgrTempInfo());
    unit.setVgrTempInfo("012345678901234567-");
    assertEquals("012345678901234567-", unit.getVgrTempInfo());
    unit.setVgrTempInfo("aaaabbcc-ddddeeff temp temp");
    assertEquals("aaaabbcc-ddddeeff temp temp", unit.getVgrTempInfo());
    unit.setVgrTempInfo("20090108-temp temp temp temp");
    assertEquals("20090108-temp temp temp temp", unit.getVgrTempInfo());
    assertEquals(format.parse("20090101"), unit.getVgrTempInfoStart());
    assertEquals("temp temp temp", unit.getVgrTempInfoBody());
    unit.setVgrTempInfo("20090108-20090118 temp temp temp");
    assertEquals("20090108-20090118 temp temp temp", unit.getVgrTempInfo());
    assertEquals(format.parse("20090119"), unit.getVgrTempInfoEnd());
    assertEquals("temp temp temp", unit.getVgrTempInfoBody());
  }

  @Test
  public void testVgrRefInfo() {
    assertNull(unit.getVgrRefInfo());
    unit.setVgrRefInfo(TEST);
    assertEquals(TEST, unit.getVgrRefInfo());
    unit.setVgrRefInfo(TEST2);
    assertEquals(TEST2, unit.getVgrRefInfo());
  }

  @Test
  public void testFormattedAncestor() {
    try {
      unit.getFormattedAncestor();
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    unit.setHsaIdentity("123-ABC");
    assertEquals("", unit.getFormattedAncestor());

    unit.setHsaIdentity("123-FGH");
    // Make sure that HealthcareTypeConditionHelper is reset to it's correct state.
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
        super.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");
      }
    };

    ArrayList<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
    HealthcareType healtcareType = healthcareTypeConditionHelper.getHealthcareTypeByName("Sjukhus");
    healthcareTypes.add(healtcareType);
    unit.setHealthcareTypes(healthcareTypes);
    assertEquals("", unit.getFormattedAncestor());

    healthcareTypes.clear();
    healtcareType = healthcareTypeConditionHelper.getHealthcareTypeByName("Akutmottagning");
    healthcareTypes.add(healtcareType);
    unit.setHealthcareTypes(healthcareTypes);
    DN dn = DN.createDNFromString("ou=SubUnit,ou=AncestorUnit,o=VGR");
    unit.setDn(dn);
    assertEquals(", tillhör AncestorUnit", unit.getFormattedAncestor());
  }
}
