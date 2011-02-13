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

package se.vgregion.kivtools.search.svc.codetables.impl.vgr;

import static org.junit.Assert.assertEquals;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.KivwsFactoryBean;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfDeletedObject;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfPerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfResource;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfServer;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfString;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfTransaction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnsurePerson;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Person;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionDirectory;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebServiceImplPortType;

import com.thoughtworks.xstream.XStream;

public class KivwsCodeTableServiceImplTest {
  private VGRegionWebServiceImplPortType kivWebService;
  private static Map<String, String2StringMap> codeValuesMap;
  private KivwsServiceMock kivwsServiceMock;
  private KivwsCodeTablesServiceImpl kivwsCodeTablesServiceImpl;

  @SuppressWarnings("unchecked")
  @BeforeClass
  public static void berforeClass() throws IOException, ClassNotFoundException {
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    Resource codeAndCleartexts = defaultResourceLoader.getResource("classpath:codesAndCleartexts.xml");
    XStream xStream = new XStream();
    ObjectInputStream objectInputStream = xStream.createObjectInputStream(codeAndCleartexts.getInputStream());
    codeValuesMap = (Map<String, String2StringMap>) objectInputStream.readObject();
  }

  @Before
  public void setup() throws Exception {
    this.kivwsServiceMock = new KivwsServiceMock();
    this.kivwsCodeTablesServiceImpl = new KivwsCodeTablesServiceImpl(this.kivwsServiceMock);
    this.kivwsCodeTablesServiceImpl.init();
  }

  /**
   * Use to generate a snapshot of the KivwsCodeTables
   * 
   * @throws VGRException_Exception
   * @throws IOException
   */
  @SuppressWarnings("unused")
  private void writeToFile() throws VGRException_Exception, IOException {
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    Resource resource = defaultResourceLoader.getResource("classpath:se/vgregion/kivtools/search/svc/impl/kiv/ldap/search-composite-svc-connection.properties");
    Properties loadAllProperties = PropertiesLoaderUtils.loadProperties(resource);

    KivwsFactoryBean kivwsFactoryBean = new KivwsFactoryBean();
    kivwsFactoryBean.setProperties(loadAllProperties);
    this.kivWebService = kivwsFactoryBean.createWebService();

    KivwsCodeTableName[] values = KivwsCodeTableName.values();
    Map<String, String2StringMap> codeValues = new HashMap<String, String2StringMap>();

    for (KivwsCodeTableName kivwsCodeTableName : values) {
      String2StringMap attributeCodesAndCleartexts = this.kivWebService.getAttributeCodesAndCleartexts(kivwsCodeTableName.toString());
      codeValues.put(kivwsCodeTableName.toString(), attributeCodesAndCleartexts);
    }
    FileWriter fileWriter = new FileWriter("codesAndCleartexts.xml");
    XStream xStream = new XStream();
    ObjectOutputStream createObjectOutputStream = xStream.createObjectOutputStream(fileWriter);
    createObjectOutputStream.writeObject(codeValues);
    createObjectOutputStream.flush();
    createObjectOutputStream.close();

  }

