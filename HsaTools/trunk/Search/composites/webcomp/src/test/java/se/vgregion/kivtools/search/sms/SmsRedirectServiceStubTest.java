package se.vgregion.kivtools.search.sms;

import static org.junit.Assert.*;

import org.junit.Test;

public class SmsRedirectServiceStubTest {

  @Test
  public void testRetrieveSmsRedirectUrl() {
    SmsRedirectServiceStub smsRedirectService = new SmsRedirectServiceStub();
    String smsRedirectUrl = smsRedirectService.retrieveSmsRedirectUrl("123");
    assertEquals("", smsRedirectUrl);
  }
}
