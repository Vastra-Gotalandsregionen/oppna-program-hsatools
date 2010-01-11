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

    @Override
    public OrganizationChangeReport<Unit> createOrganizationChangeReport(
            List<UnitComposition<Unit>> oldFlatOrganization, List<UnitComposition<Unit>> newFlatOrganization) {

        List<UnitComposition<Unit>> removedUnits = new ArrayList<UnitComposition<Unit>>();
        Map<String,List<UnitComposition<Unit>>> movedUnits = new HashMap<String, List<UnitComposition<Unit>>>();
        List<UnitComposition<Unit>> changedUnits = new ArrayList<UnitComposition<Unit>>();
        List<UnitComposition<Unit>> addedUnits = new ArrayList<UnitComposition<Unit>>();

        // Ensure list sorted
        Collections.sort(oldFlatOrganization);

        // Ensure list sorted
        Collections.sort(newFlatOrganization);
        
        List<UnitComposition<Unit>> listSortedByDn = new ArrayList<UnitComposition<Unit>>(newFlatOrganization);
        ComarableDn comarableDn = new ComarableDn();
        Collections.sort(listSortedByDn, comarableDn);

        for (UnitComposition<Unit> oldUnitComposition : oldFlatOrganization) {
            // Search for unit in list
            int index = Collections.binarySearch(newFlatOrganization, oldUnitComposition);
            if (index < 0) {
                removedUnits.add(oldUnitComposition);
            } else {
                UnitComposition<Unit> newUnitComposition = newFlatOrganization.get(index);
                if (!newUnitComposition.getDn().equals(oldUnitComposition.getDn())) {
                    
                    // Lookup index of parent unitcomposition for the current unit.
                    int binarySearchParent = Collections.binarySearch(listSortedByDn, newUnitComposition, new ComarableDnToLookupParentUnit());
                    UnitComposition<Unit> parentUnitComposition = listSortedByDn.get(binarySearchParent);
                    
                    // Put the current moved unitcomposition in map with the parent unitcompositions's hsaIdentity as key.
                    List<UnitComposition<Unit>> list = movedUnits.get(parentUnitComposition.getUnit().getHsaIdentity());
                    if (list == null) {
                        list = new ArrayList<UnitComposition<Unit>>();
                        movedUnits.put(parentUnitComposition.getUnit().getHsaIdentity(), list);
                    }
                    list.add(newUnitComposition);
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
                addedUnits.add(newUnitComposition);
            }
        }
        OrganizationChangeReport<Unit> organizationChangeReport = new OrganizationChangeReport<Unit>(addedUnits,
                removedUnits, movedUnits, changedUnits);
        return organizationChangeReport;
    }

    private boolean isUnitChanged(UnitComposition<Unit> newUnitComposition,
            UnitComposition<Unit> oldUnitComposition) {
        boolean isChanged = true;
        isChanged = !newUnitComposition.getUnit().getName().equals(oldUnitComposition.getUnit().getName());
        return isChanged;
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
