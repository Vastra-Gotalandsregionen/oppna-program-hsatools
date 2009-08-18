package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

public interface UnitDetailsService<T> {
  T getUnitDetails(String hsaIdentity);
}
