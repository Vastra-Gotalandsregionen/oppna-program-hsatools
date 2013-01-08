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

package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Search form for unit search.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class UnitSearchSimpleForm implements Serializable {
  private static final long serialVersionUID = -6941443014284342343L;
  private String searchType = "simple";
  private String unitName = "";
  private String location = "";
  private String administrationName;
  private String liableCode;
  private String businessClassificationName;
  private String careTypeName;
  private String showAll;

  public String getSearchType() {
    return searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
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

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Checks if the form is empty.
   * 
   * @return true if unitName and searchParamValue is null or empty.
   */
  public boolean isEmpty() {
    boolean isEmpty = true;

    isEmpty &= StringUtil.isEmpty(unitName);
    isEmpty &= StringUtil.isEmpty(location);
    isEmpty &= StringUtil.isEmpty(administrationName);
    isEmpty &= StringUtil.isEmpty(liableCode);
    isEmpty &= StringUtil.isEmpty(businessClassificationName);
    isEmpty &= StringUtil.isEmpty(careTypeName);

    return isEmpty;
  }

  public void setShowAll(String showAll) {
    this.showAll = showAll;
  }

  public String getShowAll() {
    return showAll;
  }
}
