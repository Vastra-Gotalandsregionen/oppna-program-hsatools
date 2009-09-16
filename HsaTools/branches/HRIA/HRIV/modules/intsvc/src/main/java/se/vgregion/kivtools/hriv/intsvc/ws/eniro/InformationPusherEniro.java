package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Description;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.EAliasType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.TelephoneType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.AddressType.GeoCoordinates;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.BusinessClassification;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.Locality;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.Management;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.UnitType.VisitingConditions;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;

import com.domainlanguage.time.TimePoint;

/**
 * 
 * @author david
 * 
 */
public class InformationPusherEniro implements InformationPusher {

  private static final String LAST_SYNCHED_MODIFY_DATE_PROPERTY = "LastSynchedModifyDate";
  private static final String LAST_EXISTING_UNITS_FILE_NAME = "leuf.serialized";
  private Log logger = LogFactory.getLog(this.getClass());
  private UnitRepository unitRepository;
  private String organizationCountry = "Sweden";
  private String organizationName = "VGR";
  private String organizationId = "vgr";
  private Map<String, DN> lastExistingUnits;
  private File lastExistingUnitsFile;
  private List<Unit> units;
  private FtpClient ftpClient;
  private CodeTablesService codeTablesService;

  @Required
  public void setFtpClient(FtpClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  @Required
  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  public void setLastExistingUnitsFile(File lastExistingUnitsFile) {
    this.lastExistingUnitsFile = lastExistingUnitsFile;
  }

  public void setUnitRepository(UnitRepository unitRepository) {
    this.unitRepository = unitRepository;
  }

  /**
   * Harvest units that needs to be synched.
   * 
   * @return
   * @throws Exception
   */
  private List<Unit> collectUnits() {
    List<Unit> modifiedUnits = null;
    try {
      units = unitRepository.getAllUnits();
      // Get units that has been created or modified
      modifiedUnits = getCreatedMovedOrRemovedUnits(units, getLastSynchDate());
      // Get units that has been removed
      List<Unit> removedUnits = getRemovedUnits(units);
      modifiedUnits.addAll(removedUnits);
    } catch (Exception e) {
      modifiedUnits = new ArrayList<Unit>();
    }
    return modifiedUnits;
  }

  private Date getLastSynchDate() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    return new Date(prefs.getLong(LAST_SYNCHED_MODIFY_DATE_PROPERTY, 0));
  }

  private void saveLastSynchedModifyDate(Date lastSynchedModifyDate) {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.putLong(LAST_SYNCHED_MODIFY_DATE_PROPERTY, lastSynchedModifyDate.getTime());
  }

  /**
   * Returns units created/modified since specified date.
   * 
   * @param unitList The complete list of units.
   * @param lastSynchDate The date of the last synchronization.
   * @return A list of units which have been created/modified since the specified date.
   */
  private List<Unit> getCreatedMovedOrRemovedUnits(List<Unit> unitList, Date lastSynchDate) {
    List<Unit> createdAndMovedUnits = new ArrayList<Unit>();
    TimePoint lastSynchedModifyTimePoint = TimePoint.from(lastSynchDate);
    TimePoint temporaryLatestModifiedTimepoint = TimePoint.from(lastSynchDate);

    for (Unit unit : unitList) {
      if (unit != null) {
        TimePoint createTimestamp = unit.getCreateTimestamp();
        TimePoint modifyTimestamp = unit.getModifyTimestamp();
        // Check if the unit is created, moved or modified after last synch modify date
        if (isAfterTimePoint(modifyTimestamp, lastSynchedModifyTimePoint) || isAfterTimePoint(createTimestamp, lastSynchedModifyTimePoint)) {
          createdAndMovedUnits.add(unit);
          if (isAfterTimePoint(createTimestamp, lastSynchedModifyTimePoint)) {
            DN dn = lastExistingUnits.get(unit.getHsaIdentity());
            // No DN found so unit is new.
            if (dn == null) {
              unit.setNew(true);
            } else if (!dn.equals(unit.getDn())) {
              // Unit dn has been changed since last synch. That means moved in ldap.
              unit.setMoved(true);
            }
          } else {
            unit.setUpdated(true);
          }
        }
        // Update latest in order to keep track of the latest creation/modification date in this batch
        temporaryLatestModifiedTimepoint = getLatestTimePoint(temporaryLatestModifiedTimepoint, createTimestamp);
        temporaryLatestModifiedTimepoint = getLatestTimePoint(temporaryLatestModifiedTimepoint, modifyTimestamp);
      }
    }
    // Latest in this batch
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.putLong(LAST_SYNCHED_MODIFY_DATE_PROPERTY, temporaryLatestModifiedTimepoint.asJavaUtilDate().getTime());
    return createdAndMovedUnits;
  }

