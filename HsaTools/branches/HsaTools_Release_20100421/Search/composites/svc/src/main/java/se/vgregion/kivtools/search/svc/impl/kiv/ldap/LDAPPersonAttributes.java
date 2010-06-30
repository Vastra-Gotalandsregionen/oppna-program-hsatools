/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
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
  /** Middle name. */
  MIDDLE_NAME("hsaMiddleName"),
  /** Full name. */
  FULL_NAME("fullName"),
  /** surname. */
  SURNAME("sn"),
  /** Initials. */
  INITIALS("initials"),
  /** Nickname. */
  NICK_NAME("hsaNickName"),
  /** Employment title. */
  EMPLOYMENT_TITLE("title"),
  /** user id. */
  USER_ID("vgr-id"),
  /** Unit name. */
  EMPLOYED_AT_UNIT("vgrStrukturPerson"),
  /** Structure person dn. */
  STRUCTURE_PERSON_DN("vgrStrukturPersonDN"),
  /** hsaSpecialityCode. */
  SPECIALITY_AREA_CODE("hsaSpecialityCode"),
  /** user profession. */
  PROFESSION("hsaTitle"),
  /** mail. */
  E_MAIL("mail"),
  /** hsaLanguageKnowledgeCode. */
  LANGUAGE_KNOWLEDGE_CODE("hsaLanguageKnowledgeCode"),
  /** administration. */
  ADMINISTRATION("vgrAO3kod"),
  /** vgrOrgRel. */
  VGR_ORG_REL("vgrOrgRel"),
  /** vgrAnstform. */
  VGR_ANST_FORM("vgrAnstform"),
  /** */
  PERSON_IDENTITY_NUMBER("hsaPersonIdentityNumber"),
  /** hsaIdentity. */
  HSA_IDENTITY("hsaIdentity"),
  /** vgrAnsvarsnummer. */
  VGR_ANSVARSNUMMER("vgrAnsvarsnummer"),
  /** hsaPersonPrescriptionCode. */
  HSA_PERSON_PRESCRIPTION_CODE("hsaPersonPrescriptionCode"),
  /** hsaStartDate. */
  HSA_START_DATE("hsaStartDate"),
  /** hsaEndDate. */
  HSA_END_DATE("hsaEndDate");

  private String value;

  private LDAPPersonAttributes(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
