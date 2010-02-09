/**
 * Copyright 2009 Västra Götalandsregionen
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
package se.vgregion.kivtools.search.svc;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Cache service for various information caching.
 * 
 * @author Joakim Olsson
 */
public class CacheService {
  private PersonNameCacheLoader personNameCacheLoader;
  private UnitNameCacheLoader unitNameCacheLoader;
  private TitleCacheLoader titleCacheLoader;
  private AtomicReference<PersonNameCache> personNameCache = new AtomicReference<PersonNameCache>(new PersonNameCache());
  private AtomicReference<UnitNameCache> unitNameCache = new AtomicReference<UnitNameCache>(new UnitNameCache());
  private AtomicReference<TitleCache> titleCache = new AtomicReference<TitleCache>(new TitleCache());

  public void setPersonNameCacheLoader(PersonNameCacheLoader personNameCacheLoader) {
    this.personNameCacheLoader = personNameCacheLoader;
  }

  public void setUnitNameCacheLoader(UnitNameCacheLoader unitNameCacheLoader) {
    this.unitNameCacheLoader = unitNameCacheLoader;
  }

  public void setTitleCacheLoader(TitleCacheLoader titleCacheLoader) {
    this.titleCacheLoader = titleCacheLoader;
  }

  /**
   * Reloads the caches from LDAP using the cache loader instances.
   */
  public void reloadCaches() {
    personNameCache.set(personNameCacheLoader.loadCache());
    unitNameCache.set(unitNameCacheLoader.loadCache());
    titleCache.set(titleCacheLoader.loadCache());
  }

  public PersonNameCache getPersonNameCache() {
    return personNameCache.get();
  }

  public UnitNameCache getUnitNameCache() {
    return unitNameCache.get();
  }

  public TitleCache getTitleCache() {
    return titleCache.get();
  }
}
