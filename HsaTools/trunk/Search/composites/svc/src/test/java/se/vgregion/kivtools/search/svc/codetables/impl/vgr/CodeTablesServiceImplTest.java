/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;

import com.novell.ldap.LDAPException;

public class CodeTablesServiceImplTest {

  private static CodeTablesServiceImpl codeTablesService;
  private static String[] managementCodeAttributes = { "1;Landsting/Region", "2;Kommun", "3;Statlig", "4;Privat, vårdavtal", "5;Privat, enl lag om läkarvårdsersättning" };
  private static String[] hsaLanguageKnowledgeCode = { "* SWE;* Svenska", "----------------------------------", "AAR;Afar" };
  private static String[] paTitleCode = { "104510;Ledning, kultur, turism och fritid", "105010;Ledning, tekniskt arbete", "105510;Ledning, räddningstjänst" };
  private static String[] hsaTitleCode = { "1;Doctor" };
  private static LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;

  @Before
  public void setup() {
    codeTablesService = new CodeTablesServiceImpl();
    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    codeTablesService.setLdapConnectionPool(ldapConnectionPoolMock);
    for (CodeTableName codeTableName : CodeTableName.values()) {
      LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
      ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("description", managementCodeAttributes));
      ldapConnectionMock.addLDAPSearchResults("(cn=" + codeTableName.toString() + ")", ldapSearchResultsMock);
    }
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("description", hsaLanguageKnowledgeCode));
    ldapConnectionMock.addLDAPSearchResults("(cn=" + CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.toString() + ")", ldapSearchResultsMock);
    ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("description", paTitleCode));
    ldapConnectionMock.addLDAPSearchResults("(cn=" + CodeTableName.PA_TITLE_CODE.toString() + ")", ldapSearchResultsMock);
    ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("description", hsaTitleCode));
    ldapConnectionMock.addLDAPSearchResults("(cn=" + CodeTableName.HSA_TITLE.toString() + ")", ldapSearchResultsMock);

    codeTablesService.init();
  }

  @Test
  public void testInit() {
    try {
      ldapConnectionMock.setLdapException(new LDAPException());
      codeTablesService.init();
      fail("Should throw LDAPException");
    } catch (LDAPRuntimeExcepton e) {
      assertEquals("An error occured in communication with the LDAP server. Message: Success", e.getMessage());
    }
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testLanguageKnowledgeCode() {
    assertEquals("Afar", codeTablesService.getValueFromCode(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "AAR"));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testPaTitleCode() {
    assertEquals("Ledning, tekniskt arbete", codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, "105010"));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testHsaTitle() {
    assertEquals("Doctor", codeTablesService.getValueFromCode(CodeTableName.HSA_TITLE, "1"));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetCodeFromTextValueFullValue() {
    List<String> codes = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Landsting/Region");
    assertEquals(1, codes.size());
    assertEquals("1", codes.get(0));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetCodeFromTextValueNoMatch() {
    List<String> codes = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Test123Test");
    assertEquals(0, codes.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetCodeFromTextValueSubstring() {
    List<String> codes = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "st");
    assertEquals(2, codes.size());
    assertListContentEqual(codes, "1", "3");
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetValuesFromTextValueFullValue() {
    List<String> codes = codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Landsting/Region");
    assertEquals(1, codes.size());
    assertEquals("Landsting/Region", codes.get(0));
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetValuesFromTextValueNoMatch() {
    List<String> codes = codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Test123Test");
    assertEquals(0, codes.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetValuesFromTextValueSubstring() {
    List<String> codes = codeTablesService.getValuesFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "st");
    assertEquals(2, codes.size());
    assertListContentEqual(codes, "Landsting/Region", "Statlig");
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetAllValueItemsFromCodeTable() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.PA_TITLE_CODE.name());
    assertNotNull(allValuesItemsFromCodeTable);
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetAllValueItemsFromCodeTableException() {
    codeTablesService.getAllValuesItemsFromCodeTable("noValidCodeTable");
  }

  @Test
  public void testTopTenFiltered() {
    List<String> valuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.name());
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
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }
}
