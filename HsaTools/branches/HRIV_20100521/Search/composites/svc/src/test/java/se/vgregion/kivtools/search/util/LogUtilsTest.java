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

package se.vgregion.kivtools.search.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.TimeMeasurement;

public class LogUtilsTest {
  @Test
  public void testInstantiation() {
    LogUtils logUtils = new LogUtils();
    assertNotNull(logUtils);
  }

  @Test
  public void testPrintSikSearchResultListToLog() throws SikInternalException {
    LogMock logger = new LogMock();
    TimeMeasurement timeMeasurement = new TimeMeasurement(1234);
    SikSearchResultList<String> list = new SikSearchResultList<String>();
    LogUtils.printSikSearchResultListToLog(this, "testMethod", timeMeasurement, logger, list);

    List<String> expected = new ArrayList<String>();
    expected.add("*********************************************************");
    expected.add("Time measurements: LogUtilsTest::testMethod");
    expected.add("Total number of hits=0");
    expected.add("Overall elapsed time=1234 milli seconds");
    expected.add("Datasource elapsed time=0 milli seconds");
    expected.add("*********************************************************");

    logger.assertLoggedMessages(expected);
  }

  class LogMock implements Log {
    private List<Object> loggedMessages = new ArrayList<Object>();

    @Override
    public void debug(Object message) {
      this.loggedMessages.add(message);
    }

    public void assertLoggedMessages(List<? extends Object> expected) {
      assertEquals(expected, loggedMessages);
    }

    // Unimplemented methods

    @Override
    public void info(Object message) {
    }

    @Override
    public void debug(Object message, Throwable t) {
    }

    @Override
    public void error(Object message) {
    }

    @Override
    public void error(Object message, Throwable t) {
    }

    @Override
    public void fatal(Object message) {
    }

    @Override
    public void fatal(Object message, Throwable t) {
    }

    @Override
    public void info(Object message, Throwable t) {
    }

    @Override
    public boolean isDebugEnabled() {
      return false;
    }

    @Override
    public boolean isErrorEnabled() {
      return false;
    }

    @Override
    public boolean isFatalEnabled() {
      return false;
    }

    @Override
    public boolean isInfoEnabled() {
      return false;
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public boolean isWarnEnabled() {
      return false;
    }

    @Override
    public void trace(Object message) {
    }

    @Override
    public void trace(Object message, Throwable t) {
    }

    @Override
    public void warn(Object message) {
    }

    @Override
    public void warn(Object message, Throwable t) {
    }
  }
}
