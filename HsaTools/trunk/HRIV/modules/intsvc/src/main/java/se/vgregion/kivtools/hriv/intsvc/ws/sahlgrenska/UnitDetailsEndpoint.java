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
package se.vgregion.kivtools.hriv.intsvc.ws.sahlgrenska;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitRequest;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.sahlgrenska.UnitResponse;

/**
 * Spring implementation for the unit details retrival webservice endpoint.
 * 
 * @author David Bennehult & Jonas Liljenfeldt.
 */
public class UnitDetailsEndpoint extends AbstractMarshallingPayloadEndpoint {

  private UnitDetailsService<Organization> unitDetailsService;

  /**
   * Constructs a new UnitDetailsEndpoint using the provided marshallers.
   * 
   * @param marshaller The marshaller to use when marshalling from POJO's to XML.
   * @param unmarshaller The marshaller to use when unmarshalling from XML to POJO's.
   */
  public UnitDetailsEndpoint(Marshaller marshaller, Unmarshaller unmarshaller) {
    super(marshaller, unmarshaller);
  }

  public void setUnitDetailsService(UnitDetailsService<Organization> unitDetailsService) {
    this.unitDetailsService = unitDetailsService;
  }

  @Override
  protected Object invokeInternal(Object request) {
    UnitRequest unitRequest = (UnitRequest) request;
    Organization organization = null;
    organization = unitDetailsService.getUnitDetails(unitRequest.getHsaIdentity());
    UnitResponse response = new UnitResponse();
    response.setOrganization(organization);
    return response;
  }
}
