package se.vgregion.kivtools.search.presentation.types;

/**
 * Information regarding a signed document.
 * 
 * @author Jonas & Joakim
 */
public class SigningInformation {
  private final String nationalId;
  private final String samlResponse;

  /**
   * Constructs a SigningInformation object.
   * 
   * @param nationalId The national id of the user signing the document.
   * @param samlResponse The complete SAMLResponse from the signing.
   */
  public SigningInformation(String nationalId, String samlResponse) {
    this.nationalId = nationalId;
    this.samlResponse = samlResponse;
  }

  /**
   * Getter for the samlResponse property.
   * 
   * @return The complete SAMLResponse from the signing.
   */
  public String getSamlResponse() {
    return samlResponse;
  }

  /**
   * Getter for the nationalId property.
   * 
   * @return The national id of the user signing the document.
   */
  public String getNationalId() {
    return nationalId;
  }
}
