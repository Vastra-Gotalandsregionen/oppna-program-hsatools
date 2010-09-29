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

package se.vgregion.kivtools.search.svc.comparators;

import java.text.Collator;
import java.util.Comparator;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Comparator for Unit instances.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class UnitNameComparator implements Comparator<Unit> {

  /**
   * Compares two Unit instances using unit name.
   * 
   * @param unit1 The first unit to compare.
   * @param unit2 The second unit to compare.
   * @return A value less than 0 if unit1 < unit2, 0 if unit1 == unit2 and a value greater than 0 if unit1 > unit2.
   */
  public int compare(Unit unit1, Unit unit2) {
    Unit comparedUnit1 = unit1;
    Unit comparedUnit2 = unit2;

    if (comparedUnit1 == null) {
      comparedUnit1 = new Unit();
    }
    if (comparedUnit2 == null) {
      comparedUnit2 = new Unit();
    }
    if (StringUtil.isEmpty(comparedUnit1.getName())) {
      comparedUnit1.setName("");
    }
    if (StringUtil.isEmpty(comparedUnit2.getName())) {
      comparedUnit2.setName("");
    }

    Collator myCollator = Collator.getInstance();
    return myCollator.compare(comparedUnit1.getName().toLowerCase(), comparedUnit2.getName().toLowerCase());
  }
}
