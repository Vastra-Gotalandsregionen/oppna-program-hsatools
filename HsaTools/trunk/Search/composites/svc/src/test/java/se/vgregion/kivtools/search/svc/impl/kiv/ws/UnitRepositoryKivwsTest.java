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

package se.vgregion.kivtools.search.svc.impl.kiv.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.mock.CodeTableServiceMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfAnyType;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeletedObject;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeliveryPoint;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfPerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfResource;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfServer;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfTransaction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnsurePerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Person;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class UnitRepositoryKivwsTest {
  private UnitRepositoryKivws unitRepository;
  private VGRegionWebServiceMock portType;
  private final CodeTableServiceMock codeTablesService = new CodeTableServiceMock();
  private final DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
  private final KivwsUnitMapper mapper = new KivwsUnitMapper(this.codeTablesService, this.displayValueTranslator);
  private final ObjectFactory objectFactory = new ObjectFactory();
  private final LogFactoryMock logFactory = LogFactoryMock.createInstance();

  @Before
  public void setUp() throws Exception {
    this.setupTimeSource();
    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        super.resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");

    this.displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    this.codeTablesService.addListToMap(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, Arrays.asList("1501"));
    this.portType = new VGRegionWebServiceMock();
    this.unitRepository = new UnitRepositoryKivws(this.portType, this.mapper, this.codeTablesService);
  }

  @After
  public void tearDown() {
    LogFactoryMock.resetInstance();

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
        this.millis = cal.getTimeInMillis();
      }

      @Override
      public long millis() {
        return this.millis;
      }
    });
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Test search unit filter with ou.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");
    //String expectedFilterOU = "(&(ou=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";
    //String expectedFilterCN = "(&(cn=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";
    String expectedFilterOU = "(&(ou=*unitName*)(|(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";
    String expectedFilterCN = "(&(cn=*unitName*)(|(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))";

    this.unitRepository.searchUnits(searchUnitCriterions, 0);
    assertEquals(expectedFilterOU, this.portType.filterOU);
    assertEquals(expectedFilterCN, this.portType.filterCN);
  }

  @Test
  public void testSearchUnitExactMatch() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("\"unitName\"");
    searchUnitCriterions.setLocation("\"municipalityName\"");

    //String expectedFilterOU = "(&(ou=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";
    //String expectedFilterCN = "(&(cn=unitName)(|(hsaMunicipalityName=municipalityName)(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";
    String expectedFilterOU = "(&(ou=unitName)(|(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";
    String expectedFilterCN = "(&(cn=unitName)(|(|(hsaPostalAddress=municipalityName$*$*$*$*$*)(hsaPostalAddress=*$municipalityName$*$*$*$*)(hsaPostalAddress=*$*$municipalityName$*$*$*)(hsaPostalAddress=*$*$*$municipalityName$*$*)(hsaPostalAddress=*$*$*$*$municipalityName$*)(hsaPostalAddress=*$*$*$*$*$municipalityName))(|(hsaStreetAddress=municipalityName$*$*$*$*$*)(hsaStreetAddress=*$municipalityName$*$*$*$*)(hsaStreetAddress=*$*$municipalityName$*$*$*)(hsaStreetAddress=*$*$*$municipalityName$*$*)(hsaStreetAddress=*$*$*$*$municipalityName$*)(hsaStreetAddress=*$*$*$*$*$municipalityName))))";

    this.unitRepository.searchUnits(searchUnitCriterions, 0);
    assertEquals(expectedFilterOU, this.portType.filterOU);
    assertEquals(expectedFilterCN, this.portType.filterCN);
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
    this.codeTablesService.addListToMap(KivwsCodeTableName.VGR_AO3_CODE, Arrays.asList("01"));
    this.codeTablesService.addListToMap(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, Arrays.asList("1505"));
    this.codeTablesService.addListToMap(KivwsCodeTableName.CARE_TYPE, Arrays.asList("01"));

    String expectedFilterOU = "(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))";
    String expectedFilterCN = "(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))";

    this.unitRepository.searchUnits(searchUnitCriterions, 0);

    assertEquals(expectedFilterOU, this.portType.filterOU);
    assertEquals(expectedFilterCN, this.portType.filterCN);

    this.codeTablesService.addListToMap(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, new ArrayList<String>());

    searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setBusinessClassificationName("123");

    expectedFilterOU = "(hsaBusinessClassificationCode=1505)";
    expectedFilterCN = "(hsaBusinessClassificationCode=1505)";

    this.unitRepository.searchUnits(searchUnitCriterions, 0);

    assertEquals(expectedFilterOU, this.portType.filterOU);
    assertEquals(expectedFilterCN, this.portType.filterCN);
  }

  @Test
  public void testHsaEndDate() throws Exception {
    this.portType.addUnit(this.createUnit("abc-123", "d", "1", "11223", ""));
    this.portType.addUnit(this.createUnit("abc-456", "f", "1504", "", "20991231235959Z"));
    this.portType.addUnit(this.createUnit("SE6460000000-E000000000222", "a", "abc", "", ""));
    this.portType.addUnit(this.createUnit("abc-789", "b", "1", "12345", ""));
    this.portType.addUnit(this.createUnit("abc-7899", "c", "1", "12345", "20081231235959Z"));

    // No hsaEndDate set, ie unit should be returned.
    SikSearchResultList<Unit> resultList = this.unitRepository.searchAdvancedUnits(new Unit(), 4, null, true);
    assertNotNull("Result should not be null!", resultList);
    assertEquals(2, resultList.size());

    // // hsaEndDate set to a "past date", ie unit should NOT be returned.
    // resultUnit2.setHsaEndDate(TimeUtil.parseStringToZuluTime("20081231235959Z"));
    // resultList = this.unitRepository.searchAdvancedUnits(new Unit(), 1, null, true);
    // assertEquals(1, resultList.size());
    // assertNotSame(resultUnit2, resultList.get(0));
  }

  private se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit createUnit(String hsaIdentity, String name, String businessClassification, String ansvarsNr, String hsaEndDate) {
    se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit = new se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit();
    String2ArrayOfAnyTypeMap createString2ArrayOfAnyTypeMap = new String2ArrayOfAnyTypeMap();
    createString2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("hsaidentity", hsaIdentity));
    createString2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("hsaenddate", hsaEndDate));
    createString2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("vgransvarsnummer", ansvarsNr));
    createString2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("hsabusinessclassificationcode", businessClassification));
    unit.setAttributes(this.objectFactory.createServerAttributes(createString2ArrayOfAnyTypeMap));
    unit.setDn(this.objectFactory.createUnitDn("ou=" + name));

    return unit;
  }

  private Entry createEntry(String key, Object value) {
    Entry entry = new String2ArrayOfAnyTypeMap.Entry();
    entry.setKey(key);
    ArrayOfAnyType arrayOfAnyType = new ArrayOfAnyType();
    arrayOfAnyType.getAnyType().add(value);
    entry.setValue(arrayOfAnyType);
    return entry;
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
    this.unitRepository.searchAdvancedUnits(searchUnit, maxResults, new UnitNameComparator(), false);

    String correctExpectedValueOU = "(&(|(ou=*unitName*)(hsaBusinessClassificationCode=*1501*))(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2))))";
    String correctExpectedValueCN = "(&(|(cn=*unitName*)(hsaBusinessClassificationCode=*1501*))(|(hsaMunicipalityName=*Göteborg*)(hsaMunicipalityCode=*10032*)(|(hsaPostalAddress=*Göteborg*$*$*$*$*$*)(hsaPostalAddress=*$*Göteborg*$*$*$*$*)(hsaPostalAddress=*$*$*Göteborg*$*$*$*)(hsaPostalAddress=*$*$*$*Göteborg*$*$*)(hsaPostalAddress=*$*$*$*$*Göteborg*$*)(hsaPostalAddress=*$*$*$*$*$*Göteborg*))(|(hsaStreetAddress=*Göteborg*$*$*$*$*$*)(hsaStreetAddress=*$*Göteborg*$*$*$*$*)(hsaStreetAddress=*$*$*Göteborg*$*$*$*)(hsaStreetAddress=*$*$*$*Göteborg*$*$*)(hsaStreetAddress=*$*$*$*$*Göteborg*$*)(hsaStreetAddress=*$*$*$*$*$*Göteborg*)))(hsaIdentity=*hsaId*1*)(&(|(conditionKey=value1)(conditionKey=value2))))";

    assertEquals(correctExpectedValueOU, this.portType.filterOU);
    assertEquals(correctExpectedValueCN, this.portType.filterCN);
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

    this.codeTablesService.addListToMap(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, Arrays.asList("3"));

    this.unitRepository.searchUnits(searchUnitCriterions, 10);

    String expectedFilterOU = "(&(hsaIdentity=*SE2321000131*E000000000110*)(ou=*resultUnit*)(hsaBusinessClassificationCode=3))";
    String expectedFilterCN = "(&(hsaIdentity=*SE2321000131*E000000000110*)(cn=*resultUnit*)(hsaBusinessClassificationCode=3))";

    assertEquals(expectedFilterOU, this.portType.filterOU);
    assertEquals(expectedFilterCN, this.portType.filterCN);
  }

  @Test
  public void testBuildAddressSearch() {
    String correctResult = "(|(hsaPostalAddress=*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)(hsaPostalAddress=*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)"
        + "(hsaPostalAddress=*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))";
    Filter temp = this.unitRepository.buildAddressSearch("hsaPostalAddress", "*uddevalla*");
    assertEquals(correctResult, temp.encode());
  }

  @Test
  public void testBuildAddressSearchExactMatch() {
    String correctResult = "(|(hsaPostalAddress=uddevalla$*$*$*$*$*)(hsaPostalAddress=*$uddevalla$*$*$*$*)(hsaPostalAddress=*$*$uddevalla$*$*$*)(hsaPostalAddress=*$*$*$uddevalla$*$*)"
        + "(hsaPostalAddress=*$*$*$*$uddevalla$*)(hsaPostalAddress=*$*$*$*$*$uddevalla))";
    Filter temp = this.unitRepository.buildAddressSearch("hsaPostalAddress", "uddevalla");
    assertEquals(correctResult, temp.encode());
  }

  @Test
  public void testcreateSearchFilter() throws Exception {

    //String expectedOU = "(&(ou=*barn**och*ungdoms*)(|(hsaMunicipalityName=*Borås*)(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";
    //String expectedCN = "(&(cn=*barn**och*ungdoms*)(|(hsaMunicipalityName=*Borås*)(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";
    String expectedOU = "(&(ou=*barn**och*ungdoms*)(|(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";
    String expectedCN = "(&(cn=*barn**och*ungdoms*)(|(|(hsaPostalAddress=*Borås*$*$*$*$*$*)(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)(hsaPostalAddress=*$*$*$*$*$*Borås*))(|(hsaStreetAddress=*Borås*$*$*$*$*$*)(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)(hsaStreetAddress=*$*$*$*$*$*Borås*))))";

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("barn- och ungdoms");
    searchUnitCriterions.setLocation("Borås");
    String createSearchFilter = this.unitRepository.createSearchFilter(searchUnitCriterions, false);
    String createSearchFilterCN = this.unitRepository.createSearchFilter(searchUnitCriterions, true);
    assertEquals(expectedOU, createSearchFilter);
    assertEquals(expectedCN, createSearchFilterCN);
  }

  /**
   * Combined test
   */
  @Test
  public void testcreateAdvancedSearchFilter1() {
    String expectedOU = "(&(|(ou=*barn**och*ungdomsvård*)(hsaBusinessClassificationCode=*1501*))(|(hsaMunicipalityCode=*1490*)))";
    String expectedCN = "(&(|(cn=*barn**och*ungdomsvård*)(hsaBusinessClassificationCode=*1501*))(|(hsaMunicipalityCode=*1490*)))";

    Unit unit = new Unit();
    unit.setName("barn- och ungdomsvård");
    unit.setHsaMunicipalityCode("1490");
    String resultOU = this.unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = this.unitRepository.createAdvancedSearchFilter(unit, false, true);
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
    String resultOU = this.unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = this.unitRepository.createAdvancedSearchFilter(unit, false, true);
    assertEquals(expectedOU, resultOU);
    assertEquals(expectedCN, resultCN);
  }

  @Test
  public void testcreateAdvancedSearchFilter3() {
    // TODO: Should not contain the first and condition.
    String expectedOU = "(&(|(ou=*ambulans*)(hsaBusinessClassificationCode=*1501*)))";
    String expectedCN = "(&(|(cn=*ambulans*)(hsaBusinessClassificationCode=*1501*)))";

    Unit unit = new Unit();
    unit.setName("ambulans");
    String resultOU = this.unitRepository.createAdvancedSearchFilter(unit, false, false);
    String resultCN = this.unitRepository.createAdvancedSearchFilter(unit, false, true);
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
    this.unitRepository.getSubUnits(parentUnit, 3);

    assertEquals(base, this.portType.baseCN.toString());
    assertEquals(base, this.portType.baseOU.toString());
    assertEquals(filterOU, this.portType.filterOU);
    assertEquals(filterCN, this.portType.filterCN);
  }

  @Test
  public void testGetUnitByHsaId() throws KivException {
    String expectedFilter = "(hsaIdentity=abc-123)";
    this.unitRepository.getUnitByHsaId("abc-123");
    assertEquals(expectedFilter, this.portType.filterOU);
    assertEquals(expectedFilter, this.portType.filterCN);
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    String expectedOU = "";
    String expectedCN = "";
    this.unitRepository.getAllUnitsHsaIdentity();
    assertEquals(expectedOU, this.portType.filterOU);
    assertEquals(expectedCN, this.portType.filterCN);
  }

  @Test
  public void testGetAllUnitsHsaIdentityOnlyPublicUnits() throws KivException {
    String expectedOU = "(&(hsaDestinationIndicator=03))";
    String expectedCN = "(&(hsaDestinationIndicator=03))";

    this.unitRepository.getAllUnitsHsaIdentity(true);
    assertEquals(expectedOU, this.portType.filterOU);
    assertEquals(expectedCN, this.portType.filterCN);
  }

  @Test
  public void testGetAllUnitsOnlyPublicUnits() throws KivException {
    String expectedOU = "(&(hsaDestinationIndicator=03))";
    String expectedCN = "(&(hsaDestinationIndicator=03))";

    this.unitRepository.getAllUnits(true);
    assertEquals(expectedOU, this.portType.filterOU);
    assertEquals(expectedCN, this.portType.filterCN);
  }

  @Test
  public void testGetAllUnits() throws KivException {
    String expectedOU = "(ou=*)";
    String expectedCN = "(cn=*)";

    this.unitRepository.getAllUnits(false);
    assertEquals(expectedOU, this.portType.filterOU);
    assertEquals(expectedCN, this.portType.filterCN);
  }

  @Test
  public void testRemoveUnallowedUnits() throws KivException {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    this.portType.addUnit(this.createUnit("abc-123", "", "1504", "112233", ""));
    this.portType.addUnit(this.createUnit("abc-456", "", "1500", "", ""));
    this.portType.addUnit(this.createUnit("SE6460000000-E000000000222", "", "abc", "", ""));
    this.portType.addUnit(this.createUnit("abc-789", "", "1", "12345", ""));

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.addHealthcareType(healthcareType);
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    SikSearchResultList<Unit> units = this.unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, true);
    assertNotNull(units);
    assertEquals(3, units.size());
    assertEquals(3, units.getTotalNumberOfFoundItems());
  }

  // TODO: Cannot put two different care types 01 and 03 to the list. Correct query should be (&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=03)))
  @Test
  public void testGetUnitByHsaIdHasNotCareTypeInpatient() throws KivException {
    String expectedOU = "(&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=01)(vgrCareType=01)))";
    String expectedCN = "(&(hsaIdentity=abc-123)(|(vgrCareType=01)(vgrCareType=01)(vgrCareType=01)))";

    this.codeTablesService.addListToMap(KivwsCodeTableName.CARE_TYPE, Arrays.asList("01"));
    this.unitRepository.getUnitByHsaIdAndHasNotCareTypeInpatient("abc-123");

    assertEquals(expectedOU, this.portType.filterOU);
    assertEquals(expectedCN, this.portType.filterCN);
  }

  @Test
  public void getUnitByDN() throws Exception {
    this.portType.addUnit(this.createUnit("abc-123", "", "", "", ""));
    Unit unit = this.unitRepository.getUnitByDN(DN.createDNFromString("ou=abc-123,ou=other"));

    assertNotNull(unit);
    assertEquals("(ou=abc-123)", this.portType.filterOU);
  }

  @Test
  public void exceptionsFromPorttypeAreLogged() {
    this.portType.throwException = true;

    this.unitRepository.getAllUnits(true);

    String error = this.logFactory.getError(true);
    assertEquals("Exception searchUnit\nException searchFunction\n", error);
  }

  private static class VGRegionWebServiceMock implements VGRegionWebServiceImplPortType {
    private final ObjectFactory objectFactory = new ObjectFactory();
    private VGRegionDirectory vgRegionDirectory;
    private ArrayOfString attrs;
    private boolean throwException;
    private String baseOU;
    private String baseCN;
    private String searchScope;
    private String filterCN;
    private String filterOU;
    private ArrayOfUnit units;

    private void addUnit(se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit) {
      if (this.units == null) {
        this.units = this.objectFactory.createArrayOfUnit();
      }
      this.units.getUnit().add(unit);
    }

    @Override
    public String2StringMap getAttributeCodesAndCleartexts(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeletedObject getDeletedUnits(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Function getFunctionAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getFunctionTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonEmploymentAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getPersonTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit getUnitAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getUnitTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfFunction searchFunction(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      this.throwExceptionIfApplicable("Exception searchFunction");
      this.baseCN = arg3;
      this.filterCN = arg0;
      this.attrs = arg1;
      this.vgRegionDirectory = arg2;
      this.searchScope = arg4;

      ArrayOfFunction arrayOfFunction = new ArrayOfFunction();

      // Create mock function
      Function function = new Function();
      String2ArrayOfAnyTypeMap string2ArrayOfAnyTypeMap = new String2ArrayOfAnyTypeMap();
      string2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("hsaIdentity", "value234"));
      function.setAttributes(this.objectFactory.createEmploymentAttributes(string2ArrayOfAnyTypeMap));
      function.setDn(this.objectFactory.createFunctionDn("cn=abc-234"));
      arrayOfFunction.getFunction().add(function);
      return arrayOfFunction;
    }

    private Entry createEntry(String key, String value) {
      Entry entry = new String2ArrayOfAnyTypeMap.Entry();
      entry.setKey(key);
      ArrayOfAnyType arrayOfAnyType = new ArrayOfAnyType();
      arrayOfAnyType.getAnyType().add(value);
      entry.setValue(arrayOfAnyType);
      return entry;
    }

    @Override
    public ArrayOfPerson searchPerson(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfPerson searchPersonEmployment(String arg0, ArrayOfString arg1, String arg2, ArrayOfString arg3, VGRegionDirectory arg4, String arg5, String arg6) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfResource searchResource(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfServer searchServer(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfUnit searchUnit(String filter, ArrayOfString attrs, VGRegionDirectory directory, String base, String searchScope) throws VGRException_Exception {
      this.throwExceptionIfApplicable("Exception searchUnit");
      this.filterOU = filter;
      this.vgRegionDirectory = directory;
      this.attrs = attrs;
      this.baseOU = base;
      this.searchScope = searchScope;
      if (this.units == null) {
        ArrayOfUnit arrayOfUnit = new ArrayOfUnit();
        se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit unit = new se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit();
        String2ArrayOfAnyTypeMap createString2ArrayOfAnyTypeMap = this.objectFactory.createString2ArrayOfAnyTypeMap();
        createString2ArrayOfAnyTypeMap.getEntry().add(this.createEntry("hsaIdentity", "value123"));
        unit.setAttributes(this.objectFactory.createServerAttributes(createString2ArrayOfAnyTypeMap));
        unit.setDn(this.objectFactory.createUnitDn("ou=abc-123"));
        arrayOfUnit.getUnit().add(unit);
        return arrayOfUnit;
      }
      return this.units;
    }

    @Override
    public ArrayOfUnsurePerson searchUnsurePerson(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    private void throwExceptionIfApplicable(String message) throws VGRException_Exception {
      if (this.throwException) {
        throw new VGRException_Exception(message, new VGRException());
      }
    }

    @Override
    public ArrayOfString getSearchAttributesForDeliveryPoint() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeletedObject getDeletedEmployees(String timestamp) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForDeliveryPoint() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfDeliveryPoint searchDeliveryPoint(String filter, ArrayOfString attributes) throws VGRException_Exception {
      return null;
    }
  }
}
