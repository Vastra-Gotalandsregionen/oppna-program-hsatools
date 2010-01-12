package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

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
        List<UnitComposition<Unit>> topNodes = new ArrayList<UnitComposition<Unit>>();

        Map<String, List<UnitComposition<Unit>>> subNodes;
        subNodes = new HashMap<String, List<UnitComposition<Unit>>>();

        VgrUnitComposition vgrUnitComposition = new VgrUnitComposition("vgr", null);

        for (UnitComposition<Unit> unitComposition : unitCompositions) {
            // only add unitCompositions that have DN
            if (!"".equals(unitComposition.getDn())) {
                // Add top node units in separate list.
                if ("".equals(unitComposition.getParentDn())) {
                    topNodes.add(unitComposition);
                } else {
                    // Is a sub node units or leaf
                    addToNodeChildrenList(unitComposition, subNodes);
                }
            }
        }

        // Generate tree structure for each top node.
        for (UnitComposition<Unit> topNode : topNodes) {
            UnitComposition<Unit> generateOrganization = generateOrganization(topNode, subNodes.get(topNode
                    .getDn()), subNodes);
            vgrUnitComposition.getChildUnits().add(generateOrganization);
        }
        return vgrUnitComposition;
    }

    /**
     * 
     * @param unitComposite
     *            To add in List in map.
     * @param mapList
     *            Map<String, List<UnitComposition<Unit>>> to use for adding unitComposite
     */
    private void addToNodeChildrenList(UnitComposition<Unit> unitComposite,
            Map<String, List<UnitComposition<Unit>>> mapList) {
        List<UnitComposition<Unit>> nodeChildren = mapList.get(unitComposite.getParentDn());
        if (nodeChildren == null) {
            nodeChildren = new ArrayList<UnitComposition<Unit>>();
            mapList.put(unitComposite.getParentDn(), nodeChildren);
        }
        nodeChildren.add(unitComposite);
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
