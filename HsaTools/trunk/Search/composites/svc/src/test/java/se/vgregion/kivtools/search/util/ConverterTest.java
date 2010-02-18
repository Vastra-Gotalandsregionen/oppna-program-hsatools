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

package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ConverterTest {

  @Test
  public void testInstantiation() {
    Converter converter = new Converter();
    assertNotNull(converter);
  }

  @Test
  public void testGetIntegerArrayList() {
    List<Integer> result = Converter.getIntegerArrayList(null);
    assertNotNull(result);
    assertEquals(0, result.size());

    List<String> input = new ArrayList<String>();
    result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(0, result.size());

    input.add("abc");
    result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(0, result.size());

    input.add("123");
    result = Converter.getIntegerArrayList(input);
    assertNotNull(result);
    assertEquals(1, result.size());
  }
}
