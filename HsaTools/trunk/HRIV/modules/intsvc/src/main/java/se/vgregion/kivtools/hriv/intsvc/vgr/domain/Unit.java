package se.vgregion.kivtools.hriv.intsvc.vgr.domain;

import java.util.ArrayList;
import java.util.List;

public class Unit {

    private String unitId;
    private List<Unit> childUnits = new ArrayList<Unit>();
    private String unitName;

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public List<Unit> getChildUnits() {
        return childUnits;
    }

    public void setChildUnits(List<Unit> childUnits) {
        this.childUnits = childUnits;
    }

     public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    

}
