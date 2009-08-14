package se.vgregion.kivtools.search.presentation.exceptions;

/**
 * Indicates registration exceptions from Vårdval system.
 * 
 * @author Jonas Liljenfeldt & Joakim Olsson
 */
public class VardvalRegistrationException extends VardvalException {

  private static final long serialVersionUID = 1L;

  /**
   * {@inheritDoc}
   */
  public VardvalRegistrationException(String message) {
    super(message);
  }
}