  /**
   * We need to keep track of units that don't exists anymore. Compare to previous state and add virtual units that indicates the removal.
   * 
   * @param unitList
   * @return
   */
  private List<Unit> getRemovedUnits(List<Unit> unitList) {
    // Holds list of removed or moved units
    List<Unit> removedOrMovedUnitsList = new ArrayList<Unit>();

    // Sort to be able to perform binary search.
    Collections.sort(unitList);

    // Tag units as "moved" or "removed"
    for (Map.Entry<String, DN> unitEntry : lastExistingUnits.entrySet()) {
      Unit tmpUnit = new Unit();
      tmpUnit.setHsaIdentity(unitEntry.getKey());
      tmpUnit.setDn(unitEntry.getValue());

      // Search for moved units (they exists in list of current units)
      int unitPosition = Collections.binarySearch(unitList, tmpUnit);
      if (unitPosition < 0) {
        // Did not find unit in list of current units, ie removed!
        // Create virtual unit and tag as removed.
        Unit removedUnit = new Unit();
        removedUnit.setHsaIdentity(unitEntry.getKey());
        removedUnit.setRemoved(true);
        removedOrMovedUnitsList.add(removedUnit);
      }
    }
    return removedOrMovedUnitsList;
  }

  /**
   * Checks if a TimePoint is after another TimePoint.
   * 
   * @param timePointToCheck The TimePoint to check.
   * @param timePointToCheckAgainst The other TimePoint to check against.
   * @return True if the TimePoint is after the other TimePoint, otherwise false.
   */
  private boolean isAfterTimePoint(TimePoint timePointToCheck, TimePoint timePointToCheckAgainst) {
    boolean result = false;
    result = timePointToCheck != null && timePointToCheck.isAfter(timePointToCheckAgainst);
    return result;
  }

  /**
   * Returns the latest TimePoint of the two provided TimePoints.
   * 
   * @param first The first TimePoint to compare.
   * @param second The second TimePoint to compare.
   * @return The latest of the two provided TimePoints.
   */
  private TimePoint getLatestTimePoint(TimePoint first, TimePoint second) {
    TimePoint result = first;
    if (isAfterTimePoint(second, first)) {
      result = second;
    }
    return result;
  }

  /**
   * Load last existing units information when initializing spring bean.
   */
  public void initLoadLastExistingUnitsInRepository() {
    // Read last existing units from file
    if (lastExistingUnits == null) {
      // Started for the first time File not exist. Create new default file
      if (lastExistingUnitsFile == null) {
        // Assume that default home location is used. Try to read file from home dir.
        lastExistingUnitsFile = getDefaultLastExistingFile();
        // Can't read default file then this is first time program is running or file has been deleted.
        if (!lastExistingUnitsFile.exists()) {
          lastExistingUnits = new HashMap<String, DN>();
          resetLastSynchedModifyDate();
        } else {
          readFileContent();
        }
      } else if (!lastExistingUnitsFile.exists()) {
        lastExistingUnits = new HashMap<String, DN>();
        // Remove last synch date if new file.
        resetLastSynchedModifyDate();
      } else if (lastExistingUnitsFile.exists()) {
        // lastExistingUnitsFile is not set, create a default.
        readFileContent();
      }
    }
  }

