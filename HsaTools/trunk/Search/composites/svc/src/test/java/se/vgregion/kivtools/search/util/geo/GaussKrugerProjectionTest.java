package se.vgregion.kivtools.search.util.geo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GaussKrugerProjectionTest {
  private GaussKrugerProjection projection;

  @Before
  public void setUp() throws Exception {
    projection = new GaussKrugerProjection();
  }

  @Test
  public void testInstantiation() throws Exception {
    try {
      new GaussKrugerProjection(null);
      fail("Exception expected");
    } catch (Exception e) {
      // Expected exception
    }

    try {
      new GaussKrugerProjection("ABC");
      fail("Exception expected");
    } catch (Exception e) {
      // Expected exception
    }

    GaussKrugerProjection projection = new GaussKrugerProjection("2.5V");
    assertNotNull(projection);

    projection = new GaussKrugerProjection("5V");
    assertNotNull(projection);

    projection = new GaussKrugerProjection("7.5V");
    assertNotNull(projection);

    projection = new GaussKrugerProjection("0V");
    assertNotNull(projection);

    projection = new GaussKrugerProjection("2.50");
    assertNotNull(projection);

    projection = new GaussKrugerProjection("50");
    assertNotNull(projection);
  }

  @Test
  public void testGetRT90() {
    int[] rt90 = projection.getRT90(45.3456, 23.1234);
    assertNotNull(rt90);
    assertEquals(5048831, rt90[0]);
    assertEquals(2073500, rt90[1]);
  }

  @Test
  public void testGetWGS84() {
    double[] wgs84 = projection.getWGS84(5048831, 2073500);
    assertNotNull(wgs84);
    assertEquals(45.34559051337126, wgs84[0], 0.0);
    assertEquals(23.123399861475555, wgs84[1], 0.0);
  }
}
