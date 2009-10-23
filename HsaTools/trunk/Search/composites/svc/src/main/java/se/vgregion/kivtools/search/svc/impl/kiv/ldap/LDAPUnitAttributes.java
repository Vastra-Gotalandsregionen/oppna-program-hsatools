package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

/**
 * Enumeration of searchable criterions.
 * 
 * @author David Bennehult
 */
public enum LDAPUnitAttributes {
  /** Unit id. */
  UNIT_ID("hsaIdentity"),
  /** Unit name. */
  UNIT_NAME("ou"),
  /** Administration. */
  ADMINISTRATION("vgrAO3kod"),
  /** User id. */
  RESPONSIBILITY("vgrAnsvarsnummer"),
  /** Business classification code. */
  BUSINESS_CLASSIFICATION_CODE("hsaBusinessClassificationCode"),
  /** Care type. */
  CARE_TYPE("vgrCareType");

  private String value;

  private LDAPUnitAttributes(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}