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

package se.vgregion.kivtools.search.util.geo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Handles coordinate operations.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * @author Kengu5, VGR IT
 */
public class GeoUtil {

  private static Log logger = LogFactory.getLog(GeoUtil.class);
  private static final String CLASS_NAME = GeoUtil.class.getName();
  private static final double METRES_PER_MILE = 1609.344;

  /**
   * @param rt90String HSA formatted RT90 coords: X:1234567, Y:1234567.
   * @return An array of RT90 coordinates.
   */
  public static int[] parseRT90HsaString(String rt90String) {
    int[] result = null;
     
    if (!StringUtil.isEmpty(rt90String)) {
      rt90String = rt90String.replace(" ", "");
      if (rt90String.indexOf("X:") >= 0 && rt90String.indexOf("Y:") >= 0) {
        int rt90X = Integer.parseInt(rt90String.substring(rt90String.indexOf("X:") +2, rt90String.indexOf(",Y:")));
        int rt90Y = Integer.parseInt(rt90String.substring(rt90String.indexOf("Y:")+2));
        result = new int[] { rt90X, rt90Y };
      }
    }
    return result;
  }

  /**
   * Parse NMEA-String.
   * 
   * @param latOrLong Latitude or longitude in NMEA format
   * @param isLong True if longitude, false if latitude.
   * @return Latitude or longitude in radians as decimal value.
   */
  static double getLatLongRadiansDecimal(String latOrLong, boolean isLong) {
    // Get Hours (up to the 'D')
    double deciLatLon = Double.parseDouble(latOrLong.substring(0, latOrLong.indexOf("D")));

    // Remove it once we've used it
    String remainder = latOrLong.substring(latOrLong.indexOf("D") + 1);

    // Get Minutes (up to the '.') and3872648 divide by Minutes/Hour
    deciLatLon += Double.parseDouble(remainder.substring(0, remainder.indexOf("."))) / 60.0;

    // Remove it once we've used it
    remainder = remainder.substring(remainder.indexOf(".") + 1);

    // Get Seconds (up to the '"') and divide by Seconds/Hour
    String sec = remainder.substring(0, remainder.indexOf("\""));
    // Insert a dot to prevent the time from flying away...
    deciLatLon += Double.parseDouble(new StringBuilder(sec).insert(2, ".").toString()) / 3600.0;

    // Get the Hemisphere String
    remainder = remainder.substring(remainder.indexOf("\"") + 1);
    if (isLong && "S".equals(remainder) || !isLong && "W".equals(remainder)) {
      // Set us right
      deciLatLon = -deciLatLon;
    }
    // And return (as radians)
    return deciLatLon * Math.PI / 180.0;
  }

  /**
   * Convert from degrees in decimal format (eg. 49.5125) to {grade, minutes, seconds} (eg. {49, 30, 45}).
   * 
   * @param Latitude or longitude in decimal degrees.
   * @return An array with {grade, min, sec}
   */
  static double[] getGradeMinSec(double decDegree) {
    // In example, wants to get 49 degrees 30 minutes and 45 secs from
    // 49.5125
    // degree = int(49.5125) = 49
    int degree = (int) Math.floor(decDegree);
    // min = frac(49.5125)*60 = 0.5125*60 = 30.75 min
    double minDouble = (decDegree - degree) * 60;
    int min = (int) minDouble;
    // frac(min) * 60 = 45 sec
    double sec = (minDouble - min) * 60;
    return new double[] { degree, min, sec };
  }

 
  /**
   * Convert meters -> miles.
   * 
   * @param meters The number of meters to convert to miles.
   * @return The provided number of meters converted to miles.
   */
  private double getMilesFromMetres(int meters) {
    return meters / METRES_PER_MILE;
  }

  /**
   * Creates a new GeoCoordinate object from the provided coordinates.
   * 
   * @param coordinates The coordinates to use, {lat, long}.
   * @return A populated GeoCoordinate object.
   */
}
