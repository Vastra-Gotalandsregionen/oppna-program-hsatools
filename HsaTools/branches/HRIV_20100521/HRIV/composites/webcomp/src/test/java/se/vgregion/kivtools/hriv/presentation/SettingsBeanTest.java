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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
    this.bean = new SettingsBean();
  }

  @Test
  public void testSetInformationArea() {
    PojoTester.testProperty(this.bean, "informationArea", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSearchFormInclude() {
    PojoTester.testProperty(this.bean, "searchFormInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testBodyInclude() {
    PojoTester.testProperty(this.bean, "bodyInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMobileUrl() {
    PojoTester.testProperty(this.bean, "mobileUrl", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetVerifyV1() {
    PojoTester.testProperty(this.bean, "verifyV1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTitle() {
    PojoTester.testProperty(this.bean, "title", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetSearchResultTitle() {
    PojoTester.testProperty(this.bean, "searchResultTitle", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetLinkToIMOnServer() {
    PojoTester.testProperty(this.bean, "linkToIMOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowLinkToIM() {
    PojoTester.testProperty(this.bean, "showLinkToIM", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetLinkToIMBase() {
    PojoTester.testProperty(this.bean, "linkToIMBase", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseShowCloseUnits() {
    PojoTester.testProperty(this.bean, "useShowCloseUnits", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMetaAuthor() {
    PojoTester.testProperty(this.bean, "metaAuthor", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMetaCopyright() {
    PojoTester.testProperty(this.bean, "metaCopyright", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMvkLoginUrl() {
    PojoTester.testProperty(this.bean, "mvkLoginUrl", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseMvk() {
    PojoTester.testProperty(this.bean, "useMvk", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUsePrinting() {
    PojoTester.testProperty(this.bean, "usePrinting", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseListenLink() {
    PojoTester.testProperty(this.bean, "useListenLink", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetListenLinkCode2() {
    PojoTester.testProperty(this.bean, "listenLinkCode2", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetListenLinkCode1() {
    PojoTester.testProperty(this.bean, "listenLinkCode1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseTrackingCode() {
    PojoTester.testProperty(this.bean, "useTrackingCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTrackingCodeOnServer() {
    PojoTester.testProperty(this.bean, "trackingCodeOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetTrackingCode() {
    PojoTester.testProperty(this.bean, "trackingCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCode1() {
    PojoTester.testProperty(this.bean, "showUnitCode1", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCode2() {
    PojoTester.testProperty(this.bean, "showUnitCode2", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetUseShowUnitCode() {
    PojoTester.testProperty(this.bean, "useShowUnitCode", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetShowUnitCodeOnServer() {
    PojoTester.testProperty(this.bean, "showUnitCodeOnServer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFooter() {
    PojoTester.testProperty(this.bean, "footer", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetHeader() {
    PojoTester.testProperty(this.bean, "header", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetScripts() {
    assertEquals(0, this.bean.getScriptPaths().size());

    try {
      this.bean.setScripts(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    this.bean.setScripts("");
    assertEquals(0, this.bean.getScriptPaths().size());

    this.bean.setScripts(TEST_STRING);
    assertEquals(1, this.bean.getScriptPaths().size());
    assertEquals(TEST_STRING, this.bean.getScriptPaths().get(0));

    this.bean.setScripts("test,test2");
    assertEquals(2, this.bean.getScriptPaths().size());
    assertEquals("test", this.bean.getScriptPaths().get(0));
    assertEquals("test2", this.bean.getScriptPaths().get(1));
  }

  @Test
  public void testSetFindRouteLinksArray() {
    assertNotNull(this.bean.getFindRouteLinksArray());
    ArrayList<Link> routeLinksArray = this.bean.getFindRouteLinksArray();
    assertEquals(0, this.bean.getFindRouteLinksArray().size());
    routeLinksArray.add(new Link("", "", ""));
    assertEquals(1, this.bean.getFindRouteLinksArray().size());
  }

  @Test
  public void testSetStyles() {
    PojoTester.testProperty(this.bean, "styles", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFindRouteLinks() {
    assertEquals(0, this.bean.getFindRouteLinksArray().size());
    this.bean.setFindRouteLinks("test::test2::test3");
    ArrayList<Link> findRouteLinksArray = this.bean.getFindRouteLinksArray();
    assertEquals(1, findRouteLinksArray.size());
    assertEquals("test", findRouteLinksArray.get(0).getHref());
    assertEquals("test2", findRouteLinksArray.get(0).getName());
    assertEquals("test3", findRouteLinksArray.get(0).getToParamName());

    this.bean.setFindRouteLinks("test::test2;test4::test5::test6");
    findRouteLinksArray = this.bean.getFindRouteLinksArray();
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
    PojoTester.testProperty(this.bean, "distanceToUnits", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFallbackOnAddressForMap() {
    PojoTester.testProperty(this.bean, "fallbackOnAddressForMap", boolean.class, false, true, false);
  }

  @Test
  public void testSetTestingMode() {
    assertFalse(this.bean.getTestingMode());
    assertEquals("false", this.bean.getTestingModeAsString());
    this.bean.setTestingMode(true);
    assertTrue(this.bean.getTestingMode());
    assertEquals("true", this.bean.getTestingModeAsString());
  }

  @Test
  public void testSetUseAccessibilityDatabaseIntegration() {
    PojoTester.testProperty(this.bean, "useAccessibilityDatabaseIntegration", boolean.class, false, true, false);
  }

  @Test
  public void testSetGoogleMapsKey() {
    PojoTester.testProperty(this.bean, "googleMapsKey", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetMainTop() {
    PojoTester.testProperty(this.bean, "mainTop", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetStartPage() {
    PojoTester.testProperty(this.bean, "startPage", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetCareTypePage() {
    PojoTester.testProperty(this.bean, "careTypePage", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetGeoRegion() {
    PojoTester.testProperty(this.bean, "geoRegion", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetResourceBundleInclude() {
    PojoTester.testProperty(this.bean, "resourceBundleInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testExternalApplicationURL() {
    PojoTester.testProperty(this.bean, "externalApplicationURL", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testSetFavIcon() {
    PojoTester.testProperty(this.bean, "favIcon", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testGoogleMapInitInclude() {
    PojoTester.testProperty(this.bean, "googleMapInitInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testErrorReportFormInclude() {
    PojoTester.testProperty(this.bean, "errorReportFormInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testErrorReportConfirmInclude() {
    PojoTester.testProperty(this.bean, "errorReportConfirmInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testUnitHoursInclude() {
    PojoTester.testProperty(this.bean, "unitHoursInclude", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testPublicCaptchaKey() {
    PojoTester.testProperty(this.bean, "publicCaptchaKey", String.class, null, TEST_STRING, TEST_STRING2);
  }

  @Test
  public void testCareTypeInfoUrl() {
    PojoTester.testProperty(this.bean, "careTypeInfoUrl", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitSearchResultSingleUnitInclude() {
    PojoTester.testProperty(this.bean, "unitSearchResultSingleUnitInclude", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitDetailsInclude() {
    PojoTester.testProperty(this.bean, "unitDetailsInclude", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitAccessibilityInclude() {
    PojoTester.testProperty(this.bean, "unitAccessibilityInclude", String.class, null, TEST_STRING, "Test2");
  }

  @Test
  public void testUnitSearchResultSortOrderInclude() {
    PojoTester.testProperty(this.bean, "unitSearchResultSortOrderInclude", String.class, null, TEST_STRING, "Test2");
  }
}
