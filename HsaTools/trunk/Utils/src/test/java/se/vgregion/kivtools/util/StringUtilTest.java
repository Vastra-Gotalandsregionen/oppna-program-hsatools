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

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  @Test(expected = RuntimeException.class)
  public void testGetBytesException() {
    StringUtil.getBytes("test", "INVALID_ENCODING");
  }

  @Test
  public void testGetString() throws UnsupportedEncodingException {
    assertEquals("test", StringUtil.getString("test".getBytes("UTF-8"), "UTF-8"));
  }

  @Test(expected = RuntimeException.class)
  public void testGetStringException() {
    StringUtil.getString(new byte[1], "INVALID_ENCODING");
  }

  @Test
  public void testBase64Encode() {
    String input = "cn=Hedvig h Blomfrö,ou=Falkenbergsnämnden,ou=Förtroendevalda,ou=Landstinget  Halland,dc=hkat,dc=lthalland,dc=com";
    String expected = "Y249SGVkdmlnIGggQmxvbWZy9ixvdT1GYWxrZW5iZXJnc27kbW5kZW4sb3U9RvZydHJvZW5kZXZh\r\nbGRhLG91PUxhbmRzdGluZ2V0ICBIYWxsYW5kLGRjPWhrYXQsZGM9bHRoYWxsYW5kLGRjPWNvbQ==\r\n";
    String result = StringUtil.base64Encode(input);
    assertEquals(expected, result);
  }

  @Test
  public void testConcatenate() {
    assertEquals("Vårdcentral Angered, Angered", StringUtil.concatenate("Vårdcentral Angered", "Angered"));
    assertEquals("Vårdcentral Angered, Göteborg, Angered", StringUtil.concatenate("Vårdcentral Angered", "Göteborg", "Angered"));
    assertEquals("Vårdcentral Angered, Göteborg", StringUtil.concatenate("Vårdcentral Angered", "Göteborg", ""));

    assertEquals("Empty string expected for null List", "", StringUtil.concatenate((List) null));

    assertEquals("Empty string expected for null String values", "", StringUtil.concatenate("", null));
    assertEquals("Empty string expected for null String values", "", StringUtil.concatenate(null, null));
    assertEquals("Empty string expected for null String values", "Göteborg", StringUtil.concatenate(null, "Göteborg"));
    assertEquals("Empty string expected for null String values", "Göteborg", StringUtil.concatenate("Göteborg", null));
    assertEquals("Empty string expected for null String values", "Göteborg", StringUtil.concatenate("Göteborg", null, null, null));

    List<String> list = new ArrayList<String>();
    list.add("Vårdcentral Angered");
    list.add("Angered");
    assertEquals("Unexpected result", "Vårdcentral Angered, Angered", StringUtil.concatenate(list));

    list.add("   Sverige   ");
    assertEquals("Unexpected result", "Vårdcentral Angered, Angered, Sverige", StringUtil.concatenate(list));
  }

  @Test
  public void testUrlEncode() {
    String input = "&= aåäö";
    String expected = "%26%3D+a%C3%A5%C3%A4%C3%B6";
    String result = StringUtil.urlEncode(input, "UTF-8");
    assertEquals(expected, result);
  }

  @Test(expected = RuntimeException.class)
  public void testUrlEncodeInvalidEncoding() {
    StringUtil.urlEncode("test", "INVALID_ENCODING");
  }
}
