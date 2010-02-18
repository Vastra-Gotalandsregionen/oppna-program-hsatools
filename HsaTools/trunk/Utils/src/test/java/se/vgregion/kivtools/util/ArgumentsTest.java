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

package se.vgregion.kivtools.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ArgumentsTest {
  @Test
  public void testInstantiation() {
    Arguments arguments = new Arguments();
    assertNotNull(arguments);
  }

  @Test
  public void testNotNullWithNullValue() {
    try {
      Arguments.notNull("paramName", null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is null.", e.getMessage());
    }
  }

  @Test
  public void testNotNullValidValue() {
    Arguments.notNull("paramName", this);
  }

  @Test
  public void testNotEmptyWithNullString() {
    try {
      Arguments.notEmpty("paramName", (String) null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is null.", e.getMessage());
    }
  }

  @Test
  public void testNotEmptyWithEmptyString() {
    try {
      Arguments.notEmpty("paramName", "");
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is empty.", e.getMessage());
    }
  }

  @Test
  public void testNotEmptyWithOnlyWhitespace() {
    try {
      Arguments.notEmpty("paramName", "  \t\n");
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is empty.", e.getMessage());
    }
  }

  @Test
  public void testNotEmptyValidString() {
    Arguments.notEmpty("paramName", "  aa\t\n");
  }

  @Test
  public void testNotEmptyNullList() {
    try {
      Arguments.notEmpty("paramName", (List<?>) null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is null.", e.getMessage());
    }
  }

  @Test
  public void testNotEmptyWithEmptyList() {
    try {
      Arguments.notEmpty("paramName", new ArrayList<String>());
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Unexpected exception text", "Parameter paramName is empty.", e.getMessage());
    }
  }

  @Test
  public void testNotEmptyWithValidList() {
    List<String> list = new ArrayList<String>();
    list.add("test");
    Arguments.notEmpty("paramName", list);
  }
}
