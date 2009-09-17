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

/**
 * Helper-class for handling presentation logic etc.
 * 
 * @author Joakim Olsson
 */
public class PresentationHelper {

  /**
   * Converts newlines to &lt;br/&gt;-tags.
   * 
   * @param input The string to replace newlines in.
   * @return The input-string with all newlines replaced with &lt;br/&gt;-tags.
   * @throws IllegalArgumentException if input is null.
   */
  public String replaceNewlineWithBr(String input) {
    if (input == null) {
      throw new IllegalArgumentException("Parameter input may not be null");
    }
    return input.replaceAll("\\n", "<br/>");
  }

  /**
   * Returns the provided text up to a maximum of the provided length. If the text is longer than the provided length, a substring of the text is returned with an ellipsis appended.
   * 
   * @param text The text to work with.
   * @param length The maximum length to return.
   * @return The provided text if its length is less than the provided length or a substring of the text with an ellipsis appended.
   * @throws IllegalArgumentException if text is null or length is less than 1.
   */
  public String getTextWithEllipsis(String text, int length) {
    if (text == null) {
      throw new IllegalArgumentException("Parameter text may not be null");
    }
    if (length < 1) {
      throw new IllegalArgumentException("Parameter length must have a value greater than 1");
    }

    String result = text;
    if (text.length() > length) {
      result = text.substring(0, length) + "...";
    }
    return result;
  }
}
