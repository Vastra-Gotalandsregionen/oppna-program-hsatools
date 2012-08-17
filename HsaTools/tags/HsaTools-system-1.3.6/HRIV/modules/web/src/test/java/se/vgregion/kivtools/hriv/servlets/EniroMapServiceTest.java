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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class EniroMapServiceTest {

  private EniroMapService eniroMapService;
  private HttpClientMock httpClientMock;

  @Before
  public void setUp() throws Exception {
    eniroMapService = new EniroMapService();
    httpClientMock = new HttpClientMock();
    eniroMapService.setHttpClient(httpClientMock);

    String url = "http://kartor.eniro.se/api/geocode";
    HttpMethod httpMethod = new HttpMethodMock(url);
    HttpMethodParams httpMethodParams = new HttpMethodParams();
    httpMethodParams.setParameter("country", "se");
    httpMethodParams.setParameter("type", "street");
    httpMethodParams.setParameter("contentType", "xml");
    httpMethodParams.setParameter("hits", "1");
    httpMethod.setParams(httpMethodParams);

    eniroMapService.setHttpMethod(httpMethod);
  }

  @Test
  public void testGetGeoCoding() throws ServletException, IOException {
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
    String address = "Vikingsgatan 1";
    mockHttpServletRequest.setParameter("name", address);
    eniroMapService.doPost(mockHttpServletRequest, mockHttpServletResponse);
    URI uri = httpClientMock.httpResultMethod.getURI();

    assertEquals("http://kartor.eniro.se/api/geocode?country=se&name=Vikingsgatan&type=city&contentType=xml&hits=1", uri.toString());
    assertEquals("Response body text", mockHttpServletResponse.getContentAsString());
  }

  class HttpClientMock extends HttpClient {

    HttpMethod httpResultMethod;

    @Override
    public int executeMethod(HttpMethod method) throws IOException, HttpException {
      this.httpResultMethod = method;
      return 200;
    }
  }

  class HttpMethodMock extends GetMethod {

    public HttpMethodMock(String url) {
      super(url);
    }

    @Override
    public byte[] getResponseBody() throws IOException {
      return "Response body text".getBytes();
    }
  }
}
