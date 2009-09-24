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
package se.vgregion.kivtools.hriv.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

/**
 * Ajax proxy to get rid of same-origin-policy-problems. Inspired by a jroller blog
 * 
 * @author Jonas Liljenfeldt, Know IT
 * @see http://www.jroller.com/ray/entry/http_proxy_servlet_for_ajax
 */
public class Proxy extends HttpServlet {
  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
    URL url = null;
    String user = null;
    String password = null;
    String method = "GET";
    String post = null;
    int timeout = 0;
    Set entrySet = req.getParameterMap().entrySet();
    Map<String, String> headers = new HashMap<String, String>();
    for (Object anEntrySet : entrySet) {
      Map.Entry header = (Map.Entry) anEntrySet;
      String key = (String) header.getKey();
      String value = ((String[]) header.getValue())[0];
      if ("user".equals(key)) {
        user = value;
      } else if ("password".equals(key)) {
        password = value;
      } else if ("timeout".equals(key)) {
        timeout = Integer.parseInt(value);
      } else if ("method".equals(key)) {
        method = value;
      } else if ("post".equals(key)) {
        post = value;
      } else if ("url".equals(key)) {
        url = new URL(value);
      } else {
        headers.put(key, value);
      }
    }

    if (url != null) {
      String digest = null;
      if (user != null && password != null) {
        digest = "Basic " + new String(Base64.encodeBase64((user + ":" + password).getBytes()));
      }

      boolean foundRedirect = false;
      do {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if (digest != null) {
          urlConnection.setRequestProperty("Authorization", digest);
        }
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setInstanceFollowRedirects(false);
        urlConnection.setRequestMethod(method);
        if (timeout > 0) {
          urlConnection.setConnectTimeout(timeout);
        }

        // set headers
        Set headersSet = headers.entrySet();
        for (Object aHeadersSet : headersSet) {
          Map.Entry header = (Map.Entry) aHeadersSet;
          urlConnection.setRequestProperty((String) header.getKey(), (String) header.getValue());
        }

        // send post
        if (post != null) {
          OutputStreamWriter outRemote = new OutputStreamWriter(urlConnection.getOutputStream());
          outRemote.write(post);
          outRemote.flush();
        }

        // get content type
        String contentType = urlConnection.getContentType();
        if (contentType != null) {
          res.setContentType(contentType);
        }

        // get reponse code
        int responseCode = urlConnection.getResponseCode();

        if (responseCode == 302) {
          // follow redirects
          String location = urlConnection.getHeaderField("Location");
          url = new URL(location);
          foundRedirect = true;
        } else {
          res.setStatus(responseCode);
          BufferedInputStream in;
          if (responseCode == 200 || responseCode == 201) {
            in = new BufferedInputStream(urlConnection.getInputStream());
          } else {
            in = new BufferedInputStream(urlConnection.getErrorStream());
          }

          // send output to client
          BufferedOutputStream out = new BufferedOutputStream(res.getOutputStream());
          int c;
          while ((c = in.read()) >= 0) {
            out.write(c);
          }
          out.flush();
        }
      } while (foundRedirect);

    }
  }
}
