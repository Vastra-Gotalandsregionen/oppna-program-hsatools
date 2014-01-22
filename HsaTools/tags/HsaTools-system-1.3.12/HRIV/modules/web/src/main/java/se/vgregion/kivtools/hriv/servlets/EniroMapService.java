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

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EniroMapService extends HttpServlet implements Serializable {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    this.doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    getGeoCoding(req, resp);
  }

  private final Log logger = LogFactory.getLog(EniroMapService.class);
  private HttpClient httpClient;
  private HttpMethod httpMethod;

  public EniroMapService() {
    httpClient = new HttpClient();
    httpMethod = new GetMethod("http://kartor.eniro.se/api/geocode");
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Work around the same origin policy problem for JavaScript
   */
  private void getGeoCoding(HttpServletRequest request, HttpServletResponse response) {

    String address = request.getParameter("name");
    String addressWithoutNumber = address.replaceAll("[0-9]", "");
    NameValuePair country = new NameValuePair("country", "se");
    NameValuePair name = new NameValuePair("name", addressWithoutNumber.trim());
    NameValuePair type = new NameValuePair("type", "city");
    NameValuePair contentType = new NameValuePair("contentType", "xml");
    NameValuePair hits = new NameValuePair("hits", "1");

    // Provide custom retry handler is necessary
    httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
    httpMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

    httpMethod.setQueryString(new NameValuePair[] { country, name, type, contentType, hits });

    // Write responseBody to response outputStream.
    try {
      int statusCode = httpClient.executeMethod(httpMethod);
      if (statusCode == HttpStatus.SC_OK) {
        byte[] responseBody = httpMethod.getResponseBody();
        response.getOutputStream().write(responseBody);
      }

    } catch (HttpException e) {
      logger.error(e);
    } catch (IOException e) {
      logger.error(e);
    } finally {
      httpMethod.releaseConnection();
    }
  }
}
