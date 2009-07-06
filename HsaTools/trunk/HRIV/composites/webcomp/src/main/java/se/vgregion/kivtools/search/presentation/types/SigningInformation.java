package se.vgregion.kivtools.search.presentation.types;

public class SigningInformation {
	private final String nationalId;
	private final String samlResponse;

	public String getSamlResponse() {
		return samlResponse;
	}

	public SigningInformation(String nationalId, String samlResponse) {
		this.nationalId = nationalId;
		this.samlResponse = samlResponse;
	}

	public String getNationalId() {
		return nationalId;
	}

}
