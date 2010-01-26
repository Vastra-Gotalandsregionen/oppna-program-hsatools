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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;

public class PageSearchMetaDataHelperTest {

  private static final String ERROR_EXCEPTION_MESSAGE = "pageSize must be greater than zero";
  PagedSearchMetaDataHelper pagedSearchMetaDataHelper = new PagedSearchMetaDataHelper();

  /**
   * Test split data to chosen pages.
   */
  @Test
  public void testBuildPagedSearchMetaData() {
    List<String> data = Arrays.asList("data1", "data2", "data3", "data4", "data5");
    List<PagedSearchMetaData> result = pagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 1);
    // Page size 1, only one index content from the data list on each page.
    assertEquals(5, result.size());

    // Page size 2, two index content from the data list on each page.
    result = pagedSearchMetaDataHelper.buildPagedSearchMetaData(data, 2);
    // Data in data list to show on first page.
    assertEquals(0, result.get(0).getStartIndex());
    assertEquals(1, result.get(0).getEndIndex());
    // Data in data list to show on second page.
    assertEquals(2, result.get(1).getStartIndex());
    assertEquals(3, result.get(1).getEndIndex());
    // Data in data list to show on third page.
    assertEquals(4, result.get(2).getStartIndex());
    assertEquals(4, result.get(2).getEndIndex());
  }
  
  @Test
  public void testErrorhandlingForPageSize(){
    // Test pageSize -1 and 0
    for (int i = -1; i < 1 ; i++) {
      try{
        pagedSearchMetaDataHelper.buildPagedSearchMetaData(new ArrayList<String>(), i);
        fail("Should throw an exception");
      }catch (IllegalArgumentException e) {
        assertEquals(ERROR_EXCEPTION_MESSAGE, e.getMessage());
      }
    }
   
  }

}
