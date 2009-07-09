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

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * NB: For testing only!
 * 
 * Uses Lantmäteriverkets simple transformation service. Unfortunately there is no web service and string parsing is done on HTML chunk response. Not ideal... Used (and use!) only for testing purpose
 * since LM do know how to do the conversion right.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * @deprecated
 */
@Deprecated
public class LantmateriverketCoordinateTransformerServiceImpl implements CoordinateTransformerService {

  private String serviceUrl = "http://www.lantmateriet.se/templates/LMV_Enkelkoordinattransformation.aspx?id=11500";

  public int[] getRT90(double latDecDeg, double lonDecDeg) throws IOException {
    // Convert from decimal degrees to "grad,min,sek"
    double[] latDegMinSec = GeoUtil.getGradeMinSec(latDecDeg);
    double[] lonDegMinSec = GeoUtil.getGradeMinSec(lonDecDeg);

    int[] RT90Coordinates = null;
    String response = "";
    PostMethod post = new PostMethod(serviceUrl);
    NameValuePair[] data = { new NameValuePair("send", "Transformera"), new NameValuePair("FORM_NR", "Form1"), new NameValuePair("latgra", String.valueOf(latDegMinSec[0])),
        new NameValuePair("latmin", String.valueOf(latDegMinSec[1])), new NameValuePair("latsek", String.valueOf(latDegMinSec[2])), new NameValuePair("longra", String.valueOf(lonDegMinSec[0])),
        new NameValuePair("lonmin", String.valueOf(lonDegMinSec[1])), new NameValuePair("lonsek", String.valueOf(lonDegMinSec[2])), };
    post.setRequestBody(data);
    post.setRequestHeader("Rererer", "http://www.lantmateriet.se/templates/LMV_Enkelkoordinattransformation.aspx?id=11500");

    HttpClient httpclient = new HttpClient();
    try {
      httpclient.executeMethod(post);
      response = post.getResponseBodyAsString();
    } finally {
      post.releaseConnection();
    }

    int x = 0, y = 0;
    try {
      int startPos = response.indexOf("<span class=\"geodeticResultSpan transformation\">");
      String xString = response.substring(startPos + 55, startPos + 65);
      String yString = response.substring(startPos + 206, startPos + 216);
      x = (int) Math.round(Double.parseDouble(xString));
      y = (int) Math.round(Double.parseDouble(yString));
      RT90Coordinates = new int[] { x, y };
    } catch (NumberFormatException e) {
      // We did not find RT90 coordinates. Move on.
    }
    return RT90Coordinates;
  }

  public double[] getWGS84(int x, int y) {
    // TODO Auto-generated method stub
    return null;
  }
}
