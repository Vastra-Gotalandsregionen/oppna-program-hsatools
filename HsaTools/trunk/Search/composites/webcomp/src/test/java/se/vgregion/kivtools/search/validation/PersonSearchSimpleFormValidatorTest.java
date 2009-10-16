package se.vgregion.kivtools.search.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;

public class PersonSearchSimpleFormValidatorTest {
  private static final String SUCCESSFUL_OPERATION = "success";
  private PersonSearchSimpleFormValidator validator;
  private PersonSearchSimpleForm form;

  @Before
  public void setUp() throws Exception {
    validator = new PersonSearchSimpleFormValidator();
    form = new PersonSearchSimpleForm();
  }

  @Test
  public void testValidate() throws IncorrectUserInputException {
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setGivenName("a");
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setGivenName("aa");
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));

    form.setGivenName("");
    form.setSurname("a");
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setSurname("aa");
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));

    form.setSurname("");
    form.setUserId("a");
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setUserId("aa");
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));
  }
}
