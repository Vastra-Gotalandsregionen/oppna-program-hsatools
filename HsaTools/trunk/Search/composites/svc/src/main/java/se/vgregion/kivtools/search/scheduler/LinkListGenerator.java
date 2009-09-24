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
/**
 * 
 */
package se.vgregion.kivtools.search.scheduler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import se.vgregion.kivtools.search.svc.SearchService;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class LinkListGenerator extends QuartzJobBean {

    private SearchService searchService;

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void executeInternal(JobExecutionContext context) {
        try {
            FileWriter file = openFile("units.html");            
            List<String> ids = this.searchService.getAllUnitsHsaIdentity();
            file.write("<html><head><title>Lista med enheter</title></head><body>");
            for (String id : ids) {
                file.write("<div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity="
                            + id + "\">" + id + "</a></div>");
            }
            file.write("</body></html>");
            file.close();

            file = openFile("users.html");            
            ids = this.searchService.getAllPersonsId();
            file.write("<html><head><title>Lista med anv�ndare</title></head><body>");
            for (String id : ids) {
                file.write("<div><a href=\"http://kivsearch.vgregion.se/kivsearch/visaenhet?hsaidentity="
                            + id + "\">" + id + "</a></div>");
            }
            file.write("</body></html>");
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FileWriter openFile(String fileName) throws IOException {
        File f = new File(System.getProperty("user.dir"), fileName);
        if (!f.createNewFile()) {
            f.delete();
            if (!f.createNewFile()) {
                throw new IOException("Could not create file.");
            }
        }
        return new FileWriter(f);
    }
}
