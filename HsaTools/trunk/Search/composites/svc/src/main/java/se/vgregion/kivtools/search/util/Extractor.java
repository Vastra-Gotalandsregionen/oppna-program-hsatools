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
import org.apache.commons.logging.LogFactory;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
public class Extractor {

  private static final String CLASS_NAME = Extractor.class.getName();

  private static Log logger = LogFactory.getLog(CLASS_NAME);

  public static String getSystemProperty(String systemPropertyName, boolean isMandatory) throws Exception {
    String result = null;
    try {
      result = System.getProperty(systemPropertyName);
    } catch (Exception e) {
      String msg = CLASS_NAME + "::getSystemProperty(systemPropertyName(" + systemPropertyName + ") Retrieving property.";
      logger.error(msg, e);
      throw new Exception(msg);
    }
    if (Evaluator.isEmpty(result) && isMandatory) {
      String msg = CLASS_NAME + "::getSystemProperty(systemPropertyName(" + systemPropertyName + ") is not found.";
      logger.error(msg);
      throw new Exception(msg);
    }
    return result.trim();
  }

  public static long getNowAsLong() {
    return System.currentTimeMillis();
  }

  public static Long getNowAsLongObject() {
    return Long.valueOf(System.currentTimeMillis());
  }
}
