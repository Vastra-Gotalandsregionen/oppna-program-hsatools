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

package se.vgregion.hsatools.testtools.signicatws.endpoints;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import se.vgregion.hsatools.testtools.services.Service;
import se.vgregion.hsatools.testtools.signicatws.ws.domain.RegisterDocument;
import se.vgregion.hsatools.testtools.signicatws.ws.domain.RegisterDocumentResponse;
import se.vgregion.hsatools.testtools.signicatws.ws.domain.RetrieveSaml;
import se.vgregion.hsatools.testtools.signicatws.ws.domain.RetrieveSamlResponse;

public class SignicatEndpointTest {

  private static final String ARTIFACT_PREFIX = "artifact";
  private SignicatEndpoint signicatEndpoint;
  private RegisterDocument registerDocument = new RegisterDocument();
  private RetrieveSaml retrieveSaml = new RetrieveSaml();
  private RegisterDocumentResponse registerDocumentResponse = new RegisterDocumentResponse();
  private Jaxb2Marshaller jaxb2Marshaller;
  private String SSN = "197702201111";
  
  @Before
  public void setup() {
    // Only for code coverage.
    new Service();
    jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setContextPath("hriv.signicatws.ws.domain");
    signicatEndpoint = new SignicatEndpoint(jaxb2Marshaller, jaxb2Marshaller);
  }

  @Test
  public void testInvokeInternalWithRegisterDocument() throws Exception {
    long systemTime = System.nanoTime();
    registerDocumentResponse = (RegisterDocumentResponse) signicatEndpoint.invokeInternal(registerDocument);
    String[] artifact = registerDocumentResponse.getRegisterDocumentReturn().split("_");
    assertEquals(ARTIFACT_PREFIX, artifact[0]);
    assertTrue(systemTime < Long.valueOf(artifact[1]));
  }
  
  @Test
  public void testInvokeInternalWithRetrieveSaml() throws Exception {
    // Prepare a artifact to fetch from service for current test.
    String artifact = "artifact_" + System.nanoTime();
    Service.addSignature(artifact, SSN);
    retrieveSaml.setArtifact(artifact);
    RetrieveSamlResponse retrieveSamlResponse = (RetrieveSamlResponse) signicatEndpoint.invokeInternal(retrieveSaml);
    byte[] decodedResponse = Base64.decodeBase64(retrieveSamlResponse.getRetrieveSamlReturn().getBytes());
    String decodedResponseStr = new String(decodedResponse);
    assertTrue(decodedResponseStr.contains("<AttributeValue>" + SSN + "</AttributeValue>"));
  }

}
