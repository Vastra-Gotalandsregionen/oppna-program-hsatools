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

package se.vgregion.kivtools.search.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

/**
 * This class represents an Employment (swedish Anställning) Check out vgrAnstallning for reference.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Anders Asplund / KnowIT
 */
public class Employment implements Serializable {

  private static final long serialVersionUID = 1L;

  // Common name (e.g. 1750935136900000017)
  private String cn;
  // Organizational Unit Name (e.g. Barn- och ungdomspsykiatrisk mottagning Borås)
  private String ou;
  // Person-id e.g. 197209184683
  private String hsaPersonIdentityNumber;
  // HsaIdentitie to the Units where the person is employed e.g. SE2321000131-E000000000101
  private String vgrOrgRel;
  // Namn på enheten
  private String name;
  // Uppdaterat sv vem
  private String modifyersName;
  // Ansvarsnumer e.g. 1, 2
  private String vgrAnsvarsnummer;
  // e.g. 3
  private String vgrAnstform;
  // e.g. Psykolog,leg.
  private String title;
  // e.g. 12
  private String vgrFormansgrupp;
  // Ansvarsområdes kod e.g. 082
  private String vgrAO3kod;
  // Beskrivning
  private List<String> description;

  // Hemsida e.g. http://www.vgregion.se/vgrtemplates/Page____25161.aspx
  private String labeledUri;

  // Distinguished Names
  // Anställnigobjektest plats i organisationen
  private DN vgrStrukturPerson;
  // Distinguished Name (e.g. cn=1750935136900000017,cn=annth38,ou=Personal,o=VGR
  private DN dn;

  // Time Objects
  // Anställningspreiod(hsaStartDate till hsaEndDate)
  private TimeInterval employmentPeriod;
  // Senast uppdaterad
  private TimePoint modifyTimestamp;

  // Address objects
  // Fakturaadress e.g. Södra Älvsborgs Sjukhus$Låne- och fakturaservice $ $ $501 82$Borås
  private Address hsaSedfInvoiceAddress;
  // Besöksadress e.g. Elinsdalsgatan 8, Borås
  private Address hsaStreetAddress;
  // Internadress e.g. BUP Elinsdahl, Borås
  private Address hsaInternalAddress;
  // Postadress e.g. Södra Älvsborgs Sjukhus$Barn- och ungdomspsykiatrisk mottagning $ $ $501 82$Borås
  private Address hsaPostalAddress;
  // Leveransadress e.g. Södra Älvsborgs Sjukhus$Barn- och ungdomspsmottagning $Elinsdalsgatan 8$ $504 33$Borås
  private Address hsaSedfDeliveryAddress;
  private Address hsaConsigneeAddress;
  // Postnummer e.g. 416 73
  private ZipCode zipCode;

  // Phone Numbers
  // Faxnummer
  private PhoneNumber facsimileTelephoneNumber;
  // Direkttelefon visas inom organisationen
  private final List<PhoneNumber> hsaTelephoneNumber = new ArrayList<PhoneNumber>();
  // Direkttelefon visas för allmänheten
  private PhoneNumber hsaPublicTelephoneNumber;
  // Mobilnummer
  private PhoneNumber mobileTelephoneNumber;
  // Personsökare
  private PhoneNumber hsaInternalPagerNumber;
  // Minicall
  private PhoneNumber pagerTelephoneNumber;
  // Texttelefon
  private PhoneNumber hsaTextPhoneNumber;
  // Växeltelefon
  private PhoneNumber hsaSedfSwitchboardTelephoneNo;
  // Telefontid
  private final List<WeekdayTime> hsaTelephoneTime = new ArrayList<WeekdayTime>();

  private String locality;

  private String position;

  private boolean primaryEmployment;

  public String getCn() {
    return cn;
  }

  public void setCn(String cn) {
    this.cn = cn;
  }

  public DN getDn() {
    return dn;
  }

  /**
   * Gets the DN as a Base64-encoded string.
   * 
   * @return The employments DN as a Base64-encoded string.
   */
  public String getDnBase64() {
    return StringUtil.base64Encode(dn.toString());
  }

  public void setDn(DN dn) {
    this.dn = dn;
  }

  public String getOu() {
    return ou;
  }

  public void setOu(String ou) {
    this.ou = ou;
  }

