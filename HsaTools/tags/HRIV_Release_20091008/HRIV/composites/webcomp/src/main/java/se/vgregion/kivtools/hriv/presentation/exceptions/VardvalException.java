package se.vgregion.kivtools.hriv.presentation.exceptions;

/**
 * Generic class for all exceptions from the registration process.
 * 
 * @author Jonas Liljenfeldt & Joakim Olsson
 * 
 */
public class VardvalException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new VardvalException.
   * 
   * @param message The message to use.
   */
  public VardvalException(String message) {
    super(message);
  }

  /**
   * Constructs a new VardvalException.
   */
  public VardvalException() {
  }
}
