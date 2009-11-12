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
