package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

/**
 * Maps data from ldap to a PersonalRecord object.
 * 
 * @author David Bennehult
 * 
 */
public class PersonalRecordMapper implements ContextMapper {

  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContextOperations = (DirContextOperations) ctx;
    String firstName = dirContextOperations.getStringAttribute("givenName");
    String lastName = dirContextOperations.getStringAttribute("sn");
    String fullName = dirContextOperations.getStringAttribute("fullName");
    return new PersonalRecord(firstName, lastName, fullName);
  }

}
