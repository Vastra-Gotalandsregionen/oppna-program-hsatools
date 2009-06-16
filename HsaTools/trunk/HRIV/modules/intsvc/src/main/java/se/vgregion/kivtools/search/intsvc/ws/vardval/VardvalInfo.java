package se.vgregion.kivtools.search.intsvc.ws.vardval;

import java.util.Date;

public class VardvalInfo {

	private String currentHsaId;
	private Date currentValidFromDate;
	private String upcomingHsaId;
	private Date upcomingValidFromDate;

	public String getCurrentHsaId() {
		return currentHsaId;
	}

	public void setCurrentHsaId(String currentHsaId) {
		this.currentHsaId = currentHsaId;
	}

	public Date getCurrentValidFromDate() {
		return currentValidFromDate;
	}

	public void setCurrentValidFromDate(Date currentValidFromDate) {
		this.currentValidFromDate = currentValidFromDate;
	}

	public String getUpcomingHsaId() {
		return upcomingHsaId;
	}

	public void setUpcomingHsaId(String upcomingHsaId) {
		this.upcomingHsaId = upcomingHsaId;
	}

	public Date getUpcomingValidFromDate() {
		return upcomingValidFromDate;
	}

	public void setUpcomingValidFromDate(Date upcomingValidFromDate) {
		this.upcomingValidFromDate = upcomingValidFromDate;
	}

}
