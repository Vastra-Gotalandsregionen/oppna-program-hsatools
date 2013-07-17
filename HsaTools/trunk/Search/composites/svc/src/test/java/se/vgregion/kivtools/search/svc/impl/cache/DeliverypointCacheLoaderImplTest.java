package se.vgregion.kivtools.search.svc.impl.cache;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.SearchServiceLdapImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import com.domainlanguage.time.TimePoint;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.cache.DeliveryPointCache;
import se.vgregion.kivtools.search.svc.cache.UnitCache;
import se.vgregion.kivtools.search.svc.impl.cache.UnitCacheLoaderImpl;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

/**
 * Copyright 2010 Västra Götalandsregionen
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of version 2.1 of the GNU Lesser General Public License as
 * published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

public class DeliverypointCacheLoaderImplTest {
	private SearchServiceMock searchService;

	private UnitCacheLoaderMock unitCacheLoader;
	private UnitCacheServiceImpl unitCacheService; 
	private DeliverypointCacheLoaderImpl deliverypointCache; 
	@Before 
	public void init(){
		this.searchService = new SearchServiceMock();
		this.unitCacheLoader = new UnitCacheLoaderMock();
		this.unitCacheService =  new UnitCacheServiceImpl(
				unitCacheLoader);
		this.unitCacheService.setCache(this.unitCacheLoader.loadCache());
		
		this.deliverypointCache =new DeliverypointCacheLoaderImpl(
				searchService, unitCacheService);
	}
	@Test
	public void cacheIsEmptyIfSearchServiceDoesNotReturnAnyHsaIdentities() {
		DeliveryPointCache dpc = this.deliverypointCache.loadCache(); 
		assertNotNull(dpc);
		assertEquals(2,dpc.getDeliverypoints().size()); 
	}

	@Test
	public void createEmptyCacheReturnNewEmptyCacheEachTime() {
		DeliveryPointCache emptyCache1 = deliverypointCache.createEmptyCache();
		DeliveryPointCache emptyCache2 = deliverypointCache.createEmptyCache();
		assertEquals(0, emptyCache1.getDeliverypoints().size());
		assertEquals(emptyCache1.getDeliverypoints(), emptyCache2.getDeliverypoints());
		assertNotSame(emptyCache1, emptyCache2);
	}

	@Test
	public void deliverypointsAreFetchedAndAddedToCache() {
		
		DeliveryPointCache dpc = this.deliverypointCache.loadCache(); 
		assertEquals(2, dpc.getDeliverypoints().size());
		
		dpc.add(createDeliverypoint("SE2321000131-S000000012908")); 
		assertEquals(3, dpc.getDeliverypoints().size());
	}

	@Test 
	public void unitAreMappedFromDeliverypoint(){
		UnitCache uc = this.unitCacheService.getCache();
		DeliveryPointCache dpc = this.deliverypointCache.loadCache(); 
		
		Unit u = uc.getUnitByDnString("ou=test1,ou=org,o=vgr");
		
		assertNotNull(u);
		assertEquals("ABC-123",u.getHsaIdentity());
		assertEquals("Göteborg",u.getDeliverypointDeliveryAddress().get(0).getCity());
		assertEquals("Göteborg",u.getDeliverypointDeliveryAddress().get(1).getCity());
		assertEquals("Avenyn", u.getDeliverypointConsigneeAddress().get(0).getStreet());
			
		
	}
	
	
	private Deliverypoint createDeliverypoint(String hsaIdentity) {
		Deliverypoint dp = new Deliverypoint();
		dp.setHsaIdentity(hsaIdentity);
		return dp;
	}

	private static class SearchServiceMock implements SearchService {

		@Override
		public SikSearchResultList<Unit> searchUnits(
				SearchUnitCriterions searchUnitCriterions, int maxSearchResult)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit,
				int maxSearchResult, Comparator<Unit> sortOrder,
				boolean onlyPublicUnits) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Unit getUnitByHsaId(String hsaId) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Unit getUnitByDN(String dn) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Unit> getSubUnits(Unit parentUnit,
				int maxSearchResult) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Person> searchPersonsByDn(String dn,
				int maxSearchResult) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Person> searchPersons(String id,
				int maxSearchResult) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Person> searchPersons(
				SearchPersonCriterions person, int maxResult)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Person getPersonById(String id) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Employment> getEmployments(String personDn)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Deliverypoint> getAllDeliverypoints() throws KivException {
			List<Deliverypoint> dpl = new ArrayList<Deliverypoint>();
			
			List<String> vgrOrgRel = new ArrayList<String>(); 
			vgrOrgRel.add("ABC-123");
			vgrOrgRel.add("XYZ-987");
			Deliverypoint dp = this.createDeliverypoint("SE2321000131-S000000012908", "SE2321000131-S000000012908",this.createNewAddress("Göteborg", "Avenyn",new ZipCode("45154")), this.createNewAddress("Göteborg", "Avenyn på hörnet 2",new ZipCode("12345")) ,"7332784040617" , vgrOrgRel);// TODO Auto-generated method stub
			
			dpl.add(dp); 
			
			vgrOrgRel.add("ABC-123");
			Deliverypoint dp2 = this.createDeliverypoint("SE2321000131-S000000012901", "SE2321000131-S000000012901",this.createNewAddress("Göteborg", "Avenyn",new ZipCode("12345")), this.createNewAddress("Göteborg", "Kungsgatan 2",new ZipCode("54154")) ,"7332784040611" , vgrOrgRel);// TODO Auto-generated method stub
			
			dpl.add(dp2); 
			return dpl;
		}

		@Override
		public List<String> getAllUnitsHsaIdentity() throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getAllUnitsHsaIdentity(boolean onlyPublicUnits)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Unit> getAllUnits(boolean onlyPublicUnits)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getAllPersonsId() throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Person> getAllPersons() throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Employment> getEmploymentsForPerson(Person person)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units,
				int maxResult) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Person getPersonByDn(String personDn) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public byte[] getProfileImageByDn(String dn) throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SikSearchResultList<Unit> getFirstLevelSubUnits(Unit parentUnit)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Unit getUnitByHsaIdAndHasNotCareTypeInpatient(String hsaId)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getUnitAdministratorVgrIds(String hsaId)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Person> getPersonByVgrManagedObject(String managedObject)
				throws KivException {
			// TODO Auto-generated method stub
			return null;
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
}
