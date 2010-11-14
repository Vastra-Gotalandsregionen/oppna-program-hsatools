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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Holds HRIV specific settings and is made available via Spring webflow under the name "settings".
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class SettingsBean implements Serializable {
  private static final long serialVersionUID = 246274450053259084L;
  private String resourceBundleInclude;
  private String googleMapInitInclude;
  private String unitDetailsInclude;
  private String unitAccessibilityInclude;
  private String unitSearchResultSingleUnitInclude;
  private String unitSearchResultSortOrderInclude;
  private String errorReportFormInclude;
  private String errorReportConfirmInclude;
  private String unitHoursInclude;
  private String publicCaptchaKey;
  private boolean testingMode;
  private boolean useAccessibilityDatabaseIntegration;
  private boolean fallbackOnAddressForMap;
  private String googleMapsKey;
  private String distanceToUnits;
  private String styles;
  private final ArrayList<Link> findRouteLinksArray = new ArrayList<Link>();
  private String header;
  private final ArrayList<String> scriptPaths = new ArrayList<String>();
  private String footer;
  private String useShowUnitCode;
  private String showUnitCodeOnServer;
  private String showUnitCode1;
  private String showUnitCode2;
  private String useTrackingCode;
  private String trackingCodeOnServer;
  private String trackingCode;
  private String useListenLink;
  private String listenLinkCode1;
  private String listenLinkCode2;
  private String usePrinting;
  private String useMvk;
  private String mainTop;
  private String startPage;
  private String careTypePage;
  private String mvkLoginUrl;
  private String metaAuthor;
  private String metaCopyright;
  private String useShowCloseUnits;
  private String showLinkToIM;
  private String title;
  private String searchResultTitle;
  private String linkToIMOnServer;
  private String linkToIMBase;
  private String verifyV1;
  private String geoRegion;
  private String mobileUrl;
  private String informationArea;
  private String externalApplicationURL;
  private String favIcon;
  private String careTypeInfoUrl;
  private String bodyInclude;
  private String searchFormInclude;

  public String getInformationArea() {
    return this.informationArea;
  }

  public void setInformationArea(String informationArea) {
    this.informationArea = informationArea;
  }

  public String getMobileUrl() {
    return this.mobileUrl;
  }

  public void setMobileUrl(String mobileUrl) {
    this.mobileUrl = mobileUrl;
  }

  public String getVerifyV1() {
    return this.verifyV1;
  }

  public void setVerifyV1(String verifyV1) {
    this.verifyV1 = verifyV1;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSearchResultTitle() {
    return this.searchResultTitle;
  }

  public void setSearchResultTitle(String searchResultTitle) {
    this.searchResultTitle = searchResultTitle;
  }

  public String getLinkToIMOnServer() {
    return this.linkToIMOnServer;
  }

  public void setLinkToIMOnServer(String linkToIMOnServer) {
    this.linkToIMOnServer = linkToIMOnServer;
  }

  public String getShowLinkToIM() {
    return this.showLinkToIM;
  }

  public void setShowLinkToIM(String showLinkToIM) {
    this.showLinkToIM = showLinkToIM;
  }

  public String getLinkToIMBase() {
    return this.linkToIMBase;
  }

  public void setLinkToIMBase(String linkToIMBase) {
    this.linkToIMBase = linkToIMBase;
  }

  public String getUseShowCloseUnits() {
    return this.useShowCloseUnits;
  }

  public void setUseShowCloseUnits(String useShowCloseUnits) {
    this.useShowCloseUnits = useShowCloseUnits;
  }

  public String getMetaAuthor() {
    return this.metaAuthor;
  }

  public void setMetaAuthor(String metaAuthor) {
    this.metaAuthor = metaAuthor;
  }

  public String getMetaCopyright() {
    return this.metaCopyright;
  }

  public void setMetaCopyright(String metaCopyright) {
    this.metaCopyright = metaCopyright;
  }

  public String getMvkLoginUrl() {
    return this.mvkLoginUrl;
  }

  public void setMvkLoginUrl(String mvkLoginUrl) {
    this.mvkLoginUrl = mvkLoginUrl;
  }

  public String getUseMvk() {
    return this.useMvk;
  }

  public void setUseMvk(String useMvk) {
    this.useMvk = useMvk;
  }

  public String getUsePrinting() {
    return this.usePrinting;
  }

  public void setUsePrinting(String usePrinting) {
    this.usePrinting = usePrinting;
  }

  public String getUseListenLink() {
    return this.useListenLink;
  }

  public void setUseListenLink(String useListenLink) {
    this.useListenLink = useListenLink;
  }

  public String getListenLinkCode2() {
    return this.listenLinkCode2;
  }

  public void setListenLinkCode2(String listenLinkCode2) {
    this.listenLinkCode2 = listenLinkCode2;
  }

  public String getListenLinkCode1() {
    return this.listenLinkCode1;
  }

  public void setListenLinkCode1(String listenLinkCode1) {
    this.listenLinkCode1 = listenLinkCode1;
  }

  public String getUseTrackingCode() {
    return this.useTrackingCode;
  }

  public void setUseTrackingCode(String useTrackingCode) {
    this.useTrackingCode = useTrackingCode;
  }

  public String getTrackingCodeOnServer() {
    return this.trackingCodeOnServer;
  }

  public void setTrackingCodeOnServer(String trackingCodeOnServer) {
    this.trackingCodeOnServer = trackingCodeOnServer;
  }

  public String getTrackingCode() {
    return this.trackingCode;
  }

  public void setTrackingCode(String trackingCode) {
    this.trackingCode = trackingCode;
  }

  public String getShowUnitCode1() {
    return this.showUnitCode1;
  }

  public void setShowUnitCode1(String showUnitCode1) {
    this.showUnitCode1 = showUnitCode1;
  }

  public String getShowUnitCode2() {
    return this.showUnitCode2;
  }

  public void setShowUnitCode2(String showUnitCode2) {
    this.showUnitCode2 = showUnitCode2;
  }

  public String getUseShowUnitCode() {
    return this.useShowUnitCode;
  }

  public void setUseShowUnitCode(String useShowUnitCode) {
    this.useShowUnitCode = useShowUnitCode;
  }

  public String getShowUnitCodeOnServer() {
    return this.showUnitCodeOnServer;
  }

  public void setShowUnitCodeOnServer(String showUnitCodeOnServer) {
    this.showUnitCodeOnServer = showUnitCodeOnServer;
  }

  public String getFooter() {
    return this.footer;
  }

  public void setFooter(String footer) {
    this.footer = footer;
  }

  public String getHeader() {
    return this.header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  /**
   * Splits the provided scripts-string at comma and adds each part to the list of script paths to render.
   * 
   * @param scripts A comma-separated string of script paths to render.
   */
  public void setScripts(String scripts) {
    this.scriptPaths.clear();
    List<String> tempList = Arrays.asList(scripts.split(","));
    for (String scriptPath : tempList) {
      if (scriptPath.length() > 0) {
        this.scriptPaths.add(scriptPath);
      }
    }
  }

  public ArrayList<Link> getFindRouteLinksArray() {
    return this.findRouteLinksArray;
  }

  public String getStyles() {
    return this.styles;
  }

  public void setStyles(String styles) {
    this.styles = styles;
  }

  /**
   * Splits the provided findRouteLinks at semi-colon and create new Link-objects which are added to the array of findRouteLinks.
   * 
   * @param findRouteLinks A string in the format href::name::optional param;href::name:optional param;...
   */
  public void setFindRouteLinks(String findRouteLinks) {
    this.findRouteLinksArray.clear();
    String[] routeLinks = findRouteLinks.split(";");
    for (String routeLink : routeLinks) {
      String[] routeLinkComponents = routeLink.split("::");
      String href = routeLinkComponents[0];
      String name = routeLinkComponents[1];
      String toParamName = "";
      String fromParamName = "";
      if (routeLinkComponents.length > 2) {
        toParamName = routeLinkComponents[2];
      }

      if (routeLinkComponents.length > 3) {
        fromParamName = routeLinkComponents[3];
      }

      Link l = new Link(href, name, toParamName, fromParamName);
      this.addRouteLink(l);
    }
  }

  public String getDistanceToUnits() {
    return this.distanceToUnits;
  }

  public void setDistanceToUnits(String distanceToUnits) {
    this.distanceToUnits = distanceToUnits;
  }

  public boolean isFallbackOnAddressForMap() {
    return this.fallbackOnAddressForMap;
  }

  public void setFallbackOnAddressForMap(boolean fallbackOnAddressForMap) {
    this.fallbackOnAddressForMap = fallbackOnAddressForMap;
  }

  public void setTestingMode(boolean b) {
    this.testingMode = b;
  }

  public boolean getTestingMode() {
    return this.testingMode;
  }

  public String getTestingModeAsString() {
    return String.valueOf(this.testingMode);
  }

  public boolean isUseAccessibilityDatabaseIntegration() {
    return this.useAccessibilityDatabaseIntegration;
  }

  public void setUseAccessibilityDatabaseIntegration(boolean useAccessibilityDatabaseIntegration) {
    this.useAccessibilityDatabaseIntegration = useAccessibilityDatabaseIntegration;
  }

  public String getGoogleMapsKey() {
    return this.googleMapsKey;
  }

  public void setGoogleMapsKey(String googleMapsKey) {
    this.googleMapsKey = googleMapsKey;
  }

  void addRouteLink(Link l) {
    this.findRouteLinksArray.add(l);
  }

  public List<String> getScriptPaths() {
    return Collections.unmodifiableList(this.scriptPaths);
  }

  public String getMainTop() {
    return this.mainTop;
  }

  public void setMainTop(String mainTop) {
    this.mainTop = mainTop;
  }

  public String getStartPage() {
    return this.startPage;
  }

  public void setStartPage(String startPage) {
    this.startPage = startPage;
  }

  public String getCareTypePage() {
    return this.careTypePage;
  }

  public void setCareTypePage(String careTypePage) {
    this.careTypePage = careTypePage;
  }

  public String getGeoRegion() {
    return this.geoRegion;
  }

  public void setGeoRegion(String geoRegion) {
    this.geoRegion = geoRegion;
  }

  public String getResourceBundleInclude() {
    return this.resourceBundleInclude;
  }

  public void setResourceBundleInclude(String resourceBundleInclude) {
    this.resourceBundleInclude = resourceBundleInclude;
  }

  public String getExternalApplicationURL() {
    return this.externalApplicationURL;
  }

  public void setExternalApplicationURL(String externalApplicationURL) {
    this.externalApplicationURL = externalApplicationURL;
  }

  /**
   * Returns URL for favourite icon.
   * 
   * @return the favIcon
   */
  public String getFavIcon() {
    return this.favIcon;
  }

  /**
   * Sets URL for favourite icon.
   * 
   * @param favIcon the favIcon to set
   */
  public void setFavIcon(String favIcon) {
    this.favIcon = favIcon;
  }

  public void setGoogleMapInitInclude(String googleMapInitInclude) {
    this.googleMapInitInclude = googleMapInitInclude;
  }

  public String getGoogleMapInitInclude() {
    return this.googleMapInitInclude;
  }

  public void setErrorReportFormInclude(String errorReportFormInclude) {
    this.errorReportFormInclude = errorReportFormInclude;
  }

  public String getErrorReportFormInclude() {
    return this.errorReportFormInclude;
  }

  public void setErrorReportConfirmInclude(String errorReportConfirmInclude) {
    this.errorReportConfirmInclude = errorReportConfirmInclude;
  }

  public String getErrorReportConfirmInclude() {
    return this.errorReportConfirmInclude;
  }

  public void setPublicCaptchaKey(String publicCaptchaKey) {
    this.publicCaptchaKey = publicCaptchaKey;
  }

  public String getPublicCaptchaKey() {
    return this.publicCaptchaKey;
  }

  public void setCareTypeInfoUrl(String careTypeInfoUrl) {
    this.careTypeInfoUrl = careTypeInfoUrl;
  }

  public String getCareTypeInfoUrl() {
    return this.careTypeInfoUrl;
  }

  public void setSearchFormInclude(String searchFormInclude) {
    this.searchFormInclude = searchFormInclude;
  }

  public String getSearchFormInclude() {
    return this.searchFormInclude;
  }

  public void setBodyInclude(String bodyInclude) {
    this.bodyInclude = bodyInclude;
  }

  public String getBodyInclude() {
    return this.bodyInclude;
  }

  public void setUnitSearchResultSingleUnitInclude(String unitSearchResultSingleUnitInclude) {
    this.unitSearchResultSingleUnitInclude = unitSearchResultSingleUnitInclude;
  }

  public String getUnitSearchResultSingleUnitInclude() {
    return this.unitSearchResultSingleUnitInclude;
  }

  public void setUnitSearchResultSortOrderInclude(String unitSearchResultSortOrderInclude) {
    this.unitSearchResultSortOrderInclude = unitSearchResultSortOrderInclude;
  }

  public String getUnitSearchResultSortOrderInclude() {
    return this.unitSearchResultSortOrderInclude;
  }

  public void setUnitHoursInclude(String unitHoursInclude) {
    this.unitHoursInclude = unitHoursInclude;
  }

  public String getUnitHoursInclude() {
    return this.unitHoursInclude;
  }

  public void setUnitDetailsInclude(String unitDetailsInclude) {
    this.unitDetailsInclude = unitDetailsInclude;
  }

  public String getUnitDetailsInclude() {
    return this.unitDetailsInclude;
  }

  public void setUnitAccessibilityInclude(String unitAccessibilityInclude) {
    this.unitAccessibilityInclude = unitAccessibilityInclude;
  }

  public String getUnitAccessibilityInclude() {
    return this.unitAccessibilityInclude;
  }
}
