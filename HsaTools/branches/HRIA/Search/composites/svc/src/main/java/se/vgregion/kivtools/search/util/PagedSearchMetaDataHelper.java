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

import java.util.ArrayList;
import java.util.List;

import se.vgregion.kivtools.search.presentation.types.PagedSearchMetaData;

/**
 * Helper-class for handling PagedSearchMetaData.
 * 
 * @author argoyle
 */
public class PagedSearchMetaDataHelper {
  /**
   * Builds a list of PagedSearchMetaData objects based on the provided list of data and the provided pageSize.
   * 
   * @param data The list of data.
   * @param pageSize The number of elements to show per page. Must be greater than zero.
   * @return A list of PagedSearchMetaData objects.
   */
  public static List<PagedSearchMetaData> buildPagedSearchMetaData(List<?> data, int pageSize) {
    if (pageSize <= 0) {
      throw new IllegalArgumentException("pageSize must be greater than zero");
    }
    List<PagedSearchMetaData> result = new ArrayList<PagedSearchMetaData>();
    if (data != null) {
      PagedSearchMetaData metaData;
      int size = data.size();
      int index = 0;
      if (size > 0) {
        while (index < size) {
          metaData = new PagedSearchMetaData();
          // 0 the first time
          metaData.setStartIndex(index);
          int endIndex = index + pageSize - 1;
          if (endIndex >= size) {
            endIndex = size - 1;
          }
          // e.g. 274 the first time
          metaData.setEndIndex(endIndex);
          result.add(metaData);
          // e.g. 275 the first time
          index = index + pageSize;
        }
      }
    }
    return result;
  }
}
