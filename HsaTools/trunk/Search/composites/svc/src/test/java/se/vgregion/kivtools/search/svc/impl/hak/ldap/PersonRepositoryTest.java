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
import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class PersonRepositoryTest {
  private static final String TEST = "Test";
  private static final String TEST_DN = "cn=abc,ou=def";
  private static final String TEST_TIME = "1-4#08:00#17:00";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";

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

  @Test
  public void testExtractEmploymentInfo() throws KivException {
    LDAPEntryMock ldapEntry = new LDAPEntryMock();
    ldapEntry.addAttribute("cn", TEST);
    ldapEntry.addAttribute("ou", TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("street", TEST);
    ldapEntry.addAttribute("hsaInternalAddress", TEST);
    ldapEntry.addAttribute("postalAddress", TEST);
    ldapEntry.addAttribute("hsaDeliveryAddress", TEST);
    ldapEntry.addAttribute("hsaInvoiceAddress", TEST);
    ldapEntry.addAttribute("hsaConsigneeAddress", TEST);
    ldapEntry.addAttribute("facsimileTelephoneNumber", TEST);
    ldapEntry.addAttribute("labeledUri", TEST);
    ldapEntry.addAttribute("title", TEST);
    ldapEntry.addAttribute("description", TEST);
    ldapEntry.addAttribute("hsaSwitchboardNumber", TEST);
    ldapEntry.addAttribute("company", TEST);
    ldapEntry.addAttribute("telephoneNumber", TEST);
    ldapEntry.addAttribute("hsaTelephoneNumber", TEST);
    ldapEntry.addAttribute("mobile", TEST);
    ldapEntry.addAttribute("hsaInternalPagerNumber", TEST);
    ldapEntry.addAttribute("pager", TEST);
    ldapEntry.addAttribute("hsaTextPhoneNumber", TEST);
    ldapEntry.addAttribute("whenChanged", "20091109104650.0Z");
    ldapEntry.addAttribute("telephoneHours", TEST_TIME);
    ldapEntry.addAttribute("distinguishedName", TEST_DN);
    ldapEntry.addAttribute("postalCode", TEST);

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(ldapEntry);
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=*kon829*))", ldapSearchResultsMock);

    SikSearchResultList<Person> persons = personRepository.searchPersons("kon829", 1);
    assertNotNull(persons);

    Employment employment = persons.get(0).getEmployments().get(0);

    assertEquals(TEST, employment.getCn());
    assertEquals(TEST, employment.getOu());
    assertEquals(TEST, employment.getHsaPersonIdentityNumber());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaStreetAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaConsigneeAddress().getAdditionalInfo().toString());
    assertEquals(TEST, employment.getFacsimileTelephoneNumber().toString());
    assertEquals(TEST, employment.getLabeledUri());
    assertEquals(TEST, employment.getTitle());
    assertEquals(EXPECTED_LIST_RESULT, employment.getDescription().toString());
    assertEquals(TEST, employment.getHsaSedfSwitchboardTelephoneNo().toString());
    assertEquals(TEST, employment.getName());
    assertEquals(EXPECTED_LIST_RESULT, employment.getHsaTelephoneNumbers().toString());
    assertEquals(TEST, employment.getHsaPublicTelephoneNumber().toString());
    assertEquals(TEST, employment.getMobileTelephoneNumber().toString());
    assertEquals(TEST, employment.getPagerTelephoneNumber().toString());
    assertEquals(TEST, employment.getHsaInternalPagerNumber().toString());
    assertEquals(TEST, employment.getHsaTextPhoneNumber().toString());
    assertNotNull(TEST, employment.getModifyTimestamp());
    assertEquals("Måndag-Torsdag 08:00-17:00", employment.getHsaTelephoneTime().get(0).getDisplayValue());
    assertEquals(DN.createDNFromString(TEST_DN), employment.getVgrStrukturPerson());
    assertEquals(TEST, employment.getZipCode().getZipCode());
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
