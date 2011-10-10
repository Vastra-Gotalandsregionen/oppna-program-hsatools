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

package se.vgregion.kivtools.search.svc.ws.vasttrafik;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.impl.vasttrafik.VasttrafikSearchServiceImpl;
import se.vgregion.kivtools.search.svc.ws.domain.vasttrafik.TravelPlanner;
import se.vgregion.kivtools.search.svc.ws.domain.vasttrafik.TravelPlannerSoap;

public class VasttrafikIntegrationTest {
	
	private TravelPlanner travelPlanner;
	private VasttrafikSearchServiceImpl vasttrafikSearchService;


	@Before 
	  public void setup(){
	    travelPlanner = new TravelPlanner();
	    vasttrafikSearchService = new VasttrafikSearchServiceImpl(); 
	    vasttrafikSearchService.init();
	  }
	  
	  @Test
	  public void testGetAllStops(){
	    TravelPlannerSoap travelPlannerHttpPost = travelPlanner.getTravelPlannerSoap12();
	    String allStops = travelPlannerHttpPost.getAllStops("4ccae049-078a-4ea7-85bb-7b5d7d4c15ac");
	    assertNotNull(allStops);
	  }
	  
	  @Test
	  public void getGetStopIdByAddress(){
	    String stopId = vasttrafikSearchService.getStopIdByAddress("Per Dubbsgatan 15", "Göteborg");
	    assertNotNull(stopId);
	    assertEquals("00001100!1876736235", stopId);
	    
	  }


}
