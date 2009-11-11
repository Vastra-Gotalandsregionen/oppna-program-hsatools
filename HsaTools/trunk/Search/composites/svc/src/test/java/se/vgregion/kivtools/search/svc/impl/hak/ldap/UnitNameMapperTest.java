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
    List<String> matchingGivenNames = unitNameCache.getMatchingUnitNames("hylte");
    assertEquals(1, matchingGivenNames.size());
    assertEquals("Vårdcentralen Hylte", matchingGivenNames.get(0));
  }

  @Test
  public void testCn() {
    dirContextOperations.addAttributeValue("cn", "Tandregleringen\\, Varberg");
    unitNameMapper.mapFromContext(dirContextOperations);
    List<String> matchingGivenNames = unitNameCache.getMatchingUnitNames("berg");
    assertEquals(1, matchingGivenNames.size());
    assertEquals("Tandregleringen, Varberg", matchingGivenNames.get(0));
  }
}
