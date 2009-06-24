package se.vgregion.kivtools.search.svc.ws.vardval;

import java.io.Serializable;
import java.util.Date;

public class VardvalInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String currentHsaId;
	private Date currentValidFromDate;
	private String upcomingHsaId;
	private Date upcomingValidFromDate;
	private VardvalInfo vardvalInfo;
	private String ssn;
	private String name;
	private String selectedUnitId;
	private String upcomingUnitName;
	private String currentUnitName;
	private String selectedUnitName;
	private String signature;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSelectedUnitId() {
		return selectedUnitId;
	}

	public void setSelectedUnitId(String selectedUnitId) {
		this.selectedUnitId = selectedUnitId;
	}

	public String getSelectedUnitName() {
		return selectedUnitName;
	}

	public void setSelectedUnitName(String selectedUnitName) {
		this.selectedUnitName = selectedUnitName;
	}

	public String getUpcomingUnitName() {
		return upcomingUnitName;
	}

	public void setUpcomingUnitName(String upcomingUnitName) {
		this.upcomingUnitName = upcomingUnitName;
	}

	public String getCurrentUnitName() {
		return currentUnitName;
	}

	public void setCurrentUnitName(String currentUnitName) {
		this.currentUnitName = currentUnitName;
	}

	public VardvalInfo getVardvalInfo() {
		return vardvalInfo;
	}

	public void setVardvalInfo(VardvalInfo vardvalInfo) {
		this.vardvalInfo = vardvalInfo;
	}

	public String getSsn() {
		return ssn;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
