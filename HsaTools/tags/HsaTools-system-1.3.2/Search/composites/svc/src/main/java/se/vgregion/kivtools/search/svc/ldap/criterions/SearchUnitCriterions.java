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