  @Test
  public void hsaAdministrationFormTest() throws VGRException_Exception, IOException {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_ADMINISTRATION_FORM.name());
    assertEquals(33, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void hsaBusinessClassificationCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.name());
    assertEquals(303, allValuesItemsFromKivwsCodeTable.size());
  }

  @Test
  public void hsaCountyCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_COUNTY_CODE.name());
    assertEquals(1, allValuesItemsFromKivwsCodeTable.size());

  }

  // @Ignore
  @Test
  public void hsaLanguageKnowledgeCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.name());
    assertEquals(183, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void hsaManagementCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_MANAGEMENT_CODE.name());
    assertEquals(8, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void hsaMunicipalityCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_MUNICIPALITY_CODE.name());

    assertEquals(49, allValuesItemsFromKivwsCodeTable.size());
  }

  @Test
  public void hsaSpecialityCodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_SPECIALITY_CODE.name());
    assertEquals(88, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void hsaPATitleTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.PA_TITLE_CODE.name());
    assertEquals(209, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void hsavgrAO0CodeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.VGR_AO3_CODE.name());
    assertEquals(63, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void vgrCareTypeTest() {
    List<String> allValuesItemsFromKivwsCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.CARE_TYPE.name());
    assertEquals(3, allValuesItemsFromKivwsCodeTable.size());

  }

  @Test
  public void languageKnowledgeValueFromTextValueTest() {
    String searchString = "ja";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getValuesFromTextValue(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);
    assertEquals(8, kivwsCodeFromTextValue.size());
  }

  @Test
  public void languageKnowledgeCodeFromTextValueTest() {
    String searchString = "Odjibwa (Chippewa)";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);
    assertEquals("oji", kivwsCodeFromTextValue.get(0));

  }

  // TODO: Update the code to upper case
  @Test
  public void languageKnowledgeValueFromCodeTest() {
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "oji");
    assertEquals("Odjibwa (Chippewa)", valueFromKivwsCode);
  }

  @Test
  public void hsaAdminitrationFormCodeFromTextValueTest() {
    String searchString = "Regionala statliga myndigheter";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, searchString);
    assertEquals("89", kivwsCodeFromTextValue.get(0));

  }

  @Test
  public void hsaAdministrationFormValueFromCodeTest() {
    String searchString = "89";
    String resultString = "Regionala statliga myndigheter";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, searchString);
    assertEquals(resultString, valueFromKivwsCode);

  }

  @Test
  public void hsaBusinessClassificationCode_CodeFromTextValueTest() {
    String searchString = "Medicinteknisk verksamhet/MTA";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);
    assertEquals("2009", kivwsCodeFromTextValue.get(0));
  }

  @Test
  public void hsaBusinessClassificationCode_ValueFromCodeTest() {
    String searchString = "2009";
    String expectedString = "Medicinteknisk verksamhet/MTA";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void hsaCountyCode_CodeFromTextValueTest() {
    String searchString = "Västra Götalands län";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_COUNTY_CODE, searchString);
    assertEquals("14", kivwsCodeFromTextValue.get(0));

  }

  @Test
  public void hsaCountyCode_ValueFromCodeTest() {
    String searchString = "14";
    String expectedString = "Västra Götalands län";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_COUNTY_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void hsaManagentCode_CodeFromTextValueTest() {
    String searchString = "Landsting/Region";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_MANAGEMENT_CODE, searchString);
    assertEquals("1", kivwsCodeFromTextValue.get(0));

  }

  @Test
  public void hsaManagementCode_ValueFromCodeTest() {
    String searchString = "1";
    String expectedString = "Landsting/Region";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_MANAGEMENT_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void hsaMunicipalityCode_CodeFromTextValueTest() {
    String searchString = "Götene";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, searchString);
    assertEquals("1471", kivwsCodeFromTextValue.get(0));
  }

  @Test
  public void hsaMunicipalityCode_ValueFromCodeTest() {
    String searchString = "1471";
    String expectedString = "Götene";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void hsaSpecialityCode_CodeFromTextValueTest() {
    String searchString = "Kirurgisk bakteriologi och virologi";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.HSA_SPECIALITY_CODE, searchString);
    assertEquals("60200", kivwsCodeFromTextValue.get(0));

  }

  @Test
  public void hsaSpecialityCode_ValueFromCodeTest() {
    String searchString = "60200";
    String expectedString = "Kirurgisk bakteriologi och virologi";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.HSA_SPECIALITY_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);
  }

  @Test
  public void hsaPATitleCode_CodeFromTextValueTest() {
    String searchString = "Sjuksköterska, handikapp- och äldreomsorg/geriatrik";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.PA_TITLE_CODE, searchString);
    assertEquals("206014", kivwsCodeFromTextValue.get(0));

  }

  @Test
  public void hsaPATitleCode_ValueFromCodeTest() {
    String searchString = "206014";
    String expectedString = "Sjuksköterska, handikapp- och äldreomsorg/geriatrik";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.PA_TITLE_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void hsaVgrAO3Code_CodeFromTextValueTest() {
    String searchString = "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.VGR_AO3_CODE, searchString);
    assertEquals("020", kivwsCodeFromTextValue.get(0));
  }

  @Test
  public void hsaVgrAO3Code_ValueFromCodeTest() {
    String searchString = "020";
    String expectedString = "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.VGR_AO3_CODE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);
  }

  @Test
  public void hsaCareTypeCodeFromTextValueTest() {
    String searchString = "Slutenvård";
    List<String> kivwsCodeFromTextValue = this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(KivwsCodeTableName.CARE_TYPE, searchString);
    assertEquals("02", kivwsCodeFromTextValue.get(0));
  }

  @Test
  public void hsaCareTypeValueFromCodeTest() {
    String searchString = "02";
    String expectedString = "Slutenvård";
    String valueFromKivwsCode = this.kivwsCodeTablesServiceImpl.getValueFromCode(KivwsCodeTableName.CARE_TYPE, searchString);
    assertEquals(expectedString, valueFromKivwsCode);

  }

  @Test
  public void testHsaBusinessTypes() {
    List<String> allValuesItemsFromCodeTable = this.kivwsCodeTablesServiceImpl.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_BUSINESS_TYPE.name());
    assertEquals("Vårdcentral", allValuesItemsFromCodeTable.get(1));
  }

  @Test(expected = RuntimeException.class)
  public void getValueFromCodeExceptionTest() {
    String searchString = "02";
    this.kivwsCodeTablesServiceImpl.getValueFromCode(CodeTableName.VGR_CARE_TYPE, searchString);
  }

  @Test(expected = RuntimeException.class)
  public void getCodeFromTextValueExceptionTest() {
    String searchString = "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)";
    this.kivwsCodeTablesServiceImpl.getCodeFromTextValue(CodeTableName.VGR_AO3_CODE, searchString);
  }

  @Test(expected = RuntimeException.class)
  public void getValuesFromTextValueExceptionTest() {
    String searchString = "Slutenvård";
    this.kivwsCodeTablesServiceImpl.getValuesFromTextValue(CodeTableName.VGR_CARE_TYPE, searchString);
  }

  class KivwsServiceMock implements VGRegionWebServiceImplPortType {

    @Override
    public String2StringMap getAttributeCodesAndCleartexts(String arg0) throws VGRException_Exception {
      return codeValuesMap.get(arg0);
    }

    @Override
    public ArrayOfDeletedObject getDeletedUnits(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Function getFunctionAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getFunctionTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public Person getPersonEmploymentAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getPersonTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getReturnAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForEmployment(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForFunction(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForPerson(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForResource() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForServer() throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnit(VGRegionDirectory arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfString getSearchAttributesForUnsurePerson() throws VGRException_Exception {
      return null;
    }

    @Override
    public Unit getUnitAtSpecificTime(String arg0, String arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfTransaction getUnitTransactions(String arg0) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfFunction searchFunction(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfPerson searchPerson(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfPerson searchPersonEmployment(String arg0, ArrayOfString arg1, String arg2, ArrayOfString arg3, VGRegionDirectory arg4, String arg5, String arg6) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfResource searchResource(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfServer searchServer(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfUnit searchUnit(String arg0, ArrayOfString arg1, VGRegionDirectory arg2, String arg3, String arg4) throws VGRException_Exception {
      return null;
    }

    @Override
    public ArrayOfUnsurePerson searchUnsurePerson(String arg0, ArrayOfString arg1) throws VGRException_Exception {
      return null;
    }
  }
}
