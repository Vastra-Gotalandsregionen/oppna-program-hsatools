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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class PersonRepositoryTest {
  private PersonRepository personRepository;
  private Calendar calendar;
  private MockLdapTemplate mockLdapTemplate;

  @Before
  public void setUp() throws Exception {
    this.setupTimeSource();

    this.personRepository = new PersonRepository();
    this.mockLdapTemplate = new MockLdapTemplate();
    this.personRepository.setLdapTemplate(this.mockLdapTemplate);
    this.personRepository.setUnitFkField("vgrOrgRel");
    this.personRepository.setCodeTablesService(this.getCodeTableServiceMock());
  }

  @After
  public void tearDown() {
    TimeUtil.reset();
  }

  @Test
  public void testGetAllPersonsVgrIDs() throws KivException {
    LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
    DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();
    dirContextOperations.addAttributeValue("vgr-id", "user1");
    ldapTemplateMock.addDirContextOperationForSearch(dirContextOperations);
    dirContextOperations = new DirContextOperationsMock();
    dirContextOperations.addAttributeValue("vgr-id", "user2");
    ldapTemplateMock.addDirContextOperationForSearch(dirContextOperations);
    this.personRepository.setLdapTemplate(ldapTemplateMock);
    List<String> allPersonsVgrId = this.personRepository.getAllPersonsVgrId();
    assertEquals(2, allPersonsVgrId.size());
  }

  @Test(expected = KivException.class)
  public void getAllPersonsVgrIdThrowsKivExceptionOnNamingException() throws KivException {
    LdapTemplateMock ldapTemplate = new LdapTemplateMock();
    this.personRepository.setLdapTemplate(ldapTemplate);

    ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.personRepository.getAllPersonsVgrId();
  }

  @Test
  public void testEmploymentTitleSearch() throws KivException {
    this.mockLdapTemplate.result.put("(&(objectclass=vgrUser)(vgr-id=anama))", Arrays.asList((Object) new Unit()));
    String expectedLdapQuestion1 = "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))";
    String expectedLdapQuestion2 = "(&(&(objectclass=vgrUser)(!(vgrSecrMark=J)))(vgr-id=anama))";
    this.mockLdapTemplate.result.put(expectedLdapQuestion1, Arrays.asList((Object) "anama"));

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setEmploymentTitle("employmentTitle");
    this.personRepository.setLdapTemplate(this.mockLdapTemplate);
    this.personRepository.searchPersons(searchPersonCriterion, 1);
    assertEquals(expectedLdapQuestion1, this.mockLdapTemplate.filter.get(0));
    assertEquals(expectedLdapQuestion2, this.mockLdapTemplate.filter.get(1));
  }

  @Test
  public void testDetailSearchOnEmploymentAttributes() throws KivException {

    String expectedLdapQuestion = "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*)(|(hsaTelephoneNumber=*031-232323*)(mobileTelephoneNumber=*031-232323*)(hsaInternalPagerNumber=*031-232323*)(pagerTelephoneNumber=*031-232323*)(hsaTextPhoneNumber=*031-232323*)(hsaPublicTelephoneNumber=*031-232323*)(facsimileTelephoneNumber=*031-232323*)(hsaSedfSwitchboardTelephoneNo=*031-232323*))(description=*Description about something*)(|(paTitleCode=employmentTitle)(paTitleCode=Kurator)))";
    this.mockLdapTemplate.result.put(expectedLdapQuestion, Arrays.asList((Object) "anama"));

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setEmploymentTitle("employmentTitle");
    searchPersonCriterion.setPhone("031-232323");
    searchPersonCriterion.setDescription("Description about something");
    searchPersonCriterion.setEmploymentPosition("Kurator");

    this.personRepository.setLdapTemplate(this.mockLdapTemplate);
    this.personRepository.searchPersons(searchPersonCriterion, 1);
    assertEquals(expectedLdapQuestion, this.mockLdapTemplate.filter.get(0));

  }

  @Test
  public void testSearchPersons() throws KivException {
    String expectedResult = "(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(!(vgrSecrMark=J)))";
    String expectedResult2 = "(&(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(!(vgrSecrMark=J)))(vgr-id=anama))";

    List<Object> units = new ArrayList<Object>();
    units.add(new Unit());

    this.mockLdapTemplate.result.put("(&(objectclass=vgrUser)(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))", units);
    this.mockLdapTemplate.result.put(expectedResult2, units);
    this.mockLdapTemplate.result.put("(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))",
        Arrays.asList((Object) "anama"));

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setGivenName("Kalle");
    searchPersonCriterion.setSurname("Svensson");

    SikSearchResultList<Person> searchPersons = this.personRepository.searchPersons(searchPersonCriterion, 10);
    assertEquals(expectedResult, this.mockLdapTemplate.filter.get(0));

    searchPersonCriterion.setUserId("vgr-id");
    searchPersonCriterion.setEmploymentTitle("employmentTitle");
    searchPersonCriterion.setEmployedAtUnit("unitName");
    searchPersonCriterion.setSpecialityArea("speciality");
    searchPersonCriterion.setProfession("profGroup");
    searchPersonCriterion.setLanguageKnowledge("langKnowledgeCode");
    searchPersonCriterion.setEmail("email");
    searchPersonCriterion.setAdministration("administration");
    searchPersons = this.personRepository.searchPersons(searchPersonCriterion, 10);
    assertEquals(expectedResult2, this.mockLdapTemplate.filter.get(2));
    assertEquals(1, searchPersons.size());
  }

  @Test
  public void searchPersonsWithEmploymentSearchReturningMoreThanOnehundredVgridsPerformsTwoSearches() throws KivException {
    List<Person> persons1 = this.createPersons("b", 1, 100);
    List<Person> persons2 = this.createPersons("a", 101, 110);

    String expectedEmploymentQuery = "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))";
    String expectedFirstPersonQuery = "(&(&(objectclass=vgrUser)(!(vgrSecrMark=J)))(|" + this.createVgridQuery(persons1) + "))";
    String expectedSecondPersonQuery = "(&(&(objectclass=vgrUser)(!(vgrSecrMark=J)))(|" + this.createVgridQuery(persons2) + "))";

    List<Person> allPersons = new ArrayList<Person>();
    allPersons.addAll(persons1);
    allPersons.addAll(persons2);
    List<String> employees = this.createVgrIds(allPersons);

    this.mockLdapTemplate.result.put(expectedEmploymentQuery, employees);
    this.mockLdapTemplate.result.put(expectedFirstPersonQuery, persons1);
    this.mockLdapTemplate.result.put(expectedSecondPersonQuery, persons2);

    SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
    searchPersonCriterion.setEmploymentTitle("employmentTitle");

    SikSearchResultList<Person> searchPersons = this.personRepository.searchPersons(searchPersonCriterion, 11);
    assertEquals(expectedEmploymentQuery, this.mockLdapTemplate.filter.get(0));
    assertEquals(expectedFirstPersonQuery, this.mockLdapTemplate.filter.get(1));
    assertEquals(expectedSecondPersonQuery, this.mockLdapTemplate.filter.get(2));
    assertEquals(11, searchPersons.size());
    assertEquals(110, searchPersons.getTotalNumberOfFoundItems());
    assertEquals("a101", searchPersons.get(0).getSn());
    assertEquals("a110", searchPersons.get(9).getSn());
    assertEquals("b001", searchPersons.get(10).getSn());
  }

  private List<Person> createPersons(String basename, int firstIndex, int lastIndex) {
    List<Person> result = new ArrayList<Person>();
    for (int i = firstIndex; i <= lastIndex; i++) {
      Person person = new Person();
      String name = basename + StringUtils.leftPad(String.valueOf(i), 3, '0');
      person.setSn(name);
      person.setGivenName(name);
      person.setVgrId(name);
      result.add(person);
    }
    return result;
  }

  private String createVgridQuery(List<Person> persons) {
    StringBuilder result = new StringBuilder();
    for (Person person : persons) {
      result.append("(vgr-id=").append(person.getVgrId()).append(")");
    }
    return result.toString();
  }

  private List<String> createVgrIds(List<Person> persons) {
    List<String> result = new ArrayList<String>();
    for (Person person : persons) {
      result.add(person.getVgrId());
    }
    return result;
  }

  @Test
  public void testSearchPersonsWithvgrId() throws KivException {
    this.mockLdapTemplate.result.put("(&(objectclass=vgrUser)(vgr-id=*1*))", Arrays.asList((Object) new Unit()));
    this.mockLdapTemplate.result.put("(&(objectclass=vgrUser)(vgr-id=1))", Arrays.asList((Object) new Unit()));
    this.personRepository.searchPersons("", 1);
    assertEquals("(&(objectclass=vgrUser))", this.mockLdapTemplate.filter.get(0));
    this.personRepository.searchPersons("1", 1);
    assertEquals("(&(objectclass=vgrUser)(vgr-id=*1*))", this.mockLdapTemplate.filter.get(1));
    this.personRepository.searchPersons("\"1\"", 1);
    assertEquals("(&(objectclass=vgrUser)(vgr-id=1))", this.mockLdapTemplate.filter.get(2));
  }

  @Test
  public void testGetPersonsForUnits() throws Exception {
    this.mockLdapTemplate.result.put("(&(!(objectClass=vgrAnstallning))(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4)))", Arrays.asList((Object) new Unit()));
    List<Unit> units = this.generateTestUnitList();
    List<Person> persons = this.personRepository.getPersonsForUnits(units, 5);
    assertFalse(persons.isEmpty());
    assertEquals("(&(!(objectClass=vgrAnstallning))(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4)))", this.mockLdapTemplate.filter.get(0));
  }

  @Test
  public void testGetAllPersons() throws KivException {
    LdapTemplateMock ldapTemplate = new LdapTemplateMock();
    this.personRepository.setLdapTemplate(ldapTemplate);

    DirContextOperationsMock regionName1 = new DirContextOperationsMock();
    regionName1.addAttributeValue("vgr-id", "kal456");
    DirContextOperationsMock regionName2 = new DirContextOperationsMock();
    regionName2.addAttributeValue("vgr-id", "abc123");
    ldapTemplate.addDirContextOperationForSearch(regionName1);
    ldapTemplate.addDirContextOperationForSearch(regionName2);
    List<Person> allPersons = this.personRepository.getAllPersons();
    ldapTemplate.assertSearchFilter("(vgr-id=*)");
    assertNotNull(allPersons);
    assertEquals(2, allPersons.size());
  }

  @Test(expected = KivException.class)
  public void getAllPersonsThrowsKivExceptionOnNamingException() throws KivException {
    LdapTemplateMock ldapTemplate = new LdapTemplateMock();
    this.personRepository.setLdapTemplate(ldapTemplate);

    ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.personRepository.getAllPersons();
  }

  @Test
  public void testGetPersonByVgrId() throws KivException {
    LdapTemplateMock ldapTemplate = new LdapTemplateMock();
    this.personRepository.setLdapTemplate(ldapTemplate);

    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("givenName", "Kalle");
    ldapTemplate.addDirContextOperationForSearch(dirContext);

    Person person = this.personRepository.getPersonByVgrId("ksn829");

    ldapTemplate.assertSearchFilter("(objectclass=vgrUser)");
    assertNotNull(person);
  }

  @Test(expected = KivNoDataFoundException.class)
  public void getPersonByVgrIdThrowsKivNoDataFoundExceptionOnNamingException() throws KivException {
    LdapTemplateMock ldapTemplate = new LdapTemplateMock();
    this.personRepository.setLdapTemplate(ldapTemplate);

    ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.personRepository.getPersonByVgrId("abc123");
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

  private CodeTablesService getCodeTableServiceMock() {
    CodeTableServiceMock codeTableServiceMock = new CodeTableServiceMock();
    codeTableServiceMock.addListToMap(CodeTableName.HSA_SPECIALITY_CODE, Arrays.asList("specialityCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, Arrays.asList("languageCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_TITLE, Arrays.asList("profGroup"));
    codeTableServiceMock.addListToMap(CodeTableName.VGR_AO3_CODE, Arrays.asList("administration1,administration2".split(",")));
    codeTableServiceMock.addListToMap(CodeTableName.PA_TITLE_CODE, Arrays.asList("employmentTitle", "Kurator"));

    return codeTableServiceMock;
  }

  private void setupTimeSource() {
    this.calendar = Calendar.getInstance();
    this.calendar.set(2009, 8, 19, 16, 23);
    this.calendar.set(Calendar.SECOND, 48);
    this.calendar.set(Calendar.MILLISECOND, 0);
    TimeSource timeSource = new TimeSource() {
      @Override
      public long millis() {
        return PersonRepositoryTest.this.calendar.getTimeInMillis();
      }
    };
    TimeUtil.setTimeSource(timeSource);
  }

  class CodeTableServiceMock implements CodeTablesService {

    private final Map<CodeTableName, List<String>> codeTables = new HashMap<CodeTableName, List<String>>();

    public void addListToMap(CodeTableName key, List<String> list) {
      this.codeTables.put(key, list);
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return this.codeTables.get(codeTableName);
    }

    @Override
    public String getValueFromCode(CodeTableNameInterface codeTableName, String string) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getAllValuesItemsFromCodeTable(String codeTableName) {
      return null;
    }
  }

  class MockLdapTemplate extends LdapTemplate {

    String base;
    Map<String, List<? extends Object>> result = new HashMap<String, List<? extends Object>>();
    List<String> filter = new ArrayList<String>();
    DistinguishedName dn;
    ContextMapper contextMapper;

    @Override
    @SuppressWarnings("unchecked")
    public List search(Name base, String filter, int searchScope, ContextMapper mapper) {
      assertEquals(SearchControls.SUBTREE_SCOPE, searchScope);
      this.filter.add(filter);
      this.base = base.toString();
      this.contextMapper = mapper;
      return this.result.get(filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(Name base, String filter, SearchControls controls, ContextMapper mapper) {
      assertEquals("SearchControls not subtree", SearchControls.SUBTREE_SCOPE, controls.getSearchScope());
      this.filter.add(filter);
      this.base = base.toString();
      this.contextMapper = mapper;
      return this.result.get(filter);
    }
  }
}
