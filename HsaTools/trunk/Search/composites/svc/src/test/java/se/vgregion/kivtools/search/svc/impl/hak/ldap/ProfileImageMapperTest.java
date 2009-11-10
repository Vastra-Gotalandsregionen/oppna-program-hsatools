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
