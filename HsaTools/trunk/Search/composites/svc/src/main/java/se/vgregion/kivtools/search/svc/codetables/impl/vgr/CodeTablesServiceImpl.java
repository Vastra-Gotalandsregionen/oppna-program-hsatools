package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import java.util.HashMap;
import java.util.Map;

import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPSearchResults;

/**
 * Class that handles code, text pairing of ldap values.
 * 
 * @author David & Jonas
 * 
 */
public class CodeTablesServiceImpl implements CodeTablesService {

  private Map<String, Map<String, String>> codeTables = new HashMap<String, Map<String, String>>();
  private String codeTablesBase = "ou=listor,ou=System,o=VGR";
  private String attribute = "description";
  private LdapConnectionPool ldapConnectionPool;

  /**
   * Set LdapConnectionPool to use for codeTable service.
   * 
   * @param ldapConnectionPool - LdapConnectionPool to use in CodeTable service.
   */
  public void setLdapConnectionPool(LdapConnectionPool ldapConnectionPool) {
    this.ldapConnectionPool = ldapConnectionPool;
  }

  /**
   * 
   * @return String of codetable base.
   */
  public String getCodeTablesBase() {
    return codeTablesBase;
  }

  /**
   * Set the String codetabel base.
   * 
   * @param codeTablesBase - String.
   */
  public void setCodeTablesBase(String codeTablesBase) {
    this.codeTablesBase = codeTablesBase;
  }

  /**
   * 
   * @param codeTables - Map&lt;String, Map&lt;String, String&gt;&gt;
   */
  public void setCodeTables(Map<String, Map<String, String>> codeTables) {
    this.codeTables = codeTables;
  }

  /**
   * 
   * @inheritDoc
   */
  public void init() {
    codeTables.clear();
    for (CodeTableName codeTableName : CodeTableName.values()) {
      try {
        populateCodeTablesMap(codeTableName);
      } catch (Exception e) {
        throw new LDAPRuntimeExcepton(e.getMessage());
      }
    }
  }

  private void populateCodeTablesMap(CodeTableName codeTableName) throws Exception {
    LDAPSearchResults search = ldapConnectionPool.getConnection().search(codeTablesBase, LDAPConnection.SCOPE_SUB, "(cn=" + codeTableName + ")", new String[] { attribute }, false);
    Map<String, String> codeTableContent = new HashMap<String, String>();
    LDAPEntry entry = search.next();
    if (entry != null) {
      String[] codePair = entry.getAttribute(attribute).getStringValueArray();
      for (String code : codePair) {
        String[] codeArr = code.split(";");
        codeTableContent.put(codeArr[0], codeArr[1]);
      }
      codeTables.put(String.valueOf(codeTableName), codeTableContent);
    }
  }

  /**
   * 
   * @inheritDoc
   * @param codeTableName - CodeTableName enum.
   * @param code - String code.
   * @return String
   */
  public String getValueFromCode(CodeTableName codeTableName, String code) {
    Map<String, String> chosenCodeTable = codeTables.get(String.valueOf(codeTableName));
    String value = "";
    if (chosenCodeTable != null) {
      value = chosenCodeTable.get(code);
    }
    return value;
  }

}
