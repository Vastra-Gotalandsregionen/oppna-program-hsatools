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
package se.vgregion.kivtools.search.domain.values;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Represents a health care type (eg Jourcentral) that can be selected eg when searching for units.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class HealthcareType implements IsEmptyMarker, Serializable, Comparable<HealthcareType> {
  private static final long serialVersionUID = 1L;

  private Map<String, String> conditions = new HashMap<String, String>();
  private String displayName;
  private Integer index;
  // Should this care type care about hsaBusinessClassificationCode filters?
  private boolean filtered;

  /**
   * Constructs a new HealthcareType instance.
   */
  public HealthcareType() {
  }

  /**
   * Constructs a new HealthcareType instance using the provided index.
   * 
   * @param index The index to use for the new HealthcareType instance.
   */
  public HealthcareType(int index) {
    this.index = index;
  }

  /**
   * Constructs a new HealthcareType instance using the provided information.
   * 
   * @param conditions The map of conditions to use for the new HealthcareType instance.
   * @param displayName The display name to use for the new HealthcareType instance.
   * @param filtered The filtered-status to use for the new HealthcareType instance.
   * @param index The index to use for the new HealthcareType instance.
   */
  public HealthcareType(Map<String, String> conditions, String displayName, boolean filtered, Integer index) {
    super();
    this.conditions = conditions;
    this.displayName = displayName;
    this.filtered = filtered;
    this.index = index;
  }

  public boolean isFiltered() {
    return filtered;
  }

  public void setFiltered(boolean filtered) {
    this.filtered = filtered;
  }

  public Map<String, String> getConditions() {
    return conditions;
  }

  public void setConditions(Map<String, String> conditions) {
    this.conditions = conditions;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isEmpty() {
    return StringUtil.isEmpty(displayName);
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public Integer getIndex() {
    return index;
  }

  @Override
  public int compareTo(HealthcareType o) {
    return this.getIndex().compareTo(o.getIndex());
  }

  /**
   * Adds a new condition to this HealthcareType.
   * 
   * @param conditionKey The name of the condition.
   * @param conditionValue The value of the condition.
   */
  public void addCondition(String conditionKey, String conditionValue) {
    conditions.put(conditionKey, conditionValue);
  }
}
