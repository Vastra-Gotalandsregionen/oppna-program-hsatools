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
    errorReportingFlowSupportBean.reportError("TestDn", "Test");
    assertEquals("TestDn", errorReportingService.dn);
    assertEquals("Test", errorReportingService.reportText);
  }

  private static class ErrorReportingServiceMock implements ErrorReportingService {
    private String dn;
    private String reportText;

    @Override
    public void reportError(String dn, String reportText) {
      this.dn = dn;
      this.reportText = reportText;
    }
  }
}
