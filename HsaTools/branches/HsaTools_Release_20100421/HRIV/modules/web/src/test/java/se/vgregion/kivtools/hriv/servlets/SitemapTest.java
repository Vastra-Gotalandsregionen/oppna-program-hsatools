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

package se.vgregion.kivtools.hriv.servlets;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.InternalSitemapGenerator;
import se.vgregion.kivtools.search.svc.SitemapCache;
import se.vgregion.kivtools.search.svc.SitemapCacheServiceImpl;
import se.vgregion.kivtools.search.svc.SitemapEntry;
import se.vgregion.kivtools.search.svc.SitemapSupportBean;

public class SitemapTest {
  private static final String RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:hsa=\"http://www.vgregion.se/schemas/hsa_schema\">\n<url>\n<loc>http://externalurl/visaenhet?hsaidentity=abc-123</loc>\n<lastmod>2010-02-12T01:00:00+01:00</lastmod>\n<changefreq>weekly</changefreq>\n<priority>0.5</priority>\n</url>\n</urlset>";
  private static final String SCHEME = "http";
  private static final String SERVERNAME = "localhost";
  private static final int SERVER_PORT = 8080;
  private static final String CONTEXTPATH = "test";

  private SitemapCacheServiceImpl sitemapCacheService = new SitemapCacheServiceImpl(new SitemapCacheLoaderMock());
  private SitemapSupportBean sitemapSupportBean = new SitemapSupportBean(sitemapCacheService, new InternalSitemapGenerator());
  private Sitemap sitemap;

  @Before
  public void setup() throws Exception {
    // Create mock objects.
    final ServletContext servletContextMock = createMock(ServletContext.class);
    final ServletConfig servletConfigMock = createMock(ServletConfig.class);
    final WebApplicationContext webApplicationContextmock = createMock(WebApplicationContext.class);

    makeThreadSafe(webApplicationContextmock, true);
    makeThreadSafe(servletContextMock, true);
    makeThreadSafe(servletConfigMock, true);

    expect(webApplicationContextmock.getBean("sitemapSupportBean")).andReturn(sitemapSupportBean);
    expectLastCall().anyTimes();

    expect(servletConfigMock.getServletContext()).andReturn(servletContextMock);
    expectLastCall().anyTimes();

    expect(servletContextMock.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT")).andReturn(webApplicationContextmock);
    expectLastCall().anyTimes();

    replay(servletConfigMock, servletContextMock, webApplicationContextmock);
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

  private static class SitemapCacheLoaderMock implements CacheLoader<SitemapCache> {
    @Override
    public SitemapCache createEmptyCache() {
      return new SitemapCache();
    }

    @Override
    public SitemapCache loadCache() {
      SitemapCache sitemapCache = new SitemapCache();

      SitemapEntry unit = new SitemapEntry("http://externalurl/visaenhet?hsaidentity=abc-123", "2010-02-12T01:00:00+01:00", "weekly");
      sitemapCache.add(unit);

      return sitemapCache;
    }
  }
}
