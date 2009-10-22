package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;

public class SearchServiceLdapImplTest {

  private SearchServiceLdapImpl searchServiceLdapImpl;
  private PersonRepositoryMock personRepository;

  @Before
  public void setUp() throws Exception {
    searchServiceLdapImpl = new SearchServiceLdapImpl();
    personRepository = new PersonRepositoryMock();
    searchServiceLdapImpl.setPersonRepository(personRepository);
  }

  @Test
  public void testSearchPersonsPersonInt() throws KivException {
    searchServiceLdapImpl.searchPersons(new SearchPersonCriterions(), 123);
    assertTrue(personRepository.searchPersonCalled);
  }

  class PersonRepositoryMock extends PersonRepository {

    private boolean searchPersonCalled;

    @Override
    public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
      searchPersonCalled = true;
      return null;
    }

  }
}
