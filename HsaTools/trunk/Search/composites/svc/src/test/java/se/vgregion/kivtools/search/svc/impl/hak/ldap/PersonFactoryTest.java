package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class PersonFactoryTest {
  private static final String TEST = "Test";
  private static final String TEST_DN = "cn=abc,ou=def";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";

  private LDAPEntryMock ldapEntry;

  @Before
  public void setUp() throws Exception {
    ldapEntry = new LDAPEntryMock();
    ldapEntry.setDn(TEST_DN);
    ldapEntry.addAttribute("cn", TEST);
    ldapEntry.addAttribute("regionName", TEST);
    ldapEntry.addAttribute("personalIdentityNumber", TEST);
    ldapEntry.addAttribute("givenName", TEST);
    ldapEntry.addAttribute("sn", TEST);
    ldapEntry.addAttribute("middleName", TEST);
    ldapEntry.addAttribute("nickname", TEST);
    ldapEntry.addAttribute("fullName", TEST);
    ldapEntry.addAttribute("distinguishedName", TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("mail", TEST);
    ldapEntry.addAttribute("specialityName", TEST);
    ldapEntry.addAttribute("hsaSpecialityCode", TEST);
    ldapEntry.addAttribute("hsaLanguageKnowledgeCode", TEST);
    ldapEntry.addAttribute("hsaLanguageKnowledgeText", TEST);
    ldapEntry.addAttribute("hsaTitle", "Leg. psykolog$IT-Arkitekt");
    ldapEntry.addAttribute("hsaPersonPrescriptionCode", TEST);
    ldapEntry.addAttribute("hsaStartDate", TEST);
    ldapEntry.addAttribute("hsaEndDate", TEST);
  }

  @Test
  public void testInstantiation() {
    PersonFactory personFactory = new PersonFactory();
    assertNotNull(personFactory);
  }

  @Test
  public void testReconstituteNullLDAPEntry() {
    Person person = PersonFactory.reconstitute(null);
    assertNotNull(person);
    assertNull(person.getDn());
  }

  @Test
  public void testReconstitute() {
    Person person = PersonFactory.reconstitute(ldapEntry);
    assertEquals(TEST, person.getCn());
    assertEquals(TEST, person.getVgrId());
    assertEquals(TEST, person.getHsaPersonIdentityNumber());
    assertEquals(TEST, person.getGivenName());
    assertEquals(TEST, person.getSn());
    assertEquals(TEST, person.getHsaMiddleName());
    assertEquals(TEST, person.getHsaNickName());
    assertEquals(TEST, person.getFullName());
    assertEquals(TEST_DN, person.getDn());
    assertEquals(EXPECTED_LIST_RESULT, person.getVgrOrgRel().toString());
    assertEquals(TEST, person.getHsaIdentity());
    assertEquals(TEST, person.getMail());
    assertEquals(EXPECTED_LIST_RESULT, person.getHsaSpecialityName().toString());
    assertEquals(EXPECTED_LIST_RESULT, person.getHsaSpecialityCode().toString());
    assertEquals(EXPECTED_LIST_RESULT, person.getHsaLanguageKnowledgeCode().toString());
    assertEquals(EXPECTED_LIST_RESULT, person.getHsaLanguageKnowledgeText().toString());
    assertEquals("Leg. psykolog, IT-Arkitekt", person.getHsaTitle());
    assertEquals(TEST, person.getHsaPersonPrescriptionCode());
    assertNotNull(person.getEmploymentPeriod());
  }
}
