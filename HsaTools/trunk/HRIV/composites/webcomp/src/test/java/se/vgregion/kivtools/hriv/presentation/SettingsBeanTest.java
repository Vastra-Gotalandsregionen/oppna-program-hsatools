/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class SettingsBeanTest {

  private static final String TEST_STRING = "test";
  private SettingsBean bean;

  @Before
  public void setUp() throws Exception {
    bean = new SettingsBean();
  }

  @Test
  public void testSetInformationArea() {
    assertNull(bean.getInformationArea());
    bean.setInformationArea(TEST_STRING);
    assertEquals(TEST_STRING, bean.getInformationArea());
  }

  @Test
  public void testSetMobileUrl() {
    assertNull(bean.getMobileUrl());
    bean.setMobileUrl(TEST_STRING);
    assertEquals(TEST_STRING, bean.getMobileUrl());
  }

  @Test
  public void testSetVerifyV1() {
    assertNull(bean.getVerifyV1());
    bean.setVerifyV1(TEST_STRING);
    assertEquals(TEST_STRING, bean.getVerifyV1());
  }

  @Test
  public void testSetTitle() {
    assertNull(bean.getTitle());
    bean.setTitle(TEST_STRING);
    assertEquals(TEST_STRING, bean.getTitle());
  }

  @Test
  public void testSetSearchResultTitle() {
    assertNull(bean.getSearchResultTitle());
    bean.setSearchResultTitle(TEST_STRING);
    assertEquals(TEST_STRING, bean.getSearchResultTitle());
  }

  @Test
  public void testSetLinkToIMOnServer() {
    assertNull(bean.getLinkToIMOnServer());
    bean.setLinkToIMOnServer(TEST_STRING);
    assertEquals(TEST_STRING, bean.getLinkToIMOnServer());
  }

  @Test
  public void testSetShowLinkToIM() {
    assertNull(bean.getShowLinkToIM());
    bean.setShowLinkToIM(TEST_STRING);
    assertEquals(TEST_STRING, bean.getShowLinkToIM());
  }

  @Test
  public void testSetLinkToIMBase() {
    assertNull(bean.getLinkToIMBase());
    bean.setLinkToIMBase(TEST_STRING);
    assertEquals(TEST_STRING, bean.getLinkToIMBase());
  }

  @Test
  public void testSetUseShowCloseUnits() {
    assertNull(bean.getUseShowCloseUnits());
    bean.setUseShowCloseUnits(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUseShowCloseUnits());
  }

  @Test
  public void testSetMetaAuthor() {
    assertNull(bean.getMetaAuthor());
    bean.setMetaAuthor(TEST_STRING);
    assertEquals(TEST_STRING, bean.getMetaAuthor());
  }

  @Test
  public void testSetMetaCopyright() {
    assertNull(bean.getMetaCopyright());
    bean.setMetaCopyright(TEST_STRING);
    assertEquals(TEST_STRING, bean.getMetaCopyright());
  }

  @Test
  public void testSetMvkLoginUrl() {
    assertNull(bean.getMvkLoginUrl());
    bean.setMvkLoginUrl(TEST_STRING);
    assertEquals(TEST_STRING, bean.getMvkLoginUrl());
  }

  @Test
  public void testSetUseMvk() {
    assertNull(bean.getUseMvk());
    bean.setUseMvk(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUseMvk());
  }

  @Test
  public void testSetUsePrinting() {
    assertNull(bean.getUsePrinting());
    bean.setUsePrinting(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUsePrinting());
  }

  @Test
  public void testSetUseListenLink() {
    assertNull(bean.getUseListenLink());
    bean.setUseListenLink(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUseListenLink());
  }

  @Test
  public void testSetListenLinkCode2() {
    assertNull(bean.getListenLinkCode2());
    bean.setListenLinkCode2(TEST_STRING);
    assertEquals(TEST_STRING, bean.getListenLinkCode2());
  }

  @Test
  public void testSetListenLinkCode1() {
    assertNull(bean.getListenLinkCode1());
    bean.setListenLinkCode1(TEST_STRING);
    assertEquals(TEST_STRING, bean.getListenLinkCode1());
  }

  @Test
  public void testSetUseTrackingCode() {
    assertNull(bean.getUseTrackingCode());
    bean.setUseTrackingCode(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUseTrackingCode());
  }

  @Test
  public void testSetTrackingCodeOnServer() {
    assertNull(bean.getTrackingCodeOnServer());
    bean.setTrackingCodeOnServer(TEST_STRING);
    assertEquals(TEST_STRING, bean.getTrackingCodeOnServer());
  }

  @Test
  public void testSetTrackingCode() {
    assertNull(bean.getTrackingCode());
    bean.setTrackingCode(TEST_STRING);
    assertEquals(TEST_STRING, bean.getTrackingCode());
  }

  @Test
  public void testSetShowUnitCode1() {
    assertNull(bean.getShowUnitCode1());
    bean.setShowUnitCode1(TEST_STRING);
    assertEquals(TEST_STRING, bean.getShowUnitCode1());
  }

  @Test
  public void testSetShowUnitCode2() {
    assertNull(bean.getShowUnitCode2());
    bean.setShowUnitCode2(TEST_STRING);
    assertEquals(TEST_STRING, bean.getShowUnitCode2());
  }

  @Test
  public void testSetUseShowUnitCode() {
    assertNull(bean.getUseShowUnitCode());
    bean.setUseShowUnitCode(TEST_STRING);
    assertEquals(TEST_STRING, bean.getUseShowUnitCode());
  }

  @Test
  public void testSetShowUnitCodeOnServer() {
    assertNull(bean.getShowUnitCodeOnServer());
    bean.setShowUnitCodeOnServer(TEST_STRING);
    assertEquals(TEST_STRING, bean.getShowUnitCodeOnServer());
  }

  @Test
  public void testSetFooter() {
    assertNull(bean.getFooter());
    bean.setFooter(TEST_STRING);
    assertEquals(TEST_STRING, bean.getFooter());
  }

  @Test
  public void testSetHeader() {
    assertNull(bean.getHeader());
    bean.setHeader(TEST_STRING);
    assertEquals(TEST_STRING, bean.getHeader());
  }

  @Test
  public void testSetScripts() {
    assertEquals(0, bean.getScriptPaths().size());

    try {
      bean.setScripts(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    bean.setScripts("");
    assertEquals(0, bean.getScriptPaths().size());

    bean.setScripts(TEST_STRING);
    assertEquals(1, bean.getScriptPaths().size());
    assertEquals(TEST_STRING, bean.getScriptPaths().get(0));

    bean.setScripts("test,test2");
    assertEquals(2, bean.getScriptPaths().size());
    assertEquals("test", bean.getScriptPaths().get(0));
    assertEquals("test2", bean.getScriptPaths().get(1));
  }

  @Test
  public void testSetFindRouteLinksArray() {
    assertNotNull(bean.getFindRouteLinksArray());
    ArrayList<Link> routeLinksArray = bean.getFindRouteLinksArray();
    assertEquals(0, bean.getFindRouteLinksArray().size());
    routeLinksArray.add(new Link("", "", ""));
    assertEquals(1, bean.getFindRouteLinksArray().size());
  }

  @Test
  public void testSetStyles() {
    assertNull(bean.getStyles());
    bean.setStyles(TEST_STRING);
    assertEquals(TEST_STRING, bean.getStyles());
  }

  @Test
  public void testSetFindRouteLinks() {
    assertEquals(0, bean.getFindRouteLinksArray().size());
    bean.setFindRouteLinks("test::test2::test3");
    ArrayList<Link> findRouteLinksArray = bean.getFindRouteLinksArray();
    assertEquals(1, findRouteLinksArray.size());
    assertEquals("test", findRouteLinksArray.get(0).getHref());
    assertEquals("test2", findRouteLinksArray.get(0).getName());
    assertEquals("test3", findRouteLinksArray.get(0).getToParamName());

    bean.setFindRouteLinks("test::test2;test4::test5::test6");
    findRouteLinksArray = bean.getFindRouteLinksArray();
    assertEquals(2, findRouteLinksArray.size());
    assertEquals("test", findRouteLinksArray.get(0).getHref());
    assertEquals("test2", findRouteLinksArray.get(0).getName());
    assertEquals("", findRouteLinksArray.get(0).getToParamName());
    assertEquals("test4", findRouteLinksArray.get(1).getHref());
    assertEquals("test5", findRouteLinksArray.get(1).getName());
    assertEquals("test6", findRouteLinksArray.get(1).getToParamName());
  }

  @Test
  public void testSetDistanceToUnits() {
    assertNull(bean.getDistanceToUnits());
    bean.setDistanceToUnits(TEST_STRING);
    assertEquals(TEST_STRING, bean.getDistanceToUnits());
  }

  @Test
  public void testSetFallbackOnAddressForMap() {
    assertFalse(bean.isFallbackOnAddressForMap());
    bean.setFallbackOnAddressForMap(true);
    assertTrue(bean.isFallbackOnAddressForMap());
  }

  @Test
  public void testSetTestingMode() {
    assertFalse(bean.getTestingMode());
    assertEquals("false", bean.getTestingModeAsString());
    bean.setTestingMode(true);
    assertTrue(bean.getTestingMode());
    assertEquals("true", bean.getTestingModeAsString());
  }

  @Test
  public void testSetUseAccessibilityDatabaseIntegration() {
    assertFalse(bean.isUseAccessibilityDatabaseIntegration());
    bean.setUseAccessibilityDatabaseIntegration(true);
    assertTrue(bean.isUseAccessibilityDatabaseIntegration());
  }

  @Test
  public void testSetGoogleMapsKey() {
    assertNull(bean.getGoogleMapsKey());
    bean.setGoogleMapsKey(TEST_STRING);
    assertEquals(TEST_STRING, bean.getGoogleMapsKey());
  }

  @Test
  public void testSetMainTop() {
    assertNull(bean.getMainTop());
    bean.setMainTop(TEST_STRING);
    assertEquals(TEST_STRING, bean.getMainTop());
  }

  @Test
  public void testSetStartPage() {
    assertNull(bean.getStartPage());
    bean.setStartPage(TEST_STRING);
    assertEquals(TEST_STRING, bean.getStartPage());
  }

  @Test
  public void testSetCareTypePage() {
    assertNull(bean.getCareTypePage());
    bean.setCareTypePage(TEST_STRING);
    assertEquals(TEST_STRING, bean.getCareTypePage());
  }

  @Test
  public void testSetGeoRegion() {
    assertNull(bean.getGeoRegion());
    bean.setGeoRegion(TEST_STRING);
    assertEquals(TEST_STRING, bean.getGeoRegion());
  }

  @Test
  public void testSetResourceBundleInclude() {
    assertNull(bean.getResourceBundleInclude());
    bean.setResourceBundleInclude(TEST_STRING);
    assertEquals(TEST_STRING, bean.getResourceBundleInclude());
  }

  @Test
  public void testSetVardValInclude() {
    assertNull(bean.getVardValInclude());
    bean.setVardValInclude(TEST_STRING);
    assertEquals(TEST_STRING, bean.getVardValInclude());
  }

  @Test
  public void testExternalApplicationRL() {
    assertNull(bean.getExternalApplicationURL());
    bean.setExternalApplicationURL(TEST_STRING);
    assertEquals(TEST_STRING, bean.getExternalApplicationURL());
  }

  /**
   * Test of setFavIcon method, of class SettingsBean.
   */
  @Test
  public void testSetFavIcon() {
    assertNull(bean.getFavIcon());
    bean.setFavIcon(TEST_STRING);
    assertEquals(TEST_STRING, bean.getFavIcon());
  }

  @Test
  public void testGoogleMapInitInclude() {
    assertNull(bean.getGoogleMapInitInclude());
    bean.setGoogleMapInitInclude(TEST_STRING);
    assertEquals(TEST_STRING, bean.getGoogleMapInitInclude());
  }

  @Test
  public void testErrorReportFormInclude() {
    assertNull(bean.getErrorReportFormInclude());
    bean.setErrorReportFormInclude(TEST_STRING);
    assertEquals(TEST_STRING, bean.getErrorReportFormInclude());
  }

  @Test
  public void testErrorReportConfirmInclude() {
    assertNull(bean.getErrorReportConfirmInclude());
    bean.setErrorReportConfirmInclude(TEST_STRING);
    assertEquals(TEST_STRING, bean.getErrorReportConfirmInclude());
  }
}
