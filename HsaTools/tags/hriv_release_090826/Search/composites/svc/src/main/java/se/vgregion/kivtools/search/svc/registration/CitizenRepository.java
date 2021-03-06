package se.vgregion.kivtools.search.svc.registration;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Repository for citizens.
 * 
 * @author Jonas Liljenfeldt, Know IT & Joakim Olsson, Unbound
 * 
 */
public class CitizenRepository {

  private LdapTemplate ldapTemplate;

  /**
   * Setter for the Spring LdapTemplate to use.
   * 
   * @param ldapTemplate The Spring LdapTemplate to use.
   */
  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Gets a citizens registered name in the LDAP directory by the ssn.
   * 
   * @param ssn The citizens ssn.
   * @return The citizens registered name in the LDAP directory.
   */
  public String getCitizenNameFromSsn(String ssn) {
    String name = (String) ldapTemplate.lookup("uid=" + ssn, new CitizenMapper());
    return name;
  }

  /**
   * Maps LDAP attributes to a simple string.
   */
  class CitizenMapper implements AttributesMapper {
    @Override
    public Object mapFromAttributes(Attributes attrs) throws NamingException {
      String givenName = (String) attrs.get("cn").get();
      String surName = (String) attrs.get("sn").get();
      String name = "";
      if (givenName.contains(surName)) {
        name = givenName;
      } else {
        name = givenName + " " + surName;
      }
      return name;
    }
  }
}
