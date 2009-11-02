package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class PersonRepositoryTest {
  private PersonRepository personRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;

  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() {
    personRepository = new PersonRepository();

    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    personRepository.setLdapConnectionPool(ldapConnectionPoolMock);

    ldapTemplate = new LdapTemplateMock();
    personRepository.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testSearchPersons() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=*kalsve01*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))",
        ldapSearchResultsMock);

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName("Kalle");
    searchPersonCriterion.setSurname("Svensson");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");

    searchPersonCriterion.setUserId("kalsve01");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=*kalsve01*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");
    assertEquals(1, searchPersons.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersonsByDn() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock entry1 = new LDAPEntryMock();
    entry1.addAttribute("givenName", "Kalle");
    entry1.addAttribute("sn", "Kula");
    entry1.addAttribute("cn", "cn=Kalle Kula");
    LDAPEntryMock entry2 = new LDAPEntryMock();
    entry2.addAttribute("givenName", "Anders");
    entry2.addAttribute("sn", "Ask");
    entry2.addAttribute("cn", "Anders Ask");

    ldapSearchResultsMock.addLDAPEntry(entry1);
    ldapSearchResultsMock.addLDAPEntry(entry2);

    ldapConnectionMock.addLDAPSearchResults("(objectClass=hkatPerson)", ldapSearchResultsMock);

    List<Person> searchPersons = personRepository.searchPersonsByDn("CN=Anders Ask,OU=Länssjukhuset i Halmstad,OU=Landstinget Halland,DC=lthallandhsa,DC=se", 10);
    ldapConnectionMock.assertFilter("(objectClass=hkatPerson)");
    ldapConnectionMock.assertBaseDn("CN=Anders Ask,OU=Länssjukhuset i Halmstad,OU=Landstinget Halland,DC=lthallandhsa,DC=se");
    assertEquals(2, searchPersons.size());
    assertEquals("Anders", searchPersons.get(0).getGivenName());
    assertEquals("Kalle", searchPersons.get(1).getGivenName());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetAllPersonsVgrId() throws KivException {
    DirContextOperationsMock regionName1 = new DirContextOperationsMock();
    regionName1.addAttributeValue("regionName", "kal456");
    DirContextOperationsMock regionName2 = new DirContextOperationsMock();
    regionName2.addAttributeValue("regionName", "abc123");
    this.ldapTemplate.addDirContextOperationForSearch(regionName1);
    this.ldapTemplate.addDirContextOperationForSearch(regionName2);
    List<String> allPersonsVgrId = personRepository.getAllPersonsVgrId();
    ldapTemplate.assertSearchFilter("(objectClass=hkatPerson)");
    assertNotNull(allPersonsVgrId);
    assertEquals(2, allPersonsVgrId.size());
    assertTrue(allPersonsVgrId.contains("abc123"));
    assertTrue(allPersonsVgrId.contains("kal456"));
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
