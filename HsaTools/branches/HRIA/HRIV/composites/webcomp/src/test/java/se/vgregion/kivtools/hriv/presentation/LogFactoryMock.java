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
package se.vgregion.kivtools.hriv.presentation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

public class LogFactoryMock extends LogFactory {
  private Log log;

  public void setLog(Log log) {
    this.log = log;
  }

  @Override
  public Object getAttribute(String name) {
    return null;
  }

  @Override
  public String[] getAttributeNames() {
    return null;
  }

  @Override
  public Log getInstance(Class clazz) throws LogConfigurationException {
    return log;
  }

  @Override
  public Log getInstance(String name) throws LogConfigurationException {
    return log;
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
}
