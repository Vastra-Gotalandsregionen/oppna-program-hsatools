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
