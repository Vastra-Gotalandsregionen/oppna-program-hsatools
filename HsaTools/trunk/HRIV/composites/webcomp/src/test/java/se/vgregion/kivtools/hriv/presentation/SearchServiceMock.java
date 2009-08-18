/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;

public class SearchServiceMock implements SearchService {
  private int maxSearchResults = -1;

  private SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
  private Map<String, SikSearchResultList<Employment>> employments = new HashMap<String, SikSearchResultList<Employment>>();
  private Map<String, Unit> units = new HashMap<String, Unit>();
  private Map<String, SikSearchResultList<Person>> personsForUnit = new HashMap<String, SikSearchResultList<Person>>();
  private List<Exception> exceptionsToThrow = new ArrayList<Exception>();
  private List<String> allPersonsId = Collections.emptyList();
  private List<String> allUnitsId = Collections.emptyList();

  private int exceptionCallCount;

  private List<Integer> classificationCodes;

  public void setPersons(SikSearchResultList<Person> persons) {
    this.persons = persons;
  }

  public void addEmployment(String personDn, SikSearchResultList<Employment> employment) {
    this.employments.put(personDn, employment);
  }

  public void addUnit(Unit unit) {
    units.put(unit.getHsaIdentity(), unit);
  }

  public void addPersonsForUnit(Unit unit, SikSearchResultList<Person> persons) {
    personsForUnit.put(unit.getHsaIdentity(), persons);
  }

  public void setAllPersonsId(List<String> allPersonsId) {
    this.allPersonsId = allPersonsId;
  }

  public void setAllUnitsId(List<String> allUnitsId) {
    this.allUnitsId = allUnitsId;
  }

  public void addExceptionToThrow(Exception exception) {
    this.exceptionsToThrow.add(exception);
  }

  public void clearExceptionsToThrow() {
    this.exceptionsToThrow.clear();
    this.exceptionCallCount = 0;
  }

  private void throwExceptionIfApplicable() throws Exception {
    if (exceptionsToThrow.size() > 0) {
      if (exceptionsToThrow.size() == 1) {
        throw exceptionsToThrow.get(0);
      } else {
        exceptionCallCount++;
        if (exceptionCallCount <= exceptionsToThrow.size() && exceptionsToThrow.get(exceptionCallCount - 1) != null) {
          throw exceptionsToThrow.get(exceptionCallCount - 1);
        }
      }
    }
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String id, int maxResult) throws Exception {
    this.maxSearchResults = maxResult;
    return persons;
  }

  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws Exception {
    SikSearchResultList<Employment> result;
    if (employments.containsKey(personDn)) {
      result = employments.get(personDn);
    } else {
      result = new SikSearchResultList<Employment>();
    }
    return result;
  }

  @Override
  public Unit getUnitByHsaId(String hsaId) throws Exception {
    throwExceptionIfApplicable();
    return units.get(hsaId);
  }

  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws Exception {
    throwExceptionIfApplicable();
    return new SikSearchResultList<Unit>();
  }

  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws Exception {
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    if (units != null) {
      for (Unit unit : units) {

        if (personsForUnit.containsKey(unit.getHsaIdentity())) {
          result.addAll(personsForUnit.get(unit.getHsaIdentity()));
        }
      }
    }
    return result;
  }

  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws Exception {
    return this.persons;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws Exception {
    return this.persons;
  }

  @Override
  public List<String> getAllPersonsId() throws Exception {
    throwExceptionIfApplicable();
    return this.allPersonsId;
  }

  @Override
  public SikSearchResultList<Unit> searchUnits(Unit unit, int maxSearchResult) throws Exception {
    throwExceptionIfApplicable();
    this.maxSearchResults = maxSearchResult;
    return new SikSearchResultList<Unit>(this.units.values());
  }

  public void assertMaxSearchResults(int expected) {
    assertEquals("Unexpected value for maxSearchResults", expected, this.maxSearchResults);
  }

  public void assertShowUnitsWithHsaBusinessClassificationCodes(int... classificationCodes) {
    assertEquals("Unexpected number of classification codes", classificationCodes.length, this.classificationCodes.size());
    for (int classificationCode : classificationCodes) {
      assertTrue("Unexpected classification code", this.classificationCodes.contains(classificationCode));
    }
  }

  @Override
  public List<String> getAllUnitsHsaIdentity() throws Exception {
    throwExceptionIfApplicable();
    return this.allUnitsId;
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
    throwExceptionIfApplicable();
    this.maxSearchResults = maxSearchResult;
    return new SikSearchResultList<Unit>(this.units.values());
  }

  @Override
  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
    throwExceptionIfApplicable();
    this.classificationCodes = showUnitsWithTheseHsaBussinessClassificationCodes;
    return this.allUnitsId;
  }

  // Dummy implementations

  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn) throws Exception {
    return null;
  }

  @Override
  public List<Employment> getEmploymentsForPerson(Person person) throws Exception {
    return null;
  }

  @Override
  public List<HealthcareType> getHealthcareTypesList() throws Exception {
    return null;
  }

  @Override
  public Person getPersonByDN(DN dn) throws Exception {
    return null;
  }

  @Override
  public Person getPersonById(String id) throws Exception {
    return null;
  }

  @Override
  public Unit getUnitByDN(String dn) throws Exception {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, Comparator<Unit> sortOrder) throws Exception {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String id) throws Exception {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String id) throws Exception {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> searchUnits(Unit unit) throws Exception {
    return null;
  }
}