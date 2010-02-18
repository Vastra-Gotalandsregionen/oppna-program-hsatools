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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.ZipCode;

public class AddressTest {

  private Address address;

  @Before
  public void setUp() throws Exception {
    address = new Address();
  }

  @Test
  public void testConstructor() {
    address = new Address("street", null, "city", null);
    assertEquals("", address.getZipCode().getZipCode());
    assertEquals("street", address.getStreet());
    assertEquals("city", address.getCity());

    address = new Address("street", new ZipCode("12345"), null, null);
    assertEquals("12345", address.getZipCode().getZipCode());
  }

  @Test
  public void testGetConcatenatedAdditionalInfo() {
    assertEquals("", address.getConcatenatedAdditionalInfo());

    List<String> additionalInfo = new ArrayList<String>();
    additionalInfo.add("test1");
    additionalInfo.add(" test2   ");
    additionalInfo.add("test3");
    address.setAdditionalInfo(additionalInfo);
    assertEquals("test1, test2, test3", address.getConcatenatedAdditionalInfo());
  }

  @Test
  public void testIsEmpty() {
    assertTrue(address.isEmpty());
    address.setStreet("street");
    assertFalse(address.isEmpty());
    address.setStreet(null);
    address.setZipCode(new ZipCode("12345"));
    assertFalse(address.isEmpty());
    address.setZipCode(new ZipCode(""));
    address.setCity("city");
    assertFalse(address.isEmpty());
    address.setCity(null);

    List<String> additionalInfo = new ArrayList<String>();
    additionalInfo.add("2 trappor");
    address.setAdditionalInfo(additionalInfo);
    assertFalse(address.isEmpty());
    additionalInfo.clear();
    additionalInfo.add("");
    assertTrue(address.isEmpty());
  }
}
