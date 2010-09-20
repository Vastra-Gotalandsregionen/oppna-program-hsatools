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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;

import com.thoughtworks.xstream.XStream;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.CodeTableNameInterface;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfPerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService;
import se.vgregion.kivtools.util.time.TimeSource;
import se.vgregion.kivtools.util.time.TimeUtil;

public class KivwsPersonRepositoryTest {

  private KivwsPersonRepository kivwsPersonRepository;
  private LdapTemplateMock ldapTemplateMock;
  private CodeTableServiceMock codeTableServiceMock;
  private VGRegionWebService vgRegionWebService;

  @Before
  public void setUp() throws Exception {
    kivwsPersonRepository = new KivwsPersonRepository();
    kivwsPersonRepository.setUnitFkField("vgrOrgRel");
    ldapTemplateMock = new LdapTemplateMock();
    kivwsPersonRepository.setLdapTemplate(ldapTemplateMock);
    codeTableServiceMock = new CodeTableServiceMock();
    codeTableServiceMock.addListToMap(CodeTableName.HSA_SPECIALITY_CODE, Arrays.asList("specialityCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, Arrays.asList("languageCode"));
    codeTableServiceMock.addListToMap(CodeTableName.HSA_TITLE, Arrays.asList("profGroup"));
    codeTableServiceMock.addListToMap(CodeTableName.VGR_AO3_CODE, Arrays.asList("administration1,administration2".split(",")));
    codeTableServiceMock.addListToMap(CodeTableName.PA_TITLE_CODE, Arrays.asList("employmentTitle", "Kurator"));
    kivwsPersonRepository.setCodeTablesService(codeTableServiceMock);
    KivwsFactoryBean kivwsFactoryBean = new KivwsFactoryBean();
    
    
    // Integration test
    /*
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    Resource resource = defaultResourceLoader.getResource("classpath:se/vgregion/kivtools/search/svc/impl/kiv/ldap/search-composite-svc-connection.properties");
    Properties loadAllProperties = PropertiesLoaderUtils.loadProperties(resource);
    kivwsFactoryBean.setProperties(loadAllProperties);
    */
    //vgRegionWebService = kivwsFactoryBean.createWebService();
  }
  
  private void kivwsIntegrationTest(String filter)  {
    ArrayOfPerson searchPerson = null;
    try {
      searchPerson = vgRegionWebService.searchPerson(filter, new ArrayOfString(), VGRegionDirectory.KIV, null, null);
      XStream xStream = new XStream();
      FileWriter fileWriter = new FileWriter(new File("kivwsPersons.xml"));
      ObjectOutputStream createObjectOutputStream = xStream.createObjectOutputStream(fileWriter);
      createObjectOutputStream.writeObject(searchPerson);
      createObjectOutputStream.flush();
      createObjectOutputStream.close();
    } catch (VGRException_Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertNotNull(searchPerson);
  }

  @Test
  public void testSearchPersonsStringInt() throws KivException {
    String expectedFilter = "(vgr-id=*dav*)";
    String expectedBase = "ou=Personal,o=vgr";
    SikSearchResultList<Person> searchPersonsResult = kivwsPersonRepository.searchPersons("dav", 2);
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
    //kivwsIntegrationTest(expectedFilter);
  }

  @Test
  public void testGetPersonByVgrId() throws KivException {
    String expectedFilter = "(cn=andav)";
    String expectedBase = "cn=andav,ou=Personal,o=vgr";
    Person personByVgrId = kivwsPersonRepository.getPersonByVgrId("andav");
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
    //kivwsIntegrationTest(expectedFilter);
  }

  @Test
  public void testGetAllPersonsVgrId() throws KivException {
    String expectedFilter = "(vgr-id=*)";
    String expectedBase = "ou=Personal,o=vgr";
    List<String> allPersonsVgrId = kivwsPersonRepository.getAllPersonsVgrId();
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
    //kivwsIntegrationTest(expectedFilter);
  }

  @Test
  public void testGetAllPersons() throws KivException {
    String expectedFilter = "(vgr-id=*)";
    String expectedBase = "ou=Personal,o=vgr";
    List<Person> allPersons = kivwsPersonRepository.getAllPersons();
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
    assertEquals(2, ldapTemplateMock.searchScope);
    //kivwsIntegrationTest(expectedFilter);
  }

  // @Test
  // public void testGetPersonDNsByEmployment() throws KivException {
  // String expectedFilter = "(vgr-id=*)";
  // String expectedBase = "ou=Personal,o=vgr";
  // kivwsPersonRepository.getPersonDNsByEmployment("dn", 2, 2);
  // assertEquals(expectedBase, ldapTemplateMock.base.get(0));
  // assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
  // }

  @Test
  public void testGetPersonsForUnits() throws KivException {
    String expectedFilter = "(|(vgrOrgRel=unit1)(vgrOrgRel=unit2))";
    String expectedBase = "ou=Personal,o=vgr";

    Unit unit1 = new Unit();
    unit1.setHsaIdentity("unit1");

    Unit unit2 = new Unit();
    unit2.setHsaIdentity("unit2");

    SikSearchResultList<Person> personsForUnits = kivwsPersonRepository.getPersonsForUnits(Arrays.asList(unit1, unit2), 2);
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter, ldapTemplateMock.filter.get(0));
    //kivwsIntegrationTest(expectedFilter);
  }

  @Test
  public void testSearchPersonsSearchPersonCriterionsInt() throws KivException, ParseException {
    final Date date = TimeUtil.parseStringToZuluTime("20100915155815Z");
    TimeUtil.setTimeSource(new TimeSource() {

      @Override
      public long millis() {
        return date.getTime();
      }
    });
    String expectedFilter1 = "(&(hsaStartDate<=20100915155815Z)(|(!(hsaEndDate=*))(hsaEndDate>=20100915155815Z))(title=*SystemDev*)(|(hsaTelephoneNumber=*123456*)(mobileTelephoneNumber=*123456*)(hsaInternalPagerNumber=*123456*)(pagerTelephoneNumber=*123456*)(hsaTextPhoneNumber=*123456*)(hsaPublicTelephoneNumber=*123456*)(facsimileTelephoneNumber=*123456*)(hsaSedfSwitchboardTelephoneNo=*123456*))(description=*desc*)(paTitleCode=Consultant))";
    String expectedFilter2 = "(&(|(givenName=*David*)(hsaNickName=*David*))(|(sn=*Bennehult*)(hsaMiddleName=*Bennehult*))(vgr-id=*userId*)(hsaSpecialityCode=specialityCode)(!(vgrSecrMark=J))(vgr-id=test, ou=Personal, o=vgr))";
    String expectedBase = "ou=Personal,o=vgr";
    codeTableServiceMock.addListToMap(CodeTableName.PA_TITLE_CODE, Arrays.asList("Consultant"));

    SearchPersonCriterions searchPersonCriterions = new SearchPersonCriterions();
    searchPersonCriterions.setUserId("userId");
    searchPersonCriterions.setSurname("Bennehult");
    searchPersonCriterions.setGivenName("David");
    searchPersonCriterions.setEmploymentTitle("SystemDev");
    searchPersonCriterions.setEmploymentPosition("Consultant");
    searchPersonCriterions.setPhone("123456");
    searchPersonCriterions.setDescription("desc");
    searchPersonCriterions.setSpecialityArea("specialityCode");

    SikSearchResultList<Person> searchPersons = kivwsPersonRepository.searchPersons(searchPersonCriterions, 1);
    assertEquals(expectedBase, ldapTemplateMock.base.get(0));
    assertEquals(expectedFilter1, ldapTemplateMock.filter.get(0));
    assertEquals(expectedFilter2, ldapTemplateMock.filter.get(1));
   
  }

  class LdapTemplateMock extends LdapTemplate {

    private List<String> base = new ArrayList<String>();
    private List<String> filter = new ArrayList<String>();
    private int searchScope;

    @Override
    public List<Person> search(Name base, String filter, SearchControls controls, ContextMapper mapper) {
      this.base.add(base.toString());
      this.filter.add(filter);
      return Arrays.asList(new Person());
    }

    @Override
    public List<Person> search(Name base, String filter, int searchScope, String[] attrs, ContextMapper mapper) {
      this.base.add(base.toString());
      this.filter.add(filter);
      this.searchScope = searchScope;
      return Arrays.asList(new Person());
    }

    @Override
    public List<Person> search(String base, String filter, int searchScope, String[] attrs, ContextMapper mapper) {
      this.base.add(base.toString());
      this.filter.add(filter);
      this.searchScope = searchScope;
      return Arrays.asList(new Person());
    }

    @Override
    public List<String> search(Name base, String filter, ContextMapper mapper) {
      this.base.add(base.toString());
      this.filter.add(filter);
      return Arrays.asList("cn=test, ou=Personal, o=vgr");
    }
  }

  class CodeTableServiceMock implements CodeTablesService {

    private Map<CodeTableName, List<String>> codeTables = new HashMap<CodeTableName, List<String>>();

    public void addListToMap(CodeTableName key, List<String> list) {
      codeTables.put(key, list);
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableNameInterface codeTableName, String textValue) {
      return codeTables.get(codeTableName);
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

}
