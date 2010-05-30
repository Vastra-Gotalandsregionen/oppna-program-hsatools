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

package se.vgregion.kivtools.hriv.intsvc.restservices.personalrecordservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Person representation for personal record service.
 * 
 * @author David Bennehult & Joakim Olsson
 * 
 */
@XmlRootElement(name = "personRecord")
@XmlAccessorType(XmlAccessType.NONE)
public class PersonalRecord {

  @XmlElement
  private final String fullName;

  @XmlElement
  private final String firstName;

  @XmlElement
  private final String lastName;

  /**
   * Empty constructor.
   */
  public PersonalRecord() {
    fullName = null;
    firstName = null;
    lastName = null;
  }

  /**
   * Create a personRecord object.
   * 
   * @param firstName person's first name.
   * @param lastName person's last name.
   * @param fullName person's full name.
   */
  public PersonalRecord(String firstName, String lastName, String fullName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.fullName = fullName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
