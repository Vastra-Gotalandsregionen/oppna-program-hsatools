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

import se.vgregion.kivtools.search.util.geo.CoordinateTransformerService;
import se.vgregion.kivtools.search.util.geo.GaussKrugerProjection;

/**
 * Test class for GaussKrugerProjection.
 * 
 * @author Jonas Liljenfeldt, Know IT
 */
public class TestGaussKrugerProjection {

	@Test
	public void testGetRT90() throws Exception {
		// Test "gon constructor"
		CoordinateTransformerService gkpSpecifiedGon = new GaussKrugerProjection(
				"7.5V");
		int rt90X = gkpSpecifiedGon.getRT90(67.877567, 21.060250)[0];
		int rt90Y = gkpSpecifiedGon.getRT90(67.877567, 21.060250)[1];
		// Test first three most significant
		assertTrue(String.valueOf(rt90X).equals("7563902")
				&& String.valueOf(rt90Y).equals("1908513"));

		// Test default constructor
		CoordinateTransformerService gkpDefault = new GaussKrugerProjection();
		rt90X = gkpDefault.getRT90(67.877567, 21.060250)[0];
		rt90Y = gkpDefault.getRT90(67.877567, 21.060250)[1];
		// Test first three most significant
		assertTrue(String.valueOf(rt90X).equals("7540983")
				&& String.valueOf(rt90Y).equals("1720733"));
	}

	@Test
	public void testGetWGS84() throws Exception {
		// Test "gon constructor"
		CoordinateTransformerService gkpSpecifiedGon = new GaussKrugerProjection(
				"7.5V");
		double wgs84LatDegree = gkpSpecifiedGon.getWGS84(7563930, 1908687)[0];
		double wgs84LonDegree = gkpSpecifiedGon.getWGS84(7563930, 1908687)[1];

		// Test first three most significant
		assertTrue(String.valueOf(wgs84LatDegree).substring(0, 2).equals("67")
				&& String.valueOf(wgs84LonDegree).substring(0, 2).equals("21"));

		// Test default constructor
		CoordinateTransformerService gkpDefault = new GaussKrugerProjection();
		wgs84LatDegree = gkpDefault.getWGS84(7563930, 1908687)[0];
		wgs84LonDegree = gkpDefault.getWGS84(7563930, 1908687)[1];
		// Test first three most significant
		assertTrue(String.valueOf(wgs84LatDegree).substring(0, 2).equals("67")
				&& String.valueOf(wgs84LonDegree).substring(0, 2).equals("25"));
	}

	@Test
	public void testConversionBackAndForth() throws Exception {
		// Test "gon constructor"
		CoordinateTransformerService gkpSpecifiedGon = new GaussKrugerProjection(
				"7.5V");
		double wgs84LatDegree = gkpSpecifiedGon.getWGS84(7563930, 1908687)[0];
		double wgs84LonDegree = gkpSpecifiedGon.getWGS84(7563930, 1908687)[1];
		int rt90X = gkpSpecifiedGon.getRT90(wgs84LatDegree, wgs84LonDegree)[0];
		int rt90Y = gkpSpecifiedGon.getRT90(wgs84LatDegree, wgs84LonDegree)[1];

		// Did we get the same?
		assertTrue(String.valueOf(rt90X).substring(0, 5).equals("75639")
				&& String.valueOf(rt90Y).substring(0, 5).equals("19086"));

		CoordinateTransformerService gkpDefault = new GaussKrugerProjection();
		wgs84LatDegree = gkpDefault.getWGS84(7563930, 1908687)[0];
		wgs84LonDegree = gkpDefault.getWGS84(7563930, 1908687)[1];
		rt90X = gkpDefault.getRT90(wgs84LatDegree, wgs84LonDegree)[0];
		rt90Y = gkpDefault.getRT90(wgs84LatDegree, wgs84LonDegree)[1];

		// Did we get the same?
		assertTrue(String.valueOf(rt90X).substring(0, 5).equals("75639")
				&& String.valueOf(rt90Y).substring(0, 5).equals("19086"));
	}
}
