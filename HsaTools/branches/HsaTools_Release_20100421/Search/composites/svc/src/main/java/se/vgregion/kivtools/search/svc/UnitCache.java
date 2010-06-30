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

package se.vgregion.kivtools.search.svc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for units.
 * 
 * @author Joakim Olsson
 */
public class UnitCache {
  private final List<Unit> units = new ArrayList<Unit>();

  public List<Unit> getUnits() {
    return Collections.unmodifiableList(units);
  }

  /**
   * Adds a new unit to the cache.
   * 
   * @param unit The unit to add to the cache.
   */
  public void add(Unit unit) {
    Arguments.notNull("unit", unit);

    if (!this.units.contains(unit)) {
      this.units.add(unit);
    }
  }
}
