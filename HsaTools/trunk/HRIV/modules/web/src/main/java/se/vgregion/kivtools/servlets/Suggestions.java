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
package se.vgregion.kivtools.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import se.vgregion.kivtools.search.presentation.SuggestionsSupportBean;
import se.vgregion.kivtools.search.util.EnvAssistant;

/**
 * Generates suggestions based on text entered in unitName text field.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class Suggestions extends HttpServlet implements Serializable {
  private static final long serialVersionUID = 1L;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String userInputUnitName = getUserInput(request);

    // Spring bean name is hard coded!
    WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
    SuggestionsSupportBean suggestionsSupportBean = (SuggestionsSupportBean) springContext.getBean("Search.SuggestionsSupportBean");

    String outputFormat = request.getParameter("output");

    String output = suggestionsSupportBean.getSuggestions(userInputUnitName, outputFormat);

    setContentType(response, outputFormat);

    PrintWriter out = response.getWriter();
    out.print(output);
  }

  /**
   * Helper-method for setting the correct content type based on the output format.
   * 
   * @param response The response to set the content type for.
   * @param outputFormat The output format.
   */
  private void setContentType(HttpServletResponse response, String outputFormat) {
    if ("xml".equals(outputFormat)) {
      response.setContentType("text/xml");
    } else if ("text".equals(outputFormat)) {
      response.setContentType("text/plain");
    } else {
      response.setContentType("text/html");
    }
  }

  private String getUserInput(HttpServletRequest request) throws UnsupportedEncodingException {
    String userInputUnitName;
    if (EnvAssistant.isRunningOnIBM()) {
      // Running on WebSphere, UTF-8
      userInputUnitName = URLDecoder.decode(new String(request.getParameter("query").getBytes("UTF-8")), "UTF-8");
    } else {
      // We don't run on IBM (maybe Tomcat), 8859-1
      userInputUnitName = URLDecoder.decode(new String(request.getParameter("query").getBytes("ISO-8859-1")), "ISO-8859-1");
    }
    // param name is "query" (not unitName) as default when using YUI AC,
    // when using scriptaculous: String userInputUnitName =
    // request.getParameter("unitName");
    return userInputUnitName;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doPost(req, resp);
  }
}
