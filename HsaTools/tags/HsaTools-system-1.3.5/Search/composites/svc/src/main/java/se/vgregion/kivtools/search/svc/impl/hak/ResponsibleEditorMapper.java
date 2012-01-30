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

package se.vgregion.kivtools.search.svc.impl.hak;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;

/**
 * ContextMapper for fetching all responsible editors from a responsible editor node.
 * 
 * @author Joakim Olsson
 */
final class ResponsibleEditorMapper implements ContextMapper {

  /**
   * Maps the member-attribute to a list of strings containing the common names (user id's) of the responsible editors.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return A list of common names of the responsible editors or an empty list if no editors are found.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    List<String> result = new ArrayList<String>();
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);
    List<String> members = context.getStrings("member");
    for (String member : members) {
      result.add(new DistinguishedName(member).getValue("cn"));
    }
    return result;
  }
}
