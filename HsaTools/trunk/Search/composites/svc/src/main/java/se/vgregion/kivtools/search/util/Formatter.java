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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 */
public class Formatter {
  private static final String CLASS_NAME = Formatter.class.getName();
  private static Log logger = LogFactory.getLog(CLASS_NAME);

  /**
   * Replace all occurances of stringToReplace in string originalString with replacingString.
   * 
   * E.g. originalString = "hello'all'" another example: = "2005 Gold 2004 Gold" stringToReplace = "\'" = "Gold" replacingString = "''" = "Guld"
   * 
   * returns "hello''all''" = "2005 Guld 2004 Guld"
   */
  public static String replaceStringInString(String originalString, String stringToReplace, String replacingString) throws Exception {
    String methodName = "replaceStringInString(originalString=" + originalString + ", stringToReplace=" + stringToReplace + ", replacingString=" + replacingString + ")";
    String finalString = "";
    if (originalString == null) {
      return null;
    }
    int length = originalString.length();
    int beginIndex = 0;
    int endIndex = originalString.indexOf(stringToReplace, beginIndex);
    String leftPart = "";
    int count = 0;

    if (endIndex < 0) {
      return originalString;
    }

    while (beginIndex < length && endIndex >= 0) {

      endIndex = originalString.indexOf(stringToReplace, beginIndex);
      if (endIndex < 0) {
        leftPart = originalString.substring(beginIndex, length);
        finalString = finalString + leftPart;
      } else {
        // hello
        leftPart = originalString.substring(beginIndex, endIndex);
        // hello''
        finalString = finalString + leftPart + replacingString;
      }

      if (endIndex + 1 >= length) {
        return finalString;
      } else {
        beginIndex = endIndex + stringToReplace.length();
      }

      count++;
      if (count > 10000) {
        // something is wrong
        String s = "className=" + CLASS_NAME + methodName + ", caught in an endless loop";
        logger.error(s);
        throw new Exception(s);
      }
    }
    return finalString;
  }

  /**
   * This method chops up a delimeitered string and puts each piece in the resulting ArrayList Empty strings are removed.
   * 
   * @param list This list is allocated outside the call and filled with data E.g. The list get�s two new entries list[0]=Hello list[1]=Guys
   * @param inputString E.g "Hello$Guys$ $"
   * @param delimiter E.g."$"
   * @return The list of strings that was passed in with the parts of the chopped up inputstring added to it.
   */
  public static List<String> chopUpStringToList(List<String> list, String inputString, String delimiter) {
    if (!Evaluator.isEmpty(inputString) && !Evaluator.isEmpty(delimiter)) {
      int length = inputString.length();
      int beginIndex = 0;
      int endIndex = inputString.indexOf(delimiter, beginIndex);
      String leftPart = "";
      int delimiterLength = delimiter.length();

      if (endIndex >= 0) {
        while (beginIndex < length && endIndex >= 0) {
          endIndex = inputString.indexOf(delimiter, beginIndex);
          if (endIndex < 0) {
            leftPart = inputString.substring(beginIndex, length);
            if (!Evaluator.isEmpty(leftPart)) {
              list.add(leftPart);
            }
          } else {
            // hello
            leftPart = inputString.substring(beginIndex, endIndex);
            if (!Evaluator.isEmpty(leftPart)) {
              list.add(leftPart);
            }
          }

          beginIndex = endIndex + delimiterLength;
        }
      } else {
        // there was no delimiter found
        list.add(inputString);
      }
    }
    return list;
  }

  /**
   * Concatenates an arbitrary amount of strings into a nicely formatted concatenated string. Skips empty stringPart elements.
   * 
   * @param stringParts The strings to concatenate.
   * @return The concatenated strings.
   */
  public static String concatenate(String... stringParts) {
    StringBuilder concatenatedString = new StringBuilder();
    if (stringParts != null) {

      List<String> stringPartsList = new ArrayList<String>(Arrays.asList(stringParts));
      concatenatedString.append(Formatter.concatenate(stringPartsList));
    }
    return concatenatedString.toString();
  }

  /**
   * Concatenates an arbitrary amount of strings into a nicely formatted concatenated string. Skips empty stringPart elements.
   * 
   * @param stringParts The list of strings to concatenate.
   * @return The concatenated strings.
   */
  public static String concatenate(List<String> stringParts) {
    StringBuilder concatenatedString = new StringBuilder();
    if (stringParts != null) {

      List<String> stringPartsList = new ArrayList<String>(stringParts);
      for (Iterator<String> iterator = stringPartsList.iterator(); iterator.hasNext();) {
        String stringPart = iterator.next();
        if ("".equals(stringPart)) {
          iterator.remove();
        }
      }

      for (int i = 0; i < stringPartsList.size(); i++) {
        concatenatedString.append(stringPartsList.get(i).trim());
        if (i < stringPartsList.size() - 1) {
          concatenatedString.append(", ");
        }
      }
    }
    return concatenatedString.toString();
  }
}
