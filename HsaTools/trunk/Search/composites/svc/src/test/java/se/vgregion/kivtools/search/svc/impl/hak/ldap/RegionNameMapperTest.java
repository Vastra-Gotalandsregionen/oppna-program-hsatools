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

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class RegionNameMapperTest {
  private RegionNameMapper regionNameMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    regionNameMapper = new RegionNameMapper();
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    RegionNameMapper regionNameMapper = new RegionNameMapper();
    assertNotNull(regionNameMapper);
  }

  @Test
  public void testNoRegionNameFound() {
    String regionName = (String) regionNameMapper.mapFromContext(dirContextOperations);
    assertNull(regionName);
  }

  @Test
  public void testRegionNameFound() {
    dirContextOperations.addAttributeValue("regionName", "abc123");
    String regionName = (String) regionNameMapper.mapFromContext(dirContextOperations);
    assertEquals("abc123", regionName);
  }
}
