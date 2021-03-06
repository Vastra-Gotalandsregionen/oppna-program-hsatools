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

package se.vgregion.kivtools.search.svc.impl.mock;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;

public class SearchServiceMockImplTest {

  private SearchServiceMockImpl searchService;

  @Before
  public void setUp() throws Exception {
    searchService = new SearchServiceMockImpl();
  }

  @Test
  public void testGetEmployments() throws KivException {
    SikSearchResultList<Employment> employments = searchService.getEmployments("cn=kalle,ou=VGR IT,o=VGR");
    assertEquals(5, employments.size());
  }

  @Test
  public void testGetPersonById() throws KivException {
    Person person = searchService.getPersonById("anders1");
    assertEquals("Anders Sandin Asplund", person.getFullName());
    person = searchService.getPersonById("hangy2");
    assertEquals("Hans Gyllensten", person.getFullName());
    person = searchService.getPersonById("pj3");
    assertEquals("Per-Johan Andersson", person.getFullName());
  }

  @Test
  public void testGetUnitByHsaId() throws KivException {
    Unit unit = searchService.getUnitByHsaId("ABC001");
    assertEquals("VGR IT", unit.getName());
    unit = searchService.getUnitByHsaId("ABC002");
    assertEquals("Sahlgrenska Sjukhuset", unit.getName());
    unit = searchService.getUnitByHsaId("ABC003");
    assertEquals("Uddevalla vårdcentral", unit.getName());
  }

  @Test
  public void testSearchPersonsStringInt() throws KivException {
    SikSearchResultList<Person> persons = searchService.searchPersons("test", 10);
    assertEquals(0, persons.size());

    persons = searchService.searchPersons("and", 10);
    assertEquals(1, persons.size());
  }

  @Test
  public void testSearchUnits() throws KivException {
    SikSearchResultList<Unit> units = searchService.searchUnits(null, 10);
    assertEquals(3, units.size());
  }

  @Test
  public void testGetAllPersonsId() throws KivException {
    List<String> allPersonsId = searchService.getAllPersonsId();
    assertEquals(3, allPersonsId.size());
    assertTrue(allPersonsId.contains("anders1"));
    assertTrue(allPersonsId.contains("hangy2"));
    assertTrue(allPersonsId.contains("pj3"));
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    List<String> allUnitsHsaIdentity = searchService.getAllUnitsHsaIdentity();
    assertEquals(3, allUnitsHsaIdentity.size());
    assertTrue(allUnitsHsaIdentity.contains("ABC001"));
    assertTrue(allUnitsHsaIdentity.contains("ABC002"));
    assertTrue(allUnitsHsaIdentity.contains("ABC003"));
  }

  @Test
  public void testGetUnitByDN() throws KivException {
    Unit unit = searchService.getUnitByDN(null);
    assertEquals("VGR IT", unit.getName());
  }

  @Test
  public void testSearchAdvancedUnits() throws KivException {
    SikSearchResultList<Unit> units = searchService.searchAdvancedUnits(null, 0, null, false);
    assertEquals(3, units.size());
  }

  @Test
  public void testGetAllUnitsHsaIdentityListOfInteger() throws KivException {
    List<String> allUnitsHsaIdentity = searchService.getAllUnitsHsaIdentity(false);
    assertEquals(3, allUnitsHsaIdentity.size());
    assertTrue(allUnitsHsaIdentity.contains("ABC001"));
    assertTrue(allUnitsHsaIdentity.contains("ABC002"));
    assertTrue(allUnitsHsaIdentity.contains("ABC003"));
  }

  @Test
  public void testGetEmploymentsForPerson() {
    assertNull(searchService.getEmploymentsForPerson(null));
  }

  @Test
  public void testSearchPersonsByDn() throws KivException {
    assertNull(searchService.searchPersonsByDn(null, 0));
  }

  @Test
  public void testGetSubUnits() throws KivException {
    assertNull(searchService.getSubUnits(null, 0));
  }

  @Test
  public void testGetPersonsForUnits() throws KivException {
    assertNull(searchService.getPersonsForUnits(null, 0));
  }

  @Test
  public void testSearchPersonsSearchPersonCriterionsInt() throws KivException {
    SikSearchResultList<Person> persons = searchService.searchPersons((SearchPersonCriterions) null, 10);
    assertEquals(3, persons.size());
  }

  @Test
  public void testGetPersonByDn() {
    assertNull(searchService.getPersonByDn(null));
  }

  @Test
  public void testGetProfileImageByDn() throws KivException {
    assertNull(searchService.getProfileImageByDn(null));
  }

  @Test
  public void testGetAllUnits() throws KivException {
    List<Unit> units = searchService.getAllUnits(false);
    assertEquals(3, units.size());
  }

  @Test
  public void testGetAllPersons() throws KivException {
    List<Person> allPersons = searchService.getAllPersons();
    assertEquals(3, allPersons.size());
  }
}
