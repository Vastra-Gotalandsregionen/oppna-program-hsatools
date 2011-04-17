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

package se.vgregion.kivtools.util.http;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.util.http.HttpFetcherImpl;

public class HttpFetcherImplTest {

  private HttpFetcherImpl fetcher;

  @Before
  public void setUp() {
    fetcher = new HttpFetcherImpl();
  }

  @Test
  public void testMalformedUrl() {
    String result = fetcher.fetchUrl("abc.defxxx");
    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  public void testUnknownURL() {
    String result = fetcher.fetchUrl("http://xyz.yadda.yadda.se");
    assertNotNull(result);
    assertEquals("", result);
  }

  @Test
  public void testSuccessfulUrl() {
    String result = fetcher.fetchUrl("http://www.google.com");
    assertNotNull(result);
    assertTrue(result.length() > 0);
    assertTrue(result.indexOf("google") != -1);
  }

  @Test
  public void testSuccessfulSecureUrl() {
    String result = fetcher.fetchUrl("https://internetbank.swedbank.se/idp/portal");
    assertNotNull(result);
    assertTrue(result.length() > 0);
    assertTrue(result.indexOf("swedbank") != -1);
  }

  @Test
  public void testNiceHostnameVerifier() {
    assertTrue(new HttpFetcherImpl.NiceHostnameVerifier().verify("grodanboll", null));
  }
}
