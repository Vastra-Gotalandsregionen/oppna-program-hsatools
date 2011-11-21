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
 * @author David & Nhi
 * 
 */
public enum KivwsCodeTableName implements CodeTableNameInterface {
  /** Administration Form. */
  HSA_ADMINISTRATION_FORM("hsaadministrationform"),
  /** Business classification code. */
  HSA_BUSINESSCLASSIFICATION_CODE("hsabusinessclassificationcode"),
  /** Business type code **/
  HSA_BUSINESS_TYPE("hsabusinesstype"),
  /** County code. */
  HSA_COUNTY_CODE("hsacountycode"),
  /** Municipality code. */
  HSA_MUNICIPALITY_CODE("hsamunicipalitycode"),
  /** Management code. */
  HSA_MANAGEMENT_CODE("hsamanagementcode"),
  /** Speciality code. */
  HSA_SPECIALITY_CODE("hsaspecialitycode"),
  /** AO3 code (responsibility area code). */
  VGR_AO3_CODE("vgrao3kod"),
  /** Care type. */
  VGR_CARE_TYPE("vgrcaretype"),
  /** Language knowledge. */
  HSA_LANGUAGE_KNOWLEDGE_CODE("hsalanguageknowledgecode"),
  /** Employment titles. */
  PA_TITLE_CODE("patitlecode"),
  /** Profession title. */
  HSA_TITLE("hsatitle");

  private final String codeTableName;

  private KivwsCodeTableName(String s) {
    codeTableName = s;
  }

  @Override
  public String toString() {
    return codeTableName;
  }
}
