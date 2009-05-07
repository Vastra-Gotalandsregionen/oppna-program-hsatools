package se.vgregion.kivtools.search.svc.push.impl.eniro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.InformationPusher;

import com.domainlanguage.time.TimePoint;

public class InformationPusherEniro implements InformationPusher {

	Log logger = LogFactory.getLog(this.getClass());
	Date lastSynchedModifyDate;
	UnitRepository unitRepository;
	File lastSynchedModifyDateFile;

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

	public List<Unit> collectData() throws Exception {
		lastSynchedModifyDate = getLastSynchDate();
		List<Unit> freshUnits = getFreshUnits();
		return freshUnits;
	}

	private Date getLastSynchDate() {
		if (lastSynchedModifyDate == null) {
			// lastSynchDate is unknown, read from file
			try {
				if (lastSynchedModifyDateFile.exists()) {
					String lastSynchedModifyString = null;
					FileReader fileReader = new FileReader(lastSynchedModifyDateFile);
					BufferedReader br = new BufferedReader(fileReader);
					lastSynchedModifyString = br.readLine();
					if (lastSynchedModifyString != null) {
						lastSynchedModifyDate = Constants.zuluTimeFormatter.parse(lastSynchedModifyString);
					} else {
						lastSynchedModifyDate = Constants.zuluTimeFormatter.parse("1970-01-01");
					}
				}
			} catch (Exception e) {
				e.getStackTrace();
			} finally {
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
		saveLastSynchedModifyDate();
		return freshUnits;
	}

	public int doPushInformation() throws Exception {
		List<Unit> collectData = collectData();
		
		// Transform units to XML representation
		
		
		// Push (upload) XML to specified resource
		
		return collectData.size();
	}

}
