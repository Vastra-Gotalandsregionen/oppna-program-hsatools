package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnitRepositoryFTVTest {
  private UnitRepositoryFTV unitRepository;

  @Before
  public void setUp() {
    unitRepository = new UnitRepositoryFTV();
  }

  @Test
  public void testInstantiation() {
    UnitRepositoryFTV unitRepository = new UnitRepositoryFTV();
    assertNotNull(unitRepository);
  }

  @Test
  public void testGetSearchBase() {
    assertEquals("ou=Folktandvården Västra Götaland,ou=Org,o=vgr", unitRepository.getSearchBase());
  }
}
