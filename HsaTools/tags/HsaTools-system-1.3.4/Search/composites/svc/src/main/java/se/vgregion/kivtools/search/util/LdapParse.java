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

package se.vgregion.kivtools.search.util;

/**
 * Ldap specific parsing. Received from the Kiv Admin project.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
public class LdapParse {
  /**
   * Filters the query, prevents ldap injection.
   * 
   * @param filter The ldap search filter to perform escaping on.
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

  /**
   * Convert boolean value to string value used in ldap.
   * 
   * @param b boolean value to convert to string.
   * @return J for true and N for false value.
   */
  public static String convertBooleanToString(boolean b) {
    String value = "N";
    if (b) {
      value = "J";
    }
    return value;
  }
}
