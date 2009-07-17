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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.ldap.LdapORMHelper;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;
import se.vgregion.kivtools.search.util.geo.GeoUtil;

import com.domainlanguage.time.TimePoint;
import com.novell.ldap.LDAPEntry;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class UnitFactory {

  /**
   * Reconstitues a Unit based on an LDAPEnty.
   * 
   * @param unitEntry The LDAPEntry to reconstitute the unit from.
   * @return A complete Unit based on the provided LDAPEntry.
   * @throws Exception
   */
  public static Unit reconstitute(LDAPEntry unitEntry) throws Exception {
    Unit unit = new Unit();
    if (unitEntry == null) {
      return unit;
    }

    // HAK does not seem to make a difference between units and functions, assume all are units.
    unit.setIsUnit(true);

    /*
     * unit.setObjectClass(LdapORMHelper.getSingleValue(unitEntry.getAttribute("objectClass"))); String temp = unit.getObjectClass().toLowerCase(); if
     * (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_UNIT_STANDARD)) { unit.setIsUnit(true); } else if
     * (temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_SPECIFIC) || temp.equalsIgnoreCase(Constants.OBJECT_CLASS_FUNCTION_STANDARD)) { unit.setIsUnit(false); } else { Exception e = new
     * Exception("Detected unknown objectClass=" + unit.getObjectClass() + " in " + UnitFactory.class.getName() + "::reconstitute()"); }
     */

    unit.setDn(DN.createDNFromString(unitEntry.getDN()));

    // OU
    unit.setOu(LdapORMHelper.getSingleValue(unitEntry.getAttribute("ou")));

    // organizationalUnitName
    // change \, to ,
    String orgName = Formatter.replaceStringInString(unit.getOu(), "\\,", ",");
    // special
    unit.setName(orgName);

    // organizationalUnitNameShort
    unit.setOrganizationalUnitNameShort(LdapORMHelper.getSingleValue(unitEntry.getAttribute("organizationalUnitNameShort")));

    // description
    unit.setDescription(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("description")));

    // mail
    unit.setMail(LdapORMHelper.getSingleValue(unitEntry.getAttribute("mail")));

    // labeledURI
    unit.setLabeledURI(LdapORMHelper.getSingleValue(unitEntry.getAttribute("labeledURI")));

    // vgrInternalSedfInvoiceAddress
    unit.setVgrInternalSedfInvoiceAddress(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrInternalSedfInvoiceAddress")));

    // vgrCareType
    unit.setVgrCareType(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrCareType")));

    // vgrAO3kod
    unit.setVgrAO3kod(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrAO3kod")));

    // hsaIdentity
    unit.setHsaIdentity(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaIdentity")));

    // BusinessClassification

    // hsaBusinessClassificationCode
    unit.setHsaBusinessClassificationCode(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("businessClassificationCode")));

    // hsaTextPhoneNumber
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaTextPhoneNumber"))));

    // hsaTextPhoneNumber
    unit.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaTextPhoneNumber"))));

    // mobileTelephoneNumber
    unit.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("mobileTelephoneNumber"))));

    // hsaSedfSwitchboardTelephoneNo
    unit.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaSwitchboardTelephoneNo"))));

    // hsaInternalPagerNumber
    unit.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaInternalPagerNumber"))));

    // hsaSmsTelephoneNumber
    unit.setHsaSmsTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaSmsTelephoneNumber"))));

    // facsimileTelephoneNumber
    unit.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("facsimileTelephoneNumber"))));

    // pagerTelephoneNumber
    unit.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(unitEntry.getAttribute("pagerTelephoneNumber"))));

    // hsaTelephoneNumber
    unit.setHsaTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("telephoneNumber"))));

    // hsaPublicTelephoneNumber
    unit.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("telephoneNumber"))));

    // hsaTelephoneTime
    unit.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("telephoneHours"))));

    // hsaSurgeryHours
    unit.setHsaSurgeryHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("surgeryHours"))));

    // hsaDropInHours
    unit.setHsaDropInHours(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("dropInHours"))));

    // hsaInternalAddress
    unit.setHsaInternalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("hsaInternalAddress"))));

    // hsaStreetAddress
    // unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("postalAddress"))));
    List<String> addressList = new ArrayList<String>();
    addressList.add(LdapORMHelper.getSingleValue(unitEntry.getAttribute("street")));
    addressList.add(LdapORMHelper.getSingleValue(unitEntry.getAttribute("postalCode")) + " " + LdapORMHelper.getSingleValue(unitEntry.getAttribute("l")));

    unit.setHsaStreetAddress(AddressHelper.convertToStreetAddress(addressList));

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

    // hsaMunicipalityName
    unit.setHsaMunicipalityName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("municipalityName")));

    // Kommunkod
    unit.setHsaMunicipalityCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("municipalityCode")));

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

    // �garformkod
    unit.setHsaManagementCode(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaManagementCode")));

    // �garform klartext
    unit.setHsaManagementName(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaManagementName")));

    // Visiting rules
    unit.setHsaVisitingRules(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRules")));

    // Visiting rule age
    unit.setHsaVisitingRuleAge(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRuleAge")));

    // Temporary information
    unit.setVgrTempInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrTempInfo")));
    unit.setVgrRefInfo(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrRefInfo")));

    // Drifts- & juridisk formkod
    unit.setHsaAdministrationForm(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaAdministrationForm")));

    // Drifts- & juridisk formklartext
    unit.setHsaAdministrationFormText(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaAdministrationFormText")));

    // Senast uppdaterad
    if (unitEntry.getAttribute("vgrModifyTimestamp") != null) {
      unit.setModifyTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("vgrModifyTimestamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Skapad
    if (unitEntry.getAttribute("createTimeStamp") != null) {
      unit.setCreateTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(unitEntry.getAttribute("createTimeStamp")), "yyyyMMddHHmmss", TimeZone.getDefault()));
    }

    // Coordinates
    if (unitEntry.getAttribute("geographicalCoordinates") != null) {
      String hsaGeographicalCoordinates = LdapORMHelper.getSingleValue(unitEntry.getAttribute("geographicalCoordinates"));
      unit.setHsaGeographicalCoordinates(hsaGeographicalCoordinates);
      // Parse and set RT90 format
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

    // hsaVisitingRuleAge
    unit.setHsaVisitingRuleAge(LdapORMHelper.getSingleValue(unitEntry.getAttribute("hsaVisitingRuleAge")));

    // management
    unit.setHsaManagementText(getManagementDescription(LdapORMHelper.getSingleValue(unitEntry.getAttribute("management"))));

    if (unitEntry.getAttribute("route") != null) {
      unit.setHsaRoute(LdapORMHelper.getMultipleValues(unitEntry.getAttribute("route")));
    }

    // As the last step, let HealthcareTypeConditionHelper figure out which healthcare type(s) this unit belongs to
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    htch.assignHealthcareTypes(unit);

    return unit;
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
