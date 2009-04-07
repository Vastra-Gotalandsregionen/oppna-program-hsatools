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

import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.util.Extractor;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 * Used for measuring 
 */
public class TimeMeasurement implements Serializable{
    
    private Long startTime = new Long(0);
    private Long stopTime = new Long(0);
    
    public TimeMeasurement() {
        super();
    }
    
    public TimeMeasurement(long timeInMilliseconds) {
        super();
        startTime= new Long(0);
        stopTime = new Long(timeInMilliseconds);
    }
    public void start() {
        startTime = Extractor.getNowAsLongObject();
    }

    public void stop() {
        stopTime = Extractor.getNowAsLongObject();
    }
    
    public Long getElapsedTimeInMillisSeconds() throws SikInternalException {
        if (startTime==null || stopTime==null) {
            throw new SikInternalException(this, "getElapsedTimeInMillisSeconds", "startTime or stopTime == null");
        }        
        return stopTime-startTime;
    }

    public Long getElapsedTimeInSeconds() throws SikInternalException {
        return getElapsedTimeInMillisSeconds()/1000;
    }
    
    public TimeMeasurement add(TimeMeasurement t1, TimeMeasurement t2) throws SikInternalException{
        TimeMeasurement t = new TimeMeasurement();
        t.startTime=new Long(0);
        t.stopTime=new Long(t1.getElapsedTimeInMillisSeconds() + t2.getElapsedTimeInMillisSeconds());
        return t;
    }
}
