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

public class Evaluator {
  private static final String className = Evaluator.class.getName();

  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean isBoolean(String s) {
    return Boolean.TRUE.toString().equals(s) || Boolean.FALSE.toString().equals(s);
  }

  public static boolean isDouble(String s) {
    try {
      Double.parseDouble(s);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static boolean isEmpty(String s) {
    if (s == null) {
      return true;
    }
    s = s.trim();
    if (s.equalsIgnoreCase("")) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isEmpty(IsEmptyMarker obj) {
    return obj.isEmpty();
  }

  public static boolean isNegative(String s) {
    try {
      double d = Double.parseDouble(s);
      if (d < 0) {
        return true;
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean checkDouble(String s, boolean emptyAndNullIsOk) {
    if (s == null || s.equalsIgnoreCase("")) {
      if (emptyAndNullIsOk) {
        return true;
      } else {
        return false;
      }
    }
    return isDouble(s);
  }

  public static boolean stringToBoolean(String s) {
    if (s != null && s.equalsIgnoreCase("true")) {
      return true;
    }
    return false;
  }

  public static boolean isEmptyAdress(List<Address> list) {
    if (list == null || list.isEmpty()) {
      return true;
    }
    for (IsEmptyMarker mark : list) {
      if (!mark.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public static boolean isEmptyPhoneNumber(List<PhoneNumber> list) {
    if (list == null || list.isEmpty()) {
      return true;
    }
    for (IsEmptyMarker mark : list) {
      if (!mark.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public static boolean isEmptyWeekDayTime(List<WeekdayTime> list) {
    if (list == null || list.isEmpty()) {
      return true;
    }
    return false;
  }

  public static boolean isEmptyBusinessClassification(List<HealthcareType> list) {
    if (list == null || list.isEmpty()) {
      return true;
    }
    for (IsEmptyMarker mark : list) {
      if (!mark.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public static boolean isEmpty(List<String> l) {
    if (l == null) {
      return true;
    }
    return l.isEmpty();
  }

  public static boolean containsNoNumbers(String str, boolean ignoreWhiteSpace) {
    if (str == null || str.length() == 0) {
      return false;
    }
    for (int i = 0; i < str.length(); i++) {

      // If we find a non-digit character we return false.
      if (Character.isDigit(str.charAt(i))) {
        if (Character.isWhitespace(str.charAt(i))) {
          // is white space
          if (ignoreWhiteSpace) {
            continue;
          } else {
            return false;
          }
        }
        return false;
      }
    }
    return true;
  }

  public static boolean containsOnlyNumbers(String str, boolean ignoreWhiteSpace) {
    // It can't contain only numbers if it's null or empty...
    if (str == null || str.length() == 0) {
      return false;
    }
    for (int i = 0; i < str.length(); i++) {

      // If we find a non-digit character we return false.
      if (!Character.isDigit(str.charAt(i))) {
        if (Character.isWhitespace(str.charAt(i))) {
          // is white space
          if (ignoreWhiteSpace) {
            continue;
          } else {
            return false;
          }
        }
        return false;
      }
    }
    return true;
  }
}
