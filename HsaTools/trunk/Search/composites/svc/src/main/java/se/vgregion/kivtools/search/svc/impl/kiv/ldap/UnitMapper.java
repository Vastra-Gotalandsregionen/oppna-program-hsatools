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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Context mapper for unit entries.
 */
public class UnitMapper implements ContextMapper {
  private CodeTablesService codeTablesService;
  private DisplayValueTranslator displayValueTranslator;

  /**
   * Constructs a new UnitMapper.
   * 
   * @param codeTablesService The CodeTablesService to use.
   * @param displayValueTranslator The DisplayValueTranslator to use.
   */
  public UnitMapper(CodeTablesService codeTablesService, DisplayValueTranslator displayValueTranslator) {
    this.codeTablesService = codeTablesService;
    this.displayValueTranslator = displayValueTranslator;
  }

  @Override
  public Unit mapFromContext(Object ctx) {
    Unit unit = new Unit();
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

    unit.setOu(context.getString(UnitLdapAttributes.OU));
    unit.setDn(DN.createDNFromString(context.getDnString()).escape());
    unit.setHsaIdentity(context.getString(UnitLdapAttributes.HSA_IDENTITY));
    unit.setContractCode(context.getString(UnitLdapAttributes.VGR_AVTALSKOD));

    String timeStamp = context.getString(UnitLdapAttributes.CREATE_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setCreateTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    timeStamp = context.getString(UnitLdapAttributes.VGR_MODIFY_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setModifyTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
    unit.addDescription(context.getStrings(UnitLdapAttributes.DESCRIPTION));
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.FACSIMILE_TELEPHONE_NUMBER)));
    unit.setHsaConsigneeAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_CONSIGNEE_ADDRESS)));
    unit.setHsaCountyCode(context.getString(UnitLdapAttributes.HSA_COUNTY_CODE));
    unit.setHsaCountyName(context.getString(UnitLdapAttributes.HSA_COUNTY_NAME));
    unit.addHsaDropInHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.HSA_DROPIN_HOURS)));
    unit.setHsaEndDate(TimeUtil.parseStringToZuluTime(context.getString(UnitLdapAttributes.HSA_END_DATE)));
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_INTERNAL_ADDRESS)));
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));
    unit.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_POSTAL_ADDRESS)));
    List<PhoneNumber> hsaPublicTelephoneNumbers = PhoneNumber.createPhoneNumberList(context.getStrings(UnitLdapAttributes.HSA_PUBLIC_TELEPHONE_NUMBER));
    for (PhoneNumber hsaPublicTelephoneNumber : hsaPublicTelephoneNumbers) {
      unit.addHsaPublicTelephoneNumber(hsaPublicTelephoneNumber);
    }
    unit.addHsaRoute(context.getStrings(UnitLdapAttributes.HSA_ROUTE));
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_SEDF_DELIVERY_ADDRESS)));
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_SEDF_INVOICE_ADDRESS)));
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.HSA_SEDF_SWITCHBOARD_TELEPHONE_NO)));
    unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.HSA_SMS_TELEPHONE_NUMBER)));
    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(context.getStrings(UnitLdapAttributes.HSA_STREET_ADDRESS)));
    unit.addHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.HSA_SURGERY_HOURS)));
    unit.addHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(context.getStrings(UnitLdapAttributes.HSA_TELEPHONE_NUMBER)));
    unit.addHsaTelephoneTimes(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.HSA_TELEPHONE_TIME)));
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.HSA_TEXT_PHONE_NUMBER)));
    unit.setHsaUnitPrescriptionCode(context.getString(UnitLdapAttributes.HSA_UNIT_PRESCRIPTION_CODE));
    unit.setHsaVisitingRuleAge(context.getString(UnitLdapAttributes.HSA_VISITING_RULE_AGE));
    unit.setHsaVisitingRules(context.getString(UnitLdapAttributes.HSA_VISITING_RULES));
    unit.setHsaPatientVisitingRules(context.getString(UnitLdapAttributes.HSA_PATIENT_VISITING_RULES));
    unit.addInternalDescription(context.getStrings(UnitLdapAttributes.VGR_INTERNAL_DESCRIPTION));
    unit.setIsUnit(isUnitType(context.getString(UnitLdapAttributes.OBJECT_CLASS)));

    String labeledURI = context.getString(UnitLdapAttributes.LABELED_URI);
    labeledURI = fixURI(labeledURI);
    unit.setLabeledURI(labeledURI);

    String vgrLabeledURI = context.getString(UnitLdapAttributes.VGR_LABELED_URI);
    vgrLabeledURI = fixURI(vgrLabeledURI);
    unit.setInternalWebsite(vgrLabeledURI);

    unit.setLocality(context.getString(UnitLdapAttributes.L));
    unit.setMail(context.getString(UnitLdapAttributes.MAIL));
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.MOBILE_TELEPHONE_NUMBER)));

    populateUnitName(unit, context);
    unit.setObjectClass(context.getString(UnitLdapAttributes.OBJECT_CLASS));
    unit.setIsUnit(isUnitType(context.getString(UnitLdapAttributes.OBJECT_CLASS)));

    unit.setOrganizationalUnitNameShort(context.getString(UnitLdapAttributes.ORGANIZATIONAL_UNITNAME_SHORT));
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));
    populateGeoCoordinates(context, unit);
    unit.setVgrAnsvarsnummer(context.getStrings(UnitLdapAttributes.VGR_ANSVARSNUMMER));
    unit.setVgrEANCode(context.getString(UnitLdapAttributes.VGR_EAN_CODE));
    unit.setVgrEDICode(context.getString(UnitLdapAttributes.VGR_EDI_CODE));
    unit.setVgrInternalSedfInvoiceAddress(context.getString(UnitLdapAttributes.VGR_INTERNAL_SEDF_INVOICE_ADDRESS));
    unit.setVgrTempInfo(context.getString(UnitLdapAttributes.VGR_TEMP_INFO));
    unit.setVgrRefInfo(context.getString(UnitLdapAttributes.VGR_REF_INFO));
    unit.setVgrVardVal("J".equalsIgnoreCase(context.getString(UnitLdapAttributes.VGR_VARDVAL)));
    unit.setVisitingHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.HSA_VISITING_HOURS)));
    unit.setVisitingRuleReferral(context.getString(UnitLdapAttributes.HSA_VISITING_RULE_REFERRAL));

    List<String> indicators = context.getStrings(UnitLdapAttributes.HSA_DESTINATION_INDICATOR);
    for (String indicator : indicators) {
      unit.addHsaDestinationIndicator(indicator);
    }

    unit.setHsaBusinessType(context.getString(UnitLdapAttributes.HSA_BUSINESS_TYPE));

    assignCodeTableValuesToUnit(unit, context);
    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> healthcareTypes = htch.getHealthcareTypesForUnit(unit);
    unit.addHealthcareTypes(healthcareTypes);
    // Visiting rules and age interval should be shown at all times
    unit.setShowVisitingRules(true);
    unit.setShowAgeInterval(true);

    List<String> vgrObjectManagers = context.getStrings(UnitLdapAttributes.VGR_OBJECT_MANAGERS);
    if (vgrObjectManagers != null && vgrObjectManagers.size() > 0) {
      unit.setVgrObjectManagers(vgrObjectManagers);
    }
    return unit;
  }

  private static void populateGeoCoordinates(DirContextOperationsHelper context, Unit unit) {
    // Coordinates
    if (context.getString(UnitLdapAttributes.HSA_GEOGRAPHICAL_COORDINATES) != null) {
      String hsaGeographicalCoordinates = context.getString(UnitLdapAttributes.HSA_GEOGRAPHICAL_COORDINATES);
      unit.setHsaGeographicalCoordinates(hsaGeographicalCoordinates);
      // Parse and set in RT90 format
      int[] rt90Coords = GeoUtil.parseRT90HsaString(hsaGeographicalCoordinates);
      if (rt90Coords != null) {
        unit.setRt90X(rt90Coords[0]);
        unit.setRt90Y(rt90Coords[1]);

        // Convert to WGS84 and set on unit too
        CoordinateTransformerService gkp = new GaussKrugerProjection("2.5V");
        double[] wgs84Coords = gkp.getWGS84(rt90Coords[0], rt90Coords[1]);

        unit.setWgs84Lat(wgs84Coords[0]);
        unit.setWgs84Long(wgs84Coords[1]);
      }
    }
  }

  private void populateUnitName(Unit unit, DirContextOperationsHelper context) {
    // Name
    if (isUnitType(context.getString(UnitLdapAttributes.OBJECT_CLASS))) {
      // Is Unit
      // change \, to ,
      String ou = context.getString(UnitLdapAttributes.OU);
      String unitName = Formatter.replaceStringInString(ou, "\\,", ",");
      unit.setName(unitName.trim());
    } else {
      // Is Function
      String cn = context.getString(UnitLdapAttributes.CN);
      // change \, to ,
      cn = Formatter.replaceStringInString(cn, "\\,", ",");
      unit.setName(cn.trim());
    }
  }

  private boolean isUnitType(String objectClass) {
    boolean isUnitType = false;
    if (objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_SPECIFIC) || objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_STANDARD)) {
      isUnitType = true;
    } else if (objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_SPECIFIC) || objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_STANDARD)) {
      isUnitType = false;
    }
    return isUnitType;
  }

  /**
   * Prepends "http://" if the provided URI isn't a correct URI.
   * 
   * @param uri The URI to fix.
   * @return The provided URI if already correct, otherwise the provided URI with "http://" prepended.
   */
  private String fixURI(String uri) {
    String fixedUri = uri;
    // Do some simple validation/fixing
    if (!StringUtil.isEmpty(uri) && !(uri.startsWith("http://") || uri.startsWith("https://"))) {
      fixedUri = "http://" + uri;
    }

    return fixedUri;
  }

  /**
   * Uses code table service in order to lookup text value for "coded values".
   * 
   * @param unit
   */
  private void assignCodeTableValuesToUnit(Unit unit, DirContextOperationsHelper context) {

    unit.setHsaBusinessClassificationCode(context.getStrings(UnitLdapAttributes.HSA_BUSINESS_CLASSIFICATION_CODE));

    List<String> businessText = new ArrayList<String>();
    for (String businessCode : unit.getHsaBusinessClassificationCode()) {
      String hsaBusinessClassificationText = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
      businessText.add(hsaBusinessClassificationText);
    }
    unit.setHsaBusinessClassificationText(businessText);

    String hsaManagementText = displayValueTranslator.translateManagementCode(unit.getHsaManagementCode());
    unit.setHsaManagementText(hsaManagementText);

    String administrationCode = context.getString(UnitLdapAttributes.HSA_ADMINISTRATION_FORM);
    unit.setHsaAdministrationForm(administrationCode);
    String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, administrationCode);
    unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);

    unit.setHsaManagementCode(context.getString(UnitLdapAttributes.HSA_MANAGEMENT_CODE));
    unit.setHsaManagementText(displayValueTranslator.translateManagementCode(unit.getHsaManagementCode()));

    unit.setVgrAO3kod(context.getString(UnitLdapAttributes.VGR_AO3_KOD));
    String vgrAO3Text = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, unit.getVgrAO3kod());
    unit.setVgrAO3kodText(vgrAO3Text);

    unit.setCareType(context.getString(UnitLdapAttributes.VGR_CARE_TYPE));
    String careTypeText = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, unit.getCareType());
    unit.setCareTypeText(careTypeText);

    unit.setHsaMunicipalityCode(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_CODE));
    String municipalityName = codeTablesService.getValueFromCode(CodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityName(municipalityName);

    unit.setHsaMunicipalitySectionCode(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_CODE));
    unit.setHsaMunicipalitySectionName(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_NAME));
  }
}
