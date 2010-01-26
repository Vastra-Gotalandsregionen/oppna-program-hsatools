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
package se.vgregion.kivtools.search.presentation.hak;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.kivtools.search.presentation.SearchServiceMock;
import se.vgregion.kivtools.util.StringUtil;

public class ProfileImageBeanTest {
  private ProfileImageBean profileImageBean;
  private SearchServiceMock searchServiceMock;

  @Before
  public void setUp() throws Exception {
    searchServiceMock = new SearchServiceMock();
    profileImageBean = new ProfileImageBean();
    profileImageBean.setSearchService(searchServiceMock);
  }

  @Test
  public void testInstantiation() {
    ProfileImageBean profileImageBean = new ProfileImageBean();
    assertNotNull(profileImageBean);
  }

  @Test
  public void testGetProfileImageByDnNoDataFound() throws UnsupportedEncodingException {
    MockHttpServletResponse response = new MockHttpServletResponse();
    String view = profileImageBean.getProfileImageByDn(response, "cn=Nina Kanin,ou=abc,ou=def");
    assertNull(view);
    searchServiceMock.assertFetchedDn("cn=Nina Kanin,ou=abc,ou=def");
    assertEquals("", response.getContentAsString());
  }

  @Test
  public void testGetProfileImageByDn() throws UnsupportedEncodingException {
    String mockProfileImage = "MockProfileImage";
    searchServiceMock.setProfileImage(StringUtil.getBytes(mockProfileImage, "UTF-8"));
    MockHttpServletResponse response = new MockHttpServletResponse();
    String view = profileImageBean.getProfileImageByDn(response, "cn=Nina Kanin,ou=abc,ou=def");
    assertNull(view);
    searchServiceMock.assertFetchedDn("cn=Nina Kanin,ou=abc,ou=def");
    assertEquals(mockProfileImage, response.getContentAsString());
  }
}
