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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.InvalidFormatException;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;

public class UnitTest {

  private Unit unit;
  private List<HealthcareType> healthcareTypes;

  @Before
  public void setUp() {
    unit = new Unit();
    healthcareTypes = new ArrayList<HealthcareType>();
    unit.setHealthcareTypes(healthcareTypes);
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesNoHealtcareType() {
    assertTrue("Age interval and visiting rules should be displayed for a unit without healtcare types BVC, VC and JC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesBVCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Barnavårdscentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type BVC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesVCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Vårdcentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type VC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesJCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Jourcentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type JC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowInVgrVardVal() {
    assertFalse("Unit not in VGR Vardval should not show info regarding vardval", unit.isShowInVgrVardVal());

    unit.setVgrVardVal(true);
    assertFalse("Unit should not show info regarding vardval if it isn't of type Vårdcentral", unit.isShowInVgrVardVal());

    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Vårdcentral");
    healthcareTypes.add(healthcareType);
    assertTrue("Unit should show info regarding vardval if it is of type Vårdcentral and is part of VGR Vardval", unit.isShowInVgrVardVal());
  }

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
}
