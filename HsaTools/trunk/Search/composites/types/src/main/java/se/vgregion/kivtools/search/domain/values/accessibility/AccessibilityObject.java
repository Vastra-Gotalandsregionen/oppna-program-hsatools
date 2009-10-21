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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation of an accessibility object.
 */
public final class AccessibilityObject implements Serializable {
  private static final long serialVersionUID = 1L;
  private final ArrayList<Block> blocks = new ArrayList<Block>();
  private String name = "";
  private String id;
  private String updateStamp;

  /**
   * Private constructor to prevent instantiation.
   */
  private AccessibilityObject() {
  }

  /**
   * Constructs a new AccessibilityObject based on the provided XML-node.
   * 
   * @param node The XML-node to base the object on.
   * @return An AccessibilityObject populated from the provided XML-node.
   */
  public static AccessibilityObject createAccessibilityObjectFromNode(Node node) {
    AccessibilityObject accessibilityObject = new AccessibilityObject();

    // Set id
    String accessibilityObjectId = NodeHelper.getAttributeTextContent(node, "id");

    if (accessibilityObjectId != null) {
      accessibilityObject.id = accessibilityObjectId + "_" + System.currentTimeMillis();
    }

    // Get blocks
    NodeList subObjectChildren = node.getChildNodes();
    for (int i = 0; i < subObjectChildren.getLength(); i++) {
      // If node name is objectName or name, set name
      if (NodeHelper.isNodeName(subObjectChildren.item(i), "objectName") || NodeHelper.isNodeName(subObjectChildren.item(i), "name")) {
        accessibilityObject.name = subObjectChildren.item(i).getTextContent();
      }
      if (NodeHelper.isNodeName(subObjectChildren.item(i), "updateStamp")) {
        accessibilityObject.updateStamp = subObjectChildren.item(i).getTextContent();
      }
      if (NodeHelper.isNodeName(subObjectChildren.item(i), "block")) {
        Block block = Block.createBlockFromNode(subObjectChildren.item(i));
        accessibilityObject.blocks.add(block);
      }
    }

    return accessibilityObject;
  }

  /**
   * Getter for the updateStamp property.
   * 
   * @return The value of the updateStamp property.
   */
  public String getUpdateStamp() {
    return updateStamp;
  }

  /**
   * Getter for the id property.
   * 
   * @return The value of the id property.
   */
  public String getId() {
    return id;
  }

  /**
   * Getter for the name property.
   * 
   * @return The value of the name property.
   */
  public String getName() {
    return name;
  }

  /**
   * Getter for the list of blocks of this accessibility object.
   * 
   * @return A list of Block objects.
   */
  public List<Block> getBlocks() {
    return Collections.unmodifiableList(blocks);
  }
}
