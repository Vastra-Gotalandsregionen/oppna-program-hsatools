/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.hsatools.testtools.vardvalws.endpoints;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBElement;

import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import se.vgregion.hsatools.testtools.services.Service;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.GetVårdvalRequest;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.GetVårdvalResponse;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.ObjectFactory;
import se.vgregion.hsatools.testtools.vardvalws.ws.domain.SetVårdvalRequest;

public class VardvalEndpointTest {

  private static final String NEW_UNIT = "SE2321000131-E000000006727";
  private VardvalEndpoint vardvalEndpoint;
  private String DEFAULT_UNIT = "SE2321000131-E000000006740";
  private ObjectFactory objectFactory = new ObjectFactory();
  private String SSN = "197702201111";
  private Jaxb2Marshaller jaxb2Marshaller;

  @Before
  public void setup() {
    // Only for code coverage.
    new Service();
    jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath("hriv.vardvalws.ws.domain");
    vardvalEndpoint = new VardvalEndpoint(jaxb2Marshaller, jaxb2Marshaller);
  }

  @Test
  public void testInvokeInternalWithGetVardvalRequest() throws Exception {
    GetVårdvalRequest getVardvalRequest = new GetVårdvalRequest();
    JAXBElement<String> requestSsn = objectFactory.createGetVårdvalRequestPersonnummer(SSN);
    getVardvalRequest.setPersonnummer(requestSsn);
    GetVårdvalResponse response = (GetVårdvalResponse) vardvalEndpoint.invokeInternal(getVardvalRequest);
    // First time it should return default unit id.
    assertEquals(DEFAULT_UNIT, response.getAktivtVårdval().getValue().getVårdcentralHsaId());

    SetVårdvalRequest setVardvalRequest = new SetVårdvalRequest();
    setVardvalRequest.setPersonnummer(requestSsn);
    setVardvalRequest.setVårdcentralHsaId(objectFactory.createSetVårdvalRequestVårdcentralHsaId(NEW_UNIT));
    // Set new unit for user.
    vardvalEndpoint.invokeInternal(setVardvalRequest);
    // Get selected unit for user, when new select is done.
    response = (GetVårdvalResponse) vardvalEndpoint.invokeInternal(getVardvalRequest);
    assertEquals(NEW_UNIT, response.getKommandeVårdval().getValue().getVårdcentralHsaId());
  }

  @Test
  public void testInvokeInternalWithDummyRequest() throws Exception {
    assertNull(vardvalEndpoint.invokeInternal(this));
  }
}
