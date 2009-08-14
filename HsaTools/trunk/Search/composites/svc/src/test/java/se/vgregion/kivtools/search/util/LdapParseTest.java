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
  public void testInstantiation() {
    LdapParse ldapParse = new LdapParse();
    assertNotNull(ldapParse);
  }

  @Test
  public void testEscapeLDAPSearchFilter() {
    assertNull("Null input string should return null", LdapParse.escapeLDAPSearchFilter(null));
    assertEquals("No special characters to escape", "Hi This is a test #çà", LdapParse.escapeLDAPSearchFilter("Hi This is a test #çà"));
    assertEquals("Hi \\28This\\29 = is \\2a a \\5c test # ç à ô\\00", LdapParse.escapeLDAPSearchFilter("Hi (This) = is * a \\ test # ç à ô\u0000"));
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
}
