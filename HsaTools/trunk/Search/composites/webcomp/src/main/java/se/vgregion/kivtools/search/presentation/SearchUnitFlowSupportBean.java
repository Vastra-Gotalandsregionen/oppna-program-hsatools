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
import java.net.UnknownHostException;
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
import se.vgregion.kivtools.search.util.LogUtils;
import se.vgregion.kivtools.search.util.geo.GeoUtil;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
public class SearchUnitFlowSupportBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final String CLASS_NAME = SearchUnitFlowSupportBean.class.getName();

  private Log logger = LogFactory.getLog(this.getClass());
  private SearchService searchService;
  private int pageSize;

  private int maxSearchResult;

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
    theForm.setSearchParamValue("");
    theForm.setUnitName("");
  }

  public SikSearchResultList<Unit> doSearch(UnitSearchSimpleForm theForm) throws KivNoDataFoundException, NoConnectionToServerException {
    logger.info(CLASS_NAME + ".doSearch()");

    try {
      TimeMeasurement overAllTime = new TimeMeasurement();

      // start measurement
      overAllTime.start();
      SikSearchResultList<Unit> list = null;
      if (!theForm.isEmpty()) {
        Unit u = mapSearchCriteriaToUnit(theForm);
        list = getSearchService().searchUnits(u, maxSearchResult);
      }
      // stop measurement
      overAllTime.stop();

      if (list == null) {
        list = new SikSearchResultList<Unit>();
      }
      LogUtils.printSikSearchResultListToLog(this, "doSearch", overAllTime, logger, list);
      if (list.size() == 0) {
        throw new KivNoDataFoundException();
      }
      return list;
    } catch (Exception e) {
      if (e instanceof UnknownHostException) {
        throw (NoConnectionToServerException) e;
      }
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      logger.error(e.getMessage(), e);
      e.printStackTrace();
      return new SikSearchResultList<Unit>();
    }
  }

  public List<String> getAllUnitsHsaIdentity() throws KivNoDataFoundException {
    try {
      return getSearchService().getAllUnitsHsaIdentity();
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }

  /**
   * Return a list of hsaIds corresponding to startIndex->endIndex of units.
   * 
   * @param startIndex
   * @param endIndex
   * @return
   * @throws KivNoDataFoundException
   */
  public List<String> getRangeUnitsPageList(Integer startIndex, Integer endIndex) throws KivNoDataFoundException {
    try {
      List<String> list = getSearchService().getAllUnitsHsaIdentity();
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
      List<String> unitHsaIdList = getSearchService().getAllUnitsHsaIdentity();
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

  /**
   * Geocode all units to RT90.
   * 
   * @param googleKey Google Maps key.
   * @return List with units with coordinates.
   * @throws KivNoDataFoundException
   */
  public List<Unit> getAllUnitsGeocoded(String googleKey) throws KivNoDataFoundException {
    List<String> allUnitsHsaIdentity = getAllUnitsHsaIdentity();
    List<Unit> allUnitsWithPositionInfo = new ArrayList<Unit>();
    GeoUtil geoUtil = new GeoUtil();
    try {
      // int i = 0;
      for (String hsaId : allUnitsHsaIdentity) {
        // i++;
        // if (i > 10)
        // break;
        // Get coordinates from Google.
        Unit u = getSearchService().getUnitByHsaId(hsaId);
        if (u != null) {
          if (u.getHsaStreetAddressIsValid()) {
            int[] RT90Coordinates = geoUtil.geocodeToRT90(u.getHsaStreetAddress(), googleKey);
            if (RT90Coordinates != null) {
              u.setRt90X(RT90Coordinates[0]);
              u.setRt90Y(RT90Coordinates[1]);
            } else {
              u.setRt90X(-1);
              u.setRt90Y(-1);
            }

            // For debug purpose only.
            /*
             * double[] WGS84Coordinates = GeoUtil.geocodeToWGS84(u.getHsaStreetAddress(), googleKey); if (WGS84Coordinates != null) { u.setWgs84_lat(WGS84Coordinates[0]);
             * u.setWgs84_long(WGS84Coordinates[1]); } else { u.setWgs84_lat(-1); u.setWgs84_long(-1); }
             */
            allUnitsWithPositionInfo.add(u);
          }
        }
      }
      return allUnitsWithPositionInfo;
    } catch (Exception e) {
      if (e instanceof KivNoDataFoundException) {
        throw (KivNoDataFoundException) e;
      }
      e.printStackTrace();
      return new ArrayList<Unit>();
    }
  }

  public SikSearchResultList<Unit> getSubUnits(String parentHsaId) {
    SikSearchResultList<Unit> subUnits = new SikSearchResultList<Unit>();
    try {
      Unit parentUnit = getSearchService().getUnitByHsaId(parentHsaId);
      subUnits = getSearchService().getSubUnits(parentUnit, maxSearchResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return subUnits;
  }

  private Unit mapSearchCriteriaToUnit(UnitSearchSimpleForm theForm) throws Exception {
    final String methodName = CLASS_NAME + ".mapSearchCriteriaToUnit(...)";
    logger.info(methodName);
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
}
