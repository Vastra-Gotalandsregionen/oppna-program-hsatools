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
package se.vgregion.kivtools.search.svc.domain.values;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DNTest {

  @Test
  public void testEqualsObjectEmptyDN() {
    DN dn = new DN();
    assertTrue(dn.equals(dn));
    assertFalse(dn.equals(null));
    assertFalse(dn.equals(""));

    DN other = new DN();
    assertTrue(dn.equals(other));
    other = new DN(getList("cn=a"), null, null, null);
    assertFalse(dn.equals(other));
  }

  @Test
  public void testEqualsPopulatedDN() {
    DN dn = DN.createDNFromString("cn=a");
    DN other = DN.createDNFromString("cn=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a");
    other = DN.createDNFromString("cn=a,dc=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a,o=a");
    other = DN.createDNFromString("cn=a,dc=a,o=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a,o=a,ou=a");
    other = DN.createDNFromString("cn=a,dc=a,o=a,ou=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a,o=a,ou=a");
    other = DN.createDNFromString("cn=a,dc=a,o=a,ou=a");
    assertTrue(dn.equals(other));
  }

  private List<String> getList(String... strings) {
    List<String> list = new ArrayList<String>();

    if (strings != null) {
      for (String string : list) {
        list.add(string);
      }
    }

    return list;
  }
}
