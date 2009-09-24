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
package se.vgregion.kivtools.search.svc.domain.values.accessibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation of an Accessibiliity Package.
 */
public final class AccessibilityPackage implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name = "";
  private final ArrayList<ImageInfo> images = new ArrayList<ImageInfo>();
  private final ArrayList<Criteria> criterias = new ArrayList<Criteria>();
  private String id;

  /**
   * Private constructor to prevent instantiation.
   */
  private AccessibilityPackage() {
  }

  /**
   * Constructs a new AccessibilityPackage based on the provided XML-node.
   * 
   * @param node The XML-node to base the object on.
   * @return An AccessibilityPackage populated from the provided XML-node.
   */
  public static AccessibilityPackage createAccessibilityPackageFromNode(Node node) {
    AccessibilityPackage accessibilityPackage = new AccessibilityPackage();

    // Set id
    String accessibilityPackageId = NodeHelper.getAttributeTextContent(node, "id");
    if (accessibilityPackageId != null) {
      accessibilityPackage.id = accessibilityPackageId + "_" + System.currentTimeMillis();
    }

    // Get criterias
    NodeList accessibilityPackageChildren = node.getChildNodes();
    for (int i = 0; i < accessibilityPackageChildren.getLength(); i++) {
      // If node name is objectName, set name
      if (NodeHelper.isNodeName(accessibilityPackageChildren.item(i), "objectName") || NodeHelper.isNodeName(accessibilityPackageChildren.item(i), "name")) {
        accessibilityPackage.name = accessibilityPackageChildren.item(i).getTextContent();
      }
      if (NodeHelper.isNodeName(accessibilityPackageChildren.item(i), "images")) {
        for (int j = 0; j < accessibilityPackageChildren.item(i).getChildNodes().getLength(); j++) {
          if (NodeHelper.isNodeName(accessibilityPackageChildren.item(i).getChildNodes().item(j), "image")) {
            ImageInfo imageInfo = ImageInfo.createImageInfoFromNode(accessibilityPackageChildren.item(i).getChildNodes().item(j));
            accessibilityPackage.images.add(imageInfo);
          }
        }
      }
      if (NodeHelper.isNodeName(accessibilityPackageChildren.item(i), "criteria")) {
        Criteria criteria = new Criteria(accessibilityPackageChildren.item(i));
        if (!criteria.isHidden()) {
          accessibilityPackage.criterias.add(criteria);
        }
      }
    }
    return accessibilityPackage;
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
   * Getter for the list of criterias this accessibility package contains.
   * 
   * @return The list of criterias this accessibility package contains.
   */
  public List<Criteria> getCriterias() {
    return Collections.unmodifiableList(criterias);
  }

  /**
   * Getter for the list of images this accessibility package contains.
   * 
   * @return The list of images this accessibility package contains.
   */
  public List<ImageInfo> getImages() {
    return Collections.unmodifiableList(images);
  }
}
