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

package se.vgregion.kivtools.search.util.netwise;

/**
 * Helper class for Netwise services.
 * 
 * @author Joakim Olsson
 * 
 */
public class NetwiseServicesUtil {

  /**
   * Helper-method for cleaning up phone numbers to the format expected by the webservice.
   * 
   * @param phoneNumber The phone number to clean up.
   * @return The phone number with any leading +46 replaced with a 0 and in the format xxx-xxxxxx (eg. 070-123456).
   */
  public static String cleanPhoneNumber(String phoneNumber) {
    String result = phoneNumber.replaceFirst("^\\+46", "0");
    result = result.replaceAll("[- ]", "");
    result = result.replaceFirst("^(.{3})(.*)$", "$1-$2");
    return result;
  }
}