  public String getHsaPersonIdentityNumber() {
    return hsaPersonIdentityNumber;
  }

  public void setHsaPersonIdentityNumber(String hsaPersonIdentityNumber) {
    this.hsaPersonIdentityNumber = hsaPersonIdentityNumber;
  }

  public String getVgrOrgRel() {
    return vgrOrgRel;
  }

  public void setVgrOrgRel(String vgrOrgRel) {
    this.vgrOrgRel = vgrOrgRel;
  }

  public String getVgrAnsvarsnummer() {
    return vgrAnsvarsnummer;
  }

  public void setVgrAnsvarsnummer(String vgrAnsvarsnummer) {
    this.vgrAnsvarsnummer = vgrAnsvarsnummer;
  }

  public Address getHsaSedfInvoiceAddress() {
    return hsaSedfInvoiceAddress;
  }

  public void setHsaSedfInvoiceAddress(Address hsaSedfInvoiceAddress) {
    this.hsaSedfInvoiceAddress = hsaSedfInvoiceAddress;
  }

  public Address getHsaStreetAddress() {
    return hsaStreetAddress;
  }

  public void setHsaStreetAddress(Address hsaStreetAddress) {
    this.hsaStreetAddress = hsaStreetAddress;
  }

  public Address getHsaInternalAddress() {
    return hsaInternalAddress;
  }

  public void setHsaInternalAddress(Address hsaInternalAddress) {
    this.hsaInternalAddress = hsaInternalAddress;
  }

  public Address getHsaPostalAddress() {
    return hsaPostalAddress;
  }

  public void setHsaPostalAddress(Address hsaPostalAddress) {
    this.hsaPostalAddress = hsaPostalAddress;
  }

  public Address getHsaSedfDeliveryAddress() {
    return hsaSedfDeliveryAddress;
  }

  public void setHsaSedfDeliveryAddress(Address hsaSedfDeliveryAddress) {
    this.hsaSedfDeliveryAddress = hsaSedfDeliveryAddress;
  }

  public PhoneNumber getFacsimileTelephoneNumber() {
    return facsimileTelephoneNumber;
  }

  public void setFacsimileTelephoneNumber(PhoneNumber facsimileTelephoneNumber) {
    this.facsimileTelephoneNumber = facsimileTelephoneNumber;
  }

  public ZipCode getZipCode() {
    return zipCode;
  }

  public void setZipCode(ZipCode zipCode) {
    this.zipCode = zipCode;
  }

  public String getLabeledUri() {
    return labeledUri;
  }

  public void setLabeledUri(String labeledUri) {
    this.labeledUri = labeledUri;
  }

  public String getVgrAnstform() {
    return vgrAnstform;
  }

  public void setVgrAnstform(String vgrAnstform) {
    this.vgrAnstform = vgrAnstform;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getVgrFormansgrupp() {
    return vgrFormansgrupp;
  }

  public void setVgrFormansgrupp(String vgrFormansgrupp) {
    this.vgrFormansgrupp = vgrFormansgrupp;
  }

  public PhoneNumber getHsaSedfSwitchboardTelephoneNo() {
    return hsaSedfSwitchboardTelephoneNo;
  }

  public void setHsaSedfSwitchboardTelephoneNo(PhoneNumber hsaSedfSwitchboardTelephoneNo) {
    this.hsaSedfSwitchboardTelephoneNo = hsaSedfSwitchboardTelephoneNo;
  }

  public String getVgrAO3kod() {
    return vgrAO3kod;
  }

  public void setVgrAO3kod(String vgrAO3kod) {
    this.vgrAO3kod = vgrAO3kod;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for the hsaTelephoneNumber property.
   * 
   * @return The first of the hsaTelephoneNumbers or null if the property is not set.
   */
  public PhoneNumber getHsaTelephoneNumber() {
    if (!hsaTelephoneNumber.isEmpty()) {
      return hsaTelephoneNumber.get(0);
    }
    return null;
  }

  /***
   * 
   * @return - CVS String of all phonenumbers
   */
  public String getHsaTelephoneNumbersCSVString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < hsaTelephoneNumber.size(); i++) {
      if (i > 0) {
        sb.append(" , ");
      }
      sb.append(hsaTelephoneNumber.get(i).getFormattedPhoneNumber().getPhoneNumber());
    }
    return sb.toString();
  }

