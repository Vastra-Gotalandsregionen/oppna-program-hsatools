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
package se.vgregion.kivtools.search.util.geo;

import java.io.IOException;

/**
 * Since the conversion WGS <-> RT90 can be carried out in multiple ways the conversion implementations should implement this simple contract.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public interface CoordinateTransformerService {

  /**
   * Returns RT90 X and Y coordinates for specified Gon.
   * 
   * @param lat Latitude in WGS84 degrees, decimal format
   * @param lon Longitude in WGS84 degrees, decimal format.
   * @return int array with RT90 coordinates.
   * @throws IOException
   */
  public abstract int[] getRT90(double lat, double lon) throws IOException;

  /**
   * Returns WGS84 latitude and longitude in degrees, decimal format.
   * 
   * @param x RT90 X coordinate.
   * @param y RT90 Y coordinate.
   * @return double array with WGS84 coordinates, degrees in decimal format.
   */
  public abstract double[] getWGS84(int x, int y);

}