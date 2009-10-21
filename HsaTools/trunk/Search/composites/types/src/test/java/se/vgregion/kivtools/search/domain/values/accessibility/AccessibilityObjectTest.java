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
