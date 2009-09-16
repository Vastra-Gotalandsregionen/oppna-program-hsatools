package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Description;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.EAliasType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.ObjectFactory;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.Locality;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitType.VisitingConditions;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;

public class UnitDetailsServiceImpl implements UnitDetailsService<Organization> {

  private UnitRepository unitRepository;
  private ObjectFactory objectFactory = new ObjectFactory();

  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  public Organization getUnitDetails(String hsaIdentity) {
    Organization organization = objectFactory.createOrganization();
    if (hsaIdentity != null && !"".equals(hsaIdentity)) {
      Unit unit = null;
      try {
        unit = unitRepository.getUnitByHsaId(hsaIdentity);
      } catch (Exception e) {
        e.printStackTrace();
      }
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
    Locality locality = new Locality();
    locality.setValue(unit.getHsaMunicipalityName());
    unitWs.setLocality(locality);

    // Set if unit is connected to MVK
    unitWs.setMvkEnable(checkIfConnectedToMvk(unit));

    return unitWs;
  }

  private void setAddress(se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Unit unitWs, se.vgregion.kivtools.search.svc.domain.values.Address address, String addressType, Unit unit) {
    // Set unitWs street address
    Address wsAddress = objectFactory.createAddress();

    if ("".equalsIgnoreCase(address.getStreet())) {
      wsAddress.setIsConcatenated(true);
      wsAddress.setConcatenatedAddress(address.getAdditionalInfoToString());
    } else {
      wsAddress.setType(addressType);
      setStreetNameAndNumberForAddress(wsAddress, address.getStreet());
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
    if (inputString.endsWith(", ")) {
      inputString = inputString.substring(0, inputString.length() - 2);
    }
    return inputString;
  }

  private void setStreetNameAndNumberForAddress(Address address, String hsaAddress) {
    Pattern patternStreetName = Pattern.compile("\\D+");
    Matcher matcherStreetName = patternStreetName.matcher(hsaAddress);
    Pattern patternStreetNb = Pattern.compile("\\d+\\w*");
    Matcher matcherStreetNb = patternStreetNb.matcher(hsaAddress);

    if (matcherStreetName.find()) {
      String streetName = matcherStreetName.group().trim();
      address.setStreetName(streetName);
    }
    if (matcherStreetNb.find()) {
      String streetNb = matcherStreetNb.group();
      address.setStreetNumber(streetNb);
    }
  }
}
