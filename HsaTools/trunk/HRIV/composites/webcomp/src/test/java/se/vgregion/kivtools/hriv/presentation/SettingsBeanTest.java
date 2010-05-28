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

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.PojoTester;

public class SettingsBeanTest {

  private static final String TEST_STRING = "test";
  private static final String TEST_STRING2 = "test2";

  private SettingsBean bean;

  @Before
  public void setUp() throws Exception {
    bean = new SettingsBean();
  }

  @Test
  public void testSetInformationArea() {
    PojoTester.testProperty(bean, "informationArea", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSearchFormInclude() {
    PojoTester.testProperty(bean, "searchFormInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testBodyInclude() {
    PojoTester.testProperty(bean, "bodyInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMobileUrl() {
    PojoTester.testProperty(bean, "mobileUrl", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetVerifyV1() {
    PojoTester.testProperty(bean, "verifyV1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTitle() {
    PojoTester.testProperty(bean, "title", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetSearchResultTitle() {
    PojoTester.testProperty(bean, "searchResultTitle", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetLinkToIMOnServer() {
    PojoTester.testProperty(bean, "linkToIMOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowLinkToIM() {
    PojoTester.testProperty(bean, "showLinkToIM", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetLinkToIMBase() {
    PojoTester.testProperty(bean, "linkToIMBase", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseShowCloseUnits() {
    PojoTester.testProperty(bean, "useShowCloseUnits", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMetaAuthor() {
    PojoTester.testProperty(bean, "metaAuthor", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMetaCopyright() {
    PojoTester.testProperty(bean, "metaCopyright", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMvkLoginUrl() {
    PojoTester.testProperty(bean, "mvkLoginUrl", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseMvk() {
    PojoTester.testProperty(bean, "useMvk", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUsePrinting() {
    PojoTester.testProperty(bean, "usePrinting", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseListenLink() {
    PojoTester.testProperty(bean, "useListenLink", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetListenLinkCode2() {
    PojoTester.testProperty(bean, "listenLinkCode2", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetListenLinkCode1() {
    PojoTester.testProperty(bean, "listenLinkCode1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseTrackingCode() {
    PojoTester.testProperty(bean, "useTrackingCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTrackingCodeOnServer() {
    PojoTester.testProperty(bean, "trackingCodeOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTrackingCode() {
    PojoTester.testProperty(bean, "trackingCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCode1() {
    PojoTester.testProperty(bean, "showUnitCode1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCode2() {
    PojoTester.testProperty(bean, "showUnitCode2", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseShowUnitCode() {
    PojoTester.testProperty(bean, "useShowUnitCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCodeOnServer() {
    PojoTester.testProperty(bean, "showUnitCodeOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFooter() {
    PojoTester.testProperty(bean, "footer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetHeader() {
    PojoTester.testProperty(bean, "header", String.class, null, TEST_STRING, TEST_STRING2);
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
    routeLinksArray.add(new Link("", "", "", ""));
    assertEquals(1, bean.getFindRouteLinksArray().size());
  }

  @Test
  public void testSetStyles() {
    PojoTester.testProperty(bean, "styles", String.class, null, TEST_STRING, TEST_STRING2);
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
    PojoTester.testProperty(bean, "distanceToUnits", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFallbackOnAddressForMap() {
    PojoTester.testProperty(bean, "fallbackOnAddressForMap", boolean.class, false, true, false);
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
    PojoTester.testProperty(bean, "useAccessibilityDatabaseIntegration", boolean.class, false, true, false);
  }

  @Test
  public void testSetGoogleMapsKey() {
    PojoTester.testProperty(bean, "googleMapsKey", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMainTop() {
    PojoTester.testProperty(bean, "mainTop", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetStartPage() {
    PojoTester.testProperty(bean, "startPage", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetCareTypePage() {
    PojoTester.testProperty(bean, "careTypePage", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetGeoRegion() {
    PojoTester.testProperty(bean, "geoRegion", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetResourceBundleInclude() {
    PojoTester.testProperty(bean, "resourceBundleInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testExternalApplicationURL() {
    PojoTester.testProperty(bean, "externalApplicationURL", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFavIcon() {
    PojoTester.testProperty(bean, "favIcon", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testGoogleMapInitInclude() {
    PojoTester.testProperty(bean, "googleMapInitInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testErrorReportFormInclude() {
    PojoTester.testProperty(bean, "errorReportFormInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testErrorReportConfirmInclude() {
    PojoTester.testProperty(bean, "errorReportConfirmInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testUnitHoursInclude() {
    PojoTester.testProperty(bean, "unitHoursInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testPublicCaptchaKey() {
    PojoTester.testProperty(bean, "publicCaptchaKey", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testCareTypeInfoUrl() {
    PojoTester.testProperty(bean, "careTypeInfoUrl", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitSearchResultSingleUnitInclude() {
    PojoTester.testProperty(bean, "unitSearchResultSingleUnitInclude", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitSearchResultSortOrderInclude() {
    PojoTester.testProperty(bean, "unitSearchResultSortOrderInclude", String.class, null, TEST_STRING, "Test2");
  }
}
