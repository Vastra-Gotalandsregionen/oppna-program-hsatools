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

import static org.junit.Assert.*;
import geo.google.datamodel.GeoAltitude;
import geo.google.datamodel.GeoCoordinate;

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
    geoUtil = new GeoUtil();
  }

  @Test
  public void testGeocodeToWGS84FromHsaAddress() {
    double[] wgs84 = geoUtil.geocodeToWGS84FromHsaAddress(null, null);
    assertNull(wgs84);

    Address address = new Address();
    wgs84 = geoUtil.geocodeToWGS84FromHsaAddress(address, null);
    assertNull(wgs84);

    address.setStreet("Storgatan 1");
    wgs84 = geoUtil.geocodeToWGS84FromHsaAddress(address, GOOGLE_MAPS_KEY);
    assertNotNull(wgs84);

    address.setStreet("Dubbelfnuttvägen 123");
    wgs84 = geoUtil.geocodeToWGS84FromHsaAddress(address, GOOGLE_MAPS_KEY);
    assertNull(wgs84);

    address.setStreet("");
    address.setZipCode(new ZipCode("47293"));
    wgs84 = geoUtil.geocodeToWGS84FromHsaAddress(address, GOOGLE_MAPS_KEY);
    assertNull(wgs84);

  }

  @Test
  public void testGeocodeToRT90() throws Exception {
    int[] rt90 = geoUtil.geocodeToRT90(null, null);
    assertNull(rt90);

    Address address = new Address();
    rt90 = geoUtil.geocodeToRT90(address, null);
    assertNull(rt90);

    address.setStreet("Storgatan 1");
    rt90 = geoUtil.geocodeToRT90(address, GOOGLE_MAPS_KEY);
    assertNotNull(rt90);
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

    try {
      rt90 = GeoUtil.parseRT90HsaString("X: Y:");
      fail("StringIndexOutOfBoundsException expected");
    } catch (StringIndexOutOfBoundsException e) {
      // Expected exception
    }
    assertNull(rt90);

    rt90 = GeoUtil.parseRT90HsaString("X: 1234567, Y: 9876543");
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

  @Test
  public void testGetCloseUnits() {
    ArrayList<Unit> closeUnits = geoUtil.getCloseUnits(null, null, 10000, null);
    assertNotNull(closeUnits);
    assertEquals(0, closeUnits.size());

    try {
      closeUnits = geoUtil.getCloseUnits("Storgatan 1, Göteborg", null, 10000, GOOGLE_MAPS_KEY);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    ArrayList<Unit> allUnits = new ArrayList<Unit>();
    closeUnits = geoUtil.getCloseUnits("Storgatan 1, Göteborg", allUnits, 10000, GOOGLE_MAPS_KEY);
    assertEquals(0, closeUnits.size());

    Unit unit = new Unit();
    allUnits.add(unit);
    closeUnits = geoUtil.getCloseUnits("Storgatan 1, Göteborg", allUnits, 10000, GOOGLE_MAPS_KEY);
    assertEquals(0, closeUnits.size());

    unit.setGeoCoordinate(new GeoCoordinate(2.345, 1.234, new GeoAltitude()));
    closeUnits = geoUtil.getCloseUnits("Storgatan 1, Göteborg", allUnits, 10000, GOOGLE_MAPS_KEY);
    assertEquals(0, closeUnits.size());

    unit.setGeoCoordinate(new GeoCoordinate(11.945, 57.694, new GeoAltitude()));
    closeUnits = geoUtil.getCloseUnits("Storgatan 1, Göteborg", allUnits, 10000, GOOGLE_MAPS_KEY);
    assertEquals(1, closeUnits.size());
  }
}
