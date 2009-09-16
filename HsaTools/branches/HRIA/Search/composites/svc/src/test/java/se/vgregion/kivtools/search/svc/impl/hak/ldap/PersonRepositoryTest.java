package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PersonRepositoryTest {

  private PersonRepository personRepository;

  @Before
  public void setUp() {
    personRepository = new PersonRepository();
  }

  @Test
  public void testCreateSearchPersonsFilter() throws Exception {
    String searchFilter = personRepository.createSearchPersonsFilter("per", "andersson", "peran901");
    assertEquals("(&(objectclass=hkatPerson)(regionName=*peran901*)(|(givenName=*per*)(rsvFirstNames=*per*))(|(sn=*andersson*)(middleName=*andersson*)))", searchFilter);
    searchFilter = personRepository.createSearchPersonsFilter("", "", "");
    assertNull(searchFilter);
  }

  @Test
  public void testCreateSearchPersonsFilterOlin() throws Exception {
    String searchFilter = personRepository.createSearchPersonsFilter("", "olin", "");
    assertEquals("(&(objectclass=hkatPerson)(|(sn=*olin*)(middleName=*olin*)))", searchFilter);
  }
}
