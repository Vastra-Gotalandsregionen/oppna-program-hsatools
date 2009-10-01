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
package se.vgregion.kivtools.search.svc.domain;

import java.util.Comparator;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
public class PersonNameComparator implements Comparator<Person> {
  /*
   * @return <0 if person1<person2, 0 if person1==person2 and >0 if person1>person2.
   */
  public int compare(Person person1, Person person2) {
    if (person1 == null) {
      person1 = new Person();
      person1.setGivenName("");
      person1.setSn("");
    }
    if (person2 == null) {
      person2 = new Person();
      person2.setGivenName("");
      person2.setSn("");
    }
    StringBuffer name1buf = new StringBuffer(person1.getSn()).append(person1.getGivenName());
    String name1 = name1buf.toString().toLowerCase();
    StringBuffer name2buf = new StringBuffer(person2.getSn()).append(person2.getGivenName());
    String name2 = name2buf.toString().toLowerCase();

    return name1.compareTo(name2);
  }
}
