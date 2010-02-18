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

package se.vgregion.kivtools.hriv.presentation.forms;

import java.io.Serializable;

/**
 * Holds form input when displaying close units.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class DisplayCloseUnitsSimpleForm implements Serializable {

  private static final long serialVersionUID = 5237982284800930275L;
  private String address = "Gata, ort";
  private boolean searchFlag;

  public boolean getSearchFlag() {
    return searchFlag;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Sets the search flag to true.
   */
  public void setSearchFlag() {
    searchFlag = true;
  }

  /**
   * Resets the search flag to false.
   */
  public void resetSearchFlag() {
    searchFlag = false;
  }
}
