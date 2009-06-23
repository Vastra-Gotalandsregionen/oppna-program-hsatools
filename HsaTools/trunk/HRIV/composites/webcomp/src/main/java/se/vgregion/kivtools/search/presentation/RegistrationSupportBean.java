package se.vgregion.kivtools.search.presentation;

import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;

public class RegistrationSupportBean {

	VardvalInfo vardvalInfo;
	String ssn;
	String name;

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
	
}
