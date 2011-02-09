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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.MunicipalityHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;

public class SearchUnitFlowSupportBeanTest {
  private final SearchServiceMock searchService = new SearchServiceMock();
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private final SearchUnitFlowSupportBean bean = new SearchUnitFlowSupportBean();
  private UnitSearchSimpleForm form;
  private DisplayCloseUnitsSimpleForm displayCloseUnitsSimpleForm;

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
    this.bean.setUnitSearchStrategy(new UnitSearchStrategyVGRImpl());

    MunicipalityHelper municipalityHelper = new MunicipalityHelper();
    municipalityHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    this.form = new UnitSearchSimpleForm();
    this.displayCloseUnitsSimpleForm = new DisplayCloseUnitsSimpleForm();
  }

  @After
  public void tearDown() {
    new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
  }

  @Test
  public void testCleanSearchSimpleForm() {
    try {
      this.bean.cleanSearchSimpleForm(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    this.form.setUnitName("DEF");
    this.bean.cleanSearchSimpleForm(this.form);
    assertEquals("", this.form.getUnitName());
  }

  @Test
  public void testDoSearch() throws Exception {
    try {
      this.bean.doSearch(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.form.setUnitName("DEF");
    this.form.setHealthcareType("0");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    // 18 == Vårdcentral
    this.form.setHealthcareType("18");
    SikSearchResultList<Unit> result = this.bean.doSearch(this.form);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new NoConnectionToServerException());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("NoConnectionToServerException expected");
    } catch (NoConnectionToServerException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();

    this.form.setShowAll("true");
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    this.bean.doSearch(this.form);
    this.searchService.assertMaxSearchResults(Integer.MAX_VALUE);
  }

  @Test
  public void testDoSearchNoHitsFirstSearch() throws KivException {
    this.form.setUnitName("DEF");
    this.form.setHealthcareType("0");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    SikSearchResultList<Unit> result = this.bean.doSearch(this.form);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.form.setHealthcareType("");
    this.form.setMunicipality("1480");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    result = this.bean.doSearch(this.form);
    assertEquals(1, result.size());

    this.form.setUnitName("");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      result = this.bean.doSearch(this.form);
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }
  }

  @Test
  public void testDoSearchUnitNameCleaned() throws KivException {
    this.form.setUnitName("Vårdcentralen Angered, Angered");
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    SikSearchResultList<Unit> result = this.bean.doSearch(this.form);
    assertNotNull(result);
    assertEquals(1, result.size());
    this.searchService.assertUnitCriterionName("Vårdcentralen Angered");
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
  public void testSetMaxSearchResults() throws Exception {
    this.bean.setMaxSearchResult(3);

    this.form.setUnitName("a");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(3);

    this.bean.setMaxSearchResult(5);
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(5);
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
  public void testEvaluateSortOrder() throws KivException {
    this.form.setUnitName("ABC");

    this.form.setSortOrder("XYZ");
    SikSearchResultList<Unit> result = this.bean.doSearch(this.form);
    assertNotNull(result);
    assertEquals(0, result.size());

    this.form.setSortOrder("UNIT_NAME");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.form.setSortOrder("CARE_TYPE_NAME");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      this.bean.doSearch(this.form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }
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
