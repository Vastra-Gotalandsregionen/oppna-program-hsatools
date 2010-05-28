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

import se.vgregion.kivtools.search.svc.ws.domain.vasttrafik.TravelPlanner;
import se.vgregion.kivtools.search.svc.ws.domain.vasttrafik.TravelPlannerSoap;

public class VasttrafikSearchServiceImpl implements VasttrafikSearcService{

  private TravelPlanner travelPlanner;
  private String vasttrafikWebServiceId;
  private TravelPlannerSoap travelPlannerSoap;
  
  @Override
  public String getStopIdByAddress(String address, String city) {
    String xmlString = travelPlannerSoap.getAddressesSuggestions(vasttrafikWebServiceId, address, 10);
    XMLResultParse xmlResultParse = new XMLResultParse();
    return xmlResultParse.getStopId(xmlString, address, city);
    
  }

  public void setVasttrafikWebServiceId(String vasttrafikWebServiceId) {
    this.vasttrafikWebServiceId = vasttrafikWebServiceId;
  }
  
  @Override
  public void init(){
    travelPlanner = new TravelPlanner();
    travelPlannerSoap = travelPlanner.getTravelPlannerSoap12();
    
  }


}
