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
package se.vgregion.kivtools.hriv.presentation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import se.vgregion.kivtools.search.svc.domain.Unit;

/**
 * Simple mvk client.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class MvkClient {
  private String mvkGuid;
  private String mvkUrl;
  private Log logger = LogFactory.getLog(this.getClass());

  public MvkClient(String mvkGuid, String mvkUrl) {
    this.mvkGuid = mvkGuid;
    this.mvkUrl = mvkUrl;
  }

  public String getMvkUrl() {
    return mvkUrl;
  }

  public void setMvkUrl(String mvkUrl) {
    this.mvkUrl = mvkUrl;
  }

  public String getMvkGuid() {
    return mvkGuid;
  }

  public void setMvkGuid(String mvkGuid) {
    this.mvkGuid = mvkGuid;
  }

  public void assignCaseTypes(Unit u) {
    // Get accessibility info
    URL url = null;
    String mvkUrlString = mvkUrl + "&hsaid=" + u.getHsaIdentity() + "&guid=" + getMvkGuid();
    try {
      url = new URL(mvkUrlString);
    } catch (MalformedURLException e) {
      logger.error("MVK url no good: " + mvkUrlString, e);
    }
    HttpURLConnection urlConnection;
    BufferedInputStream in = null;
    try {
      urlConnection = (HttpURLConnection) url.openConnection();
      ((HttpsURLConnection) urlConnection).setHostnameVerifier(new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });

      int responseCode = urlConnection.getResponseCode();
      if (responseCode == 200 || responseCode == 201) {
        in = new BufferedInputStream(urlConnection.getInputStream());
      } else {
        in = new BufferedInputStream(urlConnection.getErrorStream());
      }
    } catch (IOException e) {
      logger.error("Error when retrieving MVK xml response", e);
    }

    // Now read the buffered stream and get mvk info
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    Document doc = null;
    try {
      docBuilder = docBuilderFactory.newDocumentBuilder();
      doc = docBuilder.parse(in);
      doc.normalize();
    } catch (ParserConfigurationException e) {
      logger.error("Error when parsing MVK xml response", e);
    } catch (SAXException e) {
      logger.error("Error when parsing MVK xml response", e);
    } catch (IOException e) {
      logger.error("Error when parsing MVK xml response", e);
    }

    // Get and assign case types
    NodeList caseTypesNodeList = doc.getElementsByTagName("casetype");
    List<String> caseTypes = new ArrayList<String>();
    for (int i = 0; i < caseTypesNodeList.getLength(); i++) {
      caseTypes.add(caseTypesNodeList.item(i).getTextContent());
    }
    u.setMvkCaseTypes(caseTypes);
  }
}
