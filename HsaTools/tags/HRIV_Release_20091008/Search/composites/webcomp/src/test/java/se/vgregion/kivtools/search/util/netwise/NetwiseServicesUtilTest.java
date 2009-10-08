package se.vgregion.kivtools.search.util.netwise;

import static org.junit.Assert.*;

import org.junit.Test;

public class NetwiseServicesUtilTest {

  @Test
  public void testInstantiation() {
    NetwiseServicesUtil netwiseServicesUtil = new NetwiseServicesUtil();
    assertNotNull(netwiseServicesUtil);
  }

  @Test
  public void testCleanPhoneNumber() {
    String input = "+46-705-123456";
    String expected = "070-5123456";
    String result = NetwiseServicesUtil.cleanPhoneNumber(input);
    assertEquals(expected, result);
  }
}
