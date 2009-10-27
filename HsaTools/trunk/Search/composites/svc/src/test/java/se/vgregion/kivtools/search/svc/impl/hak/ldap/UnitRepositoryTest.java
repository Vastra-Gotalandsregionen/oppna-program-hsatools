package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public class UnitRepositoryTest {
  @Before
  public void setUp() throws Exception {
    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    // Create ldapConnectionMock.

    UnitRepository unitRepository = new UnitRepository();
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    String expectedFilter = "(|(&(objectclass=organizationalUnit)(&(ou=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*))))(&(objectclass=organizationalRole)(&(cn=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*)))))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    UnitRepository unitRepository = new UnitRepository();
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    String expectedFilter = "(&(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";
    LDAPSearchResultsMock ldapSearchResults = new LDAPSearchResultsMock();
    ldapSearchResults.addLDAPEntry(new LDAPEntryMock("hsaIdentity", "ABC-123"));
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, ldapSearchResults);

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    List<String> allUnitsHsaIdentity = unitRepository.getAllUnitsHsaIdentity();
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }
}
