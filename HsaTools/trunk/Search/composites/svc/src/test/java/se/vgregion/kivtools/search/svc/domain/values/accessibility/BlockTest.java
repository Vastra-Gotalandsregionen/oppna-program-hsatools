package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BlockTest {
  private static final Document DOC_WITH_CRITERIA = TestXmlHelper.getDocumentFromResource("testxml/doc_with_block.xml");
  private NodeList nodeList;
  private Block block;

  @Before
  public void setUp() {
    nodeList = DOC_WITH_CRITERIA.getElementsByTagName("block");
  }

  @Test
  public void testCreateBlockFromNode() {
    Node node = nodeList.item(0);
    block = Block.createBlockFromNode(node);
    assertNotNull("Unexpected null", block);
    assertNotNull(block.getId());
    assertTrue(block.getId().startsWith("testid_"));
    assertNotNull(block.getPackages());
    assertEquals(1, block.getPackages().size());

    node = nodeList.item(1);
    block = Block.createBlockFromNode(node);
    assertNotNull("Unexpected null", block);
    assertNotNull(block.getId());
    assertTrue(block.getId().startsWith("fkSysObjId_"));
    assertNotNull(block.getName());
    assertEquals("name", block.getName());

    node = nodeList.item(2);
    block = Block.createBlockFromNode(node);
    assertNotNull("Unexpected null", block);
    assertNotNull(block.getName());
    assertEquals("objectName", block.getName());
  }
}
