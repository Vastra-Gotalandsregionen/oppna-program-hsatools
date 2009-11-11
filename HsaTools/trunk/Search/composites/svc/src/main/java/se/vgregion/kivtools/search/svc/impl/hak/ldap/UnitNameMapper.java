package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.svc.UnitNameCache;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;

/**
 * ContextMapper for retrieving the givenName- and sn-attributes for a person and populating a PersonNameCache with the retrieved information.
 * 
 * @author Joakim Olsson
 */
final class UnitNameMapper implements ContextMapper {
  private final UnitNameCache unitNameCache;

  /**
   * Constructs a new PersonNameMapper using the provided PersonNameCache.
   * 
   * @param unitNameCache The PersonNameCache the PersonNameMapper should add all person names to.
   */
  public UnitNameMapper(UnitNameCache unitNameCache) {
    this.unitNameCache = unitNameCache;
  }

  /**
   * Retrieves the givenName- and sn-attributes and adds them to the PersonNameCache.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return Always returns null since the PersonNameCache is provided in the constructor.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String unitName = dirContext.getStringAttribute("ou");
    if (StringUtil.isEmpty(unitName)) {
      unitName = dirContext.getStringAttribute("cn");
    }
    unitName = Formatter.replaceStringInString(unitName, "\\,", ",");
    this.unitNameCache.add(unitName);
    return null;
  }
}
