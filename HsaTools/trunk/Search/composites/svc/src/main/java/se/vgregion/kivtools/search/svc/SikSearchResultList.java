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
/**
 * 
 */
package se.vgregion.kivtools.search.svc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import se.vgregion.kivtools.search.exceptions.SikInternalException;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
@SuppressWarnings("serial")
public class SikSearchResultList<T> extends ArrayList<T> implements List<T>, Serializable {

  private List<TimeMeasurement> timeMeasureMentList = new ArrayList<TimeMeasurement>();
  private int totalNumberOfFoundItems;

  public SikSearchResultList() {
  }

  public SikSearchResultList(Collection<? extends T> c) {
    super(c);
  }

  public void addDataSourceSearchTime(TimeMeasurement timeForAFetch) {
    timeMeasureMentList.add(timeForAFetch);
  }

  /**
   * @return The sum of all individual times needed
   * @throws SikInternalException
   */
  public long getTotalDataSourceSearchTimeInMilliSeconds() throws SikInternalException {
    long totalTime = 0;
    for (TimeMeasurement timeSlot : timeMeasureMentList) {
      totalTime += timeSlot.getElapsedTimeInMillisSeconds();
    }
    return totalTime;
  }

  /**
   * @return The sum of all individual times needed
   * @throws SikInternalException
   */
  public long getTotalDataSourceSearchTimeInSeconds() throws SikInternalException {
    return getTotalDataSourceSearchTimeInMilliSeconds() / 1000;
  }

  public int getTotalNumberOfFoundItems() {
    return totalNumberOfFoundItems;
  }

  public void setTotalNumberOfFoundItems(int totalNumberOfFoundItems) {
    this.totalNumberOfFoundItems = totalNumberOfFoundItems;
  }
}
