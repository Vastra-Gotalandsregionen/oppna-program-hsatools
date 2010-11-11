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

package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.hriv.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.util.presentation.PresentationHelper;

public class DisplayUnitDetailsFacesTest extends FacesTesterBase {
  private Unit unit;
  private Unit emptyUnit;
  private SettingsBean settingsBean;

  @Before
  public void setUp() {
    this.unit = createPopulatedUnit();
    this.emptyUnit = createEmptyUnit();
    settingsBean = new SettingsBean();
  }

  @Test
  @Ignore("Ignored until a version of FacesTester which supports Unified EL is available in the repositories")
  public void testRenderComplete() {
    this.addBean("unit", unit);
    this.addBean("unitSearchSimpleForm", new UnitSearchSimpleForm());
    this.addBean("presentationHelper", new PresentationHelper());
    this.addBean("Search_SettingsContainer", settingsBean);

    settingsBean.setUseMvk("true");
    settingsBean.setFindRouteLinks("http://vard.vgregion.se/sv/Regler-och-rattigheter/Resa-till-och-fran-varden/::Sjukresa till och från vården::");

    Document page = this.renderPage("/displayUnitDetails.xhtml");

    System.out.println(this.getNodeContent(page));

    Node unitDiv = this.getNodeByExpression(page, "//div[@class='vcard']");

    Node summaryHeader = this.getNodeByExpression(unitDiv, "div/h1");
    assertEquals("Akutklinik, <span class=\"municipality-name\">Angered</span>", this.getNodeContent(summaryHeader));

    Node vardval = this.getNodeByExpression(unitDiv, "//div[@class='vardval-participant']");
    assertNotNull(vardval);

    Node summaryInfoTable = this.getNodeByExpression(unitDiv, "table[@class='borderless']");
    assertNotNull(summaryInfoTable);
    NodeList summaryInfoTableRows = this.getNodesByExpression(summaryInfoTable, "tr");
    assertEquals(7, summaryInfoTableRows.getLength());

    assertEquals("Tisdag-Fredag 08:00-12:00", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(0), "td[1]/div")));
    assertEquals("Onsdag-Torsdag 08:00-12:00", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(0), "td[2]/div")));
    assertEquals("ManagementText", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(1), "td[1]")));
    // Note: Swedish characters are not parsed correctly by the FacesTesterBase currently. Thereby the Alla ldrar below.
    assertEquals("Alla ldrar", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(1), "td[2]")));
    assertEquals("Temporary info", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(3), "td[1]")));
    assertEquals("RefInfo", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(5), "td[1]")));
    assertEquals("VisitingRules", this.getNodeContent(this.getNodeByExpression(summaryInfoTableRows.item(6), "td[1]")));

    Node descriptionDiv = this.getNodeByExpression(unitDiv, "div[@id='description-body']");
    assertEquals("Description", this.getNodeContent(this.getNodeByExpression(descriptionDiv, "div[@id='description-body-short']")));

    Node contactTable = this.getNodeByExpression(unitDiv, "//table[@id='contactTable']");
    NodeList contactTableRows = this.getNodesByExpression(contactTable, "tr");
    assertEquals(3, contactTableRows.getLength());

    assertEquals("031 - 12 34 56", this.getNodeContent(this.getNodeByExpression(contactTableRows.item(0), "td[1]/div/span")));
    // Make sure that we have links to MVK and Vardval registration
    assertEquals(2, this.getNodesByExpression(contactTableRows.item(0), "td[2]/p/a").getLength());
    assertEquals("Torsdag-Fredag 08:00-12:00", this.getNodeContent(this.getNodeByExpression(contactTableRows.item(1), "td[1]/div")));
    assertEquals(1, this.getNodesByExpression(contactTableRows.item(2), "td[1]/p/a").getLength());

    Node addressTable = this.getNodeByExpression(unitDiv, "//table[@id='address-table']");
    NodeList addressTableRows = this.getNodesByExpression(addressTable, "tr");
    assertEquals(6, addressTableRows.getLength());

    assertEquals("Storgatan 1", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/div/span[1]")));
    assertEquals("412 63", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/div/span[2]")));
    assertEquals("Angered", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/div/span[3]")));
    assertEquals("Storgatan 1", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/span[1]")));
    assertEquals("412 63", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/span[2]")));
    assertEquals("Angered", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/span[3]")));
    assertEquals("2 trappor", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(0), "td[1]/div[2]")));
    assertEquals("Route", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(1), "td[1]")));
    assertEquals("57.7146", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(2), "td[1]/div/abbr[1]")));
    assertEquals("11.998", this.getNodeContent(this.getNodeByExpression(addressTableRows.item(2), "td[1]/div/abbr[2]")));
    assertEquals(1, this.getNodesByExpression(addressTableRows.item(3), "td[1]/a").getLength());
  }

  @Test
  @Ignore("Ignored until a version of FacesTester which supports Unified EL is available in the repositories")
  public void testRenderEmptyUnit() {
    this.addBean("unit", emptyUnit);
    this.addBean("unitSearchSimpleForm", new UnitSearchSimpleForm());

    Document page = this.renderPage("/displayUnitDetails.xhtml");

    // System.out.println(this.getNodeContent(page));

    Node unitDiv = this.getNodeByExpression(page, "//div[@class='vcard']");

    Node vardval = this.getNodeByExpression(unitDiv, "//div[@class='vardval-participant']");
    assertNull(vardval);

    Node summaryInfoTable = this.getNodeByExpression(unitDiv, "table[@class='borderless']");
    assertNotNull(summaryInfoTable);
    NodeList summaryInfoTableRows = this.getNodesByExpression(summaryInfoTable, "tr");
    assertEquals(0, summaryInfoTableRows.getLength());

    Node descriptionHeader = this.getNodeByExpression(unitDiv, "//div[@id='description-header']");
    assertNull(descriptionHeader);

    Node contactTable = this.getNodeByExpression(unitDiv, "//table[@id='contactTable']");
    NodeList contactTableRows = this.getNodesByExpression(contactTable, "tr");
    assertEquals(0, contactTableRows.getLength());

    Node addressTable = this.getNodeByExpression(unitDiv, "//table[@id='address-table']");
    NodeList addressTableRows = this.getNodesByExpression(addressTable, "tr");
    assertEquals(0, addressTableRows.getLength());
  }

  private Unit createPopulatedUnit() {
    Unit unit = new Unit();

    unit.setHsaIdentity("ABC-123");
    unit.setName("Akutklinik");
    unit.setLocality("Angered");
    unit.setVgrVardVal(true);
    unit.addHealthcareType(new HealthcareType(null, "Vårdcentral", false, null));

    unit.addHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(Arrays.asList("2-5#08:00#12:00")));
    unit.addHsaDropInHours(WeekdayTime.createWeekdayTimeList(Arrays.asList("3-4#08:00#12:00")));

    unit.setHsaManagementText("ManagementText");
    unit.setShowAgeInterval(true);
    unit.setHsaVisitingRuleAge("0-99");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String today = dateFormat.format(new Date());

    unit.setVgrTempInfo(today + "-" + today + " Temporary info");

    unit.setVgrRefInfo("RefInfo");

    unit.setShowVisitingRules(true);
    unit.setHsaVisitingRules("VisitingRules");

    unit.addDescription(Arrays.asList("Description"));

    unit.addHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("031-123456"));
    unit.addMvkCaseType("casetype");

    unit.addHsaTelephoneTimes(WeekdayTime.createWeekdayTimeList(Arrays.asList("4-5#08:00#12:00")));
    unit.setLabeledURI("hittavard.vgregion.se");

    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(Arrays.asList("Storgatan 1", "2 trappor", "412 63 Angered")));
    unit.addHsaRoute(Arrays.asList("Route"));
    unit.setWgs84Lat(57.7146);
    unit.setWgs84Long(11.998);

    return unit;
  }

  private Unit createEmptyUnit() {
    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    return unit;
  }
}
