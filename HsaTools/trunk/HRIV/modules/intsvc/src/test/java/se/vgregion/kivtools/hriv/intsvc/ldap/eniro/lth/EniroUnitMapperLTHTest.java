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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro.lth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.NameMock;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class EniroUnitMapperLTHTest {

  private EniroUnitMapperLTH eniroUnitMapper;
  private DirContextOperationsMock dirContextOperationsMock;
  private final String ldapAddressValue = "Smörslottsgatan 1$416 85 Göteborg$Centralkliniken, plan 2$Östra sjukhuset";

  @Before
  public void setup() {
    this.eniroUnitMapper = new EniroUnitMapperLTH(Arrays.asList("1"));
    this.dirContextOperationsMock = new DirContextOperationsMock();
    this.setAttributeMocks();
  }

  @Test
  public void testMapFromContext() {
    UnitComposition unitComposition = (UnitComposition) this.eniroUnitMapper.mapFromContext(this.dirContextOperationsMock);
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
  public void testMapCn() {
    // Test function unit with cn instead of ou.
    this.dirContextOperationsMock.setAttributeValue("ou", null);
    this.dirContextOperationsMock.addAttributeValue("cn", "name");
    this.dirContextOperationsMock.addAttributeValue("street", this.ldapAddressValue);
    this.dirContextOperationsMock.addAttributeValue("geographicalCoordinates", "X: 6414080, Y: 1276736");
    UnitComposition unitComposition = (UnitComposition) this.eniroUnitMapper.mapFromContext(this.dirContextOperationsMock);
    assertNotNull(unitComposition);
    Address address = (Address) unitComposition.getEniroUnit().getTextOrImageOrAddress().get(0);
    assertEquals("Smörslottsgatan", address.getStreetName());
    assertEquals("1", address.getStreetNumber());
    assertEquals("416 85", address.getPostCode().get(0));
    assertEquals("Göteborg", address.getCity());
    assertEquals("Nedför backen, Till höger vid stenen, In under bron", unitComposition.getEniroUnit().getRoute());
    assertEquals("visit", address.getType());
    assertEquals(2, address.getHours().size());
  }

  @Test
  public void testMapNullValues() {
    // Test null values
    this.dirContextOperationsMock = new DirContextOperationsMock();
    this.dirContextOperationsMock.setDn(new NameMock("dn"));
    UnitComposition unitComposition = (UnitComposition) this.eniroUnitMapper.mapFromContext(this.dirContextOperationsMock);
    assertNotNull(unitComposition);
    assertEquals(1, unitComposition.getEniroUnit().getTextOrImageOrAddress().size());

  }

  @Test
  public void telephoneNumbersAreFormattedRatherThanHsaStandard() {
    this.dirContextOperationsMock.addAttributeValue("lthTelephoneNumber", "+46313450700");
    this.dirContextOperationsMock.addAttributeValue("telephoneHours", "1-5#08:00#18:00");

    UnitComposition unitComposition = (UnitComposition) this.eniroUnitMapper.mapFromContext(this.dirContextOperationsMock);

    for (Object info : unitComposition.getEniroUnit().getTextOrImageOrAddress()) {
      // Make sure that no address is created.
      assertFalse(info instanceof Address);

      if (info instanceof TelephoneType) {
        TelephoneType telephoneType = (TelephoneType) info;
        assertEquals("031 - 345 07 00", telephoneType.getTelephoneNumber().get(0));
      }
    }
  }

  private void setAttributeMocks() {
    this.dirContextOperationsMock.addAttributeValue("createTimeStamp", "20090118094127Z");
    this.dirContextOperationsMock.addAttributeValue("vgrModifyTimestamp", "20090318094127Z");
    this.dirContextOperationsMock.setDn(new NameMock("dn"));
    this.dirContextOperationsMock.addAttributeValue("hsaIdentity", "id1");
    this.dirContextOperationsMock.addAttributeValue("ou", "name");
    this.dirContextOperationsMock.addAttributeValue("surgeryHours", "1#08:00#19:00$2-5#08:00#17:00");
    this.dirContextOperationsMock.addAttributeValue("businessClassificationCode", "1");
    this.dirContextOperationsMock.addAttributeValue("l", "locality");
    this.dirContextOperationsMock.addAttributeValue("route", "Nedför backen$Till höger vid stenen$In under bron");
  }
}
