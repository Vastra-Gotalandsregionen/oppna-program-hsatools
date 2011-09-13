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
  private final CodeTablesService codeTablesService;
  private final DisplayValueTranslator displayValueTranslator;

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

    AttributeHelper attributeHelper = new AttributeHelper();

    for (Entry entry : attributes) {
      attributeHelper.add(entry.getKey(), entry.getValue().getAnyType());
    }

    unit.setOu(attributeHelper.getSingleValue(KivwsAttributes.OU));

    unit.setHsaIdentity(attributeHelper.getSingleValue(KivwsAttributes.HSA_IDENTITY));
    unit.setContractCode(attributeHelper.getSingleValue(KivwsAttributes.VGR_AVTALSKOD));

    String timeStamp = attributeHelper.getSingleValue(KivwsAttributes.CREATE_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setCreateTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    timeStamp = attributeHelper.getSingleValue(KivwsAttributes.VGR_MODIFY_TIMESTAMP);
    if (!StringUtil.isEmpty(timeStamp)) {
      unit.setModifyTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
    }
    unit.addDescription(attributeHelper.getMultiValue(KivwsAttributes.DESCRIPTION));
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.FACSIMILE_TELEPHONE_NUMBER)));
    unit.setHsaCountyCode(attributeHelper.getSingleValue(KivwsAttributes.HSA_COUNTY_CODE));
    unit.addHsaDropInHours(WeekdayTime.createWeekdayTimeList(attributeHelper.getMultiValue(KivwsAttributes.HSA_DROPIN_HOURS)));
    unit.setHsaEndDate(TimeUtil.parseStringToZuluTime(attributeHelper.getSingleValue(KivwsAttributes.HSA_END_DATE)));
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(attributeHelper.getMultiValue(KivwsAttributes.HSA_INTERNAL_ADDRESS)));
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.PAGER_TELEPHONE_NUMBER)));
    unit.setHsaPostalAddress(AddressHelper.convertToAddress(attributeHelper.getMultiValue(KivwsAttributes.HSA_POSTAL_ADDRESS)));
    List<PhoneNumber> createPhoneNumberList = PhoneNumber.createPhoneNumberList(attributeHelper.getMultiValue(KivwsAttributes.HSA_PUBLIC_TELEPHONE_NUMBER));
    for (PhoneNumber phoneNumber : createPhoneNumberList) {
      unit.addHsaPublicTelephoneNumber(phoneNumber);
    }
    unit.addHsaRoute(attributeHelper.getMultiValue(KivwsAttributes.HSA_ROUTE));
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(attributeHelper.getMultiValue(KivwsAttributes.HSA_SEDF_DELIVERY_ADDRESS)));
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(attributeHelper.getMultiValue(KivwsAttributes.HSA_SEDF_INVOICE_ADDRESS)));
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.HSA_SEDF_SWITCHBOARD_TELEPHONE_NO)));
    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(attributeHelper.getMultiValue(KivwsAttributes.HSA_STREET_ADDRESS)));
    unit.addHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(attributeHelper.getMultiValue(KivwsAttributes.HSA_SURGERY_HOURS)));
    unit.addHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(attributeHelper.getMultiValue(KivwsAttributes.HSA_TELEPHONE_NUMBER)));
    unit.addHsaTelephoneTimes(WeekdayTime.createWeekdayTimeList(attributeHelper.getMultiValue(KivwsAttributes.HSA_TELEPHONE_TIME)));
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.HSA_TEXT_PHONE_NUMBER)));
    unit.setHsaUnitPrescriptionCode(attributeHelper.getSingleValue(KivwsAttributes.HSA_UNIT_PRESCRIPTION_CODE));
    unit.setHsaVisitingRuleAge(attributeHelper.getSingleValue(KivwsAttributes.HSA_VISITING_RULE_AGE));
    unit.setHsaVisitingRules(attributeHelper.getSingleValue(KivwsAttributes.HSA_VISITING_RULES));
    unit.setHsaPatientVisitingRules(attributeHelper.getSingleValue(KivwsAttributes.HSA_PATIENT_VISITING_RULES));
    unit.addInternalDescription(attributeHelper.getMultiValue(KivwsAttributes.VGR_INTERNAL_DESCRIPTION));
    unit.setIsUnit((ctx instanceof se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit));

    String labeledURI = attributeHelper.getSingleValue(KivwsAttributes.LABELED_URI);
    labeledURI = this.fixURI(labeledURI);
    unit.setLabeledURI(labeledURI);

    String vgrLabeledURI = attributeHelper.getSingleValue(KivwsAttributes.VGR_LABELED_URI);
    vgrLabeledURI = this.fixURI(vgrLabeledURI);
    unit.setInternalWebsite(vgrLabeledURI);

    unit.setLocality(attributeHelper.getSingleValue(KivwsAttributes.L));
    unit.setMail(attributeHelper.getSingleValue(KivwsAttributes.MAIL));
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.MOBILE_TELEPHONE_NUMBER)));

    this.populateUnitName(unit, attributeHelper);
    unit.setObjectClass(attributeHelper.getSingleValue(KivwsAttributes.OBJECT_CLASS));

    unit.setOrganizationalUnitNameShort(attributeHelper.getSingleValue(KivwsAttributes.ORGANIZATIONAL_UNITNAME_SHORT));
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(attributeHelper.getSingleValue(KivwsAttributes.PAGER_TELEPHONE_NUMBER)));
    this.populateGeoCoordinates(unit, attributeHelper);
    unit.setVgrAnsvarsnummer(attributeHelper.getMultiValue(KivwsAttributes.VGR_ANSVARSNUMMER));
    unit.setVgrInternalSedfInvoiceAddress(attributeHelper.getSingleValue(KivwsAttributes.VGR_INTERNAL_SEDF_INVOICE_ADDRESS));
    unit.setVgrTempInfo(attributeHelper.getSingleValue(KivwsAttributes.VGR_TEMP_INFO));
    unit.setVgrRefInfo(attributeHelper.getSingleValue(KivwsAttributes.VGR_REF_INFO));
    unit.setVgrVardVal("J".equalsIgnoreCase(attributeHelper.getSingleValue(KivwsAttributes.VGR_VARDVAL)));
    unit.setVisitingHours(WeekdayTime.createWeekdayTimeList(attributeHelper.getMultiValue(KivwsAttributes.HSA_VISITING_HOURS)));
    unit.setVisitingRuleReferral(attributeHelper.getSingleValue(KivwsAttributes.HSA_VISITING_RULE_REFERRAL));

    List<String> indicators = attributeHelper.getMultiValue(KivwsAttributes.HSA_DESTINATION_INDICATOR);
    for (String indicator : indicators) {
      unit.addHsaDestinationIndicator(indicator);
    }

    unit.setHsaBusinessType(attributeHelper.getSingleValue(KivwsAttributes.HSA_BUSINESS_TYPE));

    this.assignCodeTableValuesToUnit(unit, attributeHelper);
    // As the last step, let HealthcareTypeConditionHelper figure out which
    // healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> healthcareTypes = htch.getHealthcareTypesForUnit(unit);
    unit.addHealthcareTypes(healthcareTypes);
    // Visiting rules and age interval should be shown at all times
    unit.setShowVisitingRules(true);
    unit.setShowAgeInterval(true);

    unit.setHsaResponsibleHealthCareProvider(attributeHelper.getSingleValue(KivwsAttributes.HSA_RESPONSIBLE_HEALTH_CARE_PROVIDER));
    unit.addHsaHealthCareUnitMembers(attributeHelper.getMultiValue(KivwsAttributes.HSA_HEALTH_CARE_UNIT_MEMBER));
    unit.setVgrObjectManagers(attributeHelper.getMultiValue(KivwsAttributes.VGR_OBJECT_MANAGERS));

    return unit;
  }

  private void populateGeoCoordinates(Unit unit, AttributeHelper attributeHelper) {
    // Coordinates
    if (attributeHelper.getSingleValue(KivwsAttributes.HSA_GEOGRAPHICAL_COORDINATES) != null) {
      String hsaGeographicalCoordinates = attributeHelper.getSingleValue(KivwsAttributes.HSA_GEOGRAPHICAL_COORDINATES);
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

  private void populateUnitName(Unit unit, AttributeHelper attributeHelper) {
    // Name
    String ou = attributeHelper.getSingleValue(KivwsAttributes.OU);
    if (!StringUtil.isEmpty(ou)) {
      String unitName = Formatter.replaceStringInString(ou, "\\,", ",");
      unit.setName(unitName.trim());
    } else {
      String cn = attributeHelper.getSingleValue(KivwsAttributes.CN);
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
   * @param attributeHelper
   */
  private void assignCodeTableValuesToUnit(Unit unit, AttributeHelper attributeHelper) {

    unit.setHsaBusinessClassificationCode(attributeHelper.getMultiValue(KivwsAttributes.HSA_BUSINESS_CLASSIFICATION_CODE));

    List<String> businessText = new ArrayList<String>();
    for (String businessCode : unit.getHsaBusinessClassificationCode()) {
      String hsaBusinessClassificationText = this.codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
      businessText.add(hsaBusinessClassificationText);
    }
    unit.setHsaBusinessClassificationText(businessText);

    String hsaManagementText = this.displayValueTranslator.translateManagementCode(unit.getHsaManagementCode());
    unit.setHsaManagementText(hsaManagementText);

    String administrationCode = attributeHelper.getSingleValue(KivwsAttributes.HSA_ADMINISTRATION_FORM);
    unit.setHsaAdministrationForm(administrationCode);
    String hsaHsaAdministrationFormText = this.codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_ADMINISTRATION_FORM, administrationCode);
    unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);

    unit.setHsaManagementCode(attributeHelper.getSingleValue(KivwsAttributes.HSA_MANAGEMENT_CODE));
    unit.setHsaManagementText(this.displayValueTranslator.translateManagementCode(unit.getHsaManagementCode()));

    unit.setVgrAO3kod(attributeHelper.getSingleValue(KivwsAttributes.VGR_AO3_KOD));
    String vgrAO3Text = this.codeTablesService.getValueFromCode(KivwsCodeTableName.VGR_AO3_CODE, unit.getVgrAO3kod());
    unit.setVgrAO3kodText(vgrAO3Text);

    unit.setCareType(attributeHelper.getSingleValue(KivwsAttributes.VGR_CARE_TYPE));
    String vgrCareTypeText = this.codeTablesService.getValueFromCode(KivwsCodeTableName.CARE_TYPE, unit.getCareType());
    unit.setCareTypeText(vgrCareTypeText);

    unit.setHsaMunicipalityCode(attributeHelper.getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_CODE));
    String municipalityName = this.codeTablesService.getValueFromCode(KivwsCodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
    unit.setHsaMunicipalityName(municipalityName);

    unit.setHsaMunicipalitySectionCode(attributeHelper.getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_SECTION_CODE));
    unit.setHsaMunicipalitySectionName(attributeHelper.getSingleValue(KivwsAttributes.HSA_MUNICIPALITY_SECTION_NAME));
  }

  private static class AttributeHelper {
    private final Map<String, List<Object>> ldapAttributes = new HashMap<String, List<Object>>();

    public void add(String key, List<Object> value) {
      this.ldapAttributes.put(key, value);
    }

    public String getSingleValue(String key) {
      String returnValue = "";
      if (this.ldapAttributes.containsKey(key)) {
        returnValue = (String) this.ldapAttributes.get(key).get(0);
      }
      return returnValue;
    }

    public List<String> getMultiValue(String key) {
      List<String> returnValue = new ArrayList<String>();
      if (this.ldapAttributes.containsKey(key)) {
        List<Object> list = this.ldapAttributes.get(key);
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
    // private List<String> getAddressValue(String key) {
    // List<String> returnValue = new ArrayList<String>();
    // if (ldapAttributes.containsKey(key)) {
    // List<Object> list = ldapAttributes.get(key);
    // for (Object object : list) {
    // String tmp = (String) object;
    // returnValue.add(tmp);
    // }
    // }
    // return returnValue;
    // }
  }
}
