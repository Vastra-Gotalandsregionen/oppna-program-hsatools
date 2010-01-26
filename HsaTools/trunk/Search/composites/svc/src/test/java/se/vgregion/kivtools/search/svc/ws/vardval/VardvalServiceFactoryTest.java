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

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;

/**
 * Test factory method in VardvalServiceFactory
 * 
 * @author david
 * 
 */
public class VardvalServiceFactoryTest {
  @Test
  public void testInstantiation() {
    VardvalServiceFactory vardvalServiceFactory = new VardvalServiceFactory();
    assertNotNull(vardvalServiceFactory);
  }

  /**
   * Test that the factory method returns a IVårdvalService
   * 
   * @throws MalformedURLException
   */
  @Test
  public void testGetIVardvalserviceMethod() throws MalformedURLException {
    File wsdlFile = new File("../schema/src/main/resources/wsdl/VardvalService_1.wsdl");
    IVårdvalService vårdvalService = VardvalServiceFactory.getIVardvalservice(wsdlFile.toURI().toString(), "urn:VGRegion.VGPrimärvård.Vårdval.Service", "VårdvalService");
    Assert.assertNotNull(vårdvalService);
  }
}
