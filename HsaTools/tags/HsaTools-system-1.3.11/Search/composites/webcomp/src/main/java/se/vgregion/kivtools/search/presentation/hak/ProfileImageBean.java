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

package se.vgregion.kivtools.search.presentation.hak;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;

/**
 * Bean for fetching a persons profile image and streaming it back to the client.
 * 
 * @author Joakim Olsson
 */
@Controller
public class ProfileImageBean {
  private Log log = LogFactory.getLog(this.getClass());
  private SearchService searchService;

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Retrieves a persons profile image by using the persons distinguished name and streams it back to the client.
   * 
   * @param response The HttpServletResponse to use to stream the image back to the client.
   * @param dn The distinguished name of the person to fetch the image for.
   * @return Always returns null since no Spring-view should be rendered.
   */
  @RequestMapping("/image.servlet")
  public String getProfileImageByDn(HttpServletResponse response, @RequestParam("dn") String dn) {
    try {
      byte[] profileImage = this.searchService.getProfileImageByDn(dn);
      if (profileImage != null) {
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(profileImage);
        outputStream.flush();
      }
    } catch (KivException e) {
      log.debug("Unable to retrieve profile image", e);
    } catch (IOException e) {
      log.debug("Unable to stream profile image back to client", e);
    }
    // Always return null since we are streaming back an image.
    return null;
  }
}
