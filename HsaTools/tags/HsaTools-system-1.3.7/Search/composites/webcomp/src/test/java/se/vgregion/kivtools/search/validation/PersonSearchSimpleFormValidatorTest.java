/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

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
