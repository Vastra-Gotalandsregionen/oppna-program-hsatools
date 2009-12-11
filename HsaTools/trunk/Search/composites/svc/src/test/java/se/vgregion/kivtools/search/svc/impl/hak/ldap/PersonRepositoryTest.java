package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
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
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

import com.novell.ldap.LDAPException;

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
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();

    ldapConnectionMock.setReturnNullResult(true);
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    assertNull(searchPersons);

    ldapConnectionMock.setReturnNullResult(false);

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock personEntry1 = new LDAPEntryMock();
    personEntry1.addAttribute("givenName", "Kalle");
    personEntry1.addAttribute("cn", "cn=Kalle,ou=Org,o=VGR");
    ldapSearchResultsMock.addLDAPEntry(personEntry1);
    LDAPEntryMock personEntry2 = new LDAPEntryMock();
    personEntry2.addAttribute("givenName", "Olle");
    personEntry2.addAttribute("cn", "cn=Olle,ou=Org,o=VGR");
    ldapSearchResultsMock.addLDAPEntry(personEntry2);
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=*ksn829*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))",
        ldapSearchResultsMock);

    searchPersonCriterion.setGivenName(" Kalle ");
    searchPersonCriterion.setSurname(" Svensson ");

    searchPersons = personRepository.searchPersons(searchPersonCriterion, 0);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");

    searchPersonCriterion.setUserId(" ksn829 ");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 1);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=*ksn829*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");
    assertEquals(1, searchPersons.size());

    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersonsExactMatch() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=ksn001)(|(givenName=Kalle)(rsvFirstNames=Kalle))(|(sn=Svensson)(middleName=Svensson)))", ldapSearchResultsMock);

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName(" \"Kalle\" ");
    searchPersonCriterion.setSurname(" \"Svensson\" ");
    searchPersonCriterion.setUserId(" \"ksn001\" ");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=ksn001)(|(givenName=Kalle)(rsvFirstNames=Kalle))(|(sn=Svensson)(middleName=Svensson)))");
    assertEquals(1, searchPersons.size());

    ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=ksn829))", ldapSearchResultsMock);

    searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setUserId("\"ksn829\"");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=ksn829))");
    assertEquals(1, searchPersons.size());

    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersonsTelephoneNumber() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setUserId(" 070-123 456 ");
    personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(|(regionName=*070*123*456*)(facsimileTelephoneNumber=*70123456*)(hsaSwitchboardNumber=*70123456*)"
        + "(telephoneNumber=*70123456*)(hsaTelephoneNumber=*70123456*)(mobile=*70123456*)(hsaInternalPagerNumber=*70123456*)(pager=*70123456*)(hsaTextPhoneNumber=*70123456*)))");

    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersonsExceptionHandling() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=*ksn829*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))",
        ldapSearchResultsMock);

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName(" Kalle ");
    searchPersonCriterion.setSurname(" Svensson ");
    searchPersonCriterion.setUserId(" ksn829 ");

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException("message", LDAPException.LDAP_TIMEOUT, "servermessage"));
    try {
      personRepository.searchPersons(searchPersonCriterion, 10);
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException("message", LDAPException.CONNECT_ERROR, "servermessage"));
    try {
      personRepository.searchPersons(searchPersonCriterion, 10);
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException());
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);

    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=*ksn829*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");
    assertEquals(0, searchPersons.size());

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
    assertEquals(1, persons.size());

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

    ldapSearchResultsMock.addLDAPEntry(ldapEntry);
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=kon829))", ldapSearchResultsMock);

    persons = personRepository.searchPersons("\"kon829\"", 1);
    assertNotNull(persons);
    assertEquals(1, persons.size());
  }

  @Test
  public void testGetPersonByDn() throws KivException {
    LDAPEntryMock entry = new LDAPEntryMock();
    entry.addAttribute("givenName", "Nina");
    entry.addAttribute("sn", "Kanin");
    entry.addAttribute("cn", "cn=Nina Kanin");
    ldapConnectionMock.addLDAPEntry("cn=Nina Kanin, ou=abc, ou=def", entry);

    Person person = personRepository.getPersonByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertNotNull(person);
    assertEquals("Nina", person.getGivenName());
    assertEquals("Kanin", person.getSn());
    assertEquals("cn=Nina Kanin", person.getCn());
  }

  @Test
  public void testGetPersonByDnExceptionHandling() {
    ldapConnectionMock.setLdapException(new LDAPException());

    try {
      personRepository.getPersonByDn("cn=Nina Kanin,ou=abc,ou=def");
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetProfileImageByDn() throws KivException {
    DirContextOperationsMock personWithImage = new DirContextOperationsMock();
    personWithImage.addAttributeValue("jpegPhoto", StringUtil.getBytes("MockImageData", "UTF-8"));
    this.ldapTemplate.addBoundDN(new DistinguishedName("cn=Nina Kanin,ou=abc,ou=def"), personWithImage);

    byte[] profileImage = personRepository.getProfileImageByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertEquals("MockImageData", StringUtil.getString(profileImage, "UTF-8"));
  }

  @Test
  public void testGetProfileImageByDnExceptionHandling() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new NameNotFoundException("No entry found"));

    byte[] profileImage = personRepository.getProfileImageByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertNull(profileImage);
  }

  @Test
  public void testGetConnectionFromPoolNoConnection() {
    ldapConnectionPoolMock = new LdapConnectionPoolMock(null);
    personRepository.setLdapConnectionPool(ldapConnectionPoolMock);

    try {
      personRepository.getPersonByVgrId("abc123");
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetPersonByVgrId() throws KivException {
    ldapConnectionMock.setReturnNullResult(true);
    Person person = personRepository.getPersonByVgrId("ksn829");
    assertNull(person);

    ldapConnectionMock.setReturnNullResult(false);

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=ksn829))", ldapSearchResultsMock);

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException("message", LDAPException.LDAP_TIMEOUT, "servermessage"));
    try {
      personRepository.getPersonByVgrId("ksn829");
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException("message", LDAPException.CONNECT_ERROR, "servermessage"));
    try {
      personRepository.getPersonByVgrId("ksn829");
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(new LDAPException());
    try {
      personRepository.getPersonByVgrId("ksn829");
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapSearchResultsMock.setLdapException(null);
    person = personRepository.getPersonByVgrId("ksn829");

    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=ksn829))");
    assertNotNull(person);

    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetAllPersonsInUnit() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapConnectionMock.addLDAPSearchResults("(hsaIdentity=abc-123)", ldapSearchResultsMock);

    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));

    SikSearchResultList<Person> allPersonsInUnit = personRepository.getAllPersonsInUnit("abc-123", 10);

    ldapConnectionMock.assertFilter("(hsaIdentity=abc-123)");
    assertNotNull(allPersonsInUnit);
    assertEquals(1, allPersonsInUnit.size());

    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private Map<Name, DirContextOperations> boundDNs = new HashMap<Name, DirContextOperations>();
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();
    private NameNotFoundException exceptionToThrow;

    public void addBoundDN(Name dn, DirContextOperations dirContextOperations) {
      this.boundDNs.put(dn, dirContextOperations);
    }

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void setExceptionToThrow(NameNotFoundException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    public Object lookup(Name dn, ContextMapper mapper) {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }

      DirContextOperations dirContextOperations = this.boundDNs.get(dn);
      if (dirContextOperations == null) {
        throw new NameNotFoundException("Name not found");
      }

      Object result = null;
      if (dirContextOperations != null) {
        result = mapper.mapFromContext(dirContextOperations);
      }
      return result;
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
