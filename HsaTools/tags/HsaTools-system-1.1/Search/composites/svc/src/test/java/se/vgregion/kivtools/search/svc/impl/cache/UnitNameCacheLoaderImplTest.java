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

package se.vgregion.kivtools.search.svc.impl.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.cache.UnitNameCache;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;
import se.vgregion.kivtools.search.svc.impl.cache.UnitNameCacheLoaderImpl;

public class UnitNameCacheLoaderImplTest {
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private final UnitNameCacheLoaderImpl loader = new UnitNameCacheLoaderImpl(this.unitCacheService);

  @Test
  public void createEmptyCacheReturnEmptyCache() {
    UnitNameCache emptyCache = this.loader.createEmptyCache();
    assertNotNull(emptyCache);
    assertEquals(0, emptyCache.getMatchingUnitNames("a").size());
  }

  @Test
  public void loadCacheReloadsUnitCacheIfNoUnitsAreFound() {
    UnitNameCache cache = this.loader.loadCache();
    assertNotNull(cache);
    assertEquals(3, cache.getMatchingUnitNames("a").size());
  }
}
