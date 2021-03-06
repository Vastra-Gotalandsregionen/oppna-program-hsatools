/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

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
