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
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.BeforeClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.steeplesoft.jsf.facestester.FacesPage;
import com.steeplesoft.jsf.facestester.FacesTester;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author argoyle
 */
public class FacesTesterBase {
  private static DocumentBuilderFactory documentBuilderFactory;
  private static DocumentBuilder documentBuilder;
  private static XPath xPath;
  private static Map<String, Object> sessionMap;

  protected static FacesTester facesTester;

  @BeforeClass
  public static void baseSetup() throws ParserConfigurationException {
    documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setValidating(false);
    // documentBuilderFactory.setExpandEntityReferences(false);
    documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
    documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
    documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    documentBuilder = documentBuilderFactory.newDocumentBuilder();

    xPath = XPathFactory.newInstance().newXPath();

    String pathToWebXml = new File(".").getAbsoluteFile().getParentFile().getAbsolutePath() + "/src/test/webapp";
    System.setProperty("facestester.webAppPath", pathToWebXml);
    facesTester = new FacesTester();
    sessionMap = facesTester.getFacesContext().getExternalContext().getSessionMap();
  }

  protected void addBean(String name, Object bean) {
    FacesTesterBase.sessionMap.put(name, bean);
  }

  protected Document renderPage(String pageName) {
    FacesPage page = facesTester.requestPage(pageName);
    assertNotNull(page);
    assertTrue(page.isRendered());

    String renderedPage = page.getRenderedPage();

    return this.getDocumentFromString(renderedPage);
  }

  private Document getDocumentFromString(String string) {
    ByteArrayInputStream inputStream;
		inputStream = new ByteArrayInputStream(StringUtil.getBytes(string, "UTF-8"));
    Document document;
    try {
      document = documentBuilder.parse(inputStream, null);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return document;
  }

  protected NodeList getNodesByExpression(Document document, String expression) {
    try {
      return (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  protected Node getNodeByExpression(Document document, String expression) {
    try {
      return (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  protected String getNodeContent(Node node) {
    return serializeDoc(node);
  }

  private String serializeDoc(Node doc) {
    StringWriter outText = new StringWriter();
    StreamResult sr = new StreamResult(outText);
    Properties oprops = new Properties();
    oprops.put(OutputKeys.METHOD, "xhtml");
    oprops.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = null;
    try {
      t = tf.newTransformer();
      t.setOutputProperties(oprops);
      t.transform(new DOMSource(doc), sr);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return outText.toString();
  }
}
