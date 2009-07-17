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
import se.vgregion.kivtools.search.util.ReflectionUtil;

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

  /**
   * Gets all healthcare types.
   * 
   * @return A list of all healthcare types.
   */
  public List<HealthcareType> getAllHealthcareTypes() {
    return allHealthcareTypes;
  }

  /**
   * Look through all keys in configuration file and try to match to health care types.
   * 
   * @param unit The unit to assign healthcare types to.
   * @return The updated unit.
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
        if (ReflectionUtil.hasMethod(unit, "get" + key)) {
          // Field exists, does it have correct value?

          Object value = ReflectionUtil.callMethod(unit, "get" + key);
          String conditionValue = condition.getValue();
          conditionsFulfilled &= checkConditionFulfilled(value, conditionValue);
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

  /**
   * Helper-method to check if the condition is fulfilled.
   * 
   * @param value The value to check against the condition value.
   * @param conditionValue The condition value to check against.
   * @return True if the condition is fulfilled, otherwise false.
   */
  @SuppressWarnings("unchecked")
  private boolean checkConditionFulfilled(Object value, String conditionValue) {
    boolean conditionFulfilled = false;
    // The health care type value may be multiple comma separated values
    String[] conditionValues = conditionValue.split(",");
    if (value instanceof String) {
      conditionFulfilled = checkConditionFulfilled((String) value, conditionValues);
    } else if (value instanceof List) {
      conditionFulfilled = checkConditionFulfilled((List) value, conditionValues);
    }
    return conditionFulfilled;
  }

  /**
   * Checks a List of string-values against the conditionValues to see if the condition is fulfilled.
   * 
   * @param value The List of string-values to check.
   * @param conditionValues The conditionValues to check against.
   * @return True if the condition is fulfilled.
   */
  @SuppressWarnings("unchecked")
  private boolean checkConditionFulfilled(List<?> value, String[] conditionValues) {
    boolean conditionFulfilled = false;

    for (String v : conditionValues) {
      if (((List<String>) value).contains(v)) {
        conditionFulfilled = true;
      }
    }

    return conditionFulfilled;
  }

  /**
   * Checks a string-value against the conditionValues to see if the condition is fulfilled.
   * 
   * @param value The string-value to check.
   * @param conditionValues The conditionValues to check against.
   * @return True if the condition is fulfilled.
   */
  private boolean checkConditionFulfilled(String value, String[] conditionValues) {
    boolean conditionFulfilled = false;

    if (value != null) {
      for (String v : conditionValues) {
        if (v.equals(value)) {
          conditionFulfilled = true;
        }
      }
    }

    return conditionFulfilled;
  }

  private Enumeration<String> getAllHealthcareConditionConfigurationKeys() {
    ResourceBundle bundle = ResourceBundle.getBundle(implResourcePath);
    return bundle.getKeys();
  }

  private String getHealthcareConditionValueByKey(String key) {
    String rv = "";
    ResourceBundle bundle = ResourceBundle.getBundle(implResourcePath);
    String value = bundle.getString(key);
    if (value != null) {
      rv = value;
    }
    return rv;
  }

  /**
   * Sets the resource path and builds the internal cache of healtcare type conditions.
   * 
   * @param implResourcePath The package path to the properties-file containing healthcare type conditions.
   */
  public void setImplResourcePath(String implResourcePath) {
    this.implResourcePath = implResourcePath;

    Enumeration<String> allKeys = getAllHealthcareConditionConfigurationKeys();

    // Get indexes
    Set<Integer> indexes = getIndexes(allKeys);

    // Get sub indexes
    Map<Integer, Integer> indexesAndsubIndexes = getIndexMap(indexes);

    // Iterate through all condition keys and condition values, build health
    // care types and corresponding conditions
    buildInternalCache(indexesAndsubIndexes);
  }

  /**
   * Helper-method that builds the internal cache of healthcare type conditions.
   * 
   * @param indexesAndsubIndexes The map of indexes and subindexes to base the cache on.
   */
  private void buildInternalCache(Map<Integer, Integer> indexesAndsubIndexes) {
    for (Entry<Integer, Integer> entry : indexesAndsubIndexes.entrySet()) {
      HealthcareType htc = new HealthcareType(entry.getKey());

      // Add filtered status
      String filteredKey = HEALTHCARE_TYPE_CONDITION_FILTERED + entry.getKey();
      String filteredKeyValue = getHealthcareConditionValueByKey(filteredKey);
      htc.setFiltered("true".equals(filteredKeyValue));

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

    Collections.sort(allHealthcareTypes);
  }

  /**
   * Helper-method which builds a map of indexes needed for building the internal cache of healthcare type conditions.
   * 
   * @param indexes The set of indexes to base the map on.
   * @return A map of indexes and subindexes.
   */
  private Map<Integer, Integer> getIndexMap(Set<Integer> indexes) {
    Enumeration<String> allKeys;
    String currentConditionKey;
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
    return indexesAndsubIndexes;
  }

  /**
   * Helper-method for building a set of indexes from the provided enumeration of keys.
   * 
   * @param allKeys The enumeration of keys to build the index set from.
   * @return The index set of the provided enumeration of keys.
   */
  private Set<Integer> getIndexes(Enumeration<String> allKeys) {
    String currentConditionKey;
    int currentIndex;
    int beginPos;
    int endPos;
    Set<Integer> indexes = new HashSet<Integer>();
    while (allKeys.hasMoreElements()) {
      currentConditionKey = allKeys.nextElement();
      if (currentConditionKey.startsWith(HEALTHCARE_TYPE_CONDITION_KEY)) {
        beginPos = currentConditionKey.indexOf('_');
        endPos = currentConditionKey.indexOf("-", beginPos);
        // Get index number
        currentIndex = Integer.parseInt(currentConditionKey.substring(beginPos + 1, endPos));
        indexes.add(currentIndex);
      }
    }
    return indexes;
  }

  /**
   * Get a healthcare type by index.
   * 
   * @param healthcareTypeIndex The index of the healthcare type to get.
   * @return The healthcare type with the provided index or null if no healthcare type was found.
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
   * Get a healthcare type by name.
   * 
   * @param name The name of the healthcare type to get.
   * @return The healthcare type found by the provided name or null if no healthcare type was found.
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
   * Returns all unfiltered healthcare types.
   * 
   * @return A list of all unfiltered healthcare types.
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
