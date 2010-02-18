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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility methods for String handling.
 * 
 * @author Joakim Olsson
 */
public class StringUtil {

  /**
   * Checks if the provided string is an integer.
   * 
   * @param string The string to check.
   * @return True if the provided string is an integer, otherwise false.
   */
  public static boolean isInteger(String string) {
    boolean integer = true;
    try {
      Integer.parseInt(string);
    } catch (NumberFormatException e) {
      integer = false;
    }
    return integer;
  }

  /**
   * Checks if the provided string is a boolean.
   * 
   * @param string The string to check.
   * @return True if the provided string is either of the strings "true" or "false", otherwise false.
   */
  public static boolean isBoolean(String string) {
    return Boolean.TRUE.toString().equals(string) || Boolean.FALSE.toString().equals(string);
  }

  /**
   * Checks if the provided string is a double.
   * 
   * @param string The string to check.
   * @return True if the provided string is a double.
   */
  public static boolean isDouble(String string) {
    boolean result = true;
    if (isEmpty(string)) {
      result = false;
    } else {
      try {
        Double.parseDouble(string);
      } catch (NumberFormatException e) {
        result = false;
      }
    }
    return result;
  }

  /**
   * Checks if the provided string is empty.
   * 
   * @param string The string to check.
   * @return True if the provided string is empty, null or only contains whitespace.
   */
  public static boolean isEmpty(String string) {
    boolean empty = false;

    if (string == null) {
      empty = true;
    } else {
      empty = string.trim().equalsIgnoreCase("");
    }
    return empty;
  }

  /**
   * Checks if the provided string contains any numeric characters.
   * 
   * @param string The string to check.
   * @return True if the provided string is free from numeric characters, otherwise false.
   */
  public static boolean containsNoNumbers(String string) {
    boolean result = true;

    if (isEmpty(string)) {
      result = false;
    } else {
      for (int i = 0; i < string.length(); i++) {
        // If we find a non-digit character we return false.
        result &= !Character.isDigit(string.charAt(i));
      }
    }
    return result;
  }

  /**
   * Checks if the provided string contains only numeric characters.
   * 
   * @param string The string to check.
   * @param ignoreWhiteSpace True if any whitespace in the string should be ignored.
   * @return True if the provided string consists of only numeric characters, otherwise false.
   */
  public static boolean containsOnlyNumbers(String string, boolean ignoreWhiteSpace) {
    boolean result = true;

    // It can't contain only numbers if it's null or empty...
    if (isEmpty(string)) {
      result = false;
    } else {
      for (int i = 0; i < string.length(); i++) {
        // If we find a non-digit character we return false.
        if (!Character.isDigit(string.charAt(i))) {
          result &= Character.isWhitespace(string.charAt(i)) && ignoreWhiteSpace;
        }
      }
    }
    return result;
  }

  /**
   * Makes sure that the returned string is not null.
   * 
   * @param string The string to return if not null.
   * @return Returns the provided string if not null, otherwise an empty string.
   */
  public static String emptyStringIfNull(String string) {
    String result = string;

    if (result == null) {
      result = "";
    }

    return result;
  }

  /**
   * Gets the contents of a string as a byte-array using the provided encoding.
   * 
   * @param string The string to get as a byte-array.
   * @param encoding The encoding to use.
   * @return The contents of the provided string as a byte-array.
   * @throws RuntimeException If an unsupported encoding is provided.
   */
  public static byte[] getBytes(String string, String encoding) {
    try {
      return string.getBytes(encoding);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a new String using the provided byte-array using the provided encoding.
   * 
   * @param bytes The byte-array to create a String from.
   * @param encoding The encoding to use.
   * @return A new String based on the provided byte-array.
   * @throws RuntimeException If an unsupported encoding is provided.
   */
  public static String getString(byte[] bytes, String encoding) {
    try {
      return new String(bytes, encoding);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Encodes the provided string using Base64-encoding.
   * 
   * @param string The string to encode.
   * @return The provided string Base64-encoded.
   */
  public static String base64Encode(String string) {
    return getString(Base64.encodeBase64(getBytes(string, "ISO-8859-1"), true), "ISO-8859-1");
  }

  /**
   * Concatenates an arbitrary amount of strings into a nicely formatted concatenated string. Skips empty stringPart elements.
   * 
   * @param stringParts The strings to concatenate.
   * @return The concatenated strings.
   */
  public static String concatenate(String... stringParts) {
    StringBuilder concatenatedString = new StringBuilder();
    if (stringParts != null) {

      List<String> stringPartsList = new ArrayList<String>(Arrays.asList(stringParts));
      concatenatedString.append(StringUtil.concatenate(stringPartsList));
    }
    return concatenatedString.toString();
  }

  /**
   * Concatenates an arbitrary amount of strings into a nicely formatted concatenated string. Skips empty stringPart elements.
   * 
   * @param stringParts The list of strings to concatenate.
   * @return The concatenated strings.
   */
  public static String concatenate(List<String> stringParts) {
    StringBuilder concatenatedString = new StringBuilder();
    if (stringParts != null) {

      List<String> stringPartsList = new ArrayList<String>(stringParts);
      for (Iterator<String> iterator = stringPartsList.iterator(); iterator.hasNext();) {
        String stringPart = iterator.next();
        if ("".equals(stringPart)) {
          iterator.remove();
        }
      }

      for (int i = 0; i < stringPartsList.size(); i++) {
        if (stringPartsList.get(i) != null) {
          concatenatedString.append(stringPartsList.get(i).trim());
          if (i < stringPartsList.size() - 1 && stringPartsList.get(i + 1) != null) {
            concatenatedString.append(", ");
          }
        }
      }
    }
    return concatenatedString.toString();
  }

  /**
   * URL encodes the provided input using the provided encoding.
   * 
   * @param input The input to URL encode.
   * @param characterEncoding The character encoding to use.
   * @return The provided input URL encoded.
   */
  public static String urlEncode(String input, String characterEncoding) {
    try {
      return URLEncoder.encode(input, characterEncoding);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
