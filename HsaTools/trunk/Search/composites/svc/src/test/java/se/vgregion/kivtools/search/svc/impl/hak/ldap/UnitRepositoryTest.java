package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

import com.novell.ldap.LDAPConnection;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.hak.ldap.UnitFactory;
import se.vgregion.kivtools.search.svc.impl.hak.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;


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
    String expectedFilter = "(|(&(objectclass=organizationalUnit)(&(ou=*unitName*)(|(municipalityName=*municipalityName*)(|(postalAddress=*municipalityName*$*$*$*$*$*)(postalAddress=*$*municipalityName*$*$*$*$*)(postalAddress=*$*$*municipalityName*$*$*$*)(postalAddress=*$*$*$*municipalityName*$*$*)(postalAddress=*$*$*$*$*municipalityName*$*)(postalAddress=*$*$*$*$*$*municipalityName*))(|(streetAddress=*municipalityName*$*$*$*$*$*)(streetAddress=*$*municipalityName*$*$*$*$*)(streetAddress=*$*$*municipalityName*$*$*$*)(streetAddress=*$*$*$*municipalityName*$*$*)(streetAddress=*$*$*$*$*municipalityName*$*)(streetAddress=*$*$*$*$*$*municipalityName*)))))(&(objectclass=organizationalRole)(&(cn=*unitName*)(|(municipalityName=*municipalityName*)(|(postalAddress=*municipalityName*$*$*$*$*$*)(postalAddress=*$*municipalityName*$*$*$*$*)(postalAddress=*$*$*municipalityName*$*$*$*)(postalAddress=*$*$*$*municipalityName*$*$*)(postalAddress=*$*$*$*$*municipalityName*$*)(postalAddress=*$*$*$*$*$*municipalityName*))(|(streetAddress=*municipalityName*$*$*$*$*$*)(streetAddress=*$*municipalityName*$*$*$*$*)(streetAddress=*$*$*municipalityName*$*$*$*)(streetAddress=*$*$*$*municipalityName*$*$*)(streetAddress=*$*$*$*$*municipalityName*$*)(streetAddress=*$*$*$*$*$*municipalityName*))))))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
  }
}
