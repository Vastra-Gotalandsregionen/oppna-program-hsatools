/**
 * Copyright 2009 Västra Götalandsregionen
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
 */
package se.vgregion.kivtools.hriv.presentation.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.Municipality;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("serial")
public class UnitSearchSimpleForm implements Serializable {
  private String unitName = "";
  private String municipality = "";
  private String healthcareType = "";
  private String resultType = "1";
  private String sortOrder = "UNIT_NAME";
  private SelectItem[] resultTypeItems = new SelectItem[] { new SelectItem("1", "Lista"), new SelectItem("2", "Karta") };

  private SelectItem[] healthcareTypeItems;
  private SelectItem[] municipalityItems;

  {
    List<HealthcareType> healthcareTypeList = new HealthcareTypeConditionHelper().getAllHealthcareTypes();
    List<SelectItem> htcTempList = new ArrayList<SelectItem>();
    for (HealthcareType htc : healthcareTypeList) {
      String value = htc.getIndex().toString();
      htcTempList.add(new SelectItem(value, htc.getDisplayName()));
    }
    healthcareTypeItems = new SelectItem[htcTempList.size()];
    for (int i = 0; i < htcTempList.size(); i++) {
      healthcareTypeItems[i] = htcTempList.get(i);
    }

    List<Municipality> municipalityList = new MunicipalityHelper().getAllMunicipalities();
    List<SelectItem> mTempList = new ArrayList<SelectItem>();
    for (Municipality m : municipalityList) {
      mTempList.add(new SelectItem(m.getMunicipalityCode(), m.getMunicipalityName()));
    }
    municipalityItems = new SelectItem[mTempList.size()];
    for (int i = 0; i < mTempList.size(); i++) {
      municipalityItems[i] = mTempList.get(i);
    }
  }

  public SelectItem[] getMunicipalityItems() {
    return municipalityItems;
  }

  public String getUnitName() {
    return unitName;
  }

  /**
   * Setter for the unit name to search for. Only sets the property if the provided value is not null.
   * 
   * @param unitName The unit name to search for.
   */
  public void setUnitName(String unitName) {
    if (unitName != null) {
      this.unitName = unitName;
    }
  }

  public String getMunicipality() {
    return municipality;
  }

  /**
   * Setter for the municipality to search for. Only sets the property if the provided value is not null.
   * 
   * @param municipality The municipality to search for.
   */
  public void setMunicipality(String municipality) {
    if (municipality != null) {
      this.municipality = municipality;
    }
  }

  /**
   * Checks if the form is empty.
   * 
   * @return True if the form is empty, otherwise false.
   */
  public boolean isEmpty() {
    boolean empty = true;

    empty &= StringUtil.isEmpty(unitName);
    empty &= StringUtil.isEmpty(municipality);
    empty &= StringUtil.isEmpty(healthcareType);

    return empty;
  }

  public String getHealthcareType() {
    return healthcareType;
  }

  /**
   * Setter for the healthcare type to search for. Only sets the property if the provided value is not null.
   * 
   * @param healthcareType The healthcare type to search for.
   */
  public void setHealthcareType(String healthcareType) {
    if (healthcareType != null) {
      this.healthcareType = healthcareType;
    }
  }

  public String getResultType() {
    return resultType;
  }

  /**
   * Setter for the result type to return. Only sets the property if the provided value is not null.
   * 
   * @param resultType The result type to return.
   */
  public void setResultType(String resultType) {
    if (resultType != null) {
      this.resultType = resultType;
    }
  }

  public SelectItem[] getResultTypeItems() {
    return resultTypeItems;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  /**
   * Setter for the sort order to use. Only sets the property if the provided value is not null.
   * 
   * @param sortOrder The sort order to use.
   */
  public void setSortOrder(String sortOrder) {
    if (sortOrder != null) {
      this.sortOrder = sortOrder;
    }
  }

  public SelectItem[] getHealthcareTypeItems() {
    return healthcareTypeItems;
  }
}
