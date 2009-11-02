package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

/**
 * ContextMapper for retrieving the regionName-attribute for a person.
 * 
 * @author Joakim Olsson
 */
final class RegionNameMapper implements ContextMapper {

  /**
   * Retrieves the regionName attribute and returns it as a string.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return The persons regionName as a string.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String regionName = dirContext.getStringAttribute("regionName");
    return regionName;
  }
}
