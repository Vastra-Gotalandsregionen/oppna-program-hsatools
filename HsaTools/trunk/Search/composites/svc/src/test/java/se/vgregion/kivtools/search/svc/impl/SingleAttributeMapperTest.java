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

package se.vgregion.kivtools.search.svc.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class SingleAttributeMapperTest {
  private SingleAttributeMapper singleAttributeMapper = new SingleAttributeMapper("mail");;
  private DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();

  @Test
  public void mapperReturnNullIfAttributeNotFound() {
    String email = (String) singleAttributeMapper.mapFromContext(dirContextOperations);
    assertNull(email);
  }

  @Test
  public void mapperReturnAttributeValueIfFound() {
    dirContextOperations.addAttributeValue("mail", "anders.ask@lthalland.se");
    String email = (String) singleAttributeMapper.mapFromContext(dirContextOperations);
    assertEquals("anders.ask@lthalland.se", email);
  }

  @Test(expected = IllegalArgumentException.class)
  public void mapperThrowsExceptionOnConstructionIfEmptyAttributeNameIsSpecified() {
    new SingleAttributeMapper("   ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void mapperThrowsExceptionOnConstructionIfNullAttributeNameIsSpecified() {
    new SingleAttributeMapper(null);
  }
}
