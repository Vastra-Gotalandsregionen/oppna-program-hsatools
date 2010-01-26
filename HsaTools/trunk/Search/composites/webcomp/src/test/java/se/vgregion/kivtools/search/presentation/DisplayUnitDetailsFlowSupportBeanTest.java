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
package se.vgregion.kivtools.search.presentation;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;

public class DisplayUnitDetailsFlowSupportBeanTest {

  private static final String HSA_ID = "hsaId";
  private static final String DN = "dn";
  DisplayUnitDetailsFlowSupportBean displayUnitDetailsFlowSupportBean = new DisplayUnitDetailsFlowSupportBean();
  Unit unitMock = new Unit();

  @Before
  public void setup() throws Exception {
    unitMock.setName("unitName");
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getUnitByDN(DN)).andReturn(unitMock);
    expect(searchServiceMock.getUnitByHsaId(HSA_ID)).andReturn(unitMock);
    replay(searchServiceMock);
    displayUnitDetailsFlowSupportBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testGetUnitDetails() {
    assertEquals(unitMock.getName(), displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID).getName());
  }

  @Test
  public void testUnitByDn() {
    assertEquals(unitMock.getName(), displayUnitDetailsFlowSupportBean.getUnitByDn(DN).getName());
  }

  @Test
  public void testExceptionHandling() throws Exception {
    SearchService searchServiceMock = createMock(SearchService.class);
    expect(searchServiceMock.getUnitByDN(DN)).andThrow(new KivException("Test"));
    expect(searchServiceMock.getUnitByHsaId(HSA_ID)).andThrow(new KivException("Test"));
    replay(searchServiceMock);
    displayUnitDetailsFlowSupportBean.setSearchService(searchServiceMock);
    assertNotNull(displayUnitDetailsFlowSupportBean.getUnitByDn(DN));
    assertNotNull(displayUnitDetailsFlowSupportBean.getUnitDetails(HSA_ID));
  }
}
