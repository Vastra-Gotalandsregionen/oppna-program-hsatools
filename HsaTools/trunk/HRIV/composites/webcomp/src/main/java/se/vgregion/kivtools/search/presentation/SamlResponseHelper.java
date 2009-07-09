package se.vgregion.kivtools.search.presentation;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.search.presentation.types.SigningInformation;

public class SamlResponseHelper {

  public static SigningInformation getSigningInformation(String samlAssertionString) {

    String nationalId = null;
    try {
      Document document = getDocument(samlAssertionString);
      nationalId = getNationalId(document);
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return new SigningInformation(nationalId, samlAssertionString);
  }

  private static String getNationalId(Document document) {
    String nationalId = null;
    NodeList attributes = document.getElementsByTagName("Attribute");
    for (int i = 0; i < attributes.getLength() && nationalId == null; i++) {
      Node node = attributes.item(i);
      NamedNodeMap nodeAttributes = node.getAttributes();
      Node nodeAttributeNode = nodeAttributes.getNamedItem("AttributeName");
      if (nodeAttributeNode != null && "se.persnr".equals(nodeAttributeNode.getNodeValue())) {
        NodeList childNodes = node.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
          if ("AttributeValue".equals(childNodes.item(j).getNodeName())) {
            nationalId = childNodes.item(j).getTextContent();
            break;
          }
        }
      }
    }
    return nationalId;
  }

  private static Document getDocument(String samlAssertionString) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    StringReader stringReader = new StringReader(samlAssertionString);
    InputSource inputSource = new InputSource(stringReader);
    Document document;
    document = documentBuilder.parse(inputSource);
    return document;
  }
}
