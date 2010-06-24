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
 */
package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import java.util.Comparator;
import java.util.List;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

/**
 *
 */
public class SearchServiceMockBase implements SearchService {

  @Override
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxSearchResult) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxSearchResult, Comparator<Unit> sortOrder, boolean onlyPublicUnits) throws KivException {
    return null;
  }

  @Override
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    return null;
  }

  @Override
  public Unit getUnitByDN(String dn) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Unit> getSubUnits(Unit parentUnit, int maxSearchResult) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Person> searchPersonsByDn(String dn, int maxSearchResult) throws KivException {
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
  public Person getPersonById(String id) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Employment> getEmployments(String personDn) throws KivException {
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
  public List<Unit> getAllUnits(boolean onlyPublicUnits) throws KivException {
    return null;
  }

  @Override
  public List<String> getAllPersonsId() throws KivException {
    return null;
  }

  @Override
  public List<Person> getAllPersons() throws KivException {
    return null;
  }

  @Override
  public List<Employment> getEmploymentsForPerson(Person person) throws KivException {
    return null;
  }

  @Override
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    return null;
  }

  @Override
  public Person getPersonByDn(String personDn) throws KivException {
    return null;
  }

  @Override
  public byte[] getProfileImageByDn(String dn) throws KivException {
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
}
