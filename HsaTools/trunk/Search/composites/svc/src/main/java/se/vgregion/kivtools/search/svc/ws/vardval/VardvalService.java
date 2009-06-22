package se.vgregion.kivtools.search.svc.ws.vardval;

public interface VardvalService {
	
	/**
	 * 
	 * @param sn - Social Security Number
	 * @return - Javabean
	 */
	VardvalInfo getVardval(String ssn);
	
	/**
	 * 
	 * @param sn - Social Security Number
	 * @param hsaId - HsaIdentity
	 * @param signature - Personal signature
	 */
	VardvalInfo setVardval(String ssn, String hsaId, byte[] signature);
}
