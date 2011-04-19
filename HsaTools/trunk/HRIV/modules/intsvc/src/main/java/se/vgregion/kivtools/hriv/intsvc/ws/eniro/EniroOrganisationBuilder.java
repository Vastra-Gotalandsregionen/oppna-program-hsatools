package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.util.List;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.util.StringUtil;

public abstract class EniroOrganisationBuilder {
  public abstract Organization generateOrganisation(List<UnitComposition> unitCompositions);

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
}
