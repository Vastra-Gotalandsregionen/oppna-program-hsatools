package se.vgregion.kivtools.search.svc.kiv.organizationtree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;

public class VgrOrganisationBuilderTest {

    private VgrOrganisationBuilder vgrOrganisationBuilder;
    private List<UnitComposition<Unit>> vgrUnitCompositions;

    @Before
    public void setUp() throws Exception {
        vgrOrganisationBuilder = new VgrOrganisationBuilder();

        Unit unit1 = new Unit();
        unit1.setHsaIdentity("unit1");
        VgrUnitComposition vgrUnitComposition1 = new VgrUnitComposition("ou=node1", unit1);

        Unit unit2 = new Unit();
        unit2.setHsaIdentity("unit2");
        VgrUnitComposition vgrUnitComposition2 = new VgrUnitComposition("ou=node2,ou=node1", unit2);

        Unit unit3 = new Unit();
        unit3.setHsaIdentity("unit3");
        VgrUnitComposition vgrUnitComposition3 = new VgrUnitComposition("ou=node3", unit3);

        Unit unit4 = new Unit();
        unit4.setHsaIdentity("unit4");
        VgrUnitComposition vgrUnitComposition4 = new VgrUnitComposition("ou=node4,ou=node3", unit4);

        Unit unit5 = new Unit();
        unit5.setHsaIdentity("unit5");
        VgrUnitComposition vgrUnitComposition5 = new VgrUnitComposition("ou=node5,ou=node2,ou=node1", unit5);

        vgrUnitCompositions = new ArrayList<UnitComposition<Unit>>();
        vgrUnitCompositions.add(vgrUnitComposition2);
        vgrUnitCompositions.add(vgrUnitComposition5);
        vgrUnitCompositions.add(vgrUnitComposition3);
        vgrUnitCompositions.add(vgrUnitComposition1);
        vgrUnitCompositions.add(vgrUnitComposition4);
    }

    @Test
    public void testGenerateOrganisation() {
        UnitComposition<Unit> generateOrganisation = vgrOrganisationBuilder
                .generateOrganisation(vgrUnitCompositions);
        assertNotNull(generateOrganisation);

        // Use for loop because generateOrganization use Maps that are not ordered.
        for (UnitComposition<Unit> topNodes : generateOrganisation.getChildUnits()) {
            if (topNodes.getUnit().getHsaIdentity().equals("unit1")) {
                assertEquals("unit2", topNodes.getChildUnits().get(0).getUnit().getHsaIdentity());
                assertEquals("unit5", topNodes.getChildUnits().get(0).getChildUnits().get(0).getUnit()
                        .getHsaIdentity());
            } else if (topNodes.getUnit().getHsaIdentity().equals("unit3")) {
                assertEquals("unit4", topNodes.getChildUnits().get(0).getUnit().getHsaIdentity());
            } else {
                fail("Should only contain two root nodes unit1 and unit3");
            }
        }
    }

}
