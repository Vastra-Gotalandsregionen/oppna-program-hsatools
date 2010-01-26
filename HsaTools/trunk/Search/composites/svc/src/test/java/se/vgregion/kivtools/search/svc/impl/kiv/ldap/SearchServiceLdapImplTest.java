/**
 * Copyright 2009 Västra Götalandsregionen
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
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public class SearchServiceLdapImplTest {

  private SearchServiceLdapImpl searchServiceLdapImpl;
  private PersonRepositoryMock personRepository;
  private UnitRepositoryMock unitRepository;

  @Before
  public void setUp() throws Exception {
    searchServiceLdapImpl = new SearchServiceLdapImpl();
    personRepository = new PersonRepositoryMock();
    unitRepository = new UnitRepositoryMock();
    searchServiceLdapImpl.setPersonRepository(personRepository);
    searchServiceLdapImpl.setUnitRepository(unitRepository);
  }

  @Test
  public void testSearchPersons() throws KivException {
    searchServiceLdapImpl.searchPersons(new SearchPersonCriterions(), 123);
    assertTrue(personRepository.searchPersonCalled);
  }

  @Test
  public void testSearchUnits() throws KivException {
    searchServiceLdapImpl.searchUnits(new SearchUnitCriterions(), 123);
    assertTrue(unitRepository.searchUnitCalled);
  }

  class PersonRepositoryMock extends PersonRepository {

    private boolean searchPersonCalled;

    @Override
    public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
      searchPersonCalled = true;
      return null;
    }
  }
  
  class UnitRepositoryMock extends UnitRepository {
    private boolean searchUnitCalled;
    
    @Override
    public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxResult) throws KivException {
    searchUnitCalled = true;
      return null;
    }
  }
}
