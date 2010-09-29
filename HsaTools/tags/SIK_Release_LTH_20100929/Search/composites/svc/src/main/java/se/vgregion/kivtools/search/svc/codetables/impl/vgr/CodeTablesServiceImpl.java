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

package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;

/**
 * Class that handles code, text pairing of ldap values.
 * 
 * @author David & Jonas
 */
public class CodeTablesServiceImpl implements CodeTablesService {
  private static final DistinguishedName CODE_TABLES_BASE = DistinguishedName.immutableDistinguishedName("ou=listor,ou=System,o=VGR");

  private Map<CodeTableName, Map<String, String>> codeTables = new ConcurrentHashMap<CodeTableName, Map<String, String>>();

  private final LdapTemplate ldapTemplate;

  /**
   * Constructs a new CodeTablesServiceImpl.
   * 
   * @param ldapTemplate The Spring LDAP Template to use.
   */
  public CodeTablesServiceImpl(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
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
    Filter searchFilter = new EqualsFilter("cn", codeTableName.toString());
    CodeTableMapper codeTableMapper = new CodeTableMapper();
    try {
      ldapTemplate.search(CODE_TABLES_BASE, searchFilter.encode(), codeTableMapper);
      codeTables.put(codeTableName, codeTableMapper.getCodeTableContent());
    } catch (NamingException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }
  }

  @Override
  public String getValueFromCode(CodeTableNameInterface codeTableName, String code) {

    if (codeTableName instanceof CodeTableName) {
      codeTableName = (CodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of CodeTableName");
    }

    Map<String, String> chosenCodeTable = codeTables.get(codeTableName);
    String value = "";
    if (chosenCodeTable != null) {
      value = chosenCodeTable.get(code);
    }
    return value;
  }

  @Override
  public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {

    if (codeTableName instanceof CodeTableName) {
      codeTableName = (CodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of CodeTableName");
    }

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
  public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {

    if (codeTableName instanceof CodeTableName) {
      codeTableName = (CodeTableName) codeTableName;

    } else {
      throw new RuntimeException("Object codeTableName is not a type of CodeTableName");
    }

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

  /**
   * Mapper for code table data.
   */
  private static class CodeTableMapper implements ContextMapper {
    private final Map<String, String> codeTableContent = new HashMap<String, String>();

    @Override
    public Object mapFromContext(Object ctx) {
      DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

      List<String> codePair = context.getStrings("description");

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

      return null;
    }

    public Map<String, String> getCodeTableContent() {
      return codeTableContent;
    }
  }
}
