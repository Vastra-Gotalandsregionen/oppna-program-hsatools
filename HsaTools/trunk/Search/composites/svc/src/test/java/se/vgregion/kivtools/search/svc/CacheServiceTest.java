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

import org.junit.Before;
import org.junit.Test;

public class CacheServiceTest {

  private CacheService cacheService;

  @Before
  public void setUp() throws Exception {
    cacheService = new CacheService();
    cacheService.setPersonNameCacheLoader(new PersonNameCacheLoaderMock());
    cacheService.setUnitNameCacheLoader(new UnitNameCacheLoaderMock());
    cacheService.setTitleCacheLoader(new TitleCacheLoaderMock());
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
    TitleCache titleCache = cacheService.getTitleCache();
    assertNotNull(titleCache);
    assertEquals(0, titleCache.getMatchingTitles("").size());

    cacheService.reloadCaches();

    personNameCache = cacheService.getPersonNameCache();
    assertNotNull(personNameCache);
    assertEquals(1, personNameCache.getMatchingGivenNames("", "").size());
    unitNameCache = cacheService.getUnitNameCache();
    assertNotNull(unitNameCache);
    assertEquals(1, unitNameCache.getMatchingUnitNames("").size());
    titleCache = cacheService.getTitleCache();
    assertNotNull(titleCache);
    assertEquals(1, titleCache.getMatchingTitles("").size());
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

  private static class TitleCacheLoaderMock implements TitleCacheLoader {
    @Override
    public TitleCache loadCache() {
      TitleCache titleCache = new TitleCache();
      titleCache.add("Systemingenjör");
      return titleCache;
    }
  }
}
