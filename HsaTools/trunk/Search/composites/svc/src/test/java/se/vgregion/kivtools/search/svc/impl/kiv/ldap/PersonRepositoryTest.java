package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterion;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterion.SearchCriterion;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

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
    ldapConnectionMock
        .addLDAPSearchResults(
            "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(paTitleCode=employmentTitle))",
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
    SearchPersonCriterion searchPersonCriterion = new SearchPersonCriterion();
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.EMPLOYMENT_TITEL, "employmentTitle");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 1);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(vgr-id=anama))");
  }

  @Test
  public void testSearchPersons() throws KivException {
    SearchPersonCriterion searchPersonCriterion = new SearchPersonCriterion();
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.GIVEN_NAME, "Kalle");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.SURNAME, "Svensson");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))");

    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.USER_ID, "vgr-id");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.EMPLOYMENT_TITEL, "employmentTitle");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.EMPLOYMENT_AT_UNIT, "unitName");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.SPECIALITY_AREA_CODE, "speciality");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.PROFESSION, "profGroup");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.LANGUAGE_KNOWLEDGE_CODE, "langKnowledgeCode");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.E_MAIL, "email");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.ADMINISTRATION, "administration");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock
        .assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(vgr-id=anama))");
    assertEquals(1, searchPersons.size());
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
    public void init() {
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }
  }
}
