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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.PersonCache;

/**
 * Implementation of the CacheLoader interface which populates a PersonCache by using the {@link SearchService}.
 */
public class PersonCacheLoaderImpl implements CacheLoader<PersonCache> {
  private final Log log = LogFactory.getLog(this.getClass());
  private final SearchService searchService;

  /**
   * Constructs a new {@link PersonCacheLoaderImpl}.
   * 
   * @param searchService The {@link SearchService} implementation to use to fetch persons.
   */
  public PersonCacheLoaderImpl(final SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonCache loadCache() {
    PersonCache cache = new PersonCache();

    try {
      List<Person> persons = this.searchService.getAllPersons();
      for (Person person : persons) {
        if (person.getEmployments() == null) {
          person.setEmployments(this.searchService.getEmployments(person.getDn()));
        }
        cache.add(person);
      }
    } catch (KivException e) {
      this.log.error("Something went wrong when retrieving all persons.", e);
    }

    return cache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonCache createEmptyCache() {
    return new PersonCache();
  }
}
