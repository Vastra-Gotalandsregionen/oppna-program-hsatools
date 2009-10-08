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

import java.util.List;

/**
 * Service for retrieval of UserEvent information from an external source.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public interface UserEventService {
  /**
   * Retrieves UserEvent information from an external source using the provided parameters.
   * 
   * @param firstName First name of the person to retrieve UserEvents for.
   * @param surname Surname of the person to retrieve UserEvents for.
   * @param telephoneNumber Telephone number of the person to retrieve UserEvents for.
   * @param mobileNumber Mobile phone number of the person to retrieve UserEvents for.
   * @return A List of UserEventInfo objects for the person identified by the provided fields or an empty List if no UserEvents was found.
   */
  public List<UserEventInfo> retrieveUserEvents(String firstName, String surname, String telephoneNumber, String mobileNumber);
}
