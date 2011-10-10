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
