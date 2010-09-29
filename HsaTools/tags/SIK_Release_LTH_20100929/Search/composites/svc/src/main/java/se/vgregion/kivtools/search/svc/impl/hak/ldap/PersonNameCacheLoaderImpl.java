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
import org.springframework.ldap.filter.Filter;

import se.vgregion.kivtools.search.svc.CacheLoader;
import se.vgregion.kivtools.search.svc.PersonNameCache;

/**
 * Implementation of the PersonNameCacheLoader for LTH.
 * 
 * @author Joakim Olsson
 */
public class PersonNameCacheLoaderImpl implements CacheLoader<PersonNameCache> {
  private LdapTemplate ldapTemplate;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonNameCache loadCache() {
    PersonNameCache personNameCache = new PersonNameCache();

    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    Filter filter = new EqualsFilter("objectClass", "hkatPerson");
    do {
      this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, new PersonNameMapper(personNameCache), control);
    } while (control.getCookie().getCookie() != null);

    return personNameCache;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonNameCache createEmptyCache() {
    return new PersonNameCache();
  }
}
