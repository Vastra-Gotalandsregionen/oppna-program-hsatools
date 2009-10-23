package se.vgregion.kivtools.search.svc.ldap.criterions;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Implements search criterion for searches of persons in ldap.
 * 
 * @author David Bennehult
 * 
 */
public class SearchUnitCriterions {

  private String unitId;
  private String unitName;
  private String administrationName;
  private String liableCode;
  private String businessClassificationName;
  private String careTypeName;
  private String location;

  public String getUnitId() {
    return unitId;
  }

  public void setUnitId(String unitId) {
    this.unitId = unitId;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getAdministrationName() {
    return administrationName;
  }

  public void setAdministrationName(String administrationName) {
    this.administrationName = administrationName;
  }

  public String getLiableCode() {
    return liableCode;
  }

  public void setLiableCode(String liableCode) {
    this.liableCode = liableCode;
  }

  public String getBusinessClassificationName() {
    return businessClassificationName;
  }

  public void setBusinessClassificationName(String businessClassificationName) {
    this.businessClassificationName = businessClassificationName;
  }

  public String getCareTypeName() {
    return careTypeName;
  }

  public void setCareTypeName(String careTypeName) {
    this.careTypeName = careTypeName;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Check if current fields in object is empty.
   * 
   * @return True if all fields is empty or null.
   */
  public boolean isEmpty() {
    boolean isEmpty = true;
    isEmpty &= StringUtil.isEmpty(unitId);
    isEmpty &= StringUtil.isEmpty(unitName);
    isEmpty &= StringUtil.isEmpty(administrationName);
    isEmpty &= StringUtil.isEmpty(liableCode);
    isEmpty &= StringUtil.isEmpty(businessClassificationName);
    isEmpty &= StringUtil.isEmpty(careTypeName);
    isEmpty &= StringUtil.isEmpty(location);
    return isEmpty;
  }
}
