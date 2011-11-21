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

package se.vgregion.kivtools.util.dom;

import java.io.IOException;
import java.io.InputStream;
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
    InputSource inputSource = new InputSource(new StringReader(content));
    return getDocumentFromInputSource(inputSource);
  }

  /**
   * Loads the provided resource from the classpath and parses its content to a W3C DOM-document.
   * 
   * @param resourceName The name of the resource to load and parse.
   * @return A populated DOM Document or an empty document if parsing was unsuccessful.
   */
  public static Document getDocumentFromResource(String resourceName) {
    DocumentBuilder builder = createDocumentBuilder();
    Document document;

    InputStream inputStream = DocumentHelper.class.getClassLoader().getResourceAsStream(resourceName);
    if (inputStream != null) {
      InputSource inputSource = new InputSource(inputStream);
      document = getDocumentFromInputSource(inputSource);
    } else {
      document = builder.newDocument();
    }

    return document;
  }

  /**
   * Parses the provided InputSource to a W3C DOM-document.
   * 
   * @param inputSource The InputSource to parse.
   * @return A populated DOM Document or an empty document if parsing was unsuccessful.
   */
  public static Document getDocumentFromInputSource(final InputSource inputSource) {
    DocumentBuilder builder = createDocumentBuilder();
    Document document;
    try {

      document = builder.parse(inputSource);
      document.normalize();
    } catch (SAXException e) {
      LOGGER.error("Error parsing xml", e);
      document = builder.newDocument();
    } catch (IOException e) {
      LOGGER.error("Error parsing xml", e);
      document = builder.newDocument();
    }

    return document;
  }

  private static DocumentBuilder createDocumentBuilder() {
    DocumentBuilder builder;
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(false);
    factory.setValidating(false);
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("Unable to create a DocumentBuilder", e);
    }
    return builder;
  }
}
