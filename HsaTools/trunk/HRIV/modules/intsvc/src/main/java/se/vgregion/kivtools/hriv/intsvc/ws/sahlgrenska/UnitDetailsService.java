package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

/**
 * Service interface for the webservice for retrieving unit information.
 * 
 * @author Jonas Liljenfelt & David Bennehult
 * @param <T> The type of unit details the service handles.
 */
public interface UnitDetailsService<T> {
  /**
   * Retrives unit details for the provided hsaIdentity.
   * 
   * @param hsaIdentity The hsaIdentity to retrieve unit details for.
   * @return The unit details for the provided hsaIdentity.
   */
  T getUnitDetails(String hsaIdentity);
}
