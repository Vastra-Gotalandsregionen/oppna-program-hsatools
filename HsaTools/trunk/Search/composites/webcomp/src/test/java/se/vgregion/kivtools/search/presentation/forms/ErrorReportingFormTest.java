package se.vgregion.kivtools.search.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ErrorReportingFormTest {

  private static final String TEST = "Test";
  private ErrorReportingForm errorReportingForm;

  @Before
  public void setUp() throws Exception {
    errorReportingForm = new ErrorReportingForm();
  }

  @Test
  public void testInstantiation() {
    ErrorReportingForm errorReportingForm = new ErrorReportingForm();
    assertNotNull(errorReportingForm);

  }

  @Test
  public void testDn() {
    assertNull(errorReportingForm.getDn());
    errorReportingForm.setDn(TEST);
    assertEquals(TEST, errorReportingForm.getDn());
  }

  @Test
  public void testReportText() {
    assertNull(errorReportingForm.getReportText());
    errorReportingForm.setReportText(TEST);
    assertEquals(TEST, errorReportingForm.getReportText());
  }
}
