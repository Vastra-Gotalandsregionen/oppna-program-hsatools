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
/**
 * 
 */
package se.vgregion.kivtools.search.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 * 
 *         Ldap specific parsing. Received from the Kiv Admin project
 */
public class LdapParse {
  private static Map<String, String> dayNames = new HashMap<String, String>();

  static {
    dayNames.put("1", "Måndag");
    dayNames.put("2", "Tisdag");
    dayNames.put("3", "Onsdag");
    dayNames.put("4", "Torsdag");
    dayNames.put("5", "Fredag");
    dayNames.put("6", "Lördag");
    dayNames.put("0", "Söndag");
  }

  /**
   * Översätter ett string nummer till dag i klartext.
   * 
   * @param number - String nummer mellan 0-6 representerar varsin dag.
   * @return - String returnerar dag ex. "6" blir "Lördag".
   */
  public static String getDayName(String number) {
    String dayName = dayNames.get(number);

    if (dayName == null) {
      dayName = "";
    }

    return dayName;
  }

  /**
   * Filters the query, prevents ldap injection.
   * 
   * @param filter
   * @see http://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
   * @return escaped ldap search filter
   */
  public static final String escapeLDAPSearchFilter(String filter) {
    if (filter == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filter.length(); i++) {
      char curChar = filter.charAt(i);
      switch (curChar) {
        case '\\':
          sb.append("\\5c");
          break;
        case '*':
          sb.append("\\2a");
          break;
        case '(':
          sb.append("\\28");
          break;
        case ')':
          sb.append("\\29");
          break;
        case '\u0000':
          sb.append("\\00");
          break;
        default:
          sb.append(curChar);
      }
    }
    return sb.toString();
  }
}
