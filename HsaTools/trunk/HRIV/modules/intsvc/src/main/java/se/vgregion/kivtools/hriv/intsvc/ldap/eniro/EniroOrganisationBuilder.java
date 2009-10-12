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
 * @author David Bennehutl & Joakim Olsson
 * 
 */
public class EniroOrganisationBuilder {

  private List<String> rootUnitDns;
  private String careCenter;
  private String otherCare;

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
   * {@inheritDoc}
   */
  public Organization generateOrganisation(List<UnitComposition> unitCompositions) {
    Map<String, List<Unit>> subunits = new HashMap<String, List<Unit>>();
    Map<String, List<Unit>> rootUnitsChildren = new HashMap<String, List<Unit>>();
    HashMap<String, Unit> rootunits = new HashMap<String, Unit>();

    Organization organization = new Organization();
    organization.setLoadType("Full");
    organization.setType("Municipality");
    // Create care center unit.
    Unit careCenterUnit = new Unit();
    careCenterUnit.setName(careCenter);
    organization.getUnit().add(careCenterUnit);
    // Create other care unit.
    Unit otherCareUnit = new Unit();
    otherCareUnit.setName(otherCare);
    organization.getUnit().add(otherCareUnit);

    for (UnitComposition unitComposition : unitCompositions) {
      String unitParentDn = unitComposition.getParentDn();

      if (rootUnitDns.contains(unitParentDn)) {
        // Current unit is a leaf to a root unit
        List<Unit> childrenUnits = getSubunitsList(rootUnitsChildren, unitComposition.getParentDn());
        childrenUnits.add(unitComposition.getEniroUnit());
      } else if (rootUnitDns.contains(unitComposition.getDn()) || StringUtil.isEmpty(unitParentDn)) {
        // Current unit is a root unit.
        rootunits.put(unitComposition.getDn(), unitComposition.getEniroUnit());
      } else {
        String parentOu = getUnitOuString(unitParentDn);
        // Check if unit should be added to careCenterUnit or otherCareUnit.
        if (parentOu.startsWith("ou=PVO ") && unitComposition.getCareType() == UnitType.CARE_CENTER) {
          // Current unit is a leaf to unit of type "careCenter".
          List<Unit> list = getSubunitsList(subunits, parentOu);
          list.add(unitComposition.getEniroUnit());
        } else if (unitComposition.getCareType() == UnitType.OTHER_CARE) {
          // Current unit is a leaf to unit of typ "otherCare".
          otherCareUnit.getUnit().add(unitComposition.getEniroUnit());
        }
        // else {
        // // Current unit couldn't be put under any unit type.
        // System.out.println("Enhet som inte mappas: id=" + unitComposition.getEniroUnit().getId()+ " Dn=" + unitComposition.getDn());
        // }
      }
    }

    // Go through all subUnits and add them to their locality under "careCenter" unit.
    for (Entry<String, List<Unit>> units : subunits.entrySet()) {
      Unit localityUnit = new Unit();
      localityUnit.setName(cleanDnString(units.getKey()));
      localityUnit.getUnit().addAll(units.getValue());
      careCenterUnit.getUnit().add(localityUnit);
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

  private String getUnitOuString(String unitDn) {
    String parentOu = "";
    if (!StringUtil.isEmpty(unitDn)) {
      parentOu = new DistinguishedName(unitDn).removeLast().toString();
    }
    return parentOu;
  }

  private String cleanDnString(String dn) {
    String cleanedDn = dn.replace("ou=PVO ", "");
    return cleanedDn;
  }

  /**
   * 
   * @param subunits map container to get units from.
   * @param parentOu the key to get list of units from the container.
   * @return list of units. If no units has been added then empty list is return.
   */
  private List<Unit> getSubunitsList(Map<String, List<Unit>> subunits, String parentOu) {
    List<Unit> list = subunits.get(parentOu);
    if (list == null) {
      list = new ArrayList<Unit>();
      subunits.put(parentOu, list);
    }
    return list;
  }

}
