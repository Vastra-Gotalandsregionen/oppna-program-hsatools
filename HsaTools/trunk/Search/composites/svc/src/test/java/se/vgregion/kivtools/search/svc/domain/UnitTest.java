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
package se.vgregion.kivtools.search.svc.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;

public class UnitTest {

  private Unit unit;
  private List<HealthcareType> healthcareTypes;

  @Before
  public void setUp() {
    unit = new Unit();
    healthcareTypes = new ArrayList<HealthcareType>();
    unit.setHealthcareTypes(healthcareTypes);
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesNoHealtcareType() {
    assertTrue("Age interval and visiting rules should be displayed for a unit without healtcare types BVC, VC and JC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesBVCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Barnavårdscentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type BVC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesVCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Vårdcentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type VC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowAgeIntervalAndVisitingRulesJCHealtcareType() {
    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Jourcentral");
    healthcareTypes.add(healthcareType);
    assertFalse("Age interval and visiting rules should not be displayed for a unit with healtcare type JC", unit.isShowAgeIntervalAndVisitingRules());
  }

  @Test
  public void testIsShowInVgrVardVal() {
    assertFalse("Unit not in VGR Vardval should not show info regarding vardval", unit.isShowInVgrVardVal());

    unit.setVgrVardVal(true);
    assertFalse("Unit should not show info regarding vardval if it isn't of type Vårdcentral", unit.isShowInVgrVardVal());

    HealthcareType healthcareType = new HealthcareType();
    healthcareType.setDisplayName("Vårdcentral");
    healthcareTypes.add(healthcareType);
    assertTrue("Unit should show info regarding vardval if it is of type Vårdcentral and is part of VGR Vardval", unit.isShowInVgrVardVal());
  }
}
