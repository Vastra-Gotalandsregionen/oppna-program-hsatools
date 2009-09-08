package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class PersonRepositoryTest {

  @Test
  public void testCreateSearchPersonsFilter() throws Exception {
    PersonRepository personRepository = new PersonRepository();
    String searchFilter = personRepository.createSearchPersonsFilter("per", "andersson", "peran901");
    assertEquals("(&(objectclass=hkatPerson)(regionName=*peran901*)(|(givenName=*per*)(rsvFirstNames=*per*))(|(sn=*andersson*)(rsvFirstNames=*andersson*)))", searchFilter);
    searchFilter = personRepository.createSearchPersonsFilter("", "", "");
    assertNull(searchFilter);
  }
}
