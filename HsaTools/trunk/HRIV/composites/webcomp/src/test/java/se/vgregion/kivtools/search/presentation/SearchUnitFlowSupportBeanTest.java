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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.presentation.forms.DisplayCloseUnitsSimpleForm;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

public class SearchUnitFlowSupportBeanTest {
  private SearchServiceMock searchService;
  private SearchUnitFlowSupportBean bean;
  private UnitSearchSimpleForm form;
  private DisplayCloseUnitsSimpleForm displayCloseUnitsSimpleForm;

  @Before
  public void setUp() throws Exception {
    MunicipalityHelper municipalityHelper = new MunicipalityHelper();
    municipalityHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    healthcareTypeConditionHelper.setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");

    bean = new SearchUnitFlowSupportBean();
    form = new UnitSearchSimpleForm();
    displayCloseUnitsSimpleForm = new DisplayCloseUnitsSimpleForm();
    searchService = new SearchServiceMock();

    bean.setSearchService(searchService);
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
    SikSearchResultList<Unit> result = bean.doSearch(null);
    assertNotNull(result);
    assertEquals(0, result.size());

    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    form.setUnitName("DEF");
    form.setHealthcareType("0");
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.searchService.addUnit(unit);
    form.setHealthcareType("1");
    result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new NoConnectionToServerException());
    try {
      bean.doSearch(form);
      fail("NoConnectionToServerException expected");
    } catch (NoConnectionToServerException e) {
      // Expected exception
    }
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
    this.searchService.addExceptionToThrow(new Exception());
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
    List<PagedSearchMetaData> result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());

    bean.setShowUnitsWithTheseHsaBussinessClassificationCodes("1,2,3");
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
    this.searchService.assertShowUnitsWithHsaBusinessClassificationCodes(1, 2, 3);

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
    searchService.addExceptionToThrow(new Exception());
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testSetMaxSearchResults() throws Exception {
    bean.setMaxSearchResult(3);

    form.setUnitName("a");
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.assertMaxSearchResults(3);

    bean.setMaxSearchResult(5);
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

    form.setSortOrder(null);
    SikSearchResultList<Unit> result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(0, result.size());

    form.setSortOrder("XYZ");
    result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(0, result.size());

    form.setSortOrder("UNIT_NAME");
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    form.setSortOrder("CARE_TYPE_NAME");
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
  public void testSetUnits() {
    assertNull(bean.getUnits());

    ArrayList<Unit> units = new ArrayList<Unit>();
    bean.setUnits(units);

    assertNotNull(units);
    assertEquals(0, bean.getUnits().size());

    units.add(new Unit());
    assertNotNull(units);
    assertEquals(1, bean.getUnits().size());
  }

  @Test
  public void testGetCloseUnits() {
    ArrayList<Unit> closeUnits = bean.getCloseUnits(null);
    assertNotNull(closeUnits);
    assertEquals(0, closeUnits.size());

    bean.setUnits(new ArrayList<Unit>());
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

  @Test
  public void testPopulateCoordinates() {
    try {
      bean.populateCoordinates();
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    ArrayList<Unit> units = new ArrayList<Unit>();
    Unit unit = new Unit();

    units.add(unit);
    bean.setUnits(units);
    bean.populateCoordinates();
    assertNotNull(unit.getGeoCoordinate());
    assertEquals(0.0, unit.getGeoCoordinate().getLatitude(), 0.0);
    assertEquals(0.0, unit.getGeoCoordinate().getLongitude(), 0.0);
  }

  @Test
  public void testIsUnitsCacheComplete() {
    assertFalse(bean.isUnitsCacheComplete());

    bean.setUnitsCacheComplete(true);
    assertTrue(bean.isUnitsCacheComplete());

    bean.setUnitsCacheComplete(false);
    assertFalse(bean.isUnitsCacheComplete());
  }
}
