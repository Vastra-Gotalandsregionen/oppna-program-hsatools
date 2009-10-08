package se.vgregion.kivtools.search.svc.ws.vardval;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;

/**
 * Service for retrieving and registering citizens unit registrations.
 */
public interface VardvalService {

  /**
   * Retrieves a citizens unit registration.
   * 
   * @param ssn Social Security Number in format: YYYYMMDDNNNN
   * @throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage Exception from Vårdval service.
   * @return The citizens unit registration information.
   */
  VardvalInfo getVardval(String ssn) throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;

  /**
   * Sets a citizens unit registration.
   * 
   * @param ssn Social Security Number in format: YYYYMMDDNNNN
   * @param hsaId The hsaIdentity of the unit to register on.
   * @param signature Personal signature of the registration change.
   * @return The citizens unit registration information.
   * @throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage Exception from Vårdval service.
   */
  VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
}
