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

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.SitemapCache;
import se.vgregion.kivtools.search.svc.cache.SitemapCache.EntryType;
import se.vgregion.kivtools.search.svc.impl.cache.SitemapCacheServiceImpl;
import se.vgregion.kivtools.svc.sitemap.Unit;

public class SitemapSupportBeanTest {
  private SitemapCacheLoaderMock sitemapCacheLoader = new SitemapCacheLoaderMock();
  private SitemapCacheServiceImpl sitemapCacheService = new SitemapCacheServiceImpl(sitemapCacheLoader);
  private SitemapGenerator internalSitemapGenerator = new InternalSitemapGenerator();
  private SitemapGenerator externalSitemapGenerator = new ExternalSitemapGenerator();
  private SitemapSupportBean internalSitemapSupportBean = new SitemapSupportBean(sitemapCacheService, internalSitemapGenerator);
  private SitemapSupportBean externalSitemapSupportBean = new SitemapSupportBean(sitemapCacheService, externalSitemapGenerator);

  @Test
  public void cacheIsReloadedIfEmpty() {
    internalSitemapSupportBean.getSitemapContent("true", "true");
    sitemapCacheLoader.assertCacheLoaded();
  }

  @Test
  public void locAndLastmodUsesLocationAndLastModified() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00").buildSitemapCache());
    String sitemapContent = internalSitemapSupportBean.getSitemapContent("true", "true");
    String loc = getTagContent(sitemapContent, "loc");
    assertEquals("http://external.com/visaenhet?hsaidentity=abc-123", loc);
    String lastmod = getTagContent(sitemapContent, "lastmod");
    assertEquals("2010-02-01T01:00:00+01:00", lastmod);
  }

  @Test
  public void changeFrequencyIsUsedForChangefreqTag() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00").buildSitemapCache());
    String sitemapContent = internalSitemapSupportBean.getSitemapContent("true", "true");
    String changefreq = getTagContent(sitemapContent, "changefreq");
    assertEquals("daily", changefreq);
  }

  @Test
  public void extraInformationIsAddedIfAvailable() {
    Unit extraInformation = new Unit();
    extraInformation.setHsaIdentity("abc-123");
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00", extraInformation).buildSitemapCache());
    String sitemapContent = internalSitemapSupportBean.getSitemapContent("true", "true");

    String hsaIdentity = getTagContent(sitemapContent, "ns2:hsaIdentity");
    assertEquals("abc-123", hsaIdentity);
  }

  @Test
  public void noExtraInformationIsAddedForExternalSitemap() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00", "hsaIdentity", "abc-123").buildSitemapCache());
    String sitemapContent = externalSitemapSupportBean.getSitemapContent("true", "true");

    assertEquals("hsa namespace found in sitemap content", -1, sitemapContent.indexOf("hsa:"));
  }

  @Test
  public void onlyPersonsArePresentInFileIfOnlyPersonsAreRequested() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00").withPerson("def-456", "2010-04-24T05:11:23+01:00").buildSitemapCache());
    String sitemapContent = externalSitemapSupportBean.getSitemapContent("true", "false");

    assertFalse("units present", sitemapContent.contains("visaenhet"));
    assertTrue("persons not present", sitemapContent.contains("visaperson"));
  }

  @Test
  public void onlyUnitsArePresentInFileIfOnlyUnitsAreRequested() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00").withPerson("def-456", "2010-04-24T05:11:23+01:00").buildSitemapCache());
    String sitemapContent = externalSitemapSupportBean.getSitemapContent("false", "true");

    assertTrue("units not present", sitemapContent.contains("visaenhet"));
    assertFalse("persons present", sitemapContent.contains("visaperson"));
  }

  @Test
  public void bothUnitsAndPersonsArePresentInFileIfNoSpecificTypeIsRequested() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00").withPerson("def-456", "2010-04-24T05:11:23+01:00").buildSitemapCache());
    String sitemapContent = externalSitemapSupportBean.getSitemapContent("", "");

    assertTrue("units not present", sitemapContent.contains("visaenhet"));
    assertTrue("persons not present", sitemapContent.contains("visaperson"));
  }

  @Test(expected = RuntimeException.class)
  public void exceptionIsThrownOnInvalidExtraInformation() {
    sitemapCacheLoader.setSitemapCache(new SitemapCacheBuilder().withUnit("abc-123", "2010-02-01T01:00:00+01:00", "invalid extra content").buildSitemapCache());
    internalSitemapSupportBean.getSitemapContent("true", "true");
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

  private static class SitemapCacheBuilder {
    private final SitemapCache sitemapCache = new SitemapCache();

    public SitemapCacheBuilder withPerson(final String hsaIdentity, String modifyTimestamp, Object... extraInformation) {
      createAndAddEntry(hsaIdentity, "http://external.com/visaperson?hsaidentity=" + hsaIdentity, modifyTimestamp, EntryType.PERSON, extraInformation);
      return this;
    }

    public SitemapCache buildSitemapCache() {
      return this.sitemapCache;
    }

    public SitemapCacheBuilder withUnit(final String hsaIdentity, final String modifyTimestamp, final Object... extraInformation) {
      createAndAddEntry(hsaIdentity, "http://external.com/visaenhet?hsaidentity=" + hsaIdentity, modifyTimestamp, EntryType.UNIT, extraInformation);
      return this;
    }

    private void createAndAddEntry(String hsaIdentity, String url, String modifyTimestamp, EntryType entryType, Object... extraInformation) {
      SitemapEntry entry = new SitemapEntry(url, modifyTimestamp, "daily");
      for (int i = 0; extraInformation != null && i < extraInformation.length; i++) {
        entry.addExtraInformation(extraInformation[i]);
      }
      sitemapCache.add(entry, entryType);
    }
  }

  private static class SitemapCacheLoaderMock implements CacheLoader<SitemapCache> {
    private boolean cacheLoaded;
    private SitemapCache sitemapCache = new SitemapCache();

    public void setSitemapCache(SitemapCache sitemapCache) {
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
