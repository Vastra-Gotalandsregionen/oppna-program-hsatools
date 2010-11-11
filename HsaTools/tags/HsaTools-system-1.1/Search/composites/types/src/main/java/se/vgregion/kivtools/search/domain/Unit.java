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

import geo.google.datamodel.GeoCoordinate;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.domain.util.Evaluator;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Represents a unit (mottagning).
 * 
 * @author hangy2, Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class Unit implements Serializable, Comparable<Unit> {

  private static final long serialVersionUID = 1L;
  private static final String HSA_BUSINESS_CLASSIFICATION_NAME_UNKNOWN = "Ok\u00E4nd v\u00E5rdtyp";
  private static final Log LOG = LogFactory.getLog(Unit.class);

  // 0u (e.g.Näl)
  private String ou;
  // Distinuished Name (e.g. ou=Näl,ou=Org,o=VGR)
  private DN dn;
  private String manager;
  private String managerDN;
  // Enhetens namn
  private String name;
  // Enhetens kort namn
  private String organizationalUnitNameShort;
  // Var i organisationen enheten
  private String ldapDistinguishedName;
  // e.g. organizationalUnit, organizationalRole
  private String objectClass;
  // true=Unit, false=Function
  private boolean isUnit;
  private final List<String> hsaDestinationIndicator = new ArrayList<String>();
  private String hsaBusinessType;

  // Code tables values
  // Vårdform
  private String careType;
  // Vårdform klartext
  private String careTypeText;
  // Ansvarsområdes kod
  private String vgrAO3kod;
  // Ansvarsområdeskodens namn
  private String vgrAO3kodText;
  private List<String> hsaBusinessClassificationCode;
  private List<String> hsaBusinessClassificationText;
  // Kommunkod
  private String hsaMunicipalityCode;
  // Kommunnamn
  private String hsaMunicipalityName;
  // Länskod
  private String hsaCountyCode;
  // Länsnamn
  private String hsaCountyName;
  // Ägarformkod
  private String hsaManagementCode;
  // Ägarform klartext
  private String hsaManagementName;
  // Drifts- & juridisk formkod
  private String hsaAdministrationForm;
  // Drifts- & juridisk formklartext
  private String hsaAdministrationFormText;

  // Extern beskrivning
  private final List<String> description = new ArrayList<String>();
  // Intern beskrivning
  private final List<String> internalDescription = new ArrayList<String>();
  // E-postadress
  private String mail;
  // Stadsdel
  private String locality;
  // Hemsida
  private String labeledURI;
  // I-nummer
  private String vgrInternalSedfInvoiceAddress;
  // HSA identitet
  private String hsaIdentity;
  private final List<HealthcareType> healthcareTypes = new ArrayList<HealthcareType>();
  // Ansvarsnr
  private List<String> vgrAnsvarsnummer;
  // Arbetsplatskod
  private String hsaUnitPrescriptionCode;

  // Addresses
  // Postadress
  private Address hsaPostalAddress;
  // Internadress
  private Address hsaInternalAddress;
  // Besöksadress
  private Address hsaStreetAddress;
  // Leveransadress
  private Address hsaSedfDeliveryAddress;
  // Fakturaadress
  private Address hsaSedfInvoiceAddress;
  private Address hsaConsigneeAddress;

  // Phone numbers
  // Växeltelefon
  private PhoneNumber hsaSedfSwitchboardTelephoneNo;
  // Personsökare
  private PhoneNumber hsaInternalPagerNumber;
  // Minicall
  private PhoneNumber pagerTelephoneNumber;
  // Texttelefon
  private PhoneNumber hsaTextPhoneNumber;
  // Mobiltelefon
  private PhoneNumber mobileTelephoneNumber;
  // Faxnummer
  private PhoneNumber facsimileTelephoneNumber;
  // Direkttelefon
  private final List<PhoneNumber> hsaTelephoneNumber = new ArrayList<PhoneNumber>();
  // Telefon publik
  private final List<PhoneNumber> hsaPublicTelephoneNumber = new ArrayList<PhoneNumber>();
  // Telefontid
  private final List<WeekdayTime> hsaTelephoneTime = new ArrayList<WeekdayTime>();
  // Giltighetsslutdatum
  private Date hsaEndDate;
  // Kommundelsnamn
  private String hsaMunicipalitySectionName;
  // Kommundelskod
  private String hsaMunicipalitySectionCode;

  private final List<WeekdayTime> hsaSurgeryHours = new ArrayList<WeekdayTime>();
  private final List<WeekdayTime> hsaDropInHours = new ArrayList<WeekdayTime>();
  private List<WeekdayTime> visitingHours;
  private String vgrOrganizationalRole;
  // Detta skall vara businessClass i ldap
  private String hsaManagementText;
  private String hsaVisitingRules;
  private String hsaVisitingRuleAge;
  private String hsaPatientVisitingRules;

  private String vgrTempInfo;
  private String vgrRefInfo;
  private Date vgrTempInfoStart;
  private Date vgrTempInfoEnd;
  private String vgrTempInfoBody;

  // Senast uppdaterad
  private TimePoint modifyTimestamp;
  // Skapad
  private TimePoint createTimestamp;

  // Geo coordinates
  private String hsaGeographicalCoordinates;
  // In decimal degrees
  private double wgs84Lat;
  // In decimal degrees
  private double wgs84Long;
  private int rt90X;
  private int rt90Y;
  // Needed for calculation of close units
  private GeoCoordinate geoCoordinate;
  private String distanceToTarget;
  private final List<String> mvkCaseTypes = new ArrayList<String>();

  private boolean vgrVardVal;

  // Vägbeskrivning
  private final List<String> hsaRoute = new ArrayList<String>();
  private String hsaRouteConcatenated = "";

  private Integer accessibilityDatabaseId;
  private AccessibilityInformation accessibilityInformation;

  private boolean showAgeInterval;
  private boolean showVisitingRules;

  private String internalWebsite;
  private String contractCode;
  private String visitingRuleReferral;

  private String hsaResponsibleHealthCareProvider;
  private final List<String> hsaHealthCareUnitMembers = new ArrayList<String>();

  private List<String> vgrObjectManagers = new ArrayList<String>();

  public boolean isVgrVardVal() {
    return this.vgrVardVal;
  }

  public void setVgrVardVal(boolean vgrVardVal) {
    this.vgrVardVal = vgrVardVal;
  }

  public String getDistanceToTarget() {
    return this.distanceToTarget;
  }

  public void setDistanceToTarget(String distanceToTarget) {
    this.distanceToTarget = distanceToTarget;
  }

  /**
   * Only used by HAK
   * 
   * @return {@link GeoCoordinate}
   */
  public GeoCoordinate getGeoCoordinate() {
    return this.geoCoordinate;
  }

  /**
   * Only used by HAK
   * 
   * @param geoCoordinate
   */
  public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
    this.geoCoordinate = geoCoordinate;
  }

  public String getHsaRouteConcatenated() {
    return this.hsaRouteConcatenated;
  }

  public void setHsaRouteConcatenated(String hsaRouteConcatenated) {
    this.hsaRouteConcatenated = hsaRouteConcatenated;
  }

  public List<WeekdayTime> getHsaSurgeryHours() {
    return this.hsaSurgeryHours;
  }

  /**
   * Adds a new surgery hour to the unit.
   * 
   * @param hours The surgery hour to add.
   */
  public void addHsaSurgeryHours(WeekdayTime hours) {
    this.hsaSurgeryHours.add(hours);
  }

  /**
   * Adds a list of surgery hours to the unit.
   * 
   * @param hours The list of surgery hours to add.
   */
  public void addHsaSurgeryHours(List<WeekdayTime> hours) {
    if (hours != null) {
      this.hsaSurgeryHours.addAll(hours);
    }
  }

  public List<WeekdayTime> getHsaDropInHours() {
    return this.hsaDropInHours;
  }

  /**
   * Adds a new dropin hour to the unit.
   * 
   * @param hours The dropin hour to add.
   */
  public void addHsaDropInHours(WeekdayTime hours) {
    this.hsaDropInHours.add(hours);
  }

  /**
   * Adds a list of dropin hours to the unit.
   * 
   * @param hours The list of dropin hours to add.
   */
  public void addHsaDropInHours(List<WeekdayTime> hours) {
    if (hours != null) {
      this.hsaDropInHours.addAll(hours);
    }
  }

  public String getHsaMunicipalityCode() {
    return this.hsaMunicipalityCode;
  }

  public void setHsaMunicipalityCode(String hsaMunicipalityCode) {
    this.hsaMunicipalityCode = hsaMunicipalityCode;
  }

  public String getHsaMunicipalitySectionName() {
    return this.hsaMunicipalitySectionName;
  }

  public void setHsaMunicipalitySectionName(String hsaMunicipalitySectionName) {
    this.hsaMunicipalitySectionName = hsaMunicipalitySectionName;
  }

  public String getHsaMunicipalitySectionCode() {
    return this.hsaMunicipalitySectionCode;
  }

  public void setHsaMunicipalitySectionCode(String hsaMunicipalitySectionCode) {
    this.hsaMunicipalitySectionCode = hsaMunicipalitySectionCode;
  }

  public String getHsaCountyCode() {
    return this.hsaCountyCode;
  }

  public void setHsaCountyCode(String hsaCountyCode) {
    this.hsaCountyCode = hsaCountyCode;
  }

  public String getHsaCountyName() {
    return this.hsaCountyName;
  }

  public void setHsaCountyName(String hsaCountyName) {
    this.hsaCountyName = hsaCountyName;
  }

  public String getHsaManagementCode() {
    return this.hsaManagementCode;
  }

  public void setHsaManagementCode(String hsaManagementCode) {
    this.hsaManagementCode = hsaManagementCode;
  }

  public String getHsaManagementName() {
    return this.hsaManagementName;
  }

  public void setHsaManagementName(String hsaManagementName) {
    this.hsaManagementName = hsaManagementName;
  }

  public String getHsaAdministrationForm() {
    return this.hsaAdministrationForm;
  }

  public void setHsaAdministrationForm(String hsaAdministrationForm) {
    this.hsaAdministrationForm = hsaAdministrationForm;
  }

  public String getHsaAdministrationFormText() {
    return this.hsaAdministrationFormText;
  }

  public void setHsaAdministrationFormText(String hsaAdministrationFormText) {
    this.hsaAdministrationFormText = hsaAdministrationFormText;
  }

  public DN getDn() {
    return this.dn;
  }

  public void setDn(DN dn) {
    this.dn = dn;
  }

  /**
   * Gets the DN as a Base64-encoded string.
   * 
   * @return The units DN as a Base64-encoded string.
   */
  public String getDnBase64() {
    return StringUtil.base64Encode(this.dn.toString());
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrganizationalUnitNameShort() {
    return this.organizationalUnitNameShort;
  }

  public void setOrganizationalUnitNameShort(String organizationalUnitNameShort) {
    this.organizationalUnitNameShort = organizationalUnitNameShort;
  }

  public String getLdapDistinguishedName() {
    return this.ldapDistinguishedName;
  }

  public void setLdapDistinguishedName(String ldapDistinguishedName) {
    this.ldapDistinguishedName = ldapDistinguishedName;
  }

  public String getMail() {
    return this.mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getLabeledURI() {
    return this.labeledURI;
  }

  /**
   * Setter for the labeledURI property.
   * 
   * @param labeledURI The new value of the labeledURI property.
   */
  public void setLabeledURI(String labeledURI) {
    // Do some simple validation/fixing
    if (!"".equals(labeledURI) && !(labeledURI.startsWith("http://") || labeledURI.startsWith("https://"))) {
      this.labeledURI = "http://" + labeledURI;
    } else {
      this.labeledURI = labeledURI;
    }
  }

  public String getLocality() {
    return this.locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public String getVgrInternalSedfInvoiceAddress() {
    return this.vgrInternalSedfInvoiceAddress;
  }

  public void setVgrInternalSedfInvoiceAddress(String vgrInternalSedfInvoiceAddress) {
    this.vgrInternalSedfInvoiceAddress = vgrInternalSedfInvoiceAddress;
  }

  public String getCareType() {
    return this.careType;
  }

  public void setCareType(String careType) {
    this.careType = careType;
  }

  public String getCareTypeText() {
    return this.careTypeText;
  }

  public void setCareTypeText(String careTypeText) {
    this.careTypeText = careTypeText;
  }

  public String getVgrAO3kod() {
    return this.vgrAO3kod;
  }

  public void setVgrAO3kod(String vgrAO3kod) {
    this.vgrAO3kod = vgrAO3kod;
  }

  public String getVgrAO3kodText() {
    return this.vgrAO3kodText;
  }

  public void setVgrAO3kodText(String vgrAO3kodText) {
    this.vgrAO3kodText = vgrAO3kodText;
  }

  public String getHsaIdentity() {
    return this.hsaIdentity;
  }

  public void setHsaIdentity(String hsaIdentity) {
    this.hsaIdentity = hsaIdentity;
  }

  public PhoneNumber getHsaSedfSwitchboardTelephoneNo() {
    return this.hsaSedfSwitchboardTelephoneNo;
  }

  public void setHsaSedfSwitchboardTelephoneNo(PhoneNumber hsaSedfSwitchboardTelephoneNo) {
    this.hsaSedfSwitchboardTelephoneNo = hsaSedfSwitchboardTelephoneNo;
  }

  public PhoneNumber getHsaInternalPagerNumber() {
    return this.hsaInternalPagerNumber;
  }

  public void setHsaInternalPagerNumber(PhoneNumber hsaInternalPagerNumber) {
    this.hsaInternalPagerNumber = hsaInternalPagerNumber;
  }

  public PhoneNumber getPagerTelephoneNumber() {
    return this.pagerTelephoneNumber;
  }

  public void setPagerTelephoneNumber(PhoneNumber pagerTelephoneNumber) {
    this.pagerTelephoneNumber = pagerTelephoneNumber;
  }

  public PhoneNumber getHsaTextPhoneNumber() {
    return this.hsaTextPhoneNumber;
  }

  public void setHsaTextPhoneNumber(PhoneNumber hsaTextPhoneNumber) {
    this.hsaTextPhoneNumber = hsaTextPhoneNumber;
  }

  public PhoneNumber getMobileTelephoneNumber() {
    return this.mobileTelephoneNumber;
  }

  public void setMobileTelephoneNumber(PhoneNumber mobileTelephoneNumber) {
    this.mobileTelephoneNumber = mobileTelephoneNumber;
  }

  public PhoneNumber getFacsimileTelephoneNumber() {
    return this.facsimileTelephoneNumber;
  }

  public void setFacsimileTelephoneNumber(PhoneNumber facsimileTelephoneNumber) {
    this.facsimileTelephoneNumber = facsimileTelephoneNumber;
  }

  public String getHsaUnitPrescriptionCode() {
    return this.hsaUnitPrescriptionCode;
  }

  public void setHsaUnitPrescriptionCode(String hsaUnitPrescriptionCode) {
    this.hsaUnitPrescriptionCode = hsaUnitPrescriptionCode;
  }

  public String getHsaMunicipalityName() {
    return this.hsaMunicipalityName;
  }

  public void setHsaMunicipalityName(String hsaMunicipalityName) {
    this.hsaMunicipalityName = hsaMunicipalityName;
  }

  public String getOu() {
    return this.ou;
  }

  public void setOu(String ou) {
    this.ou = ou;
  }

  public List<String> getDescription() {
    return this.description;
  }

  /**
   * Adds a list of external descriptions to the unit.
   * 
   * @param descriptions the external descriptions to add.
   */
  public void addDescription(List<String> descriptions) {
    if (descriptions != null) {
      this.description.addAll(descriptions);
    }
  }

  public List<String> getInternalDescription() {
    return this.internalDescription;
  }

  /**
   * Adds a list of internal descriptions to the unit.
   * 
   * @param descriptions the internal descriptions to add.
   */
  public void addInternalDescription(List<String> descriptions) {
    if (descriptions != null) {
      this.internalDescription.addAll(descriptions);
    }
  }

  public List<PhoneNumber> getHsaTelephoneNumber() {
    return this.hsaTelephoneNumber;
  }

  /**
   * Adds a list of telephone numbers to the unit.
   * 
   * @param hsaTelephoneNumbers The list of telephone numbers to add
   */
  public void addHsaTelephoneNumber(List<PhoneNumber> hsaTelephoneNumbers) {
    if (hsaTelephoneNumbers != null) {
      this.hsaTelephoneNumber.addAll(hsaTelephoneNumbers);
    }
  }

  public List<PhoneNumber> getHsaPublicTelephoneNumber() {
    return this.hsaPublicTelephoneNumber;
  }

  /**
   * Adds a hsaPublicTelephoneNumber to the unit.
   * 
   * @param telephoneNumber the hsaPublicTelephoneNumber to add.
   */
  public void addHsaPublicTelephoneNumber(PhoneNumber telephoneNumber) {
    this.hsaPublicTelephoneNumber.add(telephoneNumber);
  }

  public List<WeekdayTime> getHsaTelephoneTime() {
    return this.hsaTelephoneTime;
  }

  /**
   * Adds a new telephone time to the unit.
   * 
   * @param telephoneTime The telephone time to add.
   */
  public void addHsaTelephoneTime(WeekdayTime telephoneTime) {
    this.hsaTelephoneTime.add(telephoneTime);
  }

  /**
   * Adds a list of telephone times to the unit.
   * 
   * @param telephoneTimes The list of telephone times to add.
   */
  public void addHsaTelephoneTimes(List<WeekdayTime> telephoneTimes) {
    if (telephoneTimes != null) {
      this.hsaTelephoneTime.addAll(telephoneTimes);
    }
  }

  public Address getHsaInternalAddress() {
    return this.hsaInternalAddress;
  }

  public void setHsaInternalAddress(Address hsaInternalAddress) {
    this.hsaInternalAddress = hsaInternalAddress;
  }

  public Address getHsaStreetAddress() {
    return this.hsaStreetAddress;
  }

  public void setHsaStreetAddress(Address hsaStreetAddress) {
    this.hsaStreetAddress = hsaStreetAddress;
  }

  public Address getHsaPostalAddress() {
    return this.hsaPostalAddress;
  }

  public void setHsaPostalAddress(Address hsaPostalAddress) {
    this.hsaPostalAddress = hsaPostalAddress;
  }

  public Address getHsaSedfDeliveryAddress() {
    return this.hsaSedfDeliveryAddress;
  }

  public void setHsaSedfDeliveryAddress(Address hsaSedfDeliveryAddress) {
    this.hsaSedfDeliveryAddress = hsaSedfDeliveryAddress;
  }

  public Address getHsaSedfInvoiceAddress() {
    return this.hsaSedfInvoiceAddress;
  }

  public void setHsaSedfInvoiceAddress(Address hsaSedfInvoiceAddress) {
    this.hsaSedfInvoiceAddress = hsaSedfInvoiceAddress;
  }

  public List<String> getVgrAnsvarsnummer() {
    return this.vgrAnsvarsnummer;
  }

  public void setVgrAnsvarsnummer(List<String> vgrAnsvarsnummer) {
    this.vgrAnsvarsnummer = vgrAnsvarsnummer;
  }

  public String getVgrOrganizationalRole() {
    return this.vgrOrganizationalRole;
  }

  public void setVgrOrganizationalRole(String vgrOrganizationalRole) {
    this.vgrOrganizationalRole = vgrOrganizationalRole;
  }

  public String getHsaManagementText() {
    return this.hsaManagementText;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.hsaIdentity == null) {
      result = prime * result;
    } else {
      result = prime * result + this.hsaIdentity.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    boolean result = true;
    if (this != obj) {
      if (obj == null) {
        result = false;
      } else {
        if (this.getClass() != obj.getClass()) {
          result = false;
        } else {
          Unit other = (Unit) obj;
          if (this.hsaIdentity == null) {
            if (other.hsaIdentity != null) {
              result = false;
            }
          } else if (!this.hsaIdentity.equals(other.hsaIdentity)) {
            result = false;
          }
        }
      }
    }
    return result;
  }

  public void setHsaManagementText(String hsaManagementText) {
    this.hsaManagementText = hsaManagementText;
  }

  public String getHsaVisitingRuleAge() {
    return this.hsaVisitingRuleAge;
  }

  /**
   * Setter for the hsaVisitingRuleAge property. Converts the value from the LDAP-value to a more human readable form.
   * 
   * @param hsaVisitingRuleAge The LDAP-value for the hsaVisitingRuleAge property.
   */
  public void setHsaVisitingRuleAge(String hsaVisitingRuleAge) {
    if ("0-99".equals(hsaVisitingRuleAge) || "00-99".equals(hsaVisitingRuleAge)) {
      this.hsaVisitingRuleAge = "Alla \u00E5ldrar";
    } else if (hsaVisitingRuleAge.endsWith("-99")) {
      this.hsaVisitingRuleAge = hsaVisitingRuleAge.substring(0, hsaVisitingRuleAge.indexOf("-")) + " \u00E5r eller \u00E4ldre";
    } else if (!"".equals(hsaVisitingRuleAge)) {
      this.hsaVisitingRuleAge = hsaVisitingRuleAge + " \u00E5r";
    } else {
      this.hsaVisitingRuleAge = hsaVisitingRuleAge;
    }
  }

  public String getHsaVisitingRules() {
    return this.hsaVisitingRules;
  }

  public void setHsaVisitingRules(String hsaVisitingRules) {
    this.hsaVisitingRules = hsaVisitingRules;
  }

  public String getVgrTempInfo() {
    return this.vgrTempInfo;
  }

  /**
   * Setter for the vgrTempInfo property. The provided value is parsed and the result populates the fields vgrTempInfoStart, vgrTempInfoEnd and vgrTempInfoBody as well.
   * 
   * @param vgrTempInfo The new value for vgrTempInfo.
   */
  public void setVgrTempInfo(String vgrTempInfo) {
    this.vgrTempInfo = vgrTempInfo;
    if ("".equals(vgrTempInfo) || !vgrTempInfo.contains("-") || vgrTempInfo.length() < 19) {
      return;
    }
    /*
     * Split to vgrTempInfoStart, vgrTempInfoEnd and vgrTempInfoBody vgrTempInfo format: YYYYMMDD-YYYYMMDD Message
     */

    // Start date
    String[] temp = vgrTempInfo.split("-");
    if (temp.length > 1) {
      String startDateString = temp[0];
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
      try {
        Date startDate = df.parse(startDateString);
        Calendar cal = new GregorianCalendar();
        cal.setTime(startDate);
        // Start showing one week
        cal.add(Calendar.DAY_OF_YEAR, -7);
        // earlier
        this.setVgrTempInfoStart(cal.getTime());
      } catch (ParseException e) {
        // KIV validates this field. Nothing we can do if it is incorrect.
        LOG.error("Unable to parse provided startdate as a date", e);
      }
    } else {
      return;
    }

    // End date
    temp = temp[1].split(" ");
    if (temp.length > 1) {
      String endDateString = temp[0];
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
      try {
        Date endDate = df.parse(endDateString);
        Calendar cal = new GregorianCalendar();
        cal.setTime(endDate);
        // Stop showing the day after
        cal.add(Calendar.DAY_OF_YEAR, 1);
        this.setVgrTempInfoEnd(cal.getTime());
      } catch (ParseException e) {
        // KIV validates this field. Nothing we can do if it is incorrect.
        LOG.error("Unable to parse provided enddate as a date", e);
      }
      // Message
      this.setVgrTempInfoBody(vgrTempInfo.substring(vgrTempInfo.indexOf(" ") + 1));
    }
  }

  public Date getVgrTempInfoStart() {
    return this.vgrTempInfoStart;
  }

  public void setVgrTempInfoStart(Date vgrTempInfoStart) {
    this.vgrTempInfoStart = vgrTempInfoStart;
  }

  public Date getVgrTempInfoEnd() {
    return this.vgrTempInfoEnd;
  }

  public void setVgrTempInfoEnd(Date vgrTempInfoEnd) {
    this.vgrTempInfoEnd = vgrTempInfoEnd;
  }

  public String getVgrTempInfoBody() {
    return this.vgrTempInfoBody;
  }

  public void setVgrTempInfoBody(String vgrTempInfoBody) {
    this.vgrTempInfoBody = vgrTempInfoBody;
  }

  /**
   * Checks if the temporary info should be shown depending on the temporary info start and end dates.
   * 
   * @return True if now is between temporary info start and end date.
   */
  public boolean getShouldVgrTempInfoBeShown() {
    boolean show = false;
    Date now = TimeUtil.asDate();

    if (this.vgrTempInfoStart != null && this.vgrTempInfoEnd != null) {
      show = now.after(this.vgrTempInfoStart) && now.before(this.vgrTempInfoEnd);
    }

    return show;
  }

  public String getVgrRefInfo() {
    return this.vgrRefInfo;
  }

  public void setVgrRefInfo(String vgrRefInfo) {
    this.vgrRefInfo = vgrRefInfo;
  }

  public String getConcatenatedDescription() {
    return this.getConcatenatedDescription(false);
  }

  public String getInternalConcatenatedDescription() {
    return this.getConcatenatedDescription(true);
  }

  /***
   * 
   * @param useInteralDescription - if true internal description is returned concatenated
   * @return - String of concatenated internal or external description
   */
  private String getConcatenatedDescription(boolean useInteralDescription) {
    String concatenatedDescription = "";
    if (useInteralDescription && this.internalDescription != null && this.internalDescription.size() > 0) {
      for (String s : this.internalDescription) {
        concatenatedDescription += s + "";
      }
    } else if (!useInteralDescription && this.description != null && this.description.size() > 0) {
      for (String s : this.description) {
        concatenatedDescription += s + "";
      }
    }
    return concatenatedDescription;
  }

  /**
   * Gets the units healthcare types as a comma separated string.
   * 
   * @return The units healthcare types as a comma separated string.
   */
  public String getCaretypeCustomized() {
    if (this.healthcareTypes != null) {
      String healthcareTypeString = "";
      for (HealthcareType htc : this.healthcareTypes) {
        healthcareTypeString += htc.getDisplayName() + ", ";
      }
      if (!"".equals(healthcareTypeString)) {
        return healthcareTypeString.substring(0, healthcareTypeString.length() - 2);
      }
    }
    return HSA_BUSINESS_CLASSIFICATION_NAME_UNKNOWN;
  }

  // VALIDATION
  // ***************

  /**
   * Checks if the units name is valid.
   * 
   * @return True if the units name is not empty, otherwise false.
   */
  public boolean getNameIsValid() {
    if (StringUtil.isEmpty(this.getName())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units municipality name is valid.
   * 
   * @return True if the units municipality name is not empty, otherwise false.
   */
  public boolean getHsaMunicapilatyNameIsValid() {
    if (StringUtil.isEmpty(this.getHsaMunicipalityName())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units street address is valid.
   * 
   * @return True if the units street address is set and is not empty, otherwise false.
   */
  public boolean getHsaStreetAddressIsValid() {
    if (this.getHsaStreetAddress() == null || this.getHsaStreetAddress().isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units surgery hours is valid.
   * 
   * @return True if the units surgery hours is not empty, otherwise false.
   */
  public boolean getHsaSurgeryHoursIsValid() {
    if (Evaluator.isEmptyWeekDayTime(this.getHsaSurgeryHours())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units drop in hours is valid.
   * 
   * @return True if the units drop in hours is not empty, otherwise false.
   */
  public boolean getHsaDropInHoursIsValid() {
    if (Evaluator.isEmptyWeekDayTime(this.getHsaDropInHours())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units public telephone number is valid.
   * 
   * @return True if the units public telephone number is not empty, otherwise false.
   */
  public boolean getHsaPublicTelephoneNumberIsValid() {
    if (Evaluator.isEmptyPhoneNumber(this.getHsaPublicTelephoneNumber())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units telephone time is valid.
   * 
   * @return True if the units telephone time is not empty, otherwise false.
   */
  public boolean getHsaTelephoneTimeIsValid() {
    if (Evaluator.isEmptyWeekDayTime(this.getHsaTelephoneTime())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units geographical coordinates is valid.
   * 
   * @return True if the units geographical coordinates is not empty, otherwise false.
   */
  public boolean getHsaGeographicalCoordinatesIsValid() {
    if (StringUtil.isEmpty(this.getHsaGeographicalCoordinates())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units labeled URI is valid.
   * 
   * @return True if the units labeled URI is not empty, otherwise false.
   */
  public boolean getLabeledURIIsValid() {
    if (StringUtil.isEmpty(this.getLabeledURI()) || !(this.getLabeledURI().startsWith("http://") || this.getLabeledURI().startsWith("https://"))) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units description is valid.
   * 
   * @return True if the units description is not empty, otherwise false.
   */
  public boolean getDescriptionIsValid() {
    if (Evaluator.isEmpty(this.getDescription())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units organizational role is valid.
   * 
   * @return True if the units organizational role is not empty, otherwise false.
   */
  public boolean getVgrOrganizationalRoleIsValid() {
    if (StringUtil.isEmpty(this.getVgrOrganizationalRole())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units business classification name is valid.
   * 
   * @return True if the units business classification name is not empty, otherwise false.
   */
  public boolean getHsaBusinessClassificationNameIsValid() {
    if (Evaluator.isEmptyBusinessClassification(this.healthcareTypes)) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units visiting rule age is valid.
   * 
   * @return True if the units visiting rule age is not empty, otherwise false.
   */
  public boolean getHsaVisitingRuleAgeIsValid() {
    if (StringUtil.isEmpty(this.getHsaVisitingRuleAge())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units visiting rules are valid.
   * 
   * @return True if the units visiting rules are not empty, otherwise false.
   */
  public boolean getHsaVisitingRulesIsValid() {
    if (StringUtil.isEmpty(this.getHsaVisitingRules())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units care type is valid.
   * 
   * @return True if the units care type is not empty, otherwise false.
   */
  public boolean getCareTypeIsValid() {
    if (StringUtil.isEmpty(this.getCareType())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units management text is valid.
   * 
   * @return True if the units management text is not empty, otherwise false.
   */
  public boolean getHsaManagementTextIsValid() {
    if (StringUtil.isEmpty(this.getHsaManagementText())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the content of the unit is valid.
   * 
   * @return True if the content is valid, otherwise false.
   */
  public boolean getContentValidationOk() {
    boolean valid = true;

    valid &= this.getNameIsValid();
    valid &= this.getHsaMunicapilatyNameIsValid();
    valid &= this.getHsaStreetAddressIsValid();
    valid &= this.getHsaRouteIsValid();
    valid &= this.getHsaGeographicalCoordinatesIsValid();
    valid &= this.getHsaSurgeryHoursIsValid();
    valid &= this.getHsaDropInHoursIsValid();
    valid &= this.getHsaPublicTelephoneNumberIsValid();
    valid &= this.getHsaTelephoneTimeIsValid();
    valid &= this.getLabeledURIIsValid();
    valid &= this.getDescriptionIsValid();
    valid &= this.getHsaVisitingRuleAgeIsValid();
    valid &= this.getHsaVisitingRulesIsValid();
    valid &= this.getHsaManagementTextIsValid();

    return valid;
  }

  private boolean getHsaRouteIsValid() {
    return !Evaluator.isEmpty(this.getHsaRoute());
  }

  public void setModifyTimestamp(TimePoint modifyTimestamp) {
    this.modifyTimestamp = modifyTimestamp;

  }

  public TimePoint getModifyTimestamp() {
    return this.modifyTimestamp;
  }

  /**
   * Gets the units modify timestamp as a W3C-formatted string.
   * 
   * @return The units modify timestamp as a W3C-formatted string (yyyy-MM-dd'T'HH:mm:ssZ).
   */
  public String getModifyTimestampFormattedInW3CDatetimeFormat() {
    if (this.modifyTimestamp != null) {
      return TimeUtil.formatDateW3C(this.modifyTimestamp.asJavaUtilDate());
    } else {
      return "";
    }
  }

  public TimePoint getCreateTimestamp() {
    return this.createTimestamp;
  }

  public void setCreateTimestamp(TimePoint createTimestamp) {
    this.createTimestamp = createTimestamp;
  }

  /**
   * Gets the units create timestamp as a W3C-formatted string.
   * 
   * @return The units create timestamp as a W3C-formatted string (yyyy-MM-dd'T'HH:mm:ssZ).
   */
  public String getCreateTimestampFormattedInW3CDatetimeFormat() {
    if (this.createTimestamp != null) {
      return TimeUtil.formatDateW3C(this.createTimestamp.asJavaUtilDate());
    } else {
      return "";
    }
  }

  public String getHsaGeographicalCoordinates() {
    return this.hsaGeographicalCoordinates;
  }

  public void setHsaGeographicalCoordinates(String hsaGeographicalCoordinates) {
    this.hsaGeographicalCoordinates = hsaGeographicalCoordinates;
  }

  public double getWgs84Lat() {
    return this.wgs84Lat;
  }

  public double getWgs84LatRounded() {
    return (double) Math.round(this.wgs84Lat * 10000) / 10000;
  }

  public void setWgs84Lat(double wgs84Lat) {
    this.wgs84Lat = wgs84Lat;
  }

  public double getWgs84Long() {
    return this.wgs84Long;
  }

  public double getWgs84LongRounded() {
    return (double) Math.round(this.wgs84Long * 10000) / 10000;

  }

  public void setWgs84Long(double wgs84Long) {
    this.wgs84Long = wgs84Long;
  }

  public int getRt90X() {
    return this.rt90X;
  }

  public void setRt90X(int rt90X) {
    this.rt90X = rt90X;
  }

  public int getRt90Y() {
    return this.rt90Y;
  }

  public void setRt90Y(int rt90Y) {
    this.rt90Y = rt90Y;
  }

  public Integer getAccessibilityDatabaseId() {
    return this.accessibilityDatabaseId;
  }

  public void setAccessibilityDatabaseId(int accessibilityDatabaseId) {
    this.accessibilityDatabaseId = accessibilityDatabaseId;
  }

  public String getObjectClass() {
    return this.objectClass;
  }

  public void setObjectClass(String objectClass) {
    this.objectClass = objectClass;
  }

  public boolean getIsUnit() {
    return this.isUnit;
  }

  public void setIsUnit(boolean isUnit) {
    this.isUnit = isUnit;
  }

  public List<String> getHsaRoute() {
    return this.hsaRoute;
  }

  /**
   * Setter for the hsaRoute property. Also sets the hsaRouteConcatenated property.
   * 
   * @param hsaRoutelist The list of strings the hsaRoute consists of.
   */
  public void addHsaRoute(List<String> hsaRoutelist) {
    if (hsaRoutelist != null && hsaRoutelist.size() > 0) {
      for (String s : hsaRoutelist) {
        this.hsaRoute.add(s);
        this.hsaRouteConcatenated += s + "";
      }
    }
  }

  public List<HealthcareType> getHealthcareTypes() {
    return this.healthcareTypes;
  }

  /**
   * Adds a new healthcare type to the unit.
   * 
   * @param healthcareType The healthcare type to add.
   */
  public void addHealthcareType(HealthcareType healthcareType) {
    this.healthcareTypes.add(healthcareType);
  }

  /**
   * Adds a list of healthcare types to the unit.
   * 
   * @param addedHealthcareTypes The list of healthcare types to add.
   */
  public void addHealthcareTypes(List<HealthcareType> addedHealthcareTypes) {
    if (addedHealthcareTypes != null) {
      this.healthcareTypes.addAll(addedHealthcareTypes);
    }
  }

  /**
   * Returns comma separated list of assigned health care types.
   * 
   * @return A list of HealthcareType objects where all but the last one have a comma appended to it's display name.
   */
  public List<HealthcareType> getHealthcareTypesCustomized() {
    List<HealthcareType> healthcareTypesCustomized = new ArrayList<HealthcareType>();
    for (HealthcareType ht : this.healthcareTypes) {
      HealthcareType htNew = new HealthcareType(ht.getConditions(), ht.getDisplayName(), ht.isFiltered(), ht.getIndex());
      healthcareTypesCustomized.add(htNew);
    }
    for (int i = 0; i < healthcareTypesCustomized.size() - 1; i++) {
      healthcareTypesCustomized.get(i).setDisplayName(healthcareTypesCustomized.get(i).getDisplayName() + ", ");
    }
    return healthcareTypesCustomized;
  }

  public List<String> getHsaBusinessClassificationCode() {
    return this.hsaBusinessClassificationCode;
  }

  /**
   * Overloaded to support LTH.
   * 
   * @return business classification code
   */
  public List<String> getBusinessClassificationCode() {
    return this.hsaBusinessClassificationCode;
  }

  public void setHsaBusinessClassificationCode(List<String> hsaBusinessClassificationCode) {
    this.hsaBusinessClassificationCode = hsaBusinessClassificationCode;
  }

  public List<String> getHsaBusinessClassificationText() {
    return this.hsaBusinessClassificationText;
  }

  public String getHsaBusinessClassificationTextFormatted() {
    return StringUtil.concatenate(this.hsaBusinessClassificationText);
  }

  public void setHsaBusinessClassificationText(List<String> hsaBusinessClassificationText) {
    this.hsaBusinessClassificationText = hsaBusinessClassificationText;
  }

  /**
   * Adds a MVK casetype to the unit.
   * 
   * @param caseType The casetype to add.
   */
  public void addMvkCaseType(String caseType) {
    this.mvkCaseTypes.add(caseType);
  }

  public List<String> getMvkCaseTypes() {
    return this.mvkCaseTypes;
  }

  /**
   * Gets the units ancestor as a nicely formatted string.
   * 
   * @return the units ancestor as a nicely formatted string or an empty string if hsaIdentity does not contain the letter F or if the units is a hospital.
   */
  public String getFormattedAncestor() {
    String formattedAncestor = "";
    // Should be safe to use this condition as that is specified by HSA standard.
    if (this.hsaIdentity.indexOf("F") > 0) {
      // Hospitals should not be included
      HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
      if (!this.getHealthcareTypes().contains(healthcareTypeConditionHelper.getHealthcareTypeByName("Sjukhus"))) {
        formattedAncestor = ", tillhör " + this.dn.getAncestor(1).getUnitName();
      }
    }

    return formattedAncestor;
  }

  @Override
  public int compareTo(Unit o) {
    return this.hsaIdentity.compareTo(o.getHsaIdentity());
  }

  public AccessibilityInformation getAccessibilityInformation() {
    return this.accessibilityInformation;
  }

  public void setAccessibilityInformation(AccessibilityInformation accessibilityInformation) {
    this.accessibilityInformation = accessibilityInformation;
  }

  public Date getHsaEndDate() {
    return this.hsaEndDate;
  }

  public void setHsaEndDate(Date hsaEndDate) {
    this.hsaEndDate = hsaEndDate;
  }

  /**
   * Checks if information regarding VGR Vardval should be shown for the unit.
   * 
   * @return True if information should be shown, otherwise false.
   */
  public boolean isShowInVgrVardVal() {
    return this.isVgrVardVal() && this.hasHealthcareType("Vårdcentral");
  }

  /**
   * Helper method for checking if a unit has a specific healthcare type.
   * 
   * @param healthcareType The specific healthcare type to look for.
   * @return True if the unit has the provided healthcare type, otherwise false.
   */
  public boolean hasHealthcareType(String healthcareType) {
    boolean result = false;

    if (healthcareType != null && this.getHealthcareTypes() != null) {
      for (HealthcareType ht : this.getHealthcareTypes()) {
        if (healthcareType.equals(ht.getDisplayName())) {
          result = true;
        }
      }
    }
    return result;
  }

  public boolean isShowAgeInterval() {
    return this.showAgeInterval;
  }

  public void setShowAgeInterval(boolean showAgeInterval) {
    this.showAgeInterval = showAgeInterval;
  }

  public boolean isShowVisitingRules() {
    return this.showVisitingRules;
  }

  public void setShowVisitingRules(boolean showVisitingRules) {
    this.showVisitingRules = showVisitingRules;
  }

  public String getInternalWebsite() {
    return this.internalWebsite;
  }

  public void setInternalWebsite(String internalWebsite) {
    this.internalWebsite = internalWebsite;
  }

  public String getContractCode() {
    return this.contractCode;
  }

  public void setContractCode(String contractCode) {
    this.contractCode = contractCode;
  }

  public List<WeekdayTime> getVisitingHours() {
    return this.visitingHours;
  }

  public void setVisitingHours(List<WeekdayTime> visitingHours) {
    this.visitingHours = visitingHours;
  }

  public String getVisitingRuleReferral() {
    return this.visitingRuleReferral;
  }

  public void setVisitingRuleReferral(String visitingRuleReferral) {
    this.visitingRuleReferral = visitingRuleReferral;
  }

  public void setManager(String manager) {
    this.manager = manager;
  }

  public String getManager() {
    return this.manager;
  }

  public void setManagerDN(String managerDN) {
    this.managerDN = managerDN;
  }

  public String getManagerDN() {
    return this.managerDN;
  }

  public void setHsaConsigneeAddress(Address hsaConsigneeAddress) {
    this.hsaConsigneeAddress = hsaConsigneeAddress;
  }

  public Address getHsaConsigneeAddress() {
    return this.hsaConsigneeAddress;
  }

  /**
   * Adds a new indicator to the list of destination indicators.
   * 
   * @param indicator The indicator to add.
   */
  public void addHsaDestinationIndicator(String indicator) {
    this.getHsaDestinationIndicator().add(indicator);
  }

  public List<String> getHsaDestinationIndicator() {
    return this.hsaDestinationIndicator;
  }

  public boolean isForPublicDisplay() {
    return this.getHsaDestinationIndicator().contains("03");
  }

  public void setHsaBusinessType(String hsaBusinessType) {
    this.hsaBusinessType = hsaBusinessType;
  }

  public String getHsaBusinessType() {
    return this.hsaBusinessType;
  }

  public void setHsaPatientVisitingRules(String hsaPatientVisitingRules) {
    this.hsaPatientVisitingRules = hsaPatientVisitingRules;
  }

  public String getHsaPatientVisitingRules() {
    return this.hsaPatientVisitingRules;
  }

  public void setVgrObjectManagers(List<String> vgrObjectManagers) {
    this.vgrObjectManagers = vgrObjectManagers;
  }

  public List<String> getVgrObjectManagers() {
    return this.vgrObjectManagers;
  }

  public void setHsaResponsibleHealthCareProvider(String hsaResponsibleHealthCareProvider) {
    this.hsaResponsibleHealthCareProvider = hsaResponsibleHealthCareProvider;
  }

  public String getHsaResponsibleHealthCareProvider() {
    return hsaResponsibleHealthCareProvider;
  }

  public List<String> getHsaHealthCareUnitMembers() {
    return hsaHealthCareUnitMembers;
  }

  public void addHsaHealthCareUnitMembers(List<String> hsaHealthCareUnitMembers) {
    if (hsaHealthCareUnitMembers != null) {
      this.hsaHealthCareUnitMembers.addAll(hsaHealthCareUnitMembers);
    }
  }
}
