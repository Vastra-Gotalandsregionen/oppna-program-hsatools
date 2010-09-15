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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ldap.core.ContextMapper;

import se.vgregion.kivtools.search.svc.ws.domain.kivws.Function;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2ArrayOfAnyTypeMap.Entry;
import se.vgregion.kivtools.util.Arguments;

public class SingleAttributeMapper implements ContextMapper {

  private String attributeName;
  private Map<String, List<Object>> ldapAttributes;

  public SingleAttributeMapper(String attributeName) {
    Arguments.notEmpty("attributeName", attributeName);
    this.attributeName = attributeName;
  }

  @Override
  public String mapFromContext(Object ctx) {
    ldapAttributes = new HashMap<String, List<Object>>();
    List<Entry> attributes = null;
    if (ctx instanceof Function) {
      Function kivwsFunction = (Function) ctx;
      attributes = kivwsFunction.getAttributes().getValue().getEntry();
    } else if (ctx instanceof se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit) {
      se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit kivwsUnit = (se.vgregion.kivtools.search.svc.ws.domain.kivws.Unit) ctx;
      attributes = kivwsUnit.getAttributes().getValue().getEntry();
    } else {
      throw new RuntimeException("Object is not a type of Function or Unit");
    }

    for (Entry entry : attributes) {
      ldapAttributes.put(entry.getKey(), entry.getValue().getAnyType());
    }

    return getSingleValue(attributeName);
  }

  private String getSingleValue(String key) {
    String returnValue = "";
    if (ldapAttributes.containsKey(key)) {
      returnValue = (String) ldapAttributes.get(key).get(0);
    }
    return returnValue;
  }

}