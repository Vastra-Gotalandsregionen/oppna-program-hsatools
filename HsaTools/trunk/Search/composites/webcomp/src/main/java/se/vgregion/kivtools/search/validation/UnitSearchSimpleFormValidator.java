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
/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
package se.vgregion.kivtools.search.validation;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;

/**
 * Validates user input in a UnitSearchSimpleForm.
 */
@SuppressWarnings("serial")
public class UnitSearchSimpleFormValidator implements Serializable {

  private static final String SUCCESSFUL_OPERATION = "success";
  private Log logger = LogFactory.getLog(this.getClass());

  /**
   * Validates user input in a UnitSearchSimpleForm.
   * 
   * @param param The form to validate.
   * @return "success" in case of a succesful validation.
   * @throws IncorrectUserInputException if the validation fails.
   */
  public String validate(UnitSearchSimpleForm param) throws IncorrectUserInputException {
    logger.debug(this.getClass().getName() + ".validate()");

    int paramlength = param.getLocation().trim().length();
    int unitNameLength = param.getUnitName().trim().length();

    if (!(paramlength >= 2 || unitNameLength >= 2)) {
      throw new IncorrectUserInputException("Ange minst tv\u00E5 tecken som s\u00F6kkriteria i ett av f\u00E4lten.");
    }

    return SUCCESSFUL_OPERATION;
  }
}
