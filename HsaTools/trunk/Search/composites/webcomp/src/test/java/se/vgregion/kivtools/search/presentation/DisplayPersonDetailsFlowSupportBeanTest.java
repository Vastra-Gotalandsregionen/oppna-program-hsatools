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

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;

public class DisplayPersonDetailsFlowSupportBeanTest {

  private static final String PERSON_DN = "cn=Nina Kanin,ou=abc,ou=def";
  private static final String VGR_ID = "vgrId";
  private final DisplayPersonDetailsFlowSupportBean displayPersonDetailsFlowSupportBean = new DisplayPersonDetailsFlowSupportBean();
  private final Person person = new Person();
  private final SearchServiceMock searchServiceMock = new SearchServiceMock();
  private final ServletContext servletContext = new MockServletContext();
  private final MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
  private final MockHttpServletResponse response = new MockHttpServletResponse();
  private final ServletExternalContext externalContext = new ServletExternalContext(servletContext, request, response);

  @Before
  public void setup() throws Exception {
    searchServiceMock.setPerson(person);
    displayPersonDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetPersonDetails() throws Exception {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID, externalContext);
    assertEquals(person, person);
  }

  @Test
  public void testGetPersonDetailsPersonAlreadyGotEmployments() throws Exception {
    person.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID, externalContext);
    assertEquals(person, person);
  }

  @Test
  public void getPersonDetailsSets404StatusCodeAndThrowsExceptionOnMissingPerson() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivNoDataFoundException("exception"));
    try {
      displayPersonDetailsFlowSupportBean.getPersonDetails(VGR_ID, externalContext);
      fail("KivException should be thrown");
    } catch (KivException e) {
      assertEquals("http status code", 404, response.getStatus());
    }
  }

  @Test
  public void testGetPersonDetailsByDn() throws KivException {
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN, externalContext);
    assertNotNull(person);
  }

  @Test
  public void testGetPersonDetailsByDnPersonAlreadyGotEmployments() throws Exception {
    person.setEmployments(new ArrayList<Employment>());
    Person person = displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN, externalContext);
    assertEquals(person, person);
  }

  @Test
  public void getPersonDetailsByDnSets404StatusCodeAndThrowsExceptionOnMissingPerson() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivNoDataFoundException("exception"));
    try {
      displayPersonDetailsFlowSupportBean.getPersonDetailsByDn(PERSON_DN, externalContext);
      fail("KivException should be thrown");
    } catch (KivException e) {
      assertEquals("http status code", 404, response.getStatus());
    }
  }
}
