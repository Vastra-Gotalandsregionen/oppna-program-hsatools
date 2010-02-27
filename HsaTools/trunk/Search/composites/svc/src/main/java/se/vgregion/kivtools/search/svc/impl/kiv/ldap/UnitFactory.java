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

import geo.google.datamodel.GeoAltitude;
import geo.google.datamodel.GeoCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ldap.LdapORMHelper;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;
import com.novell.ldap.LDAPEntry;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
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

  /**
   * 
   * @param unitEntry LDAPEntry to reconstitute.
   * @return A unit object.
   * @throws KivException if the unit cannot be reconstituted.
   */
  public Unit reconstitute(LDAPEntry unitEntry) throws KivException {
    Unit unit = new Unit();
    if (unitEntry == null) {
      return unit;
    }

    populateUnitType(unitEntry, unit);
    populateUnitIdentities(unitEntry, unit);
    populateUnitName(unitEntry, unit);
    populateDescriptions(unitEntry, unit);
    populateWebsiteAndMailInformation(unitEntry, unit);
    populateAddressInformation(unitEntry, unit);
    populateMiscInformation(unitEntry, unit);
    populatePhoneInformation(unitEntry, unit);
    populateLocalityInformation(unitEntry, unit);
    populateVisitingInformation(unitEntry, unit);
    populateCreateModifyTimestamps(unitEntry, unit);
    populateGeoCoordinates(unitEntry, unit);

    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> healthcareTypes = htch.getHealthcareTypesForUnit(unit);
    unit.setHealthcareTypes(healthcareTypes);

    assignCodeTableValuesToUnit(unit);

    // Must be performed after healthcare types has been assigned.
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

  private void populateCreateModifyTimestamps(LDAPEntry unitEntry, Unit unit) {
    // Senast uppdaterad
    if (unitEntry.getAttribute("vgrModifyTimestamp") != null) {
      unit.setModifyTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrModifyTimestamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Skapad
    if (unitEntry.getAttribute("createTimeStamp") != null) {
      unit.setCreateTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("createTimeStamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
  }

  private void populateMiscInformation(LDAPEntry unitEntry, Unit unit) {
    // vgrCareType
    unit.setVgrCareType(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrCareType")));

    // vgrAO3kod
    unit.setVgrAO3kod(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrAO3kod")));

    // hsaBusinessClassificationCode
    unit.setHsaBusinessClassificationCode(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaBusinessClassificationCode")));

    // hsaEndDate
    unit.setHsaEndDate(TimeUtil.parseStringToZuluTime(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaEndDate"))));

    // hsaSurgeryHours
    unit.setHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaSurgeryHours"))));

    // hsaDropInHours
    unit.setHsaDropInHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaDropInHours"))));

    // hsaUnitPrescriptionCode
    unit.setHsaUnitPrescriptionCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaUnitPrescriptionCode")));

    // vgrAnsvarsnummer
    unit.setVgrAnsvarsnummer(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("vgrAnsvarsnummer")));

    // EDI-kod
    unit.setVgrEDICode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrEdiCode")));

    // EAN-kod
    unit.setVgrEANCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrEanCode")));

    // Ägarformkod
    unit.setHsaManagementCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaManagementCode")));

    // Temporary information
    unit.setVgrTempInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrTempInfo")));
    unit.setVgrRefInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrRefInfo")));

    // Drifts- & juridisk formkod
    unit.setHsaAdministrationForm(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaAdministrationForm")));

    unit.setContractCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrAvtalskod")));

    // Vårdval
    unit.setVgrVardVal(LdapParse.convertStringToBoolean(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrVardVal"))));
  }

  private void populateDescriptions(LDAPEntry unitEntry, Unit unit) {
    // description
    unit.setDescription(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("description")));

    // internal description
    unit.setInternalDescription(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("vgrInternalDescription")));
  }

  private void populateUnitName(LDAPEntry unitEntry, Unit unit) {
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
  }

  private void populateUnitIdentities(LDAPEntry unitEntry, Unit unit) {
    unit.setDn(DN.createDNFromString(unitEntry.getDN()));

    // OU
    unit.setOu(LdapORMHelper.getSingleValue(unitEntry.getAttribute(Constants.LDAP_PROPERTY_UNIT_NAME)));

    // hsaIdentity
    unit.setHsaIdentity(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaIdentity")));
  }

  private void populateUnitType(LDAPEntry unitEntry, Unit unit) throws KivException {
    // set object class
    unit.setObjectClass(LdapORMHelper.getSingleValue(unitEntry.getAttribute("objectClass")));
    String temp = unit.getObjectClass().toLowerCase();
    if (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_STANDARD)) {
      unit.setIsUnit(true);
    } else if (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_STANDARD)) {
      unit.setIsUnit(false);
    } else {
      throw new KivException("Detected unknown objectClass=" + unit.getObjectClass() + " in " + UnitFactory.class.getName() + "::reconstitute()");
    }
  }

  private void populateWebsiteAndMailInformation(LDAPEntry unitEntry, Unit unit) {
    // mail
    unit.setMail(LdapORMHelper.getSingleValue(unitEntry.getAttribute("mail")));

    // labeledURI
    String labeledURI = LdapORMHelper.getSingleValue(unitEntry.getAttribute("labeledUri"));
    labeledURI = fixURI(labeledURI);
    unit.setLabeledURI(labeledURI);

    String vgrLabeledURI = LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrLabeledURI"));
    vgrLabeledURI = fixURI(vgrLabeledURI);
    unit.setInternalWebsite(vgrLabeledURI);
  }

  private void populateVisitingInformation(LDAPEntry unitEntry, Unit unit) {
    // hsaVisitingHours
    unit.setVisitingHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaVisitingHours"))));

    // Visiting rules
    unit.setHsaVisitingRules(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRules")));

    // Visiting rule age
    unit.setHsaVisitingRuleAge(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRuleAge")));

    unit.setVisitingRuleReferral(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRuleReferral")));
  }

  private static void populateLocalityInformation(LDAPEntry unitEntry, Unit unit) {
    // l (=locality)
    unit.setLocality(LdapORMHelper.getSingleValue(unitEntry.getAttribute("l")));

    // Kommunkod
    unit.setHsaMunicipalityCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalityCode")));

    // Kommundelsnamn
    unit.setHsaMunicipalitySectionName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalitySectionName")));

    // Kommundelskod
    unit.setHsaMunicipalitySectionCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaMunicipalitySectionCode")));

    // Länskod
    unit.setHsaCountyCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaCountyCode")));

    // Länsnamn
    unit.setHsaCountyName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaCountyName")));
  }

  private static void populateGeoCoordinates(LDAPEntry unitEntry, Unit unit) {
    // Coordinates
    if (unitEntry.getAttribute("hsaGeographicalCoordinates") != null) {
      String hsaGeographicalCoordinates = LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaGeographicalCoordinates"));
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
        unit.setGeoCoordinate(new GeoCoordinate(wgs84Coords[1], wgs84Coords[0], new GeoAltitude()));
      }
    }
  }

  private static void populateAddressInformation(LDAPEntry unitEntry, Unit unit) {
    // vgrInternalSedfInvoiceAddress
    unit.setVgrInternalSedfInvoiceAddress(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrInternalSedfInvoiceAddress")));

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

    if (unitEntry.getAttribute("hsaRoute") != null) {
      unit.setHsaRoute(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaRoute")));
    }
  }

  private static void populatePhoneInformation(LDAPEntry unitEntry, Unit unit) {
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

    // hsaPublicTelephoneNumber
    unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaPublicTelephoneNumber"))));

    // hsaTelephoneTime
    unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaTelephoneTime"))));
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
}
