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
/**
 * 
 */
package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.UnitCareTypeNameComparator;
import se.vgregion.kivtools.search.svc.domain.UnitNameComparator;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.util.LogUtils;
import se.vgregion.kivtools.search.util.geo.GeoUtil;

/**
 * @author hangy2, Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
public class SearchUnitFlowSupportBean implements Serializable {

  private static final long serialVersionUID = 1L;

  Log logger = LogFactory.getLog(this.getClass());

  private static final String CLASS_NAME = SearchUnitFlowSupportBean.class.getName();

  private SearchService searchService;

  private int pageSize;

  private int maxSearchResult;

  private ArrayList<Unit> units;

  private boolean unitsCacheComplete;

  private String googleMapsKey;

  private int meters;

  private List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes = new ArrayList<Integer>();

  public int getMeters() {
    return meters;
  }

  public void setMeters(int meters) {
    this.meters = meters;
  }

  public String getGoogleMapsKey() {
    return googleMapsKey;
  }

  public void setGoogleMapsKeys(String googleMapsKey) {
    this.googleMapsKey = googleMapsKey;
  }

  public ArrayList<Unit> getUnits() {
    return units;
  }

  public void setUnits(ArrayList<Unit> units) {
    this.units = units;
  }

  public void setMaxSearchResult(int maxSearchResult) {
    this.maxSearchResult = maxSearchResult;
  }

  public SearchService getSearchService() {
    return searchService;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void initalLoad() {
    logger.info(CLASS_NAME + ".initalLoad()");
  }

  public void cleanSearchSimpleForm(UnitSearchSimpleForm theForm) {
    logger.info(CLASS_NAME + ".cleanSearchSimpleForm()");
    theForm.setMunicipality("");
    theForm.setUnitName("");
    theForm.setHealthcareType("");
  }

  public SikSearchResultList<Unit> doSearch(UnitSearchSimpleForm theForm) throws KivNoDataFoundException, KivException {
    logger.info(CLASS_NAME + ".doSearch()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();
      // start measurement
      overAllTime.start();
      SikSearchResultList<Unit> list = new SikSearchResultList<Unit>();

      if (!theForm.isEmpty()) {
        Unit u = mapSearchCriteriaToUnit(theForm);

        Comparator<Unit> sortOrder = evaluateSortOrder(theForm);

        list = getSearchService().searchAdvancedUnits(u, maxSearchResult, sortOrder, showUnitsWithTheseHsaBussinessClassificationCodes);
      }

      // stop measurement
      overAllTime.stop();

      LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, logger, list);
      if (list.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return list;
    } catch (Exception e) {
      if (e instanceof NoConnectionToServerException) {
        throw (NoConnectionToServerException) e;
      }
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
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
    if (s == null) {
      throw new KivException("No sort order specified. MethodName=" + this.getClass().getName() + "::doSearch(...)");
    } else if (s.trim().equalsIgnoreCase("UNIT_NAME")) {
      sortOrder = new UnitNameComparator();
    } else if (s.trim().equalsIgnoreCase("CARE_TYPE_NAME")) {
      sortOrder = new UnitCareTypeNameComparator();
    } else {
      throw new KivException("Unknown sort order specified. MethodName=" + this.getClass().getName() + "::doSearch(...)");
    }
    return sortOrder;
  }

  public List<String> getAllUnitsHsaIdentity(boolean showFilteredByHsaBusinessClassificationCode) throws KivNoDataFoundException {
    try {
      if (showFilteredByHsaBusinessClassificationCode) {
        List<String> units = getSearchService().getAllUnitsHsaIdentity(showUnitsWithTheseHsaBussinessClassificationCodes);
        return units;
      } else {
        return getSearchService().getAllUnitsHsaIdentity();
      }
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }

  public List<String> getAllUnitsHsaIdentity() throws KivNoDataFoundException {
    return getAllUnitsHsaIdentity(false);
  }

  /**
   * Return a list of hsaIds corresponding to startIndex->endIndex of units
   * 
   * @param startIndex
   * @param endIndex
   * @return
   * @throws KivNoDataFoundException
   */
  public List<String> getRangeUnitsPageList(Integer startIndex, Integer endIndex) throws KivNoDataFoundException {
    try {
      List<String> list = getAllUnitsHsaIdentity(true);
      if (startIndex < 0 || startIndex > endIndex || endIndex < 0 || endIndex > list.size() - 1) {
        throw new SikInternalException(this, "getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + ")", "Error input parameters are wrong (result list size=" + list.size()
            + ")");
      }
      List<String> result = new ArrayList<String>();
      for (int position = startIndex; position <= endIndex; position++) {
        try {
          result.add(list.get(position));
        } catch (Exception e) {
          logger.error("Error in " + CLASS_NAME + "::getRangeUnitsPageList(startIndex=" + startIndex + ", endIndex=" + endIndex + ") index position=" + position + " does not exist as expected", e);
        }
      }
      return result;
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }

  /**
   * Return a list of PagedSearchMetaData objects which chops up the full list in to minor chunks Used in case of indexing all units. Returns a list of page meta data.
   * 
   * @param pageSizeString
   * @return
   * @throws KivNoDataFoundException
   */
  public List<PagedSearchMetaData> getAllUnitsPageList(String pageSizeString) throws KivNoDataFoundException {
    try {
      PagedSearchMetaData metaData;
      List<PagedSearchMetaData> result = new ArrayList<PagedSearchMetaData>();
      List<String> unitHsaIdList = getAllUnitsHsaIdentity(true);
      int size = unitHsaIdList.size();
      if (isInteger(pageSizeString)) {
        int temp = Integer.parseInt(pageSizeString);
        if (temp > pageSize) {
          // we can only increase the page size
          pageSize = temp;
        }
      }
      int index = 0;
      if (size > 0) {
        while (index < size) {
          metaData = new PagedSearchMetaData();
          // 0 the first time
          metaData.setStartIndex(index);
          int endIndex = index + pageSize > size ? size - 1 : index + pageSize - 1;
          // e.g. 274 the first time
          metaData.setEndIndex(endIndex);
          result.add(metaData);
          // e.g. 275 the first time
          index = index + pageSize;
        }
      }
      return result;
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
      return new ArrayList<PagedSearchMetaData>();
    }
  }

  private Unit mapSearchCriteriaToUnit(UnitSearchSimpleForm theForm) throws Exception {
    final String methodName = CLASS_NAME + ".mapSearchCriteriaToUnit(...)";
    logger.info(methodName);
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
    Integer healthcareTypeIndex = null;
    try {
      healthcareTypeIndex = Integer.parseInt(theForm.getHealthcareType());
    } catch (NumberFormatException nfe) {
      // No health care type was chosen.
    }
    if (healthcareTypeIndex != null) {
      HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
      HealthcareType ht = htch.getHealthcareTypeByIndex(healthcareTypeIndex);
      List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
      if (ht != null) {
        healthcareTypes.add(ht);
      }
      unit.setHealthcareTypes(healthcareTypes);
    }
    return unit;
  }

  public void logger() {
    logger.info("Logger");
  }

  public boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public ArrayList<Unit> getCloseUnits(DisplayCloseUnitsSimpleForm form) {
    ArrayList<Unit> closeUnits = new ArrayList<Unit>();
    if (units == null) {
      // Units are not set, probably because the unit popuplation is not finished yet.
      return closeUnits;
    }

    GeoUtil geoUtil = new GeoUtil();
    closeUnits = geoUtil.getCloseUnits(form.getAddress(), units, meters, googleMapsKey);
    return closeUnits;
  }

  public void populateCoordinates() {
    GeoUtil geoUtil = new GeoUtil();
    for (Unit u : units) {
      geoUtil.setGeoCoordinate(u, new double[] { u.getWgs84Lat(), u.getWgs84Long() });
    }
  }

  public void setShowUnitsWithTheseHsaBussinessClassificationCodes(String showUnitsWithTheseHsaBussinessClassificationCodes) {
    List<String> tempList = Arrays.asList(showUnitsWithTheseHsaBussinessClassificationCodes.split(","));
    for (String id : tempList) {
      if (id.length() > 0) {
        this.showUnitsWithTheseHsaBussinessClassificationCodes.add(Integer.parseInt(id));
      }
    }
  }

  public boolean isUnitsCacheComplete() {
    return unitsCacheComplete;
  }

  public void setUnitsCacheComplete(boolean unitsCacheComplete) {
    this.unitsCacheComplete = unitsCacheComplete;
  }
}
