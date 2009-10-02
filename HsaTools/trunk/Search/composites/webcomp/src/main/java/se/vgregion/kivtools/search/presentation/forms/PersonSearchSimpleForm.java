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
package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Search form for person search.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
@SuppressWarnings("serial")
public class PersonSearchSimpleForm implements Serializable {
  private String givenName = "";
  private String sirName = "";
  private String vgrId = "";
  private String searchType = "name_selected";

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getSirName() {
    return sirName;
  }

  public void setSirName(String sirName) {
    this.sirName = sirName;
  }

  public String getVgrId() {
    return vgrId;
  }

  public void setVgrId(String vgrId) {
    this.vgrId = vgrId;
  }

  public String getSearchType() {
    return searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }

  /**
   * Checks if the form is empty.
   * 
   * @return true if givenName, sirName and vgrId is null or empty.
   */
  public boolean isEmpty() {
    boolean isEmpty = true;

    isEmpty &= StringUtil.isEmpty(givenName);
    isEmpty &= StringUtil.isEmpty(sirName);
    isEmpty &= StringUtil.isEmpty(vgrId);

    return isEmpty;
  }
}
