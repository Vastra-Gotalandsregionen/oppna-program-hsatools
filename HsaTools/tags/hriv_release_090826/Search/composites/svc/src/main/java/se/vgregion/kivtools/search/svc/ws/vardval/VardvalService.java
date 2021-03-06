package se.vgregion.kivtools.search.svc.ws.vardval;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;

public interface VardvalService {

  /**
   * 
   * @param ssn Social Security Number in format: YYYYMMDDNNNN
   * @throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage Exception from Vårdval service.
   * @return - Javabean
   */
  VardvalInfo getVardval(String ssn) throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;

  /**
   * 
   * @param ssn Social Security Number
   * @param hsaId HsaIdentity
   * @param signature Personal signature
   * @throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage
   */
  VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
}
