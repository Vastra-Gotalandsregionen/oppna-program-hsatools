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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.List;

import javax.naming.Name;

import se.vgregion.kivtools.search.domain.Unit;

/**
 * Interface to use for search service implementations. SearchServices is of type LDAP.
 * 
 * @author davidbennehult
 * 
 */
public interface SearchService {

  /**
   * Search for unit(s).
   * 
   * @param base - base to start search from.
   * @param filter - Search filter to use.
   * @param searchScope - SearchScope to use. 0 object scope, 1 onelevel scope, subtree scope 2.
   * @param attrs - Search attributes.
   * @return {@link List} of {@link Unit}
   */
  List<Unit> searchUnits(Name base, String filter, int searchScope, List<String> attrs);

  /**
   * Search for {@link Unit}s of type function (cn)
   * 
   * @param base - base to start search from.
   * @param filter - Search filter to use.
   * @param searchScope - SearchScope to use. 0 object scope, 1 onelevel scope, subtree scope 2.
   * @param attrs - Search attributes.
   * @return {@link List} of {@link Unit}
   * @return
   */
  List<Unit> searchFunctionUnits(Name base, String filter, int searchScope, List<String> attrs);

  /**
   * Search for single attribute.
   * 
   * @param base - base to start search from.
   * @param filter - Search filter to use.
   * @param searchScope - SearchScope to use. 0 object scope, 1 onelevel scope, subtree scope 2.
   * @param attrs - Search attributes.
   * @param mappingAttribute - Attribute to map to search result.
   * @return {@link List} of {@link String}
   */
  List<String> searchSingleAttribute(Name base, String filter, int searchScope, List<String> attrs, String mappingAttribute);

  /**
   * Lookup a single {@link Unit}
   * 
   * @param name the {@link Name} of the unit to lookup.
   * @param attrs array of attributes.
   * @return {@link Unit}
   */
  Unit lookupUnit(Name name, List<String> attrs);
}
