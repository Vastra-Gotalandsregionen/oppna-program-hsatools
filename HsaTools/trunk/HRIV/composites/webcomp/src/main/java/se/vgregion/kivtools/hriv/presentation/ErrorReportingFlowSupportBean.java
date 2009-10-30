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

import se.vgregion.kivtools.hriv.presentation.validation.CaptchaValidator;
import se.vgregion.kivtools.search.svc.ErrorReportingService;

/**
 * Support bean for the error reporting flow in SIK.
 * 
 * @author Joakim Olsson
 */
public class ErrorReportingFlowSupportBean {
  private ErrorReportingService errorReportingService;
  private CaptchaValidator captchaValidator;

  /**
   * Reports an error in the LDAP directory for the provided DN.
   * 
   * @param dn The DN of the node in the LDAP directory that is in error.
   * @param reportText The report text from the user.
   * @param detailLink A link to the detail-page of the unit or person.
   * @param captchaChallenge The captcha-challenge the user received.
   * @param captchaResponse The captcha-response from the user.
   * @param remoteAddress The users IP-address.
   * @return Always returns true.
   */
  public String reportError(String dn, String reportText, String detailLink, String captchaChallenge, String captchaResponse, String remoteAddress) {
    String result = null;
    if (captchaValidator.validate(captchaChallenge, captchaResponse, remoteAddress)) {
      errorReportingService.reportError(dn, reportText, detailLink);
      result = "success";
    } else {
      result = "failure";
    }
    return result;
  }

  public void setErrorReportingService(ErrorReportingService errorReportingService) {
    this.errorReportingService = errorReportingService;
  }

  public void setCaptchaValidator(CaptchaValidator captchaValidator) {
    this.captchaValidator = captchaValidator;
  }
}
