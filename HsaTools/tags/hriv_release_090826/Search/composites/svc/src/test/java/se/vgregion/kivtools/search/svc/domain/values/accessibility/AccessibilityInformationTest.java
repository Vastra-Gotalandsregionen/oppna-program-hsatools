package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

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
