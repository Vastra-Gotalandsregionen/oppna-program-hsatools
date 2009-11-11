package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CacheServiceTest {

  private CacheService cacheService;

  @Before
  public void setUp() throws Exception {
    cacheService = new CacheService();
    cacheService.setPersonNameCacheLoader(new PersonNameCacheLoaderMock());
    cacheService.setUnitNameCacheLoader(new UnitNameCacheLoaderMock());
  }

  @Test
  public void testInstantiation() {
    CacheService cacheService = new CacheService();
    assertNotNull(cacheService);
  }

  @Test
  public void testReloadCaches() {
    PersonNameCache personNameCache = cacheService.getPersonNameCache();
    assertNotNull(personNameCache);
    assertEquals(0, personNameCache.getMatchingGivenNames("", "").size());
    UnitNameCache unitNameCache = cacheService.getUnitNameCache();
    assertNotNull(unitNameCache);
    assertEquals(0, unitNameCache.getMatchingUnitNames("").size());

    cacheService.reloadCaches();

    personNameCache = cacheService.getPersonNameCache();
    assertNotNull(personNameCache);
    assertEquals(1, personNameCache.getMatchingGivenNames("", "").size());
    unitNameCache = cacheService.getUnitNameCache();
    assertNotNull(unitNameCache);
    assertEquals(1, unitNameCache.getMatchingUnitNames("").size());
  }

  private static class PersonNameCacheLoaderMock implements PersonNameCacheLoader {
    @Override
    public PersonNameCache loadCache() {
      PersonNameCache personNameCache = new PersonNameCache();
      personNameCache.add("Nina", "Kanin");
      return personNameCache;
    }
  }

  private static class UnitNameCacheLoaderMock implements UnitNameCacheLoader {
    @Override
    public UnitNameCache loadCache() {
      UnitNameCache unitNameCache = new UnitNameCache();
      unitNameCache.add("Tandregleringen Varberg");
      return unitNameCache;
    }
  }
}
