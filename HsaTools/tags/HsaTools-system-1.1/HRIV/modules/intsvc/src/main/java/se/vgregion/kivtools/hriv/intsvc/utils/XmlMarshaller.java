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

package se.vgregion.kivtools.hriv.intsvc.utils;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * 
 * @author David Bennehult
 * 
 */
public final class XmlMarshaller {
  /**
   * Generate xml content of current object, by using jaxb marshaller.
   * @param <T> type of the objectToMarshaller object.
   * @param objectToMarshaller the object to generate xml content of.
   * @return String containing the generated xml content.
   */
  public static <T> String generateXmlContentOfObject(T objectToMarshaller) {
    StringWriter fileContent = new StringWriter();
    try {
      JAXBContext context = JAXBContext.newInstance(objectToMarshaller.getClass());
      Marshaller marshaller = context.createMarshaller();
      marshaller.marshal(objectToMarshaller, fileContent);
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
    return fileContent.toString();
  }

}
