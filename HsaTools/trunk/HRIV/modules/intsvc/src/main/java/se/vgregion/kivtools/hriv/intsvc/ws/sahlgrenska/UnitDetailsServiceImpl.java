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

package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Description;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.EAliasType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ObjectFactory;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ReferralInformation;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.TemporaryInformation;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.Locality;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.VisitingConditions;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Service implementation for the webservice for retrieving unit information.
 * 
 * @author Jonas Liljenfelt & David Bennehult
 */
public class UnitDetailsServiceImpl implements UnitDetailsService<Organization> {
  private Log log = LogFactory.getLog(this.getClass());
  private UnitRepository unitRepository;
  private MvkClient mvkClient;
  private ObjectFactory objectFactory = new ObjectFactory();

  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  public void setMvkClient(MvkClient mvkClient) {
    this.mvkClient = mvkClient;
  }

  /**
   * {@inheritDoc}
   */
  public Organization getUnitDetails(String hsaIdentity) {
    Organization organization = objectFactory.createOrganization();
    if (hsaIdentity != null && !"".equals(hsaIdentity)) {
      Unit unit = null;
      try {
        unit = unitRepository.getUnitByHsaId(hsaIdentity);
      } catch (KivException e) {
        log.error("Unable to retrieve unit details.", e);
      }
      mvkClient.assignCaseTypes(unit);
      organization.getUnit().add(generateWebServiceUnit(unit));
    }
    return organization;
  }

  private se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit generateWebServiceUnit(Unit unit) {
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unitWs = objectFactory.createUnit();
    unitWs.setId(unit.getHsaIdentity());
    unitWs.setName(unit.getName());

    // Description
    Description description = new Description();
    description.setValue(unit.getConcatenatedDescription());
    unitWs.getDescription().add(description);

    if (unit.getShouldVgrTempInfoBeShown()) {
      // Temp Info
      TemporaryInformation temporaryInformation = new TemporaryInformation();
      temporaryInformation.setValue(unit.getVgrTempInfoBody());
      unitWs.getTemporaryInformation().add(temporaryInformation);

      // Ref info
      ReferralInformation referralInformation = new ReferralInformation();
      referralInformation.setValue(unit.getVgrRefInfo());
      unitWs.getReferralInformation().add(referralInformation);
    }

    // Set unitWs street address
    setAddress(unitWs, unit.getHsaStreetAddress(), "Visit", unit);
    // Set unitWs post address
    setAddress(unitWs, unit.getHsaPostalAddress(), "Post", unit);
    // Set telephone
    TelephoneType telephoneType = new TelephoneType();
    for (PhoneNumber phoneNumber : unit.getHsaPublicTelephoneNumber()) {
      telephoneType.getTelephoneNumber().add(phoneNumber.getPhoneNumber());
    }
    unitWs.getTelephone().add(telephoneType);

    // Set URL
    EAliasType unitWeb = new EAliasType();
    unitWeb.setLabel("Mottagningens webbplats");
    unitWeb.setType("URL");
    unitWeb.setAlias(unit.getLabeledURI());
    unitWs.getEAlias().add(unitWeb);

    // Set e-mail address
    EAliasType email = new EAliasType();
    email.setLabel("Mottagningens e-post");
    email.setType("E-mail");
    email.setAlias(unit.getMail());
    unitWs.getEAlias().add(email);

    // Set visiting rules
    VisitingConditions visitingConditions = new UnitType.VisitingConditions();
    visitingConditions.setVisitingRules(unit.getHsaVisitingRules());

    // Drop in hours
    String dropInConcatenated = "";
    for (WeekdayTime dropInInfo : unit.getHsaDropInHours()) {
      dropInConcatenated += dropInInfo.getDisplayValue() + ", ";
    }
    dropInConcatenated = stripEndingCommaAndSpace(dropInConcatenated);
    visitingConditions.setDropInHours(dropInConcatenated);

    // Visiting hours
    String visitingHoursConcatenated = "";
    for (WeekdayTime visitingHoursInfo : unit.getHsaSurgeryHours()) {
      visitingHoursConcatenated += visitingHoursInfo.getDisplayValue() + ", ";
    }
    visitingHoursConcatenated = stripEndingCommaAndSpace(visitingHoursConcatenated);
    visitingConditions.setVisitingHours(visitingHoursConcatenated);

    // Telephone hours
    String telephoneHoursConcatenated = "";
    for (WeekdayTime telephoneHoursInfo : unit.getHsaTelephoneTime()) {
      telephoneHoursConcatenated += telephoneHoursInfo.getDisplayValue() + ", ";
    }
    telephoneHoursConcatenated = stripEndingCommaAndSpace(telephoneHoursConcatenated);
    visitingConditions.setTelephoneHours(telephoneHoursConcatenated);
    unitWs.getVisitingConditions().add(visitingConditions);

    // Management
    UnitType.Management management = new UnitType.Management();
    management.setValue(unit.getHsaManagementText());
    unitWs.setManagement(management);

    // Business classification
    List<HealthcareType> businessClassifications = unit.getHealthcareTypes();
    for (HealthcareType businessClassificationHT : businessClassifications) {
      UnitType.BusinessClassification businessClassification = new UnitType.BusinessClassification();
      businessClassification.setValue(businessClassificationHT.getDisplayName());
      unitWs.getBusinessClassification().add(businessClassification);
    }

    // Location
    if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
      Locality locality = new Locality();
      locality.setValue(unit.getHsaMunicipalityName());
      unitWs.setLocality(locality);
    }

