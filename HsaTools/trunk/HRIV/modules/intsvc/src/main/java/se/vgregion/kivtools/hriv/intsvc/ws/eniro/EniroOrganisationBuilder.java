package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.util.StringUtil;

public abstract class EniroOrganisationBuilder {
  public abstract Organization generateOrganisation(List<UnitComposition> unitCompositions, String locality);

  protected String replaceSpecialCharacters(String input) {
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

  protected String getUnitOuString(String unitDn) {
    String parentOu = "";
    if (!StringUtil.isEmpty(unitDn)) {
      parentOu = new DistinguishedName(unitDn).removeLast().toString();
    }
    return parentOu;
  }

  /**
   * 
   * @param subunits map container to get units from.
   * @param parentOu the key to get list of units from the container.
   * @return list of units. If no units has been added then empty list is return.
   */
  protected List<Unit> getSubunitsList(Map<String, List<Unit>> subunits, String parentOu) {
    List<Unit> list = subunits.get(parentOu);
    if (list == null) {
      list = new ArrayList<Unit>();
      subunits.put(parentOu, list);
    }
    return list;
  }

  protected Unit createUnit(String name, String locality) {
    Unit unit = new Unit();
    unit.setName(name);
    unit.setId(this.replaceSpecialCharacters(name));
    unit.setLocality(locality);
    return unit;
  }
}
