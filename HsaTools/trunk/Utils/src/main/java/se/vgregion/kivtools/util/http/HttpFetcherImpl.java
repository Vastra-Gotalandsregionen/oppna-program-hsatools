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
package se.vgregion.kivtools.util.http;

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

import se.vgregion.kivtools.util.StringUtil;

/**
 * Implementation of HttpFetcher that uses a HttpUrlConnection for fetching the information.
 * 
 * @author argoyle
 */
public class HttpFetcherImpl implements HttpFetcher {
  private Log logger = LogFactory.getLog(this.getClass());

  /**
   * {@inheritDoc}
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

        String charset = getCharsetFromContentType(urlConnection.getContentType(), "UTF-8");

        int responseCode = urlConnection.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
          reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), charset));
        } else {
          reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), charset));
        }

        StringWriter writer = new StringWriter();

        char[] buffer = new char[1024];
        int readChars = -1;
        while ((readChars = reader.read(buffer)) > 0) {
          writer.write(buffer, 0, readChars);
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
   * Extracts the name of the charset to use from a content type string.
   * 
   * @param contentType The content type string to extract charset from.
   * @param defaultValue The default value to use if no charset can be extracted from the content type.
   * @return The charset from the content type or the provided default value if no charset can be extracted from the content type.
   */
  private String getCharsetFromContentType(String contentType, String defaultValue) {
    String charset = defaultValue;

    if (!StringUtil.isEmpty(contentType)) {
      int index = contentType.indexOf("charset=");
      if (index != -1) {
        int endIndex = contentType.indexOf(";", index);
        if (endIndex == -1) {
          charset = contentType.substring(index + 8);
        } else {
          charset = contentType.substring(index + 8, endIndex);
        }
      }
    }

    return charset;
  }

  /**
   * A Hostname verifier that is always nice and trusts everyone.
   */
  public static class NiceHostnameVerifier implements HostnameVerifier {
    /**
     * {@inheritDoc}
     */
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }
}
