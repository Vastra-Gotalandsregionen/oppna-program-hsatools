/**
 * Copyright 2009 Västa Götalandsregionen
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

import se.vgregion.kivtools.search.util.Base64;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.accessibility.AccessibilityInformation;
import se.vgregion.kivtools.search.util.Constants;
import se.vgregion.kivtools.search.util.Evaluator;

import com.domainlanguage.time.TimePoint;

/**
 * Represents a unit (mottagning).
 * 
 * @author hangy2, Hans Gyllensten / KnowIT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class Unit implements Serializable {

	public Unit() {
	}

	private static final long serialVersionUID = 1L;
	private String ou; // 0u (e.g.Näl)
	private DN dn; // Distinuished Name (e.g. ou=N�l,ou=Org,o=VGR)
	private String name; // Enhetens namn
	private String organizationalUnitNameShort; // Enhetens kort namn
	private String ldapDistinguishedName; // Var i organisationen enheten
	private String objectClass; // e.g. organizationalUnit, organizationalRole
	private boolean isUnit; // true=Unit, false=Function

	private List<String> description; // Extern beskrivning
	private List<String> internalDescription; // Intern beskrivning
	private String mail; // E-postadress
	private String labeledURI; // Hemsida
	private String vgrInternalSedfInvoiceAddress; // I-nummer
	private String vgrCareType; // V�rdform
	private String vgrAO3kod; // Ansvarsomr�des kod
	private String hsaIdentity; // HSA identitet
	private List<HealthcareType> healthcareTypes; // Verksamhetskoder
	private List<String> hsaBusinessClassificationCode;
	private String hsaUnitPrescriptionCode; // Arbetsplatskod
	private List<String> vgrAnsvarsnummer; // Ansvarsnr
	private String hsaMunicipalityName; // Kommunnamn

	// Addresses
	private Address hsaPostalAddress; // Postadress
	private Address hsaInternalAddress; // Internadress
	private Address hsaStreetAddress; // Bes�ksadress
	private Address hsaSedfDeliveryAddress; // Leveransadress
	private Address hsaSedfInvoiceAddress; // Fakturaadress

	// Phone numbers
	private PhoneNumber hsaSedfSwitchboardTelephoneNo; // V�xeltelefon
	private PhoneNumber hsaInternalPagerNumber; // Persons�kare
	private PhoneNumber pagerTelephoneNumber; // Minicall
	private PhoneNumber hsaTextPhoneNumber; // Texttelefon
	private PhoneNumber mobileTelephoneNumber; // Mobiltelefon
	private PhoneNumber hsaSmsTelephoneNumber; // SMS
	private PhoneNumber facsimileTelephoneNumber; // Faxnummer
	private List<PhoneNumber> hsaTelephoneNumber; // Direkttelefon
	private List<PhoneNumber> hsaPublicTelephoneNumber; // Telefon publik
	private List<WeekdayTime> hsaTelephoneTime; // Telefontid

	private String vgrEDICode; // EDI-kod
	private String vgrEANCode; // EAN-kod
	private String hsaMunicipalityCode; // Kommunkod
	private String hsaMunicipalitySectionName; // Kommundelsnamn
	private String hsaMunicipalitySectionCode; // Kommundelskod
	private String hsaCountyCode; // L�nskod
	private String hsaCountyName; // L�nsnamn
	private String hsaManagementCode; // �garformkod
	private String hsaManagementName; // �garform klartext
	private String hsaAdministrationForm; // Drifts- & juridisk formkod
	private String hsaAdministrationFormText; // Drifts- & juridisk formklartext

	private List<WeekdayTime> hsaSurgeryHours;
	private List<WeekdayTime> hsaDropInHours;
	private String vgrOrganizationalRole;
	private String hsaManagementText;
	private String hsaVisitingRules;
	private String hsaVisitingRuleAge;
	
	private String vgrTempInfo;
	private String vgrRefInfo;
	private Date vgrTempInfoStart;
	private Date vgrTempInfoEnd;
	private String vgrTempInfoBody;
	
	private TimePoint modifyTimestamp; // Senast uppdaterad
	private TimePoint createTimestamp; // Skapad

	// Geo coordinates
	private String hsaGeographicalCoordinates;
	private double wgs84Lat; // In decimal degrees
	private double wgs84Long; // In decimal degrees
	private int rt90X;
	private int rt90Y;
	private GeoCoordinate geoCoordinate; // Needed for calculation of close units
	private String distanceToTarget;
	private List<String> mvkCaseTypes;

	// Vägbeskrivning
	private List<String> hsaRoute;
	private String hsaRouteConcatenated = "";

	private Integer accessibilityDatabaseId;
	private AccessibilityInformation accessibilityInformation;

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

	public String getDnBase64() throws UnsupportedEncodingException {
		String dnString = dn.toString();
		//dnString = "CN=Hedvig h Blomfrö,OU=Falkenbergsnämnden,OU=Förtroendevalda,OU=Landstinget  Halland,DC=hkat,DC=lthalland,DC=com";
		String dnStringBase64Encoded = Base64.encodeBytes(dnString.getBytes("ISO-8859-1"));
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

	public void setOrganizationalUnitNameShort(
			String organizationalUnitNameShort) {
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

	public void setLabeledURI(String labeledURI) {
		// Do some simple validation/fixing
		if (!"".equals(labeledURI) && !(labeledURI.startsWith("http://") || labeledURI.startsWith("https://"))) {
			labeledURI = "http://" + labeledURI;
		}
		
		this.labeledURI = labeledURI;
	}

	public String getVgrInternalSedfInvoiceAddress() {
		return vgrInternalSedfInvoiceAddress;
	}

	public void setVgrInternalSedfInvoiceAddress(
			String vgrInternalSedfInvoiceAddress) {
		this.vgrInternalSedfInvoiceAddress = vgrInternalSedfInvoiceAddress;
	}

	public String getVgrCareType() {
		return vgrCareType;
	}

	public void setVgrCareType(String vgrCareType) {
		this.vgrCareType = vgrCareType;
	}

	public String getVgrAO3kod() {
		return vgrAO3kod;
	}

	public void setVgrAO3kod(String vgrAO3kod) {
		this.vgrAO3kod = vgrAO3kod;
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

	public void setHsaSedfSwitchboardTelephoneNo(
			PhoneNumber hsaSedfSwitchboardTelephoneNo) {
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

	public void setInternalDescription(List<String> description) {
		this.internalDescription = description;
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

	public void setHsaPublicTelephoneNumber(
			List<PhoneNumber> hsaPublicTelephoneNumber) {
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

	public void setHsaManagementText(String hsaManagementText) {
		this.hsaManagementText = hsaManagementText;
	}

	public String getHsaVisitingRuleAge() {
		return hsaVisitingRuleAge;
	}

	public void setHsaVisitingRuleAge(String hsaVisitingRuleAge) {
		if ("0-99".equals(hsaVisitingRuleAge)) {
			this.hsaVisitingRuleAge = "Alla åldrar";
		} else if (hsaVisitingRuleAge.endsWith("-99")) {
			this.hsaVisitingRuleAge = hsaVisitingRuleAge.substring(0, hsaVisitingRuleAge.indexOf("-")) + " år eller äldre";
		} else if (!"".equals(hsaVisitingRuleAge)) {
			this.hsaVisitingRuleAge = hsaVisitingRuleAge + " år";
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

	public void setVgrTempInfo(String vgrTempInfo) {
		this.vgrTempInfo = vgrTempInfo;
		if ("".equals(vgrTempInfo) || !vgrTempInfo.contains("-") || vgrTempInfo.length() < 19) {
			return;
		}
		/* Split to vgrTempInfoStart, vgrTempInfoEnd and vgrTempInfoBody
		 * vgrTempInfo format: YYYYMMDD-YYYYMMDD Message
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
				cal.add(Calendar.DAY_OF_YEAR, -7); // Start showing one week earlier
				setVgrTempInfoStart(cal.getTime());
			} catch (ParseException e) {
				// KIV validates this field. Nothing we can do if it is incorrect.
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
				cal.add(Calendar.DAY_OF_YEAR, 1); // Stop showing the day after
				setVgrTempInfoEnd(cal.getTime());
			} catch (ParseException e) {
				// KIV validates this field. Nothing we can do if it is incorrect.
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

	public boolean getShouldVgrTempInfoBeShown() {
		Date now = new Date();
		
		if (vgrTempInfoStart == null || vgrTempInfoEnd == null) {
			return false;
		}
		
		if (now.after(vgrTempInfoStart) && now.before(vgrTempInfoEnd)) {
			return true;
		} else {
			return false;
		}
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
	 * @param useInteralDescription
	 *            - if true internal description is returned concatenated
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

	public String getCaretypeCustomized() {
		if (healthcareTypes != null) {
			String healthcareTypeString = "";
			for (HealthcareType htc : healthcareTypes){
				healthcareTypeString += htc.getDisplayName() + ", ";				
			}
			if (!"".equals(healthcareTypeString)) {
				return healthcareTypeString.substring(0, healthcareTypeString.length() - 2);
			}
		}
		return Constants.HSA_BUSINESS_CLASSIFICATION_NAME_UNKNOWN;
	}

	// VALIDATION
	// ***************
	public boolean getNameIsValid() {
		if (Evaluator.isEmpty(getName())) {
			return false;
		}
		return true;
	}

	public boolean getHsaMunicapilatyNameIsValid() {
		if (Evaluator.isEmpty(getHsaMunicipalityName())) {
			return false;
		}
		return true;
	}

	public boolean getHsaStreetAddressIsValid() {
		if (getHsaStreetAddress().isEmpty()) {
			return false;
		}
		return true;
	}

	public boolean getHsaSurgeryHoursIsValid() {
		if (Evaluator.isEmptyWeekDayTime(getHsaSurgeryHours())) {
			return false;
		}
		return true;
	}

	public boolean getHsaDropInHoursIsValid() {
		if (Evaluator.isEmptyWeekDayTime(getHsaDropInHours())) {
			return false;
		}
		return true;
	}

	public boolean getHsaPublicTelephoneNumberIsValid() {
		if (Evaluator.isEmptyPhoneNumber(getHsaPublicTelephoneNumber())) {
			return false;
		}
		return true;
	}

	public boolean getHsaTelephoneTimeIsValid() {
		if (Evaluator.isEmptyWeekDayTime(getHsaTelephoneTime())) {
			return false;
		}
		return true;
	}

	public boolean getHsaGeographicalCoordinatesIsValid() {
		if (Evaluator.isEmpty(getHsaGeographicalCoordinates())) {
			return false;
		}
		return true;
	}
	
	public boolean getLabeledURIIsValid() {
		if (Evaluator.isEmpty(getLabeledURI())
				|| !(getLabeledURI().startsWith("http://") || getLabeledURI()
						.startsWith("https://"))) {
			return false;
		}
		return true;
	}

	public boolean getDescriptionIsValid() {
		if (Evaluator.isEmpty(getDescription())) {
			return false;
		}
		return true;
	}

	public boolean getVgrOrganizationalRoleIsValid() {
		if (Evaluator.isEmpty(getVgrOrganizationalRole())) {
			return false;
		}
		return true;
	}

	public boolean getHsaBusinessClassificationNameIsValid() {
		if (Evaluator.isEmptyBusinessClassification(healthcareTypes)) {
			return false;
		}
		return true;
	}

	public boolean getHsaVisitingRuleAgeIsValid() {
		if (Evaluator.isEmpty(getHsaVisitingRuleAge())) {
			return false;
		}
		return true;
	}

	public boolean getHsaVisitingRulesIsValid() {
		if (Evaluator.isEmpty(getHsaVisitingRules())) {
			return false;
		}
		return true;
	}

	public boolean getVgrCareTypeIsValid() {
		if (Evaluator.isEmpty(getVgrCareType())) {
			return false;
		}
		return true;
	}

	public boolean getHsaManagementTextIsValid() {
		if (Evaluator.isEmpty(getHsaManagementText())) {
			return false;
		}
		return true;
	}

	public boolean getContentValidationOk() {

		// name
		if (!getNameIsValid()) {
			return false;
		}

		// hsaMunicapilatyName
		if (!getHsaMunicapilatyNameIsValid()) {
			return false;
		}

		// hsaStreetAdress
		if (!getHsaStreetAddressIsValid()) {
			return false;
		}

		// hsaRoute
		if (!getHsaRouteIsValid()) {
			return false;
		}

		// coordinates
		if (!getHsaGeographicalCoordinatesIsValid()) {
			return false;
		}

		// surgeryHours
		if (!getHsaSurgeryHoursIsValid()) {
			return false;
		}

		// dropInHours
		if (!getHsaDropInHoursIsValid()) {
			return false;
		}

		// hsaPublicTelephoneNumber
		if (!getHsaPublicTelephoneNumberIsValid()) {
			return false;
		}

		// hsaTelephoneTime
		if (!getHsaTelephoneTimeIsValid()) {
			return false;
		}

		// labeledURI
		if (!getLabeledURIIsValid()) {
			return false;
		}

		// description
		if (!getDescriptionIsValid()) {
			return false;
		}

		// hsaVisitingRuleAge
		if (!getHsaVisitingRuleAgeIsValid()) {
			return false;
		}

		// hsaVisitingRules
		if (!getHsaVisitingRulesIsValid()) {
			return false;
		}		

		// vgrCareType NOT USED NOW
		// if (!getVgrCareTypeIsValid()) {
		// return false;
		// }

		// hsaManagementText
		if (!getHsaManagementTextIsValid()) {
			return false;
		}

		return true;
	}

	private boolean getHsaRouteIsValid() {
		if (Evaluator.isEmpty(getHsaRoute()))
			return false;
		else
			return true;
	}

	public void setModifyTimestamp(TimePoint modifyTimestamp) {
		this.modifyTimestamp = modifyTimestamp;

	}

	public TimePoint getModifyTimestamp() {
		return modifyTimestamp;
	}

	public String getModifyTimestampFormatted() {
		if (modifyTimestamp != null) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(modifyTimestamp.asJavaUtilDate());
		} else {
			return "";
		}
	}

	public String getModifyTimestampFormattedInW3CDatetimeFormat() {
		if (modifyTimestamp != null) {
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
					.format(modifyTimestamp.asJavaUtilDate());
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

	public String getCreateTimestampFormatted() {
		if (createTimestamp != null) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(createTimestamp.asJavaUtilDate());
		} else {
			return "";
		}
	}

	public String getCreateTimestampFormattedInW3CDatetimeFormat() {
		if (createTimestamp != null) {
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
					.format(createTimestamp.asJavaUtilDate());
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

	public AccessibilityInformation getAccessibilityInformation() {
		return accessibilityInformation;
	}

	public void setAccessibilityInformation(
			AccessibilityInformation accessibilityInformation) {
		this.accessibilityInformation = accessibilityInformation;
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

	public void setHsaRoute(List<String> hsaRoutelist) {
		this.hsaRoute = hsaRoutelist;
		if (hsaRoutelist != null && hsaRoutelist.size() > 0) {
		for (String s : hsaRoute)
			this.hsaRouteConcatenated += s + "";
		}
	}

	public List<HealthcareType> getHealthcareTypes() {
		return healthcareTypes;
	}

	public void setHealthcareTypes(
			List<HealthcareType> healthcareTypes) {
		this.healthcareTypes = healthcareTypes;
	}

	/**
	 * Returns comma separated list of assigned health care types.
	 * @return
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

	public List<String> getBusinessClassificationCode() {
		return hsaBusinessClassificationCode;
	}

	public void setHsaBusinessClassificationCode(
			List<String> hsaBusinessClassificationCode) {
		this.hsaBusinessClassificationCode = hsaBusinessClassificationCode;
	}

	public void setMvkCaseTypes(List<String> caseTypes) {
		this.mvkCaseTypes = caseTypes;
	}

	public List<String> getMvkCaseTypes() {
		return mvkCaseTypes;
	}

	public String getFormattedAncestor() {
		// Should be safe to use this condition as that is specified by HSA standard.
		if (hsaIdentity.indexOf("F") > 0) {
			// Hospitals should not be included
			HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
			 if (!getHealthcareTypes().contains(healthcareTypeConditionHelper.getHealthcareTypeByName("Sjukhus"))) {
			 	return ", tillhör " + dn.getAncestor(1).getUnitName();
			 } else {
				 return "";
			 }
		} else {
			return "";
		}
	}
	
}
