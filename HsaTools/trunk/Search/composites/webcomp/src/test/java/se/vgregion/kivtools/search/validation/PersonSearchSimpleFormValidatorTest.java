package se.vgregion.kivtools.search.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.common.Constants;
import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;

public class PersonSearchSimpleFormValidatorTest {
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
    assertEquals(Constants.SUCCESSFUL_OPERATION, validator.validate(form));

    form.setGivenName("");
    form.setSirName("a");
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setSirName("aa");
    assertEquals(Constants.SUCCESSFUL_OPERATION, validator.validate(form));

    form.setSirName("");
    form.setVgrId("a");
    try {
      validator.validate(form);
      fail("Expected IncorrectUserInputException");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setVgrId("aa");
    assertEquals(Constants.SUCCESSFUL_OPERATION, validator.validate(form));
  }
}
