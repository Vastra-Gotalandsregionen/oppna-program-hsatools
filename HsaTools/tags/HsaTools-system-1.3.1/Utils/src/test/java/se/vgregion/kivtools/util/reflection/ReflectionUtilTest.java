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

package se.vgregion.kivtools.util.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ReflectionUtilTest {

  private TestClass testClass;

  @Before
  public void setUp() {
    testClass = new TestClass();
  }

  @Test
  public void testInstantiation() {
    ReflectionUtil reflectionUtil = new ReflectionUtil();
    assertNotNull(reflectionUtil);
  }

  @Test
  public void testHasMethod() {

    assertFalse("No method should be found since we didn't provide an object", ReflectionUtil.hasMethod(null, "getString", false));
    assertFalse("No method should be found since we provided an invalid method name", ReflectionUtil.hasMethod(testClass, "getInteger", false));
    assertFalse("No method should be found since we provided the name of a private method", ReflectionUtil.hasMethod(testClass, "getStringPrivate", false));
    assertTrue("Method not found", ReflectionUtil.hasMethod(testClass, "getString", false));
  }

  @Test
  public void testCallMethod() {
    assertNull("No result should be returned since we didn't provide an object", ReflectionUtil.callMethod(null, "getString", false));
    assertNull("No result should be returned since we provided an invalid method name", ReflectionUtil.callMethod(testClass, "getInteger", false));
    assertNull("No result should be returned since we provided the name of a private method", ReflectionUtil.callMethod(testClass, "getStringPrivate", false));
    assertEquals("Unexpected result", "test", ReflectionUtil.callMethod(testClass, "getString", false));
  }

  @Test
  public void testGetProperty() {
    testClass.setTestProperty("Test");
    String result = ReflectionUtil.getProperty(testClass, "testProperty", false);
    assertEquals("Test", result);
    result = ReflectionUtil.getProperty(testClass, "unknown", false);
    assertNull(result);

    testClass.setValid(true);
    Boolean booleanResult = ReflectionUtil.getProperty(testClass, "valid", false);
    assertTrue(booleanResult);

    testClass.setChildren(false);
    booleanResult = ReflectionUtil.getProperty(testClass, "children", false);
    assertFalse(booleanResult);
  }

  @Test
  public void testSetProperty() {
    ReflectionUtil.setProperty(testClass, "testProperty", String.class, "Test2", false);
    assertEquals("Test2", testClass.getTestProperty());
    ReflectionUtil.setProperty(testClass, "valid", boolean.class, true, false);
    assertTrue(testClass.isValid());
  }

  @Test
  public void testSetField() {
    ReflectionUtil.setField(testClass, "testProperty", "Test2");
    assertEquals("Test2", testClass.getTestProperty());
    ReflectionUtil.setField(testClass, "valid", true);
    assertTrue(testClass.isValid());
  }

  @Test
  public void testGetPropertyIgnoreCasesensitive() {
    String result = ReflectionUtil.getProperty(testClass, "camelcaseproperty", true);
    assertEquals("noncamelcasepropertyname", result);

  }

  class TestClass {
    private String testProperty;
    private boolean valid;
    private boolean children;

    public String getCamelCaseProperty() {
      return "noncamelcasepropertyname";
    }

    // public String getNoncamelcasepropery() {
    // return "noncamelcasepropery";
    // }

    public String getString() {
      return "test";
    }

    @SuppressWarnings("unused")
    private String getStringPrivate() {
      return "private";
    }

    public void setTestProperty(String testProperty) {
      this.testProperty = testProperty;
    }

    public String getTestProperty() {
      return testProperty;
    }

    public void setValid(boolean valid) {
      this.valid = valid;
    }

    public boolean isValid() {
      return valid;
    }

    public void setChildren(boolean children) {
      this.children = children;
    }

    public boolean hasChildren() {
      return children;
    }
  }
}
