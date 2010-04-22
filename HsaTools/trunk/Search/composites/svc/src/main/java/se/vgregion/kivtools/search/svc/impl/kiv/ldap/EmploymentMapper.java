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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Employment;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

public class EmploymentMapper implements ContextMapper {
  private CodeTablesService codeTablesService;

  public EmploymentMapper(CodeTablesService codeTablesService) {
    super();
    this.codeTablesService = codeTablesService;
  }

  @Override
  public Employment mapFromContext(Object ctx) {
    Employment employment = new Employment();
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

    employment.setCn(context.getString(LDAPEmploymentAttributes.CN.toString()));

    // Organizational Unit Name (e.g. Barn- och ungdomspsykiatrisk mottagning Bor�s)
    employment.setOu(context.getString(LDAPEmploymentAttributes.OU.toString()));

    // Person-id (e.g. 196712085983)
    employment.setHsaPersonIdentityNumber(context.getString(LDAPEmploymentAttributes.HSA_PERSON_IDENTITY_NUMBER.toString()));

    // HsaIdentitie to the Units where the person is employed e.g. SE2321000131-E000000000101
    employment.setVgrOrgRel(context.getString(LDAPEmploymentAttributes.VGR_ORG_REL.toString()));

    employment.setVgrStrukturPerson(DN.createDNFromString(context.getString(LDAPEmploymentAttributes.EMPLOYED_AT_UNIT.toString())));

    // Ansvarsnumer e.g. 1, 2
    employment.setVgrAnsvarsnummer(context.getString(LDAPEmploymentAttributes.VGR_ANSVARS_NUMMER.toString()));

    // Anst�llningsperiod
    employment.setEmploymentPeriod(parseDateTime(context.getString(LDAPEmploymentAttributes.HSA_START_DATE.toString())), parseDateTime(context.getString(LDAPEmploymentAttributes.HSA_END_DATE
        .toString())));

    // Fakturaadress e.g. S�dra �lvsborgs Sjukhus$L�ne- och fakturaservice $ $ $501 82$Bor�s
    employment.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings(LDAPEmploymentAttributes.HSA_SEDF_INVOICE_ADDRESS.toString())));

    // Bes�ksadress e.g. Elinsdalsgatan 8, Bor�s
    employment.setHsaStreetAddress(AddressHelper.convertToAddress(context.getStrings(LDAPEmploymentAttributes.HSA_STREET_ADDRESS.toString())));

    // Internadress e.g. BUP Elinsdahl, Bor�s
    employment.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings(LDAPEmploymentAttributes.HSA_INTERNAL_ADRESS.toString())));

    // Postadress e.g. S�dra �lvsborgs Sjukhus$Barn- och ungdomspsykiatrisk ... $ $ $501 82$Bor�s
    employment.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings(LDAPEmploymentAttributes.HSA_POSTAL_ADDRESS.toString())));

    // Leveransadress e.g. S�dra �lvsborgs Sjukhus$Barn- och... $Elinsdalsgatan 8$ $504 33$Bor�s
    employment.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings(LDAPEmploymentAttributes.HSA_SEDF_DELIVERY_ADDRESS.toString())));

    // Faxnummer e.g. +46 33 6164930
    employment.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.FACSIMILE_TELEPHONE_NUMBER.toString())));

    // postnummer e.g. 416 73
    employment.setZipCode(new ZipCode(context.getString(LDAPEmploymentAttributes.POSTAL_CODE.toString())));

    // hemsida e.g. http://www.vgregion.se/...
    employment.setLabeledUri(context.getString(LDAPEmploymentAttributes.LABELED_URI.toString()));

    // e.g. 3
    employment.setVgrAnstform(context.getString(LDAPEmploymentAttributes.VGR_ANST_FORM.toString()));

    // e.g. Psykolog,leg.
    employment.setTitle(context.getString(LDAPEmploymentAttributes.EMPLOYMENT_TITLE.toString()));

    // e.g. 12
    employment.setVgrFormansgrupp(context.getString(LDAPEmploymentAttributes.VGR_FORMANS_GRUPP.toString()));

    // V�xeltelefon e.g. +46 33 6161000
    employment.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.HSA_SEDF_SWITCHBOARD_TELEPHONE.toString())));

    employment.setVgrAO3kod(context.getString(LDAPEmploymentAttributes.VGR_AO3_KOD.toString()));

    employment.setName(context.getString(LDAPEmploymentAttributes.ORGANIZATIONAL_UNIT_NAME.toString()));

    employment.setHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(context.getStrings(LDAPEmploymentAttributes.HSA_TELEPHONE_NUMBER.toString())));

    employment.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.HSA_PUBLIC_TELEPHONE_NUMBER.toString())));

    employment.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.MOBILE_TELEPHONE_NUMBER.toString())));

    employment.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.HSA_INTERNAL_PAGER_NUMBER.toString())));

    employment.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.PAGER_TELEPHONE_NUMBER.toString())));

    employment.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString(LDAPEmploymentAttributes.HSA_TEXT_PHONE_NUMBER.toString())));

    employment.setModifyTimestamp(parseDateTime(context.getString(LDAPEmploymentAttributes.MODIFY_TIMESTAMP.toString())));

    employment.setModifyersName(context.getString(LDAPEmploymentAttributes.MODIFYERS_NAME.toString()));

    employment.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(context.getStrings(LDAPEmploymentAttributes.HSA_TELEPHONE_TIME.toString())));

    employment.setDescription(context.getStrings(LDAPEmploymentAttributes.DESCRIPTION.toString()));

    // Locality
    employment.setLocality(context.getString(LDAPEmploymentAttributes.L.toString()));

    String paTitleCode = context.getString(LDAPEmploymentAttributes.PA_TITLE_CODE.toString());
    employment.setPosition(codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, paTitleCode));

    return employment;
  }

  private TimePoint parseDateTime(final String dateTime) {
    TimePoint result = null;

    if (!StringUtil.isEmpty(dateTime)) {
      result = TimePoint.parseFrom(dateTime, "yyyyMMddHHmmss'Z'", TimeZone.getDefault());
    }

    return result;
  }
}
