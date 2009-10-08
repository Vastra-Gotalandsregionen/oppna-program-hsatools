package se.vgregion.kivtools.search.userevent.impl.hak;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEventSoap;

public class UserEventServiceFactoryNetwiseTest {

  @Test
  public void testInstance() {
    assertNotNull(new UserEventServiceFactoryNetwise());
  }

  @Test
  public void testGetUserEventSoap() throws MalformedURLException {
    File wsdlFile = new File("../schema/src/main/resources/wsdl/Netwise_UserEvent_HAK.wsdl");
    UserEventSoap userEventSoap = UserEventServiceFactoryNetwise.getUserEventSoap(wsdlFile.toURI().toString(), "http://lina.lthalland.se/", "UserEvent");
    assertNotNull(userEventSoap);
  }
}
