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

package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public class SearchServiceMock implements SearchService {
  private int maxSearchResults = -1;

  private SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
  private final Map<String, SikSearchResultList<Employment>> employments = new HashMap<String, SikSearchResultList<Employment>>();
  private final Map<String, Unit> units = new HashMap<String, Unit>();
  private final Map<String, SikSearchResultList<Person>> personsForUnit = new HashMap<String, SikSearchResultList<Person>>();
  private final List<KivException> exceptionsToThrow = new ArrayList<KivException>();
  private List<String> allPersonsId = Collections.emptyList();
  private List<String> allUnitsId = Collections.emptyList();
  private final List<SikSearchResultList<Unit>> searchAdvancedUnitsSearchResults = new ArrayList<SikSearchResultList<Unit>>();
  int searchAdvancedUnitsCallCount;
  private int exceptionCallCount;
  Unit unitCriterion;

  public void setPersons(SikSearchResultList<Person> persons) {
    this.persons = persons;
  }

  public void addEmployment(String personDn, SikSearchResultList<Employment> employment) {
    this.employments.put(personDn, employment);
  }

  public void addUnit(Unit unit) {
    this.units.put(unit.getHsaIdentity(), unit);
  }

  public void addPersonsForUnit(Unit unit, SikSearchResultList<Person> persons) {
    this.personsForUnit.put(unit.getHsaIdentity(), persons);
  }

  public void setAllPersonsId(List<String> allPersonsId) {
    this.allPersonsId = allPersonsId;
  }

  public void setAllUnitsId(List<String> allUnitsId) {
    this.allUnitsId = allUnitsId;
  }

  public void addSearchAdvancedUnitsSearchResult(SikSearchResultList<Unit> searchResult) {
    this.searchAdvancedUnitsSearchResults.add(searchResult);
  }

  public void addExceptionToThrow(KivException exception) {
    this.exceptionsToThrow.add(exception);
  }

  public void clearExceptionsToThrow() {
    this.exceptionsToThrow.clear();
    this.exceptionCallCount = 0;
  }

  private void throwExceptionIfApplicable() throws KivException {
    if (this.exceptionsToThrow.size() > 0) {
      if (this.exceptionsToThrow.size() == 1) {
        throw this.exceptionsToThrow.get(0);
      } else {
        this.exceptionCallCount++;
        if (this.exceptionCallCount <= this.exceptionsToThrow.size() && this.exceptionsToThrow.get(this.exceptionCallCount - 1) != null) {
          throw this.exceptionsToThrow.get(this.exceptionCallCount - 1);
        }
      }
    }
  }

  public void assertUnitCriterionName(String expected) {
    assertEquals(expected, this.unitCriterion.getName());
  }

  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
    SikSearchResultList<Employment> result;
    if (this.employments.containsKey(personDn)) {
      result = this.employments.get(personDn);
    } else {
      result = new SikSearchResultList<Employment>();
    }
    return result;
  }

  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    this.throwExceptionIfApplicable();
    return this.units.get(hsaId);
  }

  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    this.throwExceptionIfApplicable();
    return new SikSearchResultList<Unit>();
  }

  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    if (units != null) {
      for (Unit unit : units) {

        if (this.personsForUnit.containsKey(unit.getHsaIdentity())) {
          result.addAll(this.personsForUnit.get(unit.getHsaIdentity()));
        }
      }
    }
    return result;
  }

  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
    return this.persons;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws KivException {
    return this.persons;
  }

  @Override
  public List<String> getAllPersonsId() throws KivException {
    this.throwExceptionIfApplicable();
    return this.allPersonsId;
  }

  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions unit, int maxSearchResult) throws KivException {
    this.throwExceptionIfApplicable();
    this.maxSearchResults = maxSearchResult;
    return new SikSearchResultList<Unit>(this.units.values());
  }

  public void assertMaxSearchResults(int expected) {
    assertEquals("Unexpected value for maxSearchResults", expected, this.maxSearchResults);
  }

  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    this.throwExceptionIfApplicable();
    return this.allUnitsId;
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    this.searchAdvancedUnitsCallCount++;
    this.throwExceptionIfApplicable();
    this.maxSearchResults = maxSearchResult;
    this.unitCriterion = unit;
    // return new SikSearchResultList<Unit>(this.units.values());
    return this.searchAdvancedUnitsSearchResults.get(this.searchAdvancedUnitsCallCount - 1);
  }

  @Override
  public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
    this.throwExceptionIfApplicable();
    return this.allUnitsId;
  }

  // Dummy implementations

  @Override
  public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
    return null;
  }

  @Override
  public Person getPersonById(String id) throws KivException {
    return null;
  }

  @Override
  public Unit getUnitByDN(String dn) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    return null;
  }

  @Override
  public Person getPersonByDn(String personDn) {
    return null;
  }

  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
    return null;
  }

  @Override
  public List<Person> getAllPersons() throws KivException {
    return null;
  }

  @Override
  public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
    return new SikSearchResultList<Unit>();
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
