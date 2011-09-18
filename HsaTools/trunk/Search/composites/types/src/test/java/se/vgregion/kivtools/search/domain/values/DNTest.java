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

package se.vgregion.kivtools.search.domain.values;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class DNTest {

  @Test
  public void testEquals() {
    DN dn = DN.createDNFromString("cn=a");
    assertFalse(dn.equals(null));
    assertFalse(dn.equals(this));

    DN other = DN.createDNFromString("cn=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a");
    other = DN.createDNFromString("cn=a,dc=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,dc=a,o=a");
    other = DN.createDNFromString("cn=a,dc=a,o=b");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    other = DN.createDNFromString("cn=a,ou=b,dc=a,o=a");
    assertFalse(dn.equals(other));

    dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    other = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertTrue(dn.equals(other));

    dn = DN.createDNFromString("cn=a,ou=a");
    other = DN.createDNFromString("cn=a,ou=b,dc=a,o=a");
    assertFalse(dn.equals(other));
  }

  @Test
  public void testEscape() {
    DN dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertEquals("cn=a,ou=a,dc=a,o=a", dn.escape().toString());
    dn = DN.createDNFromString("cn=a\\,b,ou=a\\,b,dc=a\\,b,o=a");
    assertEquals("cn=a\\,b,ou=a\\,b,dc=a\\,b,o=a", dn.escape().toString());
  }

  @Test
  public void plusSignIsEscaped() {
    DN dn = DN.createDNFromString("cn=Jourcentral\\, Kungsportsakuten Kungsportsläkarna\\, Läkarhuset \\+7,ou=Läkarhuset \\+7\\, Vårdcentralen,ou=Privata vårdgivare,ou=Org,o=VGR");
    String escaped = dn.escape().toString();
    assertEquals("cn=Jourcentral\\, Kungsportsakuten Kungsportsläkarna\\, Läkarhuset \\+7,ou=Läkarhuset \\+7\\, Vårdcentralen,ou=Privata vårdgivare,ou=Org,o=VGR", escaped);
  }

  @Test
  public void testGetIsUnitAndAdministrationOnSameLevel() {
    DN dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertEquals("true", dn.getIsUnitAndAdministrationOnSameLevel());
    dn = DN.createDNFromString("cn=a,ou=a,ou=b,ou=c,dc=a,o=a");
    assertEquals("false", dn.getIsUnitAndAdministrationOnSameLevel());
  }

  @Test
  public void testGetAdministration() {
    DN dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertNull(dn.getAdministration());
    dn = DN.createDNFromString("cn=a,ou=a,ou=b,dc=a,o=a");
    assertEquals(dn, dn.getAdministration());
    dn = DN.createDNFromString("cn=a,ou=a,ou=b,ou=c,dc=a,o=a");
    assertEquals("ou=b,ou=c,dc=a,o=a", dn.getAdministration().toString());
  }

  @Test
  public void testGetUnitName() {
    DN dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertEquals("a", dn.getUnitName());
    dn = DN.createDNFromString("cn=a,dc=a,o=a");
    assertEquals("", dn.getUnitName());
  }

  @Test
  public void testGetUnit() {
    DN dn = DN.createDNFromString("cn=a,dc=a,o=a");
    assertNull("", dn.getUnit());
    dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    assertEquals("ou=a,dc=a,o=a", dn.getUnit().toString());
    dn = DN.createDNFromString("cn=a,ou=a,dc=a,dc=a");
    assertEquals("ou=a,dc=a,dc=a", dn.getUnit().toString());
  }

  @Test
  public void testGetParentDN() {
    DN dn = DN.createDNFromString("cn=a,dc=a,o=a");
    DN parentDn = dn.getParentDN();
    assertEquals("dc=a,o=a", parentDn.toString());

    dn = DN.createDNFromString("cn=a,cn=b,dc=a,o=a");
    parentDn = dn.getParentDN();
    assertEquals("cn=b,dc=a,o=a", parentDn.toString());

    dn = DN.createDNFromString("ou=a,dc=a,o=a");
    parentDn = dn.getParentDN();
    assertEquals("dc=a,o=a", parentDn.toString());

    dn = DN.createDNFromString("dc=a,dc=b,o=a");
    parentDn = dn.getParentDN();
    assertNull(parentDn);
  }

  @Test
  public void testGetAncestors() {
    DN dn = DN.createDNFromString("cn=a,ou=a,ou=b,ou=c,dc=a,o=a");

    List<DN> ancestors = dn.getAncestors();
    assertNotNull(ancestors);
    assertEquals(1, ancestors.size());
    assertEquals(1, ancestors.get(0).getPosition());

    dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    ancestors = dn.getAncestors();
    assertNull(ancestors);
  }

  @Test
  public void testGetAncestor() {
    DN dn = DN.createDNFromString("cn=a,ou=a,ou=b,ou=c,dc=a,o=a");
    DN ancestor = dn.getAncestor(0);
    assertEquals(dn, ancestor);

    ancestor = dn.getAncestor(-2);
    assertEquals("ou=c,dc=a,o=a", ancestor.toString());

    ancestor = dn.getAncestor(2);
    assertEquals("ou=b,ou=c,dc=a,o=a", ancestor.toString());
  }

  @Test
  public void testGetUrlEncoded() {
    DN dn = DN.createDNFromString("cn=a,ou=a,ou=b,ou=c,dc=a,o=a");
    String result = dn.getUrlEncoded();
    assertEquals("cn%3Da%2Cou%3Da%2Cou%3Db%2Cou%3Dc%2Cdc%3Da%2Co%3Da", result);
  }

  @Test
  public void testIterator() {
    DN dn = DN.createDNFromString("cn=a,ou=a,dc=a,o=a");
    Iterator<DN> iterator = dn.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("cn=a,ou=a,dc=a,o=a", iterator.next().toString());
    assertTrue(iterator.hasNext());
    assertEquals("ou=a,dc=a,o=a", iterator.next().toString());
    assertTrue(iterator.hasNext());
    assertEquals("dc=a,o=a", iterator.next().toString());
    assertFalse(iterator.hasNext());
    assertNull(iterator.next());

    try {
      iterator.remove();
      fail("UnsupportedOperationException expected");
    } catch (UnsupportedOperationException e) {
      // Expected exception
    }

    dn = DN.createDNFromString("cn=a,ou=a,dc=a");
    iterator = dn.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("cn=a,ou=a,dc=a", iterator.next().toString());
    assertTrue(iterator.hasNext());
    assertEquals("ou=a,dc=a", iterator.next().toString());
    assertTrue(iterator.hasNext());
    assertEquals("dc=a", iterator.next().toString());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void testHashCode() {
    DN dn = DN.createDNFromString("");

    int hashCode = dn.hashCode();
    assertEquals(954274, hashCode);
  }
}
