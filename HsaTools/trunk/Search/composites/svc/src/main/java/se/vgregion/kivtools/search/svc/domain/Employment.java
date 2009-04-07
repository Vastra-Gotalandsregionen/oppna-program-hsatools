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

import java.io.Serializable;
import java.util.List;

import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.ZipCode;

import com.domainlanguage.time.TimeInterval;
import com.domainlanguage.time.TimePoint;

/**
 * This class represents an Employment (swedish Anst�llning) Check out
 * vgrAnstallning for reference
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 * @author Anders Asplund / KnowIT
 * 
 */

public class Employment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cn;                              // Common name (e.g. 1750935136900000017)
    private String ou;                              // Organizational Unit Name (e.g. Barn- och ungdomspsykiatrisk mottagning Bor�s)
    private String hsaPersonIdentityNumber;         // Person-id e.g. 197209184683
    private String vgrOrgRel;                       // HsaIdentitie to the Units where the person is employed e.g. SE2321000131-E000000000101
    private String name;                            // Namn p� enheten
    private String modifyersName;                   // Uppdaterat sv vem
    private String vgrAnsvarsnummer;                // Ansvarsnumer e.g. 1, 2
    private String vgrAnstform;                     // e.g. 3
    private String title;                           // e.g. Psykolog,leg.
    private String vgrFormansgrupp;                 // e.g. 12
    private String vgrAO3kod;                       // Ansvarsomr�des kod e.g. 082
    private List<String> description;               // Beskrivning

    private String labeledUri;                         // Hemsida e.g. http://www.vgregion.se/vgrtemplates/Page____25161.aspx

    // Distinguished Names
    private DN vgrStrukturPerson;                   // Anst�llnigobjektest plats i organisationen
    private DN dn;                                  // Distinguished Name (e.g. cn=1750935136900000017,cn=annth38,ou=Personal,o=VGR

    // Time Objects
    private TimeInterval employmentPeriod;          // Anst�llningspreiod(hsaStartDate till hsaEndDate)
    private TimePoint modifyTimestamp;              // Senast uppdaterad


    // Address objects
    private Address hsaSedfInvoiceAddress;          // Fakturaadress e.g. S�dra �lvsborgs Sjukhus$L�ne- och fakturaservice $ $ $501 82$Bor�s
    private Address hsaStreetAddress;               // Bes�ksadress e.g. Elinsdalsgatan 8, Bor�s
    private Address hsaInternalAddress;             // Internadress e.g. BUP Elinsdahl, Bor�s
    private Address hsaPostalAddress;               // Postadress e.g. S�dra �lvsborgs Sjukhus$Barn- och ungdomspsykiatrisk mottagning $ $ $501 82$Bor�s
    private Address hsaSedfDeliveryAddress;         // Leveransadress e.g. S�dra �lvsborgs Sjukhus$Barn- och ungdomspsmottagning $Elinsdalsgatan 8$ $504 33$Bor�s
    private ZipCode zipCode;                        // Postnummer e.g. 416 73

    // Phone Numbers
    private PhoneNumber facsimileTelephoneNumber;   // Faxnummer
    private PhoneNumber hsaTelephoneNumber;         // Direkttelefon visas inom organisationen
    private PhoneNumber hsaPublicTelephoneNumber;   // Direkttelefon visas f�r allm�nheten
    private PhoneNumber mobileTelephoneNumber;      // Mobilnummer
    private PhoneNumber hsaInternalPagerNumber;     // Persons�kare
    private PhoneNumber pagerTelephoneNumber;       // Minicall
    private PhoneNumber hsaTextPhoneNumber;         // Texttelefon
    private PhoneNumber hsaSedfSwitchboardTelephoneNo;  // V�xeltelefon
    private List<WeekdayTime> hsaTelephoneTime;          // Telefontid


    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public DN getDn() {
        return dn;
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

    public void setHsaSedfSwitchboardTelephoneNo(
            PhoneNumber hsaSedfSwitchboardTelephoneNo) {
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

    public PhoneNumber getHsaTelephoneNumber() {
        return hsaTelephoneNumber;
    }

    public void setHsaTelephoneNumber(PhoneNumber hsaTelephoneNumber) {
        this.hsaTelephoneNumber = hsaTelephoneNumber;
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

    public void setHsaTelephoneTime(List<WeekdayTime> hsaTelephoneTime) {
        this.hsaTelephoneTime = hsaTelephoneTime;
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

    public void setEmploymentPeriod(TimePoint startDate, TimePoint stopDate) {
        setEmploymentPeriod(TimeInterval.over(startDate, stopDate));
    }

    public DN getVgrStrukturPerson() {
        return vgrStrukturPerson;
    }

    public void setVgrStrukturPerson(DN vgrStrukturPerson) {
        this.vgrStrukturPerson = vgrStrukturPerson;
    }

}
