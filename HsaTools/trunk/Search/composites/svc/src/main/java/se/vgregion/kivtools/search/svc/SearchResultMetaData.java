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

import org.apache.commons.logging.Log;

import se.vgregion.kivtools.search.exceptions.SikInternalException;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Container for search meta data
 */
@SuppressWarnings("serial")
public class SearchResultMetaData implements Serializable {
	
	private int totalNumberOfHits = 0;
	// used to measure the responsetime of the datasource e.g. LDAP
	private TimeMeasurement dataSourceSearchTime = new TimeMeasurement();
	// used to measure the overall response time
	private TimeMeasurement contextSearchTime = new TimeMeasurement();

	public SearchResultMetaData() {
		super();
	}

	public void startContextSearchTime() {
		contextSearchTime.start();
	}

	public void stopContextSearchTime() {
		contextSearchTime.stop();
	}

	public void startDataSourceSearchTime() {
		dataSourceSearchTime.start();
	}

	public void stopDataSourceSearchTime() {
		dataSourceSearchTime.stop();
	}

	public TimeMeasurement getDataSourceSearchTime() {
		return dataSourceSearchTime;
	}

	public TimeMeasurement getContextSearchTime() {
		return contextSearchTime;
	}

	public int getTotalNumberOfHits() {
		return totalNumberOfHits;
	}

	public void setTotalNumberOfHits(int totalNumberOfHits) {
		this.totalNumberOfHits = totalNumberOfHits;
	}

	public void printToLog(Object obj, String methodName, Log logger) throws SikInternalException {
		logger.info("*********************************************************");
		logger.info("Time measurements: " + obj.getClass() + "::" + methodName);
		logger.info("Total number of hits=" + getTotalNumberOfHits());
		logger.info("Overall elapsed time=" + getContextSearchTime().getElapsedTimeInMillisSeconds() + " milli seconds");
		logger.info("Datasource elapsed time=" + getDataSourceSearchTime().getElapsedTimeInMillisSeconds() + " milli seconds");
		logger.info("*********************************************************");
	}
}
