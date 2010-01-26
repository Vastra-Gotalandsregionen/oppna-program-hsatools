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
package se.vgregion.kivtools.search.svc.impl.mock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnitLdapEntryMock extends LDAPEntryMock {

  public UnitLdapEntryMock(String name, String hsaIdentity, String objectClass, Date vgrCreateTimestamp, Date vgrModifyTimeStamp) {
    super();
    addAttribute("name", name);
    addAttribute("hsaIdentity", hsaIdentity);
    addAttribute("objectClass", objectClass);
    addAttribute("createTimestamp", getDateInZuloFormat(vgrCreateTimestamp));
    addAttribute("vgrModifyTimestamp", getDateInZuloFormat(vgrModifyTimeStamp));
  }

  private String getDateInZuloFormat(Date date) {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
    return dateFormatter.format(date);
  }
}
