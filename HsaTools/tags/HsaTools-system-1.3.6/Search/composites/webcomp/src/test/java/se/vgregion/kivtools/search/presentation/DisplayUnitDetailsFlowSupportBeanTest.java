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

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;

public class DisplayUnitDetailsFlowSupportBeanTest {
  private static final String HSA_ID = "hsaId";
  private static final String UNIT_DN = "cn=dn,o=VGR";
  private final DisplayUnitDetailsFlowSupportBean displayUnitDetailsFlowSupportBean = new DisplayUnitDetailsFlowSupportBean();
  private final Unit unit = new Unit();
  private final SearchServiceMock searchServiceMock = new SearchServiceMock();
  private final ServletContext servletContext = new MockServletContext();
  private final MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
  private final MockHttpServletResponse response = new MockHttpServletResponse();
  private final ServletExternalContext externalContext = new ServletExternalContext(servletContext, request, response);

  @Before
  public void setup() throws Exception {
    unit.setHsaIdentity(HSA_ID);
    unit.setName("unitName");
    unit.setDn(DN.createDNFromString(UNIT_DN));
    searchServiceMock.addUnit(unit);
    displayUnitDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetUnitDetails() throws KivException {
    assertEquals(unit.getName(), displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID, externalContext).getName());
  }

  @Test
  public void getUnitDetailsSets404StatusCodeAndThrowsExceptionOnMissingUnit() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivNoDataFoundException("exception"));
    try {
      displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID, externalContext);
      fail("KivException should be thrown");
    } catch (KivException e) {
      assertEquals("http status code", 404, response.getStatus());
    }
  }

  @Test
  public void testUnitByDn() throws KivException {
    assertEquals(unit.getName(), displayUnitDetailsFlowSupportBean.getUnitByDn(UNIT_DN, externalContext).getName());
  }

  @Test
  public void getUnitByDnSets404StatusCodeAndThrowsExceptionOnMissingUnit() throws Exception {
    searchServiceMock.addExceptionToThrow(new KivNoDataFoundException("exception"));
    try {
      displayUnitDetailsFlowSupportBean.getUnitByDn(UNIT_DN, externalContext);
      fail("KivException should be thrown");
    } catch (KivException e) {
      assertEquals("http status code", 404, response.getStatus());
    }
  }
}
