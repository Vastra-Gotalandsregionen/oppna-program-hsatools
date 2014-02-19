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

package se.vgregion.kivtools.search.domain.values;

/**
 * Enum types of valid code table names.
 * 
 * @author David & Jonas
 * 
 */
public enum CodeTableName implements CodeTableNameInterface {
  /** Administration Form. */
  HSA_ADMINISTRATION_FORM("list_hsaAdministrationForm"),
  /** Business classification code. */
  HSA_BUSINESSCLASSIFICATION_CODE("list_hsaBusinessClassificationCode"),
  /** County code. */
  HSA_COUNTY_CODE("list_hsaCountyCode"),
  /** Municipality code. */
  HSA_MUNICIPALITY_CODE("list_hsaMunicipalityCode"),
  /** Management code. */
  HSA_MANAGEMENT_CODE("list_hsaManagementCode"),
  /** Speciality code. */
  HSA_SPECIALITY_CODE("list_hsaSpecialityCode"),
  /** AO3 code (responsibility area code). */
  VGR_AO3_CODE("list_vgrAO3kod"),
  /** Care type. */
  VGR_CARE_TYPE("list_vgrCareType"),
  /** Language knowledge. */
  HSA_LANGUAGE_KNOWLEDGE_CODE("list_hsaLanguageKnowledgeCode"),
  /** Employment titles. */
  PA_TITLE_CODE("list_paTitleCode"),
  /** Profession title. */
  HSA_TITLE("list_hsaTitle");

  private final String codeTableName;

  private CodeTableName(String s) {
    codeTableName = s;
  }

  @Override
  public String toString() {
    return codeTableName;
  }
}
