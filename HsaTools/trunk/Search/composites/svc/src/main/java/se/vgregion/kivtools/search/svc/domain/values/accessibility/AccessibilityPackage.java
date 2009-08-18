/**
 * Copyright 2009 Västa Götalandsregionen
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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AccessibilityPackage implements Serializable {
  private static final long serialVersionUID = 1L;
  private String name = "";
  private ArrayList<ImageInfo> images = new ArrayList<ImageInfo>();
  private ArrayList<Criteria> criterias = new ArrayList<Criteria>();
  private String id;

  public AccessibilityPackage(Node accessibilityPackage) {
    // Set id
    NamedNodeMap attributes = accessibilityPackage.getAttributes();
    if (attributes != null && attributes.getNamedItem("id") != null) {
      id = attributes.getNamedItem("id").getTextContent() + "_" + System.currentTimeMillis();
    }

    // Get criterias
    NodeList accessibilityPackageChildren = accessibilityPackage.getChildNodes();
    for (int i = 0; i < accessibilityPackageChildren.getLength(); i++) {
      // If node name is objectName, set name
      if (accessibilityPackageChildren.item(i).getNodeName().equals("objectName") || accessibilityPackageChildren.item(i).getNodeName().equals("name")) {
        name = accessibilityPackageChildren.item(i).getTextContent();
      }
      if ("images".equals(accessibilityPackageChildren.item(i).getNodeName())) {
        for (int j = 0; j < accessibilityPackageChildren.item(i).getChildNodes().getLength(); j++) {
          if ("image".equals(accessibilityPackageChildren.item(i).getChildNodes().item(j).getNodeName())) {
            ImageInfo imageInfo = ImageInfo.createImageInfoFromNode(accessibilityPackageChildren.item(i).getChildNodes().item(j));
            images.add(imageInfo);
          }
        }
      }
      if (accessibilityPackageChildren.item(i).getNodeName().equals("criteria")) {
        Criteria criteria = new Criteria(accessibilityPackageChildren.item(i));
        if (!criteria.isHidden()) {
          criterias.add(criteria);
        }
      }
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Criteria> getCriterias() {
    return criterias;
  }

  public void setCriterias(ArrayList<Criteria> criterias) {
    this.criterias = criterias;
  }

  public ArrayList<ImageInfo> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageInfo> images) {
    this.images = images;
  }
}
