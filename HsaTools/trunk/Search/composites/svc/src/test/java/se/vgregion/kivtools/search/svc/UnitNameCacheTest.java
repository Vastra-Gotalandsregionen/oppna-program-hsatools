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
package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UnitNameCacheTest {

  private UnitNameCache unitNameCache;

  @Before
  public void setUp() throws Exception {
    unitNameCache = new UnitNameCache();
    unitNameCache.add("Vårdcentralen Hylte");
    unitNameCache.add("IT-Avdelningen");
    // Add same name again to make sure that duplicates are removed.
    unitNameCache.add("IT-Avdelningen");
    unitNameCache.add(" Akutmottagning Varberg ");
  }

  @Test
  public void testInstantiation() {
    UnitNameCache unitNameCache = new UnitNameCache();
    assertNotNull(unitNameCache);
  }

  @Test
  public void testGetMatchingUnitNamesNoMatchesFound() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("test");
    assertEquals(0, matchingUnitNames.size());
  }

  @Test
  public void testGetMatchingUnitNamesExactMatch() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("it-avdelningen");
    assertEquals(1, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("IT-Avdelningen"));
  }

  @Test
  public void testGetMatchingUnitNamesPartialMatch() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("lte");
    assertEquals(1, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("Vårdcentralen Hylte"));
  }

  @Test
  public void testGetMatchingUnitNamesUntrimmedInput() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames(" lte ");
    assertEquals(1, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("Vårdcentralen Hylte"));
  }

  @Test
  public void testGetMatchingUnitNamesUntrimmedCachedData() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("varberg");
    assertEquals(1, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("Akutmottagning Varberg"));
  }

  @Test
  public void testGetMatchingUnitNamesCorrectSortOrder() {
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("e");
    assertEquals(3, matchingUnitNames.size());
    assertEquals("Akutmottagning Varberg", matchingUnitNames.get(0));
    assertEquals("IT-Avdelningen", matchingUnitNames.get(1));
    assertEquals("Vårdcentralen Hylte", matchingUnitNames.get(2));
  }
}
