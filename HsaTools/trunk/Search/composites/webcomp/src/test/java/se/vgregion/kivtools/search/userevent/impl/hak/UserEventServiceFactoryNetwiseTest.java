package se.vgregion.kivtools.search.userevent.impl.hak;

import static org.junit.Assert.*;

import org.junit.Test;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEventSoap;

public class UserEventServiceFactoryNetwiseTest {

  @Test
  public void testGetUserEventSoap() {
    UserEventSoap userEventSoap = UserEventServiceFactoryNetwise.getUserEventSoap();
    assertNotNull(userEventSoap);
  }
}
