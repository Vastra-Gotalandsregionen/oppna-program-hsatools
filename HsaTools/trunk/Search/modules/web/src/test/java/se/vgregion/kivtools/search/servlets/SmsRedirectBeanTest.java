package se.vgregion.kivtools.search.servlets;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.servlets.SmsRedirectSupportBean;
import se.vgregion.kivtools.search.sms.SmsRedirectService;

public class SmsRedirectBeanTest {

  private static final String HTTP_REDIRECT_URL = "http://redirect_url";
  private static final String HTTP_ERROR_PAGE = "http://error_page";
  private static final String MOBILE_NUMBER = "423-432432";
  private SmsRedirectSupportBean smsRedirectSupportBean;

  @Before
  public void setup() {
    smsRedirectSupportBean = new SmsRedirectSupportBean();
    smsRedirectSupportBean.setSmsRedirectService(new SmsRedirectServiceMock());
  }

  @Test
  public void testSendSms() {
    String sendSmsUrl = smsRedirectSupportBean.sendSms(MOBILE_NUMBER);
    assertEquals("redirect:" + HTTP_REDIRECT_URL, sendSmsUrl);
  }
  
  @Test
  public void testSendSmsErrorPage(){
    String sendSmsUrl = smsRedirectSupportBean.sendSms("1234");
    assertEquals("redirect:" + HTTP_ERROR_PAGE, sendSmsUrl);
  }

  class SmsRedirectServiceMock implements SmsRedirectService {

    @Override
    public String retrieveSmsRedirectUrl(String mobileNumber) {
      String url = HTTP_ERROR_PAGE;
      if (mobileNumber.equals(MOBILE_NUMBER)) {
        url = HTTP_REDIRECT_URL;
      }
      return url;
    }
  }
}
