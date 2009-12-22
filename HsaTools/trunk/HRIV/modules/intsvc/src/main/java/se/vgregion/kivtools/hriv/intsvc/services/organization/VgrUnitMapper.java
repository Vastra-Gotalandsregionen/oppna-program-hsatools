package se.vgregion.kivtools.hriv.intsvc.services.organization;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.hriv.intsvc.vgr.domain.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author David Bennehult & Joakim Olsson
 * 
 */
public class VgrUnitMapper implements ContextMapper {

    @Override
    public Object mapFromContext(Object ctx) {
        DirContextOperations dirContextOperations = (DirContextOperations) ctx;
        
        // Fill unit with data.
        Unit unit = new Unit();
        unit.setUnitId(dirContextOperations.getStringAttribute("hsaIdentity"));
        unit.setUnitName(getUnitName(dirContextOperations));

        UnitComposition<Unit> unitComposition = new VgrUnitComposition(dirContextOperations.getDn().toString(),
                unit);

        return unitComposition;
    }

    private String getUnitName(DirContextOperations dirContextOperations) {
        String name = dirContextOperations.getStringAttribute("ou");
        // Is a function, name is the cn attribute instead.
        if (StringUtil.isEmpty(name)) {
            name = dirContextOperations.getStringAttribute("cn");
        }
        return name;
    }

}
