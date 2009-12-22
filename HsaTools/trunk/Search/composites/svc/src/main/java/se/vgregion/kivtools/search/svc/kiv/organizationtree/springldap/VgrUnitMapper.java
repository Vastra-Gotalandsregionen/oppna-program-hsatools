package se.vgregion.kivtools.search.svc.kiv.organizationtree.springldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.kiv.organizationtree.UnitComposition;
import se.vgregion.kivtools.search.svc.kiv.organizationtree.VgrUnitComposition;
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
        unit.setHsaIdentity(dirContextOperations.getStringAttribute("hsaIdentity"));
        unit.setName(getUnitName(dirContextOperations));

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
