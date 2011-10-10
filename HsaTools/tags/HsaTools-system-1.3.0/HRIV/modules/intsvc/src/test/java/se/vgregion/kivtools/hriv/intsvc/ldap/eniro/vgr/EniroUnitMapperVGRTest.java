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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro.vgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.EniroUnitMapperVGR;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;

import com.domainlanguage.time.TimePoint;

public class EniroUnitMapperVGRTest {

  private EniroUnitMapperVGR eniroUnitMapper;
  private Unit unit;
  private final se.vgregion.kivtools.search.domain.values.Address address = AddressHelper.convertToStreetAddress(Arrays.asList("Smörslottsgatan 1", "416 85 Göteborg", "Centralkliniken, plan 2",
      "Östra sjukhuset"));

  @Before
  public void setup() {
    this.eniroUnitMapper = new EniroUnitMapperVGR("Uddevalla", Arrays.asList("1"));
    this.unit = new Unit();
    this.setUnitAttributes();
  }

  @Test
  public void testMap() {
    UnitComposition unitComposition = this.eniroUnitMapper.map(this.unit);
    assertNotNull(unitComposition);
    assertEquals("id1", unitComposition.getEniroUnit().getId());
    assertEquals("name", unitComposition.getEniroUnit().getName());
    BusinessClassification businessClassification = null;
    for (Object info : unitComposition.getEniroUnit().getTextOrImageOrAddress()) {
      // Make sure that no address is created.
      assertFalse(info instanceof Address);

      if (info instanceof BusinessClassification) {
        businessClassification = (BusinessClassification) info;
      }
    }
    assertEquals("1", businessClassification.getBCCode());
    assertEquals("", businessClassification.getBCName());
  }

  @Test
  public void testMapStreetAddress() {
    // // Test function unit with cn instead of ou.
    this.unit.setHsaStreetAddress(this.address);
    this.unit.setHsaGeographicalCoordinates("X: 6414080, Y: 1276736");

    UnitComposition unitComposition = this.eniroUnitMapper.map(this.unit);

    assertNotNull(unitComposition);
    Address address = (Address) unitComposition.getEniroUnit().getTextOrImageOrAddress().get(0);
    assertEquals("Smörslottsgatan", address.getStreetName());
    assertEquals("1", address.getStreetNumber());
    assertEquals("416 85", address.getPostCode());
    assertEquals("Göteborg", address.getCity());
    assertEquals("Nedför backen, Till höger vid stenen, In under bron", unitComposition.getEniroUnit().getRoute());
    assertEquals("visit", address.getType());
    assertEquals(2, address.getHours().size());
  }

  @Test
  public void testMapNullValues() {
    // // Test null values
    Unit emptyUnit = new Unit();
    emptyUnit.setDn(DN.createDNFromString("ou=a,ou=b"));

    UnitComposition unitComposition = this.eniroUnitMapper.map(emptyUnit);

    assertNotNull(unitComposition);
    assertEquals(0, unitComposition.getEniroUnit().getTextOrImageOrAddress().size());
  }

  @Test
  public void telephoneNumbersAreFormattedRatherThanHsaStandard() throws Exception {
    this.unit.addHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber("+46313450700"));
    this.unit.addHsaTelephoneTime(new WeekdayTime("1-5#08:00#18:00"));

    UnitComposition unitComposition = this.eniroUnitMapper.map(this.unit);

    for (Object info : unitComposition.getEniroUnit().getTextOrImageOrAddress()) {
      // Make sure that no address is created.
      assertFalse(info instanceof Address);

      if (info instanceof TelephoneType) {
        TelephoneType telephoneType = (TelephoneType) info;
        assertEquals("031 - 345 07 00", telephoneType.getTelephoneNumber());
      }
    }
  }

  @Test
  public void dnIsEscapedSoParentDnIsPossibleToRetrieve() {
    this.unit.setDn(DN.createDNFromString("cn=Jourcentral\\, Kungsportsakuten Kungsportsläkarna\\, Läkarhuset +7,ou=Läkarhuset +7\\, Vårdcentralen,ou=Privata vårdgivare,ou=Org,o=VGR").escape());

    UnitComposition unitComposition = this.eniroUnitMapper.map(this.unit);

    assertEquals("ou=Läkarhuset \\+7\\, Vårdcentralen,ou=Privata vårdgivare,ou=Org,o=VGR", unitComposition.getParentDn());
  }

  @Test
  public void midnightIsMappedAs235959InsteadOf240000() throws Exception {
    this.unit.setHsaStreetAddress(this.address);
    this.unit.getHsaSurgeryHours().clear();
    this.unit.addHsaSurgeryHours(new WeekdayTime("1-1#00:00#24:00"));

    UnitComposition unitComposition = this.eniroUnitMapper.map(this.unit);

    Address address = (Address) unitComposition.getEniroUnit().getTextOrImageOrAddress().get(0);
    assertEquals("2000-01-20T00:00:00Z", address.getHours().get(0).getTimeFrom().toXMLFormat());
    assertEquals("2000-01-20T23:59:59Z", address.getHours().get(0).getTimeTo().toXMLFormat());
  }

  private void setUnitAttributes() {
    this.unit.setCreateTimestamp(TimePoint.parseFrom("20090118094127", "yyyyMMddHHmmss", TimeZone.getDefault()));
    this.unit.setModifyTimestamp(TimePoint.parseFrom("20090318094127", "yyyyMMddHHmmss", TimeZone.getDefault()));
    this.unit.setDn(DN.createDNFromString("ou=dn,ou=cn"));
    this.unit.setHsaIdentity("id1");
    this.unit.setName("name");
    this.unit.addHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(Arrays.asList("1-1#08:00#19:00", "2-5#08:00#17:00")));
    this.unit.setHsaBusinessClassificationCode(Arrays.asList("1"));
    this.unit.setLocality("locality");
    this.unit.addHsaRoute(Arrays.asList("Nedför backen", "Till höger vid stenen", "In under bron"));
  }
}
