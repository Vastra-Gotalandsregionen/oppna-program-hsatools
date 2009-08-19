/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.search.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

public class UnitSearchSimpleFormValidatorTest {

  private static final String SUCCESSFUL_OPERATION = "success";
  private UnitSearchSimpleFormValidator validator;
  private UnitSearchSimpleForm form;

  @Before
  public void setUp() throws Exception {
    new MunicipalityHelper().setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-municipalities");
    validator = new UnitSearchSimpleFormValidator();
    form = new UnitSearchSimpleForm();
  }

  @Test
  public void testValidate() throws IncorrectUserInputException {
    try {
      validator.validate(form);
      fail("IncorrectUserInputException expected");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setSearchParamValue("a");
    try {
      validator.validate(form);
      fail("IncorrectUserInputException expected");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setSearchParamValue("aa");
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));

    form.setSearchParamValue("");
    form.setUnitName("a");
    try {
      assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));
      fail("IncorrectUserInputException expected");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }

    form.setUnitName("aa");
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));

    form.setSearchParamValue("a");
    form.setUnitName("a");
    try {
      validator.validate(form);
      fail("IncorrectUserInputException expected");
    } catch (IncorrectUserInputException e) {
      // Expected exception
    }
  }
}
