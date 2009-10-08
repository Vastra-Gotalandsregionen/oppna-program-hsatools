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
  private String fullName;

  @XmlElement
  private String firstName;

  @XmlElement
  private String lastName;

  /**
   * Empty constructor.
   */
  public PersonalRecord() {
    super();
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

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
