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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;

public class DisplayUnitDetailsFacesTest extends FacesTesterBase {
  private Unit unit;

  @Before
  public void setUp() {
    unit = createUnit();
  }

  @Test
  @Ignore
  public void testRender() {
    this.addBean("unit", unit);
    this.addBean("unitSearchSimpleForm", new UnitSearchSimpleForm());
    Document page = this.renderPage("/displayUnitDetails.xhtml");

    Node summaryHeader = this.getNodeByExpression(page, "//div[@id='print-area']/div/div/h1");
    assertEquals("<h1>Akutklinik, <span class=\"municipality-name\">Angered</span></h1>", this.getNodeContent(summaryHeader));

    // System.out.println(this.getNodeContent(page));
  }

  private Unit createUnit() {
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    unit.setName("Akutklinik");
    unit.setLocality("Angered");
    return unit;
  }
}
