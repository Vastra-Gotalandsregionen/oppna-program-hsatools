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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.svc.TitleCache;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

public class TitleCacheLoaderImplTest {
  private TitleCacheLoaderImpl titleCacheLoaderImpl;
  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    ldapTemplate = new LdapTemplateMock();
    titleCacheLoaderImpl = new TitleCacheLoaderImpl();
    titleCacheLoaderImpl.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testInstantiation() {
    TitleCacheLoaderImpl unitNameCacheLoaderImpl = new TitleCacheLoaderImpl();
    assertNotNull(unitNameCacheLoaderImpl);
  }

  @Test
  public void testLoadCache() {
    DirContextOperationsMock person1 = new DirContextOperationsMock();
    person1.addAttributeValue("title", "Assistent");
    DirContextOperationsMock person2 = new DirContextOperationsMock();
    person2.addAttributeValue("   ", "Assistent");
    DirContextOperationsMock person3 = new DirContextOperationsMock();
    this.ldapTemplate.addDirContextOperationForSearch(person1);
    this.ldapTemplate.addDirContextOperationForSearch(person2);
    this.ldapTemplate.addDirContextOperationForSearch(person3);

    TitleCache titleCache = titleCacheLoaderImpl.loadCache();

    this.ldapTemplate.assertSearchFilter("(objectClass=hkatPerson)");

    List<String> matchingTitles = titleCache.getMatchingTitles("sis");
    assertEquals(1, matchingTitles.size());
    assertTrue(matchingTitles.contains("Assistent"));
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      // Use ReflectionUtil since there is no set-method for cookie.
      ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
      return result;
    }
  }
}
