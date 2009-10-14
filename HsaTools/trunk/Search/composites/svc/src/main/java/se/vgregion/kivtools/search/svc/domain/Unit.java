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
package se.vgregion.kivtools.search.svc.domain;

import geo.google.datamodel.GeoCoordinate;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.util.Evaluator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;

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

  // Code tables values
  // Vårdform
  private String vgrCareType;
  // Vårdform klartext
  private String vgrCareTypeText;
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
  private List<String> description;
  // Intern beskrivning
  private List<String> internalDescription;
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
  private List<HealthcareType> healthcareTypes;
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
  // SMS
  private PhoneNumber hsaSmsTelephoneNumber;
  // Faxnummer
  private PhoneNumber facsimileTelephoneNumber;
  // Direkttelefon
  private List<PhoneNumber> hsaTelephoneNumber;
  // Telefon publik
  private List<PhoneNumber> hsaPublicTelephoneNumber;
  // Telefontid
  private List<WeekdayTime> hsaTelephoneTime;
  // Giltighetsslutdatum
  private Date hsaEndDate;

  // EDI-kod
  private String vgrEDICode;
  // EAN-kod
  private String vgrEANCode;
  // Kommundelsnamn
  private String hsaMunicipalitySectionName;
  // Kommundelskod
  private String hsaMunicipalitySectionCode;

  private List<WeekdayTime> hsaSurgeryHours;
  private List<WeekdayTime> hsaDropInHours;
  private List<WeekdayTime> visitingHours;
  private String vgrOrganizationalRole;
  // Detta skall vara businessClass i ldap
  private String hsaManagementText;
  private String hsaVisitingRules;
  private String hsaVisitingRuleAge;

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
  // Needed for calculation of close
  private GeoCoordinate geoCoordinate;
  // units
  private String distanceToTarget;
  private List<String> mvkCaseTypes;

  private boolean vgrVardVal;

  // Vägbeskrivning
  private List<String> hsaRoute;
  private String hsaRouteConcatenated = "";

  private Integer accessibilityDatabaseId;
  private AccessibilityInformation accessibilityInformation;

  private boolean showAgeInterval;
  private boolean showVisitingRules;

  private String internalWebsite;
  private String contractCode;
  private String visitingRuleReferral;

  public boolean isVgrVardVal() {
    return vgrVardVal;
  }

  public void setVgrVardVal(boolean vgrVardVal) {
    this.vgrVardVal = vgrVardVal;
  }

  public String getDistanceToTarget() {
    return distanceToTarget;
  }

  public void setDistanceToTarget(String distanceToTarget) {
    this.distanceToTarget = distanceToTarget;
  }

  public GeoCoordinate getGeoCoordinate() {
    return geoCoordinate;
  }

  public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
    this.geoCoordinate = geoCoordinate;
  }

  public String getHsaRouteConcatenated() {
    return hsaRouteConcatenated;
  }

  public void setHsaRouteConcatenated(String hsaRouteConcatenated) {
    this.hsaRouteConcatenated = hsaRouteConcatenated;
  }

  public List<WeekdayTime> getHsaSurgeryHours() {
    return hsaSurgeryHours;
  }

  public void setHsaSurgeryHours(List<WeekdayTime> hsaSurgeryHours) {
    this.hsaSurgeryHours = hsaSurgeryHours;
  }

  public List<WeekdayTime> getHsaDropInHours() {
    return hsaDropInHours;
  }

  public void setHsaDropInHours(List<WeekdayTime> list) {
    this.hsaDropInHours = list;
  }

  public String getVgrEANCode() {
    return vgrEANCode;
  }

  public void setVgrEANCode(String vgrEANCode) {
    this.vgrEANCode = vgrEANCode;
  }

  public String getHsaMunicipalityCode() {
    return hsaMunicipalityCode;
  }

  public void setHsaMunicipalityCode(String hsaMunicipalityCode) {
    this.hsaMunicipalityCode = hsaMunicipalityCode;
  }

  public String getHsaMunicipalitySectionName() {
    return hsaMunicipalitySectionName;
  }

  public void setHsaMunicipalitySectionName(String hsaMunicipalitySectionName) {
    this.hsaMunicipalitySectionName = hsaMunicipalitySectionName;
  }

  public String getHsaMunicipalitySectionCode() {
    return hsaMunicipalitySectionCode;
  }

  public void setHsaMunicipalitySectionCode(String hsaMunicipalitySectionCode) {
    this.hsaMunicipalitySectionCode = hsaMunicipalitySectionCode;
  }

  public String getHsaCountyCode() {
    return hsaCountyCode;
  }

  public void setHsaCountyCode(String hsaCountyCode) {
    this.hsaCountyCode = hsaCountyCode;
  }

  public String getHsaCountyName() {
    return hsaCountyName;
  }

  public void setHsaCountyName(String hsaCountyName) {
    this.hsaCountyName = hsaCountyName;
  }

  public String getHsaManagementCode() {
    return hsaManagementCode;
  }

  public void setHsaManagementCode(String hsaManagementCode) {
    this.hsaManagementCode = hsaManagementCode;
  }

  public String getHsaManagementName() {
    return hsaManagementName;
  }

  public void setHsaManagementName(String hsaManagementName) {
    this.hsaManagementName = hsaManagementName;
  }

  public String getHsaAdministrationForm() {
    return hsaAdministrationForm;
  }

  public void setHsaAdministrationForm(String hsaAdministrationForm) {
    this.hsaAdministrationForm = hsaAdministrationForm;
  }

  public String getHsaAdministrationFormText() {
    return hsaAdministrationFormText;
  }

  public void setHsaAdministrationFormText(String hsaAdministrationFormText) {
    this.hsaAdministrationFormText = hsaAdministrationFormText;
  }

  public DN getDn() {
    return dn;
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
    String dnString = dn.toString();
    String dnStringBase64Encoded;
		dnStringBase64Encoded = new String(Base64.encodeBase64(StringUtil.getBytes(dnString, "ISO-8859-1"), true));
    return dnStringBase64Encoded;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOrganizationalUnitNameShort() {
    return organizationalUnitNameShort;
  }

  public void setOrganizationalUnitNameShort(String organizationalUnitNameShort) {
    this.organizationalUnitNameShort = organizationalUnitNameShort;
  }

  public String getLdapDistinguishedName() {
    return ldapDistinguishedName;
  }

  public void setLdapDistinguishedName(String ldapDistinguishedName) {
    this.ldapDistinguishedName = ldapDistinguishedName;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getLabeledURI() {
    return labeledURI;
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
    return locality;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public String getVgrInternalSedfInvoiceAddress() {
    return vgrInternalSedfInvoiceAddress;
  }

  public void setVgrInternalSedfInvoiceAddress(String vgrInternalSedfInvoiceAddress) {
    this.vgrInternalSedfInvoiceAddress = vgrInternalSedfInvoiceAddress;
  }

  public String getVgrCareType() {
    return vgrCareType;
  }

  public void setVgrCareType(String vgrCareType) {
    this.vgrCareType = vgrCareType;
  }

  public String getVgrCareTypeText() {
    return vgrCareTypeText;
  }

  public void setVgrCareTypeText(String vgrCareTypeText) {
    this.vgrCareTypeText = vgrCareTypeText;
  }

  public String getVgrAO3kod() {
    return vgrAO3kod;
  }

  public void setVgrAO3kod(String vgrAO3kod) {
    this.vgrAO3kod = vgrAO3kod;
  }

  public String getVgrAO3kodText() {
    return vgrAO3kodText;
  }

  public void setVgrAO3kodText(String vgrAO3kodText) {
    this.vgrAO3kodText = vgrAO3kodText;
  }

  public String getHsaIdentity() {
    return hsaIdentity;
  }

  public void setHsaIdentity(String hsaIdentity) {
    this.hsaIdentity = hsaIdentity;
  }

  public PhoneNumber getHsaSedfSwitchboardTelephoneNo() {
    return hsaSedfSwitchboardTelephoneNo;
  }

  public void setHsaSedfSwitchboardTelephoneNo(PhoneNumber hsaSedfSwitchboardTelephoneNo) {
    this.hsaSedfSwitchboardTelephoneNo = hsaSedfSwitchboardTelephoneNo;
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

  public PhoneNumber getMobileTelephoneNumber() {
    return mobileTelephoneNumber;
  }

  public void setMobileTelephoneNumber(PhoneNumber mobileTelephoneNumber) {
    this.mobileTelephoneNumber = mobileTelephoneNumber;
  }

  public PhoneNumber getHsaSmsTelephoneNumber() {
    return hsaSmsTelephoneNumber;
  }

  public void setHsaSmsTelephoneNumber(PhoneNumber hsaSmsTelephoneNumber) {
    this.hsaSmsTelephoneNumber = hsaSmsTelephoneNumber;
  }

  public PhoneNumber getFacsimileTelephoneNumber() {
    return facsimileTelephoneNumber;
  }

  public void setFacsimileTelephoneNumber(PhoneNumber facsimileTelephoneNumber) {
    this.facsimileTelephoneNumber = facsimileTelephoneNumber;
  }

  public String getHsaUnitPrescriptionCode() {
    return hsaUnitPrescriptionCode;
  }

  public void setHsaUnitPrescriptionCode(String hsaUnitPrescriptionCode) {
    this.hsaUnitPrescriptionCode = hsaUnitPrescriptionCode;
  }

  public String getHsaMunicipalityName() {
    return hsaMunicipalityName;
  }

  public void setHsaMunicipalityName(String hsaMunicipalityName) {
    this.hsaMunicipalityName = hsaMunicipalityName;
  }

  public String getOu() {
    return ou;
  }

  public void setOu(String ou) {
    this.ou = ou;
  }

  public List<String> getDescription() {
    return description;
  }

  public void setDescription(List<String> description) {
    this.description = description;
  }

  public void setInternalDescription(List<String> internalDescription) {
    this.internalDescription = internalDescription;
  }

  public List<PhoneNumber> getHsaTelephoneNumber() {
    return hsaTelephoneNumber;
  }

  public void setHsaTelephoneNumber(List<PhoneNumber> hsaTelephoneNumber) {
    this.hsaTelephoneNumber = hsaTelephoneNumber;
  }

  public List<PhoneNumber> getHsaPublicTelephoneNumber() {
    return hsaPublicTelephoneNumber;
  }

  public void setHsaPublicTelephoneNumber(List<PhoneNumber> hsaPublicTelephoneNumber) {
    this.hsaPublicTelephoneNumber = hsaPublicTelephoneNumber;
  }

  public List<WeekdayTime> getHsaTelephoneTime() {
    return hsaTelephoneTime;
  }

  public void setHsaTelephoneTime(List<WeekdayTime> hsaTelephoneTime) {
    this.hsaTelephoneTime = hsaTelephoneTime;
  }

  public Address getHsaInternalAddress() {
    return hsaInternalAddress;
  }

  public void setHsaInternalAddress(Address hsaInternalAddress) {
    this.hsaInternalAddress = hsaInternalAddress;
  }

  public Address getHsaStreetAddress() {
    return hsaStreetAddress;
  }

  public void setHsaStreetAddress(Address hsaStreetAddress) {
    this.hsaStreetAddress = hsaStreetAddress;
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

  public Address getHsaSedfInvoiceAddress() {
    return hsaSedfInvoiceAddress;
  }

  public void setHsaSedfInvoiceAddress(Address hsaSedfInvoiceAddress) {
    this.hsaSedfInvoiceAddress = hsaSedfInvoiceAddress;
  }

  public List<String> getVgrAnsvarsnummer() {
    return vgrAnsvarsnummer;
  }

  public void setVgrAnsvarsnummer(List<String> vgrAnsvarsnummer) {
    this.vgrAnsvarsnummer = vgrAnsvarsnummer;
  }

  public String getVgrEDICode() {
    return vgrEDICode;
  }

  public void setVgrEDICode(String vgrEDICode) {
    this.vgrEDICode = vgrEDICode;
  }

  public String getVgrOrganizationalRole() {
    return vgrOrganizationalRole;
  }

  public void setVgrOrganizationalRole(String vgrOrganizationalRole) {
    this.vgrOrganizationalRole = vgrOrganizationalRole;
  }

  public String getHsaManagementText() {
    return hsaManagementText;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (hsaIdentity == null) {
      result = prime * result;
    } else {
      result = prime * result + hsaIdentity.hashCode();
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
        if (getClass() != obj.getClass()) {
          result = false;
        } else {
          Unit other = (Unit) obj;
          if (hsaIdentity == null) {
            if (other.hsaIdentity != null) {
              result = false;
            }
          } else if (!hsaIdentity.equals(other.hsaIdentity)) {
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
    return hsaVisitingRuleAge;
  }

  /**
   * Setter for the hsaVisitingRuleAge property. Converts the value from the LDAP-value to a more human readable form.
   * 
   * @param hsaVisitingRuleAge The LDAP-value for the hsaVisitingRuleAge property.
   */
  public void setHsaVisitingRuleAge(String hsaVisitingRuleAge) {
    if ("0-99".equals(hsaVisitingRuleAge)) {
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
    return hsaVisitingRules;
  }

  public void setHsaVisitingRules(String hsaVisitingRules) {
    this.hsaVisitingRules = hsaVisitingRules;
  }

  public String getVgrTempInfo() {
    return vgrTempInfo;
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
        setVgrTempInfoStart(cal.getTime());
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
        setVgrTempInfoEnd(cal.getTime());
      } catch (ParseException e) {
        // KIV validates this field. Nothing we can do if it is incorrect.
        LOG.error("Unable to parse provided enddate as a date", e);
      }
      // Message
      setVgrTempInfoBody(vgrTempInfo.substring(vgrTempInfo.indexOf(" ") + 1));
    }
  }

  public Date getVgrTempInfoStart() {
    return vgrTempInfoStart;
  }

  public void setVgrTempInfoStart(Date vgrTempInfoStart) {
    this.vgrTempInfoStart = vgrTempInfoStart;
  }

  public Date getVgrTempInfoEnd() {
    return vgrTempInfoEnd;
  }

  public void setVgrTempInfoEnd(Date vgrTempInfoEnd) {
    this.vgrTempInfoEnd = vgrTempInfoEnd;
  }

  public String getVgrTempInfoBody() {
    return vgrTempInfoBody;
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
    Date now = new Date();

    if (vgrTempInfoStart != null && vgrTempInfoEnd != null) {
      show = now.after(vgrTempInfoStart) && now.before(vgrTempInfoEnd);
    }

    return show;
  }

  public String getVgrRefInfo() {
    return vgrRefInfo;
  }

  public void setVgrRefInfo(String vgrRefInfo) {
    this.vgrRefInfo = vgrRefInfo;
  }

  public String getConcatenatedDescription() {
    return getConcatenatedDescription(false);
  }

  public String getInternalConcatenatedDescription() {
    return getConcatenatedDescription(true);
  }

  /***
   * 
   * @param useInteralDescription - if true internal description is returned concatenated
   * @return - String of concatenated internal or external description
   */
  private String getConcatenatedDescription(boolean useInteralDescription) {
    String concatenatedDescription = "";
    if (useInteralDescription && internalDescription != null && internalDescription.size() > 0) {
      for (String s : internalDescription) {
        concatenatedDescription += s + "";
      }
    } else if (!useInteralDescription && description != null && description.size() > 0) {
      for (String s : description) {
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
    if (healthcareTypes != null) {
      String healthcareTypeString = "";
      for (HealthcareType htc : healthcareTypes) {
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
    if (StringUtil.isEmpty(getName())) {
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
    if (StringUtil.isEmpty(getHsaMunicipalityName())) {
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
    if (getHsaStreetAddress() == null || getHsaStreetAddress().isEmpty()) {
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
    if (Evaluator.isEmptyWeekDayTime(getHsaSurgeryHours())) {
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
    if (Evaluator.isEmptyWeekDayTime(getHsaDropInHours())) {
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
    if (Evaluator.isEmptyPhoneNumber(getHsaPublicTelephoneNumber())) {
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
    if (Evaluator.isEmptyWeekDayTime(getHsaTelephoneTime())) {
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
    if (StringUtil.isEmpty(getHsaGeographicalCoordinates())) {
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
    if (StringUtil.isEmpty(getLabeledURI()) || !(getLabeledURI().startsWith("http://") || getLabeledURI().startsWith("https://"))) {
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
    if (Evaluator.isEmpty(getDescription())) {
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
    if (StringUtil.isEmpty(getVgrOrganizationalRole())) {
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
    if (Evaluator.isEmptyBusinessClassification(healthcareTypes)) {
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
    if (StringUtil.isEmpty(getHsaVisitingRuleAge())) {
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
    if (StringUtil.isEmpty(getHsaVisitingRules())) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the units care type is valid.
   * 
   * @return True if the units care type is not empty, otherwise false.
   */
  public boolean getVgrCareTypeIsValid() {
    if (StringUtil.isEmpty(getVgrCareType())) {
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
    if (StringUtil.isEmpty(getHsaManagementText())) {
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

    // name
    valid &= getNameIsValid();

    // hsaMunicapilatyName
    valid &= getHsaMunicapilatyNameIsValid();

    // hsaStreetAdress
    valid &= getHsaStreetAddressIsValid();

    // hsaRoute
    valid &= getHsaRouteIsValid();

    // coordinates
    valid &= getHsaGeographicalCoordinatesIsValid();

    // surgeryHours
    valid &= getHsaSurgeryHoursIsValid();

    // dropInHours
    valid &= getHsaDropInHoursIsValid();

    // hsaPublicTelephoneNumber
    valid &= getHsaPublicTelephoneNumberIsValid();

    // hsaTelephoneTime
    valid &= getHsaTelephoneTimeIsValid();

    // labeledURI
    valid &= getLabeledURIIsValid();

    // description
    valid &= getDescriptionIsValid();

    // hsaVisitingRuleAge
    valid &= getHsaVisitingRuleAgeIsValid();

    // hsaVisitingRules
    valid &= getHsaVisitingRulesIsValid();

    // vgrCareType NOT USED NOW
    // valid &= getVgrCareTypeIsValid();

    // hsaManagementText
    valid &= getHsaManagementTextIsValid();

    return valid;
  }

  private boolean getHsaRouteIsValid() {
    return !Evaluator.isEmpty(getHsaRoute());
  }

  public void setModifyTimestamp(TimePoint modifyTimestamp) {
    this.modifyTimestamp = modifyTimestamp;

  }

  public TimePoint getModifyTimestamp() {
    return modifyTimestamp;
  }

  /**
   * Gets the units modify timestamp as a formatted string.
   * 
   * @return The units modify timestamp as a formatted string (yyyy-MM-dd HH:mm:ss).
   */
  public String getModifyTimestampFormatted() {
    if (modifyTimestamp != null) {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(modifyTimestamp.asJavaUtilDate());
    } else {
      return "";
    }
  }

  /**
   * Gets the units modify timestamp as a W3C-formatted string.
   * 
   * @return The units modify timestamp as a W3C-formatted string (yyyy-MM-dd'T'HH:mm:ssZ).
   */
  public String getModifyTimestampFormattedInW3CDatetimeFormat() {
    if (modifyTimestamp != null) {
      String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(modifyTimestamp.asJavaUtilDate());
      return timeStamp.substring(0, 22) + ":" + timeStamp.substring(22);
    } else {
      return "";
    }
  }

  public TimePoint getCreateTimestamp() {
    return createTimestamp;
  }

  public void setCreateTimestamp(TimePoint createTimestamp) {
    this.createTimestamp = createTimestamp;
  }

  /**
   * Gets the units create timestamp as a formatted string.
   * 
   * @return The units create timestamp as a formatted string (yyyy-MM-dd HH:mm:ss).
   */
  public String getCreateTimestampFormatted() {
    if (createTimestamp != null) {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTimestamp.asJavaUtilDate());
    } else {
      return "";
    }
  }

  /**
   * Gets the units create timestamp as a W3C-formatted string.
   * 
   * @return The units create timestamp as a W3C-formatted string (yyyy-MM-dd'T'HH:mm:ssZ).
   */
  public String getCreateTimestampFormattedInW3CDatetimeFormat() {
    if (createTimestamp != null) {
      String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(createTimestamp.asJavaUtilDate());
      return timeStamp.substring(0, 22) + ":" + timeStamp.substring(22);
    } else {
      return "";
    }
  }

  public String getHsaGeographicalCoordinates() {
    return hsaGeographicalCoordinates;
  }

  public void setHsaGeographicalCoordinates(String hsaGeographicalCoordinates) {
    this.hsaGeographicalCoordinates = hsaGeographicalCoordinates;
  }

  public double getWgs84Lat() {
    return wgs84Lat;
  }

  public double getWgs84LatRounded() {
    return (double) Math.round(wgs84Lat * 10000) / 10000;
  }

  public void setWgs84Lat(double wgs84Lat) {
    this.wgs84Lat = wgs84Lat;
  }

  public double getWgs84Long() {
    return wgs84Long;
  }

  public double getWgs84LongRounded() {
    return (double) Math.round(wgs84Long * 10000) / 10000;

  }

  public void setWgs84Long(double wgs84Long) {
    this.wgs84Long = wgs84Long;
  }

  public int getRt90X() {
    return rt90X;
  }

  public void setRt90X(int rt90X) {
    this.rt90X = rt90X;
  }

  public int getRt90Y() {
    return rt90Y;
  }

  public void setRt90Y(int rt90Y) {
    this.rt90Y = rt90Y;
  }

  public Integer getAccessibilityDatabaseId() {
    return accessibilityDatabaseId;
  }

  public void setAccessibilityDatabaseId(int accessibilityDatabaseId) {
    this.accessibilityDatabaseId = accessibilityDatabaseId;
  }

  public String getObjectClass() {
    return objectClass;
  }

  public void setObjectClass(String objectClass) {
    this.objectClass = objectClass;
  }

  public boolean getIsUnit() {
    return isUnit;
  }

  public void setIsUnit(boolean isUnit) {
    this.isUnit = isUnit;
  }

  public List<String> getHsaRoute() {
    return hsaRoute;
  }

  /**
   * Setter for the hsaRoute property. Also sets the hsaRouteConcatenated property.
   * 
   * @param hsaRoutelist The list of strings the hsaRoute consists of.
   */
  public void setHsaRoute(List<String> hsaRoutelist) {
    this.hsaRoute = hsaRoutelist;
    if (hsaRoutelist != null && hsaRoutelist.size() > 0) {
      for (String s : hsaRoute) {
        this.hsaRouteConcatenated += s + "";
      }
    }
  }

  public List<HealthcareType> getHealthcareTypes() {
    return healthcareTypes;
  }

  public void setHealthcareTypes(List<HealthcareType> healthcareTypes) {
    this.healthcareTypes = healthcareTypes;
  }

  /**
   * Returns comma separated list of assigned health care types.
   * 
   * @return A list of HealthcareType objects where all but the last one have a comma appended to it's display name.
   */
  public List<HealthcareType> getHealthcareTypesCustomized() {
    List<HealthcareType> healthcareTypesCustomized = new ArrayList<HealthcareType>();
    for (HealthcareType ht : healthcareTypes) {
      HealthcareType htNew = new HealthcareType(ht.getConditions(), ht.getDisplayName(), ht.isFiltered(), ht.getIndex());
      healthcareTypesCustomized.add(htNew);
    }
    for (int i = 0; i < healthcareTypesCustomized.size() - 1; i++) {
      healthcareTypesCustomized.get(i).setDisplayName(healthcareTypesCustomized.get(i).getDisplayName() + ", ");
    }
    return healthcareTypesCustomized;
  }

  public List<String> getHsaBusinessClassificationCode() {
    return hsaBusinessClassificationCode;
  }

  /**
   * Overloaded to support LTH.
   * 
   * @return business classification code
   */
  public List<String> getBusinessClassificationCode() {
    return hsaBusinessClassificationCode;
  }

  public void setHsaBusinessClassificationCode(List<String> hsaBusinessClassificationCode) {
    this.hsaBusinessClassificationCode = hsaBusinessClassificationCode;
  }

  public List<String> getHsaBusinessClassificationText() {
    return hsaBusinessClassificationText;
  }

  public String getHsaBusinessClassificationTextFormatted() {
    return Formatter.concatenate(hsaBusinessClassificationText);
  }

  public void setHsaBusinessClassificationText(List<String> hsaBusinessClassificationText) {
    this.hsaBusinessClassificationText = hsaBusinessClassificationText;
  }

  public void setMvkCaseTypes(List<String> caseTypes) {
    this.mvkCaseTypes = caseTypes;
  }

  public List<String> getMvkCaseTypes() {
    return mvkCaseTypes;
  }

  /**
   * Gets the units ancestor as a nicely formatted string.
   * 
   * @return the units ancestor as a nicely formatted string or an empty string if hsaIdentity does not contain the letter F or if the units is a hospital.
   */
  public String getFormattedAncestor() {
    String formattedAncestor = "";
    // Should be safe to use this condition as that is specified by HSA standard.
    if (hsaIdentity.indexOf("F") > 0) {
      // Hospitals should not be included
      HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
      if (!getHealthcareTypes().contains(healthcareTypeConditionHelper.getHealthcareTypeByName("Sjukhus"))) {
        formattedAncestor = ", tillhör " + dn.getAncestor(1).getUnitName();
      }
    }

    return formattedAncestor;
  }

  @Override
  public int compareTo(Unit o) {
    return this.hsaIdentity.compareTo(o.getHsaIdentity());
  }

  public AccessibilityInformation getAccessibilityInformation() {
    return accessibilityInformation;
  }

  public void setAccessibilityInformation(AccessibilityInformation accessibilityInformation) {
    this.accessibilityInformation = accessibilityInformation;
  }

  public Date getHsaEndDate() {
    return hsaEndDate;
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
    return isVgrVardVal() && hasHealthcareType("Vårdcentral");
  }

  /**
   * Helper method for checking if a unit has a specific healthcare type.
   * 
   * @param healthcareType The specific healthcare type to look for.
   * @return True if the unit has the provided healthcare type, otherwise false.
   */
  public boolean hasHealthcareType(String healthcareType) {
    boolean result = false;

    if (healthcareType != null && getHealthcareTypes() != null) {
      for (HealthcareType ht : getHealthcareTypes()) {
        if (healthcareType.equals(ht.getDisplayName())) {
          result = true;
        }
      }
    }
    return result;
  }

  public boolean isShowAgeInterval() {
    return showAgeInterval;
  }

  public void setShowAgeInterval(boolean showAgeInterval) {
    this.showAgeInterval = showAgeInterval;
  }

  public boolean isShowVisitingRules() {
    return showVisitingRules;
  }

  public void setShowVisitingRules(boolean showVisitingRules) {
    this.showVisitingRules = showVisitingRules;
  }

  public String getInternalWebsite() {
    return internalWebsite;
  }

  public void setInternalWebsite(String internalWebsite) {
    this.internalWebsite = internalWebsite;
  }

  public String getContractCode() {
    return contractCode;
  }

  public void setContractCode(String contractCode) {
    this.contractCode = contractCode;
  }

  public List<WeekdayTime> getVisitingHours() {
    return visitingHours;
  }

  public void setVisitingHours(List<WeekdayTime> visitingHours) {
    this.visitingHours = visitingHours;
  }

  public String getVisitingRuleReferral() {
    return visitingRuleReferral;
  }

  public void setVisitingRuleReferral(String visitingRuleReferral) {
    this.visitingRuleReferral = visitingRuleReferral;
  }
}
