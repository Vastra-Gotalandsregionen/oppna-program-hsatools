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

/**
 * Klass för fel i formattering.
 * 
 */
public class InvalidFormatException extends Exception {

  private static final long serialVersionUID = -1563818733230370743L;

  /**
   * Constructs an InvalidFormatException using the provided message.
   * 
   * @param message The specific message describing the problem.
   */
  public InvalidFormatException(String message) {
    super(message);
  }
}
