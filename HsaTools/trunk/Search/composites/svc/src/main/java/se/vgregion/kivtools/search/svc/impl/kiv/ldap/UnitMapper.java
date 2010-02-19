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
import java.util.Arrays;
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
import se.vgregion.kivtools.search.util.DisplayValueTranslator;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;

import com.domainlanguage.time.TimePoint;

public class UnitMapper implements ContextMapper {

	public UnitMapper(CodeTablesService codeTablesService, DisplayValueTranslator displayValueTranslator) {
		super();
		this.codeTablesService = codeTablesService;
		this.displayValueTranslator = displayValueTranslator;
	}

	private CodeTablesService codeTablesService;
	private DisplayValueTranslator displayValueTranslator;
	private DirContextOperations dirContext;

	@Override
	public Unit mapFromContext(Object ctx) {

		Unit unit = new Unit();
		dirContext = (DirContextOperations) ctx;
		unit.setOu(getStringValue(UnitLdapAttributes.OU));
		unit.setDn(DN.createDNFromString(dirContext.getDn().toString()));
		unit.setHsaIdentity(getStringValue(UnitLdapAttributes.HSA_IDENTITY));
		unit.setContractCode(getStringValue(UnitLdapAttributes.VGR_AVTALSKOD));

		String timeStamp = getStringValue(UnitLdapAttributes.CREATE_TIMESTAMP);
		if (!StringUtil.isEmpty(timeStamp)) {
			unit.setCreateTimestamp(TimePoint.parseFrom(getStringValue(UnitLdapAttributes.CREATE_TIMESTAMP), "yyyyMMddHHmmss", TimeZone.getDefault()));
		}

		timeStamp = getStringValue(UnitLdapAttributes.vgrModifyTimestamp);
		if (!StringUtil.isEmpty(timeStamp)) {
			unit.setModifyTimestamp(TimePoint.parseFrom(timeStamp, "yyyyMMddHHmmss", TimeZone.getDefault()));
		}
		unit.setDescription(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.DESCRIPTION)));
		unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.FACSIMILE_TELEPHONE_NUMBER)));

		// Set administration form text by using codeTableService
		String administrationCode = getStringValue(UnitLdapAttributes.HSA_ADMINISTRATION_FORM);
		unit.setHsaAdministrationForm(administrationCode);
		String hsaHsaAdministrationFormText = codeTablesService.getValueFromCode(CodeTableName.HSA_ADMINISTRATION_FORM, administrationCode);
		unit.setHsaAdministrationFormText(hsaHsaAdministrationFormText);

		unit.setHsaBusinessClassificationCode(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.HSA_BUSINESS_CLASSIFICATION_CODE)));

		// Set Business classification code text values
		List<String> businessText = new ArrayList<String>();
		for (String businessCode : unit.getHsaBusinessClassificationCode()) {
			String hsaBusinessClassificationText = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessCode);
			businessText.add(hsaBusinessClassificationText);
		}
		unit.setHsaBusinessClassificationText(businessText);

		unit.setHsaConsigneeAddress(AddressHelper.convertToAddress(getFormattedAddressList(UnitLdapAttributes.HSA_CONSIGNEE_ADDRESS)));
		unit.setHsaCountyCode(getStringValue(UnitLdapAttributes.HSA_COUNTY_CODE));
		unit.setHsaCountyName(getStringValue(UnitLdapAttributes.HSA_COUNTY_NAME));
		unit.setHsaDropInHours(WeekdayTime
				.createWeekdayTimeList(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.HSA_DROPIN_HOURS))));
		unit.setHsaEndDate(TimeUtil.parseStringToZuluTime(getStringValue(UnitLdapAttributes.HSA_END_DATE)));
		unit.setHsaInternalAddress(AddressHelper.convertToAddress(getFormattedAddressList(UnitLdapAttributes.HSA_INTERNAL_ADDRESS)));
		unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));

		unit.setHsaManagementCode(getStringValue(UnitLdapAttributes.HSA_MANAGEMENT_CODE));
		unit.setHsaManagementText(displayValueTranslator.translateManagementCode(unit.getHsaManagementCode()));

		unit.setHsaMunicipalityCode(getStringValue(UnitLdapAttributes.HSA_MUNICIPALITY_CODE));

		String municipalityName = codeTablesService.getValueFromCode(CodeTableName.HSA_MUNICIPALITY_CODE, unit.getHsaMunicipalityCode());
		unit.setHsaMunicipalityName(municipalityName);
		unit.setHsaMunicipalitySectionCode(getStringValue(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_CODE));
		unit.setHsaMunicipalitySectionName(getStringValue(UnitLdapAttributes.HSA_MUNICIPALITY_SECTION_NAME));
		unit.setHsaPostalAddress(AddressHelper.convertToAddress(getFormattedAddressList(UnitLdapAttributes.HSA_POSTAL_ADDRESS)));
		unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(getListFromArrayAttributes(dirContext
				.getStringAttributes(UnitLdapAttributes.hsaPublicTelephoneNumber))));
		unit.setHsaRoute(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.hsaRoute)));
		unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(getFormattedAddressList(UnitLdapAttributes.hsaSedfDeliveryAddress)));
		unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(getFormattedAddressList(UnitLdapAttributes.hsaSedfInvoiceAddress)));
		unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.hsaSedfSwitchboardTelephoneNo)));
		unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.hsaSmsTelephoneNumber)));
		unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(getFormattedAddressList(UnitLdapAttributes.hsaStreetAddress)));
		unit.setHsaSurgeryHours(WeekdayTime
				.createWeekdayTimeList(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.hsaSurgeryHours))));
		unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(getListFromArrayAttributes(dirContext
				.getStringAttributes(UnitLdapAttributes.hsaTelephoneNumber))));
		unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(getListFromArrayAttributes(dirContext
				.getStringAttributes(UnitLdapAttributes.hsaTelephoneTime))));
		unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.hsaTextPhoneNumber)));
		unit.setHsaUnitPrescriptionCode(getStringValue(UnitLdapAttributes.hsaUnitPrescriptionCode));
		unit.setHsaVisitingRuleAge(getStringValue(UnitLdapAttributes.hsaVisitingRuleAge));
		unit.setHsaVisitingRules(getStringValue(UnitLdapAttributes.hsaVisitingRules));
		unit.setInternalDescription(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.VGR_INTERNAL_DESCRIPTION)));
		// unit.setIsUnit(isUnit);

		String labeledURI = getStringValue(UnitLdapAttributes.LABELED_URI);
		labeledURI = fixURI(labeledURI);
		unit.setLabeledURI(labeledURI);

		String vgrLabeledURI = getStringValue(UnitLdapAttributes.vgrLabeledURI);
		vgrLabeledURI = fixURI(vgrLabeledURI);
		unit.setInternalWebsite(vgrLabeledURI);

		unit.setLocality(getStringValue(UnitLdapAttributes.L));
		unit.setMail(getStringValue(UnitLdapAttributes.MAIL));
		unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.mobileTelephoneNumber)));
		// TODO: check if this is needed
		String unitName = Formatter.replaceStringInString(getStringValue(UnitLdapAttributes.OU), "\\,", ",");
		unit.setName(unitName.trim());
		unit.setName(unitName);
		unit.setObjectClass(getStringValue(UnitLdapAttributes.OBJECT_CLASS));
		unit.setIsUnit(isUnitType(getStringValue(UnitLdapAttributes.OBJECT_CLASS)));
		unit.setInternalDescription(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.VGR_INTERNAL_DESCRIPTION)));

		unit.setName(getUnitName(dirContext));
		unit.setOrganizationalUnitNameShort(getStringValue(UnitLdapAttributes.ORGANIZATIONAL_UNITNAME_SHORT));
		unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(getStringValue(UnitLdapAttributes.PAGER_TELEPHONE_NUMBER)));
		populateGeoCoordinates(dirContext, unit);
		unit.setVgrAnsvarsnummer(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.vgrAnsvarsnummer)));
		unit.setVgrAO3kod(getStringValue(UnitLdapAttributes.vgrAO3kod));

		String vgrAO3Text = codeTablesService.getValueFromCode(CodeTableName.VGR_AO3_CODE, getStringValue(UnitLdapAttributes.vgrAO3kod));
		unit.setVgrAO3kodText(vgrAO3Text);

		unit.setVgrCareType(getStringValue(UnitLdapAttributes.vgrCareType));
		String vgrCareTypeText = codeTablesService.getValueFromCode(CodeTableName.VGR_CARE_TYPE, UnitLdapAttributes.vgrCareType);
		unit.setVgrCareTypeText(vgrCareTypeText);
		unit.setVgrEANCode(getStringValue(UnitLdapAttributes.vgrEanCode));
		unit.setVgrEDICode(getStringValue(UnitLdapAttributes.vgrEdiCode));
		unit.setVgrInternalSedfInvoiceAddress(getStringValue(UnitLdapAttributes.VGR_INTERNAL_SEDF_INVOICE_ADDRESS));
		unit.setVgrTempInfo(getStringValue(UnitLdapAttributes.vgrTempInfo));
		unit.setVgrRefInfo(getStringValue(UnitLdapAttributes.vgrRefInfo));
		unit.setVgrVardVal(Boolean.getBoolean(getStringValue(UnitLdapAttributes.vgrVardVal)));
		unit.setVisitingHours(WeekdayTime
				.createWeekdayTimeList(getListFromArrayAttributes(dirContext.getStringAttributes(UnitLdapAttributes.hsaVisitingHours))));
		unit.setVisitingRuleReferral(getStringValue(UnitLdapAttributes.hsaVisitingRuleReferral));

		// As the last step, let HealthcareTypeConditionHelper figure out which
		// healthcare type(s) this unit belongs to
		HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
		htch.assignHealthcareTypes(unit);
		populateShowVisitingRulesAndAgeInterval(unit);
		return unit;
	}

	private String getStringValue(String attributeKey) {
		String stringAttribute = dirContext.getStringAttribute(attributeKey);
		if (StringUtil.isEmpty(stringAttribute)) {
			stringAttribute = "";
		}
		return stringAttribute;
	}

	private List<String> getListFromArrayAttributes(String[] arrayAttributes) {
		List<String> stringList = null;
		if (arrayAttributes == null) {
			stringList = new ArrayList<String>();
		} else {
			stringList = Arrays.asList(arrayAttributes);
		}
		return stringList;
	}

	private List<String> getFormattedAddressList(String attributeKey) {
		List<String> result = new ArrayList<String>();
		String attributeValue = dirContext.getStringAttribute(attributeKey);
		if (!StringUtil.isEmpty(attributeValue)) {
			String[] values = attributeValue.split("\\$");
			for (String string : values) {
				result.add(string);
			}
		}
		return result;
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

	private static void populateGeoCoordinates(DirContextOperations dirContext, Unit unit) {
		// Coordinates
		if (dirContext.getStringAttribute(UnitLdapAttributes.hsaGeographicalCoordinates) != null) {
			String hsaGeographicalCoordinates = dirContext.getStringAttribute(UnitLdapAttributes.hsaGeographicalCoordinates);
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

	private String getUnitName(DirContextOperations dirContextOperations) {
		String name = dirContextOperations.getStringAttribute("ou");
		// Is a function, name is the cn attribute instead.
		if (StringUtil.isEmpty(name)) {
			name = dirContextOperations.getStringAttribute("cn");
		}
		return name;
	}

	private boolean isUnitType(String objectClass) {
		boolean isUnitType = false;
		if (objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_SPECIFIC) || objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_STANDARD)) {
			isUnitType = true;
		} else if (objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_SPECIFIC)
				|| objectClass.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_STANDARD)) {
			isUnitType = false;
		}
		return isUnitType;
	}

	/**
	 * Prepends "http://" if the provided URI isn't a correct URI.
	 * 
	 * @param uri
	 *            The URI to fix.
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
