package se.vgregion.kivtools.search.sms.impl.hak;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.sms.SMSRedirectSoap;

public class SmsRedirectSoapStub implements SMSRedirectSoap {

  private String url;

  public SmsRedirectSoapStub(String url) {
    super();
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
