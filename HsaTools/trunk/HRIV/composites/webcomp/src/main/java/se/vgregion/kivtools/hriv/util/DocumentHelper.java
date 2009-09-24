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
package se.vgregion.kivtools.hriv.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Helper-class for parsing a String to a W3C DOM-document.
 * 
 * @author argoyle
 */
public class DocumentHelper {
  private static final Log LOGGER = LogFactory.getLog(DocumentHelper.class);

  /**
   * Parses the provided string to a W3C DOM-document.
   * 
   * @param content The string to parse.
   * @return A populated DOM Document or an empty document if parsing was unsuccessful.
   */
  public static Document getDocumentFromString(String content) {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = null;
    Document doc = null;
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
      InputSource inputSource = new InputSource(new StringReader(content));
      doc = docBuilder.parse(inputSource);
      doc.normalize();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Unable to create a DocumentBuilder", e);
    } catch (SAXException e) {
      LOGGER.error("Error parsing xml", e);
      doc = docBuilder.newDocument();
    } catch (IOException e) {
      LOGGER.error("Error parsing xml", e);
      doc = docBuilder.newDocument();
    }
    return doc;
  }
}
