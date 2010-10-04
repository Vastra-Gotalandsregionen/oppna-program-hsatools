/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

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

  @Test
  public void testGetPersonByDn() throws KivException {
    searchServiceLdapImpl.getPersonByDn("cn=abc,ou=def");
    assertTrue(personRepository.getPersonByDnCalled);
  }

  @Test
  public void testGetProfileImageByDn() throws KivException {
    searchServiceLdapImpl.getProfileImageByDn("cn=Nina Kanin,ou=abc,ou=def");
    assertTrue(personRepository.getProfileImageByDnCalled);
  }

  class PersonRepositoryMock extends PersonRepository {
    private boolean searchPersonCalled;
    private boolean getPersonByDnCalled;
    public boolean getProfileImageByDnCalled;

    @Override
    public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
      searchPersonCalled = true;
      return null;
    }

    @Override
    public Person getPersonByDn(String dn) throws KivException {
      getPersonByDnCalled = true;
      return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getProfileImageByDn(String dn) throws KivException {
      getProfileImageByDnCalled = true;
      return null;
    }
  }
}