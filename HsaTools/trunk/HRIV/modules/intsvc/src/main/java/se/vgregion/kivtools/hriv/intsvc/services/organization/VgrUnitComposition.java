package se.vgregion.kivtools.hriv.intsvc.services.organization;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author David Bennehult
 * @author Anders Bergkvist
 */
public class VgrUnitComposition implements UnitComposition<Unit> {

    private String unitDn;
    private Unit unit;

    /**
     * 
     * @param unitDn
     *            Dn name of the unit to use with the composition.
     * @param unit
     *            Object of a unit with the given unitDn.
     */
    public VgrUnitComposition(String unitDn, Unit unit) {
        this.unitDn = unitDn;
        this.unit = unit;
    }

    @Override
    public String getDn() {
        return unitDn;
    }

    @Override
    public String getParentDn() {
        String value = "";
        if (!StringUtil.isEmpty(unitDn)) {
            DistinguishedName distinguishedName = new DistinguishedName(unitDn);
            distinguishedName.removeLast();
            value = distinguishedName.toString();
        }
        return value;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }

}
