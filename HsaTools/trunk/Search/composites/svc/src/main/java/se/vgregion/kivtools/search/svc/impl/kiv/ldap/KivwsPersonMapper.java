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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.springframework.ldap.core.ContextMapper;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.util.Arguments;

enum KivwsPersonAttributes {
  cn;

}

public class KivwsPersonMapper implements ContextMapper {

  private Map<String, List<Object>> attributes;

  @Override
  public Person mapFromContext(Object ctx) {
    Arguments.notNull("ctx", ctx);
    Person person = new Person();

    se.vgregion.kivtools.search.svc.ws.domain.kivws.Person kivwsPerson = (se.vgregion.kivtools.search.svc.ws.domain.kivws.Person) ctx;
    JAXBElement<String2ArrayOfAnyTypeMap> jaxbElmTmp = kivwsPerson.getAttributes();
    String2ArrayOfAnyTypeMap entries = jaxbElmTmp.getValue();
    attributes = new HashMap<String, List<Object>>();

    for (Entry entry : entries.getEntry()) {
      attributes.put(entry.getKey(), entry.getValue().getAnyType());
    }
    person.setCn(getSingleValue(KivwsPersonAttributes.cn.toString()));

    return person;
  }

  private String getSingleValue(String key) {
    String returnValue = "";
    if (attributes.containsKey(key)) {
      returnValue = (String) attributes.get(key).get(0);
    }
    return returnValue;
  }

  private List<String> getMultiValue(String key) {
    List<String> returnValue = new ArrayList<String>();
    if (attributes.containsKey(key)) {
      List<Object> list = attributes.get(key);
      for (Object object : list) {
        String tmp = (String) object;
        String[] split = tmp.split("\\$");
        for (String string : split) {
          returnValue.add(string.trim());
        }
      }
    }
    return returnValue;
  }

}
