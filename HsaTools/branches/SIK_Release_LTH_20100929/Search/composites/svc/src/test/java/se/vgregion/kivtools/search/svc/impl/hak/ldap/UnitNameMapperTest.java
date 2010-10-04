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
import se.vgregion.kivtools.search.svc.UnitNameCache;

public class UnitNameMapperTest {
  private UnitNameCache unitNameCache;
  private UnitNameMapper unitNameMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    unitNameCache = new UnitNameCache();
    unitNameMapper = new UnitNameMapper(unitNameCache);
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    UnitNameMapper unitNameMapper = new UnitNameMapper(unitNameCache);
    assertNotNull(unitNameMapper);
  }

  @Test
  public void testOu() {
    dirContextOperations.addAttributeValue("ou", "Vårdcentralen Hylte");
    unitNameMapper.mapFromContext(dirContextOperations);
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("hylte");
    assertEquals(1, matchingUnitNames.size());
    assertEquals("Vårdcentralen Hylte", matchingUnitNames.get(0));
  }

  @Test
  public void testCn() {
    dirContextOperations.addAttributeValue("cn", "Tandregleringen\\, Varberg");
    unitNameMapper.mapFromContext(dirContextOperations);
    List<String> matchingUnitNames = unitNameCache.getMatchingUnitNames("berg");
    assertEquals(1, matchingUnitNames.size());
    assertEquals("Tandregleringen, Varberg", matchingUnitNames.get(0));
  }
}
