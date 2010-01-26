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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityObject;

public class AccessibilityInformationTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_accessibility_object.xml");
  private NodeList nodeList;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("object");
  }

  @Test
  public void testAccessibilityInformation() {
    AccessibilityInformation accessibilityInformation;
    try {
      accessibilityInformation = new AccessibilityInformation(null, null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    AccessibilityObject businessObject = AccessibilityObject.createAccessibilityObjectFromNode(nodeList.item(0));
    ArrayList<AccessibilityObject> subObjects = new ArrayList<AccessibilityObject>();

    try {
      accessibilityInformation = new AccessibilityInformation(businessObject, null);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    try {
      accessibilityInformation = new AccessibilityInformation(null, subObjects);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    accessibilityInformation = new AccessibilityInformation(businessObject, subObjects);
    assertNotNull(accessibilityInformation.getBusinessObject());
    assertNotNull(accessibilityInformation.getSubObjects());
    assertEquals(0, accessibilityInformation.getSubObjects().size());
  }
}
