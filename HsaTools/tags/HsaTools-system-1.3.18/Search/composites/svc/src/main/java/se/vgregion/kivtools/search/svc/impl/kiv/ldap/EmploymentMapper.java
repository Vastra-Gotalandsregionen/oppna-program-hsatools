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

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * Context mapper for employment entries.
 */
public class EmploymentMapper implements ContextMapper {
  private CodeTablesService codeTablesService;

  /**
   * Constructs a new EmploymentMapper.
   * 
   * @param codeTablesService The CodeTablesService to use.
   */
  public EmploymentMapper(CodeTablesService codeTablesService) {
    super();
    this.codeTablesService = codeTablesService;
  }

  @Override
  public Employment mapFromContext(Object ctx) {
    Employment employment = new Employment();
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

   employment.setCn(context.getString(EmploymentSearchAttributes.CN.toString()));

    // Organizational Unit Name (e.g. Barn- och ungdomspsykiatrisk mottagning Bor�s)
    employment.setOu(context.getString(EmploymentSearchAttributes.OU.toString()));

    // Person-id (e.g. 196712085983)
    employment.setHsaPersonIdentityNumber(context.getString(EmploymentSearchAttributes.HSA_PERSON_IDENTITY_NUMBER.toString()));

    // HsaIdentitie to the Units where the person is employed e.g. SE2321000131-E000000000101
    employment.setVgrOrgRel(context.getString(EmploymentSearchAttributes.VGR_ORG_REL.toString()));

    employment.setVgrStrukturPerson(DN.createDNFromString(context.getString(EmploymentSearchAttributes.EMPLOYED_AT_UNIT.toString())));

    // Ansvarsnumer e.g. 1, 2
    employment.setVgrAnsvarsnummer(context.getString(EmploymentSearchAttributes.VGR_ANSVARS_NUMMER.toString()));

    
    //Fr&aumlvaro, startdatum 
    
    employment.setVgrAbsenceStartDate(parseStrDate(context.getString(EmploymentSearchAttributes.VGR_ABSENCE_START_DATE.toString())));
    
    //Fr&aumlvaro, Slutdatum
    employment.setVgrAbsenceEndDate(parseStrDate(context.getString(EmploymentSearchAttributes.VGR_ABSENCE_END_DATE.toString())));
    
    //Linjechef 
    employment.setVgrEmployeeManager(context.getString(EmploymentSearchAttributes.VGR_EMPLOYEE_MANAGER.toString()));
   
    // Anst�llningsperiod
    employment.setEmploymentPeriod(parseDateTime(context.getString(EmploymentSearchAttributes.HSA_START_DATE.toString())), parseDateTime(context.getString(EmploymentSearchAttributes.HSA_END_DATE
        .toString())));

    // Fakturaadress e.g. S�dra �lvsborgs Sjukhus$L�ne- och fakturaservice $ $ $501 82$Bor�s
    employment.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings(EmploymentSearchAttributes.HSA_SEDF_INVOICE_ADDRESS.toString())));

    // Bes�ksadress e.g. Elinsdalsgatan 8, Bor�s
    employment.setHsaStreetAddress(AddressHelper.convertToAddress(context.getStrings(EmploymentSearchAttributes.HSA_STREET_ADDRESS.toString())));

    // Internadress e.g. BUP Elinsdahl, Bor�s
    employment.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings(EmploymentSearchAttributes.HSA_INTERNAL_ADRESS.toString())));

    // Postadress e.g. S�dra �lvsborgs Sjukhus$Barn- och ungdomspsykiatrisk ... $ $ $501 82$Bor�s
    employment.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings(EmploymentSearchAttributes.HSA_POSTAL_ADDRESS.toString())));

    // Leveransadress e.g. S�dra �lvsborgs Sjukhus$Barn- och... $Elinsdalsgatan 8$ $504 33$Bor�s
    employment.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings(EmploymentSearchAttributes.HSA_SEDF_DELIVERY_ADDRESS.toString())));

    // Faxnummer e.g. +46 33 6164930
    employment.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.FACSIMILE_TELEPHONE_NUMBER.toString())));

    // postnummer e.g. 416 73
    employment.setZipCode(new ZipCode(context.getString(EmploymentSearchAttributes.POSTAL_CODE.toString())));

    // hemsida e.g. http://www.vgregion.se/...
    employment.setLabeledUri(context.getString(EmploymentSearchAttributes.LABELED_URI.toString()));

    // e.g. 3
    employment.setVgrAnstform(context.getString(EmploymentSearchAttributes.VGR_ANST_FORM.toString()));

    // e.g. Psykolog,leg.
    employment.setTitle(context.getString(EmploymentSearchAttributes.EMPLOYMENT_TITLE.toString()));

    // e.g. 12
    employment.setVgrFormansgrupp(context.getString(EmploymentSearchAttributes.VGR_FORMANS_GRUPP.toString()));

    // V�xeltelefon e.g. +46 33 6161000
    employment.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.HSA_SEDF_SWITCHBOARD_TELEPHONE.toString())));

    employment.setVgrAO3kod(context.getString(EmploymentSearchAttributes.VGR_AO3_KOD.toString()));

    employment.setName(context.getString(EmploymentSearchAttributes.ORGANIZATIONAL_UNIT_NAME.toString()));

    employment.addHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(context.getStrings(EmploymentSearchAttributes.HSA_TELEPHONE_NUMBER.toString())));

    employment.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.HSA_PUBLIC_TELEPHONE_NUMBER.toString())));

    employment.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.MOBILE_TELEPHONE_NUMBER.toString())));

    employment.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.HSA_INTERNAL_PAGER_NUMBER.toString())));

    employment.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.PAGER_TELEPHONE_NUMBER.toString())));

    employment.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString(EmploymentSearchAttributes.HSA_TEXT_PHONE_NUMBER.toString())));

    employment.setModifyTimestamp(parseDateTime(context.getString(EmploymentSearchAttributes.MODIFY_TIMESTAMP.toString())));

    employment.setModifyersName(context.getString(EmploymentSearchAttributes.MODIFYERS_NAME.toString()));

    employment.addHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(context.getStrings(EmploymentSearchAttributes.HSA_TELEPHONE_TIME.toString())));

    employment.setDescription(context.getStrings(EmploymentSearchAttributes.DESCRIPTION.toString()));

    // Locality
    employment.setLocality(context.getString(EmploymentSearchAttributes.L.toString()));

    employment.setHsaManagerCode(context.getString(EmploymentSearchAttributes.HSA_MANAGER_CODE.toString()));

    String paTitleCode = context.getString(EmploymentSearchAttributes.PA_TITLE_CODE.toString());
    employment.setPosition(codeTablesService.getValueFromCode(CodeTableName.PA_TITLE_CODE, paTitleCode));
    
    employment.setVgrEmploymentDescriptionList(context.getStrings(EmploymentSearchAttributes.VGR_EMPLOYMENT_DESCRIPTION_LIST.toString()));
    
    employment.setVgrPrimaryEmpl(getPrimaryEmplText(context.getString(EmploymentSearchAttributes.VGR_PRIMARY_EMPL.toString())));
    
    return employment;
  }

  private TimePoint parseDateTime(final String dateTime) {
    TimePoint result = null;

    if (!StringUtil.isEmpty(dateTime)) {
      result = TimePoint.parseFrom(dateTime, "yyyyMMddHHmmss'Z'", TimeZone.getDefault());
    }

    return result;
  }
  
  private String getPrimaryEmplText(String primaryEmplValue){
	  String retVal = primaryEmplValue;
	  if (primaryEmplValue == null){
		  retVal = "";
	  } else if (primaryEmplValue.equals("PRIM")){
		 retVal = "Manuellt vald";
	  } else if (primaryEmplValue.endsWith("PRIA")){
		 retVal = "Automatiskt vald"; 
	  }
	  return retVal;
  }
  
  private String parseStrDate (String strDate){
    String pReturnStrDate =""; 
    String pStrDate = strDate;  
    if (!StringUtil.isEmpty( pStrDate )){
      pReturnStrDate =  pStrDate .substring(0,8);
            
    }
    return pReturnStrDate;
  }
}
