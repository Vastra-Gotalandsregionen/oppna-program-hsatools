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
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representation of a accessibility criteria.
 */
public class Criteria implements Serializable {
  private static final long serialVersionUID = 1L;

  private final ArrayList<String> disabilities = new ArrayList<String>();
  private final ArrayList<String> additionalCriterias = new ArrayList<String>();
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
   * @param criteria A Node describing a Criteria.
   */
  public Criteria(Node criteria) {
    // Set name, status and type
    String criteriaName = NodeHelper.getAttributeTextContent(criteria, "objectName");
    if (criteriaName != null) {
      name = criteriaName + "_" + System.currentTimeMillis();
    }
    String status = NodeHelper.getAttributeTextContent(criteria, "status");
    if ("16".equals(status)) {
      hidden = true;
    }
    String type = NodeHelper.getAttributeTextContent(criteria, "type");
    if ("1".equals(type)) {
      notice = true;
    }

    NodeList criteriaChildren = criteria.getChildNodes();
    // Loop through child nodes of criteria element
    for (int i = 0; i < criteriaChildren.getLength(); i++) {
      // Set disabilities
      if (NodeHelper.isNodeName(criteriaChildren.item(i), "Disabilities")) {
        List<String> nodeDisabilities = Criteria.getDisabilities(criteriaChildren.item(i));
        disabilities.addAll(nodeDisabilities);
      }
      // Add bCriterias
      if (NodeHelper.isNodeName(criteriaChildren.item(i), "bcriteria")) {
        additionalCriterias.add(criteriaChildren.item(i).getTextContent());
      }
      // Set description
      if (NodeHelper.isNodeName(criteriaChildren.item(i), "input")) {
        description = criteriaChildren.item(i).getTextContent();
      }
    }
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
   * Setter for the name property.
   * 
   * @param name The new value of the name property.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for the show property.
   * 
   * @return The value of the show property.
   */
  public boolean getShow() {
    return show;
  }

  /**
   * Setter for the show property.
   * 
   * @param show The new value of the show property.
   */
  public void setShow(boolean show) {
    this.show = show;
  }

  /**
   * Getter for the additionalCriterias property.
   * 
   * @return The value of the additionalCriterias property.
   */
  public ArrayList<String> getAdditionalCriterias() {
    return additionalCriterias;
  }

  /**
   * Checks the notice property.
   * 
   * @return The value of the notice property.
   */
  public boolean isNotice() {
    return notice;
  }

  /**
   * Setter for the notice property.
   * 
   * @param notice The new value of the notice property.
   */
  public void setNotice(boolean notice) {
    this.notice = notice;
  }

  /**
   * Checks the hidden property.
   * 
   * @return The value of the hidden property.
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Setter for the hidden property.
   * 
   * @param hidden The new value of the hidden property.
   */
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * Getter for the disabilities property.
   * 
   * @return The value of the disabilities property.
   */
  public ArrayList<String> getDisabilities() {
    return disabilities;
  }

  /**
   * Getter for the description property.
   * 
   * @return The value of the description property.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Setter for the description property.
   * 
   * @param description The new value of the description property.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Helper-method to get all the disabilities from a Node.
   * 
   * @param node The node to get disabilities from.
   * @return A list of all the disabilities for the provided Node.
   */
  private static List<String> getDisabilities(Node node) {
    List<String> disabilities = new ArrayList<String>();

    NodeList disabilitiesElements = node.getChildNodes();
    for (int j = 0; j < disabilitiesElements.getLength(); j++) {
      Node disableElement = disabilitiesElements.item(j);
      if (disableElement.getNodeType() == Node.ELEMENT_NODE) {
        disabilities.add(disableElement.getNodeName());
      }
    }

    return disabilities;
  }
}
