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

import geo.google.GeoAddressStandardizer;
import geo.google.GeoException;
import geo.google.datamodel.GeoAddress;
import geo.google.datamodel.GeoAddressAccuracy;
import geo.google.datamodel.GeoAltitude;
import geo.google.datamodel.GeoCoordinate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.util.Evaluator;

/**
 * Handles coordinate operations.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class GeoUtil {

  private static Log logger = LogFactory.getLog(GeoUtil.class);
  private static final String CLASS_NAME = GeoUtil.class.getName();
  private static final double METRES_PER_MILE = 1609.344;

  /**
   * Geocode an address to RT90.
   * 
   * @param hsaStreetAddress The address to geocode.
   * @param googleKey The Google Maps key to use.
   * @return RT90Coordinates: element #0 = latitude, #1 = longitude
   * @throws Exception If anything goes wrong.
   * @see http://geo-google.sourceforge.net/usage.html
   */
  public int[] geocodeToRT90(Address hsaStreetAddress, String googleKey) throws Exception {
    logger.debug(CLASS_NAME + ".geocodeToRT90()");

    int[] rt90Coordinates = null;
    double[] wgs84Coordinates = geocodeToWGS84FromHsaAddress(hsaStreetAddress, googleKey);

    if (wgs84Coordinates != null) {
      // 2.5V is HSA standard
      CoordinateTransformerService cts = new GaussKrugerProjection("2.5V");
      rt90Coordinates = cts.getRT90(wgs84Coordinates[0], wgs84Coordinates[1]);
      if (rt90Coordinates != null) {
        logger.debug("RT90 Coords after projection: " + rt90Coordinates[0] + "," + rt90Coordinates[1]);
      }
    }
    return rt90Coordinates;
  }

  /**
   * Geocode an address to WGS84.
   * 
   * @param address The street address.
   * @param googleKey The Google Maps key to use.
   * @param accuracy The accuracy of the addresses to find.
   * @return An array of WGS84 coordinates.
   */
  private double[] geocodeToWGS84FromString(String address, String googleKey, GeoAddressAccuracy accuracy) {
    double[] result = null;

    GeoAddressStandardizer st = new GeoAddressStandardizer(googleKey);
    GeoAddress geoAddress = null;
    try {
      logger.debug("Geocode " + address);
      // The best match is returned first
      List<GeoAddress> geoAddresses = st.standardizeToGeoAddresses(address);
      geoAddress = null;
      if (geoAddresses != null && geoAddresses.size() > 0) {
        geoAddress = geoAddresses.get(0);
      }

      // We need at least "address level"
      if (geoAddress != null && geoAddress.getAccuracy().getCode() >= accuracy.getCode()) {
        result = new double[] { geoAddress.getCoordinate().getLatitude(), geoAddress.getCoordinate().getLongitude() };
      }

      logger.debug("WGS84 Coord from Google Maps: " + geoAddress.getCoordinate().getLatitude() + "," + geoAddress.getCoordinate().getLongitude());
    } catch (GeoException e) {
      // We could not geocode, possibly a non accurate address.
      logger.debug("Could not geocode: " + address);
    }
    return result;
  }

  /**
   * Geocode an address to WGS84.
   * 
   * @param hsaStreetAddress The street address.
   * @param googleKey The Google Maps key to use.
   * @return An array of WGS84 coordinates.
   */
  public double[] geocodeToWGS84FromHsaAddress(Address hsaStreetAddress, String googleKey) {
    logger.debug(CLASS_NAME + ".geocodeToWGS84()");
    // We can't do anything if we don't have at least a street name, zip code or city.
    if (hsaStreetAddress == null || Evaluator.isEmpty(hsaStreetAddress.getStreet()) && Evaluator.isEmpty(hsaStreetAddress.getZipCode().getZipCode()) && Evaluator.isEmpty(hsaStreetAddress.getCity())) {
      return null;
    }
    String address = hsaStreetAddress.getStreet().trim() + ", " + hsaStreetAddress.getZipCode().getFormattedZipCode().toString().trim() + " " + hsaStreetAddress.getCity().trim() + ", sweden";
    return geocodeToWGS84FromString(address, googleKey, GeoAddressAccuracy.STREET_LEVEL);
  }

  /**
   * @param rt90String HSA formatted RT90 coords: X: 1234567, Y: 1234567.
   * @return An array of RT90 coordinates.
   */
  public static int[] parseRT90HsaString(String rt90String) {
    int[] result = null;
    if (!Evaluator.isEmpty(rt90String)) {
      if (rt90String.indexOf("X:") >= 0 && rt90String.indexOf("Y:") >= 0) {
        int rt90X = Integer.parseInt(rt90String.substring(3, 10));
        int rt90Y = Integer.parseInt(rt90String.substring(15));
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
   * Gets a list of units which are in the specified distance of the provided address.
   * 
   * @param address The address to check distance to
   * @param allUnits The list of units to check against.
   * @param meters The distance from the provided address which the units should be within to be a part of the result.
   * @param googleMapsKey The Google Maps key to use.
   * @return A list of Units that is within the distance of the address.
   */
  public ArrayList<Unit> getCloseUnits(String address, ArrayList<Unit> allUnits, int meters, String googleMapsKey) {
    // Create GeoCoordinate from given address
    double[] coordinates = geocodeToWGS84FromString(address, googleMapsKey, GeoAddressAccuracy.POST_CODE_LEVEL);
    ArrayList<Unit> closeUnits = new ArrayList<Unit>();
    if (coordinates == null) {
      // If we could not geocode address, return a list with no units.
      return closeUnits;
    }
    GeoCoordinate targetCoordinate = getGeoCoordinate(coordinates);

    // Calculate distance in miles
    double milesToTarget = getMilesFromMetres(meters);

    for (Unit u : allUnits) {
      if (u.getGeoCoordinate() != null) {
        double distMiles = u.getGeoCoordinate().distanceTo(targetCoordinate);
        if (distMiles < milesToTarget) {
          double dist = distMiles * METRES_PER_MILE / 1000;
          DecimalFormat f = new DecimalFormat("#.##");
          u.setDistanceToTarget(f.format(dist));
          closeUnits.add(u);
        }
      }
    }
    return closeUnits;
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
  private GeoCoordinate getGeoCoordinate(double[] coordinates) {
    // Since altitude is ignored when calculating distance we can skip it.
    return new GeoCoordinate(coordinates[1], coordinates[0], new GeoAltitude());
  }

  /**
   * Sets a geoCoordinate on specified unit.
   * 
   * @param unit The unit to set GeoCoordinate on.
   * @param coordinates The coordinates to use for the GeoCoordinate.
   */
  public void setGeoCoordinate(Unit unit, double[] coordinates) {
    unit.setGeoCoordinate(getGeoCoordinate(coordinates));
  }
}
