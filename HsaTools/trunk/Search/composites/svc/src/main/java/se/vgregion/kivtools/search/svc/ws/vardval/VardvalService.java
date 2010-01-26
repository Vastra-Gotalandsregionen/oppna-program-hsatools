/**
 * Copyright 2009 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 */
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
