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

import org.apache.commons.logging.Log;

import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;

/**
 * Helper class for logging information on a search result.
 * 
 * @author hangy2 , Hans Gyllensten / KnowIT
 */
public class LogUtils {
  /***
   * Logs information on a search result.
   * 
   * @param obj The object that called the log method.
   * @param methodName The method that called the the log method.
   * @param overallTime The overall time the search took.
   * @param logger The logger to use.
   * @param list The list of search results.
   * @throws SikInternalException If anything goes wrong.
   */
  public static void printSikSearchResultListToLog(Object obj, String methodName, TimeMeasurement overallTime, Log logger, SikSearchResultList<?> list) throws SikInternalException {
    logger.debug("*********************************************************");
    logger.debug("Time measurements: " + obj.getClass().getSimpleName() + "::" + methodName);
    logger.debug("Total number of hits=" + list.getTotalNumberOfFoundItems());
    logger.debug("Overall elapsed time=" + overallTime.getElapsedTimeInMillisSeconds() + " milli seconds");
    logger.debug("Datasource elapsed time=" + list.getTotalDataSourceSearchTimeInMilliSeconds() + " milli seconds");
    logger.debug("*********************************************************");
  }
}
