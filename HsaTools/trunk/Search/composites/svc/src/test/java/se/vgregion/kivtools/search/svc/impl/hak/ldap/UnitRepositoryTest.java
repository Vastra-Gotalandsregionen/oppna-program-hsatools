package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class UnitRepositoryTest {
  private UnitRepository unitRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;

  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    unitRepository = new UnitRepository();
    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);

    ldapTemplate = new LdapTemplateMock();
    unitRepository.setLdapTemplate(ldapTemplate);

    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    // Create ldapConnectionMock.

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(&(ou=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*))))(&(objectclass=organizationalRole)(&(cn=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*)))))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchUnitNameWithDash() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("Kvalitet- och säkerhetsavdelningen");

    // Create ldapConnectionMock.

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(ou=*Kvalitet*och*säkerhetsavdelningen*))(&(objectclass=organizationalRole)(cn=*Kvalitet*och*säkerhetsavdelningen*)))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";

    List<String> allUnitsHsaIdentity = unitRepository.getAllUnitsHsaIdentity();

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetAllUnitsHsaIdentityBusinessClassificationCodesSpecified() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(businessClassificationCode=123)(businessClassificationCode=456)(&(hsaIdentity=SE6460000000-E000000000222)(vgrAnsvarsnummer=12345))(&(hsaIdentity=SE2321000131-E000000000110)(|(vgrAO3kod=5a3)(vgrAO3kod=4d7)(vgrAO3kod=1xp))))(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";

    List<String> allUnitsHsaIdentity = unitRepository.getAllUnitsHsaIdentity(Arrays.asList(Integer.valueOf(123), Integer.valueOf(456)));

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      // Use ReflectionUtil since there is no set-method for cookie.
      ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
      return result;
    }
  }
}
