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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Helper-class for getting information from XML Node objects.
 * 
 * @author argoyle
 */
public class NodeHelper {
  /**
   * Helper-method to get the text content of a named attribute from a Node.
   * 
   * @param node The Node to get the attribute from.
   * @param attributeName The name of the attribute to get.
   * @return The text content of the named attribute or null if the attribute was not found.
   */
  public static String getAttributeTextContent(Node node, String attributeName) {
    if (node == null) {
      throw new IllegalArgumentException("A node must be supplied");
    }
    if (StringUtil.isEmpty(attributeName)) {
      throw new IllegalArgumentException("An attributeName must be supplied");
    }

    String attribute = null;

    NamedNodeMap attributes = node.getAttributes();
    Node attributeNode = attributes.getNamedItem(attributeName);
    if (attributeNode != null) {
      attribute = attributeNode.getTextContent();
    }
    return attribute;
  }

  /**
   * Checks if the name of the provided node is equal to the provided name.
   * 
   * @param node The node to check.
   * @param name The name to compare to.
   * @return True if the name of the node is equal to the provided name, otherwise false.
   */
  public static boolean isNodeName(Node node, String name) {
    if (node == null) {
      throw new IllegalArgumentException("A node must be supplied");
    }
    if (StringUtil.isEmpty(name)) {
      throw new IllegalArgumentException("A name must be supplied");
    }

    return name.equals(node.getNodeName());
  }
}
