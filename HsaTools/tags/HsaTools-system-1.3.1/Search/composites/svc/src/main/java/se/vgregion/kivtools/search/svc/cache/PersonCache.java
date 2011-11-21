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

package se.vgregion.kivtools.search.svc.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for persons.
 * 
 * @author Joakim Olsson
 */
public class PersonCache {
  private final List<Person> persons = new ArrayList<Person>();

  public List<Person> getPersons() {
    return Collections.unmodifiableList(this.persons);
  }

  /**
   * Adds a new person to the cache.
   * 
   * @param person The person to add to the cache.
   */
  public void add(Person person) {
    Arguments.notNull("person", person);

    if (!this.persons.contains(person)) {
      this.persons.add(person);
    }
  }
}
