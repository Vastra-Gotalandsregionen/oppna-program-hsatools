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

package se.vgregion.kivtools.search.svc.impl.vasttrafik;

/**
 * Search service for Västtrafik with methods for retrieving a stopId based on an address.
 */
public interface VasttrafikSearchService {
  /**
   * Look up the stop id of a specific street address by using the web service that Västtrafik provides.
   * 
   * @param streetAddress The street address to find a stopId for.
   * @param municipality The municipality the street is in.
   * @return id - E.g 00001100!1876736235 the first 7 digits represents the stop id and the rest of the digits represents the id of the address.
   */
  public String getStopIdByAddress(String streetAddress, String municipality);
}
