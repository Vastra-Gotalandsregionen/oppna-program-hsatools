package se.vgregion.kivtools.hriv.intsvc.ws.eniro.lth;

import java.util.List;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.EniroOrganisationBuilder;

public class EniroOrganisationBuilderLTH extends EniroOrganisationBuilder {
  private final String careCenter;
  private final String otherCare;

  public EniroOrganisationBuilderLTH(final String careCenter, final String otherCare) {
    this.careCenter = careCenter;
    this.otherCare = otherCare;
  }

  @Override
  public Organization generateOrganisation(List<UnitComposition> unitCompositions) {
    Organization organization = new Organization();
    organization.setLoadType("Full");
    organization.setType("County Council");
    organization.setCountry("SE");

    for (UnitComposition unitComposition : unitCompositions) {
      String unitParentDn = unitComposition.getParentDn();
      String parentOu = this.getUnitOuString(unitParentDn);
      // Check if unit should be added to careCenterUnit or otherCareUnit.
      if (unitComposition.getCareType() == UnitType.CARE_CENTER) {
        // Current unit is a leaf to unit of type "careCenter".
        List<Unit> list = this.getSubunitsList(subunits, unitComposition.getEniroUnit().getLocality(), parentOu);
        list.add(unitComposition.getEniroUnit());
      } else if (unitComposition.getCareType() == UnitType.OTHER_CARE) {
        // Current unit is a leaf to unit of typ "otherCare".
        this.getOtherCareUnit(unitComposition.getEniroUnit().getLocality(), organization).getUnit().add(unitComposition.getEniroUnit());
      }
    }

    return organization;
  }
}
