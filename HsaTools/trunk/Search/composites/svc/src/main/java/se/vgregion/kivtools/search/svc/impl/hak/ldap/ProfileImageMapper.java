package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

/**
 * ContextMapper for retrieving the profile image (jpegPhoto-attribute) for a person.
 * 
 * @author Joakim Olsson
 */
final class ProfileImageMapper implements ContextMapper {
  /**
   * Retrieves the jpegPhoto attribute and returns it as a byte-array.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return The persons profile image as a byte-array.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    byte[] profileImage = (byte[]) dirContext.getObjectAttribute("jpegPhoto");
    return profileImage;
  }
}
