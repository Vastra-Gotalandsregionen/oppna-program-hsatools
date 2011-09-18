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

package se.vgregion.kivtools.hriv.intsvc.ldap.eniro;

import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Container for a unit which also hold some meta data about the unit which is used by the InformationPusherEniro-service.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class UnitComposition {

  /**
   * 
   * @author david
   * 
   */
  public enum UnitType {
    CARE_CENTER, OTHER_CARE;
  }

  private final Unit eniroUnit = new Unit();
  private String dn;
  private UnitType careType;

  public UnitType getCareType() {
    return this.careType;
  }

  public void setCareType(UnitType careType) {
    this.careType = careType;
  }

  public Unit getEniroUnit() {
    return this.eniroUnit;
  }

  public String getDn() {
    return this.dn;
  }

  public void setDn(String dn) {
    this.dn = dn;
  }

  /**
   * Gets the distinguished name of the units parent.
   * 
   * @return The distinguished name of the units parent.
   */
  public String getParentDn() {
    String value = "";
    if (!StringUtil.isEmpty(this.dn)) {
      DistinguishedName distinguishedName = new DistinguishedName(this.dn);
      distinguishedName.removeLast();
      value = distinguishedName.toString();
    }
    return value;
  }
}
