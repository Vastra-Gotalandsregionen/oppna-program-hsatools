package se.vgregion.kivtools.search.presentation.exceptions;

/**
 * Generic class for all exceptions from the registration process.
 * 
 * @author Jonas Liljenfeldt & Joakim Olsson
 * 
 */
public class VardvalException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * {@inheritDoc}
   */
  public VardvalException(String message) {
    super(message);
  }

  /**
   * {@inheritDoc}
   */
  public VardvalException() {
  }

}
