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

package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

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
  private Map<String, SikSearchResultList<Employment>> employments = new HashMap<String, SikSearchResultList<Employment>>();
  private Map<String, Unit> units = new HashMap<String, Unit>();
  private Map<String, SikSearchResultList<Person>> personsForUnit = new HashMap<String, SikSearchResultList<Person>>();
  private List<KivException> exceptionsToThrow = new ArrayList<KivException>();
  private List<String> allPersonsId = Collections.emptyList();
  private List<String> allUnitsId = Collections.emptyList();
  private Person person;
  private byte[] profileImage;
  private String dn;

  private int exceptionCallCount;

  public void setPersons(SikSearchResultList<Person> persons) {
    this.persons = persons;
  }

  public void setPerson(Person person) {
    this.person = person;
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

  public void addExceptionToThrow(KivException exception) {
    this.exceptionsToThrow.add(exception);
  }

  public void clearExceptionsToThrow() {
    this.exceptionsToThrow.clear();
    this.exceptionCallCount = 0;
  }

  private void throwExceptionIfApplicable() throws KivException {
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

  public void setProfileImage(byte[] profileImage) {
    this.profileImage = profileImage;
  }

  public void assertFetchedDn(String expectedDn) {
    assertEquals("Unexpected dn fetched", expectedDn, dn);
  }

  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
    throwExceptionIfApplicable();
    SikSearchResultList<Employment> result;
    if (employments.containsKey(personDn)) {
      result = employments.get(personDn);
    } else {
      result = new SikSearchResultList<Employment>();
    }
    return result;
  }

  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    throwExceptionIfApplicable();
    return units.get(hsaId);
  }

  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    throwExceptionIfApplicable();
    return new SikSearchResultList<Unit>();
  }

  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
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
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
    return this.persons;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws KivException {
    return this.persons;
  }

  @Override
  public List<String> getAllPersonsId() throws KivException {
    throwExceptionIfApplicable();
    return this.allPersonsId;
  }

  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions unit, int maxSearchResult) throws KivException {
    throwExceptionIfApplicable();
    this.maxSearchResults = maxSearchResult;
    return new SikSearchResultList<Unit>(this.units.values());
  }

  public void assertMaxSearchResults(int expected) {
    assertEquals("Unexpected value for maxSearchResults", expected, this.maxSearchResults);
  }

  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    throwExceptionIfApplicable();
    return this.allUnitsId;
  }

  @Override
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    throwExceptionIfApplicable();
    this.maxSearchResults = maxResult;
    return persons;
  }

  @Override
  public Person getPersonById(String id) throws KivException {
    throwExceptionIfApplicable();
    return this.person;
  }

  @Override
  public Person getPersonByDn(String personDn) throws KivException {
    throwExceptionIfApplicable();
    return this.person;
  }

  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
    this.dn = dn;
    return this.profileImage;
  }

  // Dummy implementations

  @Override
  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException {
    return null;
  }

  @Override
  public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException {
    return null;
  }

  @Override
  public Unit getUnitByDN(String dn) throws KivException {
    return null;
  }
}