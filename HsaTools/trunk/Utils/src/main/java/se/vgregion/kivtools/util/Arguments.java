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
package se.vgregion.kivtools.util;

import java.util.List;

/**
 * Methods for verifying method arguments.
 */
public class Arguments {
  /**
   * Verifies that the provided parameter value is not null.
   * 
   * @param parameterName The name of the parameter that is verified.
   * @param parameterValue The value of the parameter that is verified.
   * @throws IllegalArgumentException if the provided parameter value is null.
   */
  public static final void notNull(final String parameterName, final Object parameterValue) {
    if (parameterValue == null) {
      throw new IllegalArgumentException("Parameter " + parameterName + " is null.");
    }
  }

  /**
   * Verifies that the provided string value is not null or empty (empty includes strings only containing whitespace).
   * 
   * @param parameterName The name of the parameter that is verified.
   * @param parameterValue The value of the parameter that is verified.
   * @throws IllegalArgumentException if the provided parameter value is null or empty.
   */
  public static final void notEmpty(final String parameterName, final String parameterValue) {
    notNull(parameterName, parameterValue);
    if (parameterValue.trim().length() == 0) {
      throw new IllegalArgumentException("Parameter " + parameterName + " is empty.");
    }
  }

  /**
   * Verifies that the provided list is not null or empty.
   * 
   * @param parameterName The name of the parameter that is verified.
   * @param parameterValue The value of the parameter that is verified.
   * @throws IllegalArgumentException if the provided parameter value is null or empty.
   */
  public static final void notEmpty(final String parameterName, final List<?> parameterValue) {
    notNull(parameterName, parameterValue);
    if (parameterValue.size() == 0) {
      throw new IllegalArgumentException("Parameter " + parameterName + " is empty.");
    }
  }
}
