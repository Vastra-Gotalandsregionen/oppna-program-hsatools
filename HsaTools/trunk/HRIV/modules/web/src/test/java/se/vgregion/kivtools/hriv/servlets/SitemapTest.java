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
package se.vgregion.kivtools.hriv.servlets;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

import com.domainlanguage.time.TimePoint;

import se.vgregion.kivtools.hriv.presentation.SearchUnitFlowSupportBean;
import se.vgregion.kivtools.hriv.presentation.SettingsBean;
import se.vgregion.kivtools.hriv.servlets.Sitemap;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SearchService;

public class SitemapTest {

  private static final String HTTP_EXTERNAL_URL = "http://externalurl/";
  private static final String RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n<url>\n<loc>http://externalurl//visaenhet?hsaidentity=hsaId_0</loc>\n<lastmod>1970-01-01T01:00:02+01:00</lastmod>\n<changefreq>weekly</changefreq>\n<priority>0.5</priority>\n</url>\n<url>\n<loc>http://externalurl//visaenhet?hsaidentity=hsaId_1</loc>\n<lastmod>1970-01-01T01:00:02+01:00</lastmod>\n<changefreq>weekly</changefreq>\n<priority>0.5</priority>\n</url>\n</urlset>";
  private static final String SCHEME = "hhtp";
  private static final String SERVERNAME = "localhost";
  private static final int SERVER_PORT = 8080;
  private static final String CONTEXTPATH = "test";
  private static final int NBR_OF_UNITS = 2;
  private Sitemap sitemap;
  
  private ArrayList<Unit> unitMocks = new ArrayList<Unit>();

  @Before
  public void setup() throws Exception {
    // Create mock objects.
    final ServletContext servletContextMock = createMock(ServletContext.class);
    final ServletConfig servletConfigMock = createMock(ServletConfig.class);
    final WebApplicationContext webApplicationContextmock = createMock(WebApplicationContext.class);
    final SearchUnitFlowSupportBean searchUnitFlowSupportBeanMock = createMock(SearchUnitFlowSupportBean.class);
    final SearchService searchServiceMock = createMock(SearchService.class);
    final SettingsBean SettingsBeanMock = createMock(SettingsBean.class);
    
    makeThreadSafe(SettingsBeanMock, true);
    makeThreadSafe(searchServiceMock, true);
    makeThreadSafe(searchUnitFlowSupportBeanMock, true);
    makeThreadSafe(webApplicationContextmock, true);
    makeThreadSafe(servletContextMock, true);
    makeThreadSafe(servletConfigMock, true);
    makeThreadSafe(searchUnitFlowSupportBeanMock, true);
    
    expect(webApplicationContextmock.getBean("Search_SearchUnitFlowSupportBean")).andReturn(searchUnitFlowSupportBeanMock);
    expectLastCall().anyTimes();
    
    expect(webApplicationContextmock.getBean("Search_SearchService")).andReturn(searchServiceMock);
    expectLastCall().anyTimes();
    
    searchUnitFlowSupportBeanMock.setSearchService(searchServiceMock);
    expectLastCall().anyTimes();
    
    expect(searchUnitFlowSupportBeanMock.getAllUnitsHsaIdentity(true)).andReturn(createUnitHsaIds());
    expectLastCall().anyTimes();
   
    searchUnitFlowSupportBeanMock.setUnitsCacheComplete(false);
    expectLastCall().anyTimes();
    
    searchUnitFlowSupportBeanMock.setUnits(createUnitmocks());
    expectLastCall().anyTimes();
    
    searchUnitFlowSupportBeanMock.populateCoordinates();
    expectLastCall().anyTimes();
    
    searchUnitFlowSupportBeanMock.setUnitsCacheComplete(true);
    expectLastCall().anyTimes();

    expect(webApplicationContextmock.getBean("Search_SettingsContainer")).andReturn(SettingsBeanMock);
    expectLastCall().anyTimes();
    
    expect(servletConfigMock.getServletContext()).andReturn(servletContextMock);
    expectLastCall().anyTimes();
    
    expect(servletContextMock.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT")).andReturn(webApplicationContextmock);
    expectLastCall().anyTimes();
    
    expect(searchUnitFlowSupportBeanMock.getSearchService()).andReturn(searchServiceMock);
    expectLastCall().anyTimes();
    for (int i = 0; i < NBR_OF_UNITS; i++) {
      expect(searchServiceMock.getUnitByHsaId(Integer.toString(i))).andReturn(unitMocks.get(i));
      expectLastCall().anyTimes();
    }
    expect(SettingsBeanMock.getExternalApplicationURL()).andReturn(HTTP_EXTERNAL_URL);
    expectLastCall().anyTimes();
    
    replay(servletConfigMock, servletContextMock, webApplicationContextmock, searchUnitFlowSupportBeanMock, searchServiceMock, SettingsBeanMock);
    sitemap = new Sitemap();
    sitemap.init(servletConfigMock);
  }

  /**
   * Test that correct outstream is written to http response.
   * 
   * @throws ServletException
   * @throws IOException
   */
  @Test
  public void testDoGet() throws ServletException, IOException {
    BasicConfigurator.configure();

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    final HttpServletResponse mockHttpServletResponse = createMock(HttpServletResponse.class);
    final HttpServletRequest mockHttpServletRequest = createMock(HttpServletRequest.class);
    makeThreadSafe(mockHttpServletRequest, true);
    makeThreadSafe(mockHttpServletResponse, true);
    expect(mockHttpServletRequest.getScheme()).andReturn(SCHEME);
    expect(mockHttpServletRequest.getServerName()).andReturn(SERVERNAME);
    expect(mockHttpServletRequest.getServerPort()).andReturn(SERVER_PORT);
    expect(mockHttpServletRequest.getContextPath()).andReturn(CONTEXTPATH);
    expect(mockHttpServletResponse.getWriter()).andReturn(printWriter);
    replay(mockHttpServletRequest, mockHttpServletResponse);
    sitemap.doGet(mockHttpServletRequest, mockHttpServletResponse);
    String result = byteArrayOutputStream.toString();
    assertEquals(RESULT, result);
  }

  private ArrayList<Unit> createUnitmocks() {
    
    TimePoint timePoint = TimePoint.from(2007);
    for (int i = 0; i < NBR_OF_UNITS; i++) {
      Unit unit = new Unit();
      unit.setName("unit_" + i);
      unit.setHsaIdentity("hsaId_" + i);
      unit.setModifyTimestamp(timePoint);
      unitMocks.add(i, unit);
    }
    return unitMocks;
  }
  
  private List<String> createUnitHsaIds(){
    List<String> hsaIds = new ArrayList<String>();
    for (int i = 0; i < NBR_OF_UNITS; i++) {
      hsaIds.add(Integer.toString(i));
    }
    return hsaIds;
  }
 
}
