package se.vgregion.kivtools.search.intsvc.ws.sahlgrenska;

public interface UnitDetailsService <T>{
	T getUnitDetails(String hsaIdentity);
}
