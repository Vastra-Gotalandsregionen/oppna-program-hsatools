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
