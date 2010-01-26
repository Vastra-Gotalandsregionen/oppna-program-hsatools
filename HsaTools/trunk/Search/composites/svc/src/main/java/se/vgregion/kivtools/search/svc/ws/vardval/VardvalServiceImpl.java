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

import java.security.KeyStoreException;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalEntry;
/**
 * 
 * @author David Bennehult, Jonas Liljenfeld och Joakim Olsson.
 *
 */
public class VardvalServiceImpl implements VardvalService {

  private ObjectFactory objectFactory = new ObjectFactory();
  private IVårdvalService service;
  private String webserviceEndpoint;

  public void setService(IVårdvalService service) {
    this.service = service;
  }
/**
 * Sets endpoint for the webservice. webserviceEndpoint variable is used.
 * @throws KeyStoreException .
 */
  public void setEndpoint() throws KeyStoreException {
    BindingProvider bindingProvider = (BindingProvider) service;
    Map<String, Object> requestContext = bindingProvider.getRequestContext();
    requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceEndpoint);
  }

  public void setWebserviceEndpoint(String webserviceEndpoint) {
    this.webserviceEndpoint = webserviceEndpoint;
  }

  /**
   * {@inheritDoc}
   * 
   */
  @Override
  public VardvalInfo getVardval(String ssn) throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage {
    GetVårdvalResponse response = getVardvalInfo(ssn);
    return generateVardvalInfo(response);
  }

  private VardvalInfo generateVardvalInfo(Object response) {
    VardvalInfo vardvalInfo = new VardvalInfo();
    JAXBElement<VårdvalEntry> currentVardval = null;
    JAXBElement<VårdvalEntry> upcomingVardval = null;
    // Check what sort of soap response
    if (response instanceof GetVårdvalResponse) {
      currentVardval = ((GetVårdvalResponse) response).getAktivtVårdval();
      upcomingVardval = ((GetVårdvalResponse) response).getKommandeVårdval();
    } else if (response instanceof SetVårdvalResponse) {
      currentVardval = ((SetVårdvalResponse) response).getAktivtVårdval();
      upcomingVardval = ((SetVårdvalResponse) response).getKommandeVårdval();
    }

    if (currentVardval != null && currentVardval.getValue() != null) {
      vardvalInfo.setCurrentHsaId(currentVardval.getValue().getVårdcentralHsaId());
      vardvalInfo.setCurrentValidFromDate(currentVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
    }

    if (upcomingVardval != null && upcomingVardval.getValue() != null) {
      vardvalInfo.setUpcomingHsaId(upcomingVardval.getValue().getVårdcentralHsaId());
      vardvalInfo.setUpcomingValidFromDate(upcomingVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
    }
    return vardvalInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage {
    SetVårdvalRequest vardvalRequest = new SetVårdvalRequest();
    JAXBElement<String> soapSsn = objectFactory.createSetVårdvalRequestPersonnummer(ssn);
    JAXBElement<byte[]> soapSignature = objectFactory.createSetVårdvalRequestSigneringskod(signature);
    JAXBElement<String> soapHsaId = objectFactory.createSetVårdvalRequestVårdcentralHsaId(hsaId);
    vardvalRequest.setPersonnummer(soapSsn);
    vardvalRequest.setSigneringskod(soapSignature);
    vardvalRequest.setVårdcentralHsaId(soapHsaId);
    SetVårdvalResponse response = service.setVårdVal(vardvalRequest);
    return generateVardvalInfo(response);
  }

  /**
   * 
   * @param ssn - Social Security Number
   * @return - Soap response
   * @throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage Exception from Vårdval service.
   */
  private GetVårdvalResponse getVardvalInfo(String ssn) throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage {
    JAXBElement<String> soapSsn = objectFactory.createGetVårdvalRequestPersonnummer(ssn);
    GetVårdvalRequest getVardvalRequest = objectFactory.createGetVårdvalRequest();
    getVardvalRequest.setPersonnummer(soapSsn);
    GetVårdvalResponse getVardvalResponse = service.getVårdVal(getVardvalRequest);
    return getVardvalResponse;
  }
}
