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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.TimeZone;

import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.ldap.LdapORMHelper;

import com.domainlanguage.time.TimePoint;
import com.novell.ldap.LDAPEntry;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class EmploymentFactory {

  public static Employment reconstitute(LDAPEntry employmentEntry) {
    Employment employment = new Employment();

    if (employmentEntry == null) {
      return employment;
    }

    employment.setCn(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("cn")));

    // Organizational Unit Name (e.g. Barn- och ungdomspsykiatrisk mottagning Bor�s)
    employment.setOu(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("ou")));

    // Person-id (e.g. 196712085983)
    employment.setHsaPersonIdentityNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaPersonIdentityNumber")));

    // HsaIdentitie to the Units where the person is employed e.g. SE2321000131-E000000000101
    employment.setVgrOrgRel(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrOrgRel")));

    employment.setVgrStrukturPerson(DN.createDNFromString(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrStrukturPerson"))));

    // Ansvarsnumer e.g. 1, 2
    employment.setVgrAnsvarsnummer(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrAnsvarsnummer")));

    // Anst�llningsperiod
    employment.setEmploymentPeriod(TimePoint.parseFrom(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaStartDate")), ""/* TODO Add pattern */, TimeZone.getDefault()), TimePoint
        .parseFrom(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaEndDate")), ""/* TODO Add pattern */, TimeZone.getDefault()));

    // Fakturaadress e.g. S�dra �lvsborgs Sjukhus$L�ne- och fakturaservice $ $ $501 82$Bor�s
    employment.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaSedfInvoiceAddress"))));

    // Bes�ksadress e.g. Elinsdalsgatan 8, Bor�s
    employment.setHsaStreetAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaStreetAddress"))));

    // Internadress e.g. BUP Elinsdahl, Bor�s
    employment.setHsaInternalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaInternalAddress"))));

    // Postadress e.g. S�dra �lvsborgs Sjukhus$Barn- och ungdomspsykiatrisk ... $ $ $501 82$Bor�s
    employment.setHsaPostalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaPostalAddress"))));

    // Leveransadress e.g. S�dra �lvsborgs Sjukhus$Barn- och... $Elinsdalsgatan 8$ $504 33$Bor�s
    employment.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaSedfDeliveryAddress"))));

    // Faxnummer e.g. +46 33 6164930
    employment.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("facsimileTelephoneNumber"))));

    // postnummer e.g. 416 73
    employment.setZipCode(new ZipCode(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("postalCode"))));

    // hemsida e.g. http://www.vgregion.se/...
    employment.setLabeledUri(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("labeledUri")));

    // e.g. 3
    employment.setVgrAnstform(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrAnstform")));

    // e.g. Psykolog,leg.
    employment.setTitle(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("title")));

    // e.g. 12
    employment.setVgrFormansgrupp(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrFormansgrupp")));

    // V�xeltelefon e.g. +46 33 6161000
    employment.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaSedfSwitchboardTelephoneNo"))));

    employment.setVgrAO3kod(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("vgrAO3kod")));

    employment.setName(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("organizationalUnitName")));

    employment.setHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaTelephoneNumber"))));

    employment.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaPublicTelephoneNumber"))));

    employment.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("mobileTelephoneNumber"))));

    employment.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaInternalPagerNumber"))));

    employment.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("pagerTelephoneNumber"))));

    employment.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("hsaTextPhoneNumber"))));

    employment.setModifyTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("modifyTimestamp")), ""/* TODO Add pattern */, TimeZone.getDefault()));

    employment.setModifyersName(LdapORMHelper.getSingleValue(employmentEntry.getAttribute("modifyersName")));

    employment.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("hsaTelephoneTime"))));

    employment.setDescription(LdapORMHelper.getMultipleValues(employmentEntry.getAttribute("description")));

    return employment;
  }

}
