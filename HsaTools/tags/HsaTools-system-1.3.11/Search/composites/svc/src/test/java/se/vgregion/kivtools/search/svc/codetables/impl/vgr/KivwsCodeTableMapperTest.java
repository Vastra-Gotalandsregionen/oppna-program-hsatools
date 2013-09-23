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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.svc.impl.kiv.ws.KivwsCodeNameTableMapper;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;

import com.thoughtworks.xstream.XStream;

public class KivwsCodeTableMapperTest {

  private static String2StringMap kivwsAdministrationReadObject;
  private static String2StringMap kivwsBussninessReadObject;
  private static String2StringMap kivwsCountyReadObject;
  private static String2StringMap kivwsLanguageReadObject;
  private static String2StringMap kivwsManagementReadObject;
  private static String2StringMap kivwsMunicipalityReadObject;
  private static String2StringMap kivwsSpecialityReadObject;
  private static String2StringMap kivwsPATitleReadObject;
  private static String2StringMap kivwsVgrAO3KodReadObject;
  private static String2StringMap kivwsVgrCareTypeReadObject;
  private static Map<KivwsCodeTableName, Map<String, String>> codeTables = new ConcurrentHashMap<KivwsCodeTableName, Map<String, String>>();

  @BeforeClass
  public static void setupTestData() throws IOException, ClassNotFoundException {

    ClassPathResource kivwsAdministrationCode = new ClassPathResource("hsaadministrationform.xml");
    ClassPathResource kivwsBusinessClassificationCode = new ClassPathResource("hsabusinessclassificationcode.xml");
    ClassPathResource kivwsCountyCode = new ClassPathResource("hsacountycode.xml");
    ClassPathResource kivwsLanguageKnowledgeCode = new ClassPathResource("hsalanguageknowledgecode.xml");
    ClassPathResource kivwsManagementCode = new ClassPathResource("hsamanagementcode.xml");
    ClassPathResource kivwsMunicipalityCode = new ClassPathResource("hsamunicipalitycode.xml");
    ClassPathResource kivwsSpecialityCode = new ClassPathResource("hsaspecialitycode.xml");
    ClassPathResource kivwsPATitle = new ClassPathResource("patitlecode.xml");
    ClassPathResource kivwsVgrAO3Kod = new ClassPathResource("vgrao3kod.xml");
    ClassPathResource kivwsVgrCareType = new ClassPathResource("vgrcaretype.xml");

    XStream xStream = new XStream();
    InputStream inputStream = kivwsAdministrationCode.getInputStream();
    ObjectInputStream kivwsAdministrationCodeInputStream = xStream.createObjectInputStream(inputStream);
    ObjectInputStream kivwsBusinessClassificationCodeInputStream = xStream.createObjectInputStream(kivwsBusinessClassificationCode.getInputStream());
    ObjectInputStream kivwsCountyCodeInputStream = xStream.createObjectInputStream(kivwsCountyCode.getInputStream());
    ObjectInputStream kivwsLanguageKnowledgeCodeInputStream = xStream.createObjectInputStream(kivwsLanguageKnowledgeCode.getInputStream());
    ObjectInputStream kivwsManagementCodeInputStream = xStream.createObjectInputStream(kivwsManagementCode.getInputStream());
    ObjectInputStream kivwsMunicipalityCodeInputStream = xStream.createObjectInputStream(kivwsMunicipalityCode.getInputStream());
    ObjectInputStream kivwsSpecialityCodeInputStream = xStream.createObjectInputStream(kivwsSpecialityCode.getInputStream());
    ObjectInputStream kivwsPATitleInputStream = xStream.createObjectInputStream(kivwsPATitle.getInputStream());
    ObjectInputStream kivwsVgrAO3KodInputStream = xStream.createObjectInputStream(kivwsVgrAO3Kod.getInputStream());
    ObjectInputStream kivwsVgrCareTypeInputStream = xStream.createObjectInputStream(kivwsVgrCareType.getInputStream());

    kivwsAdministrationReadObject = (String2StringMap) kivwsAdministrationCodeInputStream.readObject();
    kivwsBussninessReadObject = (String2StringMap) kivwsBusinessClassificationCodeInputStream.readObject();
    kivwsCountyReadObject = (String2StringMap) kivwsCountyCodeInputStream.readObject();
    kivwsLanguageReadObject = (String2StringMap) kivwsLanguageKnowledgeCodeInputStream.readObject();
    kivwsManagementReadObject = (String2StringMap) kivwsManagementCodeInputStream.readObject();
    kivwsMunicipalityReadObject = (String2StringMap) kivwsMunicipalityCodeInputStream.readObject();
    kivwsSpecialityReadObject = (String2StringMap) kivwsSpecialityCodeInputStream.readObject();
    kivwsPATitleReadObject = (String2StringMap) kivwsPATitleInputStream.readObject();
    kivwsVgrAO3KodReadObject = (String2StringMap) kivwsVgrAO3KodInputStream.readObject();
    kivwsVgrCareTypeReadObject = (String2StringMap) kivwsVgrCareTypeInputStream.readObject();

    KivwsCodeNameTableMapper kivwsCodeTableMapper = new KivwsCodeNameTableMapper();
    kivwsCodeTableMapper.mapFromContext(kivwsAdministrationReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsBussninessReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsCountyReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_COUNTY_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsLanguageReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsManagementReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_MANAGEMENT_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsMunicipalityReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsSpecialityReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.HSA_SPECIALITY_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsPATitleReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.PA_TITLE_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsVgrAO3KodReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.VGR_AO3_CODE, kivwsCodeTableMapper.getCodeTableContent());

    kivwsCodeTableMapper.mapFromContext(kivwsVgrCareTypeReadObject);
    kivwsCodeTableMapper.getCodeTableContent();
    codeTables.put(KivwsCodeTableName.VGR_CARE_TYPE, kivwsCodeTableMapper.getCodeTableContent());
  }

