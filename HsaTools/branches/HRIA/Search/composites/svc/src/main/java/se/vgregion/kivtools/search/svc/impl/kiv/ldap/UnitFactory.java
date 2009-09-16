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
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.ldap.LdapORMHelper;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;

import com.domainlanguage.time.TimePoint;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class UnitFactory {

  private CodeTablesService codeTablesService;
  private DisplayValueTranslator displayValueTranslator;

  /**
   * Set CodeTablesService object for UnitFactory to use.
   * 
   * @param codeTablesService - CodeTablesService
   */
  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  /**
   * Setter for the displayValueTranslator to use.
   * 
   * @param displayValueTranslator The DisplayValueTranslator to use.
   */
  public void setDisplayValueTranslator(DisplayValueTranslator displayValueTranslator) {
    this.displayValueTranslator = displayValueTranslator;
  }

  public Unit reconstitute(LDAPEntry unitEntry) throws Exception {
    Unit unit = new Unit();
    if (unitEntry == null) {
      return unit;
    }

    // set object class
    unit.setObjectClass(LdapORMHelper.getSingleValue(unitEntry.getAttribute("objectClass")));
    String temp = unit.getObjectClass().toLowerCase();
    if (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_STANDARD)) {
      unit.setIsUnit(true);
    } else if (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_STANDARD)) {
      unit.setIsUnit(false);
    } else {
      Exception e = new Exception("Detected unknown objectClass=" + unit.getObjectClass() + " in " + UnitFactory.class.getName() + "::reconstitute()");
    }

    unit.setDn(DN.createDNFromString(unitEntry.getDN()));

    // OU
    unit.setOu(LdapORMHelper.getSingleValue(unitEntry.getAttribute(Constants.LDAP_PROPERTY_UNIT_NAME)));

    // hsaIdentity
    unit.setHsaIdentity(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaIdentity")));

    // Name
    if (unit.getIsUnit()) {
      // Is Unit
      // change \, to ,
      String unitName = Formatter.replaceStringInString(unit.getOu(), "\\,", ",");
      unit.setName(unitName.trim());
    } else {
      // Is Function
      String cn = LdapORMHelper.getSingleValue(unitEntry.getAttribute(Constants.LDAP_PROPERTY_FUNCTION_NAME));
      // change \, to ,
      cn = Formatter.replaceStringInString(cn, "\\,", ",");
      unit.setName(cn.trim());
    }

    // organizationalUnitNameShort
    unit.setOrganizationalUnitNameShort(LdapORMHelper.getSingleValue(unitEntry.getAttribute("organizationalUnitNameShort")));

    // description
    unit.setDescription(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("description")));

    // internal description
    unit.setInternalDescription(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("vgrInternalDescription")));

    // mail
    unit.setMail(LdapORMHelper.getSingleValue(unitEntry.getAttribute("mail")));

    // l (=locality)
    unit.setLocality(LdapORMHelper.getSingleValue(unitEntry.getAttribute("l")));

    // labeledURI
    unit.setLabeledURI(LdapORMHelper.getSingleValue(unitEntry.getAttribute("labeledUri")));

    // vgrInternalSedfInvoiceAddress
    unit.setVgrInternalSedfInvoiceAddress(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrInternalSedfInvoiceAddress")));

    // vgrCareType
    unit.setVgrCareType(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrCareType")));

    // vgrAO3kod
    unit.setVgrAO3kod(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrAO3kod")));

    // hsaBusinessClassificationCode
    unit.setHsaBusinessClassificationCode(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaBusinessClassificationCode")));

    // hsaTextPhoneNumber
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaTextPhoneNumber"))));

    // hsaTextPhoneNumber
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaTextPhoneNumber"))));

    // mobileTelephoneNumber
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("mobileTelephoneNumber"))));

    // hsaSedfSwitchboardTelephoneNo
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaSedfSwitchboardTelephoneNo"))));

    // hsaTelephoneNumber
    unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaTelephoneNumber"))));

    // hsaSmsTelephoneNumber
    unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaSmsTelephoneNumber"))));

    // facsimileTelephoneNumber
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("facsimileTelephoneNumber"))));

    // pagerTelephoneNumber
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("pagerTelephoneNumber"))));

    // hsaTelephoneNumber
    unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaTelephoneNumber"))));

    // hsaPublicTelephoneNumber
    unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaPublicTelephoneNumber"))));

    // hsaTelephoneTime
    unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaTelephoneTime"))));

    // hsaEndDate
    unit.setHsaEndDate(Constants.parseStringToZuluTime(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaEndDate"))));

    // hsaSurgeryHours
    unit.setHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaSurgeryHours"))));

    // hsaDropInHours
    unit.setHsaDropInHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaDropInHours"))));

    // hsaInternalAddress
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaInternalAddress"))));

    // hsaStreetAddress
    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaStreetAddress"))));

    // hsaPostalAddress
    unit.setHsaPostalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaPostalAddress"))));

    // hsaSedfDeliveryAddress
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaSedfDeliveryAddress"))));

    // hsaSedfInvoiceAddress
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaSedfInvoiceAddress"))));

    // hsaUnitPrescriptionCode
    unit.setHsaUnitPrescriptionCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaUnitPrescriptionCode")));

    // vgrAnsvarsnummer
    unit.setVgrAnsvarsnummer(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("vgrAnsvarsnummer")));

    // Kommunkod
    unit.setHsaMunicipalityCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalityCode")));

    // EDI-kod
    unit.setVgrEDICode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrEdiCode")));

    // EAN-kod
    unit.setVgrEANCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrEanCode")));

    // Kommundelsnamn
    unit.setHsaMunicipalitySectionName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalitySectionName")));

    // Kommundelskod
    unit.setHsaMunicipalitySectionCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalitySectionCode")));

    // Länskod
    unit.setHsaCountyCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaCountyCode")));

    // Länsnamn
    unit.setHsaCountyName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaCountyName")));

    // Ägarformkod
    unit.setHsaManagementCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaManagementCode")));

    // Visiting rules
    unit.setHsaVisitingRules(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRules")));

    // Visiting rule age
    unit.setHsaVisitingRuleAge(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRuleAge")));

    // Temporary information
    unit.setVgrTempInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrTempInfo")));
    unit.setVgrRefInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrRefInfo")));

    // Drifts- & juridisk formkod
    unit.setHsaAdministrationForm(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaAdministrationForm")));

    // Senast uppdaterad
    if (unitEntry.getAttribute("vgrModifyTimestamp") != null) {
      unit.setModifyTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrModifyTimestamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Skapad
    if (unitEntry.getAttribute("createTimeStamp") != null) {
      unit.setCreateTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("createTimeStamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Coordinates
    if (unitEntry.getAttribute("hsaGeographicalCoordinates") != null) {
      String hsaGeographicalCoordinates = LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaGeographicalCoordinates"));
      unit.setHsaGeographicalCoordinates(hsaGeographicalCoordinates);
      // Parse and set in RT90 format
      int[] rt90Coords = GeoUtil.parseRT90HsaString(hsaGeographicalCoordinates);
      if (rt90Coords != null && rt90Coords.length == 2) {
        unit.setRt90X(rt90Coords[0]);
        unit.setRt90Y(rt90Coords[1]);

        // Convert to WGS84 and set on unit too
        CoordinateTransformerService gkp = new GaussKrugerProjection("2.5V");
        double[] wgs84Coords = gkp.getWGS84(rt90Coords[0], rt90Coords[1]);

        if (wgs84Coords != null && wgs84Coords.length == 2) {
          unit.setWgs84Lat(wgs84Coords[0]);
          unit.setWgs84Long(wgs84Coords[1]);
        }
      }
    }

    if (unitEntry.getAttribute("hsaRoute") != null) {
      unit.setHsaRoute(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaRoute")));
    }

    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    htch.assignHealthcareTypes(unit);

    // Vårdval
    unit.setVgrVardVal(LdapParse.convertStringToBoolean(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrVardVal"))));
    assignCodeTableValuesToUnit(unit);
    return unit;
  }

  /**
   * Uses code table service in order to lookup text value for "coded values".
   * 
   * @param unit
   */
  private void assignCodeTableValuesToUnit(Unit unit) {
    List<String> businessText = new ArrayList<String>();
    for (String businessCode : unit.getHsaBusinessClassificationCode()) {
      String hsaBusinessClassificationText = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
      businessText.add(hsaBusinessClassificationText);
    }
    unit.setHsaBusinessClassificationText(businessText);
    String hsaManagementText = displayValueTranslator.translateManagementCode(unit.getHsaManagementCode());
    unit.setHsaManagementText(hsaManagementText);
    String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, unit.getHsaAdministrationForm());
    unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);
    String vgrAO3Text = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, unit.getVgrAO3kod());
    unit.setVgrAO3kodText(vgrAO3Text);
    String vgrCareTypeText = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, unit.getVgrCareType());
    unit.setVgrCareTypeText(vgrCareTypeText);
    String municipalityName = codeTablesService.getValueFromCode(CodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityName(municipalityName);
  }

  public void printAllAttributes(LDAPEntry entry) {
    LDAPAttributeSet mySet = entry.getAttributeSet();
    for (Iterator<LDAPAttribute> myIterator = mySet.iterator(); myIterator.hasNext();) {
      LDAPAttribute attr = myIterator.next();
      System.out.println("attr=" + attr.getName() + ", value=" + attr.getStringValue());
    }
    System.out.println("*******************");
  }
}
