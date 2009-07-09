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

public class Block implements Serializable {
  private static final long serialVersionUID = 1L;
  private ArrayList<AccessibilityPackage> packages = new ArrayList<AccessibilityPackage>();
  private String id;
  private String name = "";

  public Block(Node block) {
    // Set id
    NamedNodeMap attributes = block.getAttributes();
    if (attributes != null && attributes.getNamedItem("id") != null) {
      id = attributes.getNamedItem("id").getTextContent() + "_" + Math.random();
    }

    if (attributes != null && attributes.getNamedItem("fkSystemObjectId") != null) {
      id = attributes.getNamedItem("fkSystemObjectId").getTextContent() + "_" + Math.random();
    }

    // Get packages
    NodeList blockChildren = block.getChildNodes();
    for (int i = 0; i < blockChildren.getLength(); i++) {
      // If node name is objectName, set name
      if (blockChildren.item(i).getNodeName().equals("objectName") || blockChildren.item(i).getNodeName().equals("name")) {
        name = blockChildren.item(i).getTextContent();
      }
      if (blockChildren.item(i).getNodeName().equals("package")) {
        AccessibilityPackage accessibilityPackage = new AccessibilityPackage(blockChildren.item(i));
        packages.add(accessibilityPackage);
      }
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ArrayList<AccessibilityPackage> getPackages() {
    return packages;
  }

  public void setPackages(ArrayList<AccessibilityPackage> packages) {
    this.packages = packages;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
