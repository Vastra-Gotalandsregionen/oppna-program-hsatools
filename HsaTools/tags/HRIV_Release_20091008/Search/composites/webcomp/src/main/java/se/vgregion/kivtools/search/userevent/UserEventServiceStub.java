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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.vgregion.kivtools.util.StringUtil;

/**
 * Stub-implementation to use when there is no real backend implementation.
 * 
 * @author Joakim Olsson
 */
public class UserEventServiceStub implements UserEventService {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserEventInfo> retrieveUserEvents(String firstName, String surname, String telephoneNumber, String mobileNumber) {
    List<UserEventInfo> events = new ArrayList<UserEventInfo>();

    if (!StringUtil.isEmpty(firstName)) {
      events.add(UserEventInfo.createUserEventInfo("ejvidarekopplad", "möte", new Date(), new Date(), "Möte resten av dagen", "/Kalle"));
      events.add(UserEventInfo.createUserEventInfo("vidarekopplad", "gåttfördagen", new Date(), new Date(), "Gått för dagen", "/Kalle"));
      events.add(UserEventInfo.createUserEventInfo("ejvidarekopplad", "sjuk", new Date(), null, "Hemma med snuva", "/Kalle"));
    }

    return events;
  }
}
