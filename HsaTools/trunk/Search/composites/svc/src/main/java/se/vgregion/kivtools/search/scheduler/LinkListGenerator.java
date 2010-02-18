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

package se.vgregion.kivtools.search.scheduler;

import java.io.StringWriter;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.util.file.FileUtil;

/**
 * @author Anders Asplund - KnowIT
 *
 */
public class LinkListGenerator extends QuartzJobBean {

  private SearchService searchService;
  private FileUtil fileUtil;

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setFileUtil(FileUtil fileUtil) {
    this.fileUtil = fileUtil;
  }

  @Override
  public void executeInternal(JobExecutionContext context) {
    try {
      StringWriter units = new StringWriter();
      List<String> ids = this.searchService.getAllUnitsHsaIdentity();
      units.write("<html><head><title>Lista med enheter</title></head><body>");
      for (String id : ids) {
        units.write("<div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity=" + id + "\">" + id + "</a></div>");
      }
      units.write("</body></html>");

      this.fileUtil.writeFile("units.html", units.toString());

      StringWriter users = new StringWriter();

      ids = this.searchService.getAllPersonsId();
      users.write("<html><head><title>Lista med anv�ndare</title></head><body>");
      for (String id : ids) {
        users.write("<div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity=" + id + "\">" + id + "</a></div>");
      }
      users.write("</body></html>");

      this.fileUtil.writeFile("users.html", users.toString());
    } catch (KivException e) {
      e.printStackTrace();
    }
  }
}
