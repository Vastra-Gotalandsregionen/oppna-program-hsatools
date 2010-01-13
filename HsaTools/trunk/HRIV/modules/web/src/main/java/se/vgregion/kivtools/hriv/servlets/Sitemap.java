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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import se.vgregion.kivtools.hriv.presentation.SearchUnitFlowSupportBean;
import se.vgregion.kivtools.hriv.presentation.SettingsBean;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Generates a sitemap for HRIV.
 * 
 * TODO: Make a generic "do-something-with-every-unit-at-startup-and-then-regulary" servlet with a hook where you can register your "plugin" (like Sitemap etc).
 * 
 * @author Jonas Liljenfeldt, Know IT
 * @see http://www.sitemaps.org/protocol.php
 */
public class Sitemap extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final String CLASS_NAME = Sitemap.class.getName();
  private static final Log LOGGER = LogFactory.getLog(Sitemap.class);
  private static ArrayList<UnitSitemapInformation> siteMapInformationUnits = new ArrayList<UnitSitemapInformation>();
  private static ServletContext servletContext;

  private Timer createUnitsTimer = new Timer();

  public static ArrayList<UnitSitemapInformation> getUnits() {
    return siteMapInformationUnits;
  }

  /**
   * Create scheduler during servlet initialization.
   * 
   * @throws ServletException if an exception occurs that interrupts the servlet's normal operation.
   * 
   */
  @Override
  public void init() throws ServletException {
    LOGGER.info(CLASS_NAME + ".init()");
    servletContext = getServletContext();
    super.init();

    /*
     * Pre-fetch all units in order to generate sitemap quick(er) and schedule updates.
     */
    createUnitsTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        Sitemap.fillUnits(LOGGER);
      }
      // Update list of units once in a 24 hour period. Use delay because it fails the unit test. Temporary fix.
    }, 30000, 3600000 * 24);
  }

  /**
   * Populates list with all units.
   * 
   * @param logger
   */
  protected static void fillUnits(Log logger) {
    logger.debug("Starting to create list of UnitSitemapInformation");
    long startTimeMillis = System.currentTimeMillis();

    WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

    // Spring bean name (Search.SearchUnitFlowSupportBea) is hard coded!
    SearchUnitFlowSupportBean sb = (SearchUnitFlowSupportBean) springContext.getBean("Search_SearchUnitFlowSupportBean");

    // Spring bean name (Search_SearchService) is hard coded!
    sb.setSearchService((SearchService) springContext.getBean("Search_SearchService"));

    List<String> allUnitsHsaId;
    ArrayList<Unit> allUnits = new ArrayList<Unit>();
    siteMapInformationUnits.clear();
    try {
      allUnitsHsaId = sb.getAllUnitsHsaIdentity(true);
      sb.setUnitsCacheComplete(false);
      for (String hsaId : allUnitsHsaId) {
        if (hsaId != null) {
          Unit u;
          try {
            u = sb.getSearchService().getUnitByHsaId(hsaId);
          } catch (KivException e) {
            logger.error("Exception while getting unit details for hsaIdentity '" + hsaId + "'");
            throw e;
          }
          if (u != null) {
            siteMapInformationUnits.add(new UnitSitemapInformation(u.getHsaIdentity(), u.getModifyTimestampFormattedInW3CDatetimeFormat(), u.getCreateTimestampFormattedInW3CDatetimeFormat()));
            // While we are at it, populate lists with complete units objects.
            allUnits.add(u);
          }
        }
      }
      // Give list of units to SearchUnitFlowSupportBean and tell it to set up coordinates.
      sb.setUnits(allUnits);
      sb.populateCoordinates();
      sb.setUnitsCacheComplete(true);
    } catch (KivNoDataFoundException e1) {
      logger.error("Something went wrong when retrieving all units.", e1);
    } catch (KivException e) {
      logger.error("Something went wrong when retrieving all units.", e);
    }
    long endTimeMillis = System.currentTimeMillis();
    logger.debug("Finished creating list of UnitSitemapInformation. It took: " + (endTimeMillis - startTimeMillis) / 1000 + " seconds.");
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    LOGGER.debug(CLASS_NAME + ".doGet()");
    LOGGER.debug("Starting to put together the sitemap.");

    long startTimeMillis = System.currentTimeMillis();

    // Check if list of units is populated, otherwise we fill it up!
    if (siteMapInformationUnits.size() < 1) {
      fillUnits(LOGGER);
    }

    // Spring bean name is hard coded!
    WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    SettingsBean settingsBean = (SettingsBean) springContext.getBean("Search_SettingsContainer");

    // Using StringBuilder instead of concatenating strings improved
    // performance a lot...
    StringBuilder output = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

    String externalApplicationURL = settingsBean.getExternalApplicationURL();

    for (UnitSitemapInformation u : siteMapInformationUnits) {
      String hsaId = u.getHsaId();
      String lastmod = getLastModifiedDateTime(u);
      output.append("<url>\n");
      output.append("<loc>" + externalApplicationURL + "/" + "visaenhet?hsaidentity=" + hsaId + "</loc>\n");
      output.append("<lastmod>" + lastmod + "</lastmod>\n");
      output.append("<changefreq>weekly</changefreq>\n");
      output.append("<priority>0.5</priority>\n");
      output.append("</url>\n");

      LOGGER.debug("Added unit " + hsaId);
    }

    output.append("</urlset>");
    long endTimeMillis = System.currentTimeMillis();
    LOGGER.debug("Sitemap generation finished. It took: " + (endTimeMillis - startTimeMillis) / 1000 + " seconds.");
    PrintWriter pw = response.getWriter();
    pw.write(output.toString());
    pw.flush();
    pw.close();
  }

  /**
   * Helper method to get the last modified date/time in W3C format for a unit.
   * 
   * @param unitInformation The unit information to get the last modified date/time from.
   * @return The modify timestamp if set, otherwise the creation timestamp.
   */
  private String getLastModifiedDateTime(UnitSitemapInformation unitInformation) {
    String lastModified = null;

    if (StringUtil.isEmpty(unitInformation.getModifyTimestampFormattedInW3CDatetimeFormat())) {
      lastModified = unitInformation.getCreateTimestampFormattedInW3CDatetimeFormat();
    } else {
      lastModified = unitInformation.getModifyTimestampFormattedInW3CDatetimeFormat();
    }
    return lastModified;
  }
}
