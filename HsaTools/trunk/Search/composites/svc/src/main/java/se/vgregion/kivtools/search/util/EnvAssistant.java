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
package se.vgregion.kivtools.search.util;

import java.util.Enumeration;
import java.util.Properties;

public class EnvAssistant {

  /**
   * This class is just used for the method createBean() to return a sorted list
   */
  public class SortableItem implements Comparable {

    private String value;
    private String id;

    SortableItem(String id, String value) {
      this.value = value; // Label
      this.id = id;
    }

    public int compareTo(Object o) {
      return this.value.compareTo(((SortableItem) o).value);
    }
  }

  public static void printEnvironment(Class clazz) {
    System.out.println("******** Enviroment in class: " + clazz.getName() + "**********");
    Properties prop = System.getProperties();
    Enumeration propList = prop.propertyNames();
    String propertyName, propertyValue;
    while (propList.hasMoreElements()) {
      propertyName = (String) propList.nextElement();
      propertyValue = prop.getProperty(propertyName);
      System.out.println("Property=" + propertyName + " ,\t Value=" + propertyValue);
    }
  }

  /*
   * Determine if the running application server is IBM based
   */
  public static boolean isRunningOnIBM() {
    String temp = System.getProperty("java.vm.name");
    if (!Evaluator.isEmpty(temp)) {
      temp = temp.toUpperCase();
      return temp.contains("IBM");
    } else {
      return false;
    }
  }
}
