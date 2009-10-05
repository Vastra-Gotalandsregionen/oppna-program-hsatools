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
package se.vgregion.kivtools.search.svc.domain;

import java.text.Collator;
import java.util.Comparator;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Comparator used when sorting on care type name.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class UnitCareTypeNameComparator implements Comparator<Unit> {

  /**
   * 
   * Sorting by getHsaBusinessClassificationCode. We only take the first HsaBusinessClassificationCode into account and hope that the most important code is first. Algorithm: Sort by
   * hsaBusinessClassificationName and sort the invalid ones
   * 
   * @param unit1 Unit to use in compare with.
   * @param unit2 Unit to use in compare with.
   * @return Returns an integer less than, equal to or greater than zero depending on whether the unit1 String is less than, equal to or greater than the unit2.
   */
  public int compare(Unit unit1, Unit unit2) {
    String unit1HealthcareTypeDisplayName = null;
    String unit2HealthcareTypeDisplayName = null;

    unit1HealthcareTypeDisplayName = getFirstHealthcareTypeDisplayName(unit1);
    unit2HealthcareTypeDisplayName = getFirstHealthcareTypeDisplayName(unit2);

    // If both units have valid classification codes, compare the BusinessClassificationName.
    // If unit1 lacks valid getHsaBusinessClassificationCode, put it last.
    // Same is true for unit2. If both unit1 and unit2 are missing a valid
    // getHsaBusinessClassificationCode, sort by unit name.

    Collator myCollator = Collator.getInstance();
    int compareResult;
    if (!StringUtil.isEmpty(unit1HealthcareTypeDisplayName) && !StringUtil.isEmpty(unit2HealthcareTypeDisplayName)) {
      compareResult = myCollator.compare(unit1HealthcareTypeDisplayName.toLowerCase(), unit2HealthcareTypeDisplayName.toLowerCase());
    } else if (!StringUtil.isEmpty(unit1HealthcareTypeDisplayName) && StringUtil.isEmpty(unit2HealthcareTypeDisplayName)) {
      compareResult = -1;
    } else if (StringUtil.isEmpty(unit1HealthcareTypeDisplayName) && !StringUtil.isEmpty(unit2HealthcareTypeDisplayName)) {
      compareResult = 1;
    } else {
      compareResult = myCollator.compare(unit1.getName().toLowerCase(), unit2.getName().toLowerCase());
    }
    return compareResult;
  }

  private String getFirstHealthcareTypeDisplayName(Unit unit) {
    String displayName = null;

    if (unit.getHealthcareTypes() != null && unit.getHealthcareTypes().size() > 0) {
      displayName = unit.getHealthcareTypes().get(0).getDisplayName();
    }

    return displayName;
  }
}
