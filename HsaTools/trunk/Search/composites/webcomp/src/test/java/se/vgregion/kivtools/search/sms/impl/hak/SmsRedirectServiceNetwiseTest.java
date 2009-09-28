package se.vgregion.kivtools.search.sms.impl.hak;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.sms.SMSRedirectSoap;

public class SmsRedirectServiceNetwiseTest {
  private SmsRedirectServiceNetwise smsRedirectServiceNetwise;
  private SmsRedirectSoapMock smsRedirectSoapMock;
  private final String TEST_MOBILE_NUMBER = "0707-43434343";
  private final String URL = "http://resultUrl";

  @Before
  public void setup() {
    smsRedirectServiceNetwise = new SmsRedirectServiceNetwise();
    smsRedirectSoapMock = new SmsRedirectSoapMock(URL);
    smsRedirectServiceNetwise.setSmsRedirectSoap(smsRedirectSoapMock);
  }

  @Test
  public void testRetrieveSmsRedirectUrl() {
    String retrieveSmsRedirectUrl = smsRedirectServiceNetwise.retrieveSmsRedirectUrl(TEST_MOBILE_NUMBER);
    assertEquals(URL, retrieveSmsRedirectUrl);
  }

  class SmsRedirectSoapMock implements SMSRedirectSoap {

    private String url;

    public SmsRedirectSoapMock(String url) {
      this.url = url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    @Override
    public String getUrlFromMNr(String mNr) {
      return url;
    }

    @Override
    public String getUrlFromMNrAndHPagin(String mNr, String hPagin) {
      return url;
    }
  }
}
