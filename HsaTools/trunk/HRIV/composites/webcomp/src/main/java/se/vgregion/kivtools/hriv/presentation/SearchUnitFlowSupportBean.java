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

package se.vgregion.kivtools.hriv.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.presentation.comparators.UnitCareTypeNameComparator;
import se.vgregion.kivtools.hriv.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;
import se.vgregion.kivtools.search.util.LogUtils;
import se.vgregion.kivtools.search.util.PagedSearchMetaDataHelper;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author hangy2, Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
public class SearchUnitFlowSupportBean implements Serializable {
  private static final String CLASS_NAME = SearchUnitFlowSupportBean.class.getName();
  private static final long serialVersionUID = 1L;

  private final Log logger = LogFactory.getLog(this.getClass());

  private SearchService searchService;
  private UnitCacheServiceImpl unitCacheService;
  private UnitSearchStrategy unitSearchStrategy;

  private int pageSize;
  private int maxSearchResult;

  private String googleMapsKey;

  private int meters;
  private boolean onlyPublicUnits;

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setUnitCacheService(UnitCacheServiceImpl unitCacheService) {
    this.unitCacheService = unitCacheService;
  }

  public void setUnitSearchStrategy(UnitSearchStrategy unitSearchStrategy) {
    this.unitSearchStrategy = unitSearchStrategy;
  }

  /**
   * Getter for the number of meters that denotes close units.
   * 
   * @return The number of meters that denotes close units.
   */
  public int getMeters() {
    return this.meters;
  }

  /**
   * Setter for the number of meters that denotes close units.
   * 
   * @param meters The number of meter that denotes close units.
   */
  public void setMeters(int meters) {
    this.meters = meters;
  }

  public void setOnlyPublicUnits(boolean onlyPublicUnits) {
    this.onlyPublicUnits = onlyPublicUnits;
  }

  /**
   * Getter for the Google Maps key to use.
   * 
   * @return The Google Maps key to use.
   */
  public String getGoogleMapsKey() {
    return this.googleMapsKey;
  }

  /**
   * Setter for the Google Maps key to use.
   * 
   * @param googleMapsKey The Google Maps key to use.
   */
  public void setGoogleMapsKey(String googleMapsKey) {
    this.googleMapsKey = googleMapsKey;
  }

  /**
   * Sets the maximum number of search results to return.
   * 
   * @param maxSearchResult The maximum number of search results to return.
   */
  public void setMaxSearchResult(int maxSearchResult) {
    this.maxSearchResult = maxSearchResult;
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
    this.logger.debug(CLASS_NAME + ".cleanSearchSimpleForm()");
    theForm.setMunicipality("");
    theForm.setUnitName("");
    theForm.setHealthcareType("");
  }

  /**
   * Searches for units by the criterias specified in the provided form.
   * 
   * @param theForm The form with the search criterias.
   * @return A list of matching units.
   * @throws KivException If there are any problems during the search.
   */
  public SikSearchResultList<Unit> doSearch(UnitSearchSimpleForm theForm) throws KivException {
    this.logger.debug(CLASS_NAME + ".doSearch()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Unit> list = new SikSearchResultList<Unit>();

      if (!theForm.isEmpty()) {
        Comparator<Unit> sortOrder = this.evaluateSortOrder(theForm);
        int effectiveMaxSearchResult = this.maxSearchResult;
        if ("true".equals(theForm.getShowAll())) {
          effectiveMaxSearchResult = Integer.MAX_VALUE;
        }

        list = this.unitSearchStrategy.performSearch(theForm, sortOrder, effectiveMaxSearchResult, this.searchService, this.onlyPublicUnits);
      }

      // stop measurement
      overAllTime.stop();

      LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, this.logger, list);
      if (list.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return list;
    } catch (NoConnectionToServerException e) {
      throw e;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      this.logger.debug(e.getMessage(), e);
      return new SikSearchResultList<Unit>();
    }
  }

  /**
   * Evaluate user input and match to an appropriate unit comparator.
   * 
   * @param theForm
   * @return
   * @throws KivException
   */
  private Comparator<Unit> evaluateSortOrder(UnitSearchSimpleForm theForm) throws KivException {
    Comparator<Unit> sortOrder = null;
    String s = theForm.getSortOrder();
    if (s.trim().equalsIgnoreCase("UNIT_NAME")) {
      sortOrder = new UnitNameComparator();
    } else if (s.trim().equalsIgnoreCase("CARE_TYPE_NAME")) {
      sortOrder = new UnitCareTypeNameComparator();
    } else {
      throw new KivException("Unknown sort order specified. MethodName=" + this.getClass().getName() + "::doSearch(...)");
    }
    return sortOrder;
  }

  /**
   * Gets a list of the HsaIdentities of all units.
   * 
   * @param showFilteredByHsaBusinessClassificationCode True if the result list should be filtered by HsaBusinessClassificationCode, otherwise false.
   * @return A list of the HsaIdentities of all units.
   * @throws KivNoDataFoundException If no result was found
   */
  public List<String> getAllUnitsHsaIdentity(boolean showFilteredByHsaBusinessClassificationCode) throws KivNoDataFoundException {
    List<String> allUnits;
    try {
      if (showFilteredByHsaBusinessClassificationCode) {
        allUnits = this.searchService.getAllUnitsHsaIdentity(this.onlyPublicUnits);
      } else {
        allUnits = this.searchService.getAllUnitsHsaIdentity();
      }
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      this.logger.debug(e.getMessage(), e);
      allUnits = new ArrayList<String>();
    }

    return allUnits;
  }

  /**
   * Gets a list of the HsaIdentities of all units.
   * 
   * @return A list of the HsaIdentities of all units.
   * @throws KivNoDataFoundException If no result was found
   */
  public List<String> getAllUnitsHsaIdentity() throws KivNoDataFoundException {
    return this.getAllUnitsHsaIdentity(false);
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

    List<String> list = this.getAllUnitsHsaIdentity(true);
    if (startIndex < 0 || startIndex > endIndex || endIndex > list.size() - 1) {
      this.logger.error("getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + "), Error input parameters are wrong (result list size=" + list.size() + ")");
    } else {
      for (int position = startIndex; position <= endIndex; position++) {
        result.add(list.get(position));
      }
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
    List<String> unitHsaIdList = this.getAllUnitsHsaIdentity(true);
    if (StringUtil.isInteger(pageSizeString)) {
      int temp = Integer.parseInt(pageSizeString);
      if (temp > this.pageSize) {
        // we can only increase the page size
        this.pageSize = temp;
      }
    }
    return PagedSearchMetaDataHelper.buildPagedSearchMetaData(unitHsaIdList, this.pageSize);
  }

  /**
   * Gets a list of units which are considered to be close to the address in the provided form.
   * 
   * @param form The form with the address to get close units for.
   * @return A list of Units that is within the distance of the address in the provided form.
   */
 
  //geoGoogle has deprecated
  /* public SikSearchResultList<Unit> getCloseUnits(DisplayCloseUnitsSimpleForm form) {
    SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
    List<Unit> units = this.unitCacheService.getCache().getUnits();
    if (units.isEmpty()) {
      // Units are not set, probably because the unit population is not finished yet.
      return result;
    }

    GeoUtil geoUtil = new GeoUtil();
    ArrayList<Unit> closeUnits = geoUtil.getCloseUnits(form.getAddress(), units, this.meters, this.googleMapsKey);
    result.addAll(closeUnits);
    result.setTotalNumberOfFoundItems(closeUnits.size());

    return result;
  }
  
  */
}
