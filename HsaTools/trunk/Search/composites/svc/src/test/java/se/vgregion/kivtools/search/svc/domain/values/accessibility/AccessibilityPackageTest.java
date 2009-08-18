package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

  }
}
