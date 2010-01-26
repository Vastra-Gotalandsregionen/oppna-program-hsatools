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
package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class EniroUnitMapperTest {

  private EniroUnitMapper eniroUnitMapper;
  private DirContextOperationsMock dirContextOperationsMock;
  private String ldapAddressValue = "Smörslottsgatan 1$416 85 Göteborg$Centralkliniken, plan 2$Östra sjukhuset";

  @Before
  public void setup() {
    eniroUnitMapper = new EniroUnitMapper(Arrays.asList("1"));
    dirContextOperationsMock = new DirContextOperationsMock();
    setAttributeMocks();
  }

  @Test
  public void testMapFromContext() {
    dirContextOperationsMock.addAttributeValue("hsaPublicTelephoneNumber", "+46 31 3450700");
    dirContextOperationsMock.addAttributeValue("hsaTelephoneTime", "1-5#08:00#18:00");
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    assertEquals("id1", unitComposition.getEniroUnit().getId());
    assertEquals("name", unitComposition.getEniroUnit().getName());
    BusinessClassification businessClassification = null;
    for (Object info : unitComposition.getEniroUnit().getTextOrImageOrAddress()) {
      // Make sure that no address is created.
      assertFalse(info instanceof Address);

      if (info instanceof BusinessClassification) {
        businessClassification = (BusinessClassification) info;
      } else if (info instanceof TelephoneType) {
        TelephoneType telephoneType = (TelephoneType) info;
        assertEquals("+46 31 3450700", telephoneType.getTelephoneNumber().get(0));
      }
    }
    assertEquals("1", businessClassification.getBCCode());
    assertEquals("", businessClassification.getBCName());
  }

  @Test
  public void testMapCn() {
    // Test function unit with cn instead of ou.
    dirContextOperationsMock.setAttributeValue("ou", null);
    dirContextOperationsMock.addAttributeValue("cn", "name");
    dirContextOperationsMock.addAttributeValue("hsaStreetAddress", ldapAddressValue);
    dirContextOperationsMock.addAttributeValue("hsaGeographicalCoordinates", "X: 6414080, Y: 1276736");
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
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
    dirContextOperationsMock = new DirContextOperationsMock();
    dirContextOperationsMock.setDn(new NameMock("dn"));
    UnitComposition unitComposition = (UnitComposition) eniroUnitMapper.mapFromContext(dirContextOperationsMock);
    assertNotNull(unitComposition);
    assertEquals(1, unitComposition.getEniroUnit().getTextOrImageOrAddress().size());

  }

  private void setAttributeMocks() {
    dirContextOperationsMock.addAttributeValue("createTimeStamp", "20090118094127Z");
    dirContextOperationsMock.addAttributeValue("vgrModifyTimestamp", "20090318094127Z");
    dirContextOperationsMock.setDn(new NameMock("dn"));
    dirContextOperationsMock.addAttributeValue("hsaIdentity", "id1");
    dirContextOperationsMock.addAttributeValue("ou", "name");
    dirContextOperationsMock.addAttributeValue("hsaSurgeryHours", "1#08:00#19:00$2-5#08:00#17:00");
    dirContextOperationsMock.addAttributeValue("hsaBusinessClassificationCode", "1");
    dirContextOperationsMock.addAttributeValue("l", "locality");
    dirContextOperationsMock.addAttributeValue("hsaRoute", "Nedför backen$Till höger vid stenen$In under bron");
  }
}
