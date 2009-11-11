package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.svc.PersonNameCache;

/**
 * ContextMapper for retrieving the givenName- and sn-attributes for a person and populating a PersonNameCache with the retrieved information.
 * 
 * @author Joakim Olsson
 */
final class PersonNameMapper implements ContextMapper {
  private final PersonNameCache personNameCache;

  /**
   * Constructs a new PersonNameMapper using the provided PersonNameCache.
   * 
   * @param personNameCache The PersonNameCache the PersonNameMapper should add all person names to.
   */
  public PersonNameMapper(PersonNameCache personNameCache) {
    this.personNameCache = personNameCache;
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
    String givenName = dirContext.getStringAttribute("givenName");
    String surname = dirContext.getStringAttribute("sn");
    this.personNameCache.add(givenName, surname);
    return null;
  }
}
