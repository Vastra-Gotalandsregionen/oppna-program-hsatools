package se.vgregion.kivtools.search.presentation.types;

public class SigningInformation {
	private final String nationalId;
	private final String signature;

	public SigningInformation(String nationalId, String signature) {
		this.nationalId = nationalId;
		this.signature = signature;
	}

	public String getNationalId() {
		return nationalId;
	}

	public String getSignature() {
		return signature;
	}
}
