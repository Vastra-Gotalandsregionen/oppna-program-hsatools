package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UnitCompositionTest {

  private UnitComposition unitComposition;

  @Before
  public void setup() {
    unitComposition = new UnitComposition();
    unitComposition.setDn("ou=unit,ou=parent,o=organisation");
    //unitComposition.setCreateTimePoint(null);
    //unitComposition.setModifyTimePoint(null);
  }

  @Test
  public void testGetParentDn() {
    assertEquals("ou=parent,o=organisation", unitComposition.getParentDn());
  }

}
