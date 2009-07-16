/**
 * Copyright 2009 Västa Götalandsregionen
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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Standard LDAP search filter escaping test.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class LdapParseTest {

  @Test
  public void testEscapeLDAPSearchFilter() {
    assertEquals("No special characters to escape", "Hi This is a test #çà", LdapParse.escapeLDAPSearchFilter("Hi This is a test #çà"));
    assertEquals("Hi \\28This\\29 = is \\2a a \\5c test # ç à ô", LdapParse.escapeLDAPSearchFilter("Hi (This) = is * a \\ test # ç à ô"));
  }

  @Test
  public void testGetDayName() {
    assertEquals("Unexpected returned value for null", "", LdapParse.getDayName(null));
    assertEquals("Unexpected returned value for empty string", "", LdapParse.getDayName(""));
    assertEquals("Unexpected returned value for non numeric string", "", LdapParse.getDayName("xx"));
    assertEquals("Unexpected returned value for 0", "Söndag", LdapParse.getDayName("0"));
    assertEquals("Unexpected returned value for 1", "Måndag", LdapParse.getDayName("1"));
    assertEquals("Unexpected returned value for 2", "Tisdag", LdapParse.getDayName("2"));
    assertEquals("Unexpected returned value for 3", "Onsdag", LdapParse.getDayName("3"));
    assertEquals("Unexpected returned value for 4", "Torsdag", LdapParse.getDayName("4"));
    assertEquals("Unexpected returned value for 5", "Fredag", LdapParse.getDayName("5"));
    assertEquals("Unexpected returned value for 6", "Lördag", LdapParse.getDayName("6"));
    assertEquals("Unexpected returned value for 7", "", LdapParse.getDayName("7"));
    assertEquals("Unexpected returned value for 10", "", LdapParse.getDayName("10"));
  }

  @Test
  public void testVgrObjectStatusToText() {
    assertEquals("Unexpected returned value for null", "", LdapParse.vgrObjectStatusToText(null));
    assertEquals("Unexpected returned value for empty string", "", LdapParse.vgrObjectStatusToText(""));
    assertEquals("Unexpected returned value for non numeric string", "", LdapParse.vgrObjectStatusToText("xx"));
    assertEquals("Unexpected returned value for 0", "[Klarmarkerad]", LdapParse.vgrObjectStatusToText("0"));
    assertEquals("Unexpected returned value for 10", "[Palett] Ny anställning", LdapParse.vgrObjectStatusToText("10"));
    assertEquals("Unexpected returned value for 11", "[Palett] Ansvarsnummer", LdapParse.vgrObjectStatusToText("11"));
    assertEquals("Unexpected returned value for 12", "[Palett] Titel", LdapParse.vgrObjectStatusToText("12"));
    assertEquals("Unexpected returned value for 20", "Ny person", LdapParse.vgrObjectStatusToText("20"));
    assertEquals("Unexpected returned value for 21", "[Västfolket] Namn", LdapParse.vgrObjectStatusToText("21"));
    assertEquals("Unexpected returned value for 22", "[Västfolket] Sekretessmark.", LdapParse.vgrObjectStatusToText("22"));
    assertEquals("Unexpected returned value for 30", "[Notes] E-postadress", LdapParse.vgrObjectStatusToText("30"));
    assertEquals("Unexpected returned value for 7", "", LdapParse.vgrObjectStatusToText("7"));
  }

  @Test
  public void testIsValidDateFormat() {
    assertTrue(LdapParse.isValidDateFormat("2009-01-01", "yyyy-MM-dd"));
    assertFalse(LdapParse.isValidDateFormat("2009-01-00", "yyyy-MM-dd"));
    assertFalse(LdapParse.isValidDateFormat("xxxx-01-00", "yyyy-MM-dd"));
  }
}
