package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.vgregion.kivtools.search.domain.Unit;

/**
 * 
 * @author David Bennehult & Anders Bergkvist
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
    public UnitComposition<Unit> generateOrganisation(List<UnitComposition<Unit>> unitCompositions) {
        Map<UnitComposition<Unit>, List<UnitComposition<Unit>>> topUnitChildren;
        topUnitChildren = new HashMap<UnitComposition<Unit>, List<UnitComposition<Unit>>>();
        
        Map<String, List<UnitComposition<Unit>>> subUnitChildren;
        subUnitChildren = new HashMap<String, List<UnitComposition<Unit>>>();
        
        VgrUnitComposition vgrUnitComposition = new VgrUnitComposition("vgr", null);

        for (UnitComposition<Unit> unitComposition : unitCompositions) {
            String unitParentDn = unitComposition.getParentDn();

            // Add top unit in separate list.
            if ("".equals(unitParentDn)) {
                List<UnitComposition<Unit>> topUnitList = topUnitChildren.get(unitComposition.getDn());
                if (topUnitList == null) {
                    topUnitList = new ArrayList<UnitComposition<Unit>>();
                    topUnitChildren.put(unitComposition, topUnitList);
                    //topUnits.put(unitComposition.getDn(), unitComposition);
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

        for (Entry<UnitComposition<Unit>, List<UnitComposition<Unit>>> topUnit : topUnitChildren.entrySet()) {
            UnitComposition<Unit> topUnitComposite = topUnit.getKey();
            UnitComposition<Unit> generateOrganization = generateOrganization(topUnit.getKey(),
                    subUnitChildren.get(topUnitComposite.getDn()), subUnitChildren);
            vgrUnitComposition.getChildUnits().add(generateOrganization);
        }
        return vgrUnitComposition;
    }

    private UnitComposition<Unit> generateOrganization(UnitComposition<Unit> rootUnit,
            List<UnitComposition<Unit>> units, Map<String, List<UnitComposition<Unit>>> subUnitChildren) {

        if (units != null) {
            rootUnit.getChildUnits().addAll(units);

            for (UnitComposition<Unit> unitComposition : units) {
                generateOrganization(unitComposition, subUnitChildren.get(unitComposition.getDn()),
                        subUnitChildren);
            }
        }

        return rootUnit;

    }
}
