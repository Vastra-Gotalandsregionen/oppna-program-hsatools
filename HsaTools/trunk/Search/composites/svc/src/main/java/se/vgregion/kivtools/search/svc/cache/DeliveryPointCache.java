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

package se.vgregion.kivtools.search.svc.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import se.vgregion.kivtools.search.domain.Deliverypoint;
import se.vgregion.kivtools.util.Arguments;
/**
 * A chache for deliverypoints that keeps track of all connections between deliverypoints and units. 
 * 
 * @author kengu5
 */
public class DeliveryPointCache {
	private final List<Deliverypoint> deliverypoints = new ArrayList<Deliverypoint>();
	private final Map<String, Deliverypoint> deliverypointByHsaId = new HashMap<String, Deliverypoint>();
	private final Map<String, Set<Deliverypoint>> unitAndDeliveryPoint = new HashMap<String, Set<Deliverypoint>>();

	public List<Deliverypoint> getDeliverypoints() {
		return Collections.unmodifiableList(deliverypoints);
	}

	/**
	 * Adds a new unit to the cache.
	 * 
	 * @param unit
	 *            The unit to add to the cache.
	 */
	public void add(Deliverypoint deliverypoint) {
		Arguments.notNull("unit", deliverypoint);

		if (!this.deliverypoints.contains(deliverypoint)) {
			this.deliverypoints.add(deliverypoint);
			if (deliverypoint.getHsaIdentity() != null) {
				this.deliverypointByHsaId.put(deliverypoint.getHsaIdentity()
						.toString(), deliverypoint);
			}
		}
	}

	/**
	 * Retrieves a Deliverypoint frim the cache using it's HsaIdentity.
	 * 
	 * @param hsaIdentityStrung
	 * @return the found Deliveryppoint or null if no unit was found.
	 */
	public Deliverypoint getDeliverypointByHsaidenity(String hsaIdentity) {
		return deliverypointByHsaId.get(hsaIdentity);
	}
	/**
	 * 
	 * Creates a new map for all vgrOrgrel. Used to get specifik units Deliverypoint(s). 
	 * 
	 * 
	 */
	public void createUnitHsaIdentityMatchOnDeliverPoint() {
		if (this.deliverypointByHsaId.size() > 0) {
			Iterator<Entry<String, Deliverypoint>> it = this.deliverypointByHsaId
					.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry) it.next();
				for (String pVgrOrgRel : this.deliverypointByHsaId.get(
						pairs.getKey()).getVgrOrgRel()) {
					if (this.unitAndDeliveryPoint.containsKey(pVgrOrgRel)) {
						this.unitAndDeliveryPoint.get(pVgrOrgRel).add(
								this.deliverypointByHsaId.get(pairs.getKey()));
					} else {
						Set<Deliverypoint> pNewUnitPut = new HashSet<Deliverypoint>();
						pNewUnitPut.add(this.deliverypointByHsaId.get(pairs
								.getKey()));
						this.unitAndDeliveryPoint.put(pVgrOrgRel, pNewUnitPut);
						pNewUnitPut = null;
					}
				}
				it.remove(); // avoids a ConcurrentModificationException
			}
		}
	}
	/**
	 * Retrieves Deliverypoints for a specific unit. 
	 * 
	 * @param hsaIdentity on unit (String) 
	 * @return Set<Deliverypoint> 
	 * 
	 */
	public Set<Deliverypoint> getDeliverypointsByUnitHsaId(String hsaId) {
		Set<Deliverypoint> dps = this.unitAndDeliveryPoint.get(hsaId);
		return dps;
	}
}
