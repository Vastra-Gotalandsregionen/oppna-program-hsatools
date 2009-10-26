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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;

public class PersonNameWeightedComparatorTest {

  private Person alexanderssonlisa;
  private Person andersmonalisa;
  private Person anderssonisa;
  private Person sanderssonmona;

  @Before
  public void setUp() throws Exception {
    alexanderssonlisa = createPerson("Lisa", "Alexandersson");
    sanderssonmona = createPerson("Mona", "Sandersson");
    andersmonalisa = createPerson("Monalisa", "Anders");
    anderssonisa = createPerson("Isa", "andersson");
  }

  @Test
  public void testCompareSamePerson() {
    assertEquals(0, new PersonNameWeightedComparator("", "").compare(alexanderssonlisa, alexanderssonlisa));
  }

  @Test
  public void testCompareMonaIsa() {
    assertTrue(new PersonNameWeightedComparator("", "").compare(sanderssonmona, anderssonisa) > 0);
  }

  @Test
  public void testCompareMonalisaIsa() {
    assertTrue(new PersonNameWeightedComparator("", "").compare(andersmonalisa, anderssonisa) < 0);
  }

  @Test
  public void testCompareLisaMonalisaSearchedForMonalisa() {
    assertTrue(new PersonNameWeightedComparator("monalisa", "").compare(alexanderssonlisa, andersmonalisa) > 0);
  }

  @Test
  public void testCompareIsaMonalisaSearchedForIsa() {
    assertTrue(new PersonNameWeightedComparator("isa", "").compare(andersmonalisa, anderssonisa) > 0);
  }

  @Test
  public void testCompareIsaLisaSearchedForAndersson() {
    assertTrue(new PersonNameWeightedComparator("", "andersson").compare(anderssonisa, alexanderssonlisa) < 0);
  }

  @Test
  public void testCompareMonalisaIsaSearchedForIsa() {
    assertTrue(new PersonNameWeightedComparator("isa", "").compare(anderssonisa, andersmonalisa) < 0);
  }

  @Test
  public void testCompareLisaIsaSearchedForAndersson() {
    assertTrue(new PersonNameWeightedComparator("", "andersson").compare(alexanderssonlisa, anderssonisa) > 0);
  }

  private Person createPerson(String givenName, String surname) {
    Person person = new Person();
    person.setGivenName(givenName);
    person.setSn(surname);
    return person;
  }
}
