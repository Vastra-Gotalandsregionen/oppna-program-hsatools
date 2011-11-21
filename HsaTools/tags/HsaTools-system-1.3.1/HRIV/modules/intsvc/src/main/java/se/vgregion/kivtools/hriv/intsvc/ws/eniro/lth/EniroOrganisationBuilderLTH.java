package se.vgregion.kivtools.hriv.intsvc.ws.eniro.lth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.EniroOrganisationBuilder;

public class EniroOrganisationBuilderLTH extends EniroOrganisationBuilder {
  private final String careCenter;
  private final String otherCare;
  private Unit otherCareUnit;
  private Unit careCenterUnit;

  public EniroOrganisationBuilderLTH(final String careCenter, final String otherCare) {
    this.careCenter = careCenter;
    this.otherCare = otherCare;
  }

  @Override
  public Organization generateOrganisation(List<UnitComposition> unitCompositions, String locality) {
    Map<String, List<Unit>> subunits = new HashMap<String, List<Unit>>();

    Organization organization = new Organization();
    organization.setLoadType("Full");
    organization.setType("County Council");
    organization.setCountry("SE");
    organization.setSwapCoordinates(Boolean.TRUE);

    Unit rootUnit = this.createUnit("Region Halland Toppnivå", locality);
    rootUnit.setParentUnitId("24386");
    rootUnit.setOperation("create");
    organization.getUnit().add(rootUnit);

    this.otherCareUnit = this.createUnit(this.otherCare, locality);
    rootUnit.getUnit().add(this.otherCareUnit);
    this.careCenterUnit = this.createUnit(this.careCenter, locality);
    rootUnit.getUnit().add(this.careCenterUnit);

    for (UnitComposition unitComposition : unitCompositions) {
      String unitParentDn = unitComposition.getParentDn();
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

    return organization;
  }

  private String cleanDnString(String dn) {
    return dn.replace("ou=", "");
  }
}
