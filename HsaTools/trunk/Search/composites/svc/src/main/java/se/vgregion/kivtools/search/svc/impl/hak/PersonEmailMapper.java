package se.vgregion.kivtools.search.svc.impl.hak;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

/**
 * ContextMapper for retrieving the mail-attribute for a person.
 * 
 * @author Joakim Olsson
 */
final class PersonEmailMapper implements ContextMapper {

  /**
   * Retrieves the mail attribute and returns it as a string.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return The persons email address as a string.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String email = dirContext.getStringAttribute("mail");
    return email;
  }
}
