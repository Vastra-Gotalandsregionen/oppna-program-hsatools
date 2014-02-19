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

package se.vgregion.kivtools.search.util.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.Address;
import se.vgregion.kivtools.search.domain.values.ZipCode;

public class GeoUtilTest {
  private static final String GOOGLE_MAPS_KEY = "ABQIAAAAsj7OTaHyEfNXhETUKuAVeBStFeF4n64ejGN5IPknXd-RNbYWcBREjFsf4BWmaarbveYhRN4pqE33og";

  private GeoUtil geoUtil;

  @Before
  public void setUp() throws Exception {
    this.geoUtil = new GeoUtil();
  }

 

  @Test
  public void testParseRT90HsaString() {
    int[] rt90 = null;
    rt90 = GeoUtil.parseRT90HsaString(null);
    assertNull(rt90);

    rt90 = GeoUtil.parseRT90HsaString("");
    assertNull(rt90);

    rt90 = GeoUtil.parseRT90HsaString("X:");
    assertNull(rt90);

    rt90 = GeoUtil.parseRT90HsaString("Y:");
    assertNull(rt90);

 

    rt90 = GeoUtil.parseRT90HsaString("X: 1234567, Y: 9876543");
    assertEquals(1234567, rt90[0]);
    assertEquals(9876543, rt90[1]);
    
    rt90 = GeoUtil.parseRT90HsaString("X:1234567,Y:9876543");
    assertEquals(1234567, rt90[0]);
    assertEquals(9876543, rt90[1]);
  }

  @Test
  public void testGetLatLongRadiansDecimal() {
    try {
      GeoUtil.getLatLongRadiansDecimal(null, false);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("", false);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("D", false);
      fail("NumberFormatException expected");
    } catch (NumberFormatException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("123D", false);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("123D.", false);
      fail("NumberFormatException expected");
    } catch (NumberFormatException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("123D45.", false);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("123D45.\"", false);
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      // Expected exception
    }

    try {
      GeoUtil.getLatLongRadiansDecimal("123D45.  \"", false);
      fail("NumberFormatException expected");
    } catch (NumberFormatException e) {
      // Expected exception
    }

    double radiansDecimal = GeoUtil.getLatLongRadiansDecimal("123D45.32\"", false);
    assertEquals(2.160000089720938, radiansDecimal, 0.0);

    radiansDecimal = GeoUtil.getLatLongRadiansDecimal("123D45.32\"S", true);
    assertEquals(-2.160000089720938, radiansDecimal, 0.0);

    radiansDecimal = GeoUtil.getLatLongRadiansDecimal("123D45.32\"W", false);
    assertEquals(-2.160000089720938, radiansDecimal, 0.0);
  }

  @Test
  public void testGetGradeMinSec() {
    double[] gradeMinSec = GeoUtil.getGradeMinSec(45.1245);
    assertEquals(45.0, gradeMinSec[0], 0.0);
    assertEquals(7.0, gradeMinSec[1], 0.0);
    assertEquals(28.199999999991405, gradeMinSec[2], 0.0);
  }
}
