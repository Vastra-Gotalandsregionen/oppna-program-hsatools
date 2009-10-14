package se.vgregion.kivtools.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilTest {
  @Test
  public void testInstantiation() {
    StringUtil stringUtil = new StringUtil();
    assertNotNull(stringUtil);
  }

  @Test
  public void testIsInteger() {
    assertFalse(StringUtil.isInteger(null));
    assertFalse(StringUtil.isInteger(""));
    assertTrue(StringUtil.isInteger("1"));
    assertFalse(StringUtil.isInteger("a"));
    assertFalse(StringUtil.isInteger("1.0"));
    assertFalse(StringUtil.isInteger(String.valueOf(Long.valueOf(Integer.MAX_VALUE) + 1)));
  }

  @Test
  public void testIsBoolean() {
    assertFalse(StringUtil.isBoolean(null));
    assertFalse(StringUtil.isBoolean(""));
    assertTrue(StringUtil.isBoolean("true"));
    assertFalse(StringUtil.isBoolean("TRUE"));
    assertTrue(StringUtil.isBoolean("false"));
    assertFalse(StringUtil.isBoolean("FALSE"));
    assertFalse(StringUtil.isBoolean("asas"));
  }

  @Test
  public void testIsDouble() {
    assertFalse(StringUtil.isDouble(null));
    assertFalse(StringUtil.isDouble(""));
    assertTrue(StringUtil.isDouble("0"));
    assertTrue(StringUtil.isDouble("0.0"));
    assertTrue(StringUtil.isDouble("1.0"));
    assertFalse(StringUtil.isDouble("a"));
  }

  @Test
  public void testIsEmptyString() {
    assertTrue(StringUtil.isEmpty((String) null));
    assertTrue(StringUtil.isEmpty(""));
    assertTrue(StringUtil.isEmpty(" "));
    assertFalse(StringUtil.isEmpty("aaa"));
  }

  @Test
  public void testContainsNoNumbers() {
    assertFalse(StringUtil.containsNoNumbers(null));
    assertFalse(StringUtil.containsNoNumbers(""));
    assertTrue(StringUtil.containsNoNumbers("a"));
    assertFalse(StringUtil.containsNoNumbers("a1"));
    assertFalse(StringUtil.containsNoNumbers("1"));
    assertFalse(StringUtil.containsNoNumbers("a 1"));
    assertFalse(StringUtil.containsNoNumbers("a 1"));
    assertTrue(StringUtil.containsNoNumbers("a  b"));
  }

  @Test
  public void testContainsOnlyNumbers() {
    assertFalse(StringUtil.containsOnlyNumbers(null, false));
    assertFalse(StringUtil.containsOnlyNumbers("", false));
    assertFalse(StringUtil.containsOnlyNumbers("a", false));
    assertFalse(StringUtil.containsOnlyNumbers("a1", false));
    assertTrue(StringUtil.containsOnlyNumbers("1", false));
    assertFalse(StringUtil.containsOnlyNumbers("a 1", false));
    assertFalse(StringUtil.containsOnlyNumbers("a 1", true));
    assertFalse(StringUtil.containsOnlyNumbers("a  b", true));
    assertFalse(StringUtil.containsOnlyNumbers("1 2 3    4", false));
    assertTrue(StringUtil.containsOnlyNumbers("1 2 3    4", true));
  }

  @Test
  public void testEmptyStringIfNull() {
    assertEquals("", StringUtil.emptyStringIfNull(null));
    assertEquals("test", StringUtil.emptyStringIfNull("test"));
  }

	@Test
	public void testGetBytes() throws UnsupportedEncodingException {
		assertTrue(Arrays.equals("test".getBytes("UTF-8"), StringUtil.getBytes("test", "UTF-8")));
	}
	@Test(expected=RuntimeException.class)
	public void testGetBytesException() {
		StringUtil.getBytes("test", "INVALID_ENCODING");
	}

	@Test
	public void testGetString() throws UnsupportedEncodingException {
		assertEquals("test", StringUtil.getString("test".getBytes("UTF-8"), "UTF-8"));
	}

	@Test(expected=RuntimeException.class)
	public void testGetStringException() {
		StringUtil.getString(new byte[1], "INVALID_ENCODING");
	}
}
