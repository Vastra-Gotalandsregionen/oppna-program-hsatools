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
 * 
 */
package se.vgregion.kivtools.search.exceptions;

import java.io.Serializable;

/**
 * Exception to throw when a connection to the LDAP-server cannot be established.
 * 
 * @author Hans Gyllensten, KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("serial")
public class NoConnectionToServerException extends KivException implements Serializable {

  /**
   * Constructs a NoConnectionToServerException using the provided message.
   * 
   * @param message The specific message describing the problem.
   */
  public NoConnectionToServerException(String message) {
    super(message);
  }

  /**
   * Constructs a NoConnectionToServerException using a default message.
   */
  public NoConnectionToServerException() {
    super("Ingen anslutning till servern. Var god f\u00F6rs\u00F6k senare.");
  }
}
