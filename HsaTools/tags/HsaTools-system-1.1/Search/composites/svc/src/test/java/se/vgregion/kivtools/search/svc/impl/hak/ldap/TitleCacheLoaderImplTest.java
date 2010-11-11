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

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.cache.TitleCache;
import se.vgregion.kivtools.search.svc.impl.cache.PersonCacheLoaderMock;
import se.vgregion.kivtools.search.svc.impl.cache.PersonCacheServiceImpl;

public class TitleCacheLoaderImplTest {
  private final PersonCacheServiceImpl personCacheService = new PersonCacheServiceImpl(new PersonCacheLoaderMock());
  private final TitleCacheLoaderImpl titleCacheLoaderImpl = new TitleCacheLoaderImpl(this.personCacheService);

  @Test
  public void createEmptyCacheReturnNewEmptyCacheEachTime() {
    TitleCache emptyCache1 = this.titleCacheLoaderImpl.createEmptyCache();
    TitleCache emptyCache2 = this.titleCacheLoaderImpl.createEmptyCache();
    assertEquals(0, emptyCache1.getMatchingTitles("").size());
    assertEquals(0, emptyCache2.getMatchingTitles("").size());
    assertNotSame(emptyCache1, emptyCache2);
  }

  @Test
  public void testLoadCache() {
    TitleCache titleCache = this.titleCacheLoaderImpl.loadCache();

    List<String> matchingTitles = titleCache.getMatchingTitles("sis");
    assertEquals(1, matchingTitles.size());
    assertTrue(matchingTitles.contains("Assistent"));
  }
}
