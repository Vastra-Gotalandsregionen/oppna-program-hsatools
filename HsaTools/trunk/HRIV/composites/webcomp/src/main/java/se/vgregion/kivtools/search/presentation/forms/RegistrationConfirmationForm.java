package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

public class RegistrationConfirmationForm implements Serializable {
  private static final long serialVersionUID = 1L;

  private String ssn, hsaIdentity, signature;

  public String getSsn() {
    return ssn;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public String getHsaIdentity() {
    return hsaIdentity;
  }

  public void setHsaIdentity(String hsaIdentity) {
    this.hsaIdentity = hsaIdentity;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
}
