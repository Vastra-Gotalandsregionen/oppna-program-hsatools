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

import static org.junit.Assert.*;

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
  private SearchServiceMock searchService = new SearchServiceMock();
  private UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private SearchUnitFlowSupportBean bean = new SearchUnitFlowSupportBean();
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
    bean.setSearchService(searchService);
    bean.setUnitCacheService(unitCacheService);

    MunicipalityHelper municipalityHelper = new MunicipalityHelper();
    municipalityHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    form = new UnitSearchSimpleForm();
    displayCloseUnitsSimpleForm = new DisplayCloseUnitsSimpleForm();
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
      bean.cleanSearchSimpleForm(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    form.setUnitName("DEF");
    bean.cleanSearchSimpleForm(form);
    assertEquals("", form.getUnitName());
  }

  @Test
  public void testDoSearch() throws Exception {
    try {
      bean.doSearch(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    form.setUnitName("DEF");
    form.setHealthcareType("0");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
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
    form.setHealthcareType("18");
    SikSearchResultList<Unit> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new NoConnectionToServerException());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
      fail("NoConnectionToServerException expected");
    } catch (NoConnectionToServerException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();

    form.setShowAll("true");
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    bean.doSearch(form);
    this.searchService.assertMaxSearchResults(Integer.MAX_VALUE);
  }

  @Test
  public void testDoSearchNoHitsFirstSearch() throws KivException {
    form.setUnitName("DEF");
    form.setHealthcareType("0");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    SikSearchResultList<Unit> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    form.setHealthcareType("");
    form.setMunicipality("1480");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    result = bean.doSearch(form);
    assertEquals(1, result.size());

    form.setUnitName("");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      result = bean.doSearch(form);
      fail("KivException expected");
    } catch (KivException e) {
      // Expected exception
    }
  }

  @Test
  public void testDoSearchUnitNameCleaned() throws KivException {
    form.setUnitName("Vårdcentralen Angered, Angered");
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    SikSearchResultList<Unit> searchResult = new SikSearchResultList<Unit>();
    searchResult.add(unit);
    this.searchService.addSearchAdvancedUnitsSearchResult(searchResult);
    SikSearchResultList<Unit> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());
    this.searchService.assertUnitCriterionName("Vårdcentralen Angered");
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws Exception {
    List<String> result = bean.getAllUnitsHsaIdentity();
    assertNotNull(result);
    assertEquals(0, result.size());

    this.searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getAllUnitsHsaIdentity();
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();
    this.searchService.addExceptionToThrow(new KivException("Test"));
    result = bean.getAllUnitsHsaIdentity();
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetRangeUnitsPageList() throws KivNoDataFoundException {
    List<String> result = bean.getRangeUnitsPageList(-1, -1);
    assertNotNull(result);
    assertEquals(0, result.size());

    result = bean.getRangeUnitsPageList(2, 1);
    assertNotNull(result);
    assertEquals(0, result.size());

    result = bean.getRangeUnitsPageList(0, 0);
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    searchService.setAllUnitsId(allUnitsId);
    result = bean.getRangeUnitsPageList(0, 0);
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getRangeUnitsPageList(0, 0);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }
  }

  @Test
  public void testGetAllUnitsPageList() throws KivNoDataFoundException {
    try {
      bean.getAllUnitsPageList("a");
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    List<PagedSearchMetaData> result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    bean.setOnlyPublicUnits(true);
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    searchService.setAllUnitsId(allUnitsId);
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(1, result.size());

    allUnitsId.add("def-456");
    result = bean.getAllUnitsPageList("3");
    assertNotNull(result);
    assertEquals(1, result.size());

    searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      bean.getAllUnitsPageList("1");
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    searchService.clearExceptionsToThrow();
    searchService.addExceptionToThrow(new KivException("Test"));
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testSetMaxSearchResults() throws Exception {
    bean.setMaxSearchResult(3);

    form.setUnitName("a");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(3);

    bean.setMaxSearchResult(5);
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(5);
  }

  @Test
  public void testSetPageSize() throws KivNoDataFoundException {
    bean.setPageSize(3);

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("abc-123");
    allUnitsId.add("def-456");
    allUnitsId.add("ghi-789");
    searchService.setAllUnitsId(allUnitsId);
    List<PagedSearchMetaData> result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 3 results per page == 1 page metadata
    assertEquals(1, result.size());

    bean.setPageSize(2);
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 2 results per page == 2 page metadata
    assertEquals(2, result.size());

    bean.setPageSize(0);
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    // 1 result per page == 3 page metadata
    assertEquals(3, result.size());
  }

  @Test
  public void testEvaluateSortOrder() throws KivException {
    form.setUnitName("ABC");

    form.setSortOrder("XYZ");
    SikSearchResultList<Unit> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(0, result.size());

    form.setSortOrder("UNIT_NAME");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    form.setSortOrder("CARE_TYPE_NAME");
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    this.searchService.addSearchAdvancedUnitsSearchResult(new SikSearchResultList<Unit>());
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }
  }

  @Test
  public void testSetMeters() {
    bean.setMeters(123);
    assertEquals(123, bean.getMeters());

    bean.setMeters(234);
    assertEquals(234, bean.getMeters());
  }

  @Test
  public void testSetGoogleMapsKey() {
    bean.setGoogleMapsKey("ABC-123");
    assertEquals("ABC-123", bean.getGoogleMapsKey());

    bean.setGoogleMapsKey("DEF-234");
    assertEquals("DEF-234", bean.getGoogleMapsKey());
  }

  @Test
  public void testGetCloseUnits() {
    ArrayList<Unit> closeUnits = bean.getCloseUnits(null);
    assertNotNull(closeUnits);
    assertEquals(0, closeUnits.size());

    unitCacheService.reloadCache();
    try {
      bean.getCloseUnits(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    displayCloseUnitsSimpleForm.setAddress("Storgatan 1, Göteborg");
    closeUnits = bean.getCloseUnits(displayCloseUnitsSimpleForm);
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
