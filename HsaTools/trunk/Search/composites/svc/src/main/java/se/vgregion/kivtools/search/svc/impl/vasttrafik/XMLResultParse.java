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

import se.vgregion.kivtools.search.domain.values.accessibility.NodeHelper;
import se.vgregion.kivtools.util.dom.DocumentHelper;

public class XMLResultParse {
	/**
	   * Parses the string of a xml to get the stop id 
	   * @param xmlString
	   * @return id a empty String will return if there is no match.
	   */
	  public String getStopId(String xmlString, String address, String city){
	    String stopId = "";
	    Document documentFromString = DocumentHelper.getDocumentFromString(xmlString);
	    documentFromString.normalize();
	    
	    NodeList elementsByTagName = documentFromString.getElementsByTagName("item");
	    
	    for(int i = 0; i<elementsByTagName.getLength(); i++){
	      if(isCityValid(elementsByTagName.item(i), address, city)){
	        stopId = NodeHelper.getAttributeTextContent(elementsByTagName.item(i), "stop_id_with_hash_key");
	        return stopId;
	      }
	    }
	    return stopId;
	  }
	  
	  private boolean isCityValid(Node node, String searchAddress, String searchCity){
	    String city = null;
	    boolean valid = false;
	   
	    NodeList subObjectChildren = node.getChildNodes();
	    for (int i = 0; i < subObjectChildren.getLength(); i++) {
	      if (NodeHelper.isNodeName(subObjectChildren.item(i), "county")) {
	        city = subObjectChildren.item(i).getTextContent();
	      }
	    }
	    
	    if(city!=null && searchCity.trim().equalsIgnoreCase(city.trim())){
	      valid = true;
	    }
	    return valid;
	  }


}
