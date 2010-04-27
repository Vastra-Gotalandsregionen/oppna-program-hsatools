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

package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
@SuppressWarnings("serial")
public class DisplayUnitDetailsFlowSupportBean implements Serializable {
  private static final String CLASS_NAME = DisplayUnitDetailsFlowSupportBean.class.getName();
  private static final Log LOGGER = LogFactory.getLog(SearchPersonFlowSupportBean.class);
  private SearchService searchService;

  public SearchService getSearchService() {
    return searchService;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Retrieves details for the unit with the provided hsaIdentity.
   * 
   * @param hsaId The hsaIdentity for the unit to retrieve details for.
   * @return A Unit object populated with the details for the unit with the provided hsaIdentity.
   * @throws KivException if there is a problem retrieving the unit from the LDAP directory.
   */
  public Unit getUnitDetails(String hsaId) throws KivException {
    LOGGER.debug(CLASS_NAME + "::getUnitDetails(hsaId=" + hsaId + ")");
    return getSearchService().getUnitByHsaId(hsaId);
  }

  /**
   * Retrieves details for the unit with the provided dn.
   * 
   * @param dn The dn for the unit to retrieve details for.
   * @return A Unit object populated with the details for the unit with the provided dn.
   * @throws KivException if there is a problem retrieving the unit from the LDAP directory.
   */
  public Unit getUnitByDn(String dn) throws KivException {
    LOGGER.debug(CLASS_NAME + "::getUnitDetailsByDn(dn=" + dn + ")");
    return getSearchService().getUnitByDN(dn);
  }
}
