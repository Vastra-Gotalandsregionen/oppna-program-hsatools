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
package se.vgregion.kivtools.search.util.geo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * NB: For testing only!
 * 
 * Lookup RT90 coords with help from rl.se. Unfortunately there is no web service and string parsing is done on HTML chunk response. Not ideal... Used (and use!) only for testing purpose. Limited to
 * 50 conversions per day and IP address.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * @deprecated
 * 
 */
@Deprecated
public class RLCoordinateTransformerServiceImpl implements CoordinateTransformerService {

  private String serviceUrl = "http://rl.se/rt90";

  public int[] getRT90(double latDecDeg, double lonDecDeg) throws IOException {
    int[] rt90Coordinates = null;
    URL url = new URL(serviceUrl);
    DataOutputStream printout;
    DataInputStream input;
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setInstanceFollowRedirects(false);
    urlConnection.setRequestMethod("POST");
    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    // Send POST output.
    printout = new DataOutputStream(urlConnection.getOutputStream());
    String content = "wla=" + latDecDeg + "&wlo=" + lonDecDeg + "&rl=2";
    printout.writeBytes(content);
    printout.flush();
    printout.close();
    // Get response data.
    input = new DataInputStream(urlConnection.getInputStream());
    String str;
    while (null != (str = input.readLine())) {
      // Fetch X and Y
      int x = 0;
      int y = 0;
      if (str.startsWith("<tr class=pyjamas><td>RT90</td><td class=code>X = ")) {
        try {
          x = Integer.parseInt(str.substring(str.indexOf("X = ") + 4, str.indexOf("X = ") + 11));
          y = Integer.parseInt(str.substring(str.indexOf("Y = ") + 4, str.indexOf("Y = ") + 11));
          rt90Coordinates = new int[] { x, y };
        } catch (NumberFormatException e) {
          // We did not find RT90 coordinates. Move on.
        }
        break;
      }
    }
    input.close();
    return rt90Coordinates;
  }

  public double[] getWGS84(int x, int y) {
    // TODO Auto-generated method stub
    return null;
  }

}
