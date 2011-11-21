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

package se.vgregion.kivtools.search.svc.comparators;

import java.util.Comparator;

import se.vgregion.kivtools.search.domain.Person;

/**
 * Comparator for Person instances.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class PersonNameComparator implements Comparator<Person> {
  /**
   * Compares two Person instances using surname and given name.
   * 
   * @param person1 The first person to compare.
   * @param person2 The second person to compare.
   * @return A value less than 0 if person1 < person2, 0 if person1 == person2 and a value greater than 0 if person1 > person2.
   */
  public int compare(Person person1, Person person2) {
    Person comparedPerson1 = person1;
    Person comparedPerson2 = person2;
    if (comparedPerson1 == null) {
      comparedPerson1 = new Person();
      comparedPerson1.setGivenName("");
      comparedPerson1.setSn("");
    }
    if (comparedPerson2 == null) {
      comparedPerson2 = new Person();
      comparedPerson2.setGivenName("");
      comparedPerson2.setSn("");
    }
    StringBuffer name1buf = new StringBuffer(comparedPerson1.getSn()).append(comparedPerson1.getGivenName());
    String name1 = name1buf.toString().toLowerCase();
    StringBuffer name2buf = new StringBuffer(comparedPerson2.getSn()).append(comparedPerson2.getGivenName());
    String name2 = name2buf.toString().toLowerCase();

    return name1.compareTo(name2);
  }
}
