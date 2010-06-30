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

package se.vgregion.kivtools.search.svc.ldap.criterions;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Simple search criterion POJO for person searches.
 * 
 * @author Joakim Olsson
 */
public class SearchPersonCriterions {
  private String givenName;
  private String surname;
  private String userId;
  private String employmentTitle;
  private String employedAtUnit;
  private String specialityArea;
  private String profession;
  private String email;
  private String languageKnowledge;
  private String administration;

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getEmploymentTitle() {
    return employmentTitle;
  }

  public void setEmploymentTitle(String employmentTitle) {
    this.employmentTitle = employmentTitle;
  }

  public String getEmployedAtUnit() {
    return employedAtUnit;
  }

  public void setEmployedAtUnit(String employedAtUnit) {
    this.employedAtUnit = employedAtUnit;
  }

  public String getSpecialityArea() {
    return specialityArea;
  }

  public void setSpecialityArea(String specialityArea) {
    this.specialityArea = specialityArea;
  }

  public String getProfession() {
    return profession;
  }

  public void setProfession(String profession) {
    this.profession = profession;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLanguageKnowledge() {
    return languageKnowledge;
  }

  public void setLanguageKnowledge(String languageKnowledge) {
    this.languageKnowledge = languageKnowledge;
  }

  public String getAdministration() {
    return administration;
  }

  public void setAdministration(String administration) {
    this.administration = administration;
  }

  /**
   * Checks if the object is empty.
   * 
   * @return True if the object is empty, otherwise false.
   */
  public boolean isEmpty() {
    boolean empty = true;

    empty &= StringUtil.isEmpty(givenName);
    empty &= StringUtil.isEmpty(surname);
    empty &= StringUtil.isEmpty(userId);
    empty &= StringUtil.isEmpty(employmentTitle);
    empty &= StringUtil.isEmpty(employedAtUnit);
    empty &= StringUtil.isEmpty(specialityArea);
    empty &= StringUtil.isEmpty(profession);
    empty &= StringUtil.isEmpty(email);
    empty &= StringUtil.isEmpty(languageKnowledge);
    empty &= StringUtil.isEmpty(administration);

    return empty;
  }
}
