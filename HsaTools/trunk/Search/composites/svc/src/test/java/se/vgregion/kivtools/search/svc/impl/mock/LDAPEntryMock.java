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

import java.util.HashMap;
import java.util.Map;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPEntry;

public class LDAPEntryMock extends LDAPEntry {
  private String dn = "";
  private Map<String, LDAPAttribute> attributes = new HashMap<String, LDAPAttribute>();

  public LDAPEntryMock() {
  
  }
  
  public LDAPEntryMock(String key, String[] values){
    attributes.put(key, new LDAPAttribute(key, values));
  }
  
  public LDAPEntryMock(String key, String value) {
    attributes.put(key, new LDAPAttribute(key, value));
  }
  
  public void addAttribute(String key, String value) {
    attributes.put(key, new LDAPAttribute(key, value));
  }

  public void addAttribute(String key, String[] values) {
    attributes.put(key, new LDAPAttribute(key, values));
  }

  public void setDn(String dn) {
    this.dn = dn;
  }

  @Override
  public LDAPAttribute getAttribute(String attrName) {
    LDAPAttribute attribute = attributes.get(attrName);
    return attribute;
  }

  @Override
  public String getDN() {
    return this.dn;
  }
}
