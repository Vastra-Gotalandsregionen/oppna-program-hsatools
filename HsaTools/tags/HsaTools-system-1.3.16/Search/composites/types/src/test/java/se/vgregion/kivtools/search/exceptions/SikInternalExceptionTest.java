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

import static org.junit.Assert.*;

import org.junit.Test;

public class SikInternalExceptionTest {

  private static final String METHOD_NAME = "method name";
  private static final String ERROR_MESSAGE = "This is an error message";
  private static final String EXPECTED_ERROR_MESSAGE = "se.vgregion.kivtools.search.exceptions.SikInternalException, message=This is an error message, Method=java.lang.Object..method name";

  @Test
  public void testErrorMessage(){
   
    Object obj = new Object();
    assertEquals(EXPECTED_ERROR_MESSAGE, new SikInternalException(obj,METHOD_NAME,ERROR_MESSAGE).getMessage());
  }
}
