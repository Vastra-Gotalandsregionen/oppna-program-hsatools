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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.xml.ws.BindingProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRException_Exception;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.VGRegionWebService_Service;

import com.thoughtworks.xstream.XStream;

@Ignore
public class KivwsCodeTableServiceImplTest {
  private VGRegionWebService_Service regionWebServiceService;
  private static VGRegionWebService vgRegionWebServiceImplPort;
  private LdapTemplate ldapTemplate;
  private CodeTablesServiceImpl codeTablesService;
  private KivwsCodeTablesServiceImpl kivwsCodeTableService;

  @Before
  public void setup() throws Exception {

    // Load property file for webservice connection.
    DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
    Resource resource = defaultResourceLoader.getResource("classpath:se/vgregion/kivtools/search/svc/impl/kiv/ldap/search-composite-svc-connection.properties");
    Properties loadAllProperties = PropertiesLoaderUtils.loadProperties(resource);

    // Create KIVWS webservice.
    regionWebServiceService = new VGRegionWebService_Service();
    vgRegionWebServiceImplPort = regionWebServiceService.getVGRegionWebServiceImplPort();

    // Setup username and password authentication for webservice.
    BindingProvider bindingProvider = (BindingProvider) vgRegionWebServiceImplPort;
    bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, loadAllProperties.getProperty("hsatools.search.svc.kivws.username"));
    bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, loadAllProperties.getProperty("hsatools.search.svc.kivws.password"));

    kivwsCodeTableService = new KivwsCodeTablesServiceImpl(vgRegionWebServiceImplPort);
    kivwsCodeTableService.init();

    // Create LdapTemplate
    LdapContextSource ldapContextSource = new LdapContextSource();
    ldapContextSource.setUrl("ldap://" + loadAllProperties.getProperty("hsatools.search.svc.ldap.ldaphost"));
    ldapContextSource.setPassword(loadAllProperties.getProperty("hsatools.search.svc.ldap.password"));
    ldapContextSource.setUserDn(loadAllProperties.getProperty("hsatools.search.svc.ldap.logindn"));
    ldapContextSource.afterPropertiesSet();
    ldapTemplate = new LdapTemplate(ldapContextSource);
    codeTablesService = new CodeTablesServiceImpl(ldapTemplate);
    codeTablesService.init();
  }

  @Test
  public void hsaAdministrationFormTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_ADMINISTRATION_FORM.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_ADMINISTRATION_FORM.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaBusinessClassificationCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaCountyCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_COUNTY_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_COUNTY_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  // @Ignore
  @Test
  public void hsaLanguageKnowledgeCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE.name());

    List<String> allValuesItemsFromCodeTableTemp = new ArrayList<String>();
    allValuesItemsFromCodeTableTemp.addAll(allValuesItemsFromCodeTable);

    for (String str : allValuesItemsFromCodeTableTemp) {
      if (str.startsWith("----")) {
        allValuesItemsFromCodeTable.remove(str);
      }
    }

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaManagementCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_MANAGEMENT_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_MANAGEMENT_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaMunicipalityCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_MUNICIPALITY_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_MUNICIPALITY_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaSpecialityCodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.HSA_SPECIALITY_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.HSA_SPECIALITY_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsaPATitleTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.PA_TITLE_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.PA_TITLE_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void hsavgrAO0CodeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.VGR_AO3_CODE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.VGR_AO3_CODE.name());

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void vgrCareTypeTest() {
    List<String> allValuesItemsFromCodeTable = codeTablesService.getAllValuesItemsFromCodeTable(CodeTableName.VGR_CARE_TYPE.name());
    List<String> allValuesItemsFromKivwsCodeTable = kivwsCodeTableService.getAllValuesItemsFromCodeTable(KivwsCodeTableName.VGR_CARE_TYPE.name());

    List<String> allValuesItemsFromCodeTableTemp = new ArrayList<String>();
    allValuesItemsFromCodeTableTemp.addAll(allValuesItemsFromCodeTable);

    for (String str : allValuesItemsFromCodeTableTemp) {
      if (str.startsWith("----")) {
        allValuesItemsFromCodeTable.remove(str);
      }
    }

    compareResults(allValuesItemsFromKivwsCodeTable, allValuesItemsFromCodeTable);
  }

  @Test
  public void languageKnowledgeValueFromTextValueTest() {
    String searchString = "Odjibwa (Chippewa)";
    List<String> ldapCodeFromTextValue = codeTablesService.getValuesFromTextValue(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getValuesFromTextValue(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  // Return value of the keys from LDAP are upper case and from webservice are lower case
  @Ignore
  @Test
  public void languageKnowledgeCodeFromTextValueTest() {

    String searchString = "Odjibwa (Chippewa)";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  // TODO: Update the code to upper case
  @Test
  public void languageKnowledgeValueFromCodeTest() {

    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "OJI");
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, "oji");

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Odjibwa (Chippewa)");
  }

  @Test
  public void hsaAdminitrationFormCodeFromTextValueTest() {
    String searchString = "Regionala statliga myndigheter";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_ADMINISTRATION_FORM, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaAdministrationFormValueFromCodeTest() {
    String searchString = "89";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Regionala statliga myndigheter");
  }

  @Test
  public void hsaBusinessClassificationCode_CodeFromTextValueTest() {
    String searchString = "Arbetsrehabilitering";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaBusinessClassificationCode_ValueFromCodeTest() {
    String searchString = "1503";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Arbetsrehabilitering");
  }

  @Test
  public void hsaCountyCode_CodeFromTextValueTest() {
    String searchString = "Västra Götalands län";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_COUNTY_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_COUNTY_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaCountyCode_ValueFromCodeTest() {
    String searchString = "14";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_COUNTY_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_COUNTY_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Västra Götalands län");
  }

  @Test
  public void hsaManagentCode_CodeFromTextValueTest() {
    String searchString = "Landsting/Region";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MANAGEMENT_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_MANAGEMENT_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaManagementCode_ValueFromCodeTest() {
    String searchString = "1";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_MANAGEMENT_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_MANAGEMENT_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Landsting/Region");
  }

  @Test
  public void hsaMunicipalityCode_CodeFromTextValueTest() {
    String searchString = "Götene";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_MUNICIPALITY_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaMunicipalityCode_ValueFromCodeTest() {
    String searchString = "1471";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_MUNICIPALITY_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Götene");
  }

  @Test
  public void hsaSpecialityCode_CodeFromTextValueTest() {
    String searchString = "Kirurgisk bakteriologi och virologi";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.HSA_SPECIALITY_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.HSA_SPECIALITY_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaSpecialityCode_ValueFromCodeTest() {
    String searchString = "60200";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.HSA_SPECIALITY_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.HSA_SPECIALITY_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Kirurgisk bakteriologi och virologi");
  }

  @Test
  public void hsaPATitleCode_CodeFromTextValueTest() {
    String searchString = "Sjuksköterska, handikapp- och äldreomsorg/geriatrik";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.PA_TITLE_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.PA_TITLE_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaPATitleCode_ValueFromCodeTest() {
    String searchString = "206014";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.PA_TITLE_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Sjuksköterska, handikapp- och äldreomsorg/geriatrik");
  }

  @Test
  public void hsaVgrAO3Code_CodeFromTextValueTest() {
    String searchString = "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.VGR_AO3_CODE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.VGR_AO3_CODE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaVgrAO3Code_ValueFromCodeTest() {
    String searchString = "020";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.VGR_AO3_CODE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)");
  }

  @Test
  public void hsaCareTypeCodeFromTextValueTest() {
    String searchString = "Slutenvård";
    List<String> ldapCodeFromTextValue = codeTablesService.getCodeFromTextValue(CodeTableName.VGR_CARE_TYPE, searchString);
    List<String> kivwsCodeFromTextValue = kivwsCodeTableService.getCodeFromTextValue(KivwsCodeTableName.VGR_CARE_TYPE, searchString);

    compareResults(kivwsCodeFromTextValue, ldapCodeFromTextValue);
  }

  @Test
  public void hsaCareTypeValueFromCodeTest() {
    String searchString = "02";
    String valueFromCode = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, searchString);
    String valueFromKivwsCode = kivwsCodeTableService.getValueFromCode(KivwsCodeTableName.VGR_CARE_TYPE, searchString);

    compareCodeValues(valueFromKivwsCode, valueFromCode, "Slutenvård");
  }

  @Test(expected = RuntimeException.class)
  public void getValueFromCodeExceptionTest() {
    String searchString = "02";
    kivwsCodeTableService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, searchString);
  }

  @Test(expected = RuntimeException.class)
  public void getCodeFromTextValueExceptionTest() {
    String searchString = "Regionstyrelsen (ägarutskott, arkivnämnd, regiongem. förv.org- och verks)";
    kivwsCodeTableService.getCodeFromTextValue(CodeTableName.VGR_AO3_CODE, searchString);
  }

  @Test(expected = RuntimeException.class)
  public void getValuesFromTextValueExceptionTest() {
    String searchString = "Slutenvård";
    kivwsCodeTableService.getValuesFromTextValue(CodeTableName.VGR_CARE_TYPE, searchString);
  }

  private void compareResults(List<String> kivwsCodeTable, List<String> ldapCodeTable) {
    Collections.sort(kivwsCodeTable);
    Collections.sort(ldapCodeTable);

    Assert.assertEquals(ldapCodeTable.size(), kivwsCodeTable.size());

    for (int i = 0; i < kivwsCodeTable.size(); i++) {
      Assert.assertEquals(ldapCodeTable.get(i), kivwsCodeTable.get(i));
    }
  }

  private void compareCodeValues(String kivwsResult, String LdapResult, String expectedResult) {
    Assert.assertEquals(LdapResult, kivwsResult);
    Assert.assertEquals(expectedResult, LdapResult);
    Assert.assertEquals(expectedResult, kivwsResult);
  }

  /**
   * Write the KivwsCodeTableName object to a xml-file
   * 
   * @param kivwsCodeTableName
   * @param fileName
   */
  private static void writeObjectToXml(KivwsCodeTableName kivwsCodeTableName, String fileName) {

    try {
      File file = new File(fileName + ".xml");
      FileWriter fileWriter = new FileWriter(file);
      XStream xStream = new XStream();
      ObjectOutputStream createObjectOutputStream = xStream.createObjectOutputStream(fileWriter);

      // Make query against kivws
      String codeNameString = kivwsCodeTableName.toString();
      String2StringMap attributeCodesAndCleartexts = vgRegionWebServiceImplPort.getAttributeCodesAndCleartexts(codeNameString);
      createObjectOutputStream.writeObject(attributeCodesAndCleartexts);

      createObjectOutputStream.flush();
      createObjectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (VGRException_Exception e1) {
      e1.printStackTrace();
    }
  }

  // Use to generate xml-file for unit test
  public static void main(String[] args) throws Exception {
    KivwsCodeTableServiceImplTest kivwsCodeTableServiceImplTest = new KivwsCodeTableServiceImplTest();
    kivwsCodeTableServiceImplTest.setup();

    for (KivwsCodeTableName kivwsCodeTableName : KivwsCodeTableName.values()) {
      KivwsCodeTableServiceImplTest.writeObjectToXml(kivwsCodeTableName, kivwsCodeTableName.toString());
    }

  }

}
