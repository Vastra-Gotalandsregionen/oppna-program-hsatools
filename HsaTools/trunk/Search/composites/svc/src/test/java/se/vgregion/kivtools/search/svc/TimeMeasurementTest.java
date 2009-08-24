package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TimeMeasurementTest {

  TimeMeasurement timeMeasurement;

  @Before
  public void setup() {
    timeMeasurement = new TimeMeasurement(1000);

  }

  @Test
  public void testGetElapsedTimeInMillisSeconds() {
    Long expectedResult = Long.valueOf(1000);
    assertEquals(expectedResult, timeMeasurement.getElapsedTimeInMillisSeconds());
  }

  @Test
  public void testGetElapsedTimeInSeconds() {
    Long expectedResult = Long.valueOf(1);
    assertEquals(expectedResult, timeMeasurement.getElapsedTimeInSeconds());
  }
  
  @Test
  public void tesAdd(){
    TimeMeasurement t1 = new TimeMeasurement(1000);
    TimeMeasurement t2 = new TimeMeasurement(500);
    TimeMeasurement result =  TimeMeasurement.add(t1, t2);
    Long elapsedTimeResult = Long.valueOf(1500);
    assertEquals(elapsedTimeResult, result.getElapsedTimeInMillisSeconds());
  }
  
  @Test
  public void testDefaultValues(){
    TimeMeasurement timeMeasurement = new TimeMeasurement();
    long start = System.nanoTime();
    timeMeasurement.start();
    timeMeasurement.stop();
    long stop = System.nanoTime();
    long testTime = stop - start;
    // Testtime should be greater than timeMeasurment time.
    assertTrue(testTime > timeMeasurement.getElapsedTimeInMillisSeconds());
  }
}
