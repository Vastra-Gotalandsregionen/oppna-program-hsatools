package se.vgregion.kivtools.search.userevent;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UserEventServiceStubTest {

  private UserEventServiceStub userEventServiceStub;

  @Before
  public void setUp() throws Exception {
    userEventServiceStub = new UserEventServiceStub();
  }

  @Test
  public void testInstantiation() {
    UserEventServiceStub userEventServiceStub = new UserEventServiceStub();
    assertNotNull(userEventServiceStub);
  }

  @Test
  public void testRetrieveUserEventsNoInput() {
    List<UserEventInfo> userEvents = this.userEventServiceStub.retrieveUserEvents(null, null, null, null);
    assertNotNull(userEvents);
    assertEquals(0, userEvents.size());
  }

  @Test
  public void testRetrieveUserEvents() {
    List<UserEventInfo> userEvents = this.userEventServiceStub.retrieveUserEvents("Kalle", "Kula", "", "");
    assertNotNull(userEvents);
    assertEquals(3, userEvents.size());
    assertEquals("ejvidarekopplad", userEvents.get(0).getStatus());
    assertEquals("möte", userEvents.get(0).getCode());
    assertNotNull(userEvents.get(0).getFromDateTime());
    assertNotNull(userEvents.get(0).getToDateTime());
    assertEquals("Möte resten av dagen", userEvents.get(0).getInformation());
    assertEquals("/Kalle", userEvents.get(0).getSignature());
  }
}
