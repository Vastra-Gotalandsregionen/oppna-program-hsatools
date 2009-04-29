package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import java.util.HashMap;
import java.util.Map;

import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.LdapConnectionPool;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPSearchResults;

public class CodeTablesServiceImpl implements CodeTablesService {

	Map<String, Map<String, String>> codeTables = new HashMap<String, Map<String, String>>();
	String codeTablesBase = "ou=listor,ou=System,o=VGR";
	String attribute = "description";
	LdapConnectionPool ldapConnectionPool;

	public LdapConnectionPool getLdapConnectionPool() {
		return ldapConnectionPool;
	}

	public void setLdapConnectionPool(LdapConnectionPool ldapConnectionPool) {
		this.ldapConnectionPool = ldapConnectionPool;
	}

	public String getCodeTablesBase() {
		return codeTablesBase;
	}

	public void setCodeTablesBase(String codeTablesBase) {
		this.codeTablesBase = codeTablesBase;
	}

	public Map<String, Map<String, String>> getCodeTables() {
		return codeTables;
	}

	public void setCodeTables(Map<String, Map<String, String>> codeTables) {
		this.codeTables = codeTables;
	}

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
		LDAPSearchResults search = ldapConnectionPool.getConnection().search(codeTablesBase, LDAPConnection.SCOPE_ONE, "(cn=" + codeTableName + ")", new String[] { attribute }, false);
		LDAPEntry entry;
		Map<String, String> codeTableContent = new HashMap<String, String>();
		if ((entry = search.next()) != null) {
			String[] codePair = entry.getAttribute(attribute).getStringValueArray();
			for (String code : codePair) {
				String[] codeArr = code.split(";");
				codeTableContent.put(codeArr[0], codeArr[1]);
			}
			codeTables.put(String.valueOf(codeTableName), codeTableContent);
		}
	}

	public String getValueFromCode(CodeTableName codeTableName, String code) {
		Map<String, String> chosenCodeTable = codeTables.get(String.valueOf(codeTableName));
		String value = "";
		if (chosenCodeTable != null) {
			value = chosenCodeTable.get(code);
		}
		return value;
	}

}
