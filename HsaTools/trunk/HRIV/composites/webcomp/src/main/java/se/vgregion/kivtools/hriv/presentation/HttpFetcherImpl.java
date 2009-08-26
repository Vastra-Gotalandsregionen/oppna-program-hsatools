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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of HttpFetcher that uses a HttpUrlConnection for fetching the information.
 * 
 * @author argoyle
 */
public class HttpFetcherImpl implements HttpFetcher {
  private Log logger = LogFactory.getLog(this.getClass());

  /**
   * @inheritDoc
   */
  @Override
  public String fetchUrl(String urlToFetch) {
    String result = "";

    URL url = null;
    try {
      url = new URL(urlToFetch);

      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;
      try {
        urlConnection = (HttpURLConnection) url.openConnection();
        if (urlConnection instanceof HttpsURLConnection) {
          ((HttpsURLConnection) urlConnection).setHostnameVerifier(new NiceHostnameVerifier());
        }

        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
          reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } else {
          reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        }

        StringWriter writer = new StringWriter();

        char[] buffer = new char[1024];
        while (reader.read(buffer) > 0) {
          writer.write(buffer);
        }

        result = writer.toString();
      } catch (IOException e) {
        logger.error("Error when retrieving response", e);
      } finally {
        urlConnection.disconnect();
      }
    } catch (MalformedURLException e) {
      logger.error("URL no good: " + urlToFetch);
    }

    return result;
  }

  /**
   * A Hostname verifier that is always nice and trusts everyone.
   */
  public static class NiceHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }
}
