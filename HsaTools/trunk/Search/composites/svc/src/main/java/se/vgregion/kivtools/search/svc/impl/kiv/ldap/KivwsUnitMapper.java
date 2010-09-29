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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.ldap.core.ContextMapper;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.KivwsCodeTableName;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Mapp kivws response object to Unit.
 * 
 * @author david bennehult
 * 
 */
public class KivwsUnitMapper implements ContextMapper {

  private CodeTablesService codeTablesService;
  private DisplayValueTranslator displayValueTranslator;
  private Map<String, List<Object>> ldapAttributes;

  /**
   * Constructs a new UnitMapper.
   * 
   * @param codeTablesService The CodeTablesService to use.
   * @param displayValueTranslator The DisplayValueTranslator to use.
   */
  public KivwsUnitMapper(CodeTablesService codeTablesService, DisplayValueTranslator displayValueTranslator) {
    this.codeTablesService = codeTablesService;
    this.displayValueTranslator = displayValueTranslator;
  }

  @Override
  public Unit mapFromContext(Object ctx) {
    Unit unit = new Unit();
    ldapAttributes = new HashMap<String, List<Object>>();
    List<Entry> attributes = null;
    if (ctx instanceof Function) {
      Function kivwsFunction = (Function) ctx;
      unit.setDn(DN.createDNFromString(kivwsFunction.getDn().getValue()).escape());
      attributes = kivwsFunction.getAttributes().getValue().getEntry();
    } else if (ctx instanceof se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit) {
      se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit kivwsUnit = (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit) ctx;
      unit.setDn(DN.createDNFromString(kivwsUnit.getDn().getValue()).escape());
      attributes = kivwsUnit.getAttributes().getValue().getEntry();
    } else {
      throw new RuntimeException("Object is not a type of Function or Unit");
    }

    for (Entry entry : attributes) {
      ldapAttributes.put(entry.getKey(), entry.getValue().getAnyType());
    }

    unit.setOu((String) getSingleValue(KivwsAttributes.OU));

    unit.setHsaIdentity((String) getSingleValue(KivwsAttributes.HSA_IDENTITY));
    unit.setContractCode((String) getSingleValue(KivwsAttributes.VGR_AVTALSKOD));

    String timeStamp = (String) getSingleValue(KivwsAttributes.CREATE_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setCreateTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    timeStamp = (String) getSingleValue(KivwsAttributes.VGR_MODIFY_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setModifyTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
    unit.addDescription((List<String>) getMultiValue(KivwsAttributes.DESCRIPTION));
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.FACSIMILE_TELEPHONE_NUMBER)));
    unit.setHsaCountyCode((String) getSingleValue(KivwsAttributes.HSA_COUNTY_CODE));
    unit.addHsaDropInHours(WeekdayTime.createWeekdayTimeList((List<String>) getMultiValue(KivwsAttributes.HSA_DROPIN_HOURS)));
    unit.setHsaEndDate(TimeUtil.parseStringToZuluTime((String) getSingleValue(KivwsAttributes.HSA_END_DATE)));
    unit.setHsaInternalAddress(AddressHelper.convertToAddress((List<String>) getMultiValue(KivwsAttributes.HSA_INTERNAL_ADDRESS)));
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.PAGER_TELEPHONE_NUMBER)));
    unit.setHsaPostalAddress(AddressHelper.convertToAddress((List<String>) getMultiValue(KivwsAttributes.HSA_POSTAL_ADDRESS)));
    List<PhoneNumber> createPhoneNumberList = PhoneNumber.createPhoneNumberList((List<String>) getMultiValue(KivwsAttributes.HSA_PUBLIC_TELEPHONE_NUMBER));
    for (PhoneNumber phoneNumber : createPhoneNumberList) {
      unit.addHsaPublicTelephoneNumber(phoneNumber);
    }
    unit.addHsaRoute((List<String>) getMultiValue(KivwsAttributes.HSA_ROUTE));
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress((List<String>) getMultiValue(KivwsAttributes.HSA_SEDF_DELIVERY_ADDRESS)));
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(getAddressValue(KivwsAttributes.HSA_SEDF_INVOICE_ADDRESS)));
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.HSA_SEDF_SWITCHBOARD_TELEPHONE_NO)));
    unit.setHsaStreetAddress(AddressHelper.convertToAddress(getMultiValue(KivwsAttributes.HSA_STREET_ADDRESS)));
    unit.addHsaSurgeryHours(WeekdayTime.createWeekdayTimeList((List<String>) getMultiValue(KivwsAttributes.HSA_SURGERY_HOURS)));
    unit.addHsaTelephoneNumber(PhoneNumber.createPhoneNumberList((List<String>) getMultiValue(KivwsAttributes.HSA_TELEPHONE_NUMBER)));
    unit.addHsaTelephoneTimes(WeekdayTime.createWeekdayTimeList((List<String>) getMultiValue(KivwsAttributes.HSA_TELEPHONE_TIME)));
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.HSA_TEXT_PHONE_NUMBER)));
    unit.setHsaUnitPrescriptionCode((String) getSingleValue(KivwsAttributes.HSA_UNIT_PRESCRIPTION_CODE));
    unit.setHsaVisitingRuleAge((String) getSingleValue(KivwsAttributes.HSA_VISITING_RULE_AGE));
    unit.setHsaVisitingRules((String) getSingleValue(KivwsAttributes.HSA_VISITING_RULES));
    unit.setHsaPatientVisitingRules((String) getSingleValue(KivwsAttributes.HSA_PATIENT_VISITING_RULES));
    unit.addInternalDescription((List<String>) getMultiValue(KivwsAttributes.VGR_INTERNAL_DESCRIPTION));
    unit.setIsUnit((ctx instanceof Unit));

    String labeledURI = (String) getSingleValue(KivwsAttributes.LABELED_URI);
    labeledURI = fixURI(labeledURI);
    unit.setLabeledURI(labeledURI);

    String vgrLabeledURI = (String) getSingleValue(KivwsAttributes.VGR_LABELED_URI);
    vgrLabeledURI = fixURI(vgrLabeledURI);
    unit.setInternalWebsite(vgrLabeledURI);

    unit.setLocality((String) getSingleValue(KivwsAttributes.L));
    unit.setMail((String) getSingleValue(KivwsAttributes.MAIL));
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.MOBILE_TELEPHONE_NUMBER)));

    populateUnitName(unit);
    unit.setObjectClass((String) getSingleValue(KivwsAttributes.OBJECT_CLASS));

    unit.setOrganizationalUnitNameShort((String) getSingleValue(KivwsAttributes.ORGANIZATIONAL_UNITNAME_SHORT));
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber((String) getSingleValue(KivwsAttributes.PAGER_TELEPHONE_NUMBER)));
    populateGeoCoordinates(unit);
    unit.setVgrAnsvarsnummer((List<String>) getMultiValue(KivwsAttributes.VGR_ANSVARSNUMMER));
    unit.setVgrInternalSedfInvoiceAddress((String) getSingleValue(KivwsAttributes.VGR_INTERNAL_SEDF_INVOICE_ADDRESS));
    unit.setVgrTempInfo((String) getSingleValue(KivwsAttributes.VGR_TEMP_INFO));
    unit.setVgrRefInfo((String) getSingleValue(KivwsAttributes.VGR_REF_INFO));
    unit.setVgrVardVal("J".equalsIgnoreCase((String) getSingleValue(KivwsAttributes.VGR_VARDVAL)));
    unit.setVisitingHours(WeekdayTime.createWeekdayTimeList((List<String>) getMultiValue(KivwsAttributes.HSA_VISITING_HOURS)));
    unit.setVisitingRuleReferral((String) getSingleValue(KivwsAttributes.HSA_VISITING_RULE_REFERRAL));

    List<String> indicators = (List<String>) getMultiValue(KivwsAttributes.HSA_DESTINATION_INDICATOR);
    for (String indicator : indicators) {
      unit.addHsaDestinationIndicator(indicator);
    }

    unit.setHsaBusinessType((String) getSingleValue(KivwsAttributes.HSA_BUSINESS_TYPE));

    assignCodeTableValuesToUnit(unit);
    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> healthcareTypes = htch.getHealthcareTypesForUnit(unit);
    unit.addHealthcareTypes(healthcareTypes);
    // Visiting rules and age interval should be shown at all times
    unit.setShowVisitingRules(true);
    unit.setShowAgeInterval(true);
    return unit;
  }

  private String getSingleValue(String key) {
    String returnValue = "";
    if (ldapAttributes.containsKey(key)) {
      returnValue = (String) ldapAttributes.get(key).get(0);
    }
    return returnValue;
  }

  private List<String> getMultiValue(String key) {
    List<String> returnValue = new ArrayList<String>();
    if (ldapAttributes.containsKey(key)) {
      List<Object> list = ldapAttributes.get(key);
      for (Object object : list) {
        String tmp = (String) object;
        String[] split = tmp.split("\\$");
        for (String string : split) {
          returnValue.add(string.trim());
        }
      }
    }
    return returnValue;
  }

  // Create list with each address field in new index.
  private List<String> getAddressValue(String key) {
    List<String> returnValue = new ArrayList<String>();
    if (ldapAttributes.containsKey(key)) {
      List<Object> list = ldapAttributes.get(key);
      for (Object object : list) {
        String tmp = (String) object;
        returnValue.add(tmp);
      }
    }
    return returnValue;
  }

  private void populateGeoCoordinates(Unit unit) {
    // Coordinates
    if ((String) getSingleValue(KivwsAttributes.HSA_GEOGRAPHICAL_COORDINATES) != null) {
      String hsaGeographicalCoordinates = (String) getSingleValue(KivwsAttributes.HSA_GEOGRAPHICAL_COORDINATES);
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

  private void populateUnitName(Unit unit) {
    // Name
    String ou = getSingleValue(KivwsAttributes.OU);
    if (!StringUtil.isEmpty(ou)) {
      String unitName = Formatter.replaceStringInString(ou, "\\,", ",");
      unit.setName(unitName.trim());
    } else {
      String cn = (String) getSingleValue(KivwsAttributes.CN);
      // change \, to ,
      cn = Formatter.replaceStringInString(cn, "\\,", ",");
      unit.setName(cn.trim());
    }
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

    unit.setHsaBusinessClassificationCode((List<String>) getMultiValue(KivwsAttributes.HSA_BUSINESS_CLASSIFICATION_CODE));

    List<String> businessText = new ArrayList<String>();
    for (String businessCode : unit.getHsaBusinessClassificationCode()) {
      String hsaBusinessClassificationText = codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
      businessText.add(hsaBusinessClassificationText);
    }
    unit.setHsaBusinessClassificationText(businessText);

    String hsaManagementText = displayValueTranslator.translateManagementCode(unit.getHsaManagementCode());
    unit.setHsaManagementText(hsaManagementText);

    String administrationCode = (String) getSingleValue(KivwsAttributes.HSA_ADMINISTRATION_FORM);
    unit.setHsaAdministrationForm(administrationCode);
    String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, administrationCode);
    unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);

    unit.setHsaManagementCode((String) getSingleValue(KivwsAttributes.HSA_MANAGEMENT_CODE));
    unit.setHsaManagementText(displayValueTranslator.translateManagementCode(unit.getHsaManagementCode()));

    unit.setVgrAO3kod((String) getSingleValue(KivwsAttributes.VGR_AO3_KOD));
    String vgrAO3Text = codeTablesService.getValueFromCode(KivwsCodeTableName.VGR_AO3_CODE, unit.getVgrAO3kod());
    unit.setVgrAO3kodText(vgrAO3Text);

    unit.setCareType(getSingleValue(KivwsAttributes.VGR_CARE_TYPE));
    String vgrCareTypeText = codeTablesService.getValueFromCode(KivwsCodeTableName.CARE_TYPE, unit.getCareType());
    unit.setCareTypeText(vgrCareTypeText);

    unit.setHsaMunicipalityCode((String) getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_CODE));
    String municipalityName = codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityName(municipalityName);

    unit.setHsaMunicipalitySectionCode((String) getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_SECTION_CODE));
    unit.setHsaMunicipalitySectionName((String) getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_SECTION_NAME));
  }
}
