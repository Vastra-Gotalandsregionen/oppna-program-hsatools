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
package se.vgregion.kivtools.search.userevent.impl.hak;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.search.userevent.UserEventInfo;
import se.vgregion.kivtools.search.userevent.UserEventService;
import se.vgregion.kivtools.search.util.netwise.NetwiseServicesUtil;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.ArrayOfEvent;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.Event;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.Resultset;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEventSoap;

/**
 * Implementation of the UserEventService using Netwise webservices as the backend.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class UserEventServiceNetwise extends NetwiseServicesUtil implements UserEventService {
  private static final Log LOG = LogFactory.getLog(UserEventServiceNetwise.class);

  private UserEventSoap service;

  public void setService(UserEventSoap service) {
    this.service = service;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserEventInfo> retrieveUserEvents(String firstName, String surname, String telephoneNumber, String mobileNumber) {
    List<UserEventInfo> userEvents = new ArrayList<UserEventInfo>();

    String requestedTelephoneNumber = NetwiseServicesUtil.cleanPhoneNumber(telephoneNumber);
    String requestedMobileNumber = NetwiseServicesUtil.cleanPhoneNumber(mobileNumber);

    Resultset resultset = service.getResultsetFromPersonInfo(requestedTelephoneNumber, requestedMobileNumber, firstName, firstName);

    ArrayOfEvent eventList = resultset.getEventList();

    userEvents = populateUserEvents(eventList);

    return userEvents;
  }

  /**
   * Converts the SOAP ArrayOfEvent-object to a list of UserEventInfo-objects.
   * 
   * @param eventList The ArrayOfEvent-object to convert.
   * @return A list of UserEventInfo-objects or an empty list of no ArrayOfEvent-object was provided.
   */
  private List<UserEventInfo> populateUserEvents(ArrayOfEvent eventList) {
    List<UserEventInfo> userEvents = new ArrayList<UserEventInfo>();

    if (eventList != null) {
      List<Event> events = eventList.getEvent();

      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");

      for (Event event : events) {
        try {
          Date fromDateTime = format.parse(event.getFrom());
          Date toDateTime = format.parse(event.getTo());

          userEvents.add(UserEventInfo.createUserEventInfo(event.getStatus(), event.getCode(), fromDateTime, toDateTime, event.getInformation(), event.getSignature()));
        } catch (ParseException e) {
          LOG.error("Unable to parse date from event object", e);
        }
      }
    }

    return userEvents;
  }
}
