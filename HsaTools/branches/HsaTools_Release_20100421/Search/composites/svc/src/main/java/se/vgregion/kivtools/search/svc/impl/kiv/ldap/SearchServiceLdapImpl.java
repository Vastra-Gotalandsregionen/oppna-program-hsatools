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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.Comparator;
import java.util.List;

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
 * @author Anders Asplund, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class SearchServiceLdapImpl implements SearchService {
  private PersonRepository personRepository;
  private UnitRepository unitRepository;
  private EmploymentRepository employmentRepository;

  public void setPersonRepository(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  public void setEmploymentRepository(EmploymentRepository employmentRepository) {
    this.employmentRepository = employmentRepository;
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
    throw new UnsupportedOperationException("Not implemented!!!");
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
    return employmentRepository.getEmployments(DN.createDNFromString(personDn));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Person getPersonById(String vgrId) throws KivException {
    Person person = personRepository.getPersonByVgrId(vgrId);
    if (person != null) {
      person.setEmployments(employmentRepository.getEmployments(DN.createDNFromString(person.getDn())));
    }
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
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list of Persons
   * 
   */
  @Override
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxSearchResult) throws KivException {
    return personRepository.searchPersons(vgrId, maxSearchResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxSearchResult) throws KivException {
    return unitRepository.searchUnits(searchUnitCriterions, maxSearchResult);
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
  public List<Employment> getEmploymentsForPerson(Person person) {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    return personRepository.getPersonsForUnits(units, maxResult);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    return unitRepository.getSubUnits(parentUnit, maxSearchResult);
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
  public Person getPersonByDn(String personDn) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
    throw new UnsupportedOperationException("Not implemented!");
  }
}
