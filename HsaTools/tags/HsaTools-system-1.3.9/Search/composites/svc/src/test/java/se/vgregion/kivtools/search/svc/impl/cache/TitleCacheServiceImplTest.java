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
package se.vgregion.kivtools.search.svc.impl.cache;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.TitleCache;
import se.vgregion.kivtools.search.svc.impl.cache.TitleCacheServiceImpl;

public class TitleCacheServiceImplTest {
  @Test
  public void testInstantiation() {
    TitleCacheServiceImpl titleCacheService = new TitleCacheServiceImpl(new CacheLoaderMock());
    assertNotNull(titleCacheService);
  }

  private static class CacheLoaderMock implements CacheLoader<TitleCache> {
    @Override
    public TitleCache createEmptyCache() {
      return null;
    }

    @Override
    public TitleCache loadCache() {
      return null;
    }
  }
}
