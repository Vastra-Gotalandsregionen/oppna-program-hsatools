/**
 * Copyright 2009 Västa Götalandsregionen
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.hriv.presentation.forms.AccessibilityDatabaseFilterForm;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityObject;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.Block;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.Criteria;

public class DisplayAccessibilityDatabaseBeanTest {
  private static final Document EMPTY_DOC = DisplayAccessibilityDatabaseBeanTest.getDocumentFromResource("testxml/emptydoc.xml");
  private static final Document DOC_WITH_SUBOBJECTS = DisplayAccessibilityDatabaseBeanTest.getDocumentFromResource("testxml/doc_with_subnodes.xml");

  private DisplayAccessibilityDatabaseBean bean;
  private HttpFetcherMock httpFetcher;

  @Before
  public void setUp() {
    httpFetcher = new HttpFetcherMock();
    bean = new DisplayAccessibilityDatabaseBean();
    bean.setHttpFetcher(httpFetcher);
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

    this.httpFetcher.setContent("");
    Unit unit = new Unit();
    bean.filterAccessibilityDatabaseInfo(unit, form);

    assertEquals("Unexpected value of formerLanguageId", "1", bean.getFormerLanguageId());

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

  private static class HttpFetcherMock implements HttpFetcher {
    private String content;

    public void setContent(String content) {
      this.content = content;
    }

    @Override
    public String fetchUrl(String urlToFetch) {
      return this.content;
    }
  }
}
