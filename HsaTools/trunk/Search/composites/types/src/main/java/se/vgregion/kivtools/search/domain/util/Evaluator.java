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

package se.vgregion.kivtools.search.domain.util;

import java.util.List;

import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;

/**
 * Utility class for various datatype checks and conversions.
 */
public final class Evaluator {
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
}
