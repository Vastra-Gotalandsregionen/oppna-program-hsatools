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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.SitemapCache;
import se.vgregion.kivtools.search.svc.SitemapEntry;
import se.vgregion.kivtools.search.svc.UnitCacheServiceImpl;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Implementation of the CacheLoader interface which populates a SitemapCache by using the {@link UnitCacheServiceImpl}.
 */
public class InternalSitemapCacheLoaderImpl implements CacheLoader<SitemapCache> {
  private final Log log = LogFactory.getLog(getClass());
  private final UnitCacheServiceImpl unitCacheService;
  private final String internalApplicationURL;
  private final String changeFrequency;
  private final SearchService searchService;

  /**
   * Constructs a new {@link InternalSitemapCacheLoaderImpl}.
   * 
   * @param unitCacheService The {@link UnitCacheServiceImpl} implementation to use to fetch units.
   * @param searchService The {@link SearchService} implementation to use to fetch persons.
   * @param internalApplicationURL The internal URL to the application.
   * @param changeFrequency The change frequency of the sitemap entries.
   */
  public InternalSitemapCacheLoaderImpl(final UnitCacheServiceImpl unitCacheService, final SearchService searchService, final String internalApplicationURL, String changeFrequency) {
    this.unitCacheService = unitCacheService;
    this.searchService = searchService;
    this.internalApplicationURL = internalApplicationURL;
    this.changeFrequency = changeFrequency;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SitemapCache loadCache() {
    SitemapCache cache = new SitemapCache();

    try {
      populateUnits(cache);
      populatePersons(cache);
    } catch (KivException e) {
      log.error("Exception while populating sitemap cache", e);
      cache = new SitemapCache();
    }

    return cache;
  }

  private void populatePersons(SitemapCache cache) throws KivException {
    List<String> persons = searchService.getAllPersonsId();
    for (String vgrid : persons) {
      try {
        SikSearchResultList<Person> searchResult = searchService.searchPersons("\"" + vgrid + "\"", 1);
        if (searchResult != null && searchResult.size() > 0) {
          Person person = searchResult.get(0);
          TimePoint lastmod = TimePoint.atGMT(1970, 1, 1, 0, 0, 0);
          if (person.getEmployments() != null) {
            for (Employment employment : person.getEmployments()) {
              if (lastmod.isBefore(employment.getModifyTimestamp())) {
                lastmod = employment.getModifyTimestamp();
              }
            }
          }
          SitemapEntry entry = new SitemapEntry(internalApplicationURL + "/visaperson?vgrid=" + person.getVgrId(), TimeUtil.formatDateW3C(lastmod.asJavaUtilDate()), this.changeFrequency);
          entry.addExtraInformation("hsaIdentity", person.getHsaIdentity());
          cache.add(entry);
        }
      } catch (KivException e) {
        log.error("Exception while getting person details for vgrId '" + vgrid + "'");
        throw e;
      }
    }
  }

  private void populateUnits(SitemapCache cache) {
    List<Unit> units = unitCacheService.getCache().getUnits();
    // Check if list of units is populated, otherwise we fill it up!
    if (units.size() < 1) {
      unitCacheService.reloadCache();
      units = unitCacheService.getCache().getUnits();
    }

    for (Unit unit : units) {
      String lastmod = getLastModifiedDateTime(unit.getModifyTimestampFormattedInW3CDatetimeFormat(), unit.getCreateTimestampFormattedInW3CDatetimeFormat());
      SitemapEntry entry = new SitemapEntry(internalApplicationURL + "/" + "visaenhet?hsaidentity=" + unit.getHsaIdentity(), lastmod, changeFrequency);
      entry.addExtraInformation("hsaIdentity", unit.getHsaIdentity());
      cache.add(entry);
    }
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
