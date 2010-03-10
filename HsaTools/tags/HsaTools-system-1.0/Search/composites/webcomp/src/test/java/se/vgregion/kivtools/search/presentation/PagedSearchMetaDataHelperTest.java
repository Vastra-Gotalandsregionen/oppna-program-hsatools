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

package se.vgregion.kivtools.search.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;
import se.vgregion.kivtools.search.util.PagedSearchMetaDataHelper;

public class PagedSearchMetaDataHelperTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testInstantiation() {
    PagedSearchMetaDataHelper helper = new PagedSearchMetaDataHelper();
    assertNotNull(helper);
  }

  @Test
  public void testBuildPagedSearchMetaData() {
    try {
      PagedSearchMetaDataHelper.buildPagedSearchMetaData(null, 0);
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      // Expected exception
    }

    List<PagedSearchMetaData> metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(null, 1);
    assertNotNull(metaData);
    assertEquals(0, metaData.size());

    List<Object> data = new ArrayList<Object>();
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(0, metaData.size());

    data.add(new Object());
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(1, metaData.size());

    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 3);
    assertNotNull(metaData);
    assertEquals(1, metaData.size());

    data.add(new Object());
    data.add(new Object());
    metaData = PagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    assertNotNull(metaData);
    assertEquals(3, metaData.size());
  }
}
