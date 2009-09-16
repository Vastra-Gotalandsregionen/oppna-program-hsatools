package se.vgregion.kivtools.hriv.presentation.exceptions;


/**
 * Indicates registration exceptions from Vårdval system.
 * 
 * @author Jonas Liljenfeldt & Joakim Olsson
 */
public class VardvalRegistrationException extends VardvalException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new VardvalRegistrationException.
   * 
   * @param message The message to use.
   */
  public VardvalRegistrationException(String message) {
    super(message);
  }
}
