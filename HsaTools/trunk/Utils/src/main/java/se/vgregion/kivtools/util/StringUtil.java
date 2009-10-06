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
}
