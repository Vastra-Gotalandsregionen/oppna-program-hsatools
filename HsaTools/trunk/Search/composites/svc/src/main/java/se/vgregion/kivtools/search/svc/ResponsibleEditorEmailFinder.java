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
package se.vgregion.kivtools.search.svc;

import java.util.List;

/**
 * A service used to find the email addresses of the responsible editors of a given node in the LDAP-tree.
 * 
 * @author Joakim Olsson
 */
public interface ResponsibleEditorEmailFinder {
  /**
   * Finds the email addresses of the responsible editors of the node in the LDAP-tree identified by the given DN.
   * 
   * @param dn The DN of the node in the LDAP-tree to find responsible editors for.
   * @return A list of email addresses of the responsible editors found or an empty list if no editors where found.
   */
  public List<String> findResponsibleEditors(String dn);
}
