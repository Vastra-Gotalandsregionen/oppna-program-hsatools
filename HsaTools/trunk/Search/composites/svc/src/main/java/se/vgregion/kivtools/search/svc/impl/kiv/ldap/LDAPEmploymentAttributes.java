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
public enum LDAPEmploymentAttributes {
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
  ADMINISTRATION("vgrAO3kod"),
  /** */
  CN("cn"),
  /** */
  OU("ou"),
  /** */
  HSA_PERSON_IDENTITY_NUMBER("hsaPersonIdentityNumber"),
  /** */
  VGR_ORG_REL("vgrOrgRel"),
  /** */
  VGR_ANSVARS_NUMMER("vgrAnsvarsnummer"),
  /** */
  HSA_START_DATE("hsaStartDate"),
  /** */
  HSA_END_DATE("hsaEndDate"),
  /** */
  HSA_SEDF_INVOICE_ADDRESS("hsaSedfInvoiceAddress"),
  /** */
  HSA_STREET_ADDRESS("hsaStreetAddress"),
  /** */
  HSA_INTERNAL_ADRESS("hsaInternalAddress"),
  /** */
  HSA_POSTAL_ADDRESS("hsaPostalAddress"),
  /** */
  HSA_SEDF_DELIVERY_ADDRESS("hsaSedfDeliveryAddress"),
  /** */
  FACSIMILE_TELEPHONE_NUMBER("facsimileTelephoneNumber"),
  /** */
  POSTAL_CODE("postalCode"),
  /** */
  LABELED_URI("labeledUri"),
  /** */
  VGR_ANST_FORM("vgrAnstform"),
  /** */
  VGR_FORMANS_GRUPP("vgrFormansgrupp"),
  /** */
  HSA_SEDF_SWITCHBOARD_TELEPHONE("hsaSedfSwitchboardTelephoneNo"),
  /** */
  VGR_AO3_KOD("vgrAO3kod"),
  /** */
  ORGANIZATIONAL_UNIT_NAME("organizationalUnitName"),
  /** */
  HSA_TELEPHONE_NUMBER("hsaTelephoneNumber"),
  /** */
  HSA_PUBLIC_TELEPHONE_NUMBER("hsaPublicTelephoneNumber"),
  /** */
  MOBILE_TELEPHONE_NUMBER("mobileTelephoneNumber"),
  /** */
  HSA_INTERNAL_PAGER_NUMBER("hsaInternalPagerNumber"),
  /** */
  PAGER_TELEPHONE_NUMBER("pagerTelephoneNumber"),
  /** */
  HSA_TEXT_PHONE_NUMBER("hsaTextPhoneNumber"),
  /** */
  MODIFY_TIMESTAMP("modifyTimestamp"),
  /** */
  MODIFYERS_NAME("modifyersName"),
  /** */
  HSA_TELEPHONE_TIME("hsaTelephoneTime"),
  /** */
  DESCRIPTION("description"),
  /** */
  L("l"),
  /** hsaManagerCode */
  HSA_MANAGER_CODE("hsaManagerCode"),
  /** */
  PA_TITLE_CODE("paTitleCode");

  private String value;

  private LDAPEmploymentAttributes(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
