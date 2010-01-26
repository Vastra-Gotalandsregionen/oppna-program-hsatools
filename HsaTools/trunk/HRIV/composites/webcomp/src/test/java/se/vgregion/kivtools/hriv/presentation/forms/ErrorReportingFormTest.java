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
package se.vgregion.kivtools.hriv.presentation.forms;

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
