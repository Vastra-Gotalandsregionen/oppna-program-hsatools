package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Container for a unit which also hold some meta data about the unit which is used by the InformationPusherEniro-service.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class UnitComposition {

  /**
   * 
   * @author david
   * 
   */
  public enum UnitType {
    CARE_CENTER, OTHER_CARE;
  }

  private Unit eniroUnit = new Unit();
  private String dn;
  private UnitType careType;

  public UnitType getCareType() {
    return careType;
  }

  public void setCareType(UnitType careType) {
    this.careType = careType;
  }

  public Unit getEniroUnit() {
    return eniroUnit;
  }

  public String getDn() {
    return dn;
  }

  public void setDn(String dn) {
    this.dn = dn;
  }

  /**
   * Gets the distinguished name of the units parent.
   * 
   * @return The distinguished name of the units parent.
   */
  public String getParentDn() {
    String value = "";
    if (!StringUtil.isEmpty(dn)) {
      DistinguishedName distinguishedName = new DistinguishedName(dn);
      distinguishedName.removeLast();
      value = distinguishedName.toString();
    }
    return value;
  }
}
