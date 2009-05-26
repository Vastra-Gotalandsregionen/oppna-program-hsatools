package se.vgregion.kivtools.search.svc.push.impl.eniro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.InformationPusher;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

import com.domainlanguage.time.TimePoint;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class InformationPusherEniro implements InformationPusher {

	Log logger = LogFactory.getLog(this.getClass());
	private Date lastSynchedModifyDate;
	private UnitRepository unitRepository;
	private File lastSynchedModifyDateFile;
	private String organizationCountry = "Sweden";
	private String organizationName = "VGR";
	private String organizationId = "vgr";
	private File destinationFolder;
	private String ftpUser;
	private String ftpHost;
	private String ftpPassword;
	private String ftpDestinationFolder;
	private JSch jsch;
	private Map<String, DN> lastExistingUnits;
	private File lastExistingUnitsFile;
	private List<Unit> units;
		
	public void setLastExistingUnitsFile(File lastExistingUnitsFile) {
		this.lastExistingUnitsFile = lastExistingUnitsFile;
	}

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	public void setFtpDestinationFolder(String ftpDestinationFolder) {
		this.ftpDestinationFolder = ftpDestinationFolder;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	@Required
	public void setDestinationFolder(File destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	@Required
	public void setLastSynchedModifyDateFile(File lastSynchedModifyDateFile) {
		this.lastSynchedModifyDateFile = lastSynchedModifyDateFile;
	}

	public void setUnitRepository(UnitRepository unitRepository) {
		this.unitRepository = unitRepository;
	}

	/**
	 * Harvest units that needs to be synched
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Unit> collectUnits() throws Exception {
		units = unitRepository.getAllUnits();
		// Get units that has been created or modified
		List<Unit> freshUnits = getFreshUnits(units);
		// Get units that has been removed
		List<Unit> removedOrMovedUnits = getRemovedOrMovedUnits(units);
		freshUnits.addAll(removedOrMovedUnits);
		return freshUnits;
	}

	private Date getLastSynchDate() {
		if (lastSynchedModifyDate == null) {
			// lastSynchDate is unknown, read from file
			try {
				// Started for the first time File not exist. Create new file
				if (lastSynchedModifyDateFile.exists()) {
					FileReader fileReader = new FileReader(lastSynchedModifyDateFile);
					BufferedReader br = new BufferedReader(fileReader);
					String lastSynchedModifyString = br.readLine();
					if (lastSynchedModifyString != null) {
						lastSynchedModifyDate = Constants.zuluTimeFormatter.parse(lastSynchedModifyString);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
			// Set default start synch date if read from file failed
			if (lastSynchedModifyDate == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(1970, Calendar.JANUARY, 1);
				lastSynchedModifyDate = calendar.getTime();
			}
		}
		return lastSynchedModifyDate;
	}

	private void saveLastSynchedModifyDate() {
		try {
			FileWriter fileWriter = new FileWriter(lastSynchedModifyDateFile);
			fileWriter.write(Constants.zuluTimeFormatter.format(lastSynchedModifyDate));
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * Returns units created/modified since specified date.
	 * 
	 * @param units
	 * 
	 * @param lastSynchedModifyDate
	 * @return
	 * @throws Exception
	 */
	private List<Unit> getFreshUnits(List<Unit> units) throws Exception {
		List<Unit> freshUnits = new ArrayList<Unit>();
		TimePoint lastSynchedModifyTimePoint = TimePoint.from(getLastSynchDate());
		TimePoint temporaryLatestModifiedTimepoint = TimePoint.from(getLastSynchDate());

		for (Unit unit : units) {
			if (unit != null) {
				TimePoint modifyTimestamp = unit.getModifyTimestamp();
				TimePoint createTimestamp = unit.getCreateTimestamp();
				// Check if the unit is created or modified after last synched
				// modify date
				if ((modifyTimestamp != null && modifyTimestamp.isAfter(lastSynchedModifyTimePoint)) || (createTimestamp != null && createTimestamp.isAfter(lastSynchedModifyTimePoint))) {
					freshUnits.add(unit);
					if (createTimestamp != null && createTimestamp.isAfter(lastSynchedModifyTimePoint)) {
						unit.setNew(true);
					}
				}

				// Update latest in order to keep track of the latest
				// creation/modification date in this batch
				if (createTimestamp != null && createTimestamp.isAfter(temporaryLatestModifiedTimepoint)) {
					temporaryLatestModifiedTimepoint = createTimestamp;
				}
				if (modifyTimestamp != null && modifyTimestamp.isAfter(temporaryLatestModifiedTimepoint)) {
					temporaryLatestModifiedTimepoint = modifyTimestamp;
				}
			}
		}

		// Latest in this batch
		lastSynchedModifyDate = temporaryLatestModifiedTimepoint.asJavaUtilDate();
		return freshUnits;
	}

	/**
	 * We need to keep track of units that don't exists anymore or that are
	 * moved. Compare to previous state and add virtual units that indicates the
	 * removal and tag moved units as "moved".
	 * 
	 * @param units
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Unit> getRemovedOrMovedUnits(List<Unit> units) {
		// Read last existing units from file
		if (lastExistingUnits == null) {
			try {
				// Started for the first time File not exist. Create new file
				if (!lastExistingUnitsFile.exists()) {
					lastExistingUnits = new HashMap<String, DN>();
				} else {
					FileInputStream fileInputStream = new FileInputStream(lastExistingUnitsFile);
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
					lastExistingUnits = (HashMap<String, DN>) objectInputStream.readObject();
					objectInputStream.close();
					fileInputStream.close();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}

		// Holds list of removed or moved units
		List<Unit> removedOrMovedUnitsList = new ArrayList<Unit>();

		// Tag units as "moved" or "removed"
		for (Map.Entry<String, DN> unitEntry : lastExistingUnits.entrySet()) {
			Unit tmpUnit = new Unit();
			tmpUnit.setHsaIdentity(unitEntry.getKey());

			// Search for moved units (they exists in list of current units)
			Collections.sort(units);
			int unitPosition = Collections.binarySearch(units, tmpUnit);
			if (unitPosition > -1) {
				Unit unitInRepository = units.get(unitPosition);
				if (!unitInRepository.getDn().toString().equals(unitEntry.getValue().toString())) {
					// Unit has been moved!
					unitInRepository.setMoved(true);
					removedOrMovedUnitsList.add(tmpUnit);
				}
			} else {
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

	private void saveLastExistingUnitList() {
		try {
			for (Unit unit : units) {
				lastExistingUnits.put(unit.getHsaIdentity(), unit.getDn());
			}
			FileOutputStream fileOutputStream = new FileOutputStream(lastExistingUnitsFile);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(lastExistingUnits);
			objectOutputStream.flush();
			objectOutputStream.close();
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void doService() {
		try {
			boolean generateFullOrg = !getLastSynchDate().after(Constants.zuluTimeFormatter.parse("19700102000000Z"));
			List<Unit> collectedUnits = collectUnits();
			Organization organization = null;
			if (generateFullOrg) {
				organization = generateOrganisationTree(collectedUnits);
			} else {
				organization = generateFlatOrganization(collectedUnits);
				// TODO: organization.setIncremental("true")
			}
			organization.setId(organizationId);
			organization.setName(organizationName);
			organization.setCountry(organizationCountry);
			sendXmlFile(generateUnitDetailsXmlFile(organization));
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Generate xml file with newly updated units
	 */
	private File generateUnitDetailsXmlFile(Organization organization) throws Exception {
		JAXBContext context = JAXBContext.newInstance(organization.getClass());
		Marshaller marshaller = context.createMarshaller();
		File organizationXmlFile = new File(destinationFolder, organization.getName() + ".xml");
		marshaller.marshal(organization, new FileWriter(organizationXmlFile));
		// Push (upload) XML to specified resource
		return organizationXmlFile;
	}

	/**
	 * Transfer unit state to jaxb representation of unit
	 * 
	 * @param unit
	 * @param jaxbUnit
	 */
	private void fillJaxbUnit(Unit unit, se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbUnit) {
		jaxbUnit.setId(unit.getHsaIdentity());
		jaxbUnit.setName(unit.getName());
		Unit parentUnit = null;
		if (unit.isNew()) {
			jaxbUnit.setOperation("create");
			parentUnit = getParentUnit(unit);
		} else if (unit.isRemoved()) {
			jaxbUnit.setOperation("remove");
		} else if (unit.isMoved()) {
			jaxbUnit.setOperation("move");
			parentUnit = getParentUnit(unit);
		}
		if (parentUnit != null) {
			jaxbUnit.setParentUnitId(parentUnit.getHsaIdentity());
		}
	}

	private void sendXmlFile(File organizationXmlFile) {
		try {
			Session session = jsch.getSession(ftpUser, ftpHost, 22);
			session.setPassword(ftpPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			channelSftp.put(new FileInputStream(organizationXmlFile), ftpDestinationFolder);
			channelSftp.disconnect();
			session.disconnect();
			// Save last synch date to file after successful commit of xml file
			// to ftp
			saveLastSynchedModifyDate();
			saveLastExistingUnitList();
		} catch (Exception e) {
			// Commit to ftp was unsuccessful. Reset the lastSynchedModifyDate
			lastSynchedModifyDate = null;
			logger.error(e);
		}
	}

	private Unit getParentUnit(Unit unit) {
		DN dn = unit.getDn();
		DN parentDn = dn.getParentDN();
		if (parentDn != null) {
			Unit parent = null;
			try {
				parent = unitRepository.getUnitByDN(parentDn);
			} catch (Exception e) {
				logger.error(e);
			}
			return parent;
		} else {
			return null;
		}
	}

	/**
	 * For full unit detail pushes.
	 * 
	 * @param units
	 * @return
	 */
	public Organization generateOrganisationTree(List<Unit> units) {
		Map<DN, Unit> unitContainer = new HashMap<DN, Unit>();
		Map<DN, List<Unit>> unitChildrenContainer = new HashMap<DN, List<Unit>>();
		List<Unit> rootUnits = new ArrayList<Unit>();
		for (Unit unit : units) {
			DN dn = unit.getDn();
			DN parentDn = dn.getParentDN();
			if (dn != null) {
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
			se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbRootUnit = new se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit();
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
	 * @return Organization with a unit list of created,removed or modified
	 *         units
	 */
	private Organization generateFlatOrganization(List<Unit> collectedUnits) {
		Organization organization = new Organization();
		for (Unit unit : collectedUnits) {
			se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbUnit = new se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit();
			fillJaxbUnit(unit, jaxbUnit);
			organization.getUnit().add(jaxbUnit);
		}
		return organization;
	}

	/**
	 * 
	 * @param parentJaxbUnit
	 *            Parent unit
	 * @param dn
	 *            DN of parent unit
	 * @param unitChildrenContainer
	 *            Map holding parent - children relations
	 */
	private void populateUnit(se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit parentJaxbUnit, DN dn, Map<DN, List<Unit>> unitChildrenContainer) {
		// Get children for current parent
		List<Unit> childUnits = unitChildrenContainer.get(dn);
		if (childUnits != null) {
			for (Unit child : childUnits) {
				se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbChildUnit = new se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit();
				fillJaxbUnit(child, jaxbChildUnit);
				parentJaxbUnit.getUnit().add(jaxbChildUnit);
				populateUnit(jaxbChildUnit, child.getDn(), unitChildrenContainer);
			}
		}
	}
}
