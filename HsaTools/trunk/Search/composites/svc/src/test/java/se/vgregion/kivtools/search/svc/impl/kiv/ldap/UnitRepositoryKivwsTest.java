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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class UnitRepositoryKivwsTest {
  private UnitRepositoryKivws unitRepository;
  private LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
  private SpringLdapSearchService springLdapSearchService;
  private SearchServiceMock searchServiceMock;

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
    unitRepository = new UnitRepositoryKivws();
    searchServiceMock = new SearchServiceMock();
    unitRepository.setSearchService(searchServiceMock);
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
    // Test search unit filter with ou.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");
    String expectedFilterOU = "(&(ou=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";
    String expectedFilterCN = "(&(cn=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";

    unitRepository.searchUnits(searchUnitCriterions, 0);
    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);
  }

  @Test
  public void testSearchUnitExactMatch() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("\"unitName\"");
    searchUnitCriterions.setLocation("\"municipalityName\"");

    String expectedFilterOU = "(&(ou=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";
    String expectedFilterCN = "(&(cn=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";

    unitRepository.searchUnits(searchUnitCriterions, 0);
    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);
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
    codeTableMock.values.put(KivwsCodeTableName.VGR_AO3_CODE, "01");
    codeTableMock.values.put(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "1505");
    codeTableMock.values.put(KivwsCodeTableName.CARE_TYPE, "01");

    unitRepository.setCodeTablesService(codeTableMock);
    String expectedFilterOU = "(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))";
    String expectedFilterCN = "(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))";

    unitRepository.searchUnits(searchUnitCriterions, 0);

    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);

    codeTableMock.values.remove(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);

    searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setBusinessClassificationName("123");

    expectedFilterOU = "(hsaBusinessClassificationCode=1505)";
    expectedFilterCN = "(hsaBusinessClassificationCode=1505)";

    unitRepository.searchUnits(searchUnitCriterions, 0);

    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);
  }

  @Test
  public void testHsaEndDate() throws Exception {
    final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();

    Unit resultUnit1 = new Unit();
    resultUnit1.setHsaIdentity("abc-123");
    resultUnit1.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit1.setVgrAnsvarsnummer(Arrays.asList("11223"));
    resultUnit1.setName("d");

    Unit resultUnit2 = new Unit();
    resultUnit2.setHsaIdentity("abc-456");
    resultUnit2.setHsaBusinessClassificationCode(Arrays.asList("1504"));
    resultUnit2.setHsaEndDate(TimeUtil.parseStringToZuluTime("20091231235959Z"));
    resultUnit2.setName("f");

    Unit resultUnit3 = new Unit();
    resultUnit3.setHsaIdentity("SE6460000000-E000000000222");
    resultUnit3.setHsaBusinessClassificationCode(Arrays.asList("abc"));
    resultUnit3.setName("a");

    Unit resultUnit4 = new Unit();
    resultUnit4.setHsaIdentity("abc-789");
    resultUnit4.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit4.setVgrAnsvarsnummer(Arrays.asList("12345"));
    resultUnit4.setName("b");

    Unit resultUnit5 = new Unit();
    resultUnit5.setHsaIdentity("abc-7899");
    resultUnit5.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit5.setVgrAnsvarsnummer(Arrays.asList("12345"));
    resultUnit5.setName("c");

    result.add(resultUnit1);
    result.add(resultUnit2);
    result.add(resultUnit3);
    result.add(resultUnit4);

    SearchService searchService = new SearchService() {

      @Override
      public List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
        return result;
      }

      @Override
      public List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs) {
        // TODO Auto-generated method stub
        return new ArrayList<Unit>();
      }

      @Override
      public Unit lookupUnit(Name name, List<String> attrs) {
        // TODO Auto-generated method stub
        return null;
      }
    };

    unitRepository.setSearchService(searchService);
    // No hsaEndDate set, ie unit should be returned.
    SikSearchResultList<Unit> resultList = unitRepository.searchAdvancedUnits(new Unit(), 4, null, true);
    assertNotNull("Result should not be null!", resultList);
    assertEquals(2, resultList.size());

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
    searchUnit.addHealthcareType(healthcareType);
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    unitRepository.searchAdvancedUnits(searchUnit, maxResults, new UnitNameComparator(), false);

    String correctExpectedValueOU = "(&(|(ou=*unitName*)(hsaBusinessClassificationCode=*unitName*))(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2))))";
    String correctExpectedValueCN = "(&(|(cn=*unitName*)(hsaBusinessClassificationCode=*unitName*))(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2))))";

    assertEquals(correctExpectedValueOU, searchServiceMock.filterOU);
    assertEquals(correctExpectedValueCN, searchServiceMock.filterCN);
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
    codeTableMock.values.put(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "3");
    unitRepository.setCodeTablesService(codeTableMock);

    unitRepository.searchUnits(searchUnitCriterions, 10);

    String expectedFilterOU = "(&(hsaIdentity=*SE2321000131*E000000000110*)(ou=*resultUnit*)(hsaBusinessClassificationCode=3))";
    String expectedFilterCN = "(&(hsaIdentity=*SE2321000131*E000000000110*)(cn=*resultUnit*)(hsaBusinessClassificationCode=3))";

    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);

    assertEquals(expectedFilterOU, searchServiceMock.filterOU);
    assertEquals(expectedFilterCN, searchServiceMock.filterCN);
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

    String expectedOU = "(&(ou=*barn**och*ungdoms*)(|(hsaMunicipalityName=*Borås*)(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";
    String expectedCN = "(&(cn=*barn**och*ungdoms*)(|(hsaMunicipalityName=*Borås*)(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("barn- och ungdoms");
    searchUnitCriterions.setLocation("Borås");
    String createSearchFilter = unitRepository.createSearchFilter(searchUnitCriterions, false);
    String createSearchFilterCN = unitRepository.createSearchFilter(searchUnitCriterions, true);
    assertEquals(expectedOU, createSearchFilter);
    assertEquals(expectedCN, createSearchFilterCN);
  }

  /**
   * Combined test
   */
  @Test
  public void testcreateAdvancedSearchFilter1() {
    String expectedOU = "(&(|(ou=*barn**och*ungdomsvård*)(hsaBusinessClassificationCode=*barn**och*ungdomsvård*))(|(hsaMunicipalityCode=*1490*)))";
    String expectedCN = "(&(|(cn=*barn**och*ungdomsvård*)(hsaBusinessClassificationCode=*barn**och*ungdomsvård*))(|(hsaMunicipalityCode=*1490*)))";

    Unit unit = new Unit();
    unit.setName("barn- och ungdomsvård");
    unit.setHsaMunicipalityCode("1490");
    String resultOU = unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = unitRepository.createAdvancedSearchFilter(unit, false, true);
    assertEquals(expectedOU, resultOU);
    assertEquals(expectedCN, resultCN);
  }

  /**
   * Only hsamuncipalitycode
   */
  @Test
  public void testcreateAdvancedSearchFilter2() {
    String expectedOU = "(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540))))";
    String expectedCN = "(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540))))";

    Unit unit = new Unit();
    unit.setHsaMunicipalityCode("1490");
    HealthcareType ht = new HealthcareType();
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put("hsaBusinessClassificationCode", "1540");
    ht.setConditions(conditions);
    unit.addHealthcareType(ht);
    String resultOU = unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = unitRepository.createAdvancedSearchFilter(unit, false, true);
    assertEquals(expectedOU, resultOU);
    assertEquals(expectedCN, resultCN);
  }

  @Test
  public void testcreateAdvancedSearchFilter3() {
    // TODO: Should not contain the first and condition.
    String expectedOU = "(&(|(ou=*ambulans*)(hsaBusinessClassificationCode=*ambulans*)))";
    String expectedCN = "(&(|(cn=*ambulans*)(hsaBusinessClassificationCode=*ambulans*)))";

    Unit unit = new Unit();
    unit.setName("ambulans");
    String resultOU = unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = unitRepository.createAdvancedSearchFilter(unit, false, true);
    assertEquals(expectedOU, resultOU);
    assertEquals(expectedCN, resultCN);
  }

  // Test fetching sub units for a chosen unit
  @Test
  public void testGetSubUnits() throws Exception {
    String base = "ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr";
    String filterOU = "(ou=*)";
    String filterCN = "(cn=*)";
    Unit parentUnit = new Unit();
    parentUnit.setHsaIdentity("parent");
    parentUnit.setDn(DN.createDNFromString(base));
    unitRepository.getSubUnits(parentUnit, 3);

    assertEquals(base, searchServiceMock.baseCN.toString());
    assertEquals(base, searchServiceMock.baseOU.toString());
    assertEquals(filterOU, searchServiceMock.filterOU);
    assertEquals(filterCN, searchServiceMock.filterCN);
  }

  @Test
  public void testGetUnitByHsaId() throws KivException {
    String expectedFilter = "(hsaIdentity=abc-123)";
    unitRepository.getUnitByHsaId("abc-123");
    assertEquals(expectedFilter, searchServiceMock.filterOU);
    assertEquals(expectedFilter, searchServiceMock.filterCN);
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    String expectedOU = "(&(|(vgrCareType=01)(vgrCareType=03)))";
    String expectedCN = "(&(|(vgrCareType=01)(vgrCareType=03)))";
    unitRepository.getAllUnitsHsaIdentity();
    assertEquals(expectedOU, searchServiceMock.filterOU);
    assertEquals(expectedCN, searchServiceMock.filterCN);
  }

  @Test
  public void testGetAllUnitsOnlyPublicUnits() throws KivException {
    String expectedOU = "(&(hsaDestinationIndicator=03)(|(vgrCareType=01)(vgrCareType=03)))";
    String expectedCN = "(&(hsaDestinationIndicator=03)(|(vgrCareType=01)(vgrCareType=03)))";

    unitRepository.getAllUnitsHsaIdentity(true);
    assertEquals(expectedOU, searchServiceMock.filterOU);
    assertEquals(expectedCN, searchServiceMock.filterCN);
  }

  @Test
  public void testRemoveUnallowedUnits() throws KivException {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    Unit unit1 = new Unit();
    unit1.setHsaIdentity("abc-123");
    unit1.setHsaBusinessClassificationCode(Arrays.asList("1504"));
    unit1.setVgrAnsvarsnummer(Arrays.asList("112233"));

    Unit unit2 = new Unit();
    unit2.setHsaIdentity("abc-456");
    unit2.setHsaBusinessClassificationCode(Arrays.asList("1500"));

    Unit unit3 = new Unit();
    unit3.setHsaIdentity("SE6460000000-E000000000222");
    unit3.setHsaBusinessClassificationCode(Arrays.asList("abc"));

    Unit unit4 = new Unit();
    unit4.setHsaIdentity("abc-789");
    unit4.setHsaBusinessClassificationCode(Arrays.asList("1"));
    unit4.setVgrAnsvarsnummer(Arrays.asList("12345"));

    searchServiceMock.resultList.add(unit1);
    searchServiceMock.resultList.add(unit2);
    searchServiceMock.resultList.add(unit3);
    searchServiceMock.resultList.add(unit4);

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.addHealthcareType(healthcareType);
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, true);
    assertNotNull(units);
    assertEquals(3, units.size());
    assertEquals(3, units.getTotalNumberOfFoundItems());
  }

  // TODO: Cannot put two different care types 01 and 03 to the list. Correct query should be (&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=03)))
  @Test
  public void testGetUnitByHsaIdHasNotCareTypeInpatient() throws KivException {
    String expectedOU = "(&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=01)))";
    String expectedCN = "(&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=01)))";

    CodeTableMock codeTableMock = new CodeTableMock();
    codeTableMock.values.put(KivwsCodeTableName.CARE_TYPE, "01");
    unitRepository.setCodeTablesService(codeTableMock);
    unitRepository.getUnitByHsaIdAndHasNotCareTypeInpatient("abc-123");

    assertEquals(expectedOU, searchServiceMock.filterOU);
    assertEquals(expectedCN, searchServiceMock.filterCN);
  }

  private class CodeTableMock implements CodeTablesService {

    private Map<KivwsCodeTableName, String> values = new HashMap<KivwsCodeTableName, String>();

    @Override
    public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      List<String> codes = new ArrayList<String>();
      if (values.containsKey(codeTableName)) {
        codes = Arrays.asList(values.get(codeTableName));
      }
      return codes;
    }

    @Override
    public String getValueFromCode(CodeTableNameInterface codeTableName, String code) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }

  class SearchServiceMock implements SearchService {

    private String filterOU;
    private String filterCN;
    private Name baseOU;
    private Name baseCN;
    private List<Unit> resultList = new ArrayList<Unit>();

    {
      Unit unit = new Unit();
      unit.setHsaIdentity("test");
      resultList.add(unit);
    }

    @Override
    public Unit lookupUnit(Name name, List<String> attrs) {
      return null;
    }

    @Override
    public List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute) {
      this.filterOU = filter;
      this.filterCN = filter;
      return new ArrayList<String>();
    }

    @Override
    public List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs) {
      this.baseOU = base;
      this.filterOU = filter;
      return resultList;
    }

    @Override
    public List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs) {
      this.baseCN = base;
      this.filterCN = filter;
      return resultList;
    }

  }
}
