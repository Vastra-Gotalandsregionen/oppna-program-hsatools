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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;

public class UnitCacheTest {
  private UnitCache unitCache = new UnitCache();

  @Before
  public void setUp() throws Exception {
    unitCache.add(createUnit("abc-123"));
    unitCache.add(createUnit("def-456"));
    // Add same hsaIdentity again to make sure that duplicates are removed.
    unitCache.add(createUnit("def-456"));
  }

  @Test
  public void testInstantiation() {
    UnitCache unitCache = new UnitCache();
    assertNotNull(unitCache);
  }

  @Test
  public void testGetUnits() {
    List<Unit> units = unitCache.getUnits();
    assertEquals(2, units.size());
  }

  private Unit createUnit(String hsaIdentity) {
    Unit unit = new Unit();
    unit.setHsaIdentity(hsaIdentity);
    return unit;
  }
}
