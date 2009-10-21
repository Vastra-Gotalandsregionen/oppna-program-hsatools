/**
 * Copyright 2009 Västra Götalandsregionen
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
 */
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.hriv.presentation.forms.AccessibilityDatabaseFilterForm;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityObject;
import se.vgregion.kivtools.search.domain.values.accessibility.Block;
import se.vgregion.kivtools.search.domain.values.accessibility.Criteria;

public class DisplayAccessibilityDatabaseBeanTest {
  private static final Document EMPTY_DOC = DisplayAccessibilityDatabaseBeanTest.getDocumentFromResource("testxml/emptydoc.xml");
  private static final Document DOC_WITH_SUBOBJECTS = DisplayAccessibilityDatabaseBeanTest.getDocumentFromResource("testxml/doc_with_subnodes.xml");
  private static LogFactoryMock logFactoryMock;

  private DisplayAccessibilityDatabaseBean bean;
  private HttpFetcherMock httpFetcher;

  @BeforeClass
  public static void setup() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Before
  public void setUp() {
    httpFetcher = new HttpFetcherMock();
    httpFetcher.setContent(DisplayAccessibilityDatabaseBeanTest.getStringFromResource("testxml/doc_with_subnodes.xml"));
    bean = new DisplayAccessibilityDatabaseBean();
    bean.setHttpFetcher(httpFetcher);
    bean.setAccessibilityDatabaseIntegrationGetIdUrl("http://localhost/tdb?method=getId");
    bean.setAccessibilityDatabaseIntegrationGetInfoUrl("http://localhost/tdb?method=getInfo");
    bean.setUseAccessibilityDatabaseIntegration(Boolean.TRUE);
  }

  @Test
  public void testFilterAccessibilityDatabaseInfo() throws IOException, SAXException, ParserConfigurationException {
    try {
      bean.filterAccessibilityDatabaseInfo(null, null);
      fail("NullPointerException should be thrown");
    } catch (NullPointerException e) {
      // Expected exception
    }

    AccessibilityDatabaseFilterForm form = new AccessibilityDatabaseFilterForm();
    try {
      bean.filterAccessibilityDatabaseInfo(null, form);
      fail("NullPointerException should be thrown");
    } catch (NullPointerException e) {
      // Expected exception
    }

    form.setHear(true);
    form.setInfo(true);
    form.setMove(true);
    form.setSee(true);
    form.setSubstances(true);
    form.setLanguageId("2");

    Unit unit = new Unit();
    bean.filterAccessibilityDatabaseInfo(unit, form);

    assertEquals("Unexpected value of formerLanguageId", "2", form.getFormerLanguageId());

    Document doc = EMPTY_DOC;
    Node businessObjectNode = doc.getFirstChild();
    AccessibilityObject businessObject = AccessibilityObject.createAccessibilityObjectFromNode(businessObjectNode);
    ArrayList<AccessibilityObject> subObjects = new ArrayList<AccessibilityObject>();

    AccessibilityInformation accessibilityInformation = new AccessibilityInformation(businessObject, subObjects);
    unit.setAccessibilityInformation(accessibilityInformation);

    bean.filterAccessibilityDatabaseInfo(unit, form);

    doc = DOC_WITH_SUBOBJECTS;
    businessObjectNode = doc.getElementsByTagName("businessobject").item(0);
    businessObject = AccessibilityObject.createAccessibilityObjectFromNode(businessObjectNode);

    accessibilityInformation = new AccessibilityInformation(businessObject, subObjects);
    unit.setAccessibilityInformation(accessibilityInformation);

    Node subObjectNode = doc.getElementsByTagName("subobject").item(0);
    AccessibilityObject subObject = AccessibilityObject.createAccessibilityObjectFromNode(subObjectNode);
    subObjects.add(subObject);
    accessibilityInformation = new AccessibilityInformation(businessObject, subObjects);
    unit.setAccessibilityInformation(accessibilityInformation);

    bean.filterAccessibilityDatabaseInfo(unit, form);

    Block block = businessObject.getBlocks().get(0);
    List<Criteria> criterias = block.getPackages().get(0).getCriterias();

    assertFalse(criterias.get(0).getShow());
    assertTrue(criterias.get(1).getShow());
    assertTrue(criterias.get(2).getShow());
    assertTrue(criterias.get(3).getShow());
    assertTrue(criterias.get(4).getShow());
    assertTrue(criterias.get(5).getShow());

    block = subObject.getBlocks().get(0);
    criterias = block.getPackages().get(0).getCriterias();

    assertFalse(criterias.get(0).getShow());
    assertTrue(criterias.get(1).getShow());
    assertTrue(criterias.get(2).getShow());
    assertTrue(criterias.get(3).getShow());
    assertTrue(criterias.get(4).getShow());
    assertTrue(criterias.get(5).getShow());
  }

