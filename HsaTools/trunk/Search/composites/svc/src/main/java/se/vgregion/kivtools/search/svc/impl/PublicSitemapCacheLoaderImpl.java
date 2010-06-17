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

import java.util.List;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.SitemapCache;
import se.vgregion.kivtools.search.svc.SitemapCache.EntryType;
import se.vgregion.kivtools.search.svc.SitemapEntry;
import se.vgregion.kivtools.search.svc.UnitCacheServiceImpl;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Implementation of the CacheLoader interface which populates a SitemapCache by using the {@link UnitCacheServiceImpl}.
 */
public class PublicSitemapCacheLoaderImpl implements CacheLoader<SitemapCache> {
  private final UnitCacheServiceImpl unitCacheService;
  private final String externalApplicationURL;

  /**
   * Constructs a new {@link PublicSitemapCacheLoaderImpl}.
   * 
   * @param unitCacheService The {@link UnitCacheServiceImpl} implementation to use to fetch units.
   * @param externalApplicationURL The external URL to the application.
   */
  public PublicSitemapCacheLoaderImpl(final UnitCacheServiceImpl unitCacheService, String externalApplicationURL) {
    this.unitCacheService = unitCacheService;
    this.externalApplicationURL = externalApplicationURL;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SitemapCache loadCache() {
    SitemapCache cache = new SitemapCache();

    List<Unit> units = unitCacheService.getCache().getUnits();
    // Check if list of units is populated, otherwise we fill it up!
    if (units.size() < 1) {
      unitCacheService.reloadCache();
      units = unitCacheService.getCache().getUnits();
    }

    for (Unit unit : units) {
      String lastmod = getLastModifiedDateTime(unit.getModifyTimestampFormattedInW3CDatetimeFormat(), unit.getCreateTimestampFormattedInW3CDatetimeFormat());
      SitemapEntry entry = new SitemapEntry(externalApplicationURL + "/" + "visaenhet?hsaidentity=" + unit.getHsaIdentity(), lastmod, "weekly");
      cache.add(entry, EntryType.UNIT);
    }

    return cache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SitemapCache createEmptyCache() {
    return new SitemapCache();
  }

  private String getLastModifiedDateTime(String modifyTimestamp, String createTimestamp) {
    String lastModified = modifyTimestamp;

    if (StringUtil.isEmpty(modifyTimestamp)) {
      lastModified = createTimestamp;
    }

    return lastModified;
  }
}
