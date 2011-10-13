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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;

public class EniroOrganisationBuilderVGRTest {

  private EniroOrganisationBuilderVGR eniroOrganisationBuilder;
  private List<UnitComposition> ldapSearchResult;

  @Before
  public void setup() {
    this.eniroOrganisationBuilder = new EniroOrganisationBuilderVGR();
    this.eniroOrganisationBuilder.setRootUnits(Arrays.asList("ou=root1"));
    this.eniroOrganisationBuilder.setCareCenter("Vårdcentraler");
    this.eniroOrganisationBuilder.setOtherCare("Övrig primärvård");
    this.populateLdapSearchList();
  }

  private void populateLdapSearchList() {
    this.ldapSearchResult = new ArrayList<UnitComposition>();
    this.ldapSearchResult.add(this.createUnit("root1", UnitComposition.UnitType.CARE_CENTER, "ou=root1"));
    this.ldapSearchResult.add(this.createUnit("rootLeaf", UnitComposition.UnitType.CARE_CENTER, "ou=rootLeaf,ou=root1"));
    this.ldapSearchResult.add(this.createUnit("subUnit1", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit1,ou=PVO Unit2,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit2", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit2,ou=PVO Unit2,ou=Primärvård x"));
    this.ldapSearchResult.add(this.createUnit("subUnit3", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit3,ou=PVO Unit2,ou=Primärvård x"));
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
    Organization organisation = this.eniroOrganisationBuilder.generateOrganisation(this.ldapSearchResult, "Borås");
    assertEquals("External", organisation.getLoadType());
    assertEquals("County Council", organisation.getType());
    // Should contain root1 unit and markerUnit1.
    assertEquals(3, organisation.getUnit().size());
    assertEquals(2, organisation.getUnit().get(0).getUnit().size());
    this.assertUnits(organisation.getUnit(), "Vardcentraler", "Ovrig_primarvard", "root1");
    List<Unit> subUnits = new ArrayList<Unit>();
    subUnits.addAll(organisation.getUnit().get(0).getUnit());
    subUnits.addAll(organisation.getUnit().get(1).getUnit());
    subUnits.addAll(organisation.getUnit().get(2).getUnit());
    this.assertUnits(subUnits, "Unit2", "Unit3", "subUnit5", "subUnit6", "rootLeaf");
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
