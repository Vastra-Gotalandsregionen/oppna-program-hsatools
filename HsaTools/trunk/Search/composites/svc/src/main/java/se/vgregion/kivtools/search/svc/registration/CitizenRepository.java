/**
 * Copyright 2009 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
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
