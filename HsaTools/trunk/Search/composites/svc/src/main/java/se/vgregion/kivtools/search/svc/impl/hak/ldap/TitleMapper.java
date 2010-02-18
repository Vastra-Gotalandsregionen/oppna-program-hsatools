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

import se.vgregion.kivtools.search.svc.TitleCache;
import se.vgregion.kivtools.util.StringUtil;

/**
 * ContextMapper for retrieving the title-attribute for a person and populating a TitleCache with the retrieved information.
 * 
 * @author Joakim Olsson
 */
final class TitleMapper implements ContextMapper {
  private final TitleCache titleCache;

  /**
   * Constructs a new TitleMapper using the provided TitleCache.
   * 
   * @param titleCache The TitleCache the TitleMapper should add all titles to.
   */
  public TitleMapper(TitleCache titleCache) {
    this.titleCache = titleCache;
  }

  /**
   * Retrieves the title-attribute and adds it to the TitleCache.
   * 
   * @param ctx The DirContextOperations object from Spring LDAP.
   * @return Always returns null since the TitleCache is provided in the constructor.
   */
  @Override
  public Object mapFromContext(Object ctx) {
    DirContextOperations dirContext = (DirContextOperations) ctx;
    String title = dirContext.getStringAttribute("title");
    if (!StringUtil.isEmpty(title)) {
      this.titleCache.add(title);
    }
    return null;
  }
}
