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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

/**
 * @author Anders Asplund - Know It
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("unchecked")
public class SearchServiceLdapImpl implements SearchService {
  private PersonRepository personRepository;
  private UnitRepository unitRepository;

  /**
   * {@inheritDoc}
   */
  public void setPersonRepository(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  /**
   * {@inheritDoc}
   */
  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getAllPersonsId() throws KivException {
    return this.personRepository.getAllPersonsVgrId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> getAllPersons() throws KivException {
    return personRepository.getAllPersons();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    return this.unitRepository.getAllUnitsHsaIdentity();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits) throws KivException {
    return this.unitRepository.getAllUnitsHsaIdentity(onlyPublicUnits);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
    return unitRepository.getAllUnits(onlyPublicUnits);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
    return new SikSearchResultList<Employment>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Person getPersonById(String vgrId) throws KivException {
    Person person = personRepository.getPersonByVgrId(vgrId);
    return person;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    return unitRepository.getUnitByHsaId(hsaId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxSearchResult) throws KivException {
    return personRepository.searchPersons(vgrId, maxSearchResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions unit, int maxSearchResult) throws KivException {
    return unitRepository.searchUnits(unit, maxSearchResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    return unitRepository.searchAdvancedUnits(unit, maxSearchResult, sortOrder, onlyPublicUnits);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Unit getUnitByDN(String dn) throws KivException {
    return unitRepository.getUnitByDN(DN.createDNFromString(dn));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
    SikSearchResultList<Person> personWithEmployments = personRepository.searchPersons(person.getVgrId(), 0);
    List<Employment> employments = new ArrayList<Employment>();
    if (personWithEmployments.size() > 0) {
      employments = personWithEmployments.get(0).getEmployments();
    }
    return employments;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    Unit u = null;
    if (units.size() > 0) {
      u = units.get(0);
    }
    return personRepository.getAllPersonsInUnit(u.getHsaIdentity(), maxResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
    List<Person> persons = personRepository.searchPersonsByDn(dn, maxSearchResult);
    SikSearchResultList<Person> personsSearchList = new SikSearchResultList<Person>(persons);
    return personsSearchList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    throw new NotImplementedException("Not used by LTH.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit) throws KivException {
    throw new NotImplementedException("Not used by LTH.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    return personRepository.searchPersons(person, maxResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Person getPersonByDn(String personDn) throws KivException {
    return personRepository.getPersonByDn(personDn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
    return personRepository.getProfileImageByDn(dn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId) throws KivException {
    throw new NotImplementedException("Not used by LTH.");
  }

  @Override
  public List<String> getUnitAdministratorVgrIds(String hsaId) throws KivException {
    throw new NotImplementedException("Not used by LTH.");
  }

}
