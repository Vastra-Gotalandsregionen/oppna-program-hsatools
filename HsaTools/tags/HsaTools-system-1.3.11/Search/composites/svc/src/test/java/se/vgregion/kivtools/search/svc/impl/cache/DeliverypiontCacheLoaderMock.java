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

package se.vgregion.kivtools.search.svc.impl.cache;

import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.DeliveryPointCache;

public class DeliverypiontCacheLoaderMock implements CacheLoader<DeliveryPointCache>{

	@Override
	public DeliveryPointCache loadCache() {
		DeliveryPointCache dpc = new DeliveryPointCache(); 
		List<String> vgrOrgRel = new ArrayList<String>(); 
		vgrOrgRel.add("ABC-123");
		vgrOrgRel.add("XYZ-987");
		this.createDeliverypoint("SE2321000131-S000000012908", "SE2321000131-S000000012908",this.createNewAddress("Götlaborg", "Avenyn",new ZipCode("12345")), this.createNewAddress("Götlaborg", "Avenyn på hörnet 2",new ZipCode("12345")) ,"7332784040617" , vgrOrgRel);
		return dpc;
	}

	@Override
	public DeliveryPointCache createEmptyCache() {
		DeliveryPointCache dpc = new DeliveryPointCache(); 
		
		return dpc;
	}
	
	private Deliverypoint createDeliverypoint(String cn,String hsaIdentity,Address hsaConsigneeAddress,Address hsaSedfDeliveryAddress,String vgrEanCode, List<String> vgrOrgRel){
		Deliverypoint dp = new Deliverypoint();
		dp.setCn(cn); 
		dp.setHsaIdentity(hsaIdentity);
		dp.setHsaConsigneeAddress(hsaConsigneeAddress);
		dp.setHsaSedfDeliveryAddress(hsaSedfDeliveryAddress); 
		dp.setVgrEanCode(vgrEanCode); 
		dp.setVgrOrgRel(vgrOrgRel); 
		
		
		return dp; 
	}
	
	private Address createNewAddress(String city,String street,ZipCode zipCode){
		Address ad = new Address(); 
		ad.setCity(city); 
		ad.setStreet(street); 
		ad.setZipCode(zipCode);
		
		return ad;
	}
	
}