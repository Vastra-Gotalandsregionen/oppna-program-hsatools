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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import se.vgregion.kivtools.mocks.LogFactoryMock;


public class DocumentHelperTest {

  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void setup(){
    logFactoryMock = LogFactoryMock.createInstance();
  }
  
  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }
  
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
    logFactoryMock.getError(true);
    String input = "<?xml version=\"1.0\" encoding=\"utf-8\"?><doc><child>content</child>";
    Document document = DocumentHelper.getDocumentFromString(input);
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
    //assertEquals("Error parsing xml\n", logFactoryMock.getError(true));
  }

  @Test
  public void testEmptyInput() {
    String input = "";
    Document document = DocumentHelper.getDocumentFromString(input);
    assertNotNull(document);
    assertEquals(0, document.getChildNodes().getLength());
    //assertEquals("Error parsing xml\n", logFactoryMock.getError(true));
  }
}
