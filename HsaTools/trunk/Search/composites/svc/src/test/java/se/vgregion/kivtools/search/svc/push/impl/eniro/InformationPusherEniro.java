package se.vgregion.kivtools.search.svc.push.impl.eniro;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.InformationPusher;

public class InformationPusherEniro implements InformationPusher {

	Date lastSynchedModifyDate;
	UnitRepository unitRepository;

	public UnitRepository getUnitRepository() {
		return unitRepository;
	}

	public void setUnitRepository(UnitRepository unitRepository) {
		this.unitRepository = unitRepository;
	}

	public List<Unit> collectData() throws Exception {
		lastSynchedModifyDate = getLastSynchDate();
		List<Unit> freshUnits = unitRepository
				.getFreshUnits(lastSynchedModifyDate);
		return freshUnits;
	}

	private Date getLastSynchDate() {

		String lastSynchedModifyString = null;
		if (lastSynchedModifyDate != null) {
			// lastSynchDate is unknown, read from file
			try {
				FileReader fileReader = new FileReader(
						"lastSynchedMofiyDate.txt");
				BufferedReader br = new BufferedReader(fileReader);
				lastSynchedModifyString = br.readLine();
			} catch (FileNotFoundException e) {
				try {
					lastSynchedModifyDate = new SimpleDateFormat()
							.parse("1970-01-01");
				} catch (ParseException e1) {
					// Ignore, we have given a correct date
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// If don't exists in file, return 1970
		}
		try {
			return new SimpleDateFormat().parse(lastSynchedModifyString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastSynchedModifyDate;
	}
}
