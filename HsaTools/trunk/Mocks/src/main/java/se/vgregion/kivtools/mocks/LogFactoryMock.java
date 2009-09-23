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
package se.vgregion.kivtools.mocks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

public class LogFactoryMock extends LogFactory {
  StringBuilder info = new StringBuilder();
  StringBuilder debug = new StringBuilder();
  StringBuilder warning = new StringBuilder();
  StringBuilder error = new StringBuilder();
  StringBuilder trace = new StringBuilder();
  StringBuilder fatal = new StringBuilder();

  private Log log = new LogMock();

  public static LogFactoryMock createInstance() {
    LogFactoryMock.resetInstance();
    System.setProperty("org.apache.commons.logging.LogFactory", "se.vgregion.kivtools.mocks.LogFactoryMock");
    LogFactory.releaseAll();
    return (LogFactoryMock) LogFactory.getFactory();
  }

  public static void resetInstance() {
    System.clearProperty("org.apache.commons.logging.LogFactory");
    LogFactory.releaseAll();
  }

  public String getInfo(boolean clear) {
    String result = this.info.toString();

    if (clear) {
      this.info.setLength(0);
    }
    return result;
  }

  public String getDebug(boolean clear) {
    String result = this.debug.toString();

    if (clear) {
      this.debug.setLength(0);
    }
    return result;
  }

  public String getWarning(boolean clear) {
    String result = this.warning.toString();

    if (clear) {
      this.warning.setLength(0);
    }
    return result;
  }

  public String getError(boolean clear) {
    String result = this.error.toString();

    if (clear) {
      this.error.setLength(0);
    }
    return result;
  }

  public String getFatal(boolean clear) {
    String result = this.fatal.toString();

    if (clear) {
      this.fatal.setLength(0);
    }
    return result;
  }

  public String getTrace(boolean clear) {
    String result = this.trace.toString();

    if (clear) {
      this.trace.setLength(0);
    }
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Log getInstance(Class clazz) throws LogConfigurationException {
    return log;
  }

  @Override
  public Log getInstance(String name) throws LogConfigurationException {
    return log;
  }

  // Not implemented methods

  @Override
  public Object getAttribute(String name) {
    return null;
  }

  @Override
  public String[] getAttributeNames() {
    return null;
  }

  @Override
  public void release() {
  }

  @Override
  public void removeAttribute(String name) {
  }

  @Override
  public void setAttribute(String name, Object value) {
  }

  private class LogMock implements Log {

    @Override
    public void debug(Object message) {
      debug.append(message).append("\n");
    }

    @Override
    public void debug(Object message, Throwable t) {
      debug.append(message).append("\n");
    }

    @Override
    public void error(Object message) {
      error.append(message).append("\n");
    }

    @Override
    public void error(Object message, Throwable t) {
      error.append(message).append("\n");
    }

    @Override
    public void fatal(Object message) {
      fatal.append(message).append("\n");
    }

    @Override
    public void fatal(Object message, Throwable t) {
      fatal.append(message).append("\n");
    }

    @Override
    public void info(Object message) {
      info.append(message).append("\n");
    }

    @Override
    public void info(Object message, Throwable t) {
      info.append(message).append("\n");
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
      trace.append(message).append("\n");
    }

    @Override
    public void trace(Object message, Throwable t) {
      trace.append(message).append("\n");
    }

    @Override
    public void warn(Object message) {
      warning.append(message).append("\n");
    }

    @Override
    public void warn(Object message, Throwable t) {
      warning.append(message).append("\n");
    }
  }
}
