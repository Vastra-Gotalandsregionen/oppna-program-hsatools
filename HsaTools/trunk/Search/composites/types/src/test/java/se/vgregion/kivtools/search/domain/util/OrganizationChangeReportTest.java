package se.vgregion.kivtools.search.domain.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.interfaces.UnitComposition;

public class OrganizationChangeReportTest {

   

    private OrganizationChangeReport<Unit> organizationChangeReport;
    private ArrayList<UnitComposition<Unit>> unitCompositionList;
    private HashMap<String, List<UnitComposition<Unit>>> hashMap;

    @Before
    public void setUp() throws Exception {
       
        unitCompositionList = new ArrayList<UnitComposition<Unit>>();
        unitCompositionList.add(new UnitCompositionImplementation());
        
        hashMap = new HashMap<String, List<UnitComposition<Unit>>>();
        hashMap.put("", unitCompositionList);
    }

    @Test
    public void testIsOrganizationChangedWithNoChange() {
        organizationChangeReport = new OrganizationChangeReport<Unit>(new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>(), new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>());
        assertFalse(organizationChangeReport.isOrganizationChanged());

    }

    @Test
    public void testIsOrganizationChangedWithAddChange() {
        organizationChangeReport = new OrganizationChangeReport<Unit>(hashMap,
                new ArrayList<UnitComposition<Unit>>(), new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>());
        assertTrue(organizationChangeReport.isOrganizationChanged());

    }

    @Test
    public void testIsOrganizationChangedWithMoveChange() {
        organizationChangeReport = new OrganizationChangeReport<Unit>(new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>(), hashMap,
                new ArrayList<UnitComposition<Unit>>());
        assertTrue(organizationChangeReport.isOrganizationChanged());

    }

    @Test
    public void testIsOrganizationChangedWithRemovedChange() {
        organizationChangeReport = new OrganizationChangeReport<Unit>(new HashMap<String, List<UnitComposition<Unit>>>(),
                unitCompositionList, new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>());
        assertTrue(organizationChangeReport.isOrganizationChanged());

    }

    @Test
    public void testIsOrganizationChangedWithContentChange() {
        organizationChangeReport = new OrganizationChangeReport<Unit>(new HashMap<String, List<UnitComposition<Unit>>>(),
                new ArrayList<UnitComposition<Unit>>(), new HashMap<String, List<UnitComposition<Unit>>>(),
                unitCompositionList);
        assertTrue(organizationChangeReport.isOrganizationChanged());

    }

    
    private final class UnitCompositionImplementation implements UnitComposition<Unit> {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public int compareTo(UnitComposition<Unit> o) {
            return 0;
        }

        @Override
        public Unit getUnit() {
            return null;
        }

        @Override
        public String getParentDn() {
            return null;
        }

        @Override
        public String getDn() {
            return null;
        }

        @Override
        public List<UnitComposition<Unit>> getChildUnits() {
            return null;
        }
    }
}
