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

package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.util.OrganizationChangeReport;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

public class VgrOrganizationChangeReporter implements OrganizationChangeReporter<Unit> {

    private List<UnitComposition<Unit>> listSortedByDn;

    @Override
    public OrganizationChangeReport<Unit> createOrganizationChangeReport(
            List<UnitComposition<Unit>> oldFlatOrganization, List<UnitComposition<Unit>> newFlatOrganization) {

        List<UnitComposition<Unit>> removedUnits = new ArrayList<UnitComposition<Unit>>();
        Map<String, List<UnitComposition<Unit>>> movedUnits = new HashMap<String, List<UnitComposition<Unit>>>();
        List<UnitComposition<Unit>> changedUnits = new ArrayList<UnitComposition<Unit>>();
        Map<String, List<UnitComposition<Unit>>> addedUnits = new HashMap<String, List<UnitComposition<Unit>>>();

        // Ensure list sorted
        Collections.sort(oldFlatOrganization);
        // Ensure list sorted
        Collections.sort(newFlatOrganization);

        List<UnitComposition<Unit>> unitCompositionsSortedByDn = getSortedListByDn(newFlatOrganization);

        for (UnitComposition<Unit> oldUnitComposition : oldFlatOrganization) {
            // Search for unit in list
            int index = Collections.binarySearch(newFlatOrganization, oldUnitComposition);
            if (index < 0) {
                removedUnits.add(oldUnitComposition);
            } else {
                UnitComposition<Unit> newUnitComposition = newFlatOrganization.get(index);
                if (!newUnitComposition.getDn().equals(oldUnitComposition.getDn())) {

                    UnitComposition<Unit> parentUnitComposition = findParentUnitCompositionInList(
                            unitCompositionsSortedByDn, newUnitComposition);
                    // Put the current moved unitcomposition in map with the parent unitcompositions's hsaIdentity
                    // as key.
                    putUnitCompositionInMap(movedUnits, newUnitComposition, parentUnitComposition);
                }
                if (isUnitChanged(newUnitComposition, oldUnitComposition)) {
                    changedUnits.add(newUnitComposition);
                }
            }
        }

        for (UnitComposition<Unit> newUnitComposition : newFlatOrganization) {
            // Search for unit in list
            int index = Collections.binarySearch(oldFlatOrganization, newUnitComposition);
            if (index < 0) {
                UnitComposition<Unit> parentUnitComposition = findParentUnitCompositionInList(
                        unitCompositionsSortedByDn, newUnitComposition);

                putUnitCompositionInMap(addedUnits, newUnitComposition, parentUnitComposition);
            }
        }
        OrganizationChangeReport<Unit> organizationChangeReport = new OrganizationChangeReport<Unit>(addedUnits,
                removedUnits, movedUnits, changedUnits);
        return organizationChangeReport;
    }

    private void putUnitCompositionInMap(Map<String, List<UnitComposition<Unit>>> movedUnits,
            UnitComposition<Unit> newUnitComposition, UnitComposition<Unit> parentUnitComposition) {
        String parentUnitHsaId = "";
        if (parentUnitComposition != null) {
            parentUnitHsaId = parentUnitComposition.getUnit().getHsaIdentity();
        }
        List<UnitComposition<Unit>> list = movedUnits.get(parentUnitHsaId);
        if (list == null) {
            list = new ArrayList<UnitComposition<Unit>>();
            movedUnits.put(parentUnitHsaId, list);
        }
        list.add(newUnitComposition);
    }

    private UnitComposition<Unit> findParentUnitCompositionInList(List<UnitComposition<Unit>> listSorted,
            UnitComposition<Unit> unitComposition) {
        UnitComposition<Unit> parentUnitComposition = null;
        // Lookup index of parent unitcomposition for the current unit.
        int binarySearchParentIndex = Collections.binarySearch(listSorted, unitComposition,
                new ComarableDnToLookupParentUnit());
        if (binarySearchParentIndex > -1) {
            parentUnitComposition = listSorted.get(binarySearchParentIndex);
        }
        return parentUnitComposition;
    }

    private boolean isUnitChanged(UnitComposition<Unit> newUnitComposition,
            UnitComposition<Unit> oldUnitComposition) {
        boolean isChanged = true;
        isChanged = !newUnitComposition.getUnit().getName().equals(oldUnitComposition.getUnit().getName());
        return isChanged;
    }

    private List<UnitComposition<Unit>> getSortedListByDn(List<UnitComposition<Unit>> unitCompositions) {
        // Only do this once
        if (listSortedByDn == null) {
            listSortedByDn = new ArrayList<UnitComposition<Unit>>(unitCompositions);
            ComarableDn comarableDn = new ComarableDn();
            Collections.sort(listSortedByDn, comarableDn);
        }
        return listSortedByDn;
    }

    // Used for sort unitComposition list in DN string order
    class ComarableDn implements Comparator<UnitComposition<Unit>> {

        @Override
        public int compare(UnitComposition<Unit> o1, UnitComposition<Unit> o2) {
            return o1.getDn().compareTo(o2.getDn());
        }

    }

    // Used for lookup parent Unitcomposition in list.
    class ComarableDnToLookupParentUnit implements Comparator<UnitComposition<Unit>> {

        @Override
        public int compare(UnitComposition<Unit> o1, UnitComposition<Unit> o2) {
            return o1.getDn().compareTo(o2.getParentDn());
        }
    }
}
