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
}
