package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.filter.Filter;

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
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

public class UnitRepositoryTest {

  private UnitRepository unitRepository;
  private Unit resultUnit;
  private LDAPConnection mockLdapConnection;
  private LdapConnectionPool mockLdapConnectionPool;
  private UnitFactory mockUnitFactory;

  @Before
  public void setUp() throws Exception {
    // Create LdapConnection mock.
    mockLdapConnection = createMock(LDAPConnection.class);
    // Create LdapPoolConnection mock.
    mockLdapConnectionPool = createMock(LdapConnectionPool.class);
    mockUnitFactory = createMock(UnitFactory.class);

    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    expect(mockLdapConnectionPool.getConnection(2000)).andReturn(mockLdapConnection);
    expectLastCall().anyTimes();
    mockLdapConnectionPool.freeConnection(mockLdapConnection);
    expectLastCall().anyTimes();

    final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
    resultUnit = new Unit();
    resultUnit.setHsaBusinessClassificationCode(Arrays.asList("1"));
    result.add(resultUnit);
    unitRepository = new UnitRepository() {
      @Override
      protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
        return result;
      }
    };
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    // Create ldapConnectionMock.

    unitRepository = new UnitRepository();
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*)))))(&(objectclass=vgrOrganizationalRole)(&(cn=*unitName*)(|(hsaMunicipalityName=*municipalityName*)(|(hsaPostalAddress=*municipalityName*$*$*$*$*$*)(hsaPostalAddress=*$*municipalityName*$*$*$*$*)(hsaPostalAddress=*$*$*municipalityName*$*$*$*)(hsaPostalAddress=*$*$*$*municipalityName*$*$*)(hsaPostalAddress=*$*$*$*$*municipalityName*$*)(hsaPostalAddress=*$*$*$*$*$*municipalityName*))(|(hsaStreetAddress=*municipalityName*$*$*$*$*$*)(hsaStreetAddress=*$*municipalityName*$*$*$*$*)(hsaStreetAddress=*$*$*municipalityName*$*$*$*)(hsaStreetAddress=*$*$*$*municipalityName*$*$*)(hsaStreetAddress=*$*$*$*$*municipalityName*$*)(hsaStreetAddress=*$*$*$*$*$*municipalityName*))))))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
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
    unitRepository.setCodeTablesService(codeTableMock);
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    String expectedFilter = "(|(&(objectclass=vgrOrganizationalUnit)(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01)))(&(objectclass=vgrOrganizationalRole)(&(vgrAO3kod=01)(vgrAnsvarsnummer=*1*)(hsaBusinessClassificationCode=1505)(vgrCareType=01))))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testHsaEndDate() throws Exception {
    // No hsaEndDate set, ie unit should be returned.
    SikSearchResultList<Unit> resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, Arrays.asList(1));
    assertNotNull("Result should not be null!", resultList);
    assertEquals(1, resultList.size());

    // hsaEndDate set to a "past date", ie unit should NOT be returned.
    resultUnit.setHsaEndDate(TimeUtil.parseStringToZuluTime("20090101000000Z"));
    resultList = unitRepository.searchAdvancedUnits(new Unit(), 1, null, Arrays.asList(1));
    assertEquals(0, resultList.size());
  }

  /**
   * Test to make an advances unit search
   * 
   * @throws Exception
   */
  // TODO make a good search filter to use
  @Test
  public void testSearchAdvancedUnitsMethod() throws Exception {

    Unit resultUnit = new Unit();
    resultUnit.setName("resultUnit");
    resultUnit.setHsaIdentity("SE2321000131-E000000000110");
    resultUnit.setHsaBusinessClassificationCode(Arrays.asList("3"));
    setReturnResultforUnitfactory(resultUnit);

    // Create HealthcareType mock with conditions.
    HealthcareType mockHealthcareType = createMock(HealthcareType.class);
    Map<String, String> healthcareTypeConditions = new HashMap<String, String>();
    healthcareTypeConditions.put("conditionKey", "value1,value2");
    expect(mockHealthcareType.getConditions()).andReturn(healthcareTypeConditions);
    expectLastCall().times(2);

    // Create LDAPEntry mock.
    LDAPEntry mockLdapEntry = createMock(LDAPEntry.class);

    LDAPSearchResults mockLdapSearchResults = mockLdapSearchResult(mockLdapEntry);

    setSearchParamsForMock(mockLdapSearchResults, UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, "", new String[] { "*", "createTimeStamp" });

    // Create Unit mock with HealthcareType mocks.
    Unit mockUnit = createMock(Unit.class);
    expect(mockUnit.getName()).andReturn("unitName");
    expect(mockUnit.getHsaMunicipalityName()).andReturn("Göteborg");
    expectLastCall().times(3);
    expect(mockUnit.getHsaMunicipalityCode()).andReturn("10032");
    expectLastCall().times(2);
    expect(mockUnit.getHsaIdentity()).andReturn("hsaId-1");
    expect(mockUnit.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType));
    expectLastCall().times(3);
    expect(mockUnit.isVgrVardVal()).andReturn(true);
    expectLastCall().anyTimes();

    replay(mockUnit, mockHealthcareType, mockLdapConnection, mockLdapConnectionPool, mockLdapSearchResults, mockUnitFactory);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    UnitRepository unitRepository = new UnitRepository();
    unitRepository.setLdapConnectionPool(mockLdapConnectionPool);
    unitRepository.setUnitFactory(mockUnitFactory);
    SikSearchResultList<Unit> searchAdvancedUnits = unitRepository.searchAdvancedUnits(mockUnit, maxResults, sortOrder, new ArrayList<Integer>());
    assertEquals(1, searchAdvancedUnits.size());
  }

  /**
   * Test to make an ordinary unit search
   * 
   * @throws Exception
   */
  // TODO make a good search filter to use
  @Test
  public void testSearchUnitsMethod() throws Exception {

    // Search unit to use in search test.
    Unit unitToSearchFor = new Unit();
    unitToSearchFor.setHsaIdentity("SE2321000131-E000000000110");
    unitToSearchFor.setName("unitToSearchFor");

    // Create LDAPEntry mock.
    LDAPEntry mockLdapEntry = createMock(LDAPEntry.class);

    // Create LDAPSearchResults mock.
    LDAPSearchResults mockLdapSearchResults = mockLdapSearchResult(mockLdapEntry);

    // Set search parameters for match a return o found unit.
    setSearchParamsForMock(mockLdapSearchResults, UnitRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, "", new String[] { "*", "createTimeStamp" });

    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("resultUnit");
    searchUnitCriterions.setUnitId("SE2321000131-E000000000110");
    searchUnitCriterions.setBusinessClassificationName("3");
    setReturnResultforUnitfactory(resultUnit);

    CodeTableMock codeTableMock = new CodeTableMock();
    codeTableMock.values.put(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, "3");
    UnitRepository unitRepository = new UnitRepository();
    unitRepository.setCodeTablesService(codeTableMock);
    unitRepository.setLdapConnectionPool(mockLdapConnectionPool);
    unitRepository.setUnitFactory(mockUnitFactory);
    replay(mockLdapEntry, mockLdapSearchResults, mockUnitFactory, mockLdapConnection, mockLdapConnectionPool);
    SikSearchResultList<Unit> searchUnitsResult = unitRepository.searchUnits(searchUnitCriterions, 10);
    assertEquals(1, searchUnitsResult.size());
  }

  @Test
  public void testBuildAddressSearch() {
    String correctResult = "(|(hsaPostalAddress=*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)(hsaPostalAddress=*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)"
        + "(hsaPostalAddress=*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))";
    Filter temp = unitRepository.buildAddressSearch("hsaPostalAddress", "*uddevalla*");
    Assert.assertEquals(correctResult, temp.encode());
  }

  /**
   * 
   * @throws Exception
   */
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
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Combined test
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter1() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*)))))");
    Unit unit = new Unit();
    unit.setName("barn- och ungdomsvård");
    unit.setHsaMunicipalityCode("1490");
    String temp = unitRepository.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Only hsamuncipalitycode
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter2() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540)))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540))))))");
    Unit unit = new Unit();
    unit.setHsaMunicipalityCode("1490");
    List<HealthcareType> healthcareTypeList = new ArrayList<HealthcareType>();
    HealthcareType ht = new HealthcareType();
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put("hsaBusinessClassificationCode", "1540");
    ht.setConditions(conditions);
    healthcareTypeList.add(ht);
    unit.setHealthcareTypes(healthcareTypeList);
    String temp = unitRepository.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Only hsamuncipalitycode
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter3() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*ambulans*)))(&(objectclass=vgrOrganizationalRole)(&(cn=*ambulans*))))");
    Unit unit = new Unit();
    unit.setName("ambulans");
    String temp = unitRepository.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  // Test fetching sub units for a chosen unit
  @Test
  public void testGetSubUnits() throws Exception {
    unitRepository = new UnitRepository();
    UnitFactory unitFactory = new UnitFactory();
    unitRepository.setUnitFactory(unitFactory);
    unitFactory.setCodeTablesService(new CodeTablesServiceImpl());
    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    unitFactory.setDisplayValueTranslator(displayValueTranslator);
    String base = "ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr";
    String filter = "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";
    Unit parentUnit = new Unit();
    parentUnit.setHsaIdentity("parent");
    parentUnit.setDn(DN.createDNFromString(base));
    // Set Ldap connetion mock for the UnitRepository
    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(generateLdapConnectionMock(base, filter));
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);

    SikSearchResultList<Unit> subUnits = new SikSearchResultList<Unit>();
    Unit subUnit1 = new Unit();
    subUnit1.setName("SubUnit1");
    subUnit1.setHsaIdentity("SubUnit1");
    Unit subUnit2 = new Unit();
    subUnit2.setName("SubUnit2");
    subUnit2.setHsaIdentity("SubUnit2");

    subUnits.add(subUnit1);
    subUnits.add(subUnit2);

    SikSearchResultList<Unit> subUnitsResult = null;
    subUnitsResult = unitRepository.getSubUnits(parentUnit, 2);

    Unit returndUnit1 = subUnitsResult.get(0);
    Unit returndUnit2 = subUnitsResult.get(1);
    Assert.assertEquals(subUnit1.getName(), returndUnit1.getName());
    Assert.assertEquals(subUnit2.getName(), returndUnit2.getName());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  private LDAPConnectionMock generateLdapConnectionMock(String base, String filter) {
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    LinkedList<LDAPEntryMock> subUnitLdapEntries = new LinkedList<LDAPEntryMock>();

    // Sub unit entries
    LDAPEntryMock subUnitEntry1 = new LDAPEntryMock();
    LDAPEntryMock subUnitEntry2 = new LDAPEntryMock();
    // Sub entity 1
    subUnitEntry1.addAttribute("hsaIdentity", "1");
    subUnitEntry1.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "SubUnit1");
    subUnitEntry1.addAttribute("objectClass", Constants.OBJECT_CLASS_UNIT_SPECIFIC);
    // Sub entity 2
    subUnitEntry2.addAttribute("hsaIdentity", "2");
    subUnitEntry2.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "SubUnit2");
    subUnitEntry2.addAttribute("objectClass", Constants.OBJECT_CLASS_UNIT_SPECIFIC);

    subUnitLdapEntries.add(subUnitEntry1);
    subUnitLdapEntries.add(subUnitEntry2);
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(subUnitEntry1);
    ldapSearchResultsMock.addLDAPEntry(subUnitEntry2);
    ldapConnectionMock.addLDAPSearchResults("(objectClass=vgrOrganizationalUnit)", ldapSearchResultsMock);

    return ldapConnectionMock;
  }

  private void setReturnResultforUnitfactory(Unit resultUnit) throws Exception {
    expect(mockUnitFactory.reconstitute(isA(LDAPEntry.class))).andReturn(resultUnit);
  }

  private void setSearchParamsForMock(LDAPSearchResults mockLdapSearchResults, String base, int scope, String filter, String[] attrs) throws LDAPException {
    // TODO make a good search filter to use, remove isA(String.class) with filter param
    expect(mockLdapConnection.search(eq(UnitRepository.KIV_SEARCH_BASE), eq(LDAPConnection.SCOPE_SUB), isA(String.class), aryEq(attrs), eq(false), isA(LDAPSearchConstraints.class))).andReturn(
        mockLdapSearchResults);
  }

  private LDAPSearchResults mockLdapSearchResult(LDAPEntry mockLdapEntry) throws LDAPException {
    LDAPSearchResults mockLdapSearchResults = createMock(LDAPSearchResults.class);
    expect(mockLdapSearchResults.hasMore()).andReturn(true);
    expect(mockLdapSearchResults.next()).andReturn(mockLdapEntry);
    expect(mockLdapSearchResults.hasMore()).andReturn(false);
    return mockLdapSearchResults;
  }

  private class CodeTableMock implements CodeTablesService {

    private Map<CodeTableName, String> values = new HashMap<CodeTableName, String>();

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return Arrays.asList(values.get(codeTableName));
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
