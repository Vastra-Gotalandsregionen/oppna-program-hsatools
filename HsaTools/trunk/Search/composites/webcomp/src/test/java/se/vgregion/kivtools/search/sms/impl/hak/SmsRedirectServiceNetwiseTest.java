package se.vgregion.kivtools.search.sms.impl.hak;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SmsRedirectServiceNetwiseTest {

  private SmsRedirectServiceNetwise smsRedirectServiceNetwise;
  private SmsRedirectSoapStub smsRedirectSoapStub;
  private final String TEST_MOBILE_NUMBER = "0707-43434343";
  private final String URL = "http://resultUrl";
  
  @Before
  public void setup(){
    smsRedirectServiceNetwise = new SmsRedirectServiceNetwise();
    smsRedirectSoapStub = new SmsRedirectSoapStub(URL);
    smsRedirectServiceNetwise.setSmsRedirectSoap(smsRedirectSoapStub);
  }
  
  @Test
  public void testRetrieveSmsRedirectUrl() {
    String retrieveSmsRedirectUrl = smsRedirectServiceNetwise.retrieveSmsRedirectUrl(TEST_MOBILE_NUMBER);
    assertEquals(URL, retrieveSmsRedirectUrl);
  }

}