  /**
   * Reset the date of the latest modified entity in ldap.
   */
  private void resetLastSynchedModifyDate() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    prefs.remove("LastSynchedModifyDate");
  }

  @SuppressWarnings("unchecked")
  private void readFileContent() {
    try {
      FileInputStream fileInputStream = new FileInputStream(lastExistingUnitsFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      lastExistingUnits = (HashMap<String, DN>) objectInputStream.readObject();
      objectInputStream.close();
      fileInputStream.close();
    } catch (IOException e) {
      logger.error("Error in getLastExistingUnitsInRepository", e);
    } catch (ClassNotFoundException e) {
      logger.error("Error in getLastExistingUnitsInRepository", e);
    }
  }

  private void saveLastExistingUnitList() {
    try {
      for (Unit unit : units) {
        lastExistingUnits.put(unit.getHsaIdentity(), unit.getDn());
      }
      if (lastExistingUnitsFile == null) {
        lastExistingUnitsFile = getDefaultLastExistingFile();
      }
      FileOutputStream fileOutputStream = new FileOutputStream(lastExistingUnitsFile);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
      objectOutputStream.writeObject(lastExistingUnits);
      objectOutputStream.flush();
      objectOutputStream.close();
      fileOutputStream.flush();
      fileOutputStream.close();
    } catch (IOException e) {
      logger.error("Error in saveLastExistingUnitList", e);
    }
  }

  private File getDefaultLastExistingFile() {
    if (lastExistingUnitsFile == null) {
      String homeFolder = System.getProperty("user.home");
      File hrivSettingsFolder = new File(homeFolder, ".hriv");
      if (!hrivSettingsFolder.exists()) {
        hrivSettingsFolder.mkdir();
      }
      lastExistingUnitsFile = new File(hrivSettingsFolder, LAST_EXISTING_UNITS_FILE_NAME);
    }
    return lastExistingUnitsFile;
  }

  /**
   * Trigger service for generate unit tree xml file and push it to chosen ftp server.
   * 
   * @inheritDoc
   */
  public void doService() {
    try {
      long startTime = System.currentTimeMillis();
      // Check if there is a saved lastSynchDate. If not this is the first time and we must generate full organization.
      boolean generateFullOrg = isFullOrganizationTreeMode();
      logger.info("Unit details pusher: Started " + (generateFullOrg ? "full" : "incremental") + " upload.");
      // Get units that belongs to the organization.
      List<Unit> collectedUnits = collectUnits();
      logger.debug("Unit details pusher: Found " + collectedUnits.size() + " units to upload.");
      // Generate organization tree object.
      Organization organization = generateOrganizationTree(generateFullOrg, collectedUnits);
      // create XML presentation of organization tree object.
      String generatedUnitDetailsXmlFile = generateUnitDetailsXmlFile(organization);
      if (organization.getUnit().size() > 0) {
        if (ftpClient.sendFile(generatedUnitDetailsXmlFile)) {
          // Save last synch date to file after successful commit of xml file to ftp
          saveLastExistingUnitList();
          long endTime = System.currentTimeMillis();
          long elapsedTime = endTime - startTime;
          logger.info("Unit details pusher: Completed with success. Total time: " + elapsedTime / 1000 + " s.");
        } else {
          // Commit to ftp was unsuccessful. Reset the lastSynchedModifyDate
          saveLastSynchedModifyDate(new Date(0));
          logger.error("Unit details pusher: Completed with failure.");
        }
      }
    } catch (JAXBException e) {
      logger.error("Error in doService", e);
    }
  }

  /**
   * If lastExistingUnitsFile exist, then no full organization tree should be generated.
   * 
   * @return boolean if full organization tree should be generated.
   */
  private boolean isFullOrganizationTreeMode() {
    boolean generateFullOrganizationTree = true;
    if (lastExistingUnitsFile != null && lastExistingUnitsFile.exists()) {
      generateFullOrganizationTree = !getLastSynchDate().after(Constants.parseStringToZuluTime("19700102000000Z"));
    } else {
      resetLastSynchedModifyDate();
    }
    return generateFullOrganizationTree;
  }

  /**
   * Generate organization tree object.
   * 
   * @param generateFullOrg If true full organization tree is generated.
   * @param collectedUnits List with units that belongs to the organization
   * @return Organization object containing units.
   */
  private Organization generateOrganizationTree(boolean generateFullOrg, List<Unit> collectedUnits) {
    Organization organization;
    if (generateFullOrg) {
      organization = generateOrganisationTree(collectedUnits);
      organization.setLoadType("Full");
      // TODO: bryt ut Full till enum
    } else {
      organization = generateFlatOrganization(collectedUnits);
      organization.setLoadType("Increment");
      // TODO: bryt ut Increment till enum
    }
    organization.setId(organizationId);
    organization.setName(organizationName);
    organization.setCountry(organizationCountry);
    return organization;
  }

  /**
   * Generate xml file with newly updated units.
   * 
   * @throws JAXBException .
   */
  private String generateUnitDetailsXmlFile(Organization organization) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(organization.getClass());
    Marshaller marshaller = context.createMarshaller();
    StringWriter fileContent = new StringWriter();
    marshaller.marshal(organization, fileContent);
    // Push (upload) XML to specified resource
    return fileContent.toString();
  }

  /**
   * Transfer unit state to jaxb representation of unit.
   * 
   * @param unit
   * @param jaxbUnit
   */
  private void fillJaxbUnit(Unit unit, se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit jaxbUnit) {
    if (unit.isNew() || unit.isMoved() || unit.isUpdated()) {
      fillJaxbUnitWithData(unit, jaxbUnit);
    } else {
      jaxbUnit.setId(unit.getHsaIdentity());
      jaxbUnit.setName(unit.getName());
    }

    Unit parentUnit = null;
    if (unit.isNew()) {
      jaxbUnit.setOperation(Operation.CREATE.value);
      parentUnit = getParentUnit(unit);
    } else if (unit.isRemoved()) {
      jaxbUnit.setOperation(Operation.REMOVE.value);
    } else if (unit.isMoved()) {
      jaxbUnit.setOperation(Operation.MOVE.value);
      parentUnit = getParentUnit(unit);
    } else if (unit.isUpdated()) {
      jaxbUnit.setOperation(Operation.UPDATE.value);
    }
    if (parentUnit != null) {
      jaxbUnit.setParentUnitId(parentUnit.getHsaIdentity());
    }
  }

  private se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit fillJaxbUnitWithData(Unit unit, se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit jaxbUnit) {
    jaxbUnit.setId(unit.getHsaIdentity());
    jaxbUnit.setName(unit.getName());

    // Description
    Description description = new Description();
    description.setValue(unit.getConcatenatedDescription());
    jaxbUnit.getDescriptionOrImageOrAddress().add(description);

    // Set unitWs street address
    Address streetAddress = new Address();
    streetAddress.setType("Visit");
    // address.setLabel(value);
    setStreetNameAndNumberForAddress(streetAddress, unit.getHsaStreetAddress().getStreet());
    List<String> streetPostCodes = streetAddress.getPostCode();
    streetPostCodes.add(unit.getHsaStreetAddress().getZipCode().toString());
    streetAddress.setCity(unit.getHsaStreetAddress().getCity());
    GeoCoordinates streetGeoCoordinates = new GeoCoordinates();
    streetGeoCoordinates.setXpos(BigInteger.valueOf(unit.getRt90X()));
    streetGeoCoordinates.setYpos(BigInteger.valueOf(unit.getRt90Y()));
    streetAddress.setGeoCoordinates(streetGeoCoordinates);
    jaxbUnit.getDescriptionOrImageOrAddress().add(streetAddress);

    // Set unitWs postal address
    Address postalAddress = new Address();
    postalAddress.setType("Post");
    // address.setLabel(value);
    setStreetNameAndNumberForAddress(postalAddress, unit.getHsaPostalAddress().getStreet());
    postalAddress.getPostCode().add(unit.getHsaPostalAddress().getZipCode().toString());
    postalAddress.setCity(unit.getHsaPostalAddress().getCity());
    GeoCoordinates postalGeoCoordinates = new GeoCoordinates();
    postalGeoCoordinates.setXpos(BigInteger.valueOf(unit.getRt90X()));
    postalGeoCoordinates.setYpos(BigInteger.valueOf(unit.getRt90Y()));
    postalAddress.setGeoCoordinates(postalGeoCoordinates);
    jaxbUnit.getDescriptionOrImageOrAddress().add(postalAddress);

    // Set telephone
    TelephoneType telephoneType = new TelephoneType();
    for (PhoneNumber phoneNumber : unit.getHsaPublicTelephoneNumber()) {
      telephoneType.getTelephoneNumber().add(phoneNumber.getPhoneNumber());

    }
    // Telephone hours
    String telephoneHoursConcatenated = "";
    for (WeekdayTime telephoneHoursInfo : unit.getHsaTelephoneTime()) {
      telephoneHoursConcatenated += telephoneHoursInfo.getDisplayValue() + ", ";
    }
    telephoneHoursConcatenated = stripEndingCommaAndSpace(telephoneHoursConcatenated);
    telephoneType.setTelephoneHours(telephoneHoursConcatenated);
    jaxbUnit.getDescriptionOrImageOrAddress().add(telephoneType);

    // Set URL
    EAliasType unitWeb = new EAliasType();
    unitWeb.setLabel("Mottagningens webbplats");
    unitWeb.setType("URL");
    unitWeb.setAlias(unit.getLabeledURI());
    jaxbUnit.getDescriptionOrImageOrAddress().add(unitWeb);

    // Set visiting rules
    VisitingConditions visitingConditions = new VisitingConditions();
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
    jaxbUnit.getDescriptionOrImageOrAddress().add(visitingConditions);

    // Management
    Management management = new Management();
    management.setValue(unit.getHsaManagementText());
    jaxbUnit.setManagement(management);

    // Business classification
    List<String> businessClassifications = unit.getHsaBusinessClassificationCode();

    for (String businessClassificationCode : businessClassifications) {
      BusinessClassification businessClassification = new BusinessClassification();
      String bcName = codeTablesService.getValueFromCode(CodeTableName.HSA_BUSINESSCLASSIFICATION_CODE, businessClassificationCode);
      businessClassification.setBCName(bcName);
      businessClassification.setBCCode(businessClassificationCode);
      jaxbUnit.getDescriptionOrImageOrAddress().add(businessClassification);
    }

    // Location
    Locality locality = new Locality();
    locality.setValue(unit.getHsaMunicipalityName());
    // TODO set attribut beroende på om stadsdel eller kommun
    jaxbUnit.getDescriptionOrImageOrAddress().add(locality);

    return jaxbUnit;
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

  private String stripEndingCommaAndSpace(String inputString) {
    String result = inputString;
    if (result.endsWith(", ")) {
      result = result.substring(0, result.length() - 2);
    }
    return result;
  }

  private Unit getParentUnit(Unit unit) {
    DN dn = unit.getDn();
    DN parentDn = dn.getParentDN(2);
    if (parentDn != null) {
      Unit parent;
      try {
        parent = unitRepository.getUnitByDN(parentDn);
      } catch (Exception e) {
        // Not necessary to log logger.error("Error in getParentUnit", e);
        parent = null;
      }
      return parent;
    } else {
      return null;
    }
  }

  /**
   * For full unit detail pushes.
   * 
   * @param unitList The units to populate the Organization object with.
   * @return An Organization object populated from the provided units.
   */
  public Organization generateOrganisationTree(List<Unit> unitList) {
    Map<DN, Unit> unitContainer = new HashMap<DN, Unit>();
    Map<DN, List<Unit>> unitChildrenContainer = new HashMap<DN, List<Unit>>();
    List<Unit> rootUnits = new ArrayList<Unit>();
    for (Unit unit : unitList) {
      DN dn = unit.getDn();
      if (dn != null) {
        DN parentDn = dn.getParentDN(2);
        unitContainer.put(dn, unit);
        if (parentDn == null) {
          rootUnits.add(unit);
        } else {
          List<Unit> childrenList = unitChildrenContainer.get(parentDn);
          if (childrenList == null) {
            childrenList = new ArrayList<Unit>();
            unitChildrenContainer.put(parentDn, childrenList);
          }
          childrenList.add(unit);
        }
      }
    }

    Organization organization = new Organization();
    for (Unit rootUnit : rootUnits) {
      se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit jaxbRootUnit = new se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit();
      fillJaxbUnit(rootUnit, jaxbRootUnit);
      populateUnit(jaxbRootUnit, rootUnit.getDn(), unitChildrenContainer);
      organization.getUnit().add(jaxbRootUnit);
    }

    return organization;
  }

  /**
   * For incremental unit detail pushes.
   * 
   * @param collectedUnits
   * @return Organization with a unit list of created,removed or modified units
   */
  private Organization generateFlatOrganization(List<Unit> collectedUnits) {
    Organization organization = new Organization();
    for (Unit unit : collectedUnits) {
      se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit jaxbUnit = new se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit();
      fillJaxbUnit(unit, jaxbUnit);
      organization.getUnit().add(jaxbUnit);
    }
    return organization;
  }

  /**
   * 
   * @param parentJaxbUnit Parent unit
   * @param dn DN of parent unit
   * @param unitChildrenContainer Map holding parent - children relations
   */
  private void populateUnit(se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit parentJaxbUnit, DN dn, Map<DN, List<Unit>> unitChildrenContainer) {
    // Get children for current parent
    List<Unit> childUnits = unitChildrenContainer.get(dn);
    if (childUnits != null) {
      for (Unit child : childUnits) {
        se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit jaxbChildUnit = new se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit();
        fillJaxbUnit(child, jaxbChildUnit);
        parentJaxbUnit.getUnit().add(jaxbChildUnit);
        populateUnit(jaxbChildUnit, child.getDn(), unitChildrenContainer);
      }
    }
  }

  /**
   * Constants for Eniro operation.
   * 
   * @author david
   * 
   */
  private enum Operation {
    CREATE("create"), MOVE("move"), REMOVE("remove"), UPDATE("update");
    private String value;

    Operation(String value) {
      this.value = value;
    }
  }
}
