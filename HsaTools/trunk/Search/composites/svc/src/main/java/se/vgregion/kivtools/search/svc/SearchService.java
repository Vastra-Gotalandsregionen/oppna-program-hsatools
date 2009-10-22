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
package se.vgregion.kivtools.search.svc;

import java.util.Comparator;
import java.util.List;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;

/**
 * @author Hans and Anders
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public interface SearchService {
  /**
   * @param unit
   * @param maxSearchResult OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is considered to be in AdditionalInfo
   * 
   * @return A List of Units
   */
  public SikSearchResultList<Unit> searchUnits(Unit unit, int maxSearchResult) throws KivException;

  /**
   * @param unit
   * @param maxSearchResult OBS! for parameters of type Address (e.g. hsaPostalAddress) the criteria is considered to be in AdditionalInfo
   * 
   * @return A List of Units
   */
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException;

  /**
   * @param hsaId hsaId for a Unit
   * @return the corresponding Unit
   * @throws KivException
   */
  public Unit getUnitByHsaId(String hsaId) throws KivException;

  /**
   * @param dn dn for a Unit
   * @return the corresponding Unit
   * @throws KivException
   */
  public Unit getUnitByDN(String dn) throws KivException;

  /**
   * 
   * @param parentUnit
   * @return A list of sub units to the parent unit
   * @throws KivException
   */
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException;

  /**
   * @param dn is the distinguished name of the organizational unit that the person works for.
   * @param maxSearchResult max number of returned items in the result list
   * @return A list of matching persons.
   * @throws KivException
   */
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException;

  /**
   * @param id is complete or part of a person identifier. That is why this method can return a list. E.g. in case of VGR the id is a vgrId
   * @param maxSearchResult max number of returned items in the result list
   * @return A list of matching persons.
   * @throws KivException
   */
  public SikSearchResultList<Person> searchPersons(String id, int maxSearchResult) throws KivException;

  /**
   * Searches for persons matching information in the provided person object.
   * 
   * @param person Object containing the information to search for.
   * @param maxResult Max number of returned items in the result list.
   * @return A list of matching persons.
   * @throws KivException If no data is found.
   */
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException;

  /**
   * @param id person id for the Person (E.g vgrId in case of VGR)
   * @return The Person matching the search criteria (must be exactly one)
   * @throws KivException
   */
  public Person getPersonById(String id) throws KivException;

  /**
   * @param personDn Distinguished name for a Person
   * @return The Person matching the personDn
   * @throws KivException
   */
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException;

  /**
   * Returns a list with hsaIdentities of all Units and functions.
   * 
   * @return A list of hsaIdentities.
   * @throws KivException
   */
  public List<String> getAllUnitsHsaIdentity() throws KivException;

  /**
   * Returns a list with hsaIdentities of all Units and functions filtered with showUnitsWithTheseHsaBussinessClassificationCodes.
   * 
   * @param showUnitsWithTheseHsaBussinessClassificationCodes
   * @return A list of hsaIdentities.
   * @throws KivException
   */
  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException;

  /**
   * Returns a list of unique identifiers for all persons. E.g. vgrId in case of VGR
   * 
   * @return A list of unique identifiers for all persons.
   * @throws KivException
   */
  public List<String> getAllPersonsId() throws KivException;

  /**
   * Gets a list of employments for the provided person.
   * 
   * @param person The person to get employments for.
   * @return A list of employments.
   * @throws KivException If something goes wrong.
   */
  public List<Employment> getEmploymentsForPerson(Person person) throws KivException;

  /**
   * Returns a list of all persons employed at unit(s).
   * 
   * @param units
   * @return A list of persons employed at the provided units.
   * @throws KivException
   */
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException;
}