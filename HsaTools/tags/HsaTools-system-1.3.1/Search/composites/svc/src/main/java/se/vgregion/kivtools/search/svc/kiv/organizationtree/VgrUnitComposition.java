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
import java.util.List;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.interfaces.UnitComposition;
import se.vgregion.kivtools.util.StringUtil;

/**
 * 
 * @author David Bennehult
 * @author Anders Bergkvist
 */
public class VgrUnitComposition implements UnitComposition<Unit> {

    private static final long serialVersionUID = 1452523370194015103L;
    private String unitDn;
    private Unit unit;
    private List<UnitComposition<Unit>> childUnits = new ArrayList<UnitComposition<Unit>>();

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

    @Override
    public List<UnitComposition<Unit>> getChildUnits() {
        return childUnits;
    }

    @Override
    public int compareTo(UnitComposition<Unit> o) {
        return unit.compareTo(o.getUnit());
    }
    
    

}
