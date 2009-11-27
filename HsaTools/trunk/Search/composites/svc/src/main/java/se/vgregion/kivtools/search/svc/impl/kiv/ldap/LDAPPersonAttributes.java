package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

/**
 * Enumeration of searchable criterions.
 * 
 * @author David Bennehult
 */
public enum LDAPPersonAttributes {
  /** given name. */
  GIVEN_NAME("givenName"),
  /** surname. */
  SURNAME("sn"),
  /** Employment title. */
  EMPLOYMENT_TITLE("title"),
  /** user id. */
  USER_ID("vgr-id"),
  /** Unit name. */
  EMPLOYED_AT_UNIT("vgrStrukturPerson"),
  /** hsaSpecialityCode. */
  SPECIALITY_AREA_CODE("hsaSpecialityCode"),
  /** user profession. */
  PROFESSION("hsaTitle"),
  /** mail. */
  E_MAIL("mail"),
  /** hsaLanguageKnowledgeCode. */
  LANGUAGE_KNOWLEDGE_CODE("hsaLanguageKnowledgeCode"),
  /** administration. */
  ADMINISTRATION("vgrAO3kod");

  private String value;

  private LDAPPersonAttributes(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
