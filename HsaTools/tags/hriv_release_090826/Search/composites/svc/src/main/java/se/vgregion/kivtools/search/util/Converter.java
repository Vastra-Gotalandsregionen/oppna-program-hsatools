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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for converting.
 * 
 * @author Jonas Liljenfeldt, Know IT
 * 
 */
public class Converter {

  /**
   * Return array list of integers from array list of strings.
   * 
   * @param strings The list of strings to convert to integers.
   * @return A list of all integers found in the provided list of strings.
   */
  public static List<Integer> getIntegerArrayList(List<String> strings) {
    ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
    if (strings != null) {
      for (String s : strings) {
        if (Evaluator.isInteger(s)) {
          integerArrayList.add(Integer.parseInt(s));
        }
      }
    }
    return integerArrayList;
  }
}
