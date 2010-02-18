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

package se.vgregion.kivtools.search.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.NodeHelper;

public class NodeHelperTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_criteria.xml");
  private NodeList nodeList;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("criteria");
  }

  @Test
  public void testInstantiation() {
    NodeHelper nodeHelper = new NodeHelper();
    assertNotNull(nodeHelper);
  }

  @Test
  public void testGetAttributeTextContent() {
    try {
      NodeHelper.getAttributeTextContent(null, null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    try {
      NodeHelper.getAttributeTextContent(nodeList.item(0), null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    assertEquals("crit1", NodeHelper.getAttributeTextContent(nodeList.item(0), "objectName"));
    assertEquals("16", NodeHelper.getAttributeTextContent(nodeList.item(1), "status"));

    assertNull(NodeHelper.getAttributeTextContent(nodeList.item(2), "objectName"));
    assertNull(NodeHelper.getAttributeTextContent(nodeList.item(0), "unknown"));
  }

  @Test
  public void testIsNodeName() {
    try {
      NodeHelper.isNodeName(null, null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    try {
      NodeHelper.isNodeName(nodeList.item(0), null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    assertTrue(NodeHelper.isNodeName(nodeList.item(0), "criteria"));
    assertTrue(NodeHelper.isNodeName(nodeList.item(0).getChildNodes().item(1), "Disabilities"));
    assertFalse(NodeHelper.isNodeName(nodeList.item(1), "block"));
  }
}
