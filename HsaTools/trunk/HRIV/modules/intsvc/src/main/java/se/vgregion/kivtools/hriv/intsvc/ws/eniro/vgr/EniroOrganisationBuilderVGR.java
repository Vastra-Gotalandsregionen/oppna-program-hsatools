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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.EniroOrganisationBuilder;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class EniroOrganisationBuilderVGR extends EniroOrganisationBuilder {
  private List<String> rootUnitDns;
  private String careCenter;
  private String otherCare;
  private Unit otherCareUnit;
  private Unit careCenterUnit;

  public void setCareCenter(String careCenter) {
    this.careCenter = careCenter;
  }

  public void setOtherCare(String otherCare) {
    this.otherCare = otherCare;
  }

  /**
   * List of unit dns that should be roots in organisation.
   * 
   * @param unitDns that should point out root units.
   */
  public void setRootUnits(List<String> unitDns) {
    this.rootUnitDns = unitDns;
  }

  /**
   * Creates an Organization-tree based on the provided list of UnitCompositions.
   * 
   * @param unitCompositions The list of UnitCompositions to base the organization tree on.
   * @return A populated Organization-instance.
   */
  @Override
  public Organization generateOrganisation(List<UnitComposition> unitCompositions, String locality) {
    Map<String, List<Unit>> subunits = new HashMap<String, List<Unit>>();
    Map<String, List<Unit>> rootUnitsChildren = new HashMap<String, List<Unit>>();
    HashMap<String, Unit> rootunits = new HashMap<String, Unit>();

    Organization organization = new Organization();
    organization.setLoadType("Full");
    organization.setType("Municipality");
    organization.setCountry("SE");

    this.otherCareUnit = this.createUnit(this.otherCare, locality);
    organization.getUnit().add(this.otherCareUnit);
    this.careCenterUnit = this.createUnit(this.careCenter, locality);
    organization.getUnit().add(this.careCenterUnit);

    for (UnitComposition unitComposition : unitCompositions) {
      String unitParentDn = unitComposition.getParentDn();

      if (this.rootUnitDns.contains(unitParentDn)) {
        // Current unit is a leaf to a root unit
        List<Unit> childrenUnits = this.getSubunitsList(rootUnitsChildren, unitComposition.getParentDn());
        childrenUnits.add(unitComposition.getEniroUnit());
      } else if (this.rootUnitDns.contains(unitComposition.getDn()) || StringUtil.isEmpty(unitParentDn)) {
        // Current unit is a root unit.
        rootunits.put(unitComposition.getDn(), unitComposition.getEniroUnit());
      } else {
        String parentOu = this.getUnitOuString(unitParentDn);
        // Check if unit should be added to careCenterUnit or otherCareUnit.
        if (unitComposition.getCareType() == UnitType.CARE_CENTER) {
          // Current unit is a leaf to unit of type "careCenter".
          List<Unit> list = this.getSubunitsList(subunits, parentOu);
          list.add(unitComposition.getEniroUnit());
        } else if (unitComposition.getCareType() == UnitType.OTHER_CARE) {
          // Current unit is a leaf to unit of typ "otherCare".
          this.otherCareUnit.getUnit().add(unitComposition.getEniroUnit());
        }
        // else {
        // // Current unit couldn't be put under any unit type.
        // System.out.println("Enhet som inte mappas: id=" + unitComposition.getEniroUnit().getId()+ " Dn=" + unitComposition.getDn());
        // }
      }
    }

    // Go through all subUnits and add them to their locality under "careCenter" unit.
    for (Entry<String, List<Unit>> units : subunits.entrySet()) {
      Unit unit = new Unit();
      String cleanedDnString = this.cleanDnString(units.getKey());
      unit.setName(cleanedDnString);
      unit.setId(cleanedDnString);
      unit.setLocality(units.getValue().get(0).getLocality());
      unit.getUnit().addAll(units.getValue());
      this.careCenterUnit.getUnit().add(unit);
    }

    // Go through all root children units and add them to their root unit.
    for (Entry<String, List<Unit>> childunits : rootUnitsChildren.entrySet()) {
      Unit rootUnit = rootunits.get(childunits.getKey());
      rootUnit.getUnit().addAll(childunits.getValue());
      organization.getUnit().add(rootUnit);
      // Remove rootUnit from map.
      rootunits.remove(childunits.getKey());
    }
    // Add all remaining root units to organisation.
    organization.getUnit().addAll(rootunits.values());
    return organization;
  }

  private String cleanDnString(String dn) {
    String cleanedDn = dn.replace("ou=PVO ", "");
    cleanedDn = cleanedDn.replace("ou=", "");
    return cleanedDn;
  }
}
