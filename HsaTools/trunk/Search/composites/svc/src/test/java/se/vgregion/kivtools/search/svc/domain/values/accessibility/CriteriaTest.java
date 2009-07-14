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
package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CriteriaTest {
  private static final Document DOC_WITH_CRITERIA = CriteriaTest.getDocumentFromResource("testxml/doc_with_criteria.xml");

  @Test
  public void testConstructor() {
    NodeList nodeList = DOC_WITH_CRITERIA.getElementsByTagName("criteria");
    Node node = nodeList.item(0);
    Criteria criteria = new Criteria(node);
    assertNotNull("Unexpected null", criteria);
    assertTrue("Unexpected name", criteria.getName().startsWith("crit1_"));
    assertEquals("Unexpected size of disabilities", 2, criteria.getDisabilities().size());
    assertEquals("Unexpected size of additional criterias", 1, criteria.getAdditionalCriterias().size());
    assertEquals("Unexpected description", "input", criteria.getDescription());

    node = nodeList.item(1);
    criteria = new Criteria(node);
    assertNotNull("Unexpected null", criteria);
    assertTrue("Unexpected name", criteria.getName().startsWith("crit2_"));
    assertEquals("Unexpected size of disabilities", 2, criteria.getDisabilities().size());
    assertEquals("Unexpected size of additional criterias", 1, criteria.getAdditionalCriterias().size());
    assertEquals("Unexpected description", "input2", criteria.getDescription());
  }

  private static Document getDocumentFromResource(String resourceName) {
    Document document;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setValidating(false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream inputStream = CriteriaTest.class.getClassLoader().getResourceAsStream(resourceName);
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
}
