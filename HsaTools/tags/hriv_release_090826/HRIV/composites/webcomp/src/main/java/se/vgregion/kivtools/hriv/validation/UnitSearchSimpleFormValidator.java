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
/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
package se.vgregion.kivtools.hriv.validation;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;

/**
 * Validates and corrects user input in a UnitSearchSimpleForm.
 */
@SuppressWarnings("serial")
public class UnitSearchSimpleFormValidator implements Serializable {

  private static final String SUCCESSFUL_OPERATION = "success";
  private Log logger = LogFactory.getLog(this.getClass());
  /**
   * Validates and corrects user input in a UnitSearchSimpleForm.
   * 
   * @param param The form to validate.
   * @return "success" in case of a succesful validation.
   * @throws IncorrectUserInputException if the validation fails.
   */
  public String validate(UnitSearchSimpleForm param) throws IncorrectUserInputException {
    logger.info(this.getClass().getName() + ".validate()");

    if (param.getMunicipality() == null) {
      param.setMunicipality("");
    }
    if (param.getUnitName() == null) {
      param.setUnitName("");
    }
    if (param.getHealthcareType() == null) {
      param.setHealthcareType("0");
    }
    return SUCCESSFUL_OPERATION;
  }
}
