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

package se.vgregion.kivtools.search.svc.cache;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for unit names.
 * 
 * @author Joakim Olsson
 */
public class UnitNameCache {
  private final List<String> unitNames = new ArrayList<String>();

  /**
   * Retrieves a list of all matching unit names from the cache.
   * 
   * @param unitName The unit name to match against the cache.
   * @return A list of all matching unit names in the cache or an empty list if no matches were found.
   */
  public List<String> getMatchingUnitNames(String unitName) {
    Arguments.notNull("unitName", unitName);
    String searchString = unitName.trim().toLowerCase();
    List<String> result = new ArrayList<String>();

    for (String currentUnitName : unitNames) {
      if (currentUnitName.toLowerCase().contains(searchString)) {
        result.add(currentUnitName);
      }
    }

    Collections.sort(result, Collator.getInstance());

    return result;
  }

  /**
   * Adds a new unit name to the cache.
   * 
   * @param unitName The unit name to add to the cache.
   */
  public void add(String unitName) {
    Arguments.notNull("unitName", unitName);

    String trimmedUnitName = unitName.trim();
    if (!this.unitNames.contains(trimmedUnitName)) {
      this.unitNames.add(trimmedUnitName);
    }
  }
}
