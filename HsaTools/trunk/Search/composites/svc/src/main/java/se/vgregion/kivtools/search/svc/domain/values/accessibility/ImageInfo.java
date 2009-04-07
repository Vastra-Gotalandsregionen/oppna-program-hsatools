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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	String url;
	String urlLarge;

	String shortDescription, longDescription;
	
	public String getUrlLarge() {
		return urlLarge;
	}
	public void setUrlLarge(String urlLarge) {
		this.urlLarge = urlLarge;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	
	public ImageInfo(Node imageInfo) {
		NodeList imageChildren = imageInfo.getChildNodes();
		// Loop through child nodes of image element
		for (int i = 0; i < imageChildren.getLength(); i++) {
			// Set url
			if ("URL".equals(imageChildren.item(i).getNodeName())) {
				url = imageChildren.item(i).getTextContent();
				if (url.indexOf("small") >= 0) {
					urlLarge = url.replaceAll("small", "large");
				}
			}
			// Set short and long description
			if ("ShortValue".equals(imageChildren.item(i).getNodeName())) {
				shortDescription = imageChildren.item(i).getTextContent();
			}
			if ("LongValue".equals(imageChildren.item(i).getNodeName())) {
				longDescription = imageChildren.item(i).getTextContent();
			}
		}
	}
}
