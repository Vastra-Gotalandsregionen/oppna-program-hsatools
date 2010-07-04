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

package se.vgregion.kivtools.search.domain.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.util.StringUtil;

/**
 * PhoneNumber representation.
 * 
 * @author Anders Asplund - KnowIT
 */
public final class PhoneNumber implements Serializable, Comparable<PhoneNumber>, IsEmptyMarker {
  private static final long serialVersionUID = 1L;
  private static final String AREA_CODE_SWEDEN = "+46";
  private static final Set<String> LOCAL_AREA_CODES = new HashSet<String>();

  private final String phoneNumber;

  // Define all two- and three digit area codes. Any area code not defined in
  // the set is supposed to be a four digit area code.
  static {
    LOCAL_AREA_CODES.add("011");
    LOCAL_AREA_CODES.add("013");
    LOCAL_AREA_CODES.add("016");
    LOCAL_AREA_CODES.add("018");
    LOCAL_AREA_CODES.add("019");
    LOCAL_AREA_CODES.add("021");
    LOCAL_AREA_CODES.add("023");
    LOCAL_AREA_CODES.add("026");
    LOCAL_AREA_CODES.add("031");
    LOCAL_AREA_CODES.add("033");
    LOCAL_AREA_CODES.add("035");
    LOCAL_AREA_CODES.add("036");
    LOCAL_AREA_CODES.add("040");
    LOCAL_AREA_CODES.add("042");
    LOCAL_AREA_CODES.add("044");
    LOCAL_AREA_CODES.add("046");
    LOCAL_AREA_CODES.add("054");
    LOCAL_AREA_CODES.add("060");
    LOCAL_AREA_CODES.add("063");
    LOCAL_AREA_CODES.add("08");
    LOCAL_AREA_CODES.add("090");
  }

  /**
   * Constructs a new PhoneNumber instance using the provided phone number.
   * 
   * @param phoneNumber The phone number to use for the new instance.
   */
  private PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Factory method for creating new PhoneNumber instances.
   * 
   * @param phoneNumber The phone number to use for the new instance.
   * @return A new PhoneNumber instance.
   */
  public static PhoneNumber createPhoneNumber(String phoneNumber) {
    String newPhoneNumber = phoneNumber;

    if (StringUtil.isEmpty(newPhoneNumber)) {
      newPhoneNumber = "";
    }
    return new PhoneNumber(newPhoneNumber);
  }

  /**
   * Factory method for creating a list of PhoneNumber instances using the provided list of phone number strings.
   * 
   * @param numbers The list of phone number strings to create PhoneNumber instances from.
   * @return A list of PhoneNumber instances representing the provided list of phone number strings.
   */
  public static List<PhoneNumber> createPhoneNumberList(List<String> numbers) {
    List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
    for (String number : numbers) {
      phoneNumbers.add(new PhoneNumber(number));
    }
    return phoneNumbers;
  }

  /**
   * Konverterar LDAP-lagrade telefonnummer s� de blir mer l�ttl�sta. Fr�n katalogformat till presentationsformat.
   * 
   * @return String med konverterat v�rde
   */
  public PhoneNumber getFormattedPhoneNumber() {

    // Make a local copy so that we don't change the original phone number
    String strPhoneNumber = this.phoneNumber;

    // remove +46
    if (strPhoneNumber.indexOf(PhoneNumber.AREA_CODE_SWEDEN) != -1) {
      strPhoneNumber = strPhoneNumber.replace(PhoneNumber.AREA_CODE_SWEDEN, "").trim();
    }
    // add 0 to the area code if not already there
    if (!strPhoneNumber.startsWith("0") && !strPhoneNumber.contains("(0)")) {
      strPhoneNumber = "0" + strPhoneNumber;
    }

    // Remove all characters that's not a number.
    String regex = "\\D*";
    strPhoneNumber = strPhoneNumber.replaceAll(regex, "");

    PhoneNumber result;

    // The phone number must have at least 7 digits to pass formatting
    if (strPhoneNumber.length() < 7) {
      result = new PhoneNumber(this.phoneNumber);
    } else {
      String areaCode;
      // Split phone number correctly after area code
      if (LOCAL_AREA_CODES.contains(strPhoneNumber.substring(0, 2))) {
        // Two-digit area code
        areaCode = strPhoneNumber.substring(0, 2);
        strPhoneNumber = strPhoneNumber.substring(2);
      } else if (LOCAL_AREA_CODES.contains(strPhoneNumber.substring(0, 3))) {
        // Three-digit area code
        areaCode = strPhoneNumber.substring(0, 3);
        strPhoneNumber = strPhoneNumber.substring(3);
      } else {
        // Four-digit area code
        areaCode = strPhoneNumber.substring(0, 4);
        strPhoneNumber = strPhoneNumber.substring(4);
      }

      if (strPhoneNumber.length() > 3) {
        String tempNumber = "";
        for (int i = strPhoneNumber.length(); i > 0; i -= 2) {
          String head = strPhoneNumber.substring(0, i - 2);
          String tail = strPhoneNumber.substring(i - 2, i);

          tempNumber = " " + tail + tempNumber;
          if (head.length() == 3) {
            tempNumber = head + tempNumber;
            break;
          }
        }

        result = new PhoneNumber(areaCode + " - " + tempNumber.trim());
      } else {
        result = new PhoneNumber(this.phoneNumber);
      }
    }

    return result;
  }

  public boolean isEmpty() {
    return StringUtil.isEmpty(phoneNumber);
  }

  @Override
  public String toString() {
    return this.getPhoneNumber();
  }

  @Override
  public int compareTo(PhoneNumber anotherPhoneNumber) {
    return this.getFormattedPhoneNumber().compareTo(anotherPhoneNumber.getFormattedPhoneNumber());
  }
}
