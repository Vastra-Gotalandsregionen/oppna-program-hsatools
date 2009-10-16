package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

public class PersonRepositoryTest {

  private PersonRepository personRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;
  private static Map<String, String[]> attributeLists = new HashMap<String, String[]>();
  static {
    attributeLists.put("givenName", new String[] { "Kalle" });
  }

  @Before
  public void setUp() throws Exception {
    personRepository = new PersonRepository();
    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(new LDAPEntryMock("givenName", "Kalle"));
    ldapConnectionMock.addLDAPSearchResults("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(title=*employmentTitle*)(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2)))", ldapSearchResultsMock);
    personRepository.setLdapConnectionPool(ldapConnectionPoolMock);
   
    for (Entry<String, String[]> attributeListEntry : attributeLists.entrySet()) {
      LinkedList<LDAPEntryMock> ldapEntries = new LinkedList<LDAPEntryMock>();
      LDAPEntryMock entryMock = new LDAPEntryMock();
      entryMock.addAttribute("description", attributeListEntry.getValue());
      ldapEntries.add(entryMock);
    }
  }

  @Test
  public void testSearchPersons() throws KivException {
    
    SearchPersonCriterion searchPersonCriterion = new SearchPersonCriterion();
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.GIVEN_NAME,"Kalle");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.SURNAME,"Svensson");
    SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))");
   
    CodeTableServiceMock codeTableServiceMock = new CodeTableServiceMock();
    codeTableServiceMock.addListToMap(CodeTableName.HSA_SPECIALITY_CODE, Arrays.asList("specialityCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, Arrays.asList("languageCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_TITLE, Arrays.asList("profGroup"));
    codeTableServiceMock.addListToMap(CodeTableName.VGR_AO3_CODE, Arrays.asList("administration1,administration2".split(",")));
    
    personRepository.setCodeTablesService(codeTableServiceMock);
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.USER_ID,"vgr-id");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.EMPLOYMENT_TITEL,"employmentTitle");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.EMPLOYMENT_AT_UNIT,"unitName");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.SPECIALITY_AREA_CODE,"speciality");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.PROFESSION,"profGroup");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.LANGUAGE_KNOWLEDGE_CODE, "langKnowledgeCode");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.E_MAIL, "email");
    searchPersonCriterion.addSearchCriterionValue(SearchCriterion.ADMINISTRATION, "administration");
    searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
    ldapConnectionMock.assertFilter("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(title=*employmentTitle*)(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2)))");
    assertEquals(1, searchPersons.size());
  }
  
  class CodeTableServiceMock implements CodeTablesService {

    private Map<CodeTableName, List<String>> codeTables = new HashMap<CodeTableName, List<String>>();
    
    public void addListToMap(CodeTableName key,List<String> list){
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
    
  }

}
