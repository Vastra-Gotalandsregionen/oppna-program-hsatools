/**
 * Copyright 2009 Västa Götalandsregionen
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

import java.util.List;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;

/**
 * Utility class for various datatype checks and conversions.
 */
public final class Evaluator {
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
   * Checks if the provided object implementing IsEmptyMarker is empty.
   * 
   * @param obj The object implementing the IsEmptyMarker interface.
   * @return True if the object is empty, otherwise false.
   */
  public static boolean isEmpty(IsEmptyMarker obj) {
    return obj.isEmpty();
  }

  /**
   * Checks if the provided string is a negative number.
   * 
   * @param string The string to check.
   * @return True if the provided string is a number and less than 0, otherwise false.
   */
  public static boolean isNegative(String string) {
    boolean negative = false;

    if (isDouble(string)) {
      double d = Double.parseDouble(string);
      if (d < 0) {
        negative = true;
      }
    }

    return negative;
  }

  /**
   * Checks if the provided string is a double.
   * 
   * @param string The string to check.
   * @param emptyAndNullIsOk Decides if null and empty strings should return true or false.
   * @return True if the provided string is a valid double or if the provided string is null or empty and the emptyOrNullOk is set to true, otherwise false.
   */
  public static boolean checkDouble(String string, boolean emptyAndNullIsOk) {
    boolean result = false;

    result |= isEmpty(string) && emptyAndNullIsOk;
    result |= isDouble(string);

    return result;
  }

  /**
   * Converts the provided string to a boolean.
   * 
   * @param string The string to convert.
   * @return True if the provided string is "true", otherwise false.
   */
  public static boolean stringToBoolean(String string) {
    boolean result = false;

    if (isBoolean(string)) {
      result = Boolean.parseBoolean(string);
    }

    return result;
  }

  /**
   * Checks if a list of objects is empty.
   * 
   * @param list The list of objects to check.
   * @return True if the list is null or empty.
   */
  @SuppressWarnings("unchecked")
  public static boolean isEmptyList(List list) {
    return list == null || list.isEmpty();
  }

  /**
   * Checks if a list of objects implementing the IsEmptyMarker interface is empty or contains only empty objects.
   * 
   * @param list The list of objects implementing the IsEmptyMarker interface to check.
   * @return True if the list is null, empty or contains only empty objects, otherwise false.
   */
  public static boolean isEmptyMarkerList(List<? extends IsEmptyMarker> list) {
    boolean empty = true;
    if (!isEmptyList(list)) {
      for (IsEmptyMarker mark : list) {
        if (!mark.isEmpty()) {
          empty = false;
        }
      }
    }
    return empty;
  }

  /**
   * Checks if a list of addresses is empty or only contains empty addresses.
   * 
   * @param list The list of addresses to check.
   * @return True if the list is null, empty or only contains empty addresses, otherwise false.
   */
  public static boolean isEmptyAdress(List<Address> list) {
    return isEmptyMarkerList(list);
  }

  /**
   * Checks if a list of phone numbers is empty or only contains empty phone numbers.
   * 
   * @param list The list of phone numbers to check.
   * @return True if the list is null, empty or only contains empty phone numbers, otherwise false.
   */
  public static boolean isEmptyPhoneNumber(List<PhoneNumber> list) {
    return isEmptyMarkerList(list);
  }

  /**
   * Checks if a list of WeekdayTime objects is empty.
   * 
   * @param list The list of WeekdayTime objects to check.
   * @return True if the list is null or empty, otherwise false.
   */
  public static boolean isEmptyWeekDayTime(List<WeekdayTime> list) {
    return isEmptyList(list);
  }

  /**
   * Checks if a list of HealthcareType objects is empty or only contains empty HealthcareType objects.
   * 
   * @param list The list of HealthcareType objects to check.
   * @return True if the list is null, empty or only contains empty HealthcareType objects, otherwise false.
   */
  public static boolean isEmptyBusinessClassification(List<HealthcareType> list) {
    return isEmptyMarkerList(list);
  }

  /**
   * Checks if a list of strings is empty.
   * 
   * @param list The list of strings to check.
   * @return True if the list is null or empty.
   */
  public static boolean isEmpty(List<String> list) {
    return isEmptyList(list);
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
}
