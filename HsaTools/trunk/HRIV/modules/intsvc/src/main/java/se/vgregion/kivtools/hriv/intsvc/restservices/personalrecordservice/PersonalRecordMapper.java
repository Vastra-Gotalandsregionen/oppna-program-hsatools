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
