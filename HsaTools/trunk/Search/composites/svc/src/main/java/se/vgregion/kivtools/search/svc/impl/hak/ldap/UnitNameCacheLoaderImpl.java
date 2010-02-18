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

import javax.naming.directory.SearchControls;

import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.UnitNameCache;

/**
 * Implementation of the UnitNameCacheLoader for LTH.
 * 
 * @author Joakim Olsson
 */
public class UnitNameCacheLoaderImpl implements CacheLoader<UnitNameCache> {
  private LdapTemplate ldapTemplate;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitNameCache loadCache() {
    UnitNameCache unitNameCache = new UnitNameCache();

    OrFilter filter = new OrFilter();
    filter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
    filter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_FUNCTION_SPECIFIC));

    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    do {
      this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, new UnitNameMapper(unitNameCache), control);
    } while (control.getCookie().getCookie() != null);

    return unitNameCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UnitNameCache createEmptyCache() {
    return new UnitNameCache();
  }
}
