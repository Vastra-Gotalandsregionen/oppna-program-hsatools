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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.util.OrganizationChangeReport;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

public class VgrOrganizationChangeReporterTest {

    private VgrOrganizationChangeReporter vgrOrganizationChangeReporter;
    private VgrUnitComposition vgrUnitCompositionRoot;
    private VgrUnitComposition childMovedRoot;
    private ArrayList<UnitComposition<Unit>> newFlatOrganizationList;
    private ArrayList<UnitComposition<Unit>> oldFlatOrganizationList;

    @Before
    public void setUp() throws Exception {
        vgrOrganizationChangeReporter = new VgrOrganizationChangeReporter();
        
        vgrUnitCompositionRoot = new VgrUnitComposition("ou=rootUnit1", createUnit("ou=rootUnit1", "rootUnit"));
        VgrUnitComposition vgrUnitCompositionChild1 = new VgrUnitComposition("ou=child1,ou=rootUnit1",createUnit("childUnit1", "childUnit1"));
        VgrUnitComposition vgrUnitCompositionChild2 = new VgrUnitComposition("ou=child2,ou=rootUnit1",createUnit("childUnit2", "childUnit2"));
        VgrUnitComposition vgrUnitCompositionLeaf1 = new VgrUnitComposition("ou=leaf,ou=child1,ou=rootUnit1",createUnit("leafUnit1", "leafUnit1"));
        VgrUnitComposition vgrUnitCompositionLeaf2 = new VgrUnitComposition("ou=leaf,ou=child2,ou=rootUnit1",createUnit("leafUnit2", "leafUnit2"));
        oldFlatOrganizationList = new ArrayList<UnitComposition<Unit>>();
        oldFlatOrganizationList.add(vgrUnitCompositionRoot);
        oldFlatOrganizationList.add(vgrUnitCompositionChild1);
        oldFlatOrganizationList.add(vgrUnitCompositionChild2);
        oldFlatOrganizationList.add(vgrUnitCompositionLeaf1);
        oldFlatOrganizationList.add(vgrUnitCompositionLeaf2);
                
        childMovedRoot = new VgrUnitComposition("ou=rootUnit1", createUnit("ou=rootUnit1", "rootUnit"));
        VgrUnitComposition childMovedChild1 = new VgrUnitComposition("ou=child1,ou=rootUnit1",createUnit("childUnit1", "childUnit1"));
        VgrUnitComposition childMovedChild2 = new VgrUnitComposition("ou=child2,ou=rootUnit1",createUnit("childUnit2", "childUnit2"));
        VgrUnitComposition childMovedLeaf1 = new VgrUnitComposition("ou=leaf,ou=child2,ou=rootUnit1",createUnit("leafUnit1", "leafUnit1"));
        VgrUnitComposition childMovedLeaf2 = new VgrUnitComposition("ou=leaf,ou=child1,ou=rootUnit1",createUnit("leafUnit2", "leafUnit2"));
        newFlatOrganizationList = new ArrayList<UnitComposition<Unit>>();
        newFlatOrganizationList.add(childMovedRoot);
        newFlatOrganizationList.add(childMovedChild1);
        newFlatOrganizationList.add(childMovedChild2);
        newFlatOrganizationList.add(childMovedLeaf1);
        newFlatOrganizationList.add(childMovedLeaf2);
   
    }

    @Test
    public void testOrganizationChangeReportWithMovedUnit() {
        OrganizationChangeReport<Unit> organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, newFlatOrganizationList);
        assertEquals(2, organizationChangeReport.getMovedOrganizationUnits().size());
        
        // Check that the two leaf units has switch place in tree
        for (Entry<String, List<UnitComposition<Unit>>> mapEntry : organizationChangeReport.getMovedOrganizationUnits().entrySet()) {
            for (UnitComposition<Unit> unitComposition : mapEntry.getValue()) {
                if (unitComposition.getUnit().getHsaIdentity().equals("leafUnit1")) {
                    assertEquals(mapEntry.getKey(),"childUnit2");
                }else {
                    assertEquals(mapEntry.getKey(),"childUnit1");
                }
            }
        }
    }
    
    @Test
    public void testOrganizationChangeReportWithRemovedUnit() {
        newFlatOrganizationList.remove(3);
        OrganizationChangeReport<Unit> organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, newFlatOrganizationList);
        assertEquals(1, organizationChangeReport.getRemovedOrganizationUnits().size());
        // remove all
        organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, new ArrayList<UnitComposition<Unit>>());
        assertEquals(5, organizationChangeReport.getRemovedOrganizationUnits().size());
    }
    
    @Test
    public void testOrganizationChangeReportWithChangedUnit() {
        UnitComposition<Unit> unitComposition = newFlatOrganizationList.get(0);
        unitComposition.getUnit().setName("RootChanged");
        OrganizationChangeReport<Unit> organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, newFlatOrganizationList);
        assertEquals(1, organizationChangeReport.getChangedOrganizationUnits().size());
        assertTrue(unitComposition.compareTo(organizationChangeReport.getChangedOrganizationUnits().get(0)) == 0);
    }
    
    @Test
    public void testOrganizationChangeReportWithaddedUnit() {
        UnitComposition<Unit> unitComposition = new VgrUnitComposition("ou=child3,ou=rootUnit1",createUnit("childUnit3", "childUnit3"));
        newFlatOrganizationList.add(unitComposition);
        OrganizationChangeReport<Unit> organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, newFlatOrganizationList);
        assertEquals(1, organizationChangeReport.getAddedOrganizationUnits().size());
        assertEquals(unitComposition, organizationChangeReport.getAddedOrganizationUnits().get(childMovedRoot.getUnit().getHsaIdentity()).get(0));
		
		
    }
	
	@Test
    public void testOrganizationChangeReportWithaddedUnit2() {
		Unit unit = new Unit();

		unit.setName("Anders testar");

		unit.setHsaIdentity("MITT-ALLDELES-EGNA-ID");

		 UnitComposition<Unit> tmp = new VgrUnitComposition("ou=Anders testar, ou=apa", unit);

		 newFlatOrganizationList.add(tmp);

        //UnitComposition<Unit> unitComposition = new VgrUnitComposition("ou=child3,ou=rootUnit1",createUnit("childUnit3", "childUnit3"));
        //newFlatOrganizationList.add(unitComposition);
        OrganizationChangeReport<Unit> organizationChangeReport = vgrOrganizationChangeReporter.createOrganizationChangeReport(oldFlatOrganizationList, newFlatOrganizationList);
        assertEquals(1, organizationChangeReport.getAddedOrganizationUnits().size());
        assertEquals(tmp, organizationChangeReport.getAddedOrganizationUnits().get("").get(0));
		
		
    }
	
    
    
    
    private Unit createUnit(String id, String name) {
        Unit unit = new Unit();
        unit.setHsaIdentity(id);
        unit.setName(name);
        return unit;
    }
    
    

}
