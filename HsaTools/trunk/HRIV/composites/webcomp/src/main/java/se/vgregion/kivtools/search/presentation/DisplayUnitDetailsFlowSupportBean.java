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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
@SuppressWarnings("serial")
public class DisplayUnitDetailsFlowSupportBean implements Serializable {
  private static final String CLASS_NAME = DisplayUnitDetailsFlowSupportBean.class.getName();
  private Log logger = LogFactory.getLog(this.getClass());
  private SearchService searchService;
  private String accessibilityDatabaseIntegrationGetIdUrl;
  private String accessibilityDatabaseIntegrationGetInfoUrl;
  private String useMvkIntegration;
  private String mvkGuid;
  private String mvkUrl;

  public String getMvkUrl() {
    return mvkUrl;
  }

  public void setMvkUrl(String mvkUrl) {
    this.mvkUrl = mvkUrl;
  }

  public String getMvkGuid() {
    return mvkGuid;
  }

  public void setMvkGuid(String mvkGuid) {
    this.mvkGuid = mvkGuid;
  }

  public String getUseMvkIntegration() {
    return useMvkIntegration;
  }

  public void setUseMvkIntegration(String useMvkIntegration) {
    this.useMvkIntegration = useMvkIntegration;
  }

  public SearchService getSearchService() {
    return searchService;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public Unit getUnitDetails(String hsaId) throws NoConnectionToServerException {
    logger.info(CLASS_NAME + "::getUnitDetails(hsaId=" + hsaId + ")");
    Unit u = null;
    try {
      u = getSearchService().getUnitByHsaId(hsaId);
    } catch (NoConnectionToServerException e) {
      // We have no good connection to LDAP server and should be able to
      // tell the user we have no hope of success.
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      return new Unit();
    }

    if ("true".equals(useMvkIntegration)) {
      new MvkClient(mvkGuid, mvkUrl).assignCaseTypes(u);
    }

    return u;
  }

  public void logger(String msg) {
    logger.info(msg);
  }

  public String getAccessibilityDatabaseIntegrationGetIdUrl() {
    return accessibilityDatabaseIntegrationGetIdUrl;
  }

  public void setAccessibilityDatabaseIntegrationGetIdUrl(String accessibilityDatabaseIntegrationGetIdUrl) {
    this.accessibilityDatabaseIntegrationGetIdUrl = accessibilityDatabaseIntegrationGetIdUrl;
  }

  public String getAccessibilityDatabaseIntegrationGetInfoUrl() {
    return accessibilityDatabaseIntegrationGetInfoUrl;
  }

  public void setAccessibilityDatabaseIntegrationGetInfoUrl(String accessibilityDatabaseIntegrationGetInfoUrl) {
    this.accessibilityDatabaseIntegrationGetInfoUrl = accessibilityDatabaseIntegrationGetInfoUrl;
  }
}