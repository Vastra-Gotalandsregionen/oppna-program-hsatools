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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityObject;

public class AccessibilityObjectTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_accessibility_object.xml");
  private NodeList nodeList;
  private AccessibilityObject accessibilityObject;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("object");
  }

  @Test
  public void testCreateAccessibilityPackageFromNode() {
    Node node = nodeList.item(0);
    accessibilityObject = AccessibilityObject.createAccessibilityObjectFromNode(node);
    assertNotNull(accessibilityObject);
    assertNull(accessibilityObject.getId());
    assertNotNull(accessibilityObject.getName());
    assertEquals("", accessibilityObject.getName());
    assertNull(accessibilityObject.getUpdateStamp());
    assertNotNull(accessibilityObject.getBlocks());
    assertEquals(0, accessibilityObject.getBlocks().size());

    node = nodeList.item(1);
    accessibilityObject = AccessibilityObject.createAccessibilityObjectFromNode(node);
    assertNotNull(accessibilityObject.getId());
    assertTrue(accessibilityObject.getId().startsWith("testid_"));
    assertEquals("objectName", accessibilityObject.getName());
    assertEquals(0, accessibilityObject.getBlocks().size());

    node = nodeList.item(2);
    accessibilityObject = AccessibilityObject.createAccessibilityObjectFromNode(node);
    assertEquals("name", accessibilityObject.getName());
    assertEquals("stamp1234", accessibilityObject.getUpdateStamp());
    assertEquals(1, accessibilityObject.getBlocks().size());
  }
}
