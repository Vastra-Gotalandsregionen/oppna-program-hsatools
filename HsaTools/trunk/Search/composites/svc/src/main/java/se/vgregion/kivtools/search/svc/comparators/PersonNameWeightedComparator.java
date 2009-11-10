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
package se.vgregion.kivtools.search.svc.comparators;

import java.text.Collator;
import java.util.Comparator;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.util.Arguments;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Comparator for sorting persons where results matching the search string exactly is sorted higher than partial matches.
 * 
 * @author Joakim Olsson & David Bennehult
 */
public class PersonNameWeightedComparator implements Comparator<Person> {
  private final String givenName;
  private final String surname;

  /**
   * Constructs a new PersonNameWeightedComparator with the provided search parameters.
   * 
   * @param givenName The givenName searched for.
   * @param surname The surname searched for.
   */
  public PersonNameWeightedComparator(String givenName, String surname) {
    this.givenName = StringUtil.emptyStringIfNull(givenName).trim();
    this.surname = StringUtil.emptyStringIfNull(surname).trim();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(Person person1, Person person2) {
    Arguments.notNull("person1", person1);
    Arguments.notNull("person2", person2);

    int weight = 0;
    if (givenName.equalsIgnoreCase(person1.getGivenName())) {
      weight -= 1000;
    }
    if (givenName.equalsIgnoreCase(person2.getGivenName())) {
      weight += 1000;
    }
    if (surname.equalsIgnoreCase(person1.getSn())) {
      weight -= 100;
    }
    if (surname.equalsIgnoreCase(person2.getSn())) {
      weight += 100;
    }

    // Concatenate and make lower case.
    String name1 = StringUtil.emptyStringIfNull(person1.getSn()) + "<->" + StringUtil.emptyStringIfNull(person1.getGivenName());
    name1 = name1.toLowerCase();
    String name2 = StringUtil.emptyStringIfNull(person2.getSn()) + "<->" + StringUtil.emptyStringIfNull(person2.getGivenName());
    name2 = name2.toLowerCase();

    return weight + Collator.getInstance().compare(name1, name2);
  }
}
