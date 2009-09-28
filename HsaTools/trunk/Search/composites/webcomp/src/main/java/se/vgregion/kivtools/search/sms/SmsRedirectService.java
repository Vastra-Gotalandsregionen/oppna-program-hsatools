package se.vgregion.kivtools.search.sms;

/**
 * Interface for SMS service functionality.
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
public interface SmsRedirectService {

  /**
   * Retrieves the URL to use for sending SMS to the specified mobile number.
   * 
   * @param mobileNumber Mobile phone number to retrieve SMS URL for.
   * @return the URL to use for sending SMS.
   */
  public String retrieveSmsRedirectUrl(String mobileNumber);
}
