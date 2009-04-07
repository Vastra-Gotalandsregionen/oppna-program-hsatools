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
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class LdapParseTest {

	@Test
	public void testEscapeLDAPSearchFilter() {
		assertEquals("No special characters to escape",
				"Hi This is a test #çà", LdapParse.escapeLDAPSearchFilter("Hi This is a test #çà"));
		assertEquals("Hi \\28This\\29 = is \\2a a \\5c test # ç à ô",
				LdapParse.escapeLDAPSearchFilter("Hi (This) = is * a \\ test # ç à ô"));
	}
}
