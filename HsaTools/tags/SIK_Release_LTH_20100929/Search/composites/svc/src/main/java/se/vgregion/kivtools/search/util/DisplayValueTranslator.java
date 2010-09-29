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

import java.util.Map;

/**
 * Translates from LDAP domain values to displayable strings for UI.
 * 
 * @author David & Joakim
 */
public class DisplayValueTranslator {
  private Map<String, String> translationMap;

  /**
   * Setter for the translationMap attribute.
   * 
   * @param translationMap The translationMap to use.
   */
  public void setTranslationMap(Map<String, String> translationMap) {
    this.translationMap = translationMap;
  }

  /**
   * Translates managementCode from an LDAP domain value to a displayable string for UI.
   * 
   * @param managementCode The managementCode to translate.
   * @return The translated value. If no value is found then empty string is returned.
   */
  public String translateManagementCode(String managementCode) {
    String result = translationMap.get(managementCode);
    if (result == null) {
      result = "";
    }
    return result;
  }
}
