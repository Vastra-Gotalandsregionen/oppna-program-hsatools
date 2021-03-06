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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.svc.TitleCache;

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
  public void createEmptyCacheReturnNewEmptyCacheEachTime() {
    TitleCache emptyCache1 = titleCacheLoaderImpl.createEmptyCache();
    TitleCache emptyCache2 = titleCacheLoaderImpl.createEmptyCache();
    assertEquals(0, emptyCache1.getMatchingTitles("").size());
    assertEquals(0, emptyCache2.getMatchingTitles("").size());
    assertNotSame(emptyCache1, emptyCache2);
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
}
