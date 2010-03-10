/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.search.domain.values;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;

public class HealthcareTypeConditionHelperTest {
  private HealthcareTypeConditionHelper helper;
  private Unit unit;

  @Before
  public void setUp() throws Exception {
    helper = new HealthcareTypeConditionHelper();
    HealthcareTypeConditionHelper.resetInternalCache();
    unit = new Unit();
  }

  @After
  public void tearDown() {
    HealthcareTypeConditionHelper.resetInternalCache();
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
    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.empty");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(0, helper.getAllHealthcareTypes().size());

    HealthcareTypeConditionHelper.resetInternalCache();

    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.one_property");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(1, helper.getAllHealthcareTypes().size());

    HealthcareTypeConditionHelper.resetInternalCache();

    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.two_properties");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(2, helper.getAllHealthcareTypes().size());

    List<HealthcareType> allUnfilteredHealthCareTypes = helper.getAllUnfilteredHealthCareTypes();
    assertNotNull(allUnfilteredHealthCareTypes);
    assertEquals(1, allUnfilteredHealthCareTypes.size());

    HealthcareTypeConditionHelper.resetInternalCache();

    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.one_property_plus_subindex");
    assertNotNull(helper.getAllHealthcareTypes());
    assertEquals(1, helper.getAllHealthcareTypes().size());
  }

  @Test
  public void testGetHealthcareTypeEmptyHelper() {
    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.empty");

    HealthcareType healthcareType = helper.getHealthcareTypeByIndex(Integer.valueOf(1));
    assertNull(healthcareType);
    healthcareType = helper.getHealthcareTypeByName("Akutmottagning");
    assertNull(healthcareType);
  }

  @Test
  public void testGetHealthcareTypePopulatedHelper() {
    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.one_property");

    // Test matching healthcare type.
    HealthcareType healthcareType = helper.getHealthcareTypeByIndex(Integer.valueOf(1));
    assertNotNull(healthcareType);
    assertEquals("Akutmottagning", healthcareType.getDisplayName());
    healthcareType = helper.getHealthcareTypeByName("Akutmottagning");
    assertNotNull(healthcareType);
    assertEquals("Akutmottagning", healthcareType.getDisplayName());

    // Test non matching healthcare type.
    healthcareType = helper.getHealthcareTypeByIndex(Integer.valueOf(2));
    assertNull(healthcareType);
    healthcareType = helper.getHealthcareTypeByName("Sjukhus");
    assertNull(healthcareType);
  }

  @Test
  public void testAssignHealthcareTypes() {
    try {
      helper.getHealthcareTypesForUnit(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    helper.setImplResourcePath("testproperties.healthcaretypeconditionhelper.assign_healthcaretype");
    try {
      helper.getHealthcareTypesForUnit(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    List<HealthcareType> healthcareTypes = helper.getHealthcareTypesForUnit(unit);
    assertEquals(0, healthcareTypes.size());

    List<String> classificationCodes = new ArrayList<String>();
    unit.setVgrCareType("");
    unit.setHsaBusinessClassificationCode(classificationCodes);
    healthcareTypes = helper.getHealthcareTypesForUnit(unit);
    assertEquals(0, healthcareTypes.size());

    unit.setVgrCareType("01");
    classificationCodes.add("1000");
    healthcareTypes = helper.getHealthcareTypesForUnit(unit);
    assertEquals(1, healthcareTypes.size());
  }
}
