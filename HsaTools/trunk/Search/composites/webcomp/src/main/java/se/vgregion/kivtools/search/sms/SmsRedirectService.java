package se.vgregion.kivtools.search.sms;

/**
 * Interface for sms service functionality.
 * 
 * @author David Bennehult & Joakim Olsson
 *
 */
public interface SmsRedirectService {

  /**
   * Will lookup url to use for sending sms to specified mobile number.
   * 
   * @param mobileNumber Mobile phone number for retrieve specified url.
   * @return the url to use for sms service. 
   */
  public String retrieveSmsRedirectUrl(String mobileNumber);
}
