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

package se.vgregion.kivtools.hriv.presentation.comparators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.comparators.UnitCareTypeNameComparator;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareType;

/**
 * 
 * @author david
 * 
 */
public class UnitCareTypeNameComparatorTest {
  private static final String HEALTHCARE_TYPE1 = "HealthcareType1";
  private static final String UNIT_NAME = "UnitName";

  UnitCareTypeNameComparator comparator;
  Unit unit1;
  Unit unit2;
  private HealthcareType healthcareType1;

  @Before
  public void setup() {
    comparator = new UnitCareTypeNameComparator();
    unit1 = new Unit();
    unit2 = new Unit();
    healthcareType1 = new HealthcareType();
    healthcareType1.setDisplayName(HEALTHCARE_TYPE1);
  }

  @Test
  public void testEmptyUnits() {
    try {
      comparator.compare(unit1, unit2);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }
  }

  @Test
  public void testUnitName() {
    unit1.setName(UNIT_NAME);
    unit2.setName(UNIT_NAME);

    int result = comparator.compare(unit1, unit2);
    assertEquals(0, result);
  }

  @Test
  public void testEmptyHealthcareTypes() {
    unit1.setHealthcareTypes(new ArrayList<HealthcareType>());
    unit2.setHealthcareTypes(new ArrayList<HealthcareType>());
    unit1.setName(UNIT_NAME);
    unit2.setName(UNIT_NAME);

    int result = comparator.compare(unit1, unit2);
    assertEquals(0, result);
  }

  @Test
  public void testSameHealthcareType() {
    List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
    healthcareTypes.add(healthcareType1);
    unit1.setHealthcareTypes(healthcareTypes);
    unit2.setHealthcareTypes(healthcareTypes);

    int result = comparator.compare(unit1, unit2);
    assertEquals(0, result);
  }

  @Test
  public void testEmptyUnit1() {
    List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
    healthcareTypes.add(healthcareType1);
    unit2.setHealthcareTypes(healthcareTypes);

    int result = comparator.compare(unit1, unit2);
    assertEquals(1, result);
  }

  @Test
  public void testEmptyUnit2() {
    List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
    healthcareTypes.add(healthcareType1);
    unit1.setHealthcareTypes(healthcareTypes);

    int result = comparator.compare(unit1, unit2);
    assertEquals(-1, result);
  }
}
