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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ReflectionUtilTest {

  @Test
  public void testInstantiation() {
    ReflectionUtil reflectionUtil = new ReflectionUtil();
    assertNotNull(reflectionUtil);
  }

  @Test
  public void testHasMethod() {
    TestClass testClass = new TestClass();

    assertFalse("No method should be found since we didn't provide an object", ReflectionUtil.hasMethod(null, "getString"));
    assertFalse("No method should be found since we provided an invalid method name", ReflectionUtil.hasMethod(testClass, "getInteger"));
    assertFalse("No method should be found since we provided the name of a private method", ReflectionUtil.hasMethod(testClass, "getStringPrivate"));
    assertTrue("Method not found", ReflectionUtil.hasMethod(testClass, "getString"));
  }

  @Test
  public void testCallMethod() {
    TestClass testClass = new TestClass();

    assertNull("No result should be returned since we didn't provide an object", ReflectionUtil.callMethod(null, "getString"));
    assertNull("No result should be returned since we provided an invalid method name", ReflectionUtil.callMethod(testClass, "getInteger"));
    assertNull("No result should be returned since we provided the name of a private method", ReflectionUtil.callMethod(testClass, "getStringPrivate"));
    assertEquals("Unexpected result", "test", ReflectionUtil.callMethod(testClass, "getString"));
  }

  class TestClass {
    public String getString() {
      return "test";
    }

    @SuppressWarnings("unused")
    private String getStringPrivate() {
      return "private";
    }
  }
}
