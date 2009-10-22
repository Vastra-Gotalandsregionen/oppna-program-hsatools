package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;

public class PersonRepositoryTest {

  private PersonRepository personRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;

  @Before
  public void setUp() {
    personRepository = new PersonRepository();

    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=hkatPerson)(regionName=*kalsve01*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))",
        ldapSearchResultsMock);
    personRepository.setLdapConnectionPool(ldapConnectionPoolMock);
  }

  @Test
  public void testSearchPersons() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName("Kalle");
    searchPersonCriterion.setSurname("Svensson");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");

    searchPersonCriterion.setUserId("kalsve01");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=hkatPerson)(regionName=*kalsve01*)(|(givenName=*Kalle*)(rsvFirstNames=*Kalle*))(|(sn=*Svensson*)(middleName=*Svensson*)))");
    assertEquals(1, searchPersons.size());
  }
}
