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

package se.vgregion.kivtools.mocks;

import static org.junit.Assert.*;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

/**
 * Helper-class for testing basic properties of POJO's.
 * 
 * @author Joakim Olsson
 */
public class PojoTester {

  /**
   * Tests a basic property using ReflectionUtil to set and get the provided values.
   * 
   * @param <T> The type of the property to test.
   * @param object The object to perform the test on.
   * @param propertyName The name of the property to test.
   * @param type The type of the property to test.
   * @param defaultValue The default value of the property.
   * @param testValue1 The first test value.
   * @param testValue2 The second test value.
   */
  public static <T> void testProperty(Object object, String propertyName, Class<T> type, T defaultValue, T testValue1, T testValue2) {
    assertEquals("Unexpected default value for " + propertyName, defaultValue, ReflectionUtil.getProperty(object, propertyName));
    ReflectionUtil.setProperty(object, propertyName, type, testValue1);
    assertEquals("Unexpected testvalue1 for " + propertyName, testValue1, ReflectionUtil.getProperty(object, propertyName));
    ReflectionUtil.setProperty(object, propertyName, type, testValue2);
    assertEquals("Unexpected testvalue2 for " + propertyName, testValue2, ReflectionUtil.getProperty(object, propertyName));
  }
}
