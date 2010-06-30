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

package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import org.junit.Test;

public class SitemapSupportBeanTest {
  private SitemapCacheLoaderMock sitemapCacheLoader = new SitemapCacheLoaderMock();
  private SitemapCacheServiceImpl sitemapCacheService = new SitemapCacheServiceImpl(sitemapCacheLoader);
  private SitemapGenerator internalSitemapGenerator = new InternalSitemapGenerator();
  private SitemapGenerator externalSitemapGenerator = new ExternalSitemapGenerator();
  private SitemapSupportBean internalSitemapSupportBean = new SitemapSupportBean(sitemapCacheService, internalSitemapGenerator);
  private SitemapSupportBean externalSitemapSupportBean = new SitemapSupportBean(sitemapCacheService, externalSitemapGenerator);

  @Test
  public void cacheIsReloadedIfEmpty() {
    internalSitemapSupportBean.getSitemapContent();
    sitemapCacheLoader.assertCacheLoaded();
  }

  @Test
  public void locAndLastmodUsesLocationAndLastModified() {
    sitemapCacheLoader.setUnitCache(createSitemapCache("abc-123", "2010-02-01T01:00:00+01:00"));
    String sitemapContent = internalSitemapSupportBean.getSitemapContent();
    String loc = getTagContent(sitemapContent, "loc");
    assertEquals("http://external.com/visaenhet?hsaidentity=abc-123", loc);
    String lastmod = getTagContent(sitemapContent, "lastmod");
    assertEquals("2010-02-01T01:00:00+01:00", lastmod);
  }

  @Test
  public void changeFrequencyIsUsedForChangefreqTag() {
    sitemapCacheLoader.setUnitCache(createSitemapCache("abc-123", "2010-02-01T01:00:00+01:00"));
    String sitemapContent = internalSitemapSupportBean.getSitemapContent();
    String changefreq = getTagContent(sitemapContent, "changefreq");
    assertEquals("daily", changefreq);
  }

  @Test
  public void extraInformationIsAddedIfAvailable() {
    sitemapCacheLoader.setUnitCache(createSitemapCache("abc-123", "2010-02-01T01:00:00+01:00", "hsaIdentity", "abc-123"));
    String sitemapContent = internalSitemapSupportBean.getSitemapContent();

    String hsaIdentity = getTagContent(sitemapContent, "hsa:hsaIdentity");
    assertEquals("abc-123", hsaIdentity);
  }

  @Test
  public void noExtraInformationIsAddedForExternalSitemap() {
    sitemapCacheLoader.setUnitCache(createSitemapCache("abc-123", "2010-02-01T01:00:00+01:00", "hsaIdentity", "abc-123"));
    String sitemapContent = externalSitemapSupportBean.getSitemapContent();

    assertEquals("hsa namespace found in sitemap content", -1, sitemapContent.indexOf("hsa:"));
  }

  private String getTagContent(String content, String tag) {
    int startIndex = content.indexOf("<" + tag + ">");
    int endIndex = content.indexOf("</" + tag + ">");

    String tagContent = "";

    if (endIndex > startIndex && startIndex > -1) {
      tagContent = content.substring(startIndex + 2 + tag.length(), endIndex);
    }

    return tagContent;
  }

  private SitemapCache createSitemapCache(String hsaIdentity, String modifyTimestamp, String... extraInformation) {
    SitemapCache unitCache = new SitemapCache();
    SitemapEntry entry = new SitemapEntry("http://external.com/visaenhet?hsaidentity=" + hsaIdentity, modifyTimestamp, "daily");
    for (int i = 0; extraInformation != null && i < extraInformation.length; i += 2) {
      entry.addExtraInformation(extraInformation[i], extraInformation[i + 1]);
    }
    unitCache.add(entry);
    return unitCache;
  }

  private static class SitemapCacheLoaderMock implements CacheLoader<SitemapCache> {
    private boolean cacheLoaded;
    private SitemapCache sitemapCache = new SitemapCache();

    public void setUnitCache(SitemapCache sitemapCache) {
      this.sitemapCache = sitemapCache;
    }

    public void assertCacheLoaded() {
      assertTrue("Cache was not loaded", cacheLoaded);
    }

    @Override
    public SitemapCache createEmptyCache() {
      return new SitemapCache();
    }

    @Override
    public SitemapCache loadCache() {
      this.cacheLoaded = true;
      return sitemapCache;
    }
  }
}
