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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation of a block of accessibility packages.
 */
public final class Block implements Serializable {
  private static final long serialVersionUID = 1L;
  private final ArrayList<AccessibilityPackage> packages = new ArrayList<AccessibilityPackage>();
  private String id;
  private String name = "";

  /**
   * Private constructor to prevent instantiation.
   */
  private Block() {
  }

  /**
   * Constructs a new Block based on the provided XML-node.
   * 
   * @param node The XML-node to base the object on.
   * @return A Block populated from the provided XML-node.
   */
  public static Block createBlockFromNode(Node node) {
    Block block = new Block();

    // Set id
    String blockId = NodeHelper.getAttributeTextContent(node, "id");
    if (blockId != null) {
      block.id = blockId + "_" + Math.random();
    }

    String fkSystemObjectId = NodeHelper.getAttributeTextContent(node, "fkSystemObjectId");
    if (fkSystemObjectId != null) {
      block.id = fkSystemObjectId + "_" + Math.random();
    }

    // Get packages
    NodeList blockChildren = node.getChildNodes();
    for (int i = 0; i < blockChildren.getLength(); i++) {
      // If node name is objectName, set name
      if (NodeHelper.isNodeName(blockChildren.item(i), "objectName") || NodeHelper.isNodeName(blockChildren.item(i), "name")) {
        block.name = blockChildren.item(i).getTextContent();
      }
      if (NodeHelper.isNodeName(blockChildren.item(i), "package")) {
        AccessibilityPackage accessibilityPackage = AccessibilityPackage.createAccessibilityPackageFromNode(blockChildren.item(i));
        block.packages.add(accessibilityPackage);
      }
    }

    return block;
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
   * Getter for the list of accessibility packages this block contains.
   * 
   * @return The list of accessibility packages this block contains.
   */
  public List<AccessibilityPackage> getPackages() {
    return Collections.unmodifiableList(packages);
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
   * Checks if this Block has at least one visible accessibility package.
   * 
   * @return True if any of the accessibility packages in this Block is visible, otherwise false.
   */
  public boolean hasVisiblePackages() {
    boolean visiblePackages = false;

    for (AccessibilityPackage accessibilityPackage : this.packages) {
      visiblePackages |= accessibilityPackage.hasVisibleCriterias();
    }

    return visiblePackages;
  }
}
