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
package se.vgregion.kivtools.search.svc;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class SikSearchResultListTest {
  @Test
  public void newListReturnEmptyList() {
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>();
    assertNotNull(sikSearchResultList);
    assertEquals(0, sikSearchResultList.size());
  }

  @Test
  public void newListWithCollectionReturnNewListWithContentFromCollection() {
    Collection<String> collection = Arrays.asList("abc", "def");
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>(collection);
    assertNotNull(sikSearchResultList);
    assertEquals(2, sikSearchResultList.size());
    assertTrue("unexpected list content", sikSearchResultList.containsAll(collection));
  }

  @Test
  public void getTotalDataSourceSearchTimeInMilliSecondsReturnZeroWhenNoMeasurementsHasBeenAdded() {
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>();
    assertEquals(0, sikSearchResultList.getTotalDataSourceSearchTimeInMilliSeconds());
  }

  @Test
  public void getTotalDataSourceSearchTimeInMilliSecondsSumsAllAddedTimeMeasurements() {
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>();
    sikSearchResultList.addDataSourceSearchTime(new TimeMeasurement(100));
    sikSearchResultList.addDataSourceSearchTime(new TimeMeasurement(150));

    assertEquals(250, sikSearchResultList.getTotalDataSourceSearchTimeInMilliSeconds());
  }

  @Test
  public void getTotalDataSourceSearchTimeInSecondsReturnTotalSearchTimeInSeconds() {
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>();
    sikSearchResultList.addDataSourceSearchTime(new TimeMeasurement(1000));
    sikSearchResultList.addDataSourceSearchTime(new TimeMeasurement(1500));

    assertEquals(2, sikSearchResultList.getTotalDataSourceSearchTimeInSeconds());
  }

  @Test
  public void getTotalNumberOfFoundItemsReturnTheSetValue() {
    SikSearchResultList<String> sikSearchResultList = new SikSearchResultList<String>();
    assertEquals(0, sikSearchResultList.getTotalNumberOfFoundItems());

    sikSearchResultList.setTotalNumberOfFoundItems(1234);
    assertEquals(1234, sikSearchResultList.getTotalNumberOfFoundItems());
  }
}
