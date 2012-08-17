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

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.UnitCache;

import com.domainlanguage.time.TimePoint;

public class UnitCacheLoaderMock implements CacheLoader<UnitCache> {
  @Override
  public UnitCache createEmptyCache() {
    return new UnitCache();
  }

  @Override
  public UnitCache loadCache() {
    UnitCache unitCache = new UnitCache();

    unitCache.add(this.createUnit("ABC-123", "Mölndals Sjukhus", TimePoint.atMidnightGMT(2010, 2, 10), null));
    unitCache.add(this.createUnit("XYZ-987", "Angereds vårdcentral", TimePoint.atMidnightGMT(2010, 2, 10), TimePoint.atMidnightGMT(2010, 2, 16)));
    unitCache.add(this.createUnit("JKL-654", "Slottsskogens vårdcentral", TimePoint.atMidnightGMT(2010, 2, 16), TimePoint.atMidnightGMT(2010, 2, 16)));

    return unitCache;
  }

  private Unit createUnit(String hsaIdentity, String name, TimePoint createTimestamp, TimePoint modifyTimestamp) {
    Unit unit = new Unit();
    unit.setHsaIdentity(hsaIdentity);
    unit.setName(name);
    unit.setCreateTimestamp(createTimestamp);
    unit.setModifyTimestamp(modifyTimestamp);
    return unit;
  }
}