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
