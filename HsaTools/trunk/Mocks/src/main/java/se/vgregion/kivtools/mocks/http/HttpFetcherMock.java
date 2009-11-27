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
package se.vgregion.kivtools.mocks.http;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.util.http.HttpFetcher;

/**
 * Mock HttpFetcher to use in unit tests.
 * 
 * @author Joakim Olsson
 */
public class HttpFetcherMock implements HttpFetcher {
  private Map<String, String> content = new HashMap<String, String>();
  private List<String> urlsFetched = new ArrayList<String>();

  /**
   * Adds content for a specific URL.
   * 
   * @param url The URL to add content for.
   * @param contentToAdd The content to return for a specific requested URL.
   */
  public void addContent(String url, String contentToAdd) {
    if (contentToAdd == null) {
      this.content.put(url, "");
    } else {
      this.content.put(url, contentToAdd);
    }
  }

  /**
   * Asserts that the correct URL's where fetched.
   * 
   * @param expected The URL's that was expected to be fetched.
   */
  public void assertUrlsFetched(String... expected) {
    assertEquals(expected.length, this.urlsFetched.size());
    for (String url : expected) {
      assertTrue("Expected URL was not fetched", this.urlsFetched.contains(url));
    }
  }

  @Override
  public String fetchUrl(String urlToFetch) {
    this.urlsFetched.add(urlToFetch);
    return this.content.get(urlToFetch);
  }
}
