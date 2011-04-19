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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.EniroOrganisationBuilderVGR;

public class EniroOrganisationBuilderVGRTest {

  private EniroOrganisationBuilderVGR eniroOrganisationBuilder;
  private List<UnitComposition> ldapSearchResult;

  @Before
  public void setup() {
    eniroOrganisationBuilder = new EniroOrganisationBuilderVGR();
    eniroOrganisationBuilder.setRootUnits(Arrays.asList("ou=root1"));
    eniroOrganisationBuilder.setCareCenter("Vårdcentraler");
    eniroOrganisationBuilder.setOtherCare("Övrig primärvård");
    populateLdapSearchList();
  }

  private void populateLdapSearchList() {
    ldapSearchResult = new ArrayList<UnitComposition>();
    ldapSearchResult.add(createUnit("root1", UnitComposition.UnitType.CARE_CENTER, "ou=root1"));
    ldapSearchResult.add(createUnit("rootLeaf", UnitComposition.UnitType.CARE_CENTER, "ou=rootLeaf,ou=root1"));
    ldapSearchResult.add(createUnit("subUnit1", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit1,ou=PVO Unit2,ou=Primärvård x"));
    ldapSearchResult.add(createUnit("subUnit2", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit2,ou=PVO Unit2,ou=Primärvård x"));
    ldapSearchResult.add(createUnit("subUnit3", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit3,ou=PVO Unit2,ou=Primärvård x"));
    ldapSearchResult.add(createUnit("subUnit4", UnitComposition.UnitType.CARE_CENTER, "ou=subUnit4,ou=Unit3,ou=Primärvård x"));
    ldapSearchResult.add(createUnit("subUnit5", UnitComposition.UnitType.OTHER_CARE, "ou=subUnit5,ou=Unit3,ou=Primärvård x"));
    ldapSearchResult.add(createUnit("subUnit6", UnitComposition.UnitType.OTHER_CARE, "ou=subUnit6,ou=Folktandvården,ou=Primärvård x"));
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
    Organization organisation = eniroOrganisationBuilder.generateOrganisation(ldapSearchResult);
    // Should contain root1 unit and markerUnit1.
    assertEquals(3, organisation.getUnit().size());
    assertEquals(2, organisation.getUnit().get(0).getUnit().size());
    assertUnits(organisation.getUnit(), "Vardcentraler", "Ovrig_primarvard", "root1");
    List<Unit> subUnits = new ArrayList<Unit>();
    subUnits.addAll(organisation.getUnit().get(0).getUnit());
    subUnits.addAll(organisation.getUnit().get(1).getUnit());
    subUnits.addAll(organisation.getUnit().get(2).getUnit());
    assertUnits(subUnits, "Unit2", "Unit3", "subUnit5", "subUnit6", "rootLeaf");
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
