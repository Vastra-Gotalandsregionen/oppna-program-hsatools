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
