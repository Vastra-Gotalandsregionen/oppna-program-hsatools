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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.vgregion.kivtools.util.Arguments;

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
   * @param parameters The types of any parameters for the method.
   * @return True if the class of the provided object has a method with the provided methodName, otherwise false.
   */
  public static boolean hasMethod(Object object, String methodName, Class<?>... parameters) {
    boolean result = false;

    if (object != null) {
      try {
        object.getClass().getMethod(methodName, parameters);
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
    return callMethod(object, methodName, null, null);
  }

  /**
   * Calls the method with the provided methodName on the provided object and returns the result.
   * 
   * @param object The object to call the method on.
   * @param methodName The name of the method to call.
   * @param types The types of the arguments to the method.
   * @param values The value of the arguments to the method.
   * @return Any result that the method returns.
   */
  public static Object callMethod(Object object, String methodName, Class<?>[] types, Object[] values) {
    Object result = null;

    if (object != null && hasMethod(object, methodName, types)) {
      try {
        Method method = object.getClass().getMethod(methodName, types);
        result = method.invoke(object, values);
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

  /**
   * Gets the value of the property with the provided name from the provided object by using the get/is/has-method for the property.
   * 
   * @param object The object to get the property from.
   * @param propertyName The name of the property to get.
   * @param <T> The expected type of the property.
   * @return The value of the named property or null if no property with that name was found.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getProperty(Object object, String propertyName) {
    Arguments.notNull("object", object);
    Arguments.notNull("propertyName", propertyName);

    String name = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

    T result = null;

    if (hasMethod(object, "get" + name)) {
      result = (T) callMethod(object, "get" + name);
    } else if (hasMethod(object, "is" + name)) {
      result = (T) callMethod(object, "is" + name);
    } else if (hasMethod(object, "has" + name)) {
      result = (T) callMethod(object, "has" + name);
    }

    return result;
  }

  /**
   * Sets the value of the property with the provided name from the provided object by using the set-method for the property.
   * 
   * @param object The object to get the property from.
   * @param propertyName The name of the property to set.
   * @param type The type of the property.
   * @param value The value to set the property to.
   * @param <T> The expected type of the property.
   */
  public static <T> void setProperty(Object object, String propertyName, Class<T> type, T value) {
    Arguments.notNull("object", object);
    Arguments.notNull("propertyName", propertyName);

    String name = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

    if (hasMethod(object, "set" + name, type)) {
      callMethod(object, "set" + name, new Class<?>[] { type }, new Object[] { value });
    }
  }

  /**
   * Sets the value of the property with the provided name from the provided object by setting the value directly on the field and thereby bypassing any set-methods.
   * 
   * @param object The object to get the property from.
   * @param fieldName The name of the field to set.
   * @param value The value to set the property to.
   * @param <T> The expected type of the property.
   */
  public static <T> void setField(Object object, String fieldName, T value) {
    Arguments.notNull("object", object);
    Arguments.notNull("fieldName", fieldName);

    try {
      Field field = object.getClass().getDeclaredField(fieldName);
      if (field != null) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(accessible);
      }
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
