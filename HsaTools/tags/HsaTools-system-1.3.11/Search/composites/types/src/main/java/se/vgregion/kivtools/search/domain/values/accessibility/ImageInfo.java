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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.util.dom.NodeHelper;

/**
 * Representation of an image for an accessibility package.
 */
public final class ImageInfo implements Serializable {

  private static final long serialVersionUID = 1L;
  private String url;
  private String urlLarge;

  private String shortDescription;
  private String longDescription;

  /**
   * Private constructor to prevent instantiation.
   */
  private ImageInfo() {
  }

  /**
   * Constructs a new ImageInfo based on the provided XML-node.
   * 
   * @param node The XML-node to base the object on.
   * @return An ImageInfo populated from the provided XML-node.
   */
  public static ImageInfo createImageInfoFromNode(Node node) {
    ImageInfo imageInfo = new ImageInfo();

    NodeList imageChildren = node.getChildNodes();
    // Loop through child nodes of image element
    for (int i = 0; i < imageChildren.getLength(); i++) {
      // Set url
      if (NodeHelper.isNodeName(imageChildren.item(i), "URL")) {
        imageInfo.url = imageChildren.item(i).getTextContent();
        if (imageInfo.url.indexOf("small") >= 0) {
          imageInfo.urlLarge = imageInfo.url.replaceAll("small", "large");
        }
      }
      // Set short and long description
      if (NodeHelper.isNodeName(imageChildren.item(i), "ShortValue")) {
        imageInfo.shortDescription = imageChildren.item(i).getTextContent();
      }
      if (NodeHelper.isNodeName(imageChildren.item(i), "LongValue")) {
        imageInfo.longDescription = imageChildren.item(i).getTextContent();
      }
    }

    return imageInfo;
  }

  /**
   * Getter for the urlLarge property.
   * 
   * @return The value of the urlLarge property.
   */
  public String getUrlLarge() {
    return urlLarge;
  }

  /**
   * Getter for the url property.
   * 
   * @return The value of the url property.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Getter for the shortDescription property.
   * 
   * @return The value of the shortDescription property.
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Getter for the longDescription property.
   * 
   * @return The value of the longDescription property.
   */
  public String getLongDescription() {
    return longDescription;
  }
}
