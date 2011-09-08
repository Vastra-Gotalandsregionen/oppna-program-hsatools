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

package se.vgregion.kivtools.search.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import geo.google.datamodel.GeoCoordinate;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
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

  @Before
  public void setUp() {
    this.unit = new Unit();
  }

  @Test
  public void testGetDnBase64() throws UnsupportedEncodingException {
    DN dn = DN.createDNFromString("CN=Hedvig h Blomfrö,OU=Falkenbergsnämnden,OU=Förtroendevalda,OU=Landstinget  Halland,DC=hkat,DC=lthalland,DC=com");
    this.unit.setDn(dn);
    String expected = "Y249SGVkdmlnIGggQmxvbWZy9ixvdT1GYWxrZW5iZXJnc27kbW5kZW4sb3U9RvZydHJvZW5kZXZh\r\nbGRhLG91PUxhbmRzdGluZ2V0ICBIYWxsYW5kLGRjPWhrYXQsZGM9bHRoYWxsYW5kLGRjPWNvbQ==\r\n";
    String result = this.unit.getDnBase64();
    assertEquals("Unexpected value for Base64-encoded DN", expected, result);
  }

  @Test
  public void testGetHsaBusinessClassificationTextFormatted() {
    List<String> businessClassificationTexts = new ArrayList<String>();
    businessClassificationTexts.add("Test1");
    businessClassificationTexts.add("Test2");
    businessClassificationTexts.add("Test3");
    this.unit.setHsaBusinessClassificationText(businessClassificationTexts);

    String expected = "Test1, Test2, Test3";
    String result = this.unit.getHsaBusinessClassificationTextFormatted();
    assertEquals("Unexpected value for formatted business classification", expected, result);

    this.unit.setHsaBusinessClassificationText(null);
    expected = "";
    result = this.unit.getHsaBusinessClassificationTextFormatted();
    assertEquals("Unexpected value for formatted business classification", expected, result);
  }

  @Test
  public void testGetContentValidationOk() throws InvalidFormatException {
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setName("Angered");
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setHsaMunicipalityName("Göteborg");
    Address address = new Address();
    this.unit.setHsaStreetAddress(address);
    assertFalse(this.unit.getContentValidationOk());

    address.setStreet("Storgatan");
    assertFalse(this.unit.getContentValidationOk());

    ArrayList<String> routelist = new ArrayList<String>();
    routelist.add("Via centralen");
    this.unit.addHsaRoute(routelist);
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setHsaGeographicalCoordinates("coords");
    assertFalse(this.unit.getContentValidationOk());

    this.unit.addHsaSurgeryHours(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(this.unit.getContentValidationOk());

    this.unit.addHsaDropInHours(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(this.unit.getContentValidationOk());

    this.unit.addHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("031-12345"));
    assertFalse(this.unit.getContentValidationOk());

    this.unit.addHsaTelephoneTime(new WeekdayTime(1, 5, 9, 0, 16, 0));
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setLabeledURI("http://angered.vgregion.se");
    assertFalse(this.unit.getContentValidationOk());

    List<String> description = new ArrayList<String>();
    description.add("Beskrivning");
    this.unit.addDescription(description);
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setHsaVisitingRuleAge("0-99");
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setHsaVisitingRules("Besöksregler");
    assertFalse(this.unit.getContentValidationOk());

    this.unit.setHsaManagementText("Management text");
    assertTrue(this.unit.getContentValidationOk());
  }

  @Test
  public void testSetLabeledURI() {
    try {
      this.unit.setLabeledURI(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    this.unit.setLabeledURI("");
    assertEquals("", this.unit.getLabeledURI());

    this.unit.setLabeledURI("www.test.com");
    assertEquals("http://www.test.com", this.unit.getLabeledURI());

    this.unit.setLabeledURI("http://www.test2.com");
    assertEquals("http://www.test2.com", this.unit.getLabeledURI());

    this.unit.setLabeledURI("https://www.test2.com");
    assertEquals("https://www.test2.com", this.unit.getLabeledURI());
  }

  @Test
  public void testGetShouldVgrTempInfoBeShown() {
    assertFalse(this.unit.getShouldVgrTempInfoBeShown());

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    this.unit.setVgrTempInfoStart(calendar.getTime());
    assertFalse(this.unit.getShouldVgrTempInfoBeShown());

    this.unit.setVgrTempInfoEnd(calendar.getTime());
    assertFalse(this.unit.getShouldVgrTempInfoBeShown());

    calendar.add(Calendar.DAY_OF_YEAR, 3);
    this.unit.setVgrTempInfoEnd(calendar.getTime());
    assertTrue(this.unit.getShouldVgrTempInfoBeShown());
  }

  @Test
  public void testBasicProperties() throws InvalidFormatException {
    PojoTester.testProperty(this.unit, "vgrVardVal", boolean.class, false, true, false);
    PojoTester.testProperty(this.unit, "distanceToTarget", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "geoCoordinate", GeoCoordinate.class, null, new GeoCoordinate(0, 0, null), new GeoCoordinate(0, 123.0, null));
    PojoTester.testProperty(this.unit, "hsaRouteConcatenated", String.class, "", TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaMunicipalityCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaMunicipalitySectionName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaMunicipalitySectionCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaCountyCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaCountyName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaManagementCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaManagementName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaAdministrationForm", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaAdministrationFormText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "dn", DN.class, null, DN.createDNFromString("ou=a,ou=b,o=c"), DN.createDNFromString("ou=d,ou=e,o=f"));
    PojoTester.testProperty(this.unit, "organizationalUnitNameShort", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "ldapDistinguishedName", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "mail", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "locality", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "vgrInternalSedfInvoiceAddress", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "careType", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "careTypeText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "vgrAO3kod", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "vgrAO3kodText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaIdentity", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaSedfSwitchboardTelephoneNo", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "hsaInternalPagerNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "pagerTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "hsaTextPhoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "mobileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "facsimileTelephoneNumber", PhoneNumber.class, null, PhoneNumber.createPhoneNumber(TEST), PhoneNumber.createPhoneNumber(TEST2));
    PojoTester.testProperty(this.unit, "hsaUnitPrescriptionCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "ou", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaInternalAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(this.unit, "hsaPostalAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(this.unit, "hsaSedfDeliveryAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(this.unit, "hsaSedfInvoiceAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(this.unit, "hsaConsigneeAddress", Address.class, null, new Address(TEST, null, TEST, null), new Address(TEST2, null, TEST2, null));
    PojoTester.testProperty(this.unit, "vgrAnsvarsnummer", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(this.unit, "vgrOrganizationalRole", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "wgs84Lat", double.class, 0.0, 1.0, 32.0);
    PojoTester.testProperty(this.unit, "wgs84Long", double.class, 0.0, 1.0, 32.0);
    PojoTester.testProperty(this.unit, "rt90X", int.class, 0, 1, 32);
    PojoTester.testProperty(this.unit, "rt90Y", int.class, 0, 1, 32);
    PojoTester.testProperty(this.unit, "objectClass", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "isUnit", boolean.class, false, true, false);
    PojoTester.testProperty(this.unit, "hsaBusinessClassificationCode", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(this.unit, "hsaBusinessClassificationText", List.class, null, Arrays.asList(TEST), Arrays.asList(TEST2));
    PojoTester.testProperty(this.unit, "hsaEndDate", Date.class, null, new Date(), new Date());
    PojoTester.testProperty(this.unit, "showAgeInterval", boolean.class, false, true, false);
    PojoTester.testProperty(this.unit, "showVisitingRules", boolean.class, false, true, false);
    PojoTester.testProperty(this.unit, "internalWebsite", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "contractCode", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "visitingHours", List.class, null, Arrays.asList(new WeekdayTime(1, 2, 10, 12, 11, 13)), Arrays.asList(new WeekdayTime(3, 5, 12, 20, 15, 30)));
    PojoTester.testProperty(this.unit, "visitingRuleReferral", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "manager", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "managerDN", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaBusinessType", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaPatientVisitingRules", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaAltText", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaVpwInformation1", String.class, null, TEST, TEST2);
    PojoTester.testProperty(this.unit, "hsaVpwInformation2", String.class, null, TEST, TEST2);
  }

  @Test
  public void testHsaTelephoneNumbers() {
    assertEquals("hsaTelephoneNumber initial value", Collections.emptyList(), this.unit.getHsaTelephoneNumber());

    this.unit.addHsaTelephoneNumber(Arrays.asList(PhoneNumber.createPhoneNumber("031-123456")));
    assertEquals("hsaTelephoneNumber count", 1, this.unit.getHsaTelephoneNumber().size());
  }

  @Test
  public void testHashCode() {
    int expected = 31;
    int result = this.unit.hashCode();
    assertEquals(expected, result);

    this.unit.setHsaIdentity("abc");
    expected = 96385;
    result = this.unit.hashCode();
    assertEquals(expected, result);
  }

  @Test
  public void testEquals() {
    assertTrue(this.unit.equals(this.unit));
    assertFalse(this.unit.equals(null));
    assertFalse(this.unit.equals(this));
    Unit other = new Unit();
    assertTrue(this.unit.equals(other));
    other.setHsaIdentity("abc");
    assertFalse(this.unit.equals(other));
    this.unit.setHsaIdentity("def");
    assertFalse(this.unit.equals(other));
    this.unit.setHsaIdentity("abc");
    assertTrue(this.unit.equals(other));
  }

  @Test
  public void testHsaVisitingRuleAge() {
    assertNull(this.unit.getHsaVisitingRuleAge());
    this.unit.setHsaVisitingRuleAge("0-99");
    assertEquals("Alla \u00E5ldrar", this.unit.getHsaVisitingRuleAge());
    this.unit.setHsaVisitingRuleAge("00-99");
    assertEquals("Alla \u00E5ldrar", this.unit.getHsaVisitingRuleAge());
    this.unit.setHsaVisitingRuleAge("20-99");
    assertEquals("20 \u00E5r eller \u00E4ldre", this.unit.getHsaVisitingRuleAge());
    this.unit.setHsaVisitingRuleAge("20");
    assertEquals("20 \u00E5r", this.unit.getHsaVisitingRuleAge());
    this.unit.setHsaVisitingRuleAge("");
    assertEquals("", this.unit.getHsaVisitingRuleAge());
  }

  @Test
  public void testVgrTempInfo() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyyMMdd");

    assertNull(this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("");
    assertEquals("", this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("01234567890123456789");
    assertEquals("01234567890123456789", this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("01234-56789");
    assertEquals("01234-56789", this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("012345678901234567-");
    assertEquals("012345678901234567-", this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("aaaabbcc-ddddeeff temp temp");
    assertEquals("aaaabbcc-ddddeeff temp temp", this.unit.getVgrTempInfo());
    this.unit.setVgrTempInfo("20090108-20090118 temp temp temp");
    assertEquals("20090108-20090118 temp temp temp", this.unit.getVgrTempInfo());
    assertEquals(format.parse("20090119"), this.unit.getVgrTempInfoEnd());
    assertEquals("temp temp temp", this.unit.getVgrTempInfoBody());
  }

  @Test
  public void startDateOfTemporaryInformationShouldBeSetToTheProvidedDate() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyyMMdd");
    this.unit.setVgrTempInfo("20090108-20100502 temp temp temp");
    assertEquals("20090108-20100502 temp temp temp", this.unit.getVgrTempInfo());
    assertEquals(format.parse("20090108"), this.unit.getVgrTempInfoStart());
    assertEquals("temp temp temp", this.unit.getVgrTempInfoBody());
  }
  
  @Test
  public void temporaryInformationHandlesHsaStandardFormat() throws Exception {
    DateFormat format = new SimpleDateFormat("yyyyMMdd");
    this.unit.setVgrTempInfo("20090108;20110302;temp temp temp");
    assertEquals("20090108;20110302;temp temp temp", this.unit.getVgrTempInfo());
    assertEquals(format.parse("20090108"), this.unit.getVgrTempInfoStart());
    assertEquals(format.parse("20110303"), this.unit.getVgrTempInfoEnd());
    assertEquals("temp temp temp", this.unit.getVgrTempInfoBody());
  }

  @Test
  public void testVgrRefInfo() {
    assertNull(this.unit.getVgrRefInfo());
    this.unit.setVgrRefInfo(TEST);
    assertEquals(TEST, this.unit.getVgrRefInfo());
    this.unit.setVgrRefInfo(TEST2);
    assertEquals(TEST2, this.unit.getVgrRefInfo());
  }

  @Test(expected = NullPointerException.class)
  public void getFormattedAncestorThrowsNullPointerExceptionIfNoHsaIdentityIsSet() {
    this.unit.getFormattedAncestor();
  }

  @Test
  public void formattedAncestorIsEmptyIfHsaIdentityDoesNotContainTheLetterF() {
    this.unit.setHsaIdentity("123-ABC");
    assertEquals("", this.unit.getFormattedAncestor());
  }

  @Test
  public void formattedAncestorIsEmptyIfUnitIsAHospital() {
    this.unit.setHsaIdentity("123-FGH");
    // Make sure that HealthcareTypeConditionHelper is reset to it's correct state.
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
        super.setImplResourcePath("testproperties.healthcaretypeconditionhelper.two_properties");
      }
    };

    HealthcareType healthcareType = healthcareTypeConditionHelper.getHealthcareTypeByName("Sjukhus");
    this.unit.addHealthcareType(healthcareType);
    assertEquals("", this.unit.getFormattedAncestor());
  }

  @Test
  public void formattedAncestorIsCorrectIfHsaIdentityContainsTheLetterFAndIsNotAHospital() {
    this.unit.setHsaIdentity("123-FGH");
    // Make sure that HealthcareTypeConditionHelper is reset to it's correct state.
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
        super.setImplResourcePath("testproperties.healthcaretypeconditionhelper.two_properties");
      }
    };

    HealthcareType healthcareType = healthcareTypeConditionHelper.getHealthcareTypeByName("Akutmottagning");
    this.unit.addHealthcareType(healthcareType);
    DN dn = DN.createDNFromString("ou=SubUnit,ou=AncestorUnit,o=VGR");
    this.unit.setDn(dn);
    assertEquals(", tillhör AncestorUnit", this.unit.getFormattedAncestor());
  }

  @Test
  public void testCreateTimestamp() {
    assertNull(this.unit.getCreateTimestamp());
    assertEquals("", this.unit.getCreateTimestampFormattedInW3CDatetimeFormat());

    this.unit.setCreateTimestamp(TimePoint.parseFrom("20090101120102", "yyyyMMddHHmmss", TimeZone.getDefault()));
    assertEquals("2009-01-01T12:01:02+01:00", this.unit.getCreateTimestampFormattedInW3CDatetimeFormat());
  }

  @Test
  public void testModifyTimestamp() {
    assertNull(this.unit.getModifyTimestamp());
    assertEquals("", this.unit.getModifyTimestampFormattedInW3CDatetimeFormat());

    this.unit.setModifyTimestamp(TimePoint.parseFrom("20090101120102", "yyyyMMddHHmmss", TimeZone.getDefault()));
    assertEquals("2009-01-01T12:01:02+01:00", this.unit.getModifyTimestampFormattedInW3CDatetimeFormat());
  }

  @Test
  public void isForPublicDisplayReturnFalseIfHsaDestinationIndicatorDoesNotContain03() {
    this.unit.addHsaDestinationIndicator("01");
    this.unit.addHsaDestinationIndicator("02");

    assertFalse("not for public display", this.unit.isForPublicDisplay());
  }

  @Test
  public void isForPublicDisplayReturnTrueIfHsaDestinationIndicatorContains03() {
    this.unit.addHsaDestinationIndicator("01");
    this.unit.addHsaDestinationIndicator("03");

    assertTrue("for public display", this.unit.isForPublicDisplay());
  }

  @Test
  public void mvkCaseTypesIsNeverNull() {
    assertEquals("mvkCaseTypes", Collections.emptyList(), this.unit.getMvkCaseTypes());
  }

  @Test
  public void addMvkCaseTypesAddsTheProvidedStringToMvkCaseTypes() {
    this.unit.addMvkCaseType(TEST);
    assertEquals("mvkCaseTypes", 1, this.unit.getMvkCaseTypes().size());
    assertEquals("mvk case type", TEST, this.unit.getMvkCaseTypes().get(0));
  }

  @Test
  public void getBusinessClassificationCodesReturnTheCodesConcatenated() {
    this.unit.setHsaBusinessClassificationCode(Arrays.asList("1012", "1401"));
    assertEquals("business classification codes", "1012, 1401", this.unit.getBusinessClassificationCodes());
  }
}
