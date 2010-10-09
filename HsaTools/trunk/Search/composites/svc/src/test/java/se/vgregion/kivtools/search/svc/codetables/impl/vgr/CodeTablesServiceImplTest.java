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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;

public class CodeTablesServiceImplTest {
  private static String[] managementCodeAttributes = { "1;Landsting/Region", "2;Kommun", "3;Statlig", "4;Privat, vårdavtal", "5;Privat, enl lag om läkarvårdsersättning" };
  private static String[] hsaLanguageKnowledgeCode = { "* SWE;* Svenska", "----------------------------------", "AAR;Afar" };
  private static String[] paTitleCode = { "104510;Ledning, kultur, turism och fritid", "105010;Ledning, tekniskt arbete", "105510;Ledning, räddningstjänst" };
  private static String[] hsaTitleCode = { "1;Doctor" };

  private final LdapTemplateMock ldapTemplate = new LdapTemplateMock();

  private final CodeTablesServiceImpl codeTablesService = new CodeTablesServiceImpl(this.ldapTemplate);

  @Before
  public void setup() {

    for (CodeTableName codeTableName : CodeTableName.values()) {
      DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();
      dirContextOperations.addAttributeValue("description", managementCodeAttributes);
      this.ldapTemplate.addSearchResult("(cn=" + codeTableName.toString() + ")", dirContextOperations);
    }
    DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();
    dirContextOperations.addAttributeValue("description", hsaLanguageKnowledgeCode);
    this.ldapTemplate.addSearchResult("(cn=" + CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.toString() + ")", dirContextOperations);
    dirContextOperations = new DirContextOperationsMock();
    dirContextOperations.addAttributeValue("description", paTitleCode);
    this.ldapTemplate.addSearchResult("(cn=" + CodeTableName.PA_TITLE_CODE.toString() + ")", dirContextOperations);
    dirContextOperations = new DirContextOperationsMock();
    dirContextOperations.addAttributeValue("description", hsaTitleCode);
    this.ldapTemplate.addSearchResult("(cn=" + CodeTableName.HSA_TITLE.toString() + ")", dirContextOperations);

    this.codeTablesService.init();
  }

  @Test(expected = LDAPRuntimeExcepton.class)
  public void initThrowsExceptionOnNamingException() {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.codeTablesService.init();
  }

  @Test
  public void testLanguageKnowledgeCode() {
    assertEquals("Afar", this.codeTablesService.getValueFromCode(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "AAR"));
  }

  @Test
  public void testPaTitleCode() {
    assertEquals("Ledning, tekniskt arbete", this.codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, "105010"));
  }

  @Test
  public void testHsaTitle() {
    assertEquals("Doctor", this.codeTablesService.getValueFromCode(CodeTableName.HSA_TITLE, "1"));
  }

  @Test
  public void testGetCodeFromTextValueFullValue() {
    List<String> codes = this.codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Landsting/Region");
    assertEquals(1, codes.size());
    assertEquals("1", codes.get(0));
  }

  @Test
  public void testGetCodeFromTextValueNoMatch() {
    List<String> codes = this.codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Test123Test");
    assertEquals(0, codes.size());
  }

  @Test
  public void testGetCodeFromTextValueSubstring() {
    List<String> codes = this.codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "st");
    assertEquals(2, codes.size());
    this.assertListContentEqual(codes, "1", "3");
  }

  @Test
  public void testGetValuesFromTextValueFullValue() {
    List<String> codes = this.codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Landsting/Region");
    assertEquals(1, codes.size());
    assertEquals("Landsting/Region", codes.get(0));
  }

  @Test
  public void testGetValuesFromTextValueNoMatch() {
    List<String> codes = this.codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Test123Test");
    assertEquals(0, codes.size());
  }

  @Test
  public void testGetValuesFromTextValueSubstring() {
    List<String> codes = this.codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "st");
    assertEquals(2, codes.size());
    this.assertListContentEqual(codes, "Landsting/Region", "Statlig");
  }

  @Test
  public void testGetAllValueItemsFromCodeTable() {
    List<String> allValuesItemsFromCodeTable = this.codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.PA_TITLE_CODE.name());
    assertNotNull(allValuesItemsFromCodeTable);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetAllValueItemsFromCodeTableException() {
    this.codeTablesService.getAllValuesItemsFromCodeTable("noValidCodeTable");
  }

  @Test
  public void getAllValueItemsFromCodeTableForUninitializedCodeTableReturnEmptyList() {
    List<String> allValuesItemsFromCodeTable = this.codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_COUNTY_CODE.name());
    assertNotNull(allValuesItemsFromCodeTable);
    assertTrue("values is not empty", allValuesItemsFromCodeTable.isEmpty());
  }

  @Test
  public void testTopTenFiltered() {
    List<String> valuesItemsFromCodeTable = this.codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.name());
    assertFalse(valuesItemsFromCodeTable.contains("* Svenska"));
  }

  private void assertListContentEqual(List<String> actualStrings, String... expectedStrings) {
    for (String actualString : actualStrings) {
      boolean found = false;
      for (String expectedString : expectedStrings) {
        found |= expectedString.equals(actualString);
      }
      assertTrue("Unexpected string '" + actualString + "'", found);
    }
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private final Map<String, DirContextOperations> searchResults = new HashMap<String, DirContextOperations>();
    private NamingException exceptionToThrow;

    public void addSearchResult(String filter, DirContextOperations searchResult) {
      this.searchResults.put(filter, searchResult);
    }

    public void setExceptionToThrow(NamingException exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public List<?> search(Name base, String filter, ContextMapper mapper) {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
      Object result = mapper.mapFromContext(this.searchResults.get(filter));
      List<Object> resultList = new ArrayList<Object>();
      resultList.add(result);
      return resultList;
    }
  }
}
