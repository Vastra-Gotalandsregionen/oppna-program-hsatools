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
import se.vgregion.kivtools.util.StringUtil;

public class ProfileImageMapperTest {
  private ProfileImageMapper profileImageMapper;
  private DirContextOperationsMock dirContextOperations;

  @Before
  public void setUp() throws Exception {
    profileImageMapper = new ProfileImageMapper();
    dirContextOperations = new DirContextOperationsMock();
  }

  @Test
  public void testInstantiation() {
    ProfileImageMapper profileImageMapper = new ProfileImageMapper();
    assertNotNull(profileImageMapper);
  }

  @Test
  public void testNoProfileImageFound() {
    byte[] profileImage = (byte[]) profileImageMapper.mapFromContext(dirContextOperations);
    assertNull(profileImage);
  }

  @Test
  public void testProfileImageFound() {
    dirContextOperations.addAttributeValue("jpegPhoto", StringUtil.getBytes("MockImageData", "UTF-8"));
    byte[] profileImage = (byte[]) profileImageMapper.mapFromContext(dirContextOperations);
    assertEquals("MockImageData", StringUtil.getString(profileImage, "UTF-8"));
  }
}
