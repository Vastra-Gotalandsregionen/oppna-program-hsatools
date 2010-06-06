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

package se.vgregion.kivtools.search.svc.impl.vasttrafik;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.util.dom.DocumentHelper;
import se.vgregion.kivtools.util.dom.NodeHelper;

/**
 * Parser for the Västtrafik stoplist result.
 */
public class XMLResultParse {
  /**
   * Parses the string of a xml to get the stop id.
   * 
   * @param xmlString The XML-string to parse.
   * @param streetAddress The street address that was used in the search.
   * @param municipality The municipality the street address should be located in.
   * @return id a empty String will return if there is no match.
   */
  public String getStopId(String xmlString, String streetAddress, String municipality) {
    String stopId = "";
    Document documentFromString = DocumentHelper.getDocumentFromString(xmlString);
    documentFromString.normalize();

    NodeList elementsByTagName = documentFromString.getElementsByTagName("item");

    for (int i = 0; i < elementsByTagName.getLength(); i++) {
      if (isMunicipalityValid(elementsByTagName.item(i), streetAddress, municipality)) {
        stopId = NodeHelper.getAttributeTextContent(elementsByTagName.item(i), "stop_id_with_hash_key");
        return stopId;
      }
    }
    return stopId;
  }

  private boolean isMunicipalityValid(Node node, String searchAddress, String searchMunicipality) {
    String municipality = null;
    boolean valid = false;

    NodeList subObjectChildren = node.getChildNodes();
    for (int i = 0; i < subObjectChildren.getLength(); i++) {
      if (NodeHelper.isNodeName(subObjectChildren.item(i), "county")) {
        municipality = subObjectChildren.item(i).getTextContent();
      }
    }

    if (municipality != null && searchMunicipality.trim().equalsIgnoreCase(municipality.trim())) {
      valid = true;
    }
    return valid;
  }
}
