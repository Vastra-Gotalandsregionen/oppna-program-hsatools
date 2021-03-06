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
import se.vgregion.kivtools.search.svc.PersonNameCache;

public class PersonNameCacheLoaderImplTest {
  private PersonNameCacheLoaderImpl personNameCacheLoaderImpl;
  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    ldapTemplate = new LdapTemplateMock();
    personNameCacheLoaderImpl = new PersonNameCacheLoaderImpl();
    personNameCacheLoaderImpl.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testInstantiation() {
    PersonNameCacheLoaderImpl personNameCacheLoaderImpl = new PersonNameCacheLoaderImpl();
    assertNotNull(personNameCacheLoaderImpl);
  }

  @Test
  public void createEmptyCacheReturnNewEmptyCacheEachTime() {
    PersonNameCache emptyCache1 = personNameCacheLoaderImpl.createEmptyCache();
    PersonNameCache emptyCache2 = personNameCacheLoaderImpl.createEmptyCache();
    assertEquals(0, emptyCache1.getMatchingGivenNames("", "").size());
    assertEquals(0, emptyCache2.getMatchingGivenNames("", "").size());
    assertNotSame(emptyCache1, emptyCache2);
  }

  @Test
  public void testLoadCache() {
    DirContextOperationsMock person1 = new DirContextOperationsMock();
    person1.addAttributeValue("givenName", "Kalle");
    person1.addAttributeValue("sn", "Anka");
    DirContextOperationsMock person2 = new DirContextOperationsMock();
    person2.addAttributeValue("givenName", "Nina");
    person2.addAttributeValue("sn", "Kanin");
    this.ldapTemplate.addDirContextOperationForSearch(person1);
    this.ldapTemplate.addDirContextOperationForSearch(person2);

    PersonNameCache personNameCache = personNameCacheLoaderImpl.loadCache();

    this.ldapTemplate.assertSearchFilter("(objectClass=hkatPerson)");

    List<String> matchingGivenNames = personNameCache.getMatchingGivenNames("a", "");
    assertEquals(2, matchingGivenNames.size());
    assertTrue(matchingGivenNames.contains("Kalle"));
    assertTrue(matchingGivenNames.contains("Nina"));
    List<String> matchingSurnames = personNameCache.getMatchingSurnames("", "an");
    assertEquals(2, matchingSurnames.size());
    assertTrue(matchingSurnames.contains("Anka"));
    assertTrue(matchingSurnames.contains("Kanin"));
  }
}
