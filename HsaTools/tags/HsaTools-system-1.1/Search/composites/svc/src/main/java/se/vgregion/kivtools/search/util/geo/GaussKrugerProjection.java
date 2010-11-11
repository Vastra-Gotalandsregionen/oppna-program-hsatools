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

/**
 * Uses "Gauss konforma projektion" for conversion WGS84 <-> RT90.
 * 
 * @see http://sv.wikipedia.org/wiki/Gauss_projektion
 * @see http://www.lantmateriet.se/templates/LMV_Page.aspx?id=4766
 * @see http://www.lantmateriet.se/upload/filer/kartor/geodesi_gps_och_detaljmatning/geodesi/Formelsamling/Gauss_Conformal_Projection.pdf
 * @see http://www.lantmateriet.se/upload/filer/kartor/geodesi_gps_och_detaljmatning/Transformationer/SWEREF99_RT90_Samband/Transformationsparametrar_pdf.pdf
 * @see http://www.lantmateriet.se/templates/LMV_Page.aspx?id=3003
 * @see http://www.lantmateriet.se/upload/filer/kartor/geodesi_gps_och_detaljmatning/Kartprojektioner/Oversikt/kartprojektioner_oversikt.pdf
 * @see http://www.lantmateriet.se/upload/filer/kartor/geodesi_gps_och_detaljmatning/Kartprojektioner/Oversikt/Gauss-kruger_projektion.pdf
 * @see http://www.lantmateriet.se/templates/LMV_Page.aspx?id=5197
 * @see http://sv.wikipedia.org/wiki/RT_90
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class GaussKrugerProjection implements CoordinateTransformerService {
  // GRS 80 Ellipsoid Characteristics:
  // Semi Major axis
  private static double majorAxis = 6378137.0;
  // Flattening
  private static double flattening = 1.0 / 298.2572221010;

  // RT90 0 gon V 0:-15 fields (Use around Stockholm)
  // Centrum meridian
  private static final String CM_0V = "18D03.2268\"E";
  // Scale factor
  private static final double K0_0V = 1.000005400000;
  // False North
  private static final double FN_0V = -668.844;
  // False East
  private static final double FE_0V = 1500083.521;

  // RT90 2.5 gon V 0:-15 fields (Örebro)
  private static final String CM_25V = "15D48.22624306\"E";
  private static final double K0_25V = 1.00000561024;
  private static final double FN_25V = -667.711;
  private static final double FE_25V = 1500064.274;

  // RT90 5 gon V 0:-15 fields (Malmö)
  private static final String CM_5V = "13D33.376\"E";
  private static final double K0_5V = 1.000005800000;
  private static final double FN_5V = -667.130;
  private static final double FE_5V = 1500044.695;

  // RT90 7.5 gon V 0:-15 fields (Göteborg)
  private static final String CM_75V = "11D18.375\"E";
  private static final double K0_75V = 1.000006000000;
  private static final double FN_75V = -667.282;
  private static final double FE_75V = 1500025.141;

  // RT90 2.5 gon O 0:-15 fields (Umeå)
  private static final String CM_25O = "20D18.379\"E";
  private static final double K0_25O = 1.000005200000;
  private static final double FN_25O = -670.706;
  private static final double FE_25O = 1500102.765;

  // RT90 5 gon O 0:-15 fields (Luleå)
  private static final String CM_5O = "22D33.380\"E";
  private static final double K0_5O = 1.000004900000;
  private static final double FN_5O = -672.557;
  private static final double FE_5O = 1500121.846;

  // Variables
  private String cm;
  private double k0;
  private double fn;
  private double fe;
  // Geodetic latitude
  private double lat;
  // Geodetic longitude
  private double lon;
  // Gauss-Krüger Projection variables
  private double a;
  private double b;
  private double c;
  private double d;
  private double beta1;
  private double beta2;
  private double beta3;
  private double beta4;
  private double e2;
  private double n;
  private double aHat;
  private double delta1;
  private double delta2;
  private double delta3;
  private double delta4;
  private double aStar;
  private double bStar;
  private double cStar;
  private double dStar;

  // RT90 coordinates
  private double rt90X;
  private double rt90Y;

  // WGS84 coordinates

  /**
   * Constructor with specified gon.
   * 
   * @param gon Specified Gon
   * @throws IllegalArgumentException If an unknown Gon is specified.
   */
  public GaussKrugerProjection(String gon) {
    if (gon != null) {
      if (gon.equals("2.5V")) {
        cm = CM_25V;
        k0 = K0_25V;
        fn = FN_25V;
        fe = FE_25V;
      } else if (gon.equals("5V")) {
        cm = CM_5V;
        k0 = K0_5V;
        fn = FN_5V;
        fe = FE_5V;
      } else if (gon.equals("7.5V")) {
        cm = CM_75V;
        k0 = K0_75V;
        fn = FN_75V;
        fe = FE_75V;
      } else if (gon.equals("0V")) {
        cm = CM_0V;
        k0 = K0_0V;
        fn = FN_0V;
        fe = FE_0V;
      } else if (gon.equals("2.50")) {
        cm = CM_25O;
        k0 = K0_25O;
        fn = FN_25O;
        fe = FE_25O;
      } else if (gon.equals("50")) {
        cm = CM_5O;
        k0 = K0_5O;
        fn = FN_5O;
        fe = FE_5O;
      } else {
        throw new IllegalArgumentException("Specified Gon isn't recognized: " + gon);
      }
    } else {
      throw new IllegalArgumentException("Specified Gon isn't recognized: " + gon);
    }
    this.initialize();
  }

  /**
   * Default constructor using 2.5 V 0:-15 (as in standard RT90).
   */
  public GaussKrugerProjection() {
    // USE 2.5 V 0:-15 as default
    cm = CM_25V;
    k0 = K0_25V;
    fn = FN_25V;
    fe = FE_25V;
    this.initialize();
  }

  /**
   * Calculate constants.
   */
  private void initialize() {
    e2 = flattening * (2.0 - flattening);
    n = flattening / (2.0 - flattening);
    aHat = majorAxis / (1.0 + n) * (1.0 + 0.25 * Math.pow(n, 2) + 1.0 / 64.0 * Math.pow(n, 4));
    a = e2;
    b = 1.0 / 6.0 * (5.0 * Math.pow(a, 2) - Math.pow(a, 3));
    c = 1.0 / 120.0 * (104.0 * Math.pow(a, 3) - 45.0 * Math.pow(a, 4));
    d = 1.0 / 1260.0 * 1237.0 * Math.pow(a, 4);

    beta1 = 0.5 * n - 2.0 / 3.0 * Math.pow(n, 2) + 5.0 / 16.0 * Math.pow(n, 3) + 41.0 / 180.0 * Math.pow(n, 4);
    beta2 = 13.0 / 48.0 * Math.pow(n, 2) - 3.0 / 5.0 * Math.pow(n, 3) + 557.0 / 1440.0 * Math.pow(n, 4);
    beta3 = 61.0 / 240.0 * Math.pow(n, 3) - 103.0 / 140.0 * Math.pow(n, 4);
    beta4 = 49561.0 / 161280.0 * Math.pow(n, 4);

    delta1 = 1.0 / 2.0 * n - 2.0 / 3.0 * Math.pow(n, 2) + 37.0 / 96.0 * Math.pow(a, 3) - 1.0 / 360.0 * Math.pow(n, 4);
    delta2 = 1.0 / 48.0 * Math.pow(n, 2) + 1.0 / 15.0 * Math.pow(n, 3) - 437.0 / 1440.0 * Math.pow(n, 4);
    delta3 = 17.0 / 480.0 * Math.pow(n, 3) - 37.0 / 840.0 * Math.pow(n, 4);
    delta4 = 4397.0 / 161280.0 * Math.pow(n, 4);

    aStar = e2 + Math.pow(e2, 2) + Math.pow(e2, 3) + Math.pow(e2, 4);
    bStar = -(1.0 / 6.0) * (7 * Math.pow(e2, 2) + 17.0 * Math.pow(e2, 3) + 30 * Math.pow(e2, 4));
    cStar = 1.0 / 120.0 * (224 * Math.pow(e2, 3) + 889.0 * Math.pow(e2, 4));
    dStar = -(1.0 / 1260.0) * 4279.0 * Math.pow(e2, 4);
  }

  /**
   * Calculate grid coordinates with Gauss-Kruger projection method.
   * 
   * @param latitude Latitude in decimal format.
   * @param longitude Longitude in decimal format.
   */
  private void calcGaussKrugerProjectionFromGeodeticToGrid(double latitude, double longitude) {
    // Compute the Conformal Latitude
    double phiStar = latitude - Math.sin(latitude) * Math.cos(latitude) * (a + b * Math.pow(Math.sin(latitude), 2) + c * Math.pow(Math.sin(latitude), 4) + d * Math.pow(Math.sin(latitude), 6));

    // Difference in longitude
    double dLon = longitude - GeoUtil.getLatLongRadiansDecimal(cm, true);

    // Get Angles:
    double chi = Math.atan(Math.tan(phiStar) / Math.cos(dLon));

    // Since Atanh isn't represented in the Math-class
    // we'll use a simplification that holds for real z < 1
    // Ref:
    // http://mathworld.wolfram.com/InverseHyperbolicTangent.html
    double z = Math.cos(phiStar) * Math.sin(dLon);
    double eta = 0.5 * Math.log((1.0 + z) / (1.0 - z));

    // Calculate the carthesian (grid) coordinates in RT90
    rt90X = k0
        * aHat
        * (chi + beta1 * Math.sin(2.0 * chi) * Math.cosh(2.0 * eta) + beta2 * Math.sin(4.0 * chi) * Math.cosh(4.0 * eta) + beta3 * Math.sin(6.0 * chi) * Math.cosh(6.0 * eta) + beta4
            * Math.sin(8.0 * chi) * Math.cosh(8.0 * eta)) + fn;

    rt90Y = k0
        * aHat
        * (eta + beta1 * Math.cos(2.0 * chi) * Math.sinh(2.0 * eta) + beta2 * Math.cos(4.0 * chi) * Math.sinh(4.0 * eta) + beta3 * Math.cos(6.0 * chi) * Math.sinh(6.0 * eta) + beta4
            * Math.cos(8.0 * chi) * Math.sinh(8.0 * eta)) + fe;
  }

  /**
   * Calculate grid coordinates with Gauss-Kruger projection method.
   * 
   * @param x Latitude in RT 90.
   * @param y Longitude in RT 90
   */
  private void calcGaussKrugerProjectionFromGridToGeodetic(int x, int y) {
    double chi = (x - fn) / (k0 * aHat);
    double eta = (y - fe) / (k0 * aHat);

    double chiPrim = chi - delta1 * Math.sin(2 * chi) * Math.cosh(2 * eta) - delta2 * Math.sin(4 * chi) * Math.cosh(4 * eta) - delta3 * Math.sin(6 * chi) * Math.cosh(6 * eta) - delta4
        * Math.sin(8 * chi) * Math.cosh(8 * eta);

    double etaPrim = eta - delta1 * Math.cos(2 * chi) * Math.sinh(2 * eta) - delta2 * Math.cos(4 * chi) * Math.sinh(4 * eta) - delta3 * Math.cos(6 * chi) * Math.sinh(6 * eta) - delta4
        * Math.cos(8 * chi) * Math.sinh(8 * eta);

    // Compute the Conformal Latitude
    double phiStar = Math.asin(Math.sin(chiPrim) / Math.cosh(etaPrim));

    // Difference in longitude
    double dLon = Math.atan(Math.sinh(etaPrim) / Math.cos(chiPrim));

    // Eventually the latitude and longitude angles are calculated.
    lon = GeoUtil.getLatLongRadiansDecimal(cm, true) + dLon;

    lat = phiStar + Math.sin(phiStar) * Math.cos(phiStar) * (aStar + bStar * Math.pow(Math.sin(phiStar), 2) + cStar * Math.pow(Math.sin(phiStar), 4) + dStar * Math.pow(Math.sin(phiStar), 6));
  }

  /**
   * {@inheritDoc}
   */
  public int[] getRT90(double latitude, double longitude) {
    this.lat = latitude;
    this.lon = longitude;

    // Degrees -> radians
    this.lat = latitude * Math.PI / 180.0;
    this.lon = longitude * Math.PI / 180.0;

    // Calculate Projection on the RT90-grid
    this.calcGaussKrugerProjectionFromGeodeticToGrid(this.lat, this.lon);

    // Return x,y array. Not nice conversion but works :)
    int[] rt90Coordinates = { Integer.parseInt(String.valueOf(Math.round(rt90X))), Integer.parseInt(String.valueOf(Math.round(rt90Y))) };
    return rt90Coordinates;
  }

  /**
   * {@inheritDoc}
   */
  public double[] getWGS84(int x, int y) {
    rt90X = x;
    rt90Y = y;

    // Calculate geodetic coordinates from RT90 coordinates
    this.calcGaussKrugerProjectionFromGridToGeodetic(Integer.parseInt(String.valueOf(Math.round(rt90X))), Integer.parseInt(String.valueOf(Math.round(rt90Y))));

    // Convert WGS84 coordinates from radians to decimal degrees
    double wgs84LatDegree = lat / (Math.PI / 180.0);
    double wgs84LonDegree = lon / (Math.PI / 180.0);

    // Return lat, lon array
    double[] wgs84Coordinates = { wgs84LatDegree, wgs84LonDegree };
    return wgs84Coordinates;
  }
}
