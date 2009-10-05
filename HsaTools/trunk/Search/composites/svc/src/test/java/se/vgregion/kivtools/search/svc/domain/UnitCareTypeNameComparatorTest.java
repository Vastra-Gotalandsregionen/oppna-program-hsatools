package se.vgregion.kivtools.search.svc.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;

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
