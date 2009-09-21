package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.EniroUnitMapper;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;

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
  private String organizationCountry = "Sweden";
  private String organizationName = "VGR";
  private String organizationId = "vgr";
  private Map<String, String> lastExistingUnits;
  private File lastExistingUnitsFile;
  private List<UnitComposition> units;
  private FtpClient ftpClient;
  private LdapTemplate ldapTemplate;
  private String parentDn;

  /**
   * Constants for Eniro operation.
   * 
   * @author David Bennehult & Joakim Olsson
   * 
   */
  private enum Operation {
    CREATE("create"), MOVE("move"), REMOVE("remove"), UPDATE("update");
    private String value;

    Operation(String value) {
      this.value = value;
    }
  }
  
  /**
   * 
   * @author david
   *
   */
  private enum  LOAD_TYPE {
    FULL("Full"), INCREMENT("Increment");
    private String value;
    private LOAD_TYPE(String value) {
      this.value = value;
    }
  }

  @Required
  public void setParentDn(String parentDn) {
    this.parentDn = parentDn;
  }

  @Required
  public void setFtpClient(FtpClient ftpClient) {
    this.ftpClient = ftpClient;
  }

  public void setLastExistingUnitsFile(File lastExistingUnitsFile) {
    this.lastExistingUnitsFile = lastExistingUnitsFile;
  }

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Harvest units that needs to be synched.
   * 
   * @return
   * @throws Exception
   */
  private List<UnitComposition> collectUnits() {
    List<UnitComposition> modifiedUnits = null;
    units = getUnitsFromLdap();
    // Get units that has been created or modified
    modifiedUnits = getCreatedMovedOrRemovedUnits(units, getLastSynchDate());
    // Get units that has been removed
    List<UnitComposition> removedUnits = getRemovedUnits(units);
    modifiedUnits.addAll(removedUnits);
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
  private List<UnitComposition> getCreatedMovedOrRemovedUnits(List<UnitComposition> unitList, Date lastSynchDate) {
    List<UnitComposition> createdAndMovedUnits = new ArrayList<UnitComposition>();
    TimePoint lastSynchedModifyTimePoint = TimePoint.from(lastSynchDate);
    TimePoint temporaryLatestModifiedTimepoint = TimePoint.from(lastSynchDate);

    for (UnitComposition unit : unitList) {
      if (unit != null) {
        TimePoint createTimestamp = unit.getCreateTimePoint();
        TimePoint modifyTimestamp = unit.getModifyTimePoint();
        // Check if the unit is created, moved or modified after last synch modify date
        if (isAfterTimePoint(modifyTimestamp, lastSynchedModifyTimePoint) || isAfterTimePoint(createTimestamp, lastSynchedModifyTimePoint)) {
          createdAndMovedUnits.add(unit);
          if (isAfterTimePoint(createTimestamp, lastSynchedModifyTimePoint)) {
            String dn = lastExistingUnits.get(unit.getEniroUnit().getId());
            // No DN found so unit is new.
            if (dn == null) {
              unit.getEniroUnit().setOperation(Operation.CREATE.value);
            } else if (!dn.equals(unit.getDn())) {
              // Unit dn has been changed since last synch. That means moved in ldap.
              unit.getEniroUnit().setOperation(Operation.MOVE.value);
            }
          } else {
            unit.getEniroUnit().setOperation(Operation.UPDATE.value);
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
  private List<UnitComposition> getRemovedUnits(List<UnitComposition> unitList) {
    // Holds list of removed or moved units
    List<UnitComposition> removedOrMovedUnitsList = new ArrayList<UnitComposition>();

    // Sort to be able to perform binary search.
    Collections.sort(unitList);

    // Tag units as "moved" or "removed"
    for (Map.Entry<String, String> unitEntry : lastExistingUnits.entrySet()) {
      UnitComposition unitComposition = new UnitComposition();
      unitComposition.setDn(unitEntry.getValue().toString());

      unitComposition.getEniroUnit().setId(unitEntry.getKey());

      // Search for moved units (they exists in list of current units)
      int unitPosition = Collections.binarySearch(unitList, unitComposition);
      if (unitPosition < 0) {
        // Did not find unit in list of current units, ie removed!
        // Create virtual unit and tag as removed.
        UnitComposition tmp = new UnitComposition();
        tmp.getEniroUnit().setId(unitEntry.getKey());
        tmp.getEniroUnit().setOperation(Operation.REMOVE.value);
        removedOrMovedUnitsList.add(tmp);
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
          lastExistingUnits = new HashMap<String, String>();
          resetLastSynchedModifyDate();
        } else {
          readFileContent();
        }
      } else if (!lastExistingUnitsFile.exists()) {
        lastExistingUnits = new HashMap<String, String>();
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
      lastExistingUnits = (HashMap<String, String>) objectInputStream.readObject();
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
      for (UnitComposition unit : units) {
        lastExistingUnits.put(unit.getEniroUnit().getId(), unit.getDn());
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
      if (generateFullOrg) {
        logger.info("Unit details pusher: Started full upload.");
      }else {
        logger.info("Unit details pusher: Started incrementa upload.");
      }
      // Get units that belongs to the organization.
      List<UnitComposition> collectedUnits = collectUnits();
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
  private Organization generateOrganizationTree(boolean generateFullOrg, List<UnitComposition> collectedUnits) {
    Organization organization;
    if (generateFullOrg) {
      organization = generateOrganisationTree(collectedUnits);
      organization.setLoadType(LOAD_TYPE.FULL.value);
    } else {
      organization = generateFlatOrganization(collectedUnits);
      organization.setLoadType(LOAD_TYPE.INCREMENT.value);
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
   * For full unit detail pushes.
   * 
   * @param unitList The units to populate the Organization object with.
   * @return An Organization object populated from the provided units.
   */
  public Organization generateOrganisationTree(List<UnitComposition> unitList) {
    Map<String, UnitComposition> unitContainer = new HashMap<String, UnitComposition>();
    HashMap<String, List<UnitComposition>> unitChildrenContainer = new HashMap<String, List<UnitComposition>>();
    List<UnitComposition> rootUnits = new ArrayList<UnitComposition>();
    for (UnitComposition unit : unitList) {
      String dn = unit.getDn();
      if (dn != null) {
        String unitParentDn = unit.getParentDn();
        unitContainer.put(dn, unit);
        if (parentDn.equals(unitParentDn)) { 
          rootUnits.add(unit);
        } else {
          List<UnitComposition> childrenList = unitChildrenContainer.get(unitParentDn);
          if (childrenList == null) {
            childrenList = new ArrayList<UnitComposition>();
            unitChildrenContainer.put(unitParentDn, childrenList);
          }
          childrenList.add(unit);
        }
      }
    }

    Organization organization = new Organization();
    for (UnitComposition rootUnit : rootUnits) {
      populateUnit(rootUnit, rootUnit.getDn(), unitChildrenContainer);
      organization.getUnit().add(rootUnit.getEniroUnit());
    }

    return organization;
  }

  /**
   * For incremental unit detail pushes.
   * 
   * @param collectedUnits
   * @return Organization with a unit list of created,removed or modified units
   */
  private Organization generateFlatOrganization(List<UnitComposition> collectedUnits) {
    Organization organization = new Organization();
    for (UnitComposition unit : collectedUnits) {
      organization.getUnit().add(unit.getEniroUnit());
    }
    return organization;
  }

  /**
   * 
   * @param parentJaxbUnit Parent unit
   * @param dn DN of parent unit
   * @param unitChildrenContainer Map holding parent - children relations
   */
  private void populateUnit(UnitComposition parentUnit, String dn, Map<String, List<UnitComposition>> unitChildrenContainer) {
    // Get children for current parent
    List<UnitComposition> childUnits = unitChildrenContainer.get(dn);
    if (childUnits != null) {
      for (UnitComposition child : childUnits) {
        parentUnit.getEniroUnit().getUnit().add(child.getEniroUnit());
        populateUnit(child, child.getDn(), unitChildrenContainer);
      }
    }
  }

  private List<UnitComposition> getUnitsFromLdap() {
    OrFilter filter = new OrFilter();
    filter.or(new EqualsFilter("objectClass", "vgrOrganizationalUnit"));
    filter.or(new EqualsFilter("objectClass", "vgrOrganizationalRole"));
    filter.or(new EqualsFilter("objectClass", "organizationalUnit"));
    @SuppressWarnings("unchecked")
    List<UnitComposition> unitsList = ldapTemplate.search("", filter.encode(), new EniroUnitMapper());
    setParentIdsForUnits(unitsList);
    return unitsList;
  }

  private void setParentIdsForUnits(List<UnitComposition> compositions) {

    Map<String, UnitComposition> unitsMap = new HashMap<String, UnitComposition>();

    for (UnitComposition unitComposition : compositions) {
      unitsMap.put(unitComposition.getDn(), unitComposition);
    }

    for (UnitComposition unitComposition : compositions) {
      UnitComposition parentUnit = unitsMap.get(unitComposition.getParentDn());
      if (parentUnit != null) {
        unitComposition.getEniroUnit().setParentUnitId(parentUnit.getEniroUnit().getId());
      }
    }

  }
}
