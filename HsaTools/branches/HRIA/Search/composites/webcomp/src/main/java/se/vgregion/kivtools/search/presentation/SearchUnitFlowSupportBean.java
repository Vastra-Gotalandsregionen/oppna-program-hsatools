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
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.util.Evaluator;
import se.vgregion.kivtools.search.util.LogUtils;
import se.vgregion.kivtools.search.util.PagedSearchMetaDataHelper;
import se.vgregion.kivtools.search.util.geo.GeoUtil;

/**
 * Support bean for the Search Unit flow.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class SearchUnitFlowSupportBean implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final String CLASS_NAME = SearchUnitFlowSupportBean.class.getName();
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
   * Cleans the search form.
   * 
   * @param theForm The form to clean.
   */
  public void cleanSearchSimpleForm(UnitSearchSimpleForm theForm) {
    LOGGER.debug(CLASS_NAME + ".cleanSearchSimpleForm()");
    theForm.setSearchParamValue("");
    theForm.setUnitName("");
  }

  /**
   * Searches for units by the criterias specified in the provided form.
   * 
   * @param theForm The form with the search criterias.
   * @return A list of matching units.
   * @throws KivNoDataFoundException If no units were found with the provided criterias.
   * @throws NoConnectionToServerException If no connection to the LDAP server could be made.
   */
  public SikSearchResultList<Unit> doSearch(UnitSearchSimpleForm theForm) throws KivNoDataFoundException, NoConnectionToServerException {
    LOGGER.debug(CLASS_NAME + ".doSearch()");

    SikSearchResultList<Unit> list = null;
    try {
      TimeMeasurement overAllTime = new TimeMeasurement();

      // start measurement
      overAllTime.start();
      if (!theForm.isEmpty()) {
        Unit u = mapSearchCriteriaToUnit(theForm);
        list = getSearchService().searchUnits(u, maxSearchResult);
      }
      // stop measurement
      overAllTime.stop();

      if (list == null) {
        list = new SikSearchResultList<Unit>();
      }
      LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, LOGGER, list);
      if (list.size() == 0) {
        throw new KivNoDataFoundException();
      }
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      LOGGER.error(e);
      list = new SikSearchResultList<Unit>();
    }
    return list;
  }

  /**
   * Gets a list of the HsaIdentities of all units.
   * 
   * @return A list of the HsaIdentities of all units.
   * @throws KivNoDataFoundException If no result was found
   */
  public List<String> getAllUnitsHsaIdentity() throws KivNoDataFoundException {
    List<String> result;
    try {
      result = getSearchService().getAllUnitsHsaIdentity();
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      LOGGER.error(e);
      result = new ArrayList<String>();
    }
    return result;
  }

  /**
   * Return a list of hsaIds corresponding to startIndex->endIndex of units.
   * 
   * @param startIndex The index of the first unit in the list to return.
   * @param endIndex The index of the last unit in the list to return.
   * @return A list of HsaIdentities which is a sub-list of the complete list of id's of all units.
   * @throws KivNoDataFoundException If no result was found.
   */
  public List<String> getRangeUnitsPageList(Integer startIndex, Integer endIndex) throws KivNoDataFoundException {
    List<String> result = new ArrayList<String>();
    try {
      List<String> list = getSearchService().getAllUnitsHsaIdentity();
      if (startIndex < 0 || startIndex > endIndex || endIndex > list.size() - 1) {
        throw new SikInternalException(this, "getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + ")", "Error input parameters are wrong (result list size=" + list.size()
            + ")");
      }
      for (int position = startIndex; position <= endIndex; position++) {
        result.add(list.get(position));
      }
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      LOGGER.error(e);
      result = new ArrayList<String>();
    }
    return result;
  }

  /**
   * Return a list of PagedSearchMetaData objects which chops up the full list in to minor chunks. Used in case of indexing all units.
   * 
   * @param pageSizeString The number of search results to show per page.
   * @return A list of PagedSearchMetaData objects.
   * @throws KivNoDataFoundException If no result was found.
   */
  public List<PagedSearchMetaData> getAllUnitsPageList(String pageSizeString) throws KivNoDataFoundException {
    List<PagedSearchMetaData> result;
    try {
      List<String> unitHsaIdList = getSearchService().getAllUnitsHsaIdentity();
      if (Evaluator.isInteger(pageSizeString)) {
        int temp = Integer.parseInt(pageSizeString);
        if (temp > pageSize) {
          // we can only increase the page size
          pageSize = temp;
        }
      }
      result = PagedSearchMetaDataHelper.buildPagedSearchMetaData(unitHsaIdList, pageSize);
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      LOGGER.error(e);
      result = new ArrayList<PagedSearchMetaData>();
    }
    return result;
  }

  /**
   * Geocode all units to RT90.
   * 
   * @param googleKey Google Maps key.
   * @return List with units with coordinates.
   * @throws KivNoDataFoundException If no result was found.
   */
  public List<Unit> getAllUnitsGeocoded(String googleKey) throws KivNoDataFoundException {
    List<String> allUnitsHsaIdentity = getAllUnitsHsaIdentity();
    List<Unit> allUnitsWithPositionInfo = new ArrayList<Unit>();
    GeoUtil geoUtil = new GeoUtil();
    try {
      for (String hsaId : allUnitsHsaIdentity) {
        // Get coordinates from Google.
        Unit u = getSearchService().getUnitByHsaId(hsaId);
        if (u != null) {
          if (u.getHsaStreetAddressIsValid()) {
            int[] rt90Coordinates = geoUtil.geocodeToRT90(u.getHsaStreetAddress(), googleKey);
            if (rt90Coordinates != null) {
              u.setRt90X(rt90Coordinates[0]);
              u.setRt90Y(rt90Coordinates[1]);
            } else {
              u.setRt90X(-1);
              u.setRt90Y(-1);
            }

            allUnitsWithPositionInfo.add(u);
          }
        }
      }
      return allUnitsWithPositionInfo;
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      LOGGER.error(e);
      return new ArrayList<Unit>();
    }
  }

  /**
   * Gets all sub units for the unit with the provided HsaIdentity.
   * 
   * @param parentHsaId The HsaIdentity of the unit to find sub units for.
   * @return A list of all sub units for the unit with the provided HsaIdentity.
   */
  public SikSearchResultList<Unit> getSubUnits(String parentHsaId) {
    SikSearchResultList<Unit> subUnits = new SikSearchResultList<Unit>();
    try {
      Unit parentUnit = getSearchService().getUnitByHsaId(parentHsaId);
      subUnits = getSearchService().getSubUnits(parentUnit, maxSearchResult);
    } catch (Exception e) {
      LOGGER.error(e);
    }
    return subUnits;
  }

  private Unit mapSearchCriteriaToUnit(UnitSearchSimpleForm theForm) throws Exception {
    final String methodName = CLASS_NAME + ".mapSearchCriteriaToUnit(...)";
    LOGGER.debug(methodName);
    Unit unit = new Unit();

    // unit name
    unit.setName(theForm.getUnitName());

    // hsaStreetAddress
    List<String> list = new ArrayList<String>();
    list.add(theForm.getSearchParamValue());
    unit.setHsaStreetAddress(AddressHelper.convertToAddress(list));

    // hsaPostalAddress
    list = new ArrayList<String>();
    list.add(theForm.getSearchParamValue());
    Address adress = new Address();
    // we stuff in the text in the additionalInfo
    adress.setAdditionalInfo(list);
    unit.setHsaPostalAddress(adress);

    // hsaMunicipalityName
    unit.setHsaMunicipalityName(theForm.getSearchParamValue());
    return unit;
  }
}
