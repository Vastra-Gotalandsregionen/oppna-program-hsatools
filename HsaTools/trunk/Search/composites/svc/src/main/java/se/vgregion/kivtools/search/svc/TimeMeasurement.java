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

import se.vgregion.kivtools.search.util.Extractor;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Used for measuring
 */
public class TimeMeasurement implements Serializable {

  private Long startTime = Long.valueOf(0);
  private Long stopTime = Long.valueOf(0);

  public TimeMeasurement() {
    super();
  }

  public TimeMeasurement(long timeInMilliseconds) {
    super();
    startTime = Long.valueOf(0);
    stopTime = Long.valueOf(timeInMilliseconds);
  }

  public void start() {
    startTime = Extractor.getNowAsLongObject();
  }

  public void stop() {
    stopTime = Extractor.getNowAsLongObject();
  }

  public Long getElapsedTimeInMillisSeconds() {
    if (startTime == null || stopTime == null) {
      throw new RuntimeException("TimeMeasurement.getElapsedTimeInMillisSeconds, startTime or stopTime == null");
    }
    return stopTime - startTime;
  }

  public Long getElapsedTimeInSeconds() {
    return getElapsedTimeInMillisSeconds() / 1000;
  }

  public TimeMeasurement add(TimeMeasurement t1, TimeMeasurement t2) {
    TimeMeasurement t = new TimeMeasurement();
    t.startTime = Long.valueOf(0);
    t.stopTime = Long.valueOf(t1.getElapsedTimeInMillisSeconds() + t2.getElapsedTimeInMillisSeconds());
    return t;
  }
}
