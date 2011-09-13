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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.util.dom.DocumentHelper;
import se.vgregion.kivtools.util.http.HttpFetcher;

/**
 * Simple MVK client.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class MvkClient {
  private final HttpFetcher httpFetcher;
  private final String mvkGuid;
  private final String mvkUrl;

  public MvkClient(HttpFetcher httpFetcher, String mvkGuid, String mvkUrl) {
    this.httpFetcher = httpFetcher;
    this.mvkGuid = mvkGuid;
    this.mvkUrl = mvkUrl;
  }

  /**
   * Fetches case types from MVK and assigns them to the provided unit.
   * 
   * @param unit The unit to assign case types to.
   */
  public void assignCaseTypes(Unit unit) {
    // Get accessibility info
    String mvkUrlString = this.mvkUrl + "&hsaid=" + unit.getHsaIdentity() + "&guid=" + this.mvkGuid;

    String content = this.httpFetcher.fetchUrl(mvkUrlString);

    // Now read the buffered stream and get mvk info
    Document doc = DocumentHelper.getDocumentFromString(content);

    // Get and assign case types
    NodeList caseTypesNodeList = doc.getElementsByTagName("casetype");
    for (int i = 0; i < caseTypesNodeList.getLength(); i++) {
      unit.addMvkCaseType(caseTypesNodeList.item(i).getTextContent());
    }
  }
}
