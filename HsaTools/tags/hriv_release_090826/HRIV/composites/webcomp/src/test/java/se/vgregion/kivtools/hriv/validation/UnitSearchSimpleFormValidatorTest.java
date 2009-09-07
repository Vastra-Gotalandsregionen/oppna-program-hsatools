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
package se.vgregion.kivtools.hriv.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.hriv.validation.UnitSearchSimpleFormValidator;
import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
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
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));
    form.setMunicipality(null);
    form.setUnitName(null);
    form.setHealthcareType(null);
    assertEquals(SUCCESSFUL_OPERATION, validator.validate(form));
    assertEquals("", form.getMunicipality());
    assertEquals("", form.getUnitName());
    assertEquals("0", form.getHealthcareType());
  }
}