  public List<PhoneNumber> getHsaTelephoneNumbers() {
    return this.hsaTelephoneNumber;
  }

  /**
   * Adds telephone numbers to the employment.
   * 
   * @param hsaTelephoneNumbers The telephone numbers to add.
   */
  public void addHsaTelephoneNumbers(List<PhoneNumber> hsaTelephoneNumbers) {
    if (hsaTelephoneNumbers != null) {
      this.hsaTelephoneNumber.addAll(hsaTelephoneNumbers);
    }
  }

  public PhoneNumber getHsaPublicTelephoneNumber() {
    return hsaPublicTelephoneNumber;
  }

  public void setHsaPublicTelephoneNumber(PhoneNumber hsaPublicTelephoneNumber) {
    this.hsaPublicTelephoneNumber = hsaPublicTelephoneNumber;
  }

  public PhoneNumber getMobileTelephoneNumber() {
    return mobileTelephoneNumber;
  }

  public void setMobileTelephoneNumber(PhoneNumber mobileTelephoneNumber) {
    this.mobileTelephoneNumber = mobileTelephoneNumber;
  }

  public PhoneNumber getHsaInternalPagerNumber() {
    return hsaInternalPagerNumber;
  }

  public void setHsaInternalPagerNumber(PhoneNumber hsaInternalPagerNumber) {
    this.hsaInternalPagerNumber = hsaInternalPagerNumber;
  }

  public PhoneNumber getPagerTelephoneNumber() {
    return pagerTelephoneNumber;
  }

  public void setPagerTelephoneNumber(PhoneNumber pagerTelephoneNumber) {
    this.pagerTelephoneNumber = pagerTelephoneNumber;
  }

  public PhoneNumber getHsaTextPhoneNumber() {
    return hsaTextPhoneNumber;
  }

  public void setHsaTextPhoneNumber(PhoneNumber hsaTextPhoneNumber) {
    this.hsaTextPhoneNumber = hsaTextPhoneNumber;
  }

  public List<WeekdayTime> getHsaTelephoneTime() {
    return hsaTelephoneTime;
  }

  /**
   * Adds new telephone times to the employment.
   * 
   * @param telephoneTimes The telephone times to add.
   */
  public void addHsaTelephoneTime(List<WeekdayTime> telephoneTimes) {
    if (telephoneTimes != null) {
      this.hsaTelephoneTime.addAll(telephoneTimes);
    }
  }

  public List<String> getDescription() {
    return description;
  }

  public void setDescription(List<String> description) {
    this.description = description;
  }

  public TimePoint getModifyTimestamp() {
    return modifyTimestamp;
  }

  public void setModifyTimestamp(TimePoint modifyTimestamp) {
    this.modifyTimestamp = modifyTimestamp;
  }

  public String getModifyersName() {
    return modifyersName;
  }

  public void setModifyersName(String modifyersName) {
    this.modifyersName = modifyersName;
  }

  public TimeInterval getEmploymentPeriod() {
    return employmentPeriod;
  }

  public void setEmploymentPeriod(TimeInterval employmentPeriod) {
    this.employmentPeriod = employmentPeriod;
  }

  /**
   * Setter for the employmentPeriod property.
   * 
   * @param startDate The start date of the employment period.
   * @param stopDate The stop date of the employment period.
   */
  public void setEmploymentPeriod(TimePoint startDate, TimePoint stopDate) {
    setEmploymentPeriod(TimeInterval.over(startDate, stopDate));
  }

  public DN getVgrStrukturPerson() {
    return vgrStrukturPerson;
  }

  public void setVgrStrukturPerson(DN vgrStrukturPerson) {
    this.vgrStrukturPerson = vgrStrukturPerson;
  }

  public String getLocality() {
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getPosition() {
    return position;
  }

  public void setHsaConsigneeAddress(Address hsaConsigneeAddress) {
    this.hsaConsigneeAddress = hsaConsigneeAddress;
  }

  public Address getHsaConsigneeAddress() {
    return hsaConsigneeAddress;
  }

  public void setPrimaryEmployment(boolean primaryEmployment) {
    this.primaryEmployment = primaryEmployment;
  }

  public boolean isPrimaryEmployment() {
    return primaryEmployment;
  }
}
