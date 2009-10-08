package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;

public class UnitCompositionTest {

  private UnitComposition unitComposition;

  @Test
  public void testInstance(){
    assertNotNull(new UnitComposition());
  }
  
  
  @Before
  public void setup() {
    unitComposition = new UnitComposition();
    unitComposition.setDn("ou=unit,ou=parent,o=organisation");
    unitComposition.setCareType(UnitComposition.UnitType.CARE_CENTER);
    //unitComposition.setCreateTimePoint(null);
    //unitComposition.setModifyTimePoint(null);
  }

  @Test
  public void testUnitComposition() {
    assertEquals("ou=unit,ou=parent,o=organisation", unitComposition.getDn());
    assertEquals("ou=parent,o=organisation", unitComposition.getParentDn());
    assertEquals(UnitType.CARE_CENTER, unitComposition.getCareType());
    assertNotNull(unitComposition.getEniroUnit());
  }

}
