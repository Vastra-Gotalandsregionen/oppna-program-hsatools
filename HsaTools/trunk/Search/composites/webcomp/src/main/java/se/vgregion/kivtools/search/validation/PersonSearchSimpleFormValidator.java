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
 * 
 */
package se.vgregion.kivtools.search.validation;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.exceptions.IncorrectUserInputException;
import se.vgregion.kivtools.search.presentation.forms.PersonSearchSimpleForm;

/**
 * Validates user input in a PersonSearchSimpleForm.
 * 
 * @author Anders Asplund - KnowIT
 */
@SuppressWarnings("serial")
public class PersonSearchSimpleFormValidator implements Serializable {

  private static final String SUCCESSFUL_OPERATION = "success";
  private Log logger = LogFactory.getLog(this.getClass());

  /**
   * Validates user input in a PersonSearchSimpleForm.
   * 
   * @param param The form to validate.
   * @return "success" in case of a succesful validation.
   * @throws IncorrectUserInputException if the validation fails.
   */
  public String validate(PersonSearchSimpleForm param) throws IncorrectUserInputException {
    logger.debug(this.getClass().getName() + ".validate()");

    // if data is entered in a field it must contain at least 2 chars
    int givenNameLength = param.getGivenName().trim().length();
    int sirNameLength = param.getSirName().trim().length();
    int vgrIdLength = param.getVgrId().trim().length();

    if (givenNameLength == 0 && sirNameLength == 0 && vgrIdLength == 0) {
      throw new IncorrectUserInputException("Var v\u00E4nlig och fyll i s\u00F6kkriteria i n\u00E5got av f\u00E4lten.");
    }

    if (givenNameLength == 1 || sirNameLength == 1 || vgrIdLength == 1) {
      throw new IncorrectUserInputException("De s\u00F6kf\u00E4lt som du vill anv\u00E4nda m\u00E5ste innh\u00E5lla minst tv\u00E5 tecken.");
    }
    return SUCCESSFUL_OPERATION;
  }
}
