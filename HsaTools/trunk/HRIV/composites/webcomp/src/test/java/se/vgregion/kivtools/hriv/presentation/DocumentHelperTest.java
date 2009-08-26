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
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;

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
    assertNull(document);
  }
}
