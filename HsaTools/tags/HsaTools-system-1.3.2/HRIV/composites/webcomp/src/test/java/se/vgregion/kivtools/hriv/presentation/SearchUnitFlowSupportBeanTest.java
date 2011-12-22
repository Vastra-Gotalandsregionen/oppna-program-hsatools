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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.comparators.UnitCareTypeNameComparator;
import se.vgregion.kivtools.hriv.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;

public class SearchUnitFlowSupportBeanTest {
  private final SearchServiceMock searchService = new SearchServiceMock();
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private final SearchUnitFlowSupportBean bean = new SearchUnitFlowSupportBean();
  private UnitSearchSimpleForm form;
  private DisplayCloseUnitsSimpleForm displayCloseUnitsSimpleForm;
  private final UnitSearchStrategyMock unitSearchStrategy = new UnitSearchStrategyMock();

  @BeforeClass
  public static void setupClass() {
    LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() throws Exception {
    this.bean.setSearchService(this.searchService);
    this.bean.setUnitCacheService(this.unitCacheService);
    this.bean.setUnitSearchStrategy(this.unitSearchStrategy);

    this.form = new UnitSearchSimpleForm();
    this.displayCloseUnitsSimpleForm = new DisplayCloseUnitsSimpleForm();
  }

  @Test(expected = NullPointerException.class)
  public void cleanSearchSimpleFormThrowsNullPointerExceptionOnNullInput() {
    this.bean.cleanSearchSimpleForm(null);
  }

  @Test
  public void cleanSearchSimpleFormCleansUnitNameInForm() {
    this.form.setUnitName("DEF");
    this.bean.cleanSearchSimpleForm(this.form);
    assertEquals("", this.form.getUnitName());
  }

  @Test(expected = NullPointerException.class)
  public void doSearchThrowsNullPointerExceptionForNullInput() throws Exception {
    this.bean.doSearch(null);
  }

  @Test(expected = KivNoDataFoundException.class)
  public void doSearchThrowsKivNoDataFoundExceptionForEmptyForm() throws Exception {
    this.bean.doSearch(this.form);
    assertSame("form", this.form, this.unitSearchStrategy.form);
  }

  @Test
  public void doSearchCallsUnitSearchStrategyWithSearchForm() throws Exception {
    this.form.setUnitName("DEF");
    this.form.setHealthcareType("0");
    this.bean.doSearch(this.form);
    assertSame("form", this.form, this.unitSearchStrategy.form);
  }

  @Test
  public void doSearchPassesMaxSearchResultToUnitSearchStrategy() throws Exception {
    this.bean.setMaxSearchResult(3);
    this.form.setUnitName("a");
    this.bean.doSearch(this.form);
    assertEquals("max search result", 3, this.unitSearchStrategy.effectiveMaxSearchResult);
  }

  @Test
  public void doSearchPassesIntegerMaxValueAsMaxSearchResultIfShowAllIsSet() throws Exception {
    this.form.setShowAll("true");
    this.form.setUnitName("a");
    this.bean.doSearch(this.form);
    assertEquals("max search result", Integer.MAX_VALUE, this.unitSearchStrategy.effectiveMaxSearchResult);
  }

  @Test(expected = NoConnectionToServerException.class)
  public void doSearchDoesNotCatchNoConnectionToServerException() throws Exception {
    this.unitSearchStrategy.setExceptionToThrow(new NoConnectionToServerException());
    this.form.setUnitName("a");
    this.bean.doSearch(this.form);
  }

  @Test(expected = KivNoDataFoundException.class)
  public void doSearchDoesNotCatchKivNoDataFoundException() throws Exception {
    this.unitSearchStrategy.setExceptionToThrow(new KivNoDataFoundException());
    this.form.setUnitName("a");
    this.bean.doSearch(this.form);
  }

  @Test
  public void doSearchCatchesOtherKindsOfKivExceptionAndReturnEmptyList() throws Exception {
    this.unitSearchStrategy.setExceptionToThrow(new IncorrectUserInputException());
    this.form.setUnitName("a");
    SikSearchResultList<Unit> result = this.bean.doSearch(this.form);
    assertNotNull("result", result);
    assertEquals("result count", 0, result.size());
  }

  @Test
  public void doSearchDoesNotCallUnitSearchStrategyIfAnInvalidSortOrderIsSpecified() throws Exception {
    this.form.setUnitName("ABC");
    this.form.setSortOrder("XYZ");
    this.bean.doSearch(this.form);
    assertNull("form", this.unitSearchStrategy.form);
  }

  @Test
  public void doSearchPassesAUnitNameComparatorToUnitSearchStrategyForUnitNameSortOrder() throws Exception {
    this.form.setUnitName("ABC");
    this.form.setSortOrder("UNIT_NAME");
    this.bean.doSearch(this.form);
    assertEquals("form", UnitNameComparator.class, this.unitSearchStrategy.sortOrder.getClass());
  }

  @Test
  public void doSearchPassesAUnitCareTypeNameComparatorToUnitSearchStrategyForCareTypeSortOrder() throws Exception {
    this.form.setUnitName("ABC");
    this.form.setSortOrder("CARE_TYPE_NAME");
    this.bean.doSearch(this.form);
    assertEquals("form", UnitCareTypeNameComparator.class, this.unitSearchStrategy.sortOrder.getClass());
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws Exception {
    List<String> result = this.bean.getAllUnitsHsaIdentity();
    assertNotNull(result);
    assertEquals(0, result.size());

    this.searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      this.bean.getAllUnitsHsaIdentity();
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();
    this.searchService.addExceptionToThrow(new KivException("Test"));
    result = this.bean.getAllUnitsHsaIdentity();
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetRangeUnitsPageList() throws KivNoDataFoundException {
    List<String> result = this.bean.getRangeUnitsPageList(-1, -1);
    assertNotNull(result);
    assertEquals(0, result.size());

    result = this.bean.getRangeUnitsPageList(2, 1);
    assertNotNull(result);
    assertEquals(0, result.size());

    result = this.bean.getRangeUnitsPageList(0, 0);
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    this.searchService.setAllUnitsId(allUnitsId);
    result = this.bean.getRangeUnitsPageList(0, 0);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      this.bean.getRangeUnitsPageList(0, 0);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetAllUnitsPageList() throws KivNoDataFoundException {
    try {
      this.bean.getAllUnitsPageList("a");
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    List<PagedSearchMetaData> result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    this.bean.setOnlyPublicUnits(true);
    result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    this.searchService.setAllUnitsId(allUnitsId);
    result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(1, result.size());

    allUnitsId.add("def-456");
    result = this.bean.getAllUnitsPageList("3");
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      this.bean.getAllUnitsPageList("1");
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();
    this.searchService.addExceptionToThrow(new KivException("Test"));
    result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testSetPageSize() throws KivNoDataFoundException {
    this.bean.setPageSize(3);

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    allUnitsId.add("def-456");
    allUnitsId.add("ghi-789");
    this.searchService.setAllUnitsId(allUnitsId);
    List<PagedSearchMetaData> result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 3 results per page == 1 page metadata
    assertEquals(1, result.size());

    this.bean.setPageSize(2);
    result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 2 results per page == 2 page metadata
    assertEquals(2, result.size());

    this.bean.setPageSize(0);
    result = this.bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 1 result per page == 3 page metadata
    assertEquals(3, result.size());
  }

  @Test
  public void testSetMeters() {
    this.bean.setMeters(123);
    assertEquals(123, this.bean.getMeters());

    this.bean.setMeters(234);
    assertEquals(234, this.bean.getMeters());
  }

  @Test
  public void testSetGoogleMapsKey() {
    this.bean.setGoogleMapsKey("ABC-123");
    assertEquals("ABC-123", this.bean.getGoogleMapsKey());

    this.bean.setGoogleMapsKey("DEF-234");
    assertEquals("DEF-234", this.bean.getGoogleMapsKey());
  }

  @Test
  public void testGetCloseUnits() {
    ArrayList<Unit> closeUnits = this.bean.getCloseUnits(null);
    assertNotNull(closeUnits);
    assertEquals(0, closeUnits.size());

    this.unitCacheService.reloadCache();
    try {
      this.bean.getCloseUnits(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    this.displayCloseUnitsSimpleForm.setAddress("Storgatan 1, Göteborg");
    closeUnits = this.bean.getCloseUnits(this.displayCloseUnitsSimpleForm);
    assertNotNull(closeUnits);
    assertEquals(0, closeUnits.size());
  }

  private static class UnitSearchStrategyMock implements UnitSearchStrategy {
    private UnitSearchSimpleForm form;
    private int effectiveMaxSearchResult;
    private KivException exceptionToThrow;
    private Comparator<Unit> sortOrder;

    @Override
    public SikSearchResultList<Unit> performSearch(UnitSearchSimpleForm theForm, Comparator<Unit> sortOrder, int effectiveMaxSearchResult, SearchService searchService, boolean onlyPublicUnits)
        throws KivException {
      this.form = theForm;
      this.sortOrder = sortOrder;
      this.effectiveMaxSearchResult = effectiveMaxSearchResult;

      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }

      SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
      result.add(new Unit());
      return result;
    }

    public void setExceptionToThrow(KivException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }
  }

  private static class UnitCacheLoaderMock implements CacheLoader<UnitCache> {
    @Override
    public UnitCache createEmptyCache() {
      return new UnitCache();
    }

    @Override
    public UnitCache loadCache() {
      UnitCache unitCache = new UnitCache();
      Unit unit = new Unit();
      unit.setHsaIdentity("abc-123");
      unitCache.add(unit);
      return unitCache;
    }
  }
}
