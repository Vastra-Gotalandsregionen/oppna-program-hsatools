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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.cache.SitemapCache;
import se.vgregion.kivtools.search.svc.impl.cache.InternalSitemapCacheLoaderImpl;
import se.vgregion.kivtools.search.svc.impl.cache.PersonCacheServiceImpl;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheServiceImpl;
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.http.HttpFetcher;

public class InternalSitemapCacheLoaderImplTest {
  private final UnitCacheServiceImpl unitCacheService = new UnitCacheServiceImpl(new UnitCacheLoaderMock());
  private final PersonCacheServiceImpl personCacheService = new PersonCacheServiceImpl(new PersonCacheLoaderMock());
  private final HttpFetcherStaticMock httpFetcher = new HttpFetcherStaticMock();
  private final MvkClient mvkClient = new MvkClient(this.httpFetcher, "uid123", "http://localhost?mvk=1");
  private final InternalSitemapCacheLoaderImpl loader = new InternalSitemapCacheLoaderImpl(this.unitCacheService, this.personCacheService, this.mvkClient, "http://internal.com", "weekly");

  @Test
  public void createEmptyCacheReturnEmptyCache() {
    SitemapCache emptyCache = this.loader.createEmptyCache();
    assertNotNull(emptyCache);
    assertEquals(0, emptyCache.getEntries(null).size());
  }

  @Test
  public void loadCacheReloadsUnitCacheIfNoUnitsAreFound() {
    SitemapCache cache = this.loader.loadCache();
    assertNotNull(cache);
    assertEquals(6, cache.getEntries(null).size());
  }

  @Test
  public void locationUsesInternalUrlForUnits() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("http://internal.com/visaenhet?hsaidentity=ABC-123", cache.getEntries(null).get(0).getLocation());
  }

  @Test
  public void loadCacheUsesCreateTimestampForLastmodIfUnitIsNotModified() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-10T01:00:00+01:00", cache.getEntries(null).get(0).getLastModified());
  }

  @Test
  public void loadCacheUsesModifyTimestampForLastmodIfUnitIsModified() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-16T01:00:00+01:00", cache.getEntries(null).get(1).getLastModified());
  }

  @Test
  public void unitIsAddedAsExtraInformation() {
    SitemapCache cache = this.loader.loadCache();

    for (Object extraInformation : cache.getEntries(null).get(2)) {
      assertTrue("extra information is a unit", extraInformation instanceof se.vgregion.kivtools.svc.sitemap.Unit);
      assertEquals("JKL-654", ((se.vgregion.kivtools.svc.sitemap.Unit) extraInformation).getHsaIdentity());
    }
  }

  @Test
  public void locationUsesInternalUrlForPersons() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("http://internal.com/visaperson?vgrid=krila8", cache.getEntries(null).get(4).getLocation());
  }

  @Test
  public void personIsAddedAsExtraInformation() {
    SitemapCache cache = this.loader.loadCache();
    for (Object extraInformation : cache.getEntries(null).get(3)) {
      assertTrue("extra information is a person", extraInformation instanceof se.vgregion.kivtools.svc.sitemap.Person);
      assertEquals("hsa-456", ((se.vgregion.kivtools.svc.sitemap.Person) extraInformation).getHsaIdentity());
    }
  }

  @Test
  public void latestDateOnEmploymentIsUsedForLastmod() {
    SitemapCache cache = this.loader.loadCache();
    assertEquals("2010-02-16T01:00:00+01:00", cache.getEntries(null).get(3).getLastModified());
  }

  @Test
  public void mvkClientIsCalledForEachUnitToPopulateMvkServices() {
    this.loader.loadCache();
    assertEquals("calls to MVK", 3, this.httpFetcher.callCount);
  }

  private static class HttpFetcherStaticMock implements HttpFetcher {
    private int callCount;

    @Override
    public String fetchUrl(String urlToFetch) {
      this.callCount++;
      return "<xml></xml>";
    }
  }
}
