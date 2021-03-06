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

package se.vgregion.kivtools.search.domain.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.interfaces.UnitComposition;

/**
 * 
 * @author David Bennehult
 * @author Ulf Carlsson
 * 
 * @param <T>
 *            Unit type
 */
public class OrganizationChangeReport<T> {

    private Map<String, List<UnitComposition<T>>> addedUnits;
    private List<UnitComposition<T>> removedUnits;
    private Map<String, List<UnitComposition<T>>> movedUnits;
    private List<UnitComposition<T>> changedUnits;

    /**
     * 
     * @param addedUnits
     *            Units that has been added in organization
     * @param removedUnits
     *            Units that has been removed from organization
     * @param movedUnits
     *            Units that has been moved in organization
     * @param changedUnits
     *            Units that has content change
     */
    public OrganizationChangeReport(Map<String, List<UnitComposition<T>>> addedUnits,
            List<UnitComposition<T>> removedUnits, Map<String, List<UnitComposition<T>>> movedUnits,
            List<UnitComposition<T>> changedUnits) {
        super();
        this.addedUnits = new HashMap<String, List<UnitComposition<T>>>(addedUnits);
        this.removedUnits = new ArrayList<UnitComposition<T>>(removedUnits);
        this.movedUnits = new HashMap<String, List<UnitComposition<T>>>(movedUnits);
        this.changedUnits = new ArrayList<UnitComposition<T>>(changedUnits);
    }

    /**
     * 
     * @return {@link UnitComposition} that has been added to the organization.
     */
    public Map<String, List<UnitComposition<T>>> getAddedOrganizationUnits() {
        return Collections.unmodifiableMap(addedUnits);

    }

    /**
     * 
     * @return {@link UnitComposition} that has been removed from the organization.
     */
    public List<UnitComposition<T>> getRemovedOrganizationUnits() {
        return Collections.unmodifiableList(removedUnits);
    }

    /**
     * 
     * @return {@link Map} with {@link UnitComposition} that has been moved in the organization. Map key is the
     *         parent units hsaId, Map value is a List of the unit children ({@link UnitComposition}) to the
     *         parent.
     */
    public Map<String, List<UnitComposition<T>>> getMovedOrganizationUnits() {
        return Collections.unmodifiableMap(movedUnits);
    }

    /**
     * 
     * @return {@link UnitComposition} that unit information has been changed.
     */
    public List<UnitComposition<T>> getChangedOrganizationUnits() {
        return Collections.unmodifiableList(changedUnits);
    }

    /**
     * 
     * @return true if any unit in the organization has been changed in any way.
     */
    public boolean isOrganizationChanged() {
        return addedUnits.size() > 0 || removedUnits.size() > 0 || movedUnits.size() > 0
                || getChangedOrganizationUnits().size() > 0;
    }

}
