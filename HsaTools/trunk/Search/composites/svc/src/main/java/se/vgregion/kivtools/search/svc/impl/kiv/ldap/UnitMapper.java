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

public class UnitMapper implements ContextMapper {
  private CodeTablesService codeTablesService;
  private DisplayValueTranslator displayValueTranslator;

  public UnitMapper(CodeTablesService codeTablesService, DisplayValueTranslator displayValueTranslator) {
    this.codeTablesService = codeTablesService;
    this.displayValueTranslator = displayValueTranslator;
  }

  @Override
  public Unit mapFromContext(Object ctx) {
    Unit unit = new Unit();
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

    unit.setOu(context.getString(UnitLdapAttributes.OU));
    unit.setDn(DN.createDNFromString(context.getDnString()));
    unit.setHsaIdentity(context.getString(UnitLdapAttributes.HSA_IDENTITY));
    unit.setContractCode(context.getString(UnitLdapAttributes.VGR_AVTALSKOD));

    String timeStamp = context.getString(UnitLdapAttributes.CREATE_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setCreateTimestamp(TimePoint.parseFrom(context.getString(UnitLdapAttributes.CREATE_TIMESTAMP), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    timeStamp = context.getString(UnitLdapAttributes.vgrModifyTimestamp);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setModifyTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
    unit.setDescription(context.getStrings(UnitLdapAttributes.DESCRIPTION));
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.FACSIMILE_TELEPHONE_NUMBER)));

    // Set administration form text by using codeTableService
    String administrationCode = context.getString(UnitLdapAttributes.HSA_ADMINISTRATION_FORM);
    unit.setHsaAdministrationForm(administrationCode);
    String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, administrationCode);
    unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);

    unit.setHsaBusinessClassificationCode(context.getStrings(UnitLdapAttributes.HSA_BUSINESS_CLASSIFICATION_CODE));

    // Set Business classification code text values
    List<String> businessText = new ArrayList<String>();
    for (String businessCode : unit.getHsaBusinessClassificationCode()) {
      String hsaBusinessClassificationText = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
      businessText.add(hsaBusinessClassificationText);
    }
    unit.setHsaBusinessClassificationText(businessText);

    unit.setHsaConsigneeAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_CONSIGNEE_ADDRESS)));
    unit.setHsaCountyCode(context.getString(UnitLdapAttributes.HSA_COUNTY_CODE));
    unit.setHsaCountyName(context.getString(UnitLdapAttributes.HSA_COUNTY_NAME));
    unit.setHsaDropInHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.HSA_DROPIN_HOURS)));
    unit.setHsaEndDate(TimeUtil.parseStringToZuluTime(context.getString(UnitLdapAttributes.HSA_END_DATE)));
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_INTERNAL_ADDRESS)));
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));

    unit.setHsaManagementCode(context.getString(UnitLdapAttributes.HSA_MANAGEMENT_CODE));
    unit.setHsaManagementText(displayValueTranslator.translateManagementCode(unit.getHsaManagementCode()));

    unit.setHsaMunicipalityCode(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_CODE));

    String municipalityName = codeTablesService.getValueFromCode(CodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityName(municipalityName);
    unit.setHsaMunicipalitySectionCode(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_CODE));
    unit.setHsaMunicipalitySectionName(context.getString(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_NAME));
    unit.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.HSA_POSTAL_ADDRESS)));
    unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(context.getStrings(UnitLdapAttributes.hsaPublicTelephoneNumber)));
    unit.setHsaRoute(context.getStrings(UnitLdapAttributes.hsaRoute));
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.hsaSedfDeliveryAddress)));
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings(UnitLdapAttributes.hsaSedfInvoiceAddress)));
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.hsaSedfSwitchboardTelephoneNo)));
    unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.hsaSmsTelephoneNumber)));
    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(context.getStrings(UnitLdapAttributes.hsaStreetAddress)));
    unit.setHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.hsaSurgeryHours)));
    unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(context.getStrings(UnitLdapAttributes.hsaTelephoneNumber)));
    unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.hsaTelephoneTime)));
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.hsaTextPhoneNumber)));
    unit.setHsaUnitPrescriptionCode(context.getString(UnitLdapAttributes.hsaUnitPrescriptionCode));
    unit.setHsaVisitingRuleAge(context.getString(UnitLdapAttributes.hsaVisitingRuleAge));
    unit.setHsaVisitingRules(context.getString(UnitLdapAttributes.hsaVisitingRules));
    unit.setInternalDescription(context.getStrings(UnitLdapAttributes.VGR_INTERNAL_DESCRIPTION));
    // unit.setIsUnit(isUnit);

    String labeledURI = context.getString(UnitLdapAttributes.LABELED_URI);
    labeledURI = fixURI(labeledURI);
    unit.setLabeledURI(labeledURI);

    String vgrLabeledURI = context.getString(UnitLdapAttributes.vgrLabeledURI);
    vgrLabeledURI = fixURI(vgrLabeledURI);
    unit.setInternalWebsite(vgrLabeledURI);

    unit.setLocality(context.getString(UnitLdapAttributes.L));
    unit.setMail(context.getString(UnitLdapAttributes.MAIL));
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.mobileTelephoneNumber)));
    // TODO: check if this is needed
    String unitName = Formatter.replaceStringInString(context.getString(UnitLdapAttributes.OU), "\\,", ",");
    unit.setName(unitName.trim());
    unit.setName(unitName);
    unit.setObjectClass(context.getString(UnitLdapAttributes.OBJECT_CLASS));
    unit.setIsUnit(isUnitType(context.getString(UnitLdapAttributes.OBJECT_CLASS)));
    unit.setInternalDescription(context.getStrings(UnitLdapAttributes.VGR_INTERNAL_DESCRIPTION));

    unit.setName(getUnitName(context));
    unit.setOrganizationalUnitNameShort(context.getString(UnitLdapAttributes.ORGANIZATIONAL_UNITNAME_SHORT));
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));
    populateGeoCoordinates(context, unit);
    unit.setVgrAnsvarsnummer(context.getStrings(UnitLdapAttributes.vgrAnsvarsnummer));
    unit.setVgrAO3kod(context.getString(UnitLdapAttributes.vgrAO3kod));

    String vgrAO3Text = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, context.getString(UnitLdapAttributes.vgrAO3kod));
    unit.setVgrAO3kodText(vgrAO3Text);

    unit.setVgrCareType(context.getString(UnitLdapAttributes.vgrCareType));
    String vgrCareTypeText = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, UnitLdapAttributes.vgrCareType);
    unit.setVgrCareTypeText(vgrCareTypeText);
    unit.setVgrEANCode(context.getString(UnitLdapAttributes.vgrEanCode));
    unit.setVgrEDICode(context.getString(UnitLdapAttributes.vgrEdiCode));
    unit.setVgrInternalSedfInvoiceAddress(context.getString(UnitLdapAttributes.VGR_INTERNAL_SEDF_INVOICE_ADDRESS));
    unit.setVgrTempInfo(context.getString(UnitLdapAttributes.vgrTempInfo));
    unit.setVgrRefInfo(context.getString(UnitLdapAttributes.vgrRefInfo));
    unit.setVgrVardVal(Boolean.getBoolean(context.getString(UnitLdapAttributes.vgrVardVal)));
    unit.setVisitingHours(WeekdayTime.createWeekdayTimeList(context.getStrings(UnitLdapAttributes.hsaVisitingHours)));
    unit.setVisitingRuleReferral(context.getString(UnitLdapAttributes.hsaVisitingRuleReferral));

    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    htch.assignHealthcareTypes(unit);
    populateShowVisitingRulesAndAgeInterval(unit);
    return unit;
  }

  private void populateShowVisitingRulesAndAgeInterval(Unit unit) {
    // Rule for showing visiting rules
    boolean show = false;
    show |= unit.hasHealthcareType("Barnavårdscentral");
    show |= unit.hasHealthcareType("Vårdcentral");
    show |= unit.hasHealthcareType("Jourcentral");
    unit.setShowVisitingRules(show);
    // VGR has the same rule for age interval
    unit.setShowAgeInterval(show);
  }

  private static void populateGeoCoordinates(DirContextOperationsHelper context, Unit unit) {
    // Coordinates
    if (context.getString(UnitLdapAttributes.hsaGeographicalCoordinates) != null) {
      String hsaGeographicalCoordinates = context.getString(UnitLdapAttributes.hsaGeographicalCoordinates);
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

  private String getUnitName(DirContextOperationsHelper context) {
    String name = context.getString("ou");
    // Is a function, name is the cn attribute instead.
    if (StringUtil.isEmpty(name)) {
      name = context.getString("cn");
    }
    return name;
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
}
