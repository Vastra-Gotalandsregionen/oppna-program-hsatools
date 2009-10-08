package se.vgregion.kivtools.search.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnitSearchSimpleFormTest {
  private static final String SPACE = " ";
  private static final String TEST = "Test";
  private UnitSearchSimpleForm form;

  @Before
  public void setUp() {
    form = new UnitSearchSimpleForm();
  }

  @Test
  public void testIsEmpty() {
    form.setUnitName(null);
    form.setSearchParamValue(null);

    assertTrue(form.isEmpty());
    form.setUnitName(TEST);
    assertFalse(form.isEmpty());
    form.setSearchParamValue(TEST);
    assertFalse(form.isEmpty());

    form.setUnitName(SPACE);
    form.setSearchParamValue(SPACE);
    assertTrue(form.isEmpty());
    form.setUnitName(TEST);
    assertFalse(form.isEmpty());
    form.setSearchParamValue(TEST);
    assertFalse(form.isEmpty());
    form.setUnitName(SPACE);
    assertFalse(form.isEmpty());
  }
}
