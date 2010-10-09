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

import se.vgregion.kivtools.search.svc.cache.PersonNameCache;
import se.vgregion.kivtools.search.svc.impl.cache.PersonCacheServiceImpl;
import se.vgregion.kivtools.search.svc.impl.cache.PersonNameCacheLoaderImpl;

public class PersonNameCacheLoaderImplTest {
  private final PersonCacheServiceImpl unitCacheService = new PersonCacheServiceImpl(new PersonCacheLoaderMock());
  private final PersonNameCacheLoaderImpl loader = new PersonNameCacheLoaderImpl(this.unitCacheService);

  @Test
  public void createEmptyCacheReturnEmptyCache() {
    PersonNameCache emptyCache = this.loader.createEmptyCache();
    assertNotNull(emptyCache);
    assertEquals(0, emptyCache.getMatchingGivenNames("a", "").size());
  }

  @Test
  public void loadCacheReloadsUnitCacheIfNoUnitsAreFound() {
    PersonNameCache cache = this.loader.loadCache();
    assertNotNull(cache);
    assertEquals(3, cache.getMatchingSurnames("", "o").size());
  }
}
