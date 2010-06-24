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

import se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap;

/**
 * Map the return result from webservice to the code name table
 * 
 * @author Nhi Ly
 * 
 */
public class KivwsCodeNameTableMapper implements ContextMapper {

  private final Map<String, String> codeTableContent = new HashMap<String, String>();

  @Override
  public Object mapFromContext(Object ctx) {

    String2StringMap context = null;
    if (ctx instanceof String2StringMap) {
      context = (String2StringMap) ctx;
    } else {
      throw new RuntimeException("Object is not a type of String2StringMap");
    }

    List<se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap.Entry> entryList = context.getEntry();
    for (se.vgregion.kivtools.search.svc.ws.domain.kivws.String2StringMap.Entry entry : entryList) {
      if (!entry.getKey().startsWith("* ")) {
        codeTableContent.put(entry.getKey(), entry.getValue());
      }
    }

    return null;
  }

  public Map<String, String> getCodeTableContent() {
    return codeTableContent;
  }

}
