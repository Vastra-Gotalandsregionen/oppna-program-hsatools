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

import java.io.Serializable;
import java.util.Date;

/**
 * Represents UserEvent information for a person.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public final class UserEventInfo implements Serializable {
  private static final long serialVersionUID = -3878573201077056386L;

  private final String status;
  private final String code;
  private final Date fromDateTime;
  private final Date toDateTime;
  private final String information;
  private final String signature;

  /**
   * Constructs a new UserEventInfo object using the provided fields.
   * 
   * @param status The status to use for this UserEventInfo.
   * @param code The code to use for this UserEventInfo.
   * @param fromDateTime The fromDateTime to use for this UserEventInfo.
   * @param toDateTime The toDateTime to use for this UserEventInfo.
   * @param information The information to use for this UserEventInfo.
   * @param signature The signature to use for this UserEventInfo.
   */
  private UserEventInfo(String status, String code, Date fromDateTime, Date toDateTime, String information, String signature) {
    this.status = status;
    this.code = code;
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
    this.information = information;
    this.signature = signature;
  }

  public String getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public Date getFromDateTime() {
    return fromDateTime;
  }

  public Date getToDateTime() {
    return toDateTime;
  }

  public String getInformation() {
    return information;
  }

  public String getSignature() {
    return signature;
  }

  /**
   * Creates a new UserEventInfo object using the provided fields.
   * 
   * @param status The status to use for this UserEventInfo.
   * @param code The code to use for this UserEventInfo.
   * @param fromDateTime The fromDateTime to use for this UserEventInfo.
   * @param toDateTime The toDateTime to use for this UserEventInfo.
   * @param information The information to use for this UserEventInfo.
   * @param signature The signature to use for this UserEventInfo.
   * @return A fully populated UserEventInfo object.
   */
  public static UserEventInfo createUserEventInfo(String status, String code, Date fromDateTime, Date toDateTime, String information, String signature) {
    return new UserEventInfo(status, code, new Date(fromDateTime.getTime()), new Date(toDateTime.getTime()), information, signature);
  }
}
