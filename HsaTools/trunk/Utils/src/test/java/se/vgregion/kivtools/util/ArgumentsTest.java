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
