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
package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FormatterTest {
  @Test
  public void testInstantiation() {
    Formatter formatter = new Formatter();
    assertNotNull(formatter);
  }

  @Test
  public void testChopUpString() {
    List<String> values = new ArrayList<String>();

    values = Formatter.chopUpStringToList(values, null, null);
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 0, values.size());

    values = Formatter.chopUpStringToList(values, "", "");
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 0, values.size());

    values = Formatter.chopUpStringToList(values, "teststring", "$");
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 1, values.size());
    assertEquals("Unexpected value returned", "teststring", values.get(0));

    values.clear();

    values = Formatter.chopUpStringToList(values, "teststring$teststring2", "$");
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 2, values.size());
    assertEquals("Unexpected value returned", "teststring", values.get(0));
    assertEquals("Unexpected value returned", "teststring2", values.get(1));

    values.clear();

    values = Formatter.chopUpStringToList(values, "teststring$ $teststring2", "$");
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 2, values.size());
    assertEquals("Unexpected value returned", "teststring", values.get(0));
    assertEquals("Unexpected value returned", "teststring2", values.get(1));

    values.clear();

    values = Formatter.chopUpStringToList(values, "teststring$teststring2$ $$$", "$");
    assertNotNull("Unexpected null returned", values);
    assertEquals("Unexpected size of returned list", 2, values.size());
    assertEquals("Unexpected value returned", "teststring", values.get(0));
    assertEquals("Unexpected value returned", "teststring2", values.get(1));
  }

  @Test
  public void testReplaceStringInString() throws Exception {
    assertNull(Formatter.replaceStringInString(null, null, null));

    String original = "A quick brown fox";
    assertEquals(original, Formatter.replaceStringInString(original, "giraffe", "wolf"));

    String expected = "A quick brown wolf";
    String result = Formatter.replaceStringInString(original, "fox", "wolf");
    assertEquals(expected, result);

    expected = "A quick gray fox";
    result = Formatter.replaceStringInString(original, "brown", "gray");
    assertEquals(expected, result);

    expected = "  ";
    result = Formatter.replaceStringInString(" ", " ", "  ");
    assertEquals(expected, result);

    StringBuilder builder = new StringBuilder();
    while (builder.length() < 12000) {
      builder.append(" ");
    }

    try {
      Formatter.replaceStringInString(builder.toString(), " ", ".");
      fail("Exception expected");
    } catch (RuntimeException e) {
      // Expected exception
    }
  }
}
