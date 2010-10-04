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

package se.vgregion.kivtools.search.exceptions;

import java.io.Serializable;

/**
 * Exception to throw when incorrect user input is found during validation.
 * 
 * @author Anders Asplund - KnowIT
 */
@SuppressWarnings("serial")
public class IncorrectUserInputException extends KivException implements Serializable {

  /**
   * Constructs a IncorrectUserInputException using the provided message.
   * 
   * @param message The specific message describing the problem.
   */
  public IncorrectUserInputException(String message) {
    super(message);
  }

  /**
   * Constructs a IncorrectUserInputException using a default message.
   */
  public IncorrectUserInputException() {
    super("Du m\u00E5ste fylla i minst ett av f\u00E4lten.");
  }
}
