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
 */
package se.vgregion.kivtools.search.svc.ldap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;

public class DirContextOperationsHelperTest {
  private final DirContextOperationsMock dirContext = new DirContextOperationsMock();
  private final DirContextOperationsHelper helper = new DirContextOperationsHelper(dirContext);

  @Before
  public void setUp() {
    dirContext.setDn(DistinguishedName.immutableDistinguishedName("cn=Kalle Kula,ou=Landstinget Halland"));
    dirContext.addAttributeValue("single", "single value");
    dirContext.addAttributeValue("multiple0", "");
    dirContext.addAttributeValue("multiple1", "value1");
    dirContext.addAttributeValue("multiple2", "value1$value2$value3");
    dirContext.addAttributeValue("multiple3", "$$$value1$$value2$$value3$$$$");
    dirContext.addAttributeValue("whitespace", " \n \r\n \t value \t \r\n \n  ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorThrowsExceptionIfNoDirContextOperationsIsProvided() {
    new DirContextOperationsHelper(null);
  }

  @Test
  public void getStringReturnEmptyStringIfAttributeIsNotFound() {
    String result = helper.getString("unknown");
    assertEquals("", result);
  }

  @Test
  public void getStringReturnCorrectValueForValidAttribute() {
    String result = helper.getString("single");
    assertEquals("single value", result);
  }

  @Test
  public void getStringReturnTheFoundValueTrimmedFromWhitespaceAtStartAndEnd() {
    String result = helper.getString("whitespace");
    assertEquals("value", result);
  }

  @Test
  public void getStringsReturnEmptyListIfAttributeIsNotFound() {
    List<String> result = helper.getStrings("unknown");
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void getStringsReturnCorrectValuesForValidAttribute() {
    List<String> result = helper.getStrings("multiple0");
    assertEquals(0, result.size());
    result = helper.getStrings("multiple1");
    assertArrayEquals(new String[] { "value1" }, result.toArray());
    result = helper.getStrings("multiple2");
    assertArrayEquals(new String[] { "value1", "value2", "value3" }, result.toArray());
    result = helper.getStrings("multiple3");
    assertArrayEquals(new String[] { "value1", "value2", "value3" }, result.toArray());
  }

  @Test
  public void hasAttributeReturnFalseIfAttributeIsNotFound() {
    assertFalse(helper.hasAttribute("unknown"));
  }

  @Test
  public void hasAttributeReturnTrueForValidAttribute() {
    assertTrue(helper.hasAttribute("single"));
  }

  @Test
  public void getDnStringReturnTheDistinguishedNameOfTheContext() {
    String dnString = helper.getDnString();
    assertEquals("cn=Kalle Kula,ou=Landstinget Halland", dnString);
  }
}
