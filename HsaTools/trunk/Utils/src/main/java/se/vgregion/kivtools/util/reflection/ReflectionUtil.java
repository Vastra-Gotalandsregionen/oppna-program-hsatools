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
package se.vgregion.kivtools.util.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility class for working with reflection.
 * 
 * @author Joakim Olsson
 */
public final class ReflectionUtil {

  /**
   * Checks if the class of the provided object has a method with the provided methodName.
   * 
   * @param object The object to check.
   * @param methodName The name of the method to look for.
   * @return True if the class of the provided object has a method with the provided methodName, otherwise false.
   */
  public static boolean hasMethod(Object object, String methodName) {
    boolean result = false;

    if (object != null) {
      try {
        object.getClass().getMethod(methodName);
        result = true;
      } catch (SecurityException e) {
        // Just treat a SecurityException as if the method does not exist.
        result = false;
      } catch (NoSuchMethodException e) {
        // The method does not exist.
        result = false;
      }
    }

    return result;
  }

  /**
   * Calls the method with the provided methodName on the provided object and returns the result.
   * 
   * @param object The object to call the method on.
   * @param methodName The name of the method to call.
   * @return Any result that the method returns.
   */
  public static Object callMethod(Object object, String methodName) {
    Object result = null;

    if (object != null && hasMethod(object, methodName)) {
      try {
        Method method = object.getClass().getMethod(methodName);
        result = method.invoke(object);
      } catch (SecurityException e) {
        // Just treat a SecurityException as if the method does not exist.
        result = null;
      } catch (NoSuchMethodException e) {
        // The method does not exist.
        result = null;
      } catch (IllegalArgumentException e) {
        // Illegal arguments provided.
        result = null;
      } catch (IllegalAccessException e) {
        // Illegal access. Are we trying to call a private method?
        result = null;
      } catch (InvocationTargetException e) {
        // The invoked method throwed an exception.
        result = null;
      }
    }

    return result;
  }
}
