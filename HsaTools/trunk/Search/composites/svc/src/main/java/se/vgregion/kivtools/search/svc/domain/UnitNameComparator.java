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
package se.vgregion.kivtools.search.svc.domain;

import java.text.Collator;
import java.util.Comparator;

import se.vgregion.kivtools.util.StringUtil;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Used to sort units by name
 */
public class UnitNameComparator implements Comparator<Unit> {

  /*
   * Returns <0 if unit1<unit2 Returns 0 if unit1==unit2 Returns >0 if unit1>unit2
   */
  public int compare(Unit unit1, Unit unit2) {
    if (unit1 == null) {
      unit1 = new Unit();
      unit1.setName("");
    }
    if (unit2 == null) {
      unit2 = new Unit();
      unit2.setName("");
    }
    if (StringUtil.isEmpty(unit1.getName())) {
      unit1.setName("");
    }
    if (StringUtil.isEmpty(unit2.getName())) {
      unit2.setName("");
    }

    Collator myCollator = Collator.getInstance();
    return myCollator.compare(unit1.getName().toLowerCase(), unit2.getName().toLowerCase());
  }
}
