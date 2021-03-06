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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.NameMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

public class UnitMapperTest {
  private static final String TEST = "Test";
  private static final String VGR_OU = "vgrOrganizationalUnit";
  private static final String OU = "organizationalUnit";
  private static final String VGR_ORG_ROLE = "vgrOrganizationalRole";
  private static final String ORG_ROLE = "organizationalRole";
  private static final String CN = "ou=Unit,ou=Org,o=vgr";

  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String EXPECTED_HOURS = "Måndag-Fredag 08:30-10:00";
  private static final String EXPECTED_DATE_TIME = "2009-01-01 12:01:02";
  private static final String TEST_TIMESTAMP = "20090101120102";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private static final String TEST_ZULUTIME = "20090101120102Z";
  private SimpleDateFormat dateFormat;
  private DirContextOperationsMock dirContextOperationsMock;
  private UnitMapper unitMapper;

  @Before
  public void setUp() throws Exception {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    dirContextOperationsMock = new DirContextOperationsMock();
    dirContextOperationsMock.setDn(new NameMock("ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr"));

    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    CodeTablesServiceMock codeTablesServiceMock = new CodeTablesServiceMock();
    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    unitMapper = new UnitMapper(codeTablesServiceMock, displayValueTranslator);

    // Fill dirContextOperationsMoch with same values as ldapEntry
    dirContextOperationsMock.addAttributeValue("cn", CN);
    dirContextOperationsMock.addAttributeValue("objectClass", VGR_OU);
    dirContextOperationsMock.addAttributeValue("ou", TEST + "\\, " + TEST);
    dirContextOperationsMock.addAttributeValue("hsaIdentity", TEST);
    dirContextOperationsMock.addAttributeValue("organizationalUnitNameShort", TEST);
    dirContextOperationsMock.addAttributeValue("description", TEST);
    dirContextOperationsMock.addAttributeValue("vgrInternalDescription", TEST);
    dirContextOperationsMock.addAttributeValue("mail", TEST);
    dirContextOperationsMock.addAttributeValue("l", TEST);
    dirContextOperationsMock.addAttributeValue("labeledUri", TEST);
    dirContextOperationsMock.addAttributeValue("vgrInternalSedfInvoiceAddress", TEST);
    dirContextOperationsMock.addAttributeValue("vgrCareType", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAO3kod", TEST);
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTextPhoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTextPhoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("mobileTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSedfSwitchboardTelephoneNo", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSmsTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("facsimileTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("pagerTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPublicTelephoneNumber", TEST);
    dirContextOperationsMock.addAttributeValue("hsaTelephoneTime", TEST_TIME);
    dirContextOperationsMock.addAttributeValue("hsaEndDate", TEST_ZULUTIME);
    dirContextOperationsMock.addAttributeValue("hsaSurgeryHours", TEST_TIME);
    dirContextOperationsMock.addAttributeValue("hsaDropInHours", TEST_TIME);
    dirContextOperationsMock.addAttributeValue("hsaInternalAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaStreetAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPostalAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSedfDeliveryAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaSedfInvoiceAddress", TEST);
    dirContextOperationsMock.addAttributeValue("hsaUnitPrescriptionCode", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAnsvarsnummer", TEST);
    dirContextOperationsMock.addAttributeValue("hsaMunicipalityCode", TEST);
    dirContextOperationsMock.addAttributeValue("vgrEdiCode", TEST);
    dirContextOperationsMock.addAttributeValue("vgrEanCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaMunicipalitySectionName", TEST);
    dirContextOperationsMock.addAttributeValue("hsaMunicipalitySectionCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaCountyCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaCountyName", TEST);
    dirContextOperationsMock.addAttributeValue("hsaManagementCode", TEST);
    dirContextOperationsMock.addAttributeValue("hsaVisitingRules", TEST);
    dirContextOperationsMock.addAttributeValue("hsaVisitingRuleAge", TEST);
    dirContextOperationsMock.addAttributeValue("vgrTempInfo", TEST);
    dirContextOperationsMock.addAttributeValue("vgrRefInfo", TEST);
    dirContextOperationsMock.addAttributeValue("hsaAdministrationForm", TEST);
    dirContextOperationsMock.addAttributeValue("vgrModifyTimestamp", TEST_TIMESTAMP);
    dirContextOperationsMock.addAttributeValue("createTimeStamp", TEST_TIMESTAMP);
    dirContextOperationsMock.addAttributeValue("hsaGeographicalCoordinates", TEST);
    dirContextOperationsMock.addAttributeValue("hsaRoute", TEST);
    dirContextOperationsMock.addAttributeValue("vgrVardVal", TEST);
    dirContextOperationsMock.addAttributeValue("vgrAvtalskod", TEST);
    dirContextOperationsMock.addAttributeValue("vgrLabeledURI", "http://" + TEST);
    dirContextOperationsMock.addAttributeValue("hsaVisitingHours", TEST_TIME);
    dirContextOperationsMock.addAttributeValue("hsaVisitingRuleReferral", TEST);
    dirContextOperationsMock.addAttributeValue("hsaBusinessType", TEST);
    dirContextOperationsMock.addAttributeValue("hsaPatientVisitingRules", TEST);
  }

  @Test
  public void testMapFromContext() {
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1504");
    Unit mappedUnit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertUnit(mappedUnit);
  }

  @Test
  public void testVgrOrgUnit() throws KivException {
    dirContextOperationsMock.addAttributeValue("objectClass", VGR_OU);
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1504");
    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertUnit(unit);
  }

  @Test
  public void testOrgUnit() throws KivException {

    dirContextOperationsMock.addAttributeValue("objectClass", OU);
    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertTrue(unit.getIsUnit());
    assertTrue(unit.isShowVisitingRules());
    assertTrue(unit.isShowAgeInterval());
  }

  @Test
  public void testVgrOrgRole() throws KivException {
    dirContextOperationsMock.addAttributeValue("objectClass", VGR_ORG_ROLE);
    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertFalse(unit.getIsUnit());
    assertEquals(TEST + "\\, " + TEST, unit.getOu());
    assertEquals(CN, unit.getName());
  }

  @Test
  public void testOrgRole() throws KivException {
    dirContextOperationsMock.addAttributeValue("objectClass", ORG_ROLE);
    dirContextOperationsMock.addAttributeValue("hsaGeographicalCoordinates", "X: 1234567, Y: 1234567");
    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertFalse(unit.getIsUnit());
    assertEquals(1234567, unit.getRt90X());
    assertEquals(1234567, unit.getRt90Y());
    assertEquals(11.159754999084681, unit.getWgs84Lat(), 0.0);
    assertEquals(13.376313261575913, unit.getWgs84Long(), 0.0);
  }

  @Test
  public void testHttpsURI() throws KivException {
    dirContextOperationsMock.addAttributeValue("objectClass", OU);
    dirContextOperationsMock.addAttributeValue("vgrLabeledURI", "https://" + TEST);
    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertEquals("https://" + TEST, unit.getInternalWebsite());
  }

  @Test
  public void showVisitingRulesAndShowAgeIntervalReturnTrueAlways() {
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1519");

    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertTrue(unit.isShowVisitingRules());
    assertTrue(unit.isShowAgeInterval());
  }

  @Test
  public void isVgrVardvalReturnTrueForValueJ() {
    dirContextOperationsMock.addAttributeValue("vgrVardVal", "J");

    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    assertTrue(unit.isVgrVardVal());
  }

  @Test
  public void hsaDestinationIndicatorMapsAllValues() {
    dirContextOperationsMock.addAttributeValue("hsaDestinationIndicator", new String[] { "01", "03" });

    Unit unit = unitMapper.mapFromContext(dirContextOperationsMock);
    List<String> hsaDestinationIndicator = unit.getHsaDestinationIndicator();
    assertEquals("hsaDestinationIndicator", 2, hsaDestinationIndicator.size());
    assertTrue("01 is not mapped", hsaDestinationIndicator.contains("01"));
    assertTrue("03 is not mapped", hsaDestinationIndicator.contains("03"));
  }

  private void assertUnit(Unit unit) {
    assertEquals(VGR_OU, unit.getObjectClass());
    assertEquals(TEST + "\\, " + TEST, unit.getOu());
    assertEquals(TEST + ", " + TEST, unit.getName());
    assertEquals(TEST, unit.getHsaIdentity());
    assertEquals(TEST, unit.getOrganizationalUnitNameShort());
    assertEquals(EXPECTED_LIST_RESULT, unit.getDescription().toString());
    assertEquals(TEST, unit.getInternalConcatenatedDescription());
    assertEquals(TEST, unit.getMail());
    assertEquals(TEST, unit.getLocality());
    assertEquals("http://" + TEST, unit.getLabeledURI());
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    assertEquals(TEST, unit.getVgrCareType());
    assertEquals(TEST, unit.getVgrAO3kod());
    assertEquals("[1504]", unit.getHsaBusinessClassificationCode().toString());
    assertEquals(TEST, unit.getHsaTextPhoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaTextPhoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getMobileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaSedfSwitchboardTelephoneNo().getPhoneNumber());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaTelephoneNumber().toString());
    assertEquals(TEST, unit.getHsaSmsTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getFacsimileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getPagerTelephoneNumber().getPhoneNumber());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaTelephoneNumber().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPublicTelephoneNumber().toString());
    assertEquals(EXPECTED_HOURS, unit.getHsaTelephoneTime().get(0).getDisplayValue());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getHsaEndDate()));
    assertEquals(EXPECTED_HOURS, unit.getHsaSurgeryHours().get(0).getDisplayValue());
    assertEquals(EXPECTED_HOURS, unit.getHsaDropInHours().get(0).getDisplayValue());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaStreetAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(TEST, unit.getHsaUnitPrescriptionCode());
    assertEquals(EXPECTED_LIST_RESULT, unit.getVgrAnsvarsnummer().toString());
    assertEquals(TEST, unit.getHsaMunicipalityCode());
    assertEquals(TEST, unit.getVgrEDICode());
    assertEquals(TEST, unit.getVgrEANCode());
    assertEquals(TEST, unit.getHsaMunicipalitySectionName());
    assertEquals(TEST, unit.getHsaMunicipalitySectionCode());
    assertEquals(TEST, unit.getHsaCountyCode());
    assertEquals(TEST, unit.getHsaCountyName());
    assertEquals(TEST, unit.getHsaManagementCode());
    assertEquals(TEST, unit.getHsaVisitingRules());
    assertEquals(TEST + " år", unit.getHsaVisitingRuleAge());
    assertEquals(TEST, unit.getVgrTempInfo());
    assertEquals(TEST, unit.getVgrRefInfo());
    assertEquals(TEST, unit.getHsaAdministrationForm());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getModifyTimestamp().asJavaUtilDate()));
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getCreateTimestamp().asJavaUtilDate()));
    assertEquals(TEST, unit.getHsaGeographicalCoordinates());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaRoute().toString());
    assertFalse(unit.isVgrVardVal());
    assertTrue(unit.getIsUnit());
    assertTrue("Expected true but was: " + unit.isShowVisitingRules(), unit.isShowVisitingRules());
    assertTrue(unit.isShowAgeInterval());
    assertEquals(TEST, unit.getContractCode());
    assertEquals("http://" + TEST, unit.getInternalWebsite());
    assertEquals(EXPECTED_HOURS, unit.getVisitingHours().get(0).getDisplayValue());
    assertEquals(TEST, unit.getVisitingRuleReferral());
    assertEquals("hsaBusinessType", TEST, unit.getHsaBusinessType());
    assertEquals("hsaPatientVisitingRules", TEST, unit.getHsaPatientVisitingRules());
  }

  class CodeTablesServiceMock implements CodeTablesService {
    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return "Translated " + string;
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }
}
