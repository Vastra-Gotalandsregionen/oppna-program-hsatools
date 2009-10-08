package se.vgregion.kivtools.search.sms.impl.hak;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

public class SmsRedirectServiceFactoryTest {

  @Test
  public void testInstance() {
    assertNotNull(new SmsRedirectServiceFactory());
  }

  @Test
  public void testGetSmsRedirectSoap() throws MalformedURLException {
    File wsdlFile = new File("../schema/src/main/resources/wsdl/Netwise_SMSRedirect_HAK.wsdl");
    assertNotNull(SmsRedirectServiceFactory.getSmsRedirectSoap(wsdlFile.toURI().toString(), "http://lina.lthalland.se/", "SMSRedirect"));
  }
}
