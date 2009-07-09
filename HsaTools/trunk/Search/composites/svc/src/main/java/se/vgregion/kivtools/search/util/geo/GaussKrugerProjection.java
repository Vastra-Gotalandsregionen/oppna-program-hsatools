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
  private static double a = 6378137.0;
  // Flattening
  private static double f = 1.0 / 298.2572221010;

  // RT90 0 gon V 0:-15 fields (Use around Stockholm)
  // Centrum meridian
  private static String CM_0V = "18D03.2268\"E";
  // Scale factor
  private static double k0_0V = 1.000005400000;
  // False North
  private static double FN_0V = -668.844;
  // False East
  private static double FE_0V = 1500083.521;

  // RT90 2.5 gon V 0:-15 fields (Örebro)
  private static String CM_25V = "15D48.22624306\"E";
  private static double k0_25V = 1.00000561024;
  private static double FN_25V = -667.711;
  private static double FE_25V = 1500064.274;

  // RT90 5 gon V 0:-15 fields (Malmö)
  private static String CM_5V = "13D33.376\"E";
  private static double k0_5V = 1.000005800000;
  private static double FN_5V = -667.130;
  private static double FE_5V = 1500044.695;

  // RT90 7.5 gon V 0:-15 fields (Göteborg)
  private static String CM_75V = "11D18.375\"E";
  private static double k0_75V = 1.000006000000;
  private static double FN_75V = -667.282;
  private static double FE_75V = 1500025.141;

  // RT90 2.5 gon O 0:-15 fields (Umeå)
  private static String CM_25O = "20D18.379\"E";
  private static double k0_25O = 1.000005200000;
  private static double FN_25O = -670.706;
  private static double FE_25O = 1500102.765;

  // RT90 5 gon O 0:-15 fields (Luleå)
  private static String CM_5O = "22D33.380\"E";
  private static double k0_5O = 1.000004900000;
  private static double FN_5O = -672.557;
  private static double FE_5O = 1500121.846;

  // Variables
  private String CM;
  private double k0;
  private double FN;
  private double FE;
  private double lat; // Geodetic latitude
  private double lon; // Geodetic longitude
  // Gauss-Krüger Projection variables
  private double A, B, C, D, beta1, beta2, beta3, beta4, e2, n, aHat, delta1, delta2, delta3, delta4, Astar, Bstar, Cstar, Dstar;

  // RT90 coordinates
  private double rt90X;
  private double rt90Y;

  // WGS84 coordinates

  /**
   * Constructor with specified gon.
   * 
   * @param Specified Gon
   */
  public GaussKrugerProjection(String gon) throws Exception {
    if (gon != null) {
      if (gon.equals("2.5V")) {
        CM = CM_25V;
        k0 = k0_25V;
        FN = FN_25V;
        FE = FE_25V;
      } else if (gon.equals("5V")) {
        CM = CM_5V;
        k0 = k0_5V;
        FN = FN_5V;
        FE = FE_5V;
      } else if (gon.equals("7.5V")) {
        CM = CM_75V;
        k0 = k0_75V;
        FN = FN_75V;
        FE = FE_75V;
      } else if (gon.equals("0V")) {
        CM = CM_0V;
        k0 = k0_0V;
        FN = FN_0V;
        FE = FE_0V;
      } else if (gon.equals("2.50")) {
        CM = CM_25O;
        k0 = k0_25O;
        FN = FN_25O;
        FE = FE_25O;
      } else if (gon.equals("50")) {
        CM = CM_5O;
        k0 = k0_5O;
        FN = FN_5O;
        FE = FE_5O;
      } else {
        throw new Exception("Specified Gon isn't recognized");
      }
    } else {
      throw new Exception("Specified Gon isn't recognized");
    }
    this.initialize();
  }

  /**
   * Default constructor using 2.5 V 0:-15 (as in standard RT90).
   */
  public GaussKrugerProjection() {
    // USE 2.5 V 0:-15 as default
    CM = CM_25V;
    k0 = k0_25V;
    FN = FN_25V;
    FE = FE_25V;
    this.initialize();
  }

  /**
   * Calculate constants.
   */
  private void initialize() {
    e2 = f * (2.0 - f);
    n = f / (2.0 - f);
    aHat = a / (1.0 + n) * (1.0 + 0.25 * Math.pow(n, 2) + 1.0 / 64.0 * Math.pow(n, 4));
    A = e2;
    B = 1.0 / 6.0 * (5.0 * Math.pow(A, 2) - Math.pow(A, 3));
    C = 1.0 / 120.0 * (104.0 * Math.pow(A, 3) - 45.0 * Math.pow(A, 4));
    D = 1.0 / 1260.0 * 1237.0 * Math.pow(A, 4);

    beta1 = 0.5 * n - 2.0 / 3.0 * Math.pow(n, 2) + 5.0 / 16.0 * Math.pow(n, 3) + 41.0 / 180.0 * Math.pow(n, 4);
    beta2 = 13.0 / 48.0 * Math.pow(n, 2) - 3.0 / 5.0 * Math.pow(n, 3) + 557.0 / 1440.0 * Math.pow(n, 4);
    beta3 = 61.0 / 240.0 * Math.pow(n, 3) - 103.0 / 140.0 * Math.pow(n, 4);
    beta4 = 49561.0 / 161280.0 * Math.pow(n, 4);

    delta1 = 1.0 / 2.0 * n - 2.0 / 3.0 * Math.pow(n, 2) + 37.0 / 96.0 * Math.pow(A, 3) - 1.0 / 360.0 * Math.pow(n, 4);
    delta2 = 1.0 / 48.0 * Math.pow(n, 2) + 1.0 / 15.0 * Math.pow(n, 3) - 437.0 / 1440.0 * Math.pow(n, 4);
    delta3 = 17.0 / 480.0 * Math.pow(n, 3) - 37.0 / 840.0 * Math.pow(n, 4);
    delta4 = 4397.0 / 161280.0 * Math.pow(n, 4);

    Astar = e2 + Math.pow(e2, 2) + Math.pow(e2, 3) + Math.pow(e2, 4);
    Bstar = -(1.0 / 6.0) * (7 * Math.pow(e2, 2) + 17.0 * Math.pow(e2, 3) + 30 * Math.pow(e2, 4));
    Cstar = 1.0 / 120.0 * (224 * Math.pow(e2, 3) + 889.0 * Math.pow(e2, 4));
    Dstar = -(1.0 / 1260.0) * 4279.0 * Math.pow(e2, 4);
  }

  /**
   * Calculate grid coordinates with Gauss-Kruger projection method.
   * 
   * @param lat Latitude in decimal format.
   * @param lon Longitude in decimal format.
   */
  private void calcGaussKrugerProjectionFromGeodeticToGrid(double lat, double lon) {
    // Compute the Conformal Latitude
    double phiStar = lat - Math.sin(lat) * Math.cos(lat) * (A + B * Math.pow(Math.sin(lat), 2) + C * Math.pow(Math.sin(lat), 4) + D * Math.pow(Math.sin(lat), 6));

    // Difference in longitude
    double dLon = lon - GeoUtil.getLatLongRadiansDecimal(CM, true);

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
            * Math.sin(8.0 * chi) * Math.cosh(8.0 * eta)) + FN;

    rt90Y = k0
        * aHat
        * (eta + beta1 * Math.cos(2.0 * chi) * Math.sinh(2.0 * eta) + beta2 * Math.cos(4.0 * chi) * Math.sinh(4.0 * eta) + beta3 * Math.cos(6.0 * chi) * Math.sinh(6.0 * eta) + beta4
            * Math.cos(8.0 * chi) * Math.sinh(8.0 * eta)) + FE;
  }

  /**
   * Calculate grid coordinates with Gauss-Kruger projection method.
   * 
   * @param x Latitude in RT 90.
   * @param y Longitude in RT 90
   */
  private void calcGaussKrugerProjectionFromGridToGeodetic(int x, int y) {
    double chi = (x - FN) / (k0 * aHat);
    double eta = (y - FE) / (k0 * aHat);

    double chiPrim = chi - delta1 * Math.sin(2 * chi) * Math.cosh(2 * eta) - delta2 * Math.sin(4 * chi) * Math.cosh(4 * eta) - delta3 * Math.sin(6 * chi) * Math.cosh(6 * eta) - delta4
        * Math.sin(8 * chi) * Math.cosh(8 * eta);

    double etaPrim = eta - delta1 * Math.cos(2 * chi) * Math.sinh(2 * eta) - delta2 * Math.cos(4 * chi) * Math.sinh(4 * eta) - delta3 * Math.cos(6 * chi) * Math.sinh(6 * eta) - delta4
        * Math.cos(8 * chi) * Math.sinh(8 * eta);

    // Compute the Conformal Latitude
    double phiStar = Math.asin(Math.sin(chiPrim) / Math.cosh(etaPrim));

    // Difference in longitude
    double dLon = Math.atan(Math.sinh(etaPrim) / Math.cos(chiPrim));

    // Eventually the latitude and longitude angles are calculated.
    lon = GeoUtil.getLatLongRadiansDecimal(CM, true) + dLon;

    lat = phiStar + Math.sin(phiStar) * Math.cos(phiStar) * (Astar + Bstar * Math.pow(Math.sin(phiStar), 2) + Cstar * Math.pow(Math.sin(phiStar), 4) + Dstar * Math.pow(Math.sin(phiStar), 6));
  }

  /*
   * (non-Javadoc)
   * 
   * @see se.vgregion.kivtools.search.util.CoordinateTransformerService#getRT90(double, double)
   */
  public int[] getRT90(double lat, double lon) {
    this.lat = lat;
    this.lon = lon;

    // Degrees -> radians
    this.lat = lat * Math.PI / 180.0;
    this.lon = lon * Math.PI / 180.0;

    // Calculate Projection on the RT90-grid
    this.calcGaussKrugerProjectionFromGeodeticToGrid(this.lat, this.lon);

    // Return x,y array. Not nice conversion but works :)
    int[] RT90Coordinates = { Integer.parseInt(String.valueOf(Math.round(rt90X))), Integer.parseInt(String.valueOf(Math.round(rt90Y))) };
    return RT90Coordinates;
  }

  /*
   * (non-Javadoc)
   * 
   * @see se.vgregion.kivtools.search.util.CoordinateTransformerService#getWGS84(int, int)
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
    double[] WGS84Coordinates = { wgs84LatDegree, wgs84LonDegree };
    return WGS84Coordinates;
  }
}