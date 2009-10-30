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
package se.vgregion.kivtools.search.presentation;

import se.vgregion.kivtools.search.svc.ErrorReportingService;

/**
 * Support bean for the error reporting flow in SIK.
 * 
 * @author Joakim Olsson
 */
public class ErrorReportingFlowSupportBean {
  private ErrorReportingService errorReportingService;

  /**
   * Reports an error in the LDAP directory for the provided DN.
   * 
   * @param dn The DN of the node in the LDAP directory that is in error.
   * @param reportText The report text from the user.
   * @return Always returns true.
   */
  public boolean reportError(String dn, String reportText) {
    errorReportingService.reportError(dn, reportText);

    return true;
  }

  public void setErrorReportingService(ErrorReportingService errorReportingService) {
    this.errorReportingService = errorReportingService;
  }
}
