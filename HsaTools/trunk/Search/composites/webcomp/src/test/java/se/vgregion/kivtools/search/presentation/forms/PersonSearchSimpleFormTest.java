package se.vgregion.kivtools.search.presentation.forms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PersonSearchSimpleFormTest {

  private static final String SPACE = " ";
  private static final String TEST = "Test";
  private PersonSearchSimpleForm form;

  @Before
  public void setUp() throws Exception {
    form = new PersonSearchSimpleForm();
  }

  @Test
  public void testIsEmpty() {
    form.setGivenName(null);
    form.setSurname(null);
    form.setUserId(null);

    assertTrue(form.isEmpty());
    form.setGivenName(TEST);
    assertFalse(form.isEmpty());
    form.setSurname(TEST);
    assertFalse(form.isEmpty());
    form.setUserId(TEST);
    assertFalse(form.isEmpty());

    form.setGivenName(SPACE);
    form.setSurname(SPACE);
    form.setUserId(SPACE);
    assertTrue(form.isEmpty());
    form.setGivenName(TEST);
    assertFalse(form.isEmpty());
    form.setSurname(TEST);
    assertFalse(form.isEmpty());
    form.setGivenName(SPACE);
    assertFalse(form.isEmpty());
  }
}