  @Test(expected = RuntimeException.class)
  public void testException() {
    KivwsCodeNameTableMapper kivwsCodeNameTableMapper = new KivwsCodeNameTableMapper();
    kivwsCodeNameTableMapper.mapFromContext("Not valid class type");
  }

  @Test
  public void administrationTest() {
    String searchKey = "41";
    String expectedResult = "Bankaktiebolag";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_ADMINISTRATION_FORM);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void businessTest() {
    String searchKey = "1505";
    String expectedResult = "Barnmorskemottagning";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void countyTest() {
    String searchKey = "14";
    String expectedResult = "Västra Götalands län";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  // TODO: The key value should be in upper case
  @Test
  public void languageTest() {
    String searchKey = "lub";
    String expectedResult = "Luba-Katanga";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void managementTest() {
    String searchKey = "6";
    String expectedResult = "Privat, utan offentlig finansiering";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void municipalityTest() {
    String searchKey = "1499";
    String expectedResult = "Falköping";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void specialityTest() {
    String searchKey = "60100";
    String expectedResult = "Klinisk immunologi och transfusionsmedicin";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void paTitleTest() {
    String searchKey = "551013";
    String expectedResult = "Brandman";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void vgrAO3KodTest() {
    String searchKey = "085";
    String expectedResult = "FSS Frölunda specialist sjukhus";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  @Test
  public void vgrCareTypeTest() {
    String searchKey = "03";
    String expectedResult = "Hemsjukvård";
    Map<String, String> resultMap = codeTables.get(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE);
    compareResults(resultMap, searchKey, expectedResult);

  }

  private void compareResults(Map<String, String> result, String searchKey, String expectedResult) {
    Assert.assertNotNull(result);
    Assert.assertEquals(expectedResult, result.get(searchKey));
  }

}
