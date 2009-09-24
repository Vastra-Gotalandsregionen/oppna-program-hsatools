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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class PhoneNumber implements Serializable, Comparable<PhoneNumber>, IsEmptyMarker {
  private static final long serialVersionUID = 1L;
  private static final String AREA_CODE_SWEDEN = "+46";
  private static final Set<String> LOCAL_AREA_CODES = new HashSet<String>();

  private String phoneNumber;

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

  public PhoneNumber(String phoneNumber) {
    this.setPhoneNumber(phoneNumber);
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public static PhoneNumber createPhoneNumber(String phoneNumber) {
    return new PhoneNumber(phoneNumber);
  }

  public static List<PhoneNumber> createPhoneNumberList(List<String> numbers) {
    List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
    for (String number : numbers) {
      phoneNumbers.add(new PhoneNumber(number));
    }
    return phoneNumbers;
  }

  public void setPhoneNumber(String phoneNumber) {
    if (!PhoneNumber.isValid(phoneNumber)) {
      throw new IllegalArgumentException();
    }
    if (phoneNumber == null || phoneNumber.trim().length() == 0) {
      this.phoneNumber = "";
    }
    this.phoneNumber = phoneNumber;
  }

  public static boolean isValid(String phoneNumber) {
    return true;
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

    String areaCode;
    // The phone number must have at least 7 digits to pass formatting
    if (strPhoneNumber.length() < 7) {
      return new PhoneNumber(this.phoneNumber);
    }

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

    return new PhoneNumber(areaCode + " - " + tempNumber.trim());
  }

  public boolean isEmpty() {
    return StringUtil.isEmpty(phoneNumber);
  }

  @Override
  public String toString() {
    return this.getPhoneNumber();
  }

  public int compareTo(PhoneNumber anotherPhoneNumber) {
    return this.getFormattedPhoneNumber().compareTo(anotherPhoneNumber.getFormattedPhoneNumber());
  }
}
