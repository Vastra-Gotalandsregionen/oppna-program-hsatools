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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class DocumentHelperTest {

  @Test
  public void testInstantiation() {
    DocumentHelper documentHelper = new DocumentHelper();
    assertNotNull(documentHelper);
  }

  @Test
  public void testGetDocumentFromString() {
    String input = "<?xml version=\"1.0\" encoding=\"utf-8\"?><doc><child>content</child></doc>";
    Document document = DocumentHelper.getDocumentFromString(input);
    assertNotNull(document);
    assertEquals(1, document.getChildNodes().getLength());
    assertEquals("content", document.getChildNodes().item(0).getChildNodes().item(0).getChildNodes().item(0).getTextContent());
  }

  @Test
  public void testBrokenXML() {
    String input = "<?xml version=\"1.0\" encoding=\"utf-8\"?><doc><child>content</child>";
    Document document = DocumentHelper.getDocumentFromString(input);
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
  }

  @Test
  public void testEmptyInput() {
    String input = "";
    Document document = DocumentHelper.getDocumentFromString(input);
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
  }

  @Test
  public void getDocumentFromResourceReturnAnEmptyDocumentIfResourceIsNotFound() {
    Document document = DocumentHelper.getDocumentFromResource("testxml/unknown_resource.xml");
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
  }

  @Test
  public void getDocumentFromResourceLoadsProvidedDocumentFromClasspath() {
    Document document = DocumentHelper.getDocumentFromResource("testxml/doc_with_criteria.xml");
    assertNotNull(document);
    assertEquals(2, document.getChildNodes().getLength());
  }

  @Test
  public void getDocumentFromInputSourceReturnEmptyDocumentOnIOException() {
    InputSource inputSource = new InputSource(new ExceptionThrowingInputStream());
    Document document = DocumentHelper.getDocumentFromInputSource(inputSource);
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
  }

  private static class ExceptionThrowingInputStream extends InputStream {
    @Override
    public int read() throws IOException {
      throw new IOException();
    }
  }
}
