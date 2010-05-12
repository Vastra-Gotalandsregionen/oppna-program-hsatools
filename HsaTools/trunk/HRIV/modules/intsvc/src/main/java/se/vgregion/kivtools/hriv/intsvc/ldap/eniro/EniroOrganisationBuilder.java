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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class EniroOrganisationBuilder {
  private List<String> rootUnitDns;
  private String careCenter;
  private String otherCare;
  private Map<String, Unit> otherCareUnits = new HashMap<String, Unit>();
  private Map<String, Unit> careCenterUnits = new HashMap<String, Unit>();

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
  public Organization generateOrganisation(List<UnitComposition> unitCompositions) {
    Map<String, Map<String, List<Unit>>> subunits = new HashMap<String, Map<String, List<Unit>>>();
    Map<String, Map<String, List<Unit>>> rootUnitsChildren = new HashMap<String, Map<String, List<Unit>>>();
    HashMap<String, Unit> rootunits = new HashMap<String, Unit>();

    Organization organization = new Organization();
    organization.setLoadType("Full");
    organization.setType("Municipality");
    organization.setCountry("SE");

    for (UnitComposition unitComposition : unitCompositions) {
      String unitParentDn = unitComposition.getParentDn();

      if (rootUnitDns.contains(unitParentDn)) {
        // Current unit is a leaf to a root unit
        List<Unit> childrenUnits = getSubunitsList(rootUnitsChildren, unitComposition.getEniroUnit().getLocality(), unitComposition.getParentDn());
        childrenUnits.add(unitComposition.getEniroUnit());
      } else if (rootUnitDns.contains(unitComposition.getDn()) || StringUtil.isEmpty(unitParentDn)) {
        // Current unit is a root unit.
        rootunits.put(unitComposition.getDn(), unitComposition.getEniroUnit());
      } else {
        String parentOu = getUnitOuString(unitParentDn);
        // Check if unit should be added to careCenterUnit or otherCareUnit.
        if (unitComposition.getCareType() == UnitType.CARE_CENTER) {
          // Current unit is a leaf to unit of type "careCenter".
          List<Unit> list = getSubunitsList(subunits, unitComposition.getEniroUnit().getLocality(), parentOu);
          list.add(unitComposition.getEniroUnit());
        } else if (unitComposition.getCareType() == UnitType.OTHER_CARE) {
          // Current unit is a leaf to unit of typ "otherCare".
          getOtherCareUnit(unitComposition.getEniroUnit().getLocality(), organization).getUnit().add(unitComposition.getEniroUnit());
        }
        // else {
        // // Current unit couldn't be put under any unit type.
        // System.out.println("Enhet som inte mappas: id=" + unitComposition.getEniroUnit().getId()+ " Dn=" + unitComposition.getDn());
        // }
      }
    }

    // Go through all subUnits and add them to their locality under "careCenter" unit.
    for (Entry<String, Map<String, List<Unit>>> localityUnits : subunits.entrySet()) {
      for (Entry<String, List<Unit>> units : localityUnits.getValue().entrySet()) {
        Unit localityUnit = new Unit();
        String cleanedDnString = cleanDnString(units.getKey());
        localityUnit.setName(cleanedDnString);
        localityUnit.setId(cleanedDnString);
        localityUnit.setLocality(units.getValue().get(0).getLocality());
        localityUnit.getUnit().addAll(units.getValue());
        getCareCenterUnit(units.getValue().get(0).getLocality(), organization).getUnit().add(localityUnit);
      }
    }

    // Go through all root children units and add them to their root unit.
    for (Entry<String, Map<String, List<Unit>>> localityRootUnits : rootUnitsChildren.entrySet()) {
      for (Entry<String, List<Unit>> childunits : localityRootUnits.getValue().entrySet()) {
        Unit rootUnit = rootunits.get(childunits.getKey());
        rootUnit.getUnit().addAll(childunits.getValue());
        organization.getUnit().add(rootUnit);
        // Remove rootUnit from map.
        rootunits.remove(childunits.getKey());
      }
    }
    // Add all remaining root units to organisation.
    organization.getUnit().addAll(rootunits.values());
    return organization;
  }

  private Unit getOtherCareUnit(String locality, Organization organization) {
    Unit otherCareUnit = this.otherCareUnits.get(locality);
    if (otherCareUnit == null) {
      otherCareUnit = new Unit();
      otherCareUnit.setName(otherCare);
      otherCareUnit.setId(replaceSpecialCharacters(otherCare));
      otherCareUnit.setLocality(locality);
      this.otherCareUnits.put(locality, otherCareUnit);
      organization.getUnit().add(otherCareUnit);
    }

    return otherCareUnit;
  }

  private Unit getCareCenterUnit(String locality, Organization organization) {
    Unit careCenterUnit = this.careCenterUnits.get(locality);
    if (careCenterUnit == null) {
      careCenterUnit = new Unit();
      careCenterUnit.setName(careCenter);
      careCenterUnit.setId(replaceSpecialCharacters(careCenter));
      careCenterUnit.setLocality(locality);
      this.careCenterUnits.put(locality, careCenterUnit);
      organization.getUnit().add(careCenterUnit);
    }

    return careCenterUnit;
  }

  private String getUnitOuString(String unitDn) {
    String parentOu = "";
    if (!StringUtil.isEmpty(unitDn)) {
      parentOu = new DistinguishedName(unitDn).removeLast().toString();
    }
    return parentOu;
  }

  private String cleanDnString(String dn) {
    String cleanedDn = dn.replace("ou=PVO ", "");
    cleanedDn = cleanedDn.replace("ou=", "");
    return cleanedDn;
  }

  /**
   * 
   * @param subunits map container to get units from.
   * @param parentOu the key to get list of units from the container.
   * @return list of units. If no units has been added then empty list is return.
   */
  private List<Unit> getSubunitsList(Map<String, Map<String, List<Unit>>> subunits, String locality, String parentOu) {
    Map<String, List<Unit>> localityUnits = subunits.get(locality);
    if (localityUnits == null) {
      localityUnits = new HashMap<String, List<Unit>>();
      subunits.put(locality, localityUnits);
    }

    List<Unit> list = localityUnits.get(parentOu);
    if (list == null) {
      list = new ArrayList<Unit>();
      localityUnits.put(parentOu, list);
    }
    return list;
  }

  private String replaceSpecialCharacters(String input) {
    String result = input;

    // å
    result = result.replace("\u00E5", "a");

    // ä
    result = result.replace("\u00E4", "a");

    // ö
    result = result.replace("\u00F6", "o");

    // Å
    result = result.replace("\u00C5", "A");

    // Ä
    result = result.replace("\u00C4", "A");

    // Ö
    result = result.replace("\u00D6", "O");

    // Space
    result = result.replace(" ", "_");

    return result;
  }
}
