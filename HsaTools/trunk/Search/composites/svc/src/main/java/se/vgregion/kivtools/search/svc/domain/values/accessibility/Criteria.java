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

public class Criteria implements Serializable {
  private static final long serialVersionUID = 1L;

  private ArrayList<String> disabilities = new ArrayList<String>();
  private ArrayList<String> additionalCriterias = new ArrayList<String>();
  private String description;
  private boolean show;
  private String name = "";
  // If true, display as "Vad bör uppmärksammas". Otherwise, show as "vad är tillgängligt"
  private boolean notice;
  // Not for public display, only for internal use.
  private boolean hidden;

  /**
   * Construct AccessibilityInformation object from a node which may look like below.
   * 
   * <pre>
   * &lt;criteria id=&quot;618&quot; objectName=&quot;021 Lås (HIN)&quot; status=&quot;1&quot; type=&quot;1&quot;&gt;
   *   &lt;Disabilities&gt; 
   *     &lt;move/&gt; 
   *     &lt;information/&gt; 
   *   &lt;/Disabilities&gt;
   *   &lt;input id=&quot;79508&quot;&gt;Det krävs flera handrörelser eller båda händerna för att låsa/låsa upp.&lt;/input&gt;
   * &lt;/criteria&gt;
   * </pre>
   * 
   * @param item
   */
  public Criteria(Node criteria) {
    // Set name, status and type
    NamedNodeMap attributes = criteria.getAttributes();
    if (attributes != null && attributes.getNamedItem("objectName") != null) {
      name = attributes.getNamedItem("objectName").getTextContent() + "_" + System.currentTimeMillis();
    }
    if (attributes != null && attributes.getNamedItem("status") != null) {
      String status = attributes.getNamedItem("status").getTextContent();
      if ("16".equals(status)) {
        hidden = true;
      }
    }
    if (attributes != null && attributes.getNamedItem("type") != null) {
      String type = attributes.getNamedItem("type").getTextContent();
      if ("1".equals(type)) {
        notice = true;
      }
    }

    NodeList criteriaChildren = criteria.getChildNodes();
    // Loop through child nodes of criteria element
    for (int i = 0; i < criteriaChildren.getLength(); i++) {
      // Set disabilities
      if ("Disabilities".equals(criteriaChildren.item(i).getNodeName())) {
        NodeList disabilitiesElements = criteriaChildren.item(i).getChildNodes();
        for (int j = 0; j < disabilitiesElements.getLength(); j++) {
          Node disableElement = disabilitiesElements.item(j);
          if (disableElement.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = disableElement.getNodeName();
            if (nodeName != null) {
              disabilities.add(nodeName);
            }
          }
        }
      }
      // Add bCriterias
      if ("bcriteria".equals(criteriaChildren.item(i).getNodeName())) {
        additionalCriterias.add(criteriaChildren.item(i).getTextContent());
      }
      // Set description
      if ("input".equals(criteriaChildren.item(i).getNodeName())) {
        description = criteriaChildren.item(i).getTextContent();
      }
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getShow() {
    return show;
  }

  public void setShow(boolean show) {
    this.show = show;
  }

  public ArrayList<String> getAdditionalCriterias() {
    return additionalCriterias;
  }

  public void setAdditionalCriterias(ArrayList<String> additionalCriterias) {
    this.additionalCriterias = additionalCriterias;
  }

  public boolean isNotice() {
    return notice;
  }

  public void setNotice(boolean notice) {
    this.notice = notice;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public ArrayList<String> getDisabilities() {
    return disabilities;
  }

  public void setDisabilities(ArrayList<String> disabilities) {
    this.disabilities = disabilities;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
