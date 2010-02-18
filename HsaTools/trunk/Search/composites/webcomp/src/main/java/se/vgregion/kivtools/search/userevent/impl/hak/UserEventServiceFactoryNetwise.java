/**
 * Copyright 2010 Västra Götalandsregionen
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
 *
 */

package se.vgregion.kivtools.search.userevent.impl.hak;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEvent;
import se.vgregion.kivtools.search.ws.domain.hak.netwise.event.UserEventSoap;

/**
 * Factory-class used to retrieve a SOAP-service for the Netwise integration.
 * 
 * @author Joakim Olsson
 */
public class UserEventServiceFactoryNetwise {
  /**
   * Return default UserEventSoap object.
   * 
   * @param wsdlLocation The URL to use to find the WSDL.
   * @param namespace The namespace for the webservice.
   * @param qNameLocalPart The local part of the Qualified Name to use for the WSDL.
   * @return UserEventSoap object.
   * @throws MalformedURLException if the provided wsdlLocation is not a valid URL.
   */
  public static UserEventSoap getUserEventSoap(String wsdlLocation, String namespace, String qNameLocalPart) throws MalformedURLException {
    return new UserEvent(new URL(wsdlLocation), new QName(namespace, qNameLocalPart)).getUserEventSoap();
  }
}
