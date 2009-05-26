/**
 * Copyright 2009 Västa Götalandsregionen
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

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.presentation.forms.UnitSearchSimpleForm;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Unit;

public class SearchUnitFlowSupportBeanTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	// TODO Should be mocked in order to build without LDAP server
	public void testDoSearch() throws Exception {
		SearchService ssMock = EasyMock.createNiceMock(SearchService.class);
		SearchUnitFlowSupportBean bean = new SearchUnitFlowSupportBean();
		bean.setSearchService(ssMock);
		bean.setMaxSearchResult(200);
		bean.setPageSize(25);

		// doSearch corresponds to
		// expect one search - mock record mode
		Unit unit = new Unit();
		unit.setName("my unit");
		SikSearchResultList<Unit> unitMockList = new SikSearchResultList<Unit>();
		unitMockList.add(unit);
		EasyMock.expect(ssMock.searchUnits(unit, 200)).andReturn(unitMockList);
		EasyMock.replay(ssMock);

		// run some test
		SikSearchResultList<Unit> ul = null;
		UnitSearchSimpleForm theForm = new UnitSearchSimpleForm();
		theForm.setUnitName("my unit");
		try {
			ul = bean.doSearch(theForm);
			Assert.fail("A KivNoDataFoundException should have been thrown");
		} catch (KivNoDataFoundException e) {
			// that is ok
		}

		// verify
		Assert.assertNull(ul);
		Assert.assertEquals(1, unitMockList.size());
		// EasyMock.verify(ssMock);
	}
	
	// This is an integration test against a working LDAP server
	@Test
	@Ignore
	public void testGetSubUnits(){
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("services-config.xml");
		SearchUnitFlowSupportBean searchUnitFlowSupportBean = (SearchUnitFlowSupportBean) applicationContext.getBean("Search.SearchUnitFlowSupportBean");
		SikSearchResultList<Unit> subUnits = searchUnitFlowSupportBean.getSubUnits("SE2321000131-E000000000117");
		System.out.println("");
	}
}