    // Set if unit is connected to MVK
    unitWs.setMvkEnable(checkIfConnectedToMvk(unit));

    return unitWs;
  }

  private void setAddress(se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unitWs, se.vgregion.kivtools.search.domain.values.Address address, String addressType, Unit unit) {
    // Set unitWs street address
    Address wsAddress = objectFactory.createAddress();

    if ("".equalsIgnoreCase(address.getStreet())) {
      wsAddress.setIsConcatenated(true);
      wsAddress.setConcatenatedAddress(address.getAdditionalInfoToString());
    } else {
      wsAddress.setType(addressType);
      List<String> additionalInfo = new ArrayList<String>();
      additionalInfo.addAll(address.getAdditionalInfo());
      String additionalInfoConcatenated = StringUtil.concatenate(additionalInfo);
      setStreetNameAndNumberForAddress(wsAddress, address.getStreet(), additionalInfoConcatenated);
      List<String> postalPostCodes = wsAddress.getPostCode();
      postalPostCodes.add(address.getZipCode().toString());
      wsAddress.setCity(address.getCity());
      GeoCoordinates postalGeoCoordinates = new AddressType.GeoCoordinates();
      postalGeoCoordinates.getXpos().add(String.valueOf(unit.getRt90X()));
      postalGeoCoordinates.getYpos().add(String.valueOf(unit.getRt90Y()));
      wsAddress.setGeoCoordinates(postalGeoCoordinates);
    }
    unitWs.getAddress().add(wsAddress);
  }

  private boolean checkIfConnectedToMvk(Unit unit) {
    return unit.getMvkCaseTypes() != null && unit.getMvkCaseTypes().size() > 0;
  }

  private String stripEndingCommaAndSpace(String inputString) {
    String result = inputString;

    if (result.endsWith(", ")) {
      result = result.substring(0, result.length() - 2);
    }
    return result;
  }

  private void setStreetNameAndNumberForAddress(Address address, String hsaAddress, String additionalInfo) {
    Pattern patternStreetName = Pattern.compile("\\D+");
    Matcher matcherStreetName = patternStreetName.matcher(hsaAddress);
    Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
    Matcher matcherStreetNb = patternStreetNb.matcher(hsaAddress);

    if (matcherStreetName.find()) {
      String streetName = matcherStreetName.group().trim();

      List<String> streetNameParts = new ArrayList<String>();
      if (!StringUtil.isEmpty(additionalInfo)) {
        streetNameParts.add(additionalInfo);
      }
      streetNameParts.add(streetName);
      streetName = StringUtil.concatenate(streetNameParts);
      address.setStreetName(streetName);
    }
    if (matcherStreetNb.find()) {
      String streetNb = matcherStreetNb.group();
      address.setStreetNumber(streetNb);
    }
  }
}
