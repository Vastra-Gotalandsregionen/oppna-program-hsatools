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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.UnitCache;

/**
 * Implementation of the CacheLoader interface which populates a UnitCache by using the {@link SearchService}.
 */
public class UnitCacheLoaderImpl implements CacheLoader<UnitCache> {
  private final Log log = LogFactory.getLog(getClass());
  private final List<Integer> businessClassificationCodesToFetch;
  private final SearchService searchService;

  /**
   * Constructs a new {@link UnitCacheLoaderImpl}.
   * 
   * @param searchService The {@link SearchService} implementation to use to fetch units.
   * @param businessClassificationCodesToFetch Comma-separated list of business classification codes to fetch to the cache.
   */
  public UnitCacheLoaderImpl(final SearchService searchService, String businessClassificationCodesToFetch) {
    this.searchService = searchService;
    this.businessClassificationCodesToFetch = createBusinessClassificationCodeList(businessClassificationCodesToFetch);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitCache loadCache() {
    UnitCache cache = new UnitCache();

    try {
      List<Unit> units = searchService.getAllUnits(this.businessClassificationCodesToFetch);
      for (Unit unit : units) {
        cache.add(unit);
      }
    } catch (KivException e) {
      log.error("Something went wrong when retrieving all units.", e);
    }

    return cache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitCache createEmptyCache() {
    return new UnitCache();
  }

  private List<Integer> createBusinessClassificationCodeList(String businessClassificationCodes) {
    List<Integer> result = new ArrayList<Integer>();
    List<String> tempList = Arrays.asList(businessClassificationCodes.split(","));
    for (String id : tempList) {
      if (id.length() > 0) {
        result.add(Integer.parseInt(id));
      }
    }
    return result;
  }
}
