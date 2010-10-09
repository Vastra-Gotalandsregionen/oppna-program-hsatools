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

package se.vgregion.kivtools.search.svc.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.cache.PersonCache;
import se.vgregion.kivtools.search.svc.impl.cache.PersonCacheLoaderImpl;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public class PersonCacheLoaderImplTest {
  private final SearchServiceMock searchService = new SearchServiceMock();
  private final PersonCacheLoaderImpl personCacheLoader = new PersonCacheLoaderImpl(this.searchService);

  @Test
  public void cacheIsEmptyIfSearchServiceDoesNotReturnAnyHsaIdentities() {
    PersonCache personCache = this.personCacheLoader.loadCache();
    assertNotNull(personCache);
    assertEquals(0, personCache.getPersons().size());
  }

  @Test
  public void createEmptyCacheReturnNewEmptyCacheEachTime() {
    PersonCache emptyCache1 = this.personCacheLoader.createEmptyCache();
    PersonCache emptyCache2 = this.personCacheLoader.createEmptyCache();
    assertEquals(0, emptyCache1.getPersons().size());
    assertEquals(emptyCache1.getPersons(), emptyCache2.getPersons());
    assertNotSame(emptyCache1, emptyCache2);
  }

  @Test
  public void processingEndsOnKivException() {
    this.searchService.setExceptionToThrow(new KivException("test"));
    this.searchService.addPerson(this.createPerson("abc123", "Anna", "Andersson"));

    PersonCache personCache = this.personCacheLoader.loadCache();
    assertEquals(0, personCache.getPersons().size());
  }

  @Test
  public void unitsAreFetchedAndAddedToCache() {
    this.searchService.addPerson(this.createPerson("abc123", "Anna", "Andersson"));
    this.searchService.addPerson(this.createPerson("def456", "Berit", "Bengtsson"));

    PersonCache personCache = this.personCacheLoader.loadCache();
    assertEquals(2, personCache.getPersons().size());
  }

  private Person createPerson(String vgrId, String givenName, String surname) {
    Person person = new Person();
    person.setVgrId(vgrId);
    person.setGivenName(givenName);
    person.setSn(surname);
    return person;
  }

  private static class SearchServiceMock implements SearchService {
    private final List<Person> persons = new ArrayList<Person>();
    private KivException exceptionToThrow;

    public void addPerson(Person person) {
      this.persons.add(person);
    }

    public void setExceptionToThrow(KivException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public List<Person> getAllPersons() throws KivException {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
      return this.persons;
    }

    // Not implemented
    @Override
    public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByHsaId(String hsaId) throws KivException {
      return null;
    }

    @Override
    public List<String> getAllUnitsHsaIdentity() throws KivException {
      return null;
    }

    @Override
    public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public List<String> getAllPersonsId() throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
      return null;
    }

    @Override
    public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
      return null;
    }

    @Override
    public Person getPersonByDn(String personDn) throws KivException {
      return null;
    }

    @Override
    public Person getPersonById(String id) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
      return null;
    }

    @Override
    public byte[] getProfileImageByDn(String dn) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByDN(String dn) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxSearchResult) throws KivException {
      return null;
    }

    @Override
    public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
      return null;
    }

    @Override
    public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
      return null;
    }

    @Override
    public List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException {
      return null;
    }
  }
}
