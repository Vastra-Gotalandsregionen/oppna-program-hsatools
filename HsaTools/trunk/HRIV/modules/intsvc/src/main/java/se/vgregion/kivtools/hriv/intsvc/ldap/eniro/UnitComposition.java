package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;

import com.domainlanguage.time.TimePoint;

/**
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class UnitComposition implements Comparable<UnitComposition> {

  private Unit eniroUnit = new Unit();
  private TimePoint createTimePoint;
  private TimePoint modifyTimePoint;
  private String dn;

  public Unit getEniroUnit() {
    return eniroUnit;
  }

  public TimePoint getCreateTimePoint() {
    return createTimePoint;
  }

  public void setCreateTimePoint(TimePoint createTimePoint) {
    this.createTimePoint = createTimePoint;
  }

  public TimePoint getModifyTimePoint() {
    return modifyTimePoint;
  }

  public void setModifyTimePoint(TimePoint modifyTimePoint) {
    this.modifyTimePoint = modifyTimePoint;
  }

  public String getDn() {
    return dn;
  }

  public void setDn(String dn) {
    this.dn = dn;
  }

  public String getParentDn() {
    String value = "";
    if (!"".equals(dn)) {
      DistinguishedName distinguishedName = new DistinguishedName(dn);
      distinguishedName.removeLast();
      value = distinguishedName.toString();
    }
    return value;
  }

  @Override
  public int compareTo(UnitComposition o) {
    return this.eniroUnit.getId().compareTo(o.getEniroUnit().getId());
    // return this.dn.compareTo(o.dn);
  }
}
