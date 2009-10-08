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
package se.vgregion.kivtools.search.svc.domain.values;

import java.io.Serializable;

import se.vgregion.kivtools.search.interfaces.IsEmptyMarker;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Municipality representation.
 */
public class Municipality implements IsEmptyMarker, Serializable, Comparable<Municipality> {

  private static final long serialVersionUID = 1L;

  private String municipalityCode;
  private String municipalityName;
  private String municipalityKey;
  private Integer index;

  public String getMunicipalityCode() {
    return municipalityCode;
  }

  public void setMunicipalityCode(String municipalityCode) {
    this.municipalityCode = municipalityCode;
  }

  public String getMunicipalityName() {
    return municipalityName;
  }

  public void setMunicipalityName(String municipalityName) {
    this.municipalityName = municipalityName;
  }

  public String getMunicipalityKey() {
    return municipalityKey;
  }

  /**
   * Setter for the municipalityKey property. Also sets the index.
   * 
   * @param municipalityKey The new value for the municipalityKey property.
   */
  public void setMunicipalityKey(String municipalityKey) {
    this.municipalityKey = municipalityKey;
    int indexPos = municipalityKey.indexOf("_") + 1;
    Integer parsedIndex = Integer.parseInt(municipalityKey.substring(indexPos));
    this.setIndex(parsedIndex);
  }

  public boolean isEmpty() {
    return StringUtil.isEmpty(municipalityName);
  }

  @Override
  public int compareTo(Municipality o) {
    return this.getIndex().compareTo(o.getIndex());
  }

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }
}
