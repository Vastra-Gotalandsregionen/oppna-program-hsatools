package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class PersonRepositoryTest {

  private PersonRepository personRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;
  private Calendar calendar;
  private static Map<String, String[]> attributeLists = new HashMap<String, String[]>();
  static {
    attributeLists.put("givenName", new String[] { "Kalle" });
  }

  @Before
  public void setUp() throws Exception {
    calendar = Calendar.getInstance();
    calendar.set(2009, 8, 19, 16, 23);
    calendar.set(Calendar.SECOND, 48);
    calendar.set(Calendar.MILLISECOND, 0);
    TimeSource timeSource = new TimeSource() {
      @Override
      public long millis() {
        return calendar.getTimeInMillis();
      }
    };
    TimeUtil.setTimeSource(timeSource);

    personRepository = new PersonRepository();
    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock
        .addLDAPSearchResults(
            "(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(vgr-id=anama))",
            ldapSearchResultsMock);

    ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock = new LDAPEntryMock();
    ldapEntryMock.setDn("cn=12345,cn=anama,ou=Personal,o=vgr");
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock);
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))",
        ldapSearchResultsMock);

    personRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    personRepository.setCodeTablesService(getCodeTableServiceMock());

    for (Entry<String, String[]> attributeListEntry : attributeLists.entrySet()) {
      LinkedList<LDAPEntryMock> ldapEntries = new LinkedList<LDAPEntryMock>();
      LDAPEntryMock entryMock = new LDAPEntryMock();
      entryMock.addAttribute("description", attributeListEntry.getValue());
      ldapEntries.add(entryMock);
    }
  }

  @After
  public void tearDown() {
    TimeUtil.reset();
  }

  @Test
  public void testEmploymentTitleSearch() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setEmploymentTitle("employmentTitle");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 1);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(vgr-id=anama))");
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersons() throws KivException {
    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName("Kalle");
    searchPersonCriterion.setSurname("Svensson");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))");

    searchPersonCriterion.setUserId("vgr-id");
    searchPersonCriterion.setEmploymentTitle("employmentTitle");
    searchPersonCriterion.setEmployedAtUnit("unitName");
    searchPersonCriterion.setSpecialityArea("speciality");
    searchPersonCriterion.setProfession("profGroup");
    searchPersonCriterion.setLanguageKnowledge("langKnowledgeCode");
    searchPersonCriterion.setEmail("email");
    searchPersonCriterion.setAdministration("administration");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock
        .assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(vgr-id=anama))");
    assertEquals(1, searchPersons.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchPersonsWithvgrId() throws KivException {
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=vgrUser)(vgr-id=*1*))", new LDAPSearchResultsMock());
    personRepository.searchPersons("", 1);
    personRepository.searchPersons("1", 1);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(vgr-id=*1*))");
    personRepository.searchPersons("\"1\"", 1);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(vgr-id=1))");
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testGetPersonsForUnits() throws Exception {
    setLdapConnectionMock();
    List<Unit> units = generateTestUnitList();
    List<Person> persons = null;
    persons = personRepository.getPersonsForUnits(units, 5);
    Assert.assertFalse(persons.isEmpty());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  private List<Unit> generateTestUnitList() {
    List<Unit> units = new ArrayList<Unit>();
    Unit unit = null;
    for (int i = 0; i < 5; i++) {
      unit = new Unit();
      unit.setHsaIdentity("unit" + i);
      units.add(unit);
    }
    return units;
  }

  private List<Person> generateTestPersonList() {
    List<Person> persons = new ArrayList<Person>();
    Person person = null;
    for (int i = 0; i < 10; i++) {
      person = new Person();
      person.setHsaIdentity("person" + i);
      persons.add(person);
    }
    return persons;
  }

  private void setLdapConnectionMock() {
    List<Person> persons = generateTestPersonList();
    LinkedList<LDAPEntryMock> ldapEntries = generatePersonLdapEntries(persons);
    LDAPConnectionMock connectionMock = new LDAPConnectionMock();
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    for (LDAPEntryMock ldapEntryMock : ldapEntries) {
      ldapSearchResultsMock.addLDAPEntry(ldapEntryMock);
    }
    connectionMock.addLDAPSearchResults("(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4))", ldapSearchResultsMock);
    personRepository.setLdapConnectionPool(new LdapConnectionPoolMock(connectionMock));
    personRepository.setUnitFkField("vgrOrgRel");
  }

  private LinkedList<LDAPEntryMock> generatePersonLdapEntries(List<Person> persons) {
    LinkedList<LDAPEntryMock> ldapEntries = new LinkedList<LDAPEntryMock>();
    LDAPEntryMock entryMock = null;
    for (Person person : persons) {
      entryMock = new LDAPEntryMock();
      entryMock.addAttribute("hsaIdentity", person.getHsaIdentity());
      ldapEntries.add(entryMock);
    }
    return ldapEntries;
  }

  private CodeTablesService getCodeTableServiceMock() {
    CodeTableServiceMock codeTableServiceMock = new CodeTableServiceMock();
    codeTableServiceMock.addListToMap(CodeTableName.HSA_SPECIALITY_CODE, Arrays.asList("specialityCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, Arrays.asList("languageCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_TITLE, Arrays.asList("profGroup"));
    codeTableServiceMock.addListToMap(CodeTableName.VGR_AO3_CODE, Arrays.asList("administration1,administration2".split(",")));
    codeTableServiceMock.addListToMap(CodeTableName.PA_TITLE_CODE, Arrays.asList("employmentTitle"));
    return codeTableServiceMock;
  }

  class CodeTableServiceMock implements CodeTablesService {

    private Map<CodeTableName, List<String>> codeTables = new HashMap<CodeTableName, List<String>>();

    public void addListToMap(CodeTableName key, List<String> list) {
      codeTables.put(key, list);
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return codeTables.get(codeTableName);
    }

    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }
}
