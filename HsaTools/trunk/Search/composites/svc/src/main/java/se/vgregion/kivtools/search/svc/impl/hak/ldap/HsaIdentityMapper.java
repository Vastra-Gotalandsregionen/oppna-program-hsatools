package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

/**
 * ContextMapper for retrieving the hsaIdentity-attribute for a unit.
 * 
 * @author Joakim Olsson
 */
final class HsaIdentityMapper implements ContextMapper {

  /**
   * Retrieves the hsaIdentity attribute and returns it as a string.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return The units hsaIdentity as a string.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String hsaIdentity = dirContext.getStringAttribute("hsaIdentity");
    return hsaIdentity;
  }
}
