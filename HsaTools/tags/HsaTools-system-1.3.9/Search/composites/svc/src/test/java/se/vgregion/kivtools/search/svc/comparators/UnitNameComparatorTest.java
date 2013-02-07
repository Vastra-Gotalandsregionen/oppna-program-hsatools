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
 */
package se.vgregion.kivtools.search.svc.comparators;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;

public class UnitNameComparatorTest {
  private final UnitNameComparator comparator = new UnitNameComparator();

  @Test
  public void comparingTwoNullValuesReturnZero() {
    assertEquals(0, comparator.compare(null, null));
  }

  @Test
  public void compareConvertsUnitNameToLowercaseForComparison() {
    Unit unit1 = createUnit("UnItNaMe");
    Unit unit2 = createUnit("unitNAME");
    assertEquals(0, comparator.compare(unit1, unit2));
  }

  private Unit createUnit(String unitName) {
    Unit unit = new Unit();
    unit.setName(unitName);
    return unit;
  }
}
