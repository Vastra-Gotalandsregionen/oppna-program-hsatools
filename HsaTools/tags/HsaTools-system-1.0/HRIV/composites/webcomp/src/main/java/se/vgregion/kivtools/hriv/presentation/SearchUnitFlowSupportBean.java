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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.presentation.comparators.UnitCareTypeNameComparator;
import se.vgregion.kivtools.hriv.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.UnitCacheServiceImpl;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
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

  private Log logger = LogFactory.getLog(this.getClass());

  private SearchService searchService;
  private UnitCacheServiceImpl unitCacheService;

  private int pageSize;
  private int maxSearchResult;

  private String googleMapsKey;

  private int meters;

  private List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes = new ArrayList<Integer>();

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setUnitCacheService(UnitCacheServiceImpl unitCacheService) {
    this.unitCacheService = unitCacheService;
  }

  /**
   * Getter for the number of meters that denotes close units.
   * 
   * @return The number of meters that denotes close units.
   */
  public int getMeters() {
    return meters;
  }

  /**
   * Setter for the number of meters that denotes close units.
   * 
   * @param meters The number of meter that denotes close units.
   */
  public void setMeters(int meters) {
    this.meters = meters;
  }

  /**
   * Getter for the Google Maps key to use.
   * 
   * @return The Google Maps key to use.
   */
  public String getGoogleMapsKey() {
    return googleMapsKey;
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
    logger.debug(CLASS_NAME + ".cleanSearchSimpleForm()");
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
    logger.debug(CLASS_NAME + ".doSearch()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Unit> list = new SikSearchResultList<Unit>();

      if (!theForm.isEmpty()) {
        Unit u = mapSearchCriteriaToUnit(theForm);

        Comparator<Unit> sortOrder = evaluateSortOrder(theForm);
        int effectiveMaxSearchResult = maxSearchResult;
        if ("true".equals(theForm.getShowAll())) {
          effectiveMaxSearchResult = Integer.MAX_VALUE;
        }
        list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, showUnitsWithTheseHsaBusinessClassificationCodes);

        // No hits with complete criterions. Try again but with cleaned unit name this time
        if (list.size() == 0 && !StringUtil.isEmpty(theForm.getUnitName())) {
          u.setName(cleanUnitName(theForm.getUnitName()));
          list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, showUnitsWithTheseHsaBusinessClassificationCodes);
        }

        // Still no hits. Try again but with only the cleaned unit name this time if the user forgot to remove any care type or municipality selection.
        if (list.size() == 0 && lessSpecifiedSearchPossible(theForm)) {
          u = new Unit();
          u.setName(cleanUnitName(theForm.getUnitName()));
          list = searchService.searchAdvancedUnits(u, effectiveMaxSearchResult, sortOrder, showUnitsWithTheseHsaBusinessClassificationCodes);
        }
      }

      // stop measurement
      overAllTime.stop();

      LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, logger, list);
      if (list.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return list;
    } catch (NoConnectionToServerException e) {
      throw e;
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      logger.debug(e.getMessage(), e);
      return new SikSearchResultList<Unit>();
    }
  }

  /**
   * Checks if it's possible to perform a less specified search by analysing the search criterions.
   * 
   * @param theForm The form-instance containing the search criterions.
   * @return True if a less specified search is possible, otherwise false.
   */
  private boolean lessSpecifiedSearchPossible(UnitSearchSimpleForm theForm) {
    boolean result = false;

    // A less specified search is possible

    // If any of healthcare type and municipality was filled
    result |= !StringUtil.isEmpty(theForm.getHealthcareType());
    result |= !StringUtil.isEmpty(theForm.getMunicipality());

    // And unit name was filled
    result &= !StringUtil.isEmpty(theForm.getUnitName());

    return result;
  }

  private String cleanUnitName(String unitName) {
    String cleanedName = unitName;
    int lastComma = cleanedName.lastIndexOf(",");
    if (lastComma != -1) {
      cleanedName = cleanedName.substring(0, lastComma);
    }

    return cleanedName;
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
        allUnits = searchService.getAllUnitsHsaIdentity(showUnitsWithTheseHsaBusinessClassificationCodes);
      } else {
        allUnits = searchService.getAllUnitsHsaIdentity();
      }
    } catch (KivNoDataFoundException e) {
      throw e;
    } catch (KivException e) {
      logger.debug(e.getMessage(), e);
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
    return getAllUnitsHsaIdentity(false);
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

    List<String> list = getAllUnitsHsaIdentity(true);
    if (startIndex < 0 || startIndex > endIndex || endIndex > list.size() - 1) {
      logger.error("getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + "), Error input parameters are wrong (result list size=" + list.size() + ")");
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
    List<String> unitHsaIdList = getAllUnitsHsaIdentity(true);
    if (StringUtil.isInteger(pageSizeString)) {
      int temp = Integer.parseInt(pageSizeString);
      if (temp > pageSize) {
        // we can only increase the page size
        pageSize = temp;
      }
    }
    return PagedSearchMetaDataHelper.buildPagedSearchMetaData(unitHsaIdList, pageSize);
  }

  private Unit mapSearchCriteriaToUnit(UnitSearchSimpleForm theForm) {
    logger.debug(CLASS_NAME + ".mapSearchCriteriaToUnit(...)");
    Unit unit = new Unit();

    // unit name
    unit.setName(theForm.getUnitName());
    // hsaStreetAddress
    List<String> list = new ArrayList<String>();
    list.add(theForm.getMunicipality());
    unit.setHsaStreetAddress(AddressHelper.convertToAddress(list));

    // hsaPostalAddress
    list = new ArrayList<String>();
    list.add(theForm.getMunicipality());
    Address adress = new Address();
    // we stuff in the text in the additionalInfo
    adress.setAdditionalInfo(list);
    unit.setHsaPostalAddress(adress);

    // hsaMunicipalityCode
    unit.setHsaMunicipalityCode(theForm.getMunicipality());

    // Assign health care types
    Integer healthcareTypeIndex;
    try {
      healthcareTypeIndex = Integer.parseInt(theForm.getHealthcareType());
    } catch (NumberFormatException nfe) {
      // No health care type was chosen.
      healthcareTypeIndex = null;
    }
    if (healthcareTypeIndex != null) {
      HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
      HealthcareType ht = htch.getHealthcareTypeByIndex(healthcareTypeIndex);
      List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
      if (ht != null) {
        healthcareTypes.add(ht);
      }
      unit.setHealthcareTypes(healthcareTypes);
      // If vardcental is chosen as healthcare type, then only units with vgrVardval should be search for.
      if (unit.getHealthcareTypes().contains(htch.getHealthcareTypeByName("Vårdcentral"))) {
        unit.setVgrVardVal(true);
      }
    }

    return unit;
  }

  /**
   * Gets a list of units which are considered to be close to the address in the provided form.
   * 
   * @param form The form with the address to get close units for.
   * @return A list of Units that is within the distance of the address in the provided form.
   */
  public ArrayList<Unit> getCloseUnits(DisplayCloseUnitsSimpleForm form) {
    ArrayList<Unit> closeUnits = new ArrayList<Unit>();
    List<Unit> units = unitCacheService.getCache().getUnits();
    if (units.isEmpty()) {
      // Units are not set, probably because the unit population is not finished yet.
      return closeUnits;
    }

    GeoUtil geoUtil = new GeoUtil();
    closeUnits = geoUtil.getCloseUnits(form.getAddress(), units, meters, googleMapsKey);
    return closeUnits;
  }

  /**
   * Setter for the HsaBusinessClassificationCodes to show.
   * 
   * @param showUnitsWithTheseHsaBusinessClassificationCodes A comma-separated string of HsaBusinessClassificationCodes to show.
   */
  public void setShowUnitsWithTheseHsaBusinessClassificationCodes(String showUnitsWithTheseHsaBusinessClassificationCodes) {
    List<String> tempList = Arrays.asList(showUnitsWithTheseHsaBusinessClassificationCodes.split(","));
    for (String id : tempList) {
      if (id.length() > 0) {
        this.showUnitsWithTheseHsaBusinessClassificationCodes.add(Integer.parseInt(id));
      }
    }
  }
}
