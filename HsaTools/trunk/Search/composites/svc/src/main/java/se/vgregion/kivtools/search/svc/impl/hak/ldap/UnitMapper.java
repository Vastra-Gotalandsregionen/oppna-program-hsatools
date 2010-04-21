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
 */
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import geo.google.datamodel.GeoAltitude;
import geo.google.datamodel.GeoCoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.WordUtils;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.AddressHelper;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitLdapAttributes;
import se.vgregion.kivtools.search.svc.ldap.DirContextOperationsHelper;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;

/**
 * Context mapper for unit entries.
 */
public class UnitMapper implements ContextMapper {

  /**
   * {@inheritDoc}
   */
  @Override
  public Unit mapFromContext(Object ctx) {
    DirContextOperationsHelper context = new DirContextOperationsHelper((DirContextOperations) ctx);

    Unit unit = new Unit();

    // HAK does not seem to make a difference between units and functions, assume all are units.
    unit.setIsUnit(true);

    unit.setDn(DN.createDNFromString(context.getDnString()));

    // OU
    unit.setOu(context.getString("ou"));
    if (!StringUtil.isEmpty(unit.getOu())) {
      // organizationalUnitName
      // change \, to ,
      String orgName = Formatter.replaceStringInString(unit.getOu(), "\\,", ",");
      // special
      unit.setName(orgName);
    } else {
      String cn = context.getString("cn");
      cn = Formatter.replaceStringInString(cn, "\\,", ",");
      unit.setName(cn);
    }

    // organizationalUnitNameShort
    unit.setOrganizationalUnitNameShort(context.getString("organizationalUnitNameShort"));

    // description
    unit.setDescription(context.getStrings("description"));

    // mail
    unit.setMail(context.getString("mail"));

    // l (=locality)
    unit.setLocality(WordUtils.capitalize(context.getString("l").toLowerCase()));

    // labeledURI
    unit.setLabeledURI(context.getString("labeledURI"));

    // vgrCareType
    unit.setVgrCareType(context.getString("vgrCareType"));

    // vgrAO3kod
    unit.setVgrAO3kod(context.getString("vgrAO3kod"));

    // hsaIdentity
    unit.setHsaIdentity(context.getString("hsaIdentity"));

    // BusinessClassification

    // hsaBusinessClassificationCode
    unit.setHsaBusinessClassificationCode(context.getStrings("businessClassificationCode"));

    populatePhoneNumbers(context, unit);

    // hsaSurgeryHours
    unit.setHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(context.getStrings("surgeryHours")));

    // hsaDropInHours
    unit.setHsaDropInHours(WeekdayTime.createWeekdayTimeList(context.getStrings("dropInHours")));

    populateAddresses(context, unit);

    // hsaUnitPrescriptionCode
    unit.setHsaUnitPrescriptionCode(context.getString("hsaUnitPrescriptionCode"));

    // vgrAnsvarsnummer
    unit.setVgrAnsvarsnummer(context.getStrings("vgrAnsvarsnummer"));

    populateLocalityInformation(context, unit);

    // EDI-kod
    unit.setVgrEDICode(context.getString("vgrEdiCode"));

    // EAN-kod
    unit.setVgrEANCode(context.getString("vgrEanCode"));

    // �garformkod
    unit.setHsaManagementCode(context.getString("hsaManagementCode"));

    // �garform klartext
    unit.setHsaManagementName(context.getString("hsaManagementName"));

    // Visiting rules
    unit.setHsaVisitingRules(context.getString("hsaVisitingRules"));

    // Visiting rule age
    unit.setHsaVisitingRuleAge(context.getString("hsaVisitingRuleAge"));

    // Temporary information
    unit.setVgrTempInfo(context.getString("vgrTempInfo"));
    unit.setVgrRefInfo(context.getString("vgrRefInfo"));

    // Drifts- & juridisk formkod
    unit.setHsaAdministrationForm(context.getString("hsaAdministrationForm"));

    // Drifts- & juridisk formklartext
    unit.setHsaAdministrationFormText(context.getString("hsaAdministrationFormText"));

