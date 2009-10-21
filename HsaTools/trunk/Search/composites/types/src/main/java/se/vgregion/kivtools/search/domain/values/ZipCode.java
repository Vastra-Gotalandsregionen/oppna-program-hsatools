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
package se.vgregion.kivtools.search.domain.values;

import java.io.Serializable;

import se.vgregion.kivtools.util.StringUtil;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class ZipCode implements Serializable, Comparable<ZipCode> {

  private static final long serialVersionUID = 1L;
  private String zipCode = "";

  /**
   * Constructs a new ZipCode using the provided zipCode value.
   * 
   * @param zipCode The zipCode value to use for this instance.
   */
  public ZipCode(String zipCode) {
    setZipCode(zipCode);
  }

  /**
   * Checks if the provided zipCode is valid.
   * 
   * @param zipCode The zipCode to check.
   * @return True if the provided zipCode contains only numbers and is exactly 5 characters long, otherwise false.
   */
  public static boolean isValid(String zipCode) {
    return StringUtil.containsOnlyNumbers(zipCode, true) && zipCode.replaceAll(" ", "").length() == 5;
  }

  public String getZipCode() {
    return zipCode;
  }

  /**
   * Returns a ZipCode in the format "NNN NN".
   * 
   * @return A new ZipCode instance with the zipCode formatted as "NNN NN" if the current zipCode is valid, otherwise a new empty ZipCode instance.
   */
  public ZipCode getFormattedZipCode() {
    if (!ZipCode.isValid(this.zipCode)) {
      return new ZipCode("");
    }
    // Remove all characters that's not a number.
    String regex = "\\D*";
    // Use a new string so we don't modify the original.
    String strZipCode = this.zipCode.replaceAll(regex, "");

    return new ZipCode(strZipCode.substring(0, 3) + " " + strZipCode.substring(3));
  }

  private void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  @Override
  public int compareTo(ZipCode anotherZipCode) {
    if (anotherZipCode == null) {
      return 1;
    }
    return this.getFormattedZipCode().toString().compareTo(anotherZipCode.getFormattedZipCode().toString());
  }

  @Override
  public String toString() {
    return zipCode;
  }

}
