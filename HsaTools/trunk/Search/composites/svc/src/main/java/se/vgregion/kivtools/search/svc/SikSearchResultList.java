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

package se.vgregion.kivtools.search.svc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of List that can keep track of how long the actual search in the datasource took.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 * @param <T> The type of object this list handles.
 */
@SuppressWarnings("serial")
public class SikSearchResultList<T> extends ArrayList<T> implements List<T>, Serializable {

  private List<TimeMeasurement> timeMeasureMentList = new ArrayList<TimeMeasurement>();
  private int totalNumberOfFoundItems;

  /**
   * Empty constructor.
   */
  public SikSearchResultList() {
  }

  /**
   * Constructor which takes a collection of objects to populate the list with.
   * 
   * @param c The collection of objects to populate the list with.
   */
  public SikSearchResultList(Collection<? extends T> c) {
    super(c);
  }

  /**
   * Adds a TimeMeasurement for one fetch to the list of time measurements.
   * 
   * @param timeForAFetch The TimeMeasurement to add to the list.
   */
  public void addDataSourceSearchTime(TimeMeasurement timeForAFetch) {
    timeMeasureMentList.add(timeForAFetch);
  }

  /**
   * Gets the total search time in milliseconds.
   * 
   * @return The sum of all individual times needed
   */
  public long getTotalDataSourceSearchTimeInMilliSeconds() {
    long totalTime = 0;
    for (TimeMeasurement timeSlot : timeMeasureMentList) {
      totalTime += timeSlot.getElapsedTimeInMillisSeconds();
    }
    return totalTime;
  }

  /**
   * Gets the total search time in seconds.
   * 
   * @return The sum of all individual times needed
   */
  public long getTotalDataSourceSearchTimeInSeconds() {
    return getTotalDataSourceSearchTimeInMilliSeconds() / 1000;
  }

  /**
   * Getter for the property totalNumberOfFoundItems.
   * 
   * @return The value of totalNumberOfFoundItems.
   */
  public int getTotalNumberOfFoundItems() {
    return totalNumberOfFoundItems;
  }

  /**
   * Setter for the property totalNumberOfFoundItems.
   * 
   * @param totalNumberOfFoundItems The value of totalNumberOfFoundItems.
   */
  public void setTotalNumberOfFoundItems(int totalNumberOfFoundItems) {
    this.totalNumberOfFoundItems = totalNumberOfFoundItems;
  }
}
