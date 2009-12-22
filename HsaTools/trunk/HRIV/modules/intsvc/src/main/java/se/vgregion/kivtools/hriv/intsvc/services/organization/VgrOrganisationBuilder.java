package se.vgregion.kivtools.hriv.intsvc.services.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Organization;
import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Unit;

/**
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class VgrOrganisationBuilder {

    /**
     * Creates an Organization-tree based on the provided list of UnitCompositions.
     * 
     * @param unitCompositions
     *            The list of UnitCompositions to base the organization tree on.
     * @return A populated Organization-instance.
     */
    public Organization generateOrganisation(List<UnitComposition<Unit>> unitCompositions) {
        Map<String, List<UnitComposition<Unit>>> topUnitChildren;
        topUnitChildren = new HashMap<String, List<UnitComposition<Unit>>>();
        Map<String, List<UnitComposition<Unit>>> subUnitChildren;
        subUnitChildren = new HashMap<String, List<UnitComposition<Unit>>>();
        HashMap<String, UnitComposition<Unit>> topUnits = new HashMap<String, UnitComposition<Unit>>();
        Organization organization = new Organization();

        for (UnitComposition<Unit> unitComposition : unitCompositions) {
            String unitParentDn = unitComposition.getParentDn();

            // Add top unit in separate list.
            if ("".equals(unitParentDn)) {
                List<UnitComposition<Unit>> topUnitList = topUnitChildren.get(unitComposition.getDn());
                if (topUnitList == null) {
                    topUnitList = new ArrayList<UnitComposition<Unit>>();
                    topUnitChildren.put(unitComposition.getDn(), topUnitList);
                    topUnits.put(unitComposition.getDn(), unitComposition);
                }
                topUnitList.add(unitComposition);
            } else {
                // Is a subunit or leaf
                List<UnitComposition<Unit>> subUnitList = subUnitChildren.get(unitParentDn);
                if (subUnitList == null) {
                    subUnitList = new ArrayList<UnitComposition<Unit>>();
                    subUnitChildren.put(unitParentDn, subUnitList);
                }
                subUnitList.add(unitComposition);
            }
        }

        for (Entry<String, List<UnitComposition<Unit>>> topUnit : topUnitChildren.entrySet()) {

            Unit generateOrganization = generateOrganization(topUnits.get(topUnit.getKey()), subUnitChildren
                    .get(topUnit.getKey()), subUnitChildren);
            organization.getUnits().add(generateOrganization);
        }
        return organization;
    }

    private Unit generateOrganization(UnitComposition<Unit> rootUnit, List<UnitComposition<Unit>> units,
            Map<String, List<UnitComposition<Unit>>> subUnitChildren) {

        if (units != null) {
            for (UnitComposition<Unit> unitComposition : units) {
                rootUnit.getUnit().getChildUnits().add(unitComposition.getUnit());
            }

            for (UnitComposition<Unit> unitComposition : units) {
                generateOrganization(unitComposition, subUnitChildren.get(unitComposition.getDn()),
                        subUnitChildren);
            }
        }

        return rootUnit.getUnit();

    }
}
