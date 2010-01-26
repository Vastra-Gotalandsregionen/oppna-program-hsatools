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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ErrorReportingService;

public class ErrorReportingFlowSupportBeanTest {

  private ErrorReportingFlowSupportBean errorReportingFlowSupportBean;
  private ErrorReportingServiceMock errorReportingService;

  @Before
  public void setUp() throws Exception {
    errorReportingService = new ErrorReportingServiceMock();
    errorReportingFlowSupportBean = new ErrorReportingFlowSupportBean();
    errorReportingFlowSupportBean.setErrorReportingService(errorReportingService);
  }

  @Test
  public void testInstantiation() {
    ErrorReportingFlowSupportBean errorReportingFlowSupportBean = new ErrorReportingFlowSupportBean();
    assertNotNull(errorReportingFlowSupportBean);
  }

  @Test
  public void testReportError() {
    errorReportingFlowSupportBean.reportError("TestDn", "Test", "TestDetailLink");
    assertEquals("TestDn", errorReportingService.dn);
    assertEquals("Test", errorReportingService.reportText);
    assertEquals("TestDetailLink", errorReportingService.detailLink);
  }

  private static class ErrorReportingServiceMock implements ErrorReportingService {
    private String dn;
    private String reportText;
    private String detailLink;

    @Override
    public void reportError(String dn, String reportText, String detailLink) {
      this.dn = dn;
      this.reportText = reportText;
      this.detailLink = detailLink;
    }
  }
}
