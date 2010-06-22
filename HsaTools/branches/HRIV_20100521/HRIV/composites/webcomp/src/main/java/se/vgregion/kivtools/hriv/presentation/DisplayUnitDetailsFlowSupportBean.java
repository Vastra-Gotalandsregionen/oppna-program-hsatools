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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.util.MvkClient;

/**
 * Support bean for the display unit details flow.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
public class DisplayUnitDetailsFlowSupportBean implements Serializable {
  private static final long serialVersionUID = 7930671677796612164L;
  private static final String CLASS_NAME = DisplayUnitDetailsFlowSupportBean.class.getName();

  private Log logger = LogFactory.getLog(this.getClass());
  private SearchService searchService;
  private MvkClient mvkClient;
  private String useMvkIntegration;

  public String getUseMvkIntegration() {
    return useMvkIntegration;
  }

  public void setUseMvkIntegration(String useMvkIntegration) {
    this.useMvkIntegration = useMvkIntegration;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setMvkClient(MvkClient mvkClient) {
    this.mvkClient = mvkClient;
  }

  /**
   * Retrieves details for the unit with the provided hsaIdentity.
   * 
   * @param hsaId The hsaIdentity of the unit to retrieve details for.
   * @param externalContext The external Faces-context.
   * @return A populated Unit object.
   * @throws KivException if there is a problem retrieving the unit from the LDAP directory.
   */
  public Unit getUnitDetails(String hsaId, ExternalContext externalContext) throws KivException {
    logger.debug(CLASS_NAME + "::getUnitDetails(hsaId=" + hsaId + ")");
    Unit u = null;
    try {
      u = searchService.getUnitByHsaId(hsaId);
    } catch (NoConnectionToServerException e) {
      // We have no good connection to LDAP server and should be able to
      // tell the user we have no hope of success.
      throw e;
    } catch (KivNoDataFoundException e) {
      if (externalContext.getNativeResponse() instanceof HttpServletResponse) {
        ((HttpServletResponse) externalContext.getNativeResponse()).setStatus(404);
      }   
      throw e;
    }

    if ("true".equals(useMvkIntegration)) {
      mvkClient.assignCaseTypes(u);
    }

    return u;
  }
}
