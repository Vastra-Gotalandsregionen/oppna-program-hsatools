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
package se.vgregion.kivtools.search.util;

import org.apache.commons.logging.Log;

import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;
import se.vgregion.kivtools.search.svc.domain.Unit;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 *
 */
public class LogUtils {
    public static void printSikSearchResultListToLog(Object obj, String methodName, TimeMeasurement overallTime, 
                                                     Log logger, SikSearchResultList<?> list) throws SikInternalException{
        logger.info("*********************************************************");
        logger.info("Time measurements: " + obj.getClass().getSimpleName() + "::" + methodName);
        logger.info("Total number of hits=" + list.getTotalNumberOfFoundItems());
        logger.info("Overall elapsed time=" + overallTime.getElapsedTimeInMillisSeconds() + " milli seconds");
        logger.info("Datasource elapsed time=" + list.getTotalDataSourceSearchTimeInMilliSeconds() + " milli seconds");
        logger.info("*********************************************************");
    }

}
