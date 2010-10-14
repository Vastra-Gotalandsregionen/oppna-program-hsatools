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
 */
package se.vgregion.kivtools.search.svc.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.ldap.core.DirContextOperations;

import se.vgregion.kivtools.util.Arguments;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Helper-class for working with DirContextOperations.
 */
public class DirContextOperationsHelper {
  private static final String NEW_LINE_DELIMITER = "$";

  private final DirContextOperations dirContext;

  /**
   * Constructs a new helper-instance.
   * 
   * @param dirContext The DirContextOperations to work with.
   */
  public DirContextOperationsHelper(DirContextOperations dirContext) {
    Arguments.notNull("dirContext", dirContext);
    this.dirContext = dirContext;
  }

  /**
   * Retrieves the String value of an attribute.
   * 
   * @param attributeName the name of the attribute to retrieve.
   * @return the value of the attribute or an empty String if no attribute value could be found.
   */
  public String getString(final String attributeName) {
    return emptyIfNull(dirContext.getStringAttribute(attributeName)).trim();
  }

  /**
   * Retrieves the value of an attribute as a List of Strings. Also considers $ as a line terminator.
   * 
   * @param attributeName the name of the attribute to retrieve.
   * @return the value of the attribute or an empty List if no attribute value could be found.
   */
  public List<String> getStrings(final String attributeName) {
    String[] values = dirContext.getStringAttributes(attributeName);
    List<String> result;
    if (values != null) {
      result = convertToList(values);
    } else {
      result = Collections.emptyList();
    }
    return result;
  }

  private List<String> convertToList(String[] values) {
    List<String> result = new ArrayList<String>();

    for (final String value : values) {
      String trimmedValue = value.trim();

      List<String> items = splitOnDelimiter(trimmedValue, NEW_LINE_DELIMITER);
      result.addAll(items);
    }

    return result;
  }

  private List<String> splitOnDelimiter(String value, String delimiter) {
    List<String> result = new ArrayList<String>();
    String workValue = value;
    while (!StringUtil.isEmpty(workValue)) {
      int index = workValue.indexOf(delimiter);

      String itemValue;
      if (index != -1) {
        itemValue = workValue.substring(0, index).trim();
        workValue = workValue.substring(index + 1).trim();
      } else {
        itemValue = workValue.trim();
        workValue = "";
      }

      if (!StringUtil.isEmpty(itemValue)) {
        result.add(itemValue);
      }
    }

    return result;
  }

  private String emptyIfNull(String value) {
    String result = value;
    if (result == null) {
      result = "";
    }
    return result;
  }

  /**
   * Checks if the provided attribute has a value in the context.
   * 
   * @param attributeName the name of the attribute to check for existence.
   * @return true if the attribute has a value otherwise false.
   */
  public boolean hasAttribute(String attributeName) {
    return dirContext.getAttributes().get(attributeName) != null;
  }

  /**
   * Retrieves the distinguished name of the context.
   * 
   * @return the distinguished name of the context.
   */
  public String getDnString() {
    return dirContext.getDn().toString();
  }
}
