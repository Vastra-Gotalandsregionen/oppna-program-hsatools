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

package se.vgregion.kivtools.search.servlets.hak;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.servlets.hak.SmsRedirectSupportBean;
import se.vgregion.kivtools.search.sms.SmsRedirectService;

public class SmsRedirectSupportBeanTest {

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
