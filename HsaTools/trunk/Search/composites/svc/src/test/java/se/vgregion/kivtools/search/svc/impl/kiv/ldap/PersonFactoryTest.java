package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class PersonFactoryTest {
  private static final String DN = "cn=abcd12,ou=Personal,o=vgr";
  private static final String TEST = "Test";
  private LDAPEntryMock ldapEntry;

  @Before
  public void setUp() throws Exception {
    ldapEntry = new LDAPEntryMock();
    ldapEntry.setDn(DN);
    ldapEntry.addAttribute("cn", TEST);
    ldapEntry.addAttribute("vgr-id", TEST);
    ldapEntry.addAttribute("hsaPersonIdentityNumber", TEST);
    ldapEntry.addAttribute("givenName", TEST);
    ldapEntry.addAttribute("sn", TEST);
    ldapEntry.addAttribute("hsaMiddleName", TEST);
    ldapEntry.addAttribute("initials", TEST);
    ldapEntry.addAttribute("hsaNickName", TEST);
    ldapEntry.addAttribute("fullName", TEST);
    ldapEntry.addAttribute("vgrStrukturPersonDN", TEST);
    ldapEntry.addAttribute("vgrOrgRel", TEST);
    ldapEntry.addAttribute("vgrAnstform", TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("mail", TEST);
    ldapEntry.addAttribute("hsaSpecialityName", TEST);
    ldapEntry.addAttribute("hsaSpecialityCode", TEST);
    ldapEntry.addAttribute("vgrAO3kod", TEST);
    ldapEntry.addAttribute("vgrAnsvarsnummer", TEST);
    ldapEntry.addAttribute("hsaLanguageKnowledgeCode", TEST);
    ldapEntry.addAttribute("hsaLanguageKnowledgeText", TEST);
    ldapEntry.addAttribute("hsaTitle", TEST);
    ldapEntry.addAttribute("hsaPersonPrescriptionCode", TEST);
    ldapEntry.addAttribute("hsaStartDate", TEST);
    ldapEntry.addAttribute("hsaEndDate", TEST);
    ldapEntry.addAttribute("hsaLanguageKnowledgeCode", TEST);
  }

  @Test
  public void testInstantiation() {
    PersonFactory personFactory = new PersonFactory();
    assertNotNull(personFactory);
  }

  @Test
  public void testNullLDAPEntry() {
    Person person = PersonFactory.reconstitute(null, null);
    assertNotNull(person);
    assertNull(person.getDn());
  }

  @Test
  public void testReconstitute() {
    Person person = PersonFactory.reconstitute(ldapEntry, new CodeTablesServiceMock());
    assertNotNull(person);
    assertEquals(DN, person.getDn());
    assertEquals(TEST, person.getCn());
    assertEquals(TEST, person.getVgrId());
    assertEquals(TEST, person.getHsaPersonIdentityNumber());
    assertEquals(TEST, person.getGivenName());
    assertEquals(TEST, person.getSn());
    assertEquals(TEST, person.getHsaMiddleName());
    assertEquals(TEST, person.getInitials());
    assertEquals(TEST, person.getHsaNickName());
    assertEquals(TEST, person.getFullName());
    assertEquals("[" + TEST + "]", person.getVgrStrukturPersonDN().toString());
    assertEquals("[" + TEST + "]", person.getVgrOrgRel().toString());
    assertEquals("[" + TEST + "]", person.getVgrAnstform().toString());
    assertEquals(TEST, person.getHsaIdentity());
    assertEquals(TEST, person.getMail());
    assertEquals("[Translated " + TEST + "]", person.getHsaSpecialityName().toString());
    assertEquals("[" + TEST + "]", person.getHsaSpecialityCode().toString());
    assertEquals("[" + TEST + "]", person.getVgrAO3kod().toString());
    assertEquals("[" + TEST + "]", person.getVgrAnsvarsnummer().toString());
    assertEquals("[" + TEST + "]", person.getHsaLanguageKnowledgeCode().toString());
    assertEquals("[Translated " + TEST + "]", person.getHsaLanguageKnowledgeText().toString());
    assertEquals(TEST, person.getHsaTitle());
    assertEquals(TEST, person.getHsaPersonPrescriptionCode());
    assertNotNull(person.getEmploymentPeriod());
  }

  class CodeTablesServiceMock implements CodeTablesService {
    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return "Translated " + string;
    }

    @Override
    public void init() {
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }
  }
}