  @Test
  public void testUseAccessibilityDatabaseIntegrationFalse() {
    bean.setUseAccessibilityDatabaseIntegration(Boolean.FALSE);

    Unit unit = new Unit();
    AccessibilityDatabaseFilterForm form = new AccessibilityDatabaseFilterForm();

    bean.filterAccessibilityDatabaseInfo(unit, form);
    this.httpFetcher.assertLastUrlFetched(null);

    bean.assignAccessibilityDatabaseId(unit);
    this.httpFetcher.assertLastUrlFetched(null);

    bean.assignAccessibilityDatabaseInfo(unit, form);
    this.httpFetcher.assertLastUrlFetched(null);
  }

  @Test
  public void testAssignAccessibilityDatabaseId() {
    Unit unit = new Unit();
    this.httpFetcher.setContent("<?xml version=\"1.0\"?><doc><string>abc</string></doc>");
    boolean result = bean.assignAccessibilityDatabaseId(unit);
    assertFalse(result);

    this.httpFetcher.setContent("<?xml version=\"1.0\"?><doc><string>123</string></doc>");
    result = bean.assignAccessibilityDatabaseId(unit);
    assertTrue(result);
    assertEquals(Integer.valueOf(123), unit.getAccessibilityDatabaseId());

    this.httpFetcher.setContent("<?xml version=\"1.0\"?><doc><string>123</string><string>234</string></doc>");
    result = bean.assignAccessibilityDatabaseId(unit);
    assertTrue(result);
    assertEquals(Integer.valueOf(234), unit.getAccessibilityDatabaseId());
  }

  @Test
  public void testGetMessageBundle() {
    // Swedish
    Properties bundle = bean.getMessageBundle(1);
    assertNotNull(bundle);
    assertEquals("Svårt att höra", bundle.getProperty("hearing"));

    // English
    bundle = bean.getMessageBundle(2);
    assertNotNull(bundle);
    assertEquals("Hearing impaired", bundle.getProperty("hearing"));

    // Deutsch
    bundle = bean.getMessageBundle(4);
    assertNotNull(bundle);
    assertEquals("Schwerhörig", bundle.getProperty("hearing"));

    // Easily read swedish
    bundle = bean.getMessageBundle(5);
    assertNotNull(bundle);
    assertEquals("Svårt att höra", bundle.getProperty("hearing"));
  }

  private static Document getDocumentFromResource(String resourceName) {
    Document document;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream inputStream = DisplayAccessibilityDatabaseBeanTest.class.getClassLoader().getResourceAsStream(resourceName);
      document = builder.parse(inputStream);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return document;
  }

  private static String getStringFromResource(String resourceName) {
    InputStream inputStream = DisplayAccessibilityDatabaseBeanTest.class.getClassLoader().getResourceAsStream(resourceName);

    Reader reader = new InputStreamReader(inputStream);
    StringWriter writer = new StringWriter();

    try {
      char[] buffer = new char[1024];
      int readChars = -1;
      while ((readChars = reader.read(buffer)) > 0) {
        writer.write(buffer, 0, readChars);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return writer.toString();
  }
}