    // Senast uppdaterad
    if (context.hasAttribute("whenChanged")) {
      unit.setModifyTimestamp(TimePoint.parseFrom(context.getString("whenChanged"), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Skapad
    if (context.hasAttribute("whenCreated")) {
      unit.setCreateTimestamp(TimePoint.parseFrom(context.getString("whenCreated"), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    populateGeoCoordinates(context, unit);

    // management
    unit.setHsaManagementText(getManagementDescription(context.getString("management")));

    if (context.getString("route") != null) {
      unit.setHsaRoute(context.getStrings("route"));
    }

    // As the last step, let HealthcareTypeConditionHelper figure out which healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> healthcareTypes = htch.getHealthcareTypesForUnit(unit);
    unit.setHealthcareTypes(healthcareTypes);

    // Show age interval?
    unit.setShowAgeInterval(shouldAgeIntervalBeDisplayed(unit));
    // We always show visiting rules
    unit.setShowVisitingRules(true);

    unit.setManagerDN(context.getString("managerDN"));
    if (!StringUtil.isEmpty(unit.getManagerDN())) {
      DistinguishedName managerDN = new DistinguishedName(unit.getManagerDN());
      String manager = managerDN.removeLast().getValue();
      unit.setManager(manager);
    }

    List<String> indicators = context.getStrings(UnitLdapAttributes.HSA_DESTINATION_INDICATOR);
    for (String indicator : indicators) {
      unit.addHsaDestinationIndicator(indicator);
    }

    return unit;
  }

  private boolean shouldAgeIntervalBeDisplayed(Unit unit) {
    boolean display = true;

    display &= unit.getHsaVisitingRuleAgeIsValid();
    display &= !"Alla åldrar".equals(unit.getHsaVisitingRuleAge());

    return display;
  }

  private static void populateLocalityInformation(DirContextOperationsHelper context, Unit unit) {
    // hsaMunicipalityName
    unit.setHsaMunicipalityName(context.getString("municipalityName"));

    // Kommunkod
    unit.setHsaMunicipalityCode(context.getString("municipalityCode"));

    // Kommundelsnamn
    unit.setHsaMunicipalitySectionName(context.getString("hsaMunicipalitySectionName"));

    // Kommundelskod
    unit.setHsaMunicipalitySectionCode(context.getString("hsaMunicipalitySectionCode"));

    // Länskod
    unit.setHsaCountyCode(context.getString("hsaCountyCode"));

    // Länsnamn
    unit.setHsaCountyName(context.getString("hsaCountyName"));
  }

  private static void populateGeoCoordinates(DirContextOperationsHelper context, Unit unit) {
    // Coordinates
    if (context.getString("geographicalCoordinates") != null) {
      String hsaGeographicalCoordinates = context.getString("geographicalCoordinates");
      unit.setHsaGeographicalCoordinates(hsaGeographicalCoordinates);
      // Parse and set RT90 format
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

  private static void populateAddresses(DirContextOperationsHelper context, Unit unit) {
    // vgrInternalSedfInvoiceAddress
    unit.setVgrInternalSedfInvoiceAddress(context.getString("vgrInternalSedfInvoiceAddress"));

    // hsaInternalAddress
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(context.getStrings("hsaInternalAddress")));

    // hsaStreetAddress
    // unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("postalAddress"))));
    List<String> addressList = new ArrayList<String>();
    addressList.add(context.getString("street"));
    addressList.add(context.getString("postalCode") + " " + context.getString("l"));

    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(addressList));

    // hsaPostalAddress
    unit.setHsaPostalAddress(AddressHelper.convertToAddress(context.getStrings("postalAddress")));

    // hsaSedfDeliveryAddress
    unit.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(context.getStrings("hsaDeliveryAddress")));

    // hsaSedfInvoiceAddress
    unit.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(context.getStrings("hsaInvoiceAddress")));
    unit.setHsaConsigneeAddress(AddressHelper.convertToAddress(context.getStrings("hsaConsigneeAddress")));
  }

  private static void populatePhoneNumbers(DirContextOperationsHelper context, Unit unit) {
    // hsaTextPhoneNumber
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(context.getString("hsaTextPhoneNumber")));

    // mobileTelephoneNumber
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("mobile")));

    // hsaSedfSwitchboardTelephoneNo
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(context.getString("hsaSwitchboardNumber")));

    // hsaInternalPagerNumber
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(context.getString("hsaInternalPagerNumber")));

    // hsaSmsTelephoneNumber
    unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("hsaSmsTelephoneNumber")));

    // facsimileTelephoneNumber
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("facsimileTelephoneNumber")));

    // pagerTelephoneNumber
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(context.getString("pager")));

    // hsaTelephoneNumber
    unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(context.getStrings("hsaTelephoneNumber")));

    // hsaPublicTelephoneNumber
    unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(context.getStrings("lthTelephoneNumber")));

    // hsaTelephoneTime
    unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(context.getStrings("telephoneHours")));
  }

  private static String getManagementDescription(String code) {
    String returnValue = null;
    if (code != null) {
      if (code.equals("1")) {
        returnValue = "Landsting/Region";
      }
      if (code.equals("2")) {
        returnValue = "Kommun";
      }
      if (code.equals("3")) {
        returnValue = "Statlig";
      }
      if (code.equals("4")) {
        returnValue = "Privat, v\u00E5rdavtal";
      }
      if (code.equals("5")) {
        returnValue = "Privat, enl lag om l\u00E4karv\u00E5rdsers\u00E4ttning";
      }
      if (code.equals("6")) {
        returnValue = "Privat, utan offentlig finansiering";
      }
      if (code.equals("7")) {
        returnValue = "Kommunf\u00F6rbund/Kommunalf\u00F6rbund";
      }
      if (code.equals("9")) {
        returnValue = "\u00D6vrigt";
      }
    }
    return returnValue;
  }
}
