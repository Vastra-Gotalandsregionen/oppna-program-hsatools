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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.lth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;

public class EniroOrganisationBuilderLTHTest {

  private EniroOrganisationBuilderLTH eniroOrganisationBuilder;
  private List<UnitComposition> ldapSearchResult;

  @Before
  public void setup() {
    this.eniroOrganisationBuilder = new EniroOrganisationBuilderLTH("Vårdcentraler", "Övrig primärvård");
    this.populateLdapSearchList();
  }

  private void populateLdapSearchList() {
    this.ldapSearchResult = new ArrayList<UnitComposition>();
    this.ldapSearchResult.add(this.createUnit("subUnit1", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit1,ou=Unit2,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit2", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit2,ou=Unit2,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit3", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit3,ou=Unit2,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit4", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit4,ou=Unit3,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit5", UnitComposition.UnitType.OTHER_CARE, "ou=subUnit5,ou=Unit3,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit6", UnitComposition.UnitType.OTHER_CARE, "ou=subUnit6,ou=Folktandvården,ou=Primärvård x"));
  }

  private UnitComposition createUnit(String unitId, UnitComposition.UnitType careType, String dn) {
    UnitComposition unit = new UnitComposition();
    unit.setDn(dn);
    unit.setCareType(careType);
    unit.getEniroUnit().setId(unitId);
    return unit;
  }

  @Test
  public void testBuildOrganisation() {
    Organization organisation = this.eniroOrganisationBuilder.generateOrganisation(this.ldapSearchResult, "Halland");
    // Should contain root1 unit and markerUnit1.
    assertEquals(1, organisation.getUnit().size());
    this.assertUnits(organisation.getUnit(), "Region_Halland_Toppniva");
    assertTrue("coordinates", organisation.isSwapCoordinates());
    Unit unit = organisation.getUnit().get(0);
    assertEquals("parent unit id", "24386", unit.getParentUnitId());
    assertEquals("", "create", unit.getOperation());
    assertEquals(2, organisation.getUnit().get(0).getUnit().size());
    List<Unit> subUnits = new ArrayList<Unit>();
    subUnits.addAll(organisation.getUnit().get(0).getUnit().get(0).getUnit());
    subUnits.addAll(organisation.getUnit().get(0).getUnit().get(1).getUnit());
    this.assertUnits(subUnits, "Unit2", "Unit3", "subUnit5", "subUnit6");
  }

  private void assertUnits(List<Unit> units, String... expectedUnitIds) {
    assertEquals("Unexpected number of units found", expectedUnitIds.length, units.size());

    for (Unit unit : units) {
      boolean found = false;
      for (String id : expectedUnitIds) {
        found |= id.equals(unit.getId());
      }
      assertTrue("Did not expect a unit with id '" + unit.getId() + "'", found);
    }
  }
}
