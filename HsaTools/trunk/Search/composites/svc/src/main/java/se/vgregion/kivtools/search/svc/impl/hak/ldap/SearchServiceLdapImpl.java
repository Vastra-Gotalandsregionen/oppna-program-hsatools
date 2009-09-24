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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;

/**
 * @author Anders Asplund - Know It
 * @author Jonas Liljenfeldt, Know IT
 * 
 */

@SuppressWarnings("unchecked")
public class SearchServiceLdapImpl implements SearchService {

  private PersonRepository personRepository;
  private UnitRepository unitRepository;
  private EmploymentRepository employmentRepository;
  private HealthcareTypeConditionHelper healthcareTypeConditionHelper;

  public void setPersonRepository(PersonRepository personRepository) {
    this.personRepository = personRepository;
  }

  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  public void setEmploymentRepository(EmploymentRepository employmentRepository) {
    this.employmentRepository = employmentRepository;
  }

  public List<String> getAllPersonsId() throws Exception {
    return this.personRepository.getAllPersonsVgrId();
  }

  public List<String> getAllUnitsHsaIdentity() throws Exception {
    return this.unitRepository.getAllUnitsHsaIdentity();
  }

  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
    return this.unitRepository.getAllUnitsHsaIdentity(showUnitsWithTheseHsaBussinessClassificationCodes);
  }

  public SikSearchResultList<Employment> getEmployments(String personDn) throws Exception {
    return employmentRepository.getEmployments(DN.createDNFromString(personDn));
  }

  public Person getPersonById(String vgrId) throws Exception {
    Person person = personRepository.getPersonByVgrId(vgrId);
    return person;
  }

  public Unit getUnitByHsaId(String hsaId) throws Exception {
    return unitRepository.getUnitByHsaId(hsaId);
  }

  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String vgrId) throws Exception {
    return personRepository.searchPersons(givenName, familyName, vgrId, 0);
  }

  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String vgrId, int maxResult) throws Exception {
    return personRepository.searchPersons(givenName, familyName, vgrId, maxResult);
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list of Persons
   * @return
   * @throws Exception
   */
  public SikSearchResultList<Person> searchPersons(String vgrId) throws Exception {
    return personRepository.searchPersons(vgrId, 0);
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list of Persons
   * @return
   * @throws Exception
   */
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxSearchResult) throws Exception {
    return personRepository.searchPersons(vgrId, maxSearchResult);
  }

  public SikSearchResultList<Unit> searchUnits(Unit unit) throws Exception {
    return unitRepository.searchUnits(unit);
  }

  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, Comparator<Unit> sortOrder) throws Exception {
    return unitRepository.searchAdvancedUnits(unit, sortOrder);
  }

  public SikSearchResultList<Unit> searchUnits(Unit unit, int maxSearchResult) throws Exception {
    return unitRepository.searchUnits(unit, maxSearchResult);
  }

  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws Exception {
    return unitRepository.searchAdvancedUnits(unit, maxSearchResult, sortOrder, showUnitsWithTheseHsaBussinessClassificationCodes);
  }

  public Unit getUnitByDN(String dn) throws Exception {
    return unitRepository.getUnitByDN(DN.createDNFromString(dn));
  }

  public HealthcareTypeConditionHelper getHealthcareTypeConditionHelper() {
    return healthcareTypeConditionHelper;
  }

  public void setHealthcareTypeConditionHelper(HealthcareTypeConditionHelper healthcareTypeConditionHelper) {
    this.healthcareTypeConditionHelper = healthcareTypeConditionHelper;
  }

  /**
   * Filters the query, prevents ldap injection.
   * 
   * @param filter
   * @see http://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
   * @return escaped ldap search filter
   */
  public static final String escapeLDAPSearchFilter(String filter) {
    if (filter == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filter.length(); i++) {
      char curChar = filter.charAt(i);
      switch (curChar) {
        case '\\':
          sb.append("\\5c");
          break;
        case '*':
          sb.append("\\2a");
          break;
        case '(':
          sb.append("\\28");
          break;
        case ')':
          sb.append("\\29");
          break;
        case '\u0000':
          sb.append("\\00");
          break;
        default:
          sb.append(curChar);
      }
    }
    return sb.toString();
  }

  public List<Employment> getEmploymentsForPerson(Person person) throws Exception {
    SikSearchResultList<Person> personWithEmployments = personRepository.searchPersons(person.getVgrId(), 0);
    List<Employment> employments = new ArrayList<Employment>();
    if (personWithEmployments.size() > 0) {
      employments = personWithEmployments.get(0).getEmployments();
    }
    return employments;
  }

  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws Exception {
    Unit u = null;
    if (units.size() > 0) {
      u = units.get(0);
    }
    return personRepository.getAllPersonsInUnit(u.getHsaIdentity(), maxResult);
  }

  public SikSearchResultList<Person> searchPersonsByDn(String dn) throws Exception {
    List persons = personRepository.searchPersonsByDn(dn);
    SikSearchResultList<Person> personsSearchList = new SikSearchResultList<Person>(persons);
    return personsSearchList;
  }

  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws Exception {
    List persons = personRepository.searchPersonsByDn(dn, maxSearchResult);
    SikSearchResultList<Person> personsSearchList = new SikSearchResultList<Person>(persons);
    return personsSearchList;
  }

  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws Exception {
    throw new NotImplementedException("Not used by LTH.");
  }
}