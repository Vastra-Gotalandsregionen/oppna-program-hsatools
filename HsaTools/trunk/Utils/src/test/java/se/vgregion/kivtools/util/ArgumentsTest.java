package se.vgregion.kivtools.util;

import static org.junit.Assert.*;

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
  public void testNotEmptyWithNullValue() {
    try {
      Arguments.notEmpty("paramName", null);
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
  public void testNotEmptyValidValue() {
    Arguments.notEmpty("paramName", "  aa\t\n");
  }
}
