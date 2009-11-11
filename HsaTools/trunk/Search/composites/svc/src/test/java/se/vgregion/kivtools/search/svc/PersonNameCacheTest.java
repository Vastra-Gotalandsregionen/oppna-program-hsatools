package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PersonNameCacheTest {

  private PersonNameCache personNameCache;

  @Before
  public void setUp() throws Exception {
    personNameCache = new PersonNameCache();
    personNameCache.add("Janne", "Långben");
    personNameCache.add("Kajsa", "Anka");
    personNameCache.add("Kalle", "Gråben");
    // Add same names again to make sure that duplicates are removed.
    personNameCache.add("Kalle", "Gråben");
    personNameCache.add("Kapsejsa", "Sakta");
    personNameCache.add(" Mimmi ", " Pigg ");
    personNameCache.add("Kalle", "Kotte");
  }

  @Test
  public void testInstantiation() {
    PersonNameCache personNameCache = new PersonNameCache();
    assertNotNull(personNameCache);
  }

  @Test
  public void testGetMatchingGivenNamesNoMatchesFound() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("Kalle", "Anka");
    assertEquals(0, matchingGivenNames.size());
  }

  @Test
  public void testGetMatchingSurnamesNoMatchesFound() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("Kalle", "Anka");
    assertEquals(0, matchingSurnames.size());
  }

  @Test
  public void testGetMatchingGivenNamesExactMatch() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("Kajsa", "Anka");
    assertEquals(1, matchingGivenNames.size());
    assertEquals("Kajsa", matchingGivenNames.get(0));
  }

  @Test
  public void testGetMatchingSurnamesExactMatch() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("Kajsa", "Anka");
    assertEquals(1, matchingSurnames.size());
    assertEquals("Anka", matchingSurnames.get(0));
  }

  @Test
  public void testGetMatchingGivenNamesNoSurname() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("Kajsa", "");
    assertEquals(1, matchingGivenNames.size());
    assertEquals("Kajsa", matchingGivenNames.get(0));
  }

  @Test
  public void testGetMatchingSurnamesNoGivenName() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("", "Anka");
    assertEquals(1, matchingSurnames.size());
    assertEquals("Anka", matchingSurnames.get(0));
  }

  @Test
  public void testGetMatchingGivenNamesPartialMatchGivenName() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("jsa", "");
    assertEquals(2, matchingGivenNames.size());
    assertTrue(matchingGivenNames.contains("Kajsa"));
    assertTrue(matchingGivenNames.contains("Kapsejsa"));
  }

  @Test
  public void testGetMatchingSurnamesPartialMatchSurname() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("", "ben");
    assertEquals(2, matchingSurnames.size());
    assertTrue(matchingSurnames.contains("Långben"));
    assertTrue(matchingSurnames.contains("Gråben"));
  }

  @Test
  public void testGetMatchingGivenNamesPartialMatchBothFields() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("jsa", "kta");
    assertEquals(1, matchingGivenNames.size());
    assertTrue(matchingGivenNames.contains("Kapsejsa"));
  }

  @Test
  public void testGetMatchingSurnamesPartialMatch() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("lle", "ben");
    assertEquals(1, matchingSurnames.size());
    assertTrue(matchingSurnames.contains("Gråben"));
  }

  @Test
  public void testGetMatchingGivenNamesUntrimmedInput() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames(" jsa ", " kta ");
    assertEquals(1, matchingGivenNames.size());
    assertTrue(matchingGivenNames.contains("Kapsejsa"));
  }

  @Test
  public void testGetMatchingSurnamesUntrimmedInput() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames(" lle ", " ben ");
    assertEquals(1, matchingSurnames.size());
    assertTrue(matchingSurnames.contains("Gråben"));
  }

  @Test
  public void testGetMatchingGivenNamesUntrimmedCachedData() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("MiMmI", "pIgG");
    assertEquals(1, matchingGivenNames.size());
    assertTrue(matchingGivenNames.contains("Mimmi"));
  }

  @Test
  public void testGetMatchingSurnamesUntrimmedCachedData() {
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("MiMmI", "pIgG");
    assertEquals(1, matchingSurnames.size());
    assertTrue(matchingSurnames.contains("Pigg"));
  }

  @Test
  public void testGetMatchingGivenNamesNoDuplicates() {
    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("Kalle", "");
    assertEquals(1, matchingGivenNames.size());
  }
}
