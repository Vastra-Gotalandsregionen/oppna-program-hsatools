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
package se.vgregion.kivtools.search.svc.impl.hak;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class PersonEmailMapperTest {
  private PersonEmailMapper personEmailMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    personEmailMapper = new PersonEmailMapper();
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    PersonEmailMapper personEmailMapper = new PersonEmailMapper();
    assertNotNull(personEmailMapper);
  }

  @Test
  public void testNoEmailFound() {
    String email = (String) personEmailMapper.mapFromContext(dirContextOperations);
    assertNull(email);
  }

  @Test
  public void testEmailFound() {
    dirContextOperations.addAttributeValue("mail", "anders.ask@lthalland.se");
    String email = (String) personEmailMapper.mapFromContext(dirContextOperations);
    assertEquals("anders.ask@lthalland.se", email);
  }
}
