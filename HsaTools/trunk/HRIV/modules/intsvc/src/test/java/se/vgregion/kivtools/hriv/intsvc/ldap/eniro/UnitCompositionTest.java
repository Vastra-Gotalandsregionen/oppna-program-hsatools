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
