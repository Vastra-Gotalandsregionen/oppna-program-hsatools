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

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.steeplesoft.jsf.facestester.FacesPage;
import com.steeplesoft.jsf.facestester.FacesTester;

public class DisplayUnitDetailsFacesTest {
  private static FacesTester facesTester;

  @BeforeClass
  public static void setUp() {
    facesTester = new FacesTester();
  }

  @Test
  @Ignore
  public void testRender() {
    FacesPage page = facesTester.requestPage("/HRIV.Search.searchunit-flow.flow&hsaidentity=SE2321000131-E000000006301");
    assertNotNull(page);
    assertTrue(page.isRendered());

    String renderedPage = page.getRenderedPage();
    System.out.println(renderedPage);

    assertTrue(renderedPage.indexOf("<h1>Akutklinik, <span class=\"municipality-name\">Kungälv</span></h1>") != -1);
  }
}
