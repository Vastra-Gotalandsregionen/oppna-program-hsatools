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

public interface VasttrafikSearcService {
	
	/**
	   * Look up the stop id of a specific address by using the web service that V√§sttrafik provides 
	   * @param address
	   * @return id - E.g 00001100!1876736235 the first 7 digits represents the stop id and the rest of the digits represents the id of the address.    
	   */
	public String getStopIdByAddress(String address, String city);
	
	public void init();

}
