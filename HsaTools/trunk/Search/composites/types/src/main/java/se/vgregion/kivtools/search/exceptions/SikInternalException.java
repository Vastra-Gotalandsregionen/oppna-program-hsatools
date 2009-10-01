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
package se.vgregion.kivtools.search.exceptions;

import java.io.Serializable;

/**
 * Exception mostly used from the LDAP-integration layer.
 * 
 * @author Hans Gyllensten - KnowIT
 */
@SuppressWarnings("serial")
public class SikInternalException extends KivException implements Serializable {

  /**
   * Constructs a new SikInternalException with the provided values.
   * 
   * @param obj usually "this" then this is used to construct the qualified name of the class where the problem occurred.
   * @param methodName The name of the method that threw the exception.
   * @param message The specific message describing the problem.
   */
  public SikInternalException(Object obj, String methodName, String message) {
    super(SikInternalException.class.getName() + ", message=" + message + ", Method=" + obj.getClass().getName() + ".." + methodName);
  }
}
