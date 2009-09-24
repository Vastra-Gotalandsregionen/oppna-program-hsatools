package se.vgregion.kivtools.search.util.netwise;

/**
 * Helper class for Netwise services.
 * 
 * @author Joakim Olsson
 *
 */
public class NetwiseServicesUtil {

  /**
   * Helper-method for cleaning up phone numbers to the format expected by the webservice.
   * 
   * @param phoneNumber The phone number to clean up.
   * @return The phone number with any leading +46 replaced with a 0 and in the format xxx-xxxxxx (eg. 070-123456).
   */
  public static String cleanPhoneNumber(String phoneNumber) {
    String result = phoneNumber.replaceFirst("^\\+46", "0");
    result = result.replaceAll("[- ]", "");
    result = result.replaceFirst("^(.{3})(.*)$", "$1-$2");
    return result;
  }

}