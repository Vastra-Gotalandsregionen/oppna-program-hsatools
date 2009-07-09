/**
 * Copyright 2009 Västa Götalandsregionen
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
/**
 * 
 */
package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.Municipality;
import se.vgregion.kivtools.search.svc.domain.values.MunicipalityHelper;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 */
@SuppressWarnings("serial")
public class UnitSearchSimpleForm implements Serializable {
  private String unitName = "";
  private String municipality = "";
  private String healthcareType = "";
  private String c = "";
  private String resultType = "1";
  private String sortOrder = "UNIT_NAME";
  private SelectItem[] resultTypeItems = new SelectItem[] { new SelectItem("1", "Lista"), new SelectItem("2", "Karta") };

  private List<HealthcareType> healthcareTypeList;
  private SelectItem[] healthcareTypeItems;
  private List<Municipality> municipalityList;
  private SelectItem[] municipalityItems;

  {
    healthcareTypeList = new HealthcareTypeConditionHelper().getAllHealthcareTypes();
    List<SelectItem> htcTempList = new ArrayList<SelectItem>();
    htcTempList.add(new SelectItem("0", "Alla typer av v\u00E5rd"));
    for (HealthcareType htc : healthcareTypeList) {
      String value = htc.getIndex().toString();
      htcTempList.add(new SelectItem(value, htc.getDisplayName()));
    }
    healthcareTypeItems = new SelectItem[htcTempList.size()];
    for (int i = 0; i < htcTempList.size(); i++) {
      healthcareTypeItems[i] = htcTempList.get(i);
    }

    municipalityList = new MunicipalityHelper().getAllMunicipalities();
    List<SelectItem> mTempList = new ArrayList<SelectItem>();
    mTempList.add(new SelectItem("", "V\u00E4lj kommun"));
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

  public void setMunicipalityItems(SelectItem[] municipalityItems) {
    this.municipalityItems = municipalityItems;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getMunicipality() {
    return municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public boolean isEmpty() {
    if (unitName == null && municipality == null && healthcareType == null) {
      return true;
    }
    if (unitName.trim().length() == 0 && municipality.trim().length() == 0 && healthcareType.trim().length() == 0) {
      return true;
    }
    return false;
  }

  public String getHealthcareType() {
    return healthcareType;
  }

  public void setHealthcareType(String healthcareType) {
    this.healthcareType = healthcareType;
  }

  public String getC() {
    return c;
  }

  public void setC(String c) {
    this.c = c;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public SelectItem[] getResultTypeItems() {
    return resultTypeItems;
  }

  public void setResultTypeItems(SelectItem[] resultTypeItems) {
    this.resultTypeItems = resultTypeItems;
  }

  public String getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(String sortOrder) {
    this.sortOrder = sortOrder;
  }

  public List<HealthcareType> getHealthcareTypeList() {
    return healthcareTypeList;
  }

  public void setHealthcareTypeList(List<HealthcareType> healthcareTypeList) {
    this.healthcareTypeList = healthcareTypeList;
  }

  public SelectItem[] getHealthcareTypeItems() {
    return healthcareTypeItems;
  }

  public void setHealthcareTypeItems(SelectItem[] healthcareTypeItems) {
    this.healthcareTypeItems = healthcareTypeItems;
  }

  public List<Municipality> getMunicipalityList() {
    return municipalityList;
  }

  public void setMunicipalityList(List<Municipality> municipalityList) {
    this.municipalityList = municipalityList;
  }
}
