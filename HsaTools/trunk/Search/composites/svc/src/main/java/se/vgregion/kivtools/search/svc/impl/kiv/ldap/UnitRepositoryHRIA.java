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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

/**
 * Implementation of the UnitRepository for Hitta rätt i Administration for VGR.
 */
public class UnitRepositoryHRIA extends UnitRepository {
  @Override
  protected void removeUnallowedUnits(SikSearchResultList<Unit> units) {

    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    for (int j = units.size() - 1; j >= 0; j--) {
      if (unitMatchesUnfilteredHealtcareType(units.get(j), allUnfilteredHealthcareTypes)) {
        units.remove(units.get(j));
      }
    }
  }

  private boolean unitMatchesUnfilteredHealtcareType(Unit unit, List<HealthcareType> allUnfilteredHealthcareTypes) {
    boolean found = false;
    for (HealthcareType h : allUnfilteredHealthcareTypes) {
      for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
        String key = condition.getKey();
        String[] conditionValues = condition.getValue().split(",");
        Object value = ReflectionUtil.getProperty(unit, key);

        boolean conditionFulfilled = false;
        if (value instanceof String) {
          for (String v : conditionValues) {
            if (v.equals(value)) {
              conditionFulfilled = true;
            }
          }
        } else if (value instanceof List<?>) {
          for (String v : conditionValues) {
            if (((List<?>) value).contains(v)) {
              conditionFulfilled = true;
            }
          }
        }
        if (conditionFulfilled) {
          found = true;
          break;
        }
      }
    }
    return found;
  }
}
