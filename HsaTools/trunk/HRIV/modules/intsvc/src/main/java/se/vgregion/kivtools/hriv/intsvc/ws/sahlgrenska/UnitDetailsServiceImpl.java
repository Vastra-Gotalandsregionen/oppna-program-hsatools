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
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType.GeoCoordinatesWGS84;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Description;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.EAliasType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ObjectFactory;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ReferralInformation;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.TemporaryInformation;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.Locality;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.VisitingConditions;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.util.MvkClient;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Service implementation for the webservice for retrieving unit information.
 * 
 * @author Jonas Liljenfelt & David Bennehult
 */
public class UnitDetailsServiceImpl implements UnitDetailsService<Organization> {
  private final Log log = LogFactory.getLog(this.getClass());
  private SearchService searchService;
  private MvkClient mvkClient;
  private final ObjectFactory objectFactory = new ObjectFactory();

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setMvkClient(MvkClient mvkClient) {
    this.mvkClient = mvkClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Organization getUnitDetails(String hsaIdentity) {
    Organization organization = this.objectFactory.createOrganization();
    if (hsaIdentity != null && !"".equals(hsaIdentity)) {
      Unit unit = null;
      try {
        unit = this.searchService.getUnitByHsaId(hsaIdentity);
      } catch (KivException e) {
        this.log.error("Unable to retrieve unit details.", e);
      }
      if (!StringUtil.isEmpty(unit.getHsaIdentity())) {
        this.mvkClient.assignCaseTypes(unit);
        organization.getUnit().add(this.generateWebServiceUnit(unit));
      }
    }
    return organization;
  }

  private se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit generateWebServiceUnit(Unit unit) {
    se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unitWs = this.objectFactory.createUnit();
    unitWs.setId(unit.getHsaIdentity());
    unitWs.setName(unit.getName());
    unitWs.setShortName(unit.getOrganizationalUnitNameShort());

    Description description = new Description();
    description.setValue(unit.getConcatenatedDescription());
    unitWs.getDescription().add(description);

    if (unit.getShouldVgrTempInfoBeShown()) {
      TemporaryInformation temporaryInformation = new TemporaryInformation();
      temporaryInformation.setValue(unit.getVgrTempInfoBody());
      unitWs.getTemporaryInformation().add(temporaryInformation);

      ReferralInformation referralInformation = new ReferralInformation();
      referralInformation.setValue(unit.getVgrRefInfo());
      unitWs.getReferralInformation().add(referralInformation);
    }

    this.setAddress(unitWs, unit.getHsaStreetAddress(), "Visit", unit);
    this.setAddress(unitWs, unit.getHsaPostalAddress(), "Post", unit);

    unitWs.getDrivingDirections().addAll(unit.getHsaRoute());

    TelephoneType telephoneType = new TelephoneType();
    for (PhoneNumber phoneNumber : unit.getHsaPublicTelephoneNumber()) {
      telephoneType.getTelephoneNumber().add(phoneNumber.getPhoneNumber());
    }
    unitWs.getTelephone().add(telephoneType);

    EAliasType unitWeb = new EAliasType();
    unitWeb.setLabel("Mottagningens webbplats");
    unitWeb.setType("URL");
    unitWeb.setAlias(unit.getLabeledURI());
    unitWs.getEAlias().add(unitWeb);

    EAliasType email = new EAliasType();
    email.setLabel("Mottagningens e-post");
    email.setType("E-mail");
    email.setAlias(unit.getMail());
    unitWs.getEAlias().add(email);

    VisitingConditions visitingConditions = new UnitType.VisitingConditions();
    visitingConditions.setVisitingRules(unit.getHsaVisitingRules());

    String dropInConcatenated = "";
    for (WeekdayTime dropInInfo : unit.getHsaDropInHours()) {
      dropInConcatenated += dropInInfo.getDisplayValue() + ", ";
    }
    dropInConcatenated = this.stripEndingCommaAndSpace(dropInConcatenated);
    visitingConditions.setDropInHours(dropInConcatenated);

    String visitingHoursConcatenated = "";
    for (WeekdayTime visitingHoursInfo : unit.getHsaSurgeryHours()) {
      visitingHoursConcatenated += visitingHoursInfo.getDisplayValue() + ", ";
    }
    visitingHoursConcatenated = this.stripEndingCommaAndSpace(visitingHoursConcatenated);
    visitingConditions.setVisitingHours(visitingHoursConcatenated);

    String telephoneHoursConcatenated = "";
    for (WeekdayTime telephoneHoursInfo : unit.getHsaTelephoneTime()) {
      telephoneHoursConcatenated += telephoneHoursInfo.getDisplayValue() + ", ";
    }
    telephoneHoursConcatenated = this.stripEndingCommaAndSpace(telephoneHoursConcatenated);
    visitingConditions.setTelephoneHours(telephoneHoursConcatenated);
    unitWs.getVisitingConditions().add(visitingConditions);

    UnitType.Management management = new UnitType.Management();
    if (StringUtil.isEmpty(unit.getHsaManagementText())) {
      management.setValue("-");
    } else {
      management.setValue(unit.getHsaManagementText());
    }
    unitWs.setManagement(management);

    List<HealthcareType> businessClassifications = unit.getHealthcareTypes();
    for (HealthcareType businessClassificationHT : businessClassifications) {
      UnitType.BusinessClassification businessClassification = new UnitType.BusinessClassification();
      businessClassification.setValue(businessClassificationHT.getDisplayName());
      unitWs.getBusinessClassification().add(businessClassification);
    }

    unitWs.setCareType(unit.getCareType());

    if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
      Locality locality = new Locality();
      locality.setValue(unit.getHsaMunicipalityName());
      unitWs.setLocality(locality);
    }

    unitWs.setMvkEnable(this.checkIfConnectedToMvk(unit));
    unitWs.getMvkServices().addAll(unit.getMvkCaseTypes());

    return unitWs;
  }

