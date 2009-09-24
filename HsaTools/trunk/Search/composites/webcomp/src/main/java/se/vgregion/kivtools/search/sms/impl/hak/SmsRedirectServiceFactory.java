package se.vgregion.kivtools.search.sms.impl.hak;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.sms.SMSRedirect;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.sms.SMSRedirectSoap;

/**
 * Factory-class used to retrieve a SOAP-service for the Netwise integration.
 * 
 * @author David Bennehult
 */
public class SmsRedirectServiceFactory {

  /**
   * Return default SmsRedirectSoap object.
   * 
   * @return SmsRedirectSoap object.
   */
  public static SMSRedirectSoap getSmsRedirectSoap() {
    return new SMSRedirect().getSMSRedirectSoap();
  }

}
