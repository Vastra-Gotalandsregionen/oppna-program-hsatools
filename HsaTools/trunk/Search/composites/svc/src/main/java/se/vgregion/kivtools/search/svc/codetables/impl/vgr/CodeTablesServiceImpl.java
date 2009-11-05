package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;

/**
 * Class that handles code, text pairing of ldap values.
 * 
 * @author David & Jonas
 */
public class CodeTablesServiceImpl implements CodeTablesService {
  private static final String CODE_TABLES_BASE = "ou=listor,ou=System,o=VGR";

  private Map<CodeTableName, Map<String, String>> codeTables = new ConcurrentHashMap<CodeTableName, Map<String, String>>();
  private String attribute = "description";
  private LdapConnectionPool ldapConnectionPool;

  /**
   * Set LdapConnectionPool to use for codeTable service.
   * 
   * @param ldapConnectionPool LdapConnectionPool to use in CodeTable service.
   */
  public void setLdapConnectionPool(LdapConnectionPool ldapConnectionPool) {
    this.ldapConnectionPool = ldapConnectionPool;
  }

  /**
   * Initializes the code table service.
   */
  public void init() {
    codeTables.clear();
    for (CodeTableName codeTableName : CodeTableName.values()) {
      try {
        populateCodeTablesMap(codeTableName);
      } catch (KivException e) {
        throw new LDAPRuntimeExcepton(e.getMessage());
      }
    }
  }

  private void populateCodeTablesMap(CodeTableName codeTableName) throws KivException {
    try {
      LDAPConnection connection = ldapConnectionPool.getConnection();
      try {
        LDAPSearchResults search = connection.search(CODE_TABLES_BASE, LDAPConnection.SCOPE_SUB, "(cn=" + codeTableName + ")", new String[] { attribute }, false);
        Map<String, String> codeTableContent = new HashMap<String, String>();
        LDAPEntry entry = search.next();
        if (entry != null) {
          String[] codePair = entry.getAttribute(attribute).getStringValueArray();
          for (String code : codePair) {
            if (!code.startsWith("* ")) {
              String[] codeArr = code.split(";");
              if (codeArr.length >= 2) {
                codeTableContent.put(codeArr[0], codeArr[1]);
              } else {
                codeTableContent.put(codeArr[0], codeArr[0]);
              }
            }
          }
          codeTables.put(codeTableName, codeTableContent);
        }
      } finally {
        ldapConnectionPool.freeConnection(connection);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }
  }

  @Override
  public String getValueFromCode(CodeTableName codeTableName, String code) {
    Map<String, String> chosenCodeTable = codeTables.get(codeTableName);
    String value = "";
    if (chosenCodeTable != null) {
      value = chosenCodeTable.get(code);
    }
    return value;
  }

  @Override
  public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
    String stringToMatch = textValue.toLowerCase();
    List<String> codes = new ArrayList<String>();
    Map<String, String> chosenCodeTable = codeTables.get(codeTableName);
    for (Entry<String, String> entry : chosenCodeTable.entrySet()) {
      if (entry.getValue().toLowerCase().contains(stringToMatch)) {
        codes.add(entry.getKey());
      }
    }
    return codes;
  }

  @Override
  public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
    String stringToMatch = textValue.toLowerCase();
    List<String> values = new ArrayList<String>();
    Map<String, String> chosenCodeTable = codeTables.get(codeTableName);
    for (String value : chosenCodeTable.values()) {
      if (value.toLowerCase().contains(stringToMatch)) {
        values.add(value);
      }
    }
    return values;
  }

  @Override
  public List<String> getAllValuesItemsFromCodeTable(String codeTable) {
    return new ArrayList<String>(codeTables.get(CodeTableName.valueOf(codeTable)).values());
  }
}
