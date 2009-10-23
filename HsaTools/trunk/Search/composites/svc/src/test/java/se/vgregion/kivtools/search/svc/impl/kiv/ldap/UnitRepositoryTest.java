package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
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
    SikSearchResultList<Unit> searchUnitsResult = unitRepository.searchUnits(searchUnitCriterions);
    assertEquals(1, searchUnitsResult.size());
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
  
  private class CodeTableMock implements CodeTablesService{

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
