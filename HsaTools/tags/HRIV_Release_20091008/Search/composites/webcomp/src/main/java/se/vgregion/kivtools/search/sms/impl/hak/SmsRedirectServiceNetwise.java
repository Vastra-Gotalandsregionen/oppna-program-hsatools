package se.vgregion.kivtools.search.sms.impl.hak;

import se.vgregion.kivtools.search.sms.SmsRedirectService;
import se.vgregion.kivtools.search.util.netwise.NetwiseServicesUtil;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.sms.SMSRedirectSoap;

/**
 * Class implementing Netwise SMS service.
 * 
 * @author David Bennehult
 * 
 */
public class SmsRedirectServiceNetwise implements SmsRedirectService {

  private SMSRedirectSoap smsRedirectSoap;

  public void setSmsRedirectSoap(SMSRedirectSoap smsRedirectSoap) {
    this.smsRedirectSoap = smsRedirectSoap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String retrieveSmsRedirectUrl(String mobilNumber) {
    String cleanedPhoneNumber = NetwiseServicesUtil.cleanPhoneNumber(mobilNumber);
    String url = smsRedirectSoap.getUrlFromMNr(cleanedPhoneNumber);
    return url;
  }
}
