package se.vgregion.kivtools.hriv.intsvc.services;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.services.organization.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.services.organization.VgrUnitMapper;
import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Unit;
import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class VgrUnitMapperTest {

    private static final String HSA_ID = "hsaId";
    private static final String UNIT_NAME = "unitName";
    private VgrUnitMapper vgrUnitMapper;
    private DirContextOperationsMock dirContextOperationsMock;

    @Before
    public void setUp() throws Exception {
        vgrUnitMapper = new VgrUnitMapper();
        dirContextOperationsMock = new DirContextOperationsMock();
        dirContextOperationsMock.addAttributeValue("ou", UNIT_NAME);
        dirContextOperationsMock.addAttributeValue("hsaIdentity", HSA_ID);
        dirContextOperationsMock.setDn(new NameMock("dn"));

    }

    @Test
    public void testMapFromContextWithOu() {
        UnitComposition<Unit> mapFromContext = (UnitComposition<Unit>) vgrUnitMapper
                .mapFromContext(dirContextOperationsMock);
        assertUnit(mapFromContext.getUnit());
    }

    @Test
    public void testMapFromContextWithCn() {
        dirContextOperationsMock.setAttributeValue("ou", null);
        dirContextOperationsMock.addAttributeValue("cn", UNIT_NAME);
        UnitComposition<Unit> mapFromContext = (UnitComposition<Unit>) vgrUnitMapper
                .mapFromContext(dirContextOperationsMock);
        assertUnit(mapFromContext.getUnit());
    }

    private void assertUnit(Unit unit) {
        assertEquals(HSA_ID, unit.getUnitId());
        assertEquals(UNIT_NAME, unit.getUnitName());

    }

}
