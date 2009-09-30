package se.vgregion.kivtools.search.svc.domain;

import java.util.ArrayList;
import java.util.Arrays;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;

/**
 * 
 * @author david
 *
 */
public class UnitCareTypeNameComparatorTest {
  private static String HEALTHCARE_TYPE_DISPLAY_NAME_1 = "unitDisplayName1";
  private static String HEALTHCARE_TYPE_DISPLAY_NAME_2 = "unitDisplayName2";
  UnitCareTypeNameComparator unitCareTypeNameComparator;
  Unit mockUnit1;
  Unit mockUnit2;
  HealthcareType mockHealthcareType1;
  HealthcareType mockHealthcareType2;

  @Before
  public void setup() {
    unitCareTypeNameComparator = new UnitCareTypeNameComparator();
    mockUnit1 = EasyMock.createMock(Unit.class);
    mockUnit2 = EasyMock.createMock(Unit.class);
    mockHealthcareType1 = EasyMock.createMock(HealthcareType.class);
    mockHealthcareType2 = EasyMock.createMock(HealthcareType.class);
  }

  /**
   * Test that two different Healthcare types returns false
   */
  @Test
  public void testCompareDifferent() {
    EasyMock.expect(mockHealthcareType1.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_1);
    EasyMock.expect(mockHealthcareType2.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_2);
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType1));
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType2));
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    int compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(-1, compareResult);
  }
  
  /**
   * Test that two of the same Healthcare types returns true
   */
  @Test
  public void testCompareSame() {
    EasyMock.expect(mockHealthcareType1.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_1);
    EasyMock.expect(mockHealthcareType2.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_1);
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType1));
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType2));
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    int compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(0, compareResult);
  }
  
  /**
   * Test that two of the same Healthcare types returns true
   */
  @Test
  public void testCompareWithNullException() {
    
    // Test for HEALTHCARE_TYPE_DISPLAY_NAME_1 as null and HEALTHCARE_TYPE_DISPLAY_NAME_2 is not
    EasyMock.expect(mockHealthcareType2.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_2);
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(null);
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType2));
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    int compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(1, compareResult);
    
    // Test for HEALTHCARE_TYPE_DISPLAY_NAME_2 as null and HEALTHCARE_TYPE_DISPLAY_NAME_1 is not
    EasyMock.reset(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    EasyMock.expect(mockHealthcareType1.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_1);
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType1));
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(null);
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(-1, compareResult);
    
    // Test for HEALTHCARE_TYPE_DISPLAY_NAME_2 as null and HEALTHCARE_TYPE_DISPLAY_NAME_1 is null
    EasyMock.reset(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    EasyMock.expect(mockUnit1.getName()).andReturn("unitName1");
    EasyMock.expect(mockUnit2.getName()).andReturn("unitName2");
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(null);
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(null);
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(-1, compareResult);
  }

    /**
   * Test that two of the same Healthcare types returns true
   */
  @Test
  public void testCompareWithIndexOutOfBoundsException() {

    // Test for HEALTHCARE_TYPE_DISPLAY_NAME_1 as empty list and HEALTHCARE_TYPE_DISPLAY_NAME_2 is not
    EasyMock.expect(mockHealthcareType2.getDisplayName()).andReturn(HEALTHCARE_TYPE_DISPLAY_NAME_2);
    EasyMock.expect(mockUnit1.getHealthcareTypes()).andReturn(new ArrayList());
    EasyMock.expect(mockUnit2.getHealthcareTypes()).andReturn(Arrays.asList(mockHealthcareType2));
    EasyMock.replay(mockUnit1, mockUnit2, mockHealthcareType1, mockHealthcareType2);
    int compareResult = unitCareTypeNameComparator.compare(mockUnit1, mockUnit2);
    Assert.assertEquals(1, compareResult);
  }

}
