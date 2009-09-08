/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.search.svc.domain.values;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.Unit;

public class HealthcareTypeConditionHelperTest {

  private HealthcareTypeConditionHelper helper;
  private Unit unit;

  @Before
  public void setUp() throws Exception {
    helper = new HealthcareTypeConditionHelper();
    helper.resetInternalCache();
    unit = new Unit();
  }

  @Test
  public void testSetImplResourcePath() {
    try {
      helper.setImplResourcePath(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      helper.setImplResourcePath("");
      fail("MissingResourceException expected");
    } catch (MissingResourceException e) {
      // Expected exception
    }

    // "Real" resource path for VGR is se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions
    helper.setImplResourcePath("test.healthcaretypeconditionhelper.empty");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(0, helper.getAllHealthcareTypes().size());

    helper.resetInternalCache();

    helper.setImplResourcePath("test.healthcaretypeconditionhelper.one_property");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(1, helper.getAllHealthcareTypes().size());

    helper.resetInternalCache();

    helper.setImplResourcePath("test.healthcaretypeconditionhelper.two_properties");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(2, helper.getAllHealthcareTypes().size());

    helper.resetInternalCache();

    helper.setImplResourcePath("test.healthcaretypeconditionhelper.one_property_plus_subindex");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(1, helper.getAllHealthcareTypes().size());
  }

  @Test
  public void testAssignHealthcareTypes() {
    try {
      helper.assignHealthcareTypes(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    helper.setImplResourcePath("test.healthcaretypeconditionhelper.assign_healthcaretype");
    try {
      helper.assignHealthcareTypes(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    helper.assignHealthcareTypes(unit);
    assertEquals(0, unit.getHealthcareTypes().size());

    List<String> classificationCodes = new ArrayList<String>();
    unit.setVgrCareType("");
    unit.setHsaBusinessClassificationCode(classificationCodes);
    helper.assignHealthcareTypes(unit);
    assertEquals(0, unit.getHealthcareTypes().size());

    unit.setVgrCareType("01");
    classificationCodes.add("1000");
    helper.assignHealthcareTypes(unit);
    assertEquals(1, unit.getHealthcareTypes().size());
  }
}
