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

package se.vgregion.kivtools.search.presentation.forms;

import java.io.Serializable;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Search form for person search.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class PersonSearchSimpleForm implements Serializable {
  private static final long serialVersionUID = 254420680562201343L;
  private String givenName = "";
  private String surname = "";
  private String userId = "";
  private String searchType = "simple";
  private String administration;
  private String employedAtUnit;
  private String profession;
  private String employmentTitle;
  private String email;
  private String specialityArea;
  private String languageKnowledge;
  private String phone;
  private String employmentPosition;
  private String description;
  private String showAll;

  public String getGivenName() {
    return this.givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getSurname() {
    return this.surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getSearchType() {
    return this.searchType;
  }

  public void setSearchType(String searchType) {
    this.searchType = searchType;
  }

  public String getAdministration() {
    return this.administration;
  }

  public void setAdministration(String administration) {
    this.administration = administration;
  }

  public String getEmployedAtUnit() {
    return this.employedAtUnit;
  }

  public void setEmployedAtUnit(String employedAtUnit) {
    this.employedAtUnit = employedAtUnit;
  }

  public String getProfession() {
    return this.profession;
  }

  public void setProfession(String profession) {
    this.profession = profession;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSpecialityArea() {
    return this.specialityArea;
  }

  public void setSpecialityArea(String specialityArea) {
    this.specialityArea = specialityArea;
  }

  public String getLanguageKnowledge() {
    return this.languageKnowledge;
  }

  public void setLanguageKnowledge(String languageKnowledge) {
    this.languageKnowledge = languageKnowledge;
  }

  public String getEmploymentTitle() {
    return this.employmentTitle;
  }

  public void setEmploymentTitle(String employmentTitle) {
    this.employmentTitle = employmentTitle;
  }

  /**
   * Checks if the form is empty.
   * 
   * @return true if givenName, sirName and vgrId is null or empty.
   */
  public boolean isEmpty() {
    boolean isEmpty = true;

    isEmpty &= StringUtil.isEmpty(this.givenName);
    isEmpty &= StringUtil.isEmpty(this.surname);
    isEmpty &= StringUtil.isEmpty(this.userId);

    return isEmpty;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmploymentPosition() {
    return this.employmentPosition;
  }

  public void setEmploymentPosition(String employmentPosition) {
    this.employmentPosition = employmentPosition;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setShowAll(String showAll) {
    this.showAll = showAll;
  }

  public String getShowAll() {
    return showAll;
  }

}
