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
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;
import se.vgregion.kivtools.util.http.HttpFetcher;

public class HttpFetcherMock implements HttpFetcher {
  private String content = "";
  private String lastUrlFetched;

  public void setContent(String content) {
    if (content == null) {
      this.content = "";
    } else {
      this.content = content;
    }
  }

  public void assertLastUrlFetched(String expected) {
    assertEquals("Unexpected URL called", expected, this.lastUrlFetched);
  }

  @Override
  public String fetchUrl(String urlToFetch) {
    this.lastUrlFetched = urlToFetch;
    return this.content;
  }
}