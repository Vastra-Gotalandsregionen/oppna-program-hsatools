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

public class AccessibilityObject implements Serializable {
  private static final long serialVersionUID = 1L;
  private ArrayList<Block> blocks = new ArrayList<Block>();
  private String name = "";
  private String id;
  private String updateStamp;

  public String getUpdateStamp() {
    return updateStamp;
  }

  public void setUpdateStamp(String updateStamp) {
    this.updateStamp = updateStamp;
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

  public ArrayList<Block> getBlocks() {
    return blocks;
  }

  public void setBlocks(ArrayList<Block> blocks) {
    this.blocks = blocks;
  }

  // Create business object and sub objects
  public AccessibilityObject(Node subObject) {
    // Set id
    NamedNodeMap attributes = subObject.getAttributes();
    if (attributes != null && attributes.getNamedItem("id") != null) {
      id = attributes.getNamedItem("id").getTextContent() + "_" + System.currentTimeMillis();
    }

    // Get blocks
    NodeList subObjectChildren = subObject.getChildNodes();
    for (int i = 0; i < subObjectChildren.getLength(); i++) {
      // If node name is objectName or name, set name
      if ("objectName".equals(subObjectChildren.item(i).getNodeName()) || "name".equals(subObjectChildren.item(i).getNodeName())) {
        name = subObjectChildren.item(i).getTextContent();
      }
      if ("updateStamp".equals(subObjectChildren.item(i).getNodeName())) {
        updateStamp = subObjectChildren.item(i).getTextContent();
      }
      if (subObjectChildren.item(i).getNodeName().equals("block")) {
        Block block = new Block(subObjectChildren.item(i));
        blocks.add(block);
      }
    }
  }

}
