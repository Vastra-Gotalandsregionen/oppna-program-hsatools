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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchControls;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = { "/services-config_VGR.xml" })
public class PersonRepositoryTest {
    private PersonRepository personRepository;
    private Calendar calendar;
    private MockLdapTemplate mockLdapTemplate;
    @Autowired
    @Qualifier("ldapTemplatePerson")
    private LdapTemplate ldapTemplate;

    @Before
    public void setUp() throws Exception {
        setupTimeSource();

        personRepository = new PersonRepository();
        mockLdapTemplate = new MockLdapTemplate();
        personRepository.setLdapTemplate(mockLdapTemplate);
        personRepository.setUnitFkField("vgrOrgRel");
        personRepository.setCodeTablesService(getCodeTableServiceMock());
    }

    @After
    public void tearDown() {
        TimeUtil.reset();
    }

    @Test
    public void testGetAllPersonsVgrIDs() throws KivException {
        LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
        ldapTemplateMock.addAttributeForSearch(new BasicAttribute("vgr-id", "user1"));
        ldapTemplateMock.addAttributeForSearch(new BasicAttribute("vgr-id", "user2"));
        personRepository.setLdapTemplate(ldapTemplateMock);
        List<String> allPersonsVgrId = personRepository.getAllPersonsVgrId();
        assertEquals(2, allPersonsVgrId.size());
    }

    @Test
    public void testEmploymentTitleSearch() throws KivException {
        mockLdapTemplate.result.put(
                "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(vgr-id=anama))", Arrays
                        .asList((Object) new Unit()));
        String expectedLdapQuestion1 = "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))";
        String expectedLdapQuestion2 = "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(vgr-id=anama))";
        mockLdapTemplate.result.put(expectedLdapQuestion1, Arrays.asList((Object) "anama"));

        SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
        searchPersonCriterion.setEmploymentTitle("employmentTitle");
        personRepository.searchPersons(searchPersonCriterion, 1);
        assertEquals(expectedLdapQuestion1, mockLdapTemplate.filter.get(0));
        assertEquals(expectedLdapQuestion2, mockLdapTemplate.filter.get(1));
    }

    @Test
    public void testSearchPersons() throws KivException {
        List<Object> units = new ArrayList<Object>();
        units.add(new Unit());

        mockLdapTemplate.result
                .put(
                        "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))",
                        units);

        mockLdapTemplate.result
                .put(
                        "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(vgr-id=anama))",
                        units);

        mockLdapTemplate.result
                .put(
                        "(&(objectclass=vgrAnstallning)(hsaStartDate<=20090919162348Z)(|(!(hsaEndDate=*))(hsaEndDate>=20090919162348Z))(title=*employmentTitle*))",
                        Arrays.asList((Object) "anama"));
        SearchPersonCriterions searchPersonCriterion = new SearchPersonCriterions();
        searchPersonCriterion.setGivenName("Kalle");
        searchPersonCriterion.setSurname("Svensson");

        String expectedResult = "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*)))";

        SikSearchResultList<Person> searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
        assertEquals(expectedResult, mockLdapTemplate.filter.get(0));

        searchPersonCriterion.setUserId("vgr-id");
        searchPersonCriterion.setEmploymentTitle("employmentTitle");
        searchPersonCriterion.setEmployedAtUnit("unitName");
        searchPersonCriterion.setSpecialityArea("speciality");
        searchPersonCriterion.setProfession("profGroup");
        searchPersonCriterion.setLanguageKnowledge("langKnowledgeCode");
        searchPersonCriterion.setEmail("email");
        searchPersonCriterion.setAdministration("administration");
        searchPersons = personRepository.searchPersons(searchPersonCriterion, 10);
        String expectedResult2 = "(&(objectclass=vgrUser)(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(givenName=*Kalle*)(hsaNickName=*Kalle*))(|(sn=*Svensson*)(hsaMiddleName=*Svensson*))(vgr-id=*vgr-id*)(vgrStrukturPerson=*unitName*)(hsaSpecialityCode=specialityCode)(hsaTitle=profGroup)(mail=*email*)(hsaLanguageKnowledgeCode=languageCode)(|(vgrAO3kod=administration1)(vgrAO3kod=administration2))(vgr-id=anama))";
        assertEquals(1, searchPersons.size());
        assertEquals(expectedResult2, mockLdapTemplate.filter.get(2));
    }

    @Test
    public void testSearchPersonsWithvgrId() throws KivException {
        mockLdapTemplate.result.put("(&(objectclass=vgrUser)(vgr-id=*1*))", Arrays.asList((Object) new Unit()));
        mockLdapTemplate.result.put("(&(objectclass=vgrUser)(vgr-id=1))", Arrays.asList((Object) new Unit()));
        personRepository.searchPersons("", 1);
        assertEquals("(&(objectclass=vgrUser))", mockLdapTemplate.filter.get(0));
        personRepository.searchPersons("1", 1);
        assertEquals("(&(objectclass=vgrUser)(vgr-id=*1*))", mockLdapTemplate.filter.get(1));
        personRepository.searchPersons("\"1\"", 1);
        assertEquals("(&(objectclass=vgrUser)(vgr-id=1))", mockLdapTemplate.filter.get(2));
    }

    @Test
    public void testGetPersonsForUnits() throws Exception {
        mockLdapTemplate.result
                .put(
                        "(&(!(objectClass=vgrAnstallning))(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4)))",
                        Arrays.asList((Object) new Unit()));
        List<Unit> units = generateTestUnitList();
        List<Person> persons = personRepository.getPersonsForUnits(units, 5);
        assertFalse(persons.isEmpty());
        assertEquals(
                "(&(!(objectClass=vgrAnstallning))(!(vgrStrukturPerson=*OU=Privata Vårdgivare*))(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4)))",
                mockLdapTemplate.filter.get(0));
    }

    @Test
    public void testGetAllPersonsVgrId() {

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
        codeTableServiceMock
                .addListToMap(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, Arrays.asList("languageCode"));
        codeTableServiceMock.addListToMap(CodeTableName.HSA_TITLE, Arrays.asList("profGroup"));
        codeTableServiceMock.addListToMap(CodeTableName.VGR_AO3_CODE, Arrays
                .asList("administration1,administration2".split(",")));
        codeTableServiceMock.addListToMap(CodeTableName.PA_TITLE_CODE, Arrays.asList("employmentTitle"));
        return codeTableServiceMock;
    }

    private void setupTimeSource() {
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

    class MockLdapTemplate extends LdapTemplate {

        String base;
        Map<String, List<Object>> result = new HashMap<String, List<Object>>();
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
            return result.get(filter);
        }

        @Override
        @SuppressWarnings("unchecked")
        public List search(Name base, String filter, SearchControls controls, ContextMapper mapper) {
            assertEquals("SearchControls not subtree", SearchControls.SUBTREE_SCOPE, controls.getSearchScope());
            this.filter.add(filter);
            this.base = base.toString();
            this.contextMapper = mapper;
            return result.get(filter);
        }
    }
}
