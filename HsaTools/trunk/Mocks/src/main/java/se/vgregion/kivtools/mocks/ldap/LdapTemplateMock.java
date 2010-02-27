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
 */
package se.vgregion.kivtools.mocks.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.AuthenticatedLdapEntryContextCallback;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.NameClassPairCallbackHandler;
import org.springframework.ldap.core.NameClassPairMapper;
import org.springframework.ldap.core.SearchExecutor;

import se.vgregion.kivtools.util.reflection.ReflectionUtil;

/**
 * Mock-class to use when unit testing Spring LDAP implementations.
 */
public class LdapTemplateMock extends LdapTemplate {
  private String searchFilter;
  private Map<Name, DirContextOperations> boundDNs = new HashMap<Name, DirContextOperations>();
  private List<DirContextOperations> dirContextOperationsForSearch = new ArrayList<DirContextOperations>();
  private NameNotFoundException exceptionToThrow;

  /**
   * Binds a DirContextOperations object to a specific distinguished name.
   * 
   * @param dn the distinguished name to bind the DirContextOperations to.
   * @param dirContextOperations the DirContextOperations to bind.
   */
  public void addBoundDN(Name dn, DirContextOperations dirContextOperations) {
    this.boundDNs.put(dn, dirContextOperations);
  }

  /**
   * Clears the currently registered DirContextOperations.
   */
  public void clearDirContexts() {
    this.dirContextOperationsForSearch.clear();
  }

  /**
   * Adds a DirContextOperations object for searching.
   * 
   * @param dirContextOperations the DirContextOperations to add.
   */
  public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
    this.dirContextOperationsForSearch.add(dirContextOperations);
  }

  public void setExceptionToThrow(NameNotFoundException exceptionToThrow) {
    this.exceptionToThrow = exceptionToThrow;
  }

  /**
   * Verifies that the last used search filter is the expected.
   * 
   * @param expectedFilter the expected search filter.
   */
  public void assertSearchFilter(String expectedFilter) {
    assertEquals(expectedFilter, this.searchFilter);
  }

  @Override
  public Object lookup(Name dn, ContextMapper mapper) {
    if (this.exceptionToThrow != null) {
      throw this.exceptionToThrow;
    }

    DirContextOperations dirContextOperations = this.boundDNs.get(dn);
    if (dirContextOperations == null) {
      throw new NameNotFoundException("Name not found");
    }

    Object result = null;
    if (dirContextOperations != null) {
      result = mapper.mapFromContext(dirContextOperations);
    }
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
    List result = search(base.toString(), filter, mapper);
    // Use ReflectionUtil since there is no set-method for cookie.
    ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, int searchScope, ContextMapper mapper) {
    return search(base.toString(), filter, mapper);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, ContextMapper mapper) {
    return search(base.toString(), filter, mapper);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, ContextMapper mapper) {
    this.searchFilter = filter;
    List result = new ArrayList();
    for (DirContextOperations dirContextOperations : this.dirContextOperationsForSearch) {
      result.add(mapper.mapFromContext(dirContextOperations));
    }
    return result;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public boolean authenticate(Name base, String filter, String password, AuthenticatedLdapEntryContextCallback callback) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public boolean authenticate(Name base, String filter, String password) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public boolean authenticate(String base, String filter, String password, AuthenticatedLdapEntryContextCallback callback) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public boolean authenticate(String base, String filter, String password) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void bind(DirContextOperations ctx) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void bind(Name dn, Object obj, Attributes attributes) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void bind(String dn, Object obj, Attributes attributes) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object executeReadOnly(ContextExecutor ce) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object executeReadWrite(ContextExecutor ce) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public ContextSource getContextSource() {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void list(Name base, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List list(Name base, NameClassPairMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List list(Name base) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void list(String base, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List list(String base, NameClassPairMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List list(String base) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(Name base, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void listBindings(Name base, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(Name base, NameClassPairMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(Name base) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(String base, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void listBindings(String base, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(String base, NameClassPairMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List listBindings(String base) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(Name dn, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(Name dn, String[] attributes, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(Name dn, String[] attributes, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(Name dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(String dn, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(String dn, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(String dn, String[] attributes, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(String dn, String[] attributes, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object lookup(String dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public DirContextOperations lookupContext(Name dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public DirContextOperations lookupContext(String dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void modifyAttributes(DirContextOperations ctx) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void modifyAttributes(Name dn, ModificationItem[] mods) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void modifyAttributes(String dn, ModificationItem[] mods) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void rebind(DirContextOperations ctx) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void rebind(Name dn, Object obj, Attributes attributes) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void rebind(String dn, Object obj, Attributes attributes) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void rename(Name oldDn, Name newDn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void rename(String oldDn, String newDn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, int searchScope, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(Name base, String filter, int searchScope, boolean returningObjFlag, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, int searchScope, String[] attrs, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, int searchScope, String[] attrs, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(Name base, String filter, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, SearchControls controls, AttributesMapper mapper, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, SearchControls controls, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, SearchControls controls, ContextMapper mapper, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(Name base, String filter, SearchControls controls, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(Name base, String filter, SearchControls controls, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(SearchExecutor se, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(SearchExecutor se, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, int searchScope, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(String base, String filter, int searchScope, boolean returningObjFlag, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, int searchScope, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, int searchScope, String[] attrs, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, int searchScope, String[] attrs, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(String base, String filter, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, SearchControls controls, AttributesMapper mapper, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, SearchControls controls, AttributesMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  @SuppressWarnings("unchecked")
  public List search(String base, String filter, SearchControls controls, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler, DirContextProcessor processor) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void search(String base, String filter, SearchControls controls, NameClassPairCallbackHandler handler) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object searchForObject(Name base, String filter, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public Object searchForObject(String base, String filter, ContextMapper mapper) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void setContextSource(ContextSource contextSource) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void setIgnoreNameNotFoundException(boolean ignore) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void setIgnorePartialResultException(boolean ignore) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void unbind(Name dn, boolean recursive) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void unbind(Name dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void unbind(String dn, boolean recursive) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }

  @Override
  public void unbind(String dn) {
    throw new UnsupportedOperationException("Method not implemented in mock");
  }
}