  private void setAddress(se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unitWs, se.vgregion.kivtools.search.domain.values.Address address, String addressType, Unit unit) {
    if (address != null) {
      Address wsAddress = this.objectFactory.createAddress();

      if (StringUtil.isEmpty(address.getStreet())) {
        wsAddress.setIsConcatenated(true);
        wsAddress.setConcatenatedAddress(address.getAdditionalInfoToString());
      } else {
        wsAddress.setType(addressType);
        List<String> additionalInfo = new ArrayList<String>();
        additionalInfo.addAll(address.getAdditionalInfo());
        String additionalInfoConcatenated = StringUtil.concatenate(additionalInfo);
        this.setStreetNameAndNumberForAddress(wsAddress, address.getStreet(), additionalInfoConcatenated);
        List<String> postalPostCodes = wsAddress.getPostCode();
        postalPostCodes.add(address.getZipCode().toString());
        wsAddress.setCity(address.getCity());
      }
      GeoCoordinates postalGeoCoordinates = new AddressType.GeoCoordinates();
      postalGeoCoordinates.getXpos().add(String.valueOf(unit.getRt90X()));
      postalGeoCoordinates.getYpos().add(String.valueOf(unit.getRt90Y()));
      wsAddress.setGeoCoordinates(postalGeoCoordinates);
      GeoCoordinatesWGS84 postalGeoCoordinatesWgs84 = new AddressType.GeoCoordinatesWGS84();
      postalGeoCoordinatesWgs84.getLatitude().add(String.valueOf(unit.getWgs84LatRounded()));
      postalGeoCoordinatesWgs84.getLongitude().add(String.valueOf(unit.getWgs84LongRounded()));
      wsAddress.setGeoCoordinatesWGS84(postalGeoCoordinatesWgs84);
      unitWs.getAddress().add(wsAddress);
    }
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
