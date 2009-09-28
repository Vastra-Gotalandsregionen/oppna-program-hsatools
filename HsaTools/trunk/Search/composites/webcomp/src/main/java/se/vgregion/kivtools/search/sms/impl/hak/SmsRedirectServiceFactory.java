package se.vgregion.kivtools.search.sms.impl.hak;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

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
   * @param wsdlLocation The URL to use to find the WSDL.
   * @param namespace The namespace for the webservice.
   * @param qNameLocalPart The local part of the Qualified Name to use for the WSDL.
   * @return SmsRedirectSoap object.
   * @throws MalformedURLException if the provided wsdlLocation is not a valid URL.
   */
  public static SMSRedirectSoap getSmsRedirectSoap(String wsdlLocation, String namespace, String qNameLocalPart) throws MalformedURLException {
    return new SMSRedirect(new URL(wsdlLocation), new QName(namespace, qNameLocalPart)).getSMSRedirectSoap();
  }
}
