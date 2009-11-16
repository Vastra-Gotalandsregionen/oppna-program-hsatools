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
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
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
    return searchService;
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
      SearchPersonCriterions searchPersonCriterion = createSearchPersonCriterion(theForm);
      if (!searchPersonCriterion.isEmpty()) {
        // perform a search
        list = getSearchService().searchPersons(searchPersonCriterion, maxSearchResult);

        // fetch all employments
        // Not done this way in HAK implementation. Employment info is on person entry.
        SikSearchResultList<Employment> empList = null;
        for (Person pers : list) {
          empList = getSearchService().getEmployments(pers.getDn());
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
        SikSearchResultList<Person> personsWithoutEmploymentsList = getSearchService().searchPersonsByDn(dn, maxSearchResult);
        for (Person p : personsWithoutEmploymentsList) {
          SikSearchResultList<Person> searchPersons = getSearchService().searchPersons(p.getVgrId(), maxSearchResult);
          persons.addAll(searchPersons);
        }
      } else {
        persons = getPersonsForUnitsRecursive(hsaIdentity);

        // fetch all employments
        SikSearchResultList<Employment> empList = null;
        for (Person pers : persons) {
          empList = getSearchService().getEmployments(pers.getDn());
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
      List<String> listOfVgrIds = getSearchService().getAllPersonsId();
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
    List<String> list = getAllPersonsVgrId();
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
      List<String> personVgrIdList = getSearchService().getAllPersonsId();
      if (StringUtil.isInteger(pageSizeString)) {
        int temp = Integer.parseInt(pageSizeString);
        if (temp > pageSize) {
          // we can only increase the page size
          pageSize = temp;
        }
      }
      result = PagedSearchMetaDataHelper.buildPagedSearchMetaData(personVgrIdList, pageSize);
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
      Unit parentUnit = getSearchService().getUnitByHsaId(hsaIdentity);
      SikSearchResultList<Unit> subUnits = getSearchService().getSubUnits(parentUnit, maxSearchResult);
      // Add parent to list
      subUnits.add(parentUnit);
      persons = getSearchService().getPersonsForUnits(subUnits, maxSearchResult);
    } catch (KivException e) {
      LOGGER.error(e);
    }
    return persons;
  }
}
