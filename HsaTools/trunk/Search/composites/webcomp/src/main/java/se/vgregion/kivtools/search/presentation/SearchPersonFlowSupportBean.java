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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.comparators.PersonNameWeightedComparator;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.util.LogUtils;
import se.vgregion.kivtools.search.util.PagedSearchMetaDataHelper;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Support bean for the Search Person flow.
 * 
 * @author Anders Asplund - KnowIt
 */
public class SearchPersonFlowSupportBean implements Serializable {
  private static final long serialVersionUID = -6525334748535093644L;
  private static final String CLASS_NAME = SearchPersonFlowSupportBean.class.getName();
  private static final Log LOGGER = LogFactory.getLog(SearchPersonFlowSupportBean.class);
  private SearchService searchService;
  private int pageSize;
  private int maxSearchResult;

  /**
   * Sets the maximum number of search results to return.
   * 
   * @param maxSearchResult The maximum number of search results to return.
   */
  public void setMaxSearchResult(int maxSearchResult) {
    this.maxSearchResult = maxSearchResult;
  }

  /**
   * Getter for the SearchService to use.
   * 
   * @return The SearchService to use.
   */
  public SearchService getSearchService() {
    return this.searchService;
  }

  /**
   * Setter for the SearchService to use.
   * 
   * @param searchService The SearchService to use.
   */
  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Sets the number of search results to show per page.
   * 
   * @param pageSize The number of search results to show per page.
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Searches for persons by the criterias specified in the provided form.
   * 
   * @param theForm The form with the search criterias.
   * @return A list of matching persons.
   * @throws KivNoDataFoundException If no persons were found with the provided criterias.
   */
  public SikSearchResultList<Person> doSearch(PersonSearchSimpleForm theForm) throws KivNoDataFoundException {
    LOGGER.debug(CLASS_NAME + ".doSearch()");

    try {
      SikSearchResultList<Person> list = new SikSearchResultList<Person>();
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SearchPersonCriterions searchPersonCriterion = this.createSearchPersonCriterion(theForm);
      if (!searchPersonCriterion.isEmpty()) {

        int currentMaxSearchResult = this.maxSearchResult;
        if ("true".equals(theForm.getShowAll())) {
          currentMaxSearchResult = Integer.MAX_VALUE;
        }
        // perform a search
        list = this.getSearchService().searchPersons(searchPersonCriterion, currentMaxSearchResult);

        // fetch all employments
        // Not done this way in HAK implementation. Employment info is on person entry.
        SikSearchResultList<Employment> empList = null;
        for (Person pers : list) {
          empList = this.getSearchService().getEmployments(pers.getDn());
          if (!empList.isEmpty()) {
            pers.setEmployments(empList);
            // add the datasource time for fetching employments
            list.addDataSourceSearchTime(new TimeMeasurement(empList.getTotalDataSourceSearchTimeInMilliSeconds()));
          }
        }
        // stop measurement
        overAllTime.stop();

        LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, LOGGER, list);
        if (list.size() == 0) {
          throw new KivNoDataFoundException();
        } else {
          Collections.sort(list, new PersonNameWeightedComparator(searchPersonCriterion.getGivenName(), searchPersonCriterion.getSurname()));
        }
      }
      return list;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new SikSearchResultList<Person>();
    }
  }

  private SearchPersonCriterions createSearchPersonCriterion(PersonSearchSimpleForm personSearchSimpleForm) {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName(personSearchSimpleForm.getGivenName());
    searchPersonCriterion.setSurname(personSearchSimpleForm.getSurname());
    searchPersonCriterion.setUserId(personSearchSimpleForm.getUserId());
    if (!"simple".equals(personSearchSimpleForm.getSearchType())) {
      searchPersonCriterion.setAdministration(personSearchSimpleForm.getAdministration());
      searchPersonCriterion.setEmail(personSearchSimpleForm.getEmail());
      searchPersonCriterion.setSpecialityArea(personSearchSimpleForm.getSpecialityArea());
      searchPersonCriterion.setEmploymentTitle(personSearchSimpleForm.getEmploymentTitle());
      searchPersonCriterion.setLanguageKnowledge(personSearchSimpleForm.getLanguageKnowledge());
      searchPersonCriterion.setProfession(personSearchSimpleForm.getProfession());
      searchPersonCriterion.setEmployedAtUnit(personSearchSimpleForm.getEmployedAtUnit());
      searchPersonCriterion.setPhone(personSearchSimpleForm.getPhone());
      searchPersonCriterion.setEmploymentPosition(personSearchSimpleForm.getEmploymentPosition());
      searchPersonCriterion.setDescription(personSearchSimpleForm.getDescription());
    }
    return searchPersonCriterion;
  }

  /**
   * Gets all persons in the organisation from the unit with the provided hsaIdentity and it's sub units or all persons matching the provided dn.
   * 
   * @param hsaIdentity The hsaIdentity of the root unit.
   * @param dn The dn to search for if no hsaIdentity is provided.
   * @return A list of persons.
   * @throws KivNoDataFoundException If no matching persons are found.
   */
  public SikSearchResultList<Person> getOrganisation(String hsaIdentity, String dn) throws KivNoDataFoundException {
    LOGGER.debug(CLASS_NAME + ".getOrganisation()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Person> persons = new SikSearchResultList<Person>();

      if (StringUtil.isEmpty(hsaIdentity)) {
        SikSearchResultList<Person> personsWithoutEmploymentsList = this.getSearchService().searchPersonsByDn(dn, this.maxSearchResult);
        for (Person p : personsWithoutEmploymentsList) {
          SikSearchResultList<Person> searchPersons = this.getSearchService().searchPersons(p.getVgrId(), this.maxSearchResult);
          persons.addAll(searchPersons);
        }
      } else {
        persons = this.getPersonsForUnitsRecursive(hsaIdentity);

        // fetch all employments
        SikSearchResultList<Employment> empList = null;
        for (Person pers : persons) {
          empList = this.getSearchService().getEmployments(pers.getDn());
          pers.setEmployments(empList);
        }
      }

      // stop measurement
      overAllTime.stop();

      LogUtils.printSikSearchResultListToLog(this, "getOrganisation", overAllTime, LOGGER, persons);
      if (persons.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return persons;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new SikSearchResultList<Person>();
    }
  }

  /**
   * Gets a list of the id's of all persons.
   * 
   * @return A list of the id's of all persons.
   * @throws KivNoDataFoundException If no result was found
   */
  public List<String> getAllPersonsVgrId() throws KivNoDataFoundException {
    try {
      List<String> listOfVgrIds = this.getSearchService().getAllPersonsId();
      return listOfVgrIds;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new ArrayList<String>();
    }
  }

  /**
   * Return a list of vgrIds corresponding to startIndex->endIndex of persons.
   * 
   * @param startIndex The index of the first person in the list to return.
   * @param endIndex The index of the last person in the list to return.
   * @return A list of vgrIds which is a sub-list of the complete list of id's of all persons.
   * @throws KivNoDataFoundException If no result was found.
   */
  public List<String> getRangePersonsVgrIdPageList(Integer startIndex, Integer endIndex) throws KivNoDataFoundException {
    List<String> result = new ArrayList<String>();
    List<String> list = this.getAllPersonsVgrId();
    if (startIndex < 0 || startIndex > endIndex) {
      LOGGER.error("getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + "), Error input parameters are wrong (result list size=" + list.size() + ")");
    } else {
      int realEndIndex = endIndex;
      if (realEndIndex > list.size() - 1) {
        // It is wrong but let's continue anyway
        LOGGER.error("MethodName=" + CLASS_NAME + "::" + "getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + realEndIndex + ") detected that endIndex > ");
        realEndIndex = list.size() - 1;
      }
      for (int position = startIndex; position <= realEndIndex; position++) {
        result.add(list.get(position));
      }
    }
    return result;
  }

  /**
   * Return a list of PagedSearchMetaData objects which chops up the full list in to minor chunks. Used in case of indexing all persons.
   * 
   * @param pageSizeString The number of search results to show per page.
   * @return A list of PagedSearchMetaData objects.
   * @throws KivNoDataFoundException If no result was found.
   */
  public List<PagedSearchMetaData> getAllPersonsVgrIdPageList(String pageSizeString) throws KivNoDataFoundException {
    List<PagedSearchMetaData> result;
    try {
      List<String> personVgrIdList = this.getSearchService().getAllPersonsId();
      if (StringUtil.isInteger(pageSizeString)) {
        int temp = Integer.parseInt(pageSizeString);
        if (temp > this.pageSize) {
          // we can only increase the page size
          this.pageSize = temp;
        }
      }
      result = PagedSearchMetaDataHelper.buildPagedSearchMetaData(personVgrIdList, this.pageSize);
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      result = new ArrayList<PagedSearchMetaData>();
    }
    return result;
  }

  /**
   * Gets all persons for a unit and it's child units.
   * 
   * @param hsaIdentity The hsaIdentity of the unit to get persons for.
   * @return A list of all persons for a unit and it's child units.
   */
  public SikSearchResultList<Person> getPersonsForUnitsRecursive(String hsaIdentity) {
    SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
    try {
      Unit parentUnit = this.getSearchService().getUnitByHsaId(hsaIdentity);
      SikSearchResultList<Unit> subUnits = this.getSearchService().getSubUnits(parentUnit, this.maxSearchResult);
      // Add parent to list
      subUnits.add(parentUnit);
      persons = this.getSearchService().getPersonsForUnits(subUnits, this.maxSearchResult);
    } catch (KivException e) {
      LOGGER.error(e);
    }
    return persons;
  }

  /**
   * Gets all persons in the organization from only this unit with the provided hsaIdentity .
   * 
   * @param hsaIdentity The hsaIdentity of the root unit.
   * @return A list of persons.
   * @throws KivNoDataFoundException If no matching persons are found.
   */
  public SikSearchResultList<Person> getOrganisation(String hsaIdentity) throws KivNoDataFoundException {
    LOGGER.debug(CLASS_NAME + ".getOrganisationOnlyOneLevel()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Person> persons = new SikSearchResultList<Person>();

      if (!StringUtil.isEmpty(hsaIdentity)) {
        Unit unit = null;
        unit = this.getSearchService().getUnitByHsaId(hsaIdentity);

        List<Unit> units = new ArrayList<Unit>();
        units.add(unit);
        persons = this.getSearchService().getPersonsForUnits(units, this.maxSearchResult);

        // fetch all employments
        SikSearchResultList<Employment> empList = null;
        for (Person pers : persons) {
          empList = this.getSearchService().getEmployments(pers.getDn());
          pers.setEmployments(empList);
        }
      }

      // stop measurement
      overAllTime.stop();

      LogUtils.printSikSearchResultListToLog(this, "getOrganisationOnlyOneLevel", overAllTime, LOGGER, persons);
      if (persons.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return persons;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new SikSearchResultList<Person>();
    }
  }

  /**
   * Gets all administrators which have adminType C, D and K in this unit.
   * 
   * @param hsaIdentity The hsaIdentity of the root unit.
   * @return A list of persons.
   * @throws KivNoDataFoundException If no matching persons are found.
   */
  public SikSearchResultList<Person> getUnitAdministrators(String hsaIdentity) throws KivNoDataFoundException {
    LOGGER.debug(CLASS_NAME + ".getUnitAdministrators()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Person> persons = new SikSearchResultList<Person>();

      if (!StringUtil.isEmpty(hsaIdentity)) {
        Unit currentUnit = this.getSearchService().getUnitByHsaId(hsaIdentity);
        if (currentUnit != null) {
          Set<DN> unitDNs = new HashSet<DN>();
          unitDNs.add(currentUnit.getDn());
          unitDNs.addAll(getAncestors(currentUnit.getDn()));

          List<Unit> units = new ArrayList<Unit>();
          for (DN dn : unitDNs) {
            units.add(this.getSearchService().getUnitByDN(dn.toString()));
          }

          Set<String> unitAdministratorVgrIds = new HashSet<String>();
          for (Unit unit : units) {
            unitAdministratorVgrIds.addAll(this.getSearchService().getUnitAdministratorVgrIds(unit.getHsaIdentity()));
          }

          // Admin types that we are interested of
          List<String> validAdminTypes = new ArrayList<String>();
          validAdminTypes.add("C");
          validAdminTypes.add("D");
          validAdminTypes.add("K");

          // Do not need to fetch employment for person, is done in SearchService impl.
          for (String id : unitAdministratorVgrIds) {
            Person administrator = this.getSearchService().getPersonById(id);
            if (this.isAdministratorAdminTypeValid(administrator, validAdminTypes)) {
              persons.add(this.getSearchService().getPersonById(id));
            }
          }
        }

      }
      // stop measurement
      overAllTime.stop();
      LogUtils.printSikSearchResultListToLog(this, "getUnitAdministrators", overAllTime, LOGGER, persons);
      if (persons.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return persons;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new SikSearchResultList<Person>();
    }
  }

  private boolean isAdministratorAdminTypeValid(Person administrator, List<String> validAdminTypes) {
    for (String adminType : validAdminTypes) {
      for (String personAdminType : administrator.getVgrAdminTypes()) {
        if (personAdminType.equalsIgnoreCase(adminType)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Retrieves all persons who are administrator for a specific person. Search performs from the current unit where this specific person belongs to and goes up to its parent units to retrieve those
   * administrator also. The administrator must have vgrAdminType=E or F.
   * 
   * @param vgrid the id of the person
   * @return A list of persons
   * @throws KivNoDataFoundException
   */
  public SikSearchResultList<Person> getAdministratorForPerson(String vgrId) throws KivNoDataFoundException {

    LOGGER.debug(CLASS_NAME + ".getAdministratorForPerson()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Person> persons = new SikSearchResultList<Person>();

      if (!StringUtil.isEmpty(vgrId)) {
        Person person = this.getSearchService().getPersonById(vgrId);

        if (person != null) {
          List<Employment> employments = person.getEmployments();
          Set<DN> unitDNs = getUnitDNs(employments);

          Set<Unit> personEmploymentUnits = new HashSet<Unit>();
          for (DN dn : unitDNs) {
            personEmploymentUnits.add(this.getSearchService().getUnitByDN(dn.toString()));
          }

          Set<String> administratorVgrID = new HashSet<String>();
          for (Unit unit : personEmploymentUnits) {
            administratorVgrID.addAll(this.getSearchService().getUnitAdministratorVgrIds(unit.getHsaIdentity()));
          }

          if (administratorVgrID != null && administratorVgrID.size() > 0) {
            // AdminTypes that we are interested of
            List<String> validAdminTypes = new ArrayList<String>();
            validAdminTypes.add("E");
            validAdminTypes.add("F");

            // Do not need to fetch employment for person, is done in SearchService impl.
            for (String id : administratorVgrID) {
              if (!person.getVgrId().equals(id)) {
                Person administrator = this.getSearchService().getPersonById(id);
                if (isAdministratorAdminTypeValid(administrator, validAdminTypes) && isUnitAdministrator(administrator, personEmploymentUnits))
                  persons.add(administrator);
              }
            }
          }
        }
      }
      // stop measurement
      overAllTime.stop();
      LogUtils.printSikSearchResultListToLog(this, "getAdministratorForPerson", overAllTime, LOGGER, persons);
      if (persons.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return persons;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      LOGGER.error(e);
      return new SikSearchResultList<Person>();
    }
  }

  /**
   * Gets DN for the current unit and its ancestors.
   * 
   * @param employments
   * @return List of unit DN
   */
  private Set<DN> getUnitDNs(List<Employment> employments) {
    Set<DN> personEmploymentUnitDNs = new HashSet<DN>();
    for (Employment employment : employments) {
      DN vgrStrukturPersonDN = employment.getVgrStrukturPerson();
      personEmploymentUnitDNs.add(vgrStrukturPersonDN);
      List<DN> ancestors = getAncestors(vgrStrukturPersonDN);
      if (ancestors != null && ancestors.size() > 0) {
        personEmploymentUnitDNs.addAll(ancestors);
      }
    }
    return personEmploymentUnitDNs;
  }

  /**
   * Check if the person is administrator for this unit
   * 
   * @param person
   * @param units
   * @return Return true if the user is administrator at least for one of the units. Else false
   */
  private boolean isUnitAdministrator(Person person, Set<Unit> units) {
    for (Unit unit : units) {
      if (person.getVgrManagedObjects().contains(unit.getDn().toString())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets a list of the DN's ancestors.
   * 
   * @param childDN the current DN of the unit.
   * @return returns a list of the DN's ancestor.
   */
  private List<DN> getAncestors(DN childDN) {
    int currentFromGeneration = 1;
    List<DN> ancestors = new ArrayList<DN>();
    DN parent = childDN.getParentDN();

    int currentPosition = 1;
    while (parent != null) {
      if (--currentFromGeneration <= 0) {
        ancestors.add(parent);
      }
      currentPosition++;
      parent = parent.getParentDN();
    }

    // Remove generations at the end
    if (ancestors.size() > 3) {
      ancestors = ancestors.subList(0, ancestors.size() - 2);
    } else if (ancestors.size() == 3) {
      ancestors = ancestors.subList(0, 1);
    } else {
      ancestors.clear();
    }

    return ancestors;
  }
}
