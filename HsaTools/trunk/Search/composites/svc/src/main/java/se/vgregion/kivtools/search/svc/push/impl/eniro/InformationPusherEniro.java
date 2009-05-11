package se.vgregion.kivtools.search.svc.push.impl.eniro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.InformationPusher;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.ObjectFactory;
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
	private String country = "Sweden";
	private String organizationName = "VGR";
	private String organizationId = "vgr";
	private File destinationFolder;
	private String ftpUser;
	private String ftpHost;
	private String ftpPassword;
	private String ftpDestinationFolder;
	private JSch jsch;
	
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

	public UnitRepository getUnitRepository() {
		return unitRepository;
	}

	public void setUnitRepository(UnitRepository unitRepository) {
		this.unitRepository = unitRepository;
	}

	private List<Unit> collectData() throws Exception {
		lastSynchedModifyDate = getLastSynchDate();
		List<Unit> freshUnits = getFreshUnits();
		return freshUnits;
	}

	private Date getLastSynchDate() {
		if (lastSynchedModifyDate == null) {
			// lastSynchDate is unknown, read from file
			try {
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
			} finally {
				// Set default start synch date if read from file failed
				if (lastSynchedModifyDate == null) {
					Calendar calendar = Calendar.getInstance();
					calendar.set(1970, Calendar.JANUARY, 1);
					lastSynchedModifyDate = calendar.getTime();
				}
			}
		}
		return lastSynchedModifyDate;
	}

	private void saveLastSynchedModifyDate() {
		try {
			FileWriter fileWriter = new FileWriter(lastSynchedModifyDateFile);
			fileWriter.write(Constants.zuluTimeFormatter.format(lastSynchedModifyDate));
			fileWriter.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	/**
	 * Returns units created/modified since specified date.
	 * 
	 * @param lastSynchedModifyDate
	 * @return
	 * @throws Exception
	 */
	private List<Unit> getFreshUnits() throws Exception {
		List<String> allUnitsHsaId = unitRepository.getAllUnitsHsaIdentity();
		List<Unit> freshUnits = new ArrayList<Unit>();
		TimePoint lastSynchedModifyTimePoint = TimePoint.from(lastSynchedModifyDate);
		TimePoint temporaryLatestModifiedTimepoint = TimePoint.from(lastSynchedModifyDate);

		for (String hsaId : allUnitsHsaId) {
			Unit u = unitRepository.getUnitByHsaId(hsaId);
			if (u != null) {
				TimePoint modifyTimestamp = u.getModifyTimestamp();
				TimePoint createTimestamp = u.getCreateTimestamp();
				// Check if the unit is created or modified after last synched
				// modify date
				if ((modifyTimestamp != null && modifyTimestamp.isAfter(lastSynchedModifyTimePoint)) || (createTimestamp != null && createTimestamp.isAfter(lastSynchedModifyTimePoint))) {
					freshUnits.add(u);
					if (createTimestamp != null && createTimestamp.isAfter(temporaryLatestModifiedTimepoint)) {
						temporaryLatestModifiedTimepoint = createTimestamp;
					}
					if (u.getModifyTimestamp() != null && u.getModifyTimestamp().isAfter(temporaryLatestModifiedTimepoint)) {
						temporaryLatestModifiedTimepoint = u.getModifyTimestamp();
					}
				}
			}
		}
		lastSynchedModifyDate = temporaryLatestModifiedTimepoint.asJavaUtilDate();
		return freshUnits;
	}

	/**
	 * Generate xml file with newly updated units
	 */
	public List<Unit> doPushInformation() throws Exception {
		List<Unit> collectData = collectData();
		ObjectFactory objectFactory = new ObjectFactory();
		Organization organization = objectFactory.createOrganization();
		organization.setCountry(country);
		organization.setId(organizationId);
		organization.setName(organizationName);
		List<se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit> organizationbUnits = organization.getUnit();
		for (Unit unit : collectData) {
			se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbUnit = objectFactory.createUnit();
			fillJaxbUnit(unit, jaxbUnit);
			organizationbUnits.add(jaxbUnit);
		}
		JAXBContext context = JAXBContext.newInstance(organization.getClass());
		Marshaller marshaller = context.createMarshaller();
		File organizationXmlFile = new File(destinationFolder, organization.getName() + ".xml");
		marshaller.marshal(organization, new FileWriter(organizationXmlFile));
		// Push (upload) XML to specified resource
		sendXmlFile(organizationXmlFile);
		return collectData;
	}

	private void fillJaxbUnit(Unit unit, se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit jaxbUnit) {
		jaxbUnit.setId(unit.getHsaIdentity());
		jaxbUnit.setName(unit.getName());
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
			// Save last synch date to file after successful commit of xml file to ftp 
			saveLastSynchedModifyDate();
		} catch (Exception e) {
			// Commit to ftp was unsuccessful. Reset the lastSynchedModifyDate
			lastSynchedModifyDate = null;
			logger.error(e);
		}
	}
}
