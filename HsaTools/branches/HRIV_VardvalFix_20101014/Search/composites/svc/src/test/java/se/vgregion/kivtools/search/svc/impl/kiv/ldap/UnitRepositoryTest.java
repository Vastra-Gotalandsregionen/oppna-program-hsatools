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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class UnitRepositoryTest {
  private UnitRepository unitRepository;
  private UnitMapper unitMapper;
  private LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();

  @Before
  public void setUp() throws Exception {
    setupTimeSource();
    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");

    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());

    CodeTablesServiceImpl codeTablesService = new CodeTablesServiceImpl(ldapTemplateMock);

    unitMapper = new UnitMapper(codeTablesService, displayValueTranslator);
    unitRepository = new UnitRepository();
    unitRepository.setLdapTemplate(ldapTemplateMock);
    unitRepository.setUnitMapper(unitMapper);
  }

  @After
  public void tearDown() {
    new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
      }
    };
  }

  private void setupTimeSource() {
    TimeUtil.setTimeSource(new TimeSource() {
      private long millis;

      {
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        millis = cal.getTimeInMillis();
      }

      @Override
      public long millis() {
        return millis;
      }
    });
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    unitRepository = new UnitRepository();
    unitRepository.setUnitMapper(unitMapper);
    unitRepository.setLdapTemplate(ldapTemplateMock);
    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))))";

    unitRepository.searchUnits(searchUnitCriterions, 0);

    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testSearchUnitExactMatch() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("\"unitName\"");
    searchUnitCriterions.setLocation("\"municipalityName\"");

    unitRepository = new UnitRepository();
    unitRepository.setUnitMapper(unitMapper);
    unitRepository.setLdapTemplate(ldapTemplateMock);
    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(ou=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName)))))(&(objectclass=vgrOrganizationalRole)(&(cn=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))))";

    unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testSearchUnitOtherParams() throws KivException {
    // Create test unit.

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setAdministrationName("01");
    searchUnitCriterions.setLiableCode("1");
    searchUnitCriterions.setBusinessClassificationName("1505");
    searchUnitCriterions.setCareTypeName("01");

    // Create ldapConnectionMock.
    CodeTableMock codeTableMock = new CodeTableMock();
    codeTableMock.values.put(CodeTableName.VGR_AO3_CODE, "01");
    codeTableMock.values.put(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1505");
    codeTableMock.values.put(CodeTableName.VGR_CARE_TYPE, "01");
    unitRepository = new UnitRepository();
    unitRepository.setUnitMapper(unitMapper);
    unitRepository.setLdapTemplate(ldapTemplateMock);
    unitRepository.setCodeTablesService(codeTableMock);
    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01)))(&(objectclass=vgrOrganizationalRole)(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))))";

    unitRepository.searchUnits(searchUnitCriterions, 0);

    ldapTemplateMock.assertSearchFilter(expectedFilter);

    codeTableMock.values.remove(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);

    searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setBusinessClassificationName("123");

    expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(hsaBusinessClassificationCode=NO_VALID_CODE_TABLE_CODE_FOUND))(&(objectclass=vgrOrganizationalRole)(hsaBusinessClassificationCode=NO_VALID_CODE_TABLE_CODE_FOUND)))";

    unitRepository.searchUnits(searchUnitCriterions, 0);

    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testHsaEndDate() throws Exception {
    final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();

    Unit resultUnit1 = new Unit();
    resultUnit1.setHsaIdentity("abc-123");
    resultUnit1.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit1.setVgrAnsvarsnummer(Arrays.asList("11223"));

    Unit resultUnit2 = new Unit();
    resultUnit2.setHsaIdentity("abc-456");
    resultUnit2.setHsaBusinessClassificationCode(Arrays.asList("1504"));
    resultUnit2.setHsaEndDate(TimeUtil.parseStringToZuluTime("20091231235959Z"));

    Unit resultUnit3 = new Unit();
    resultUnit3.setHsaIdentity("SE6460000000-E000000000222");
    resultUnit3.setHsaBusinessClassificationCode(Arrays.asList("abc"));

    Unit resultUnit4 = new Unit();
    resultUnit4.setHsaIdentity("abc-789");
    resultUnit4.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit4.setVgrAnsvarsnummer(Arrays.asList("12345"));

    result.add(resultUnit1);
    result.add(resultUnit2);
    result.add(resultUnit3);
    result.add(resultUnit4);

    unitRepository = new UnitRepository() {
      @Override
      protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
        return result;
      }
    };

    // No hsaEndDate set, ie unit should be returned.
    SikSearchResultList<Unit> resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, true);
    assertNotNull("Result should not be null!", resultList);
    assertEquals(1, resultList.size());

    // hsaEndDate set to a "past date", ie unit should NOT be returned.
    resultUnit2.setHsaEndDate(TimeUtil.parseStringToZuluTime("20081231235959Z"));
    resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, true);
    assertEquals(1, resultList.size());
    assertNotSame(resultUnit2, resultList.get(0));
  }

  @Test
  public void testSearchAdvancedUnits() throws Exception {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.setHealthcareTypes(Arrays.asList(healthcareType));
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    UnitRepository unitRepository = new UnitRepository();
    unitRepository.setUnitMapper(unitMapper);
    unitRepository.setLdapTemplate(ldapTemplateMock);
    unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, false);

    String expectedFilter = "(&(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*unitName*)(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*unitName*)(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2)))))))";
    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testSearchUnits() throws Exception {
    Unit unitToSearchFor = new Unit();
    unitToSearchFor.setHsaIdentity("SE2321000131-E000000000110");
    unitToSearchFor.setName("unitToSearchFor");

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("resultUnit");
    searchUnitCriterions.setUnitId("SE2321000131-E000000000110");
    searchUnitCriterions.setBusinessClassificationName("3");

    CodeTableMock codeTableMock = new CodeTableMock();
    codeTableMock.values.put(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "3");
    unitRepository.setCodeTablesService(codeTableMock);
    unitRepository.setLdapTemplate(ldapTemplateMock);

    unitRepository.searchUnits(searchUnitCriterions, 10);

    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(hsaIdentity=*SE2321000131*E000000000110*)(ou=*resultUnit*)(hsaBusinessClassificationCode=3)))(&(objectclass=vgrOrganizationalRole)(&(hsaIdentity=*SE2321000131*E000000000110*)(cn=*resultUnit*)(hsaBusinessClassificationCode=3))))";
    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testBuildAddressSearch() {
    String correctResult = "(|(hsaPostalAddress=*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)(hsaPostalAddress=*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)"
        + "(hsaPostalAddress=*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))";
    Filter temp = unitRepository.buildAddressSearch("hsaPostalAddress", "*uddevalla*");
    assertEquals(correctResult, temp.encode());
  }

  @Test
  public void testBuildAddressSearchExactMatch() {
    String correctResult = "(|(hsaPostalAddress=uddevalla$*$*$*$*$*)(hsaPostalAddress=*$uddevalla$*$*$*$*)(hsaPostalAddress=*$*$uddevalla$*$*$*)(hsaPostalAddress=*$*$*$uddevalla$*$*)"
        + "(hsaPostalAddress=*$*$*$*$uddevalla$*)(hsaPostalAddress=*$*$*$*$*$uddevalla))";
    Filter temp = unitRepository.buildAddressSearch("hsaPostalAddress", "uddevalla");
    assertEquals(correctResult, temp.encode());
  }

  @Test
  public void testcreateSearchFilter() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdoms*)");
    correctResult.append("(|(hsaMunicipalityName=*Borås*)");
    correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
    correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*)))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdoms*)");
    correctResult.append("(|(hsaMunicipalityName=*Borås*)");
    correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
    correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*))))))");

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("barn- och ungdoms");
    searchUnitCriterions.setLocation("Borås");
    String temp = unitRepository.createSearchFilter(searchUnitCriterions);
    assertEquals(correctResult.toString(), temp);
  }

  /**
   * Combined test
   */
  @Test
  public void testcreateAdvancedSearchFilter1() {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*))))))");
    Unit unit = new Unit();
    unit.setName("barn- och ungdomsvård");
    unit.setHsaMunicipalityCode("1490");
    String temp = unitRepository.createAdvancedSearchFilter(unit, false);
    assertEquals(correctResult.toString(), temp);
  }

  /**
   * Only hsamuncipalitycode
   */
  @Test
  public void testcreateAdvancedSearchFilter2() {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(|(&(objectclass=vgrOrganizationalUnit)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540)))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540)))))))");
    Unit unit = new Unit();
    unit.setHsaMunicipalityCode("1490");
    List<HealthcareType> healthcareTypeList = new ArrayList<HealthcareType>();
    HealthcareType ht = new HealthcareType();
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put("hsaBusinessClassificationCode", "1540");
    ht.setConditions(conditions);
    healthcareTypeList.add(ht);
    unit.setHealthcareTypes(healthcareTypeList);
    String temp = unitRepository.createAdvancedSearchFilter(unit, false);
    assertEquals(correctResult.toString(), temp);
  }

  @Test
  public void testcreateAdvancedSearchFilter3() {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*ambulans*)))(&(objectclass=vgrOrganizationalRole)(&(cn=*ambulans*)))))");
    Unit unit = new Unit();
    unit.setName("ambulans");
    String temp = unitRepository.createAdvancedSearchFilter(unit, false);
    assertEquals(correctResult.toString(), temp);
  }

  // Test fetching sub units for a chosen unit
  @Test
  public void testGetSubUnits() throws Exception {
    String base = "ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr";
    String filter = "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";
    Unit parentUnit = new Unit();
    parentUnit.setHsaIdentity("parent");
    parentUnit.setDn(DN.createDNFromString(base));
    unitRepository.getSubUnits(parentUnit, 3);

    assertEquals(base, ldapTemplateMock.getBase());
    ldapTemplateMock.assertSearchFilter(filter);
  }

  @Test
  public void testGetUnitByHsaId() throws KivException {
    ldapTemplateMock.addDirContextOperationForSearch(new DirContextOperationsMock());
    String expectedFilter = "(hsaIdentity=abc-123)";
    unitRepository.getUnitByHsaId("abc-123");
    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testGetUnitByDN() throws KivException {
    String dn = "ou=Vårdcentralen Angered";// ,ou=Org,o=VGR";
    ldapTemplateMock.addBoundDN(new DistinguishedName("ou=Vårdcentralen Angered"), new DirContextOperationsMock());
    unitRepository.getUnitByDN(DN.createDNFromString(dn));
    assertEquals(DN.createDNFromString(dn).toString(), ldapTemplateMock.getDn());
  }

  @Ignore
  @Test
  public void testGetUnitByDNExceptionHandling() {
    String dn = "ou=Vårdcentralen Angered,ou=Org,o=VGR";
    try {
      unitRepository.getUnitByDN(DN.createDNFromString(dn));
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    String expectedFilter = "(&(|(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")(objectclass=" + Constants.OBJECT_CLASS_FUNCTION_SPECIFIC + ")))";

    unitRepository.getAllUnitsHsaIdentity();
    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testGetAllUnitsOnlyPublicUnits() throws KivException {
    String expectedFilter = "(&(hsaDestinationIndicator=03)(|(objectclass=vgrOrganizationalUnit)(objectclass=vgrOrganizationalRole)))";

    List<String> hsaIdentities = unitRepository.getAllUnitsHsaIdentity(true);
    assertNotNull(hsaIdentities);

    ldapTemplateMock.assertSearchFilter(expectedFilter);
  }

  @Test
  public void testRemoveUnallowedUnits() throws KivException {
    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "abc-123");
    entry1.addAttributeValue("hsaBusinessClassificationCode", "1504");
    entry1.addAttributeValue("vgrAnsvarsnummer", "11223");
    this.ldapTemplateMock.addDirContextOperationForSearch(entry1);

    DirContextOperationsMock entry2 = new DirContextOperationsMock();
    entry2.addAttributeValue("hsaIdentity", "abc-456");
    entry2.addAttributeValue("hsaBusinessClassificationCode", "1500");
    this.ldapTemplateMock.addDirContextOperationForSearch(entry2);

    DirContextOperationsMock entry3 = new DirContextOperationsMock();
    entry3.addAttributeValue("hsaIdentity", "SE6460000000-E000000000222");
    entry3.addAttributeValue("hsaBusinessClassificationCode", "abc");
    this.ldapTemplateMock.addDirContextOperationForSearch(entry3);

    DirContextOperationsMock entry4 = new DirContextOperationsMock();
    entry4.addAttributeValue("hsaIdentity", "abc-789");
    entry4.addAttributeValue("hsaBusinessClassificationCode", "1");
    entry4.addAttributeValue("vgrAnsvarsnummer", "12345");
    this.ldapTemplateMock.addDirContextOperationForSearch(entry4);

    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.setHealthcareTypes(Arrays.asList(healthcareType));
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, true);
    assertNotNull(units);
    assertEquals(3, units.size());
    assertEquals(3, units.getTotalNumberOfFoundItems());
  }

  private class CodeTableMock implements CodeTablesService {

    private Map<CodeTableName, String> values = new HashMap<CodeTableName, String>();

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      List<String> codes = new ArrayList<String>();
      if (values.containsKey(codeTableName)) {
        codes = Arrays.asList(values.get(codeTableName));
      }
      return codes;
    }

    @Override
    public String getValueFromCode(CodeTableName codeTableName, String code) {
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
