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

package se.vgregion.kivtools.search.svc.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.SitemapCache;
import se.vgregion.kivtools.search.svc.UnitCacheServiceImpl;

public class PublicSitemapCacheLoaderImplTest {
  private UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private PublicSitemapCacheLoaderImpl loader = new PublicSitemapCacheLoaderImpl(unitCacheService, "http://external.com");

  @Test
  public void createEmptyCacheReturnEmptyCache() {
    SitemapCache emptyCache = loader.createEmptyCache();
    assertNotNull(emptyCache);
    assertEquals(0, emptyCache.getEntries(null).size());
  }

  @Test
  public void loadCacheReloadsUnitCacheIfNoUnitsAreFound() {
    SitemapCache cache = loader.loadCache();
    assertNotNull(cache);
    assertEquals(3, cache.getEntries(null).size());
  }

  @Test
  public void locationUsesExternalUrl() {
    SitemapCache cache = loader.loadCache();
    assertEquals("http://external.com/visaenhet?hsaidentity=ABC-123", cache.getEntries(null).get(0).getLocation());
  }

  @Test
  public void loadCacheUsesCreateTimestampForLastmodIfEntryIsNotModified() {
    SitemapCache cache = loader.loadCache();
    assertEquals("2010-02-10T01:00:00+01:00", cache.getEntries(null).get(0).getLastModified());
  }

  @Test
  public void loadCacheUsesModifyTimestampForLastmodIfEntryIsModified() {
    SitemapCache cache = loader.loadCache();
    assertEquals("2010-02-16T01:00:00+01:00", cache.getEntries(null).get(1).getLastModified());
  }
}
