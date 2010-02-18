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

import se.vgregion.kivtools.search.domain.values.accessibility.Block;

public class BlockTest {
  private static final Document DOC_WITH_CRITERIA = XmlHelper.getDocumentFromResource("testxml/doc_with_block.xml");
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

  @Test
  public void testHasVisiblePackages() {
    Node node = nodeList.item(3);
    block = Block.createBlockFromNode(node);
    assertEquals(2, block.getPackages().size());
    assertFalse(block.hasVisiblePackages());

    block.getPackages().get(0).getCriterias().get(0).setShow(true);
    assertTrue(block.hasVisiblePackages());
  }
}
