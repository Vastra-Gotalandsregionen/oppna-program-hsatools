package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.LDAPRuntimeExcepton;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;

import com.novell.ldap.LDAPException;

public class CodeTablesServiceImplTest {

  private static CodeTablesServiceImpl codeTablesService;
  private static String[] managementCodeAttributes = { "1;Landsting/Region", "2;Kommun", "3;Statlig", "4;Privat, vårdavtal", "5;Privat, enl lag om läkarvårdsersättning" };
  private static String[] specialityCodeAttributes = { "1000;Europaläkare, allmänpraktiserande läkare", "10100;Kirurgi", "10101;Urologi", "10102;Barn- och ungdomskirurgi" };
  private static String[] vgrAO3KodAttributes = { "010;Moderförvaltning;10", "020;Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks);10", "030;Revisorskollegiet",
      "040;Patientnämnd U-a, Borås, Gbg o Mariestad" };
  private static String[] hsaLanguageKnowledgeCode = { "* SWE;* Svenska", "----------------------------------", "AAR;Afar" };
  private static String[] paTitleCode = { "104510;Ledning, kultur, turism och fritid", "105010;Ledning, tekniskt arbete", "105510;Ledning, räddningstjänst" };
  private static String[] hsaTitleCode = { "1;Doctor"};
  private static String[] vgrCareTypeAttributes = { "01;Öppenvård", "02;Slutenvård", "03;Hemsjukvård" };
  private static LDAPConnectionMock ldapConnectionMock;

  @Before
  public void setup() {
    codeTablesService = new CodeTablesServiceImpl();
    ldapConnectionMock = new LDAPConnectionMock();
    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
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
  public void testInit(){
    try {
    ldapConnectionMock.setLdapException(new LDAPException());
    codeTablesService.init();
    fail("Should throw LDAPException");
    }catch (LDAPRuntimeExcepton e) {
      assertEquals("An error occured in communication with the LDAP server. Message: Success", e.getMessage());
    }
  }
  
  @Test
  public void testLanguageKnowledgeCode() {
    assertEquals("* Svenska", codeTablesService.getValueFromCode(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "* SWE"));
  }

  @Test
  public void testPaTitleCode() {
    assertEquals("Ledning, tekniskt arbete", codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, "105010"));
  }
  
  @Test
  public void testHsaTitle(){
    assertEquals("Doctor", codeTablesService.getValueFromCode(CodeTableName.HSA_TITLE, "1"));
  }
  
  @Test
  public void testGetCodeFromTextValue(){
    assertEquals("1", codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, "Landsting/Region").get(0));
  }
}
