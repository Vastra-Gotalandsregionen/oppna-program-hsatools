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

package se.vgregion.kivtools.search.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.vgregion.kivtools.mocks.http.HttpFetcherMock;
import se.vgregion.kivtools.search.domain.Unit;

public class MvkClientTest {
  private final HttpFetcherMock httpFetcher = new HttpFetcherMock();
  private final MvkClient mvkClient = new MvkClient(this.httpFetcher, "uid123", "http://localhost?mvk=1");

  @Test
  public void testInstantiation() {
    MvkClient mvkClient = new MvkClient(null, null, null);
    assertNotNull(mvkClient);
  }

  @Test
  public void testAssignCaseTypes() {
    try {
      this.mvkClient.assignCaseTypes(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    Unit unit = new Unit();
    unit.setHsaIdentity("ABC-123");
    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123", "<xml></xml>");
    this.mvkClient.assignCaseTypes(unit);
    this.httpFetcher.assertUrlsFetched("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123");

    this.httpFetcher.addContent("http://localhost?mvk=1&hsaid=ABC-123&guid=uid123", "<?xml version=\"1.0\"?><casetypes><casetype>abc</casetype><casetype>def</casetype></casetypes>");
    this.mvkClient.assignCaseTypes(unit);
    assertEquals(2, unit.getMvkCaseTypes().size());
  }
}
