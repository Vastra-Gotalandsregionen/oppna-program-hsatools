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
package se.vgregion.kivtools.search.svc;

import java.io.Serializable;

/**
 * Used for measuring.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class TimeMeasurement implements Serializable {
  private static final long serialVersionUID = 1L;

  private Long startTime = Long.valueOf(0);
  private Long stopTime = Long.valueOf(0);

  /**
   * Constructs a new TimeMeasurement object.
   */
  public TimeMeasurement() {
    super();
  }

  /**
   * Constructs a new TimeMeasurement object initialized to the provided value.
   * 
   * @param timeInMilliseconds The time in milli-seconds to initialize this object to.
   */
  public TimeMeasurement(long timeInMilliseconds) {
    super();
    startTime = Long.valueOf(0);
    stopTime = Long.valueOf(timeInMilliseconds);
  }

  /**
   * Start measurement.
   */
  public void start() {
    startTime = System.currentTimeMillis();
  }

  /**
   * Stops measurement.
   */
  public void stop() {
    stopTime = System.currentTimeMillis();
  }

  /**
   * Gets the last measurement in milli-seconds.
   * 
   * @return The time spent between the calls to start and stop in milli-seconds.
   */
  public Long getElapsedTimeInMillisSeconds() {
    return stopTime - startTime;
  }

  /**
   * Gets the last measurement in seconds.
   * 
   * @return The time spent between the calls to start and stop in seconds.
   */
  public Long getElapsedTimeInSeconds() {
    return getElapsedTimeInMillisSeconds() / 1000;
  }

  /**
   * Sums two TimeMeasurement objects.
   * 
   * @param t1 The first TimeMeasurement to sum.
   * @param t2 The seconds TimeMeasurement to sum.
   * @return A new TimeMeasurement with the elapsed time of the provided TimeMeasurement objects are summmed.
   */
  public static TimeMeasurement add(TimeMeasurement t1, TimeMeasurement t2) {
    TimeMeasurement t = new TimeMeasurement();
    t.startTime = Long.valueOf(0);
    t.stopTime = Long.valueOf(t1.getElapsedTimeInMillisSeconds() + t2.getElapsedTimeInMillisSeconds());
    return t;
  }
}
