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

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.PersonNameCache;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Implementation of the PersonNameCacheLoader using the PersonCacheServiceImpl.
 * 
 * @author Joakim Olsson
 */
public class PersonNameCacheLoaderImpl implements CacheLoader<PersonNameCache> {
  private final PersonCacheServiceImpl personCacheService;

  public PersonNameCacheLoaderImpl(final PersonCacheServiceImpl personCacheService) {
    this.personCacheService = personCacheService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonNameCache loadCache() {
    PersonNameCache personNameCache = new PersonNameCache();

    List<Person> persons = this.personCacheService.getCache().getPersons();
    // Check if list of persons is populated, otherwise we fill it up!
    if (persons.isEmpty()) {
      this.personCacheService.reloadCache();
      persons = this.personCacheService.getCache().getPersons();
    }

    for (Person person : persons) {
      personNameCache.add(StringUtil.emptyStringIfNull(person.getGivenName()), StringUtil.emptyStringIfNull(person.getSn()));
    }

    return personNameCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonNameCache createEmptyCache() {
    return new PersonNameCache();
  }
}
