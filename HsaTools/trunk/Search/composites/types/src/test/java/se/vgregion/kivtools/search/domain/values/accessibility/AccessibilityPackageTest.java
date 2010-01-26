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
package se.vgregion.kivtools.search.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityPackage;

public class AccessibilityPackageTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_accessibility_package.xml");
  private NodeList nodeList;
  private AccessibilityPackage accessibilityPackage;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("package");
  }

  @Test
  public void testCreateAccessibilityPackageFromNode() {
    Node node = nodeList.item(0);
    accessibilityPackage = AccessibilityPackage.createAccessibilityPackageFromNode(node);
    assertNotNull(accessibilityPackage);
    assertNull(accessibilityPackage.getId());
    assertNotNull(accessibilityPackage.getName());
    assertEquals("", accessibilityPackage.getName());
    assertNotNull(accessibilityPackage.getCriterias());
    assertEquals(0, accessibilityPackage.getCriterias().size());
    assertNotNull(accessibilityPackage.getImages());
    assertEquals(0, accessibilityPackage.getImages().size());

    node = nodeList.item(1);
    accessibilityPackage = AccessibilityPackage.createAccessibilityPackageFromNode(node);
    assertNotNull(accessibilityPackage.getId());
    assertTrue(accessibilityPackage.getId().startsWith("testid_"));
    assertEquals("objectName", accessibilityPackage.getName());
    assertEquals(0, accessibilityPackage.getImages().size());
    assertEquals(1, accessibilityPackage.getCriterias().size());

    node = nodeList.item(2);
    accessibilityPackage = AccessibilityPackage.createAccessibilityPackageFromNode(node);
    assertEquals("name", accessibilityPackage.getName());
    assertEquals(1, accessibilityPackage.getImages().size());
    assertEquals(0, accessibilityPackage.getCriterias().size());
  }

  @Test
  public void testHasVisibleCriterias() {
    Node node = nodeList.item(3);
    accessibilityPackage = AccessibilityPackage.createAccessibilityPackageFromNode(node);
    assertEquals(2, accessibilityPackage.getCriterias().size());
    assertFalse(accessibilityPackage.hasVisibleCriterias());

    accessibilityPackage.getCriterias().get(0).setShow(true);
    assertTrue(accessibilityPackage.hasVisibleCriterias());
  }
}
