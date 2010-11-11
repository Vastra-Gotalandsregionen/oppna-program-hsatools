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

import java.util.List;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.UnitNameCache;

/**
 * Implementation of the UnitNameCacheLoader using the UnitCacheServiceImpl.
 * 
 * @author Joakim Olsson
 */
public class UnitNameCacheLoaderImpl implements CacheLoader<UnitNameCache> {
  private final UnitCacheServiceImpl unitCacheService;

  public UnitNameCacheLoaderImpl(UnitCacheServiceImpl unitCacheServiceImpl) {
    this.unitCacheService = unitCacheServiceImpl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitNameCache loadCache() {
    UnitNameCache unitNameCache = new UnitNameCache();

    List<Unit> units = this.unitCacheService.getCache().getUnits();
    // Check if list of units is populated, otherwise we fill it up!
    if (units.isEmpty()) {
      this.unitCacheService.reloadCache();
      units = this.unitCacheService.getCache().getUnits();
    }

    for (Unit unit : units) {
      unitNameCache.add(unit.getName());
    }

    return unitNameCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitNameCache createEmptyCache() {
    return new UnitNameCache();
  }
}
