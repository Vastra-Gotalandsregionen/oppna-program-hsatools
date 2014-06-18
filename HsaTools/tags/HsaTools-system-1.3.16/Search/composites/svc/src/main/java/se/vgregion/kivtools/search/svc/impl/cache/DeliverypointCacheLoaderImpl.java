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

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.cache.CacheLoader;
import se.vgregion.kivtools.search.svc.cache.DeliveryPointCache;
import se.vgregion.kivtools.search.svc.cache.UnitCache;

/**
 * Implementation of the DeliverypointCacheLoader using the
 * DeliverypointCacheServiceImpl.
 * 
 * @author kengu5
 */
public class DeliverypointCacheLoaderImpl implements
		CacheLoader<DeliveryPointCache> {
	private final Log log = LogFactory.getLog(getClass());
	private SearchService searchService;
	private UnitCacheServiceImpl unitCacheServiceImpl;

	public DeliverypointCacheLoaderImpl(final SearchService searchService,
			final UnitCacheServiceImpl unitCacheServiceImpl) {
		this.searchService = searchService;
		this.unitCacheServiceImpl = unitCacheServiceImpl;

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeliveryPointCache loadCache() {
		DeliveryPointCache cache = new DeliveryPointCache();
		try {
			List<Deliverypoint> deliverypoints = searchService
					.getAllDeliverypoints();
			if (deliverypoints != null) {
				for (Deliverypoint dp : deliverypoints) {
					cache.add(dp);
				}
			}
		} catch (KivException e) {
			log.error(
					"Something went wrong when retrieving all deliverypoints.",
					e);
		}
		cache.createUnitHsaIdentityMatchOnDeliverPoint();

		UnitCache unitCache = this.unitCacheServiceImpl.getCache();
		this.unitCacheServiceImpl.setCache(addDeliveryPointsToUnitChache(
				unitCache, cache));

		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeliveryPointCache createEmptyCache() {
		return new DeliveryPointCache();
	}

	private UnitCache addDeliveryPointsToUnitChache(UnitCache unitCache,
			DeliveryPointCache dpc) {
		List<Unit> units = unitCache.getUnits();
		UnitCache pUnitCache = new UnitCache();

		for (Unit unit : units) {
			pUnitCache.add(addDeliveryPointToUnit(unit, dpc));
		}

		return pUnitCache;
	}
	/**
	 * 
	 * Adds hsaConsigneeAddress and hsaSedDeliveryAddress to Unit.(from DeliverypointCache)  
	 * 
	 * @param Unit
	 * @param DeliveryPointCache
	 * @return Unit
	 */
	private Unit addDeliveryPointToUnit(Unit u, DeliveryPointCache dpc) {
		Set<Deliverypoint> unitdeliverypoints = dpc
				.getDeliverypointsByUnitHsaId(u.getHsaIdentity());
		if (unitdeliverypoints != null && !unitdeliverypoints.isEmpty()) {
			for (Deliverypoint dp : unitdeliverypoints) {
				if (dp.getHsaSedfDeliveryAddress() != null
						&& !dp.getHsaSedfDeliveryAddress().isEmpty()) {
					u.addDeliverypointDeliveryAddress(dp
							.getHsaSedfDeliveryAddress());
				}
				if (dp.getHsaConsigneeAddress() != null
						&& !dp.getHsaConsigneeAddress().isEmpty()) {
					u.addDeliverypointConsigneeAddress(dp
							.getHsaConsigneeAddress());
				}
			}
		}
		return u;
	}
}
