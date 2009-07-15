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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: RequestDispatcher.
 * 
 */
public class RequestDispatcher extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String servletName = request.getServletPath();
    request.setCharacterEncoding("UTF-8");
    if (servletName != null) {
      if (servletName.contains("visaperson")) {
        request.getRequestDispatcher("displayPersonDetails.jsf?_flowId=Search.searchperson-flow").forward(request, response);
      } else if (servletName.contains("visaenhet")) {
        request.getRequestDispatcher("displayUnitDetails.jsf?_flowId=Search.searchunit-flow").forward(request, response);
      } else if (servletName.contains("visaenhetdn")) {
        request.getRequestDispatcher("displayUnitDetails.jsf?_flowId=Search.searchunit-flow").forward(request, response);
      } else if (servletName.contains("visaorganisation")) {
        request.getRequestDispatcher("displayPersonSearchResult.jsf?_flowId=Search.searchperson-flow").forward(request, response);
      }
    }
  }
}