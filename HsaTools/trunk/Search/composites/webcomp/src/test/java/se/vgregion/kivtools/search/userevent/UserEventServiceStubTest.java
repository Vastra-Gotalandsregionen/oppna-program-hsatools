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
