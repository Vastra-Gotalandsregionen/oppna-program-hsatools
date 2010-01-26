/**
 * Copyright 2009 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
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
