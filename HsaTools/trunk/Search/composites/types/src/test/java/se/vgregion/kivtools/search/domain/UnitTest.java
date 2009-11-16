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
package se.vgregion.kivtools.search.domain;

import static org.junit.Assert.*;
import geo.google.datamodel.GeoCoordinate;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.PojoTester;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.InvalidFormatException;

import com.domainlanguage.time.TimePoint;

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

    telephoneNumbers.add(PhoneNumber.createPhoneNumber("031-12345"));
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
  public void testBasicProperties() throws InvalidFormatException {
    PojoTester.testProperty(unit, "vgrVardVal", boolean.class, false, true, false);
    PojoTester.testProperty(unit, "distanceToTarget", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "geoCoordinate", GeoCoordinate.class, null, new GeoCoordinate(0, 0, null), new GeoCoordinate(0, 123.0, null));
    PojoTester.testProperty(unit, "hsaRouteConcatenated", String.class, "", TEST, TEST2);
    PojoTester.testProperty(unit, "vgrEANCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaMunicipalityCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaMunicipalitySectionName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaMunicipalitySectionCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaCountyCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaCountyName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaManagementCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaManagementName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaAdministrationForm", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaAdministrationFormText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "dn", DN.class, null, DN.createDNFromString("ou=a,ou=b,o=c"), DN.createDNFromString("ou=d,ou=e,o=f"));
    PojoTester.testProperty(unit, "organizationalUnitNameShort", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "ldapDistinguishedName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "mail", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "locality", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrInternalSedfInvoiceAddress", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrCareType", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrCareTypeText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrAO3kod", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrAO3kodText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaIdentity", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaSedfSwitchboardTelephoneNo", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "hsaInternalPagerNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "pagerTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "hsaTextPhoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "mobileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "hsaSmsTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "facsimileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(unit, "hsaTelephoneNumber", List.class, null, Arrays.asList(PhoneNumber.createPhoneNumber(TEST)), Arrays.asList(PhoneNumber.createPhoneNumber(TEST2)));
    PojoTester.testProperty(unit, "hsaUnitPrescriptionCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "ou", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "vgrEDICode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "hsaInternalAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(unit, "hsaPostalAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(unit, "hsaSedfDeliveryAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(unit, "hsaSedfInvoiceAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(unit, "hsaConsigneeAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(unit, "vgrAnsvarsnummer", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(unit, "vgrOrganizationalRole", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "wgs84Lat", double.class, 0.0, 1.0, 32.0);
    PojoTester.testProperty(unit, "wgs84Long", double.class, 0.0, 1.0, 32.0);
    PojoTester.testProperty(unit, "rt90X", int.class, 0, 1, 32);
    PojoTester.testProperty(unit, "rt90Y", int.class, 0, 1, 32);
    PojoTester.testProperty(unit, "objectClass", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "isUnit", boolean.class, false, true, false);
    PojoTester.testProperty(unit, "hsaBusinessClassificationCode", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(unit, "hsaBusinessClassificationText", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(unit, "mvkCaseTypes", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(unit, "hsaEndDate", Date.class, null, new Date(), new Date());
    PojoTester.testProperty(unit, "showAgeInterval", boolean.class, false, true, false);
    PojoTester.testProperty(unit, "showVisitingRules", boolean.class, false, true, false);
    PojoTester.testProperty(unit, "internalWebsite", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "contractCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "visitingHours", List.class, null, Arrays.asList(new WeekdayTime(1, 2, 10, 12, 11, 13)), Arrays.asList(new WeekdayTime(3, 5, 12, 20, 15, 30)));
    PojoTester.testProperty(unit, "visitingRuleReferral", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "manager", String.class, null, TEST, TEST2);
    PojoTester.testProperty(unit, "managerDN", String.class, null, TEST, TEST2);
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
        super.setImplResourcePath("testproperties.healthcaretypeconditionhelper.two_properties");
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

  @Test
  public void testCreateTimestamp() {
    assertNull(unit.getCreateTimestamp());
    assertEquals("", unit.getCreateTimestampFormatted());
    assertEquals("", unit.getCreateTimestampFormattedInW3CDatetimeFormat());

    unit.setCreateTimestamp(TimePoint.parseFrom("20090101120102", "yyyyMMddHHmmss", TimeZone.getDefault()));
    assertEquals("2009-01-01 12:01:02", unit.getCreateTimestampFormatted());
    assertEquals("2009-01-01T12:01:02+01:00", unit.getCreateTimestampFormattedInW3CDatetimeFormat());
  }

  @Test
  public void testModifyTimestamp() {
    assertNull(unit.getModifyTimestamp());
    assertEquals("", unit.getModifyTimestampFormatted());
    assertEquals("", unit.getModifyTimestampFormattedInW3CDatetimeFormat());

    unit.setModifyTimestamp(TimePoint.parseFrom("20090101120102", "yyyyMMddHHmmss", TimeZone.getDefault()));
    assertEquals("2009-01-01 12:01:02", unit.getModifyTimestampFormatted());
    assertEquals("2009-01-01T12:01:02+01:00", unit.getModifyTimestampFormattedInW3CDatetimeFormat());
  }
}
