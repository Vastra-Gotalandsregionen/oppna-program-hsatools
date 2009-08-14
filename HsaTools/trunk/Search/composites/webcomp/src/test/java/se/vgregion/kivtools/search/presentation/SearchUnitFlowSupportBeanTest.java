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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.Address;

public class SearchUnitFlowSupportBeanTest {
  private SearchServiceMock searchService;
  private SearchUnitFlowSupportBean bean;
  private UnitSearchSimpleForm form;

  @Before
  public void setUp() throws Exception {
    bean = new SearchUnitFlowSupportBean();
    form = new UnitSearchSimpleForm();
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

    form.setSearchParamValue("ABC");
    form.setUnitName("DEF");
    bean.cleanSearchSimpleForm(form);
    assertEquals("", form.getSearchParamValue());
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
    try {
      bean.doSearch(form);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.searchService.addUnit(unit);
    result = bean.doSearch(form);
    assertNotNull(result);
    assertEquals(1, result.size());

    this.searchService.addExceptionToThrow(new UnknownHostException());
    try {
      bean.doSearch(form);
      fail("ClassCastException expected");
    } catch (ClassCastException e) {
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
    List<PagedSearchMetaData> result = bean.getAllUnitsPageList(null);
    assertNotNull(result);
    assertEquals(0, result.size());

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
    searchService.addExceptionToThrow(new Exception());
    result = bean.getAllUnitsPageList("1");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetAllUnitsGeocoded() throws KivNoDataFoundException {
    List<Unit> result = bean.getAllUnitsGeocoded(null);
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> allUnitsId = new ArrayList<String>();
    allUnitsId.add("ABC-123");
    searchService.setAllUnitsId(allUnitsId);
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.searchService.addUnit(unit);
    result = bean.getAllUnitsGeocoded(null);
    assertNotNull(result);
    assertEquals(0, result.size());

    Address address = new Address();
    address.setStreet("Storgatan 1");
    address.setCity("Göteborg");
    unit.setHsaStreetAddress(address);
    result = bean.getAllUnitsGeocoded(null);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertFalse(-1 == unit.getRt90X());
    assertFalse(-1 == unit.getRt90Y());

    address.setStreet("xyz123");
    address.setCity("abc123");
    result = bean.getAllUnitsGeocoded(null);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(-1, unit.getRt90X());
    assertEquals(-1, unit.getRt90Y());

    this.searchService.addExceptionToThrow(null);
    this.searchService.addExceptionToThrow(new KivNoDataFoundException());
    try {
      result = bean.getAllUnitsGeocoded(null);
      fail("KivNoDataFoundException expected");
    } catch (KivNoDataFoundException e) {
      // Expected exception
    }

    this.searchService.clearExceptionsToThrow();
    this.searchService.addExceptionToThrow(null);
    this.searchService.addExceptionToThrow(new Exception());
    result = bean.getAllUnitsGeocoded(null);
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetSubUnits() {
    SikSearchResultList<Unit> result = bean.getSubUnits(null);
    assertNotNull(result);
    assertEquals(0, result.size());

    this.searchService.addExceptionToThrow(new Exception());
    result = bean.getSubUnits(null);
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
}
