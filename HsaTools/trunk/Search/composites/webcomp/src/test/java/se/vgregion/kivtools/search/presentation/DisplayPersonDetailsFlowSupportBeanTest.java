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

package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;

public class DisplayPersonDetailsFlowSupportBeanTest {

  private static final String PERSON_DN = "cn=Nina Kanin,ou=abc,ou=def";
  private static final String VGR_ID = "vgrId";
  private DisplayPersonDetailsFlowSupportBean displayPersonDetailsFlowSupportBean = new DisplayPersonDetailsFlowSupportBean();
  private Person personMock = new Person();
  private SearchServiceMock searchServiceMock;

  @Before
  public void setup() throws Exception {
    searchServiceMock = new SearchServiceMock();
    searchServiceMock.setPerson(personMock);
    displayPersonDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetPersonDetails() throws Exception {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
    assertEquals(personMock, person);
  }

  @Test
  public void testGetPersonDetailsPersonAlreadyGotEmployments() throws Exception {
    personMock.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
    assertEquals(personMock, person);
  }

  @Test(expected = KivException.class)
  public void getPersonDetailsThrowsExceptionOnMissingPerson() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivException("exception"));
    displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID);
  }

  @Test
  public void testGetPersonDetailsByDn() {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertNotNull(person);
  }

  @Test
  public void testGetPersonDetailsByDnPersonAlreadyGotEmployments() throws Exception {
    personMock.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertEquals(personMock, person);
  }

  @Test
  public void testGetPersonDetailsByDnExceptionHandling() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivException("exception"));
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN);
    assertNotNull(person);
  }
}
