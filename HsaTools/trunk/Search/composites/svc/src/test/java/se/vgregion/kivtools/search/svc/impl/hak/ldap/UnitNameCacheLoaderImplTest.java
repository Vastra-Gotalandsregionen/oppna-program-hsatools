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
import se.vgregion.kivtools.search.svc.UnitNameCache;

public class UnitNameCacheLoaderImplTest {
  private UnitNameCacheLoaderImpl unitNameCacheLoaderImpl;
  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    ldapTemplate = new LdapTemplateMock();
    unitNameCacheLoaderImpl = new UnitNameCacheLoaderImpl();
    unitNameCacheLoaderImpl.setLdapTemplate(ldapTemplate);
  }

  @Test
  public void testInstantiation() {
    UnitNameCacheLoaderImpl unitNameCacheLoaderImpl = new UnitNameCacheLoaderImpl();
    assertNotNull(unitNameCacheLoaderImpl);
  }

  @Test
  public void createEmptyCacheReturnNewEmptyCacheEachTime() {
    UnitNameCache emptyCache1 = unitNameCacheLoaderImpl.createEmptyCache();
    UnitNameCache emptyCache2 = unitNameCacheLoaderImpl.createEmptyCache();
    assertEquals(0, emptyCache1.getMatchingUnitNames("").size());
    assertEquals(0, emptyCache2.getMatchingUnitNames("").size());
    assertNotSame(emptyCache1, emptyCache2);
  }

  @Test
  public void testLoadCache() {
    DirContextOperationsMock unit1 = new DirContextOperationsMock();
    unit1.addAttributeValue("ou", "Vårdcentralen Hylte");
    DirContextOperationsMock unit2 = new DirContextOperationsMock();
    unit2.addAttributeValue("cn", "Tandregleringen\\, Varberg");
    this.ldapTemplate.addDirContextOperationForSearch(unit1);
    this.ldapTemplate.addDirContextOperationForSearch(unit2);

    UnitNameCache unitNameCache = unitNameCacheLoaderImpl.loadCache();

    this.ldapTemplate.assertSearchFilter("(|(objectclass=organizationalUnit)(objectclass=organizationalRole))");

    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("le");
    assertEquals(2, matchingUnitNames.size());
    assertTrue(matchingUnitNames.contains("Vårdcentralen Hylte"));
    assertTrue(matchingUnitNames.contains("Tandregleringen, Varberg"));
  }
}
