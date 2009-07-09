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
package se.vgregion.kivtools.search.svc.domain.values;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.domain.Unit;

/**
 * Health care type operations.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class HealthcareTypeConditionHelper {
  private static final String HEALTHCARE_TYPE_CONDITION_KEY = "hsatools.search.svc.impl.condition.name_";
  private static final String HEALTHCARE_TYPE_CONDITION_VALUE = "hsatools.search.svc.impl.condition.value_";
  private static final String HEALTHCARE_TYPE_CONDITION_DISPLAYNAME = "hsatools.search.svc.impl.condition.displayname.";
  private static final String HEALTHCARE_TYPE_CONDITION_FILTERED = "hsatools.search.svc.impl.condition.filtered_";

  private static List<HealthcareType> allHealthcareTypes = new ArrayList<HealthcareType>();
  private static Set<String> allConditionKeys = new HashSet<String>();
  private Log logger = LogFactory.getLog(this.getClass());

  private String implResourcePath;

  public List<HealthcareType> getAllHealthcareTypes() {
    return allHealthcareTypes;
  }

  /**
   * Look through all keys in configuration file and try to match to health care types.
   * 
   * @param unitEntry
   * @return
   */
  public Unit assignHealthcareTypes(Unit unit) {

    long startTimeMillis = System.currentTimeMillis();

    // For every health care type, check if this unit belongs to it.
    List<HealthcareType> healthcareTypesToBeAdded = new ArrayList<HealthcareType>();
    for (HealthcareType ht : allHealthcareTypes) {
      boolean conditionsFulfilled = true;
      for (Entry<String, String> condition : ht.getConditions().entrySet()) {

        // Does the unit have a corresponding field? If the condition
        // key is hsaBusinessClassificationId the unit needs to have
        // this field.
        String key = condition.getKey();
        key = key.substring(0, 1).toUpperCase() + key.substring(1);
        Method keyMethod = null;
        try {
          keyMethod = unit.getClass().getMethod("get" + key, null);
        } catch (Exception e) {
          // If the field is not accessible (security or doesn't exists), there's nothing we can do about it.
        }
        if (keyMethod != null) {
          // Field exists, does it have correct value?

          // The health care type value may be multiple comma separated values
          String[] conditionValues = condition.getValue().split(",");
          Object value = null;
          try {
            value = keyMethod.invoke(unit, null);
          } catch (Exception e) {
            // If the field is not accessible (security or doesn't exists), there's nothing we can do about it.
          }
          boolean conditionFulfilled = false;
          if (value instanceof String) {
            if (value != null) {
              for (String v : conditionValues) {
                if (v.equals(value)) {
                  conditionFulfilled = true;
                }
              }
            }
          } else if (value instanceof List) {
            for (String v : conditionValues) {
              if (((List<String>) value).contains(v)) {
                conditionFulfilled = true;
              }
            }
          }
          if (!conditionFulfilled) {
            conditionsFulfilled = false;
          }
        }
      }
      if (conditionsFulfilled) {
        healthcareTypesToBeAdded.add(ht);
      }
    }
    unit.setHealthcareTypes(healthcareTypesToBeAdded);

    long endTimeMillis = System.currentTimeMillis();
    logger.debug("Assigning health care type for " + unit.getHsaIdentity() + " took: " + (endTimeMillis - startTimeMillis) + " milliseconds.");

    return unit;
  }

  private Enumeration<String> getAllHealthcareConditionConfigurationKeys() {
    ResourceBundle bundle = ResourceBundle.getBundle(getImplResourcePath());
    return bundle.getKeys();
  }

  private String getHealthcareConditionValueByKey(String key) {
    String rv = "";
    ResourceBundle bundle = ResourceBundle.getBundle(getImplResourcePath());
    String value = bundle.getString(key);
    if (value != null) {
      rv = value;
    }
    return rv;
  }

  public String getImplResourcePath() {
    return implResourcePath;
  }

  public void setImplResourcePath(String implResourcePath) {
    this.implResourcePath = implResourcePath;

    Enumeration<String> allKeys = getAllHealthcareConditionConfigurationKeys();
    String currentConditionKey;
    int currentIndex, beginPos, endPos;

    // Get indexes
    Set<Integer> indexes = new HashSet<Integer>();
    while (allKeys.hasMoreElements()) {
      currentConditionKey = allKeys.nextElement();
      if (currentConditionKey.startsWith(HEALTHCARE_TYPE_CONDITION_KEY)) {
        beginPos = currentConditionKey.indexOf('_');
        endPos = currentConditionKey.indexOf("-", beginPos);
        currentIndex = Integer.parseInt(currentConditionKey.substring(beginPos + 1, endPos)); // Get index number
        indexes.add(currentIndex);
      }
    }

    // Get sub indexes
    Map<Integer, Integer> indexesAndsubIndexes = new HashMap<Integer, Integer>();
    for (Integer index : indexes) {
      allKeys = getAllHealthcareConditionConfigurationKeys();
      while (allKeys.hasMoreElements()) {
        currentConditionKey = allKeys.nextElement();
        String typeConditionKeyStart = HEALTHCARE_TYPE_CONDITION_KEY + index + "-";
        if (currentConditionKey.startsWith(typeConditionKeyStart)) {
          if (indexesAndsubIndexes.get(index) == null) {
            indexesAndsubIndexes.put(index, 1);
          } else {
            int subIndex = indexesAndsubIndexes.get(index);
            indexesAndsubIndexes.put(index, ++subIndex);
          }
        }
      }
    }

    // Iterate through all condition keys and condition values, build health
    // care types and corresponding conditions
    for (Entry<Integer, Integer> entry : indexesAndsubIndexes.entrySet()) {
      HealthcareType htc = new HealthcareType(entry.getKey());

      // Add filtered status
      String filteredKey = HEALTHCARE_TYPE_CONDITION_FILTERED + entry.getKey();
      String filteredKeyValue = getHealthcareConditionValueByKey(filteredKey);
      if ("true".equals(filteredKeyValue)) {
        htc.setFiltered(true);
      } else {
        htc.setFiltered(false);
      }

      for (int subIndex = 1; subIndex <= entry.getValue(); subIndex++) {
        String conditionKeyKey = HEALTHCARE_TYPE_CONDITION_KEY + entry.getKey() + "-" + subIndex;
        String conditionKeyValue = getHealthcareConditionValueByKey(conditionKeyKey);
        String conditionValueKey = HEALTHCARE_TYPE_CONDITION_VALUE + entry.getKey() + "-" + subIndex;
        String conditionValueValue = getHealthcareConditionValueByKey(conditionValueKey);
        htc.addCondition(conditionKeyValue, conditionValueValue);
        allConditionKeys.add(conditionKeyValue);
      }
      String displayName = getHealthcareConditionValueByKey(HEALTHCARE_TYPE_CONDITION_DISPLAYNAME + entry.getKey());
      htc.setDisplayName(displayName);
      allHealthcareTypes.add(htc);
    }
    if (allHealthcareTypes != null) {
      Collections.sort(allHealthcareTypes);
    }
  }

  /**
   * Return health care type by index.
   * 
   * @param healthcareTypeIndex
   * @return
   */
  public HealthcareType getHealthcareTypeByIndex(Integer healthcareTypeIndex) {
    for (HealthcareType ht : getAllHealthcareTypes()) {
      if (ht.getIndex().equals(healthcareTypeIndex)) {
        return ht;
      }
    }
    return null;
  }

  /**
   * Return healt care type by name.
   * 
   * @param name
   * @return
   */
  public HealthcareType getHealthcareTypeByName(String name) {
    for (HealthcareType ht : getAllHealthcareTypes()) {
      if (ht.getDisplayName().equals(name)) {
        return ht;
      }
    }
    return null;
  }

  /**
   * Returns all unfiltered health care types.
   */
  public List<HealthcareType> getAllUnfilteredHealthCareTypes() {
    List<HealthcareType> allUnfilteredHealthcareTypes = new ArrayList<HealthcareType>();
    for (HealthcareType ht : getAllHealthcareTypes()) {
      if (!ht.isFiltered()) {
        allUnfilteredHealthcareTypes.add(ht);
      }
    }
    return allUnfilteredHealthcareTypes;
  }
}
