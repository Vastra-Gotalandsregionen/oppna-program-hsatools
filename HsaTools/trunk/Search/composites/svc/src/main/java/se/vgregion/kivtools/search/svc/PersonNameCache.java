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

package se.vgregion.kivtools.search.svc;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for person names which keeps track of combinations of given name and surname.
 * 
 * @author Joakim Olsson
 */
public class PersonNameCache {
  private final Map<String, List<String>> givenNameMap = new HashMap<String, List<String>>();
  private final Map<String, List<String>> surnameMap = new HashMap<String, List<String>>();

  /**
   * Retrieves a list of all given names matching the provided given name and surname.
   * 
   * @param givenName The given name to match against the cache.
   * @param surname The surname to match against the cache.
   * @return A list of all given names matching the provided given name and surname. Returns an empty list if no matches are found.
   */
  public List<String> getMatchingGivenNames(String givenName, String surname) {
    Arguments.notNull("givenName", givenName);
    Arguments.notNull("surname", surname);

    List<String> result = getMatchingNames(surname.trim().toLowerCase(), givenName.trim().toLowerCase(), surnameMap);

    return result;
  }

  /**
   * Retrieves a list of all surnames matching the provided given name and surname.
   * 
   * @param givenName The given name to match against the cache.
   * @param surname The surname to match against the cache.
   * @return A list of all surnames matching the provided given name and surname. Returns an empty list if no matches are found.
   */
  public List<String> getMatchingSurnames(String givenName, String surname) {
    Arguments.notNull("givenName", givenName);
    Arguments.notNull("surname", surname);

    List<String> result = getMatchingNames(givenName.trim().toLowerCase(), surname.trim().toLowerCase(), givenNameMap);
    return result;
  }

  /**
   * Adds a given name/surname combination to the cache.
   * 
   * @param givenName The given name to add.
   * @param surname The surname to add.
   */
  public void add(String givenName, String surname) {
    Arguments.notNull("givenName", givenName);
    Arguments.notNull("surname", surname);

    addNameToList(givenName.trim(), surname.trim(), givenNameMap);
    addNameToList(surname.trim(), givenName.trim(), surnameMap);
  }

  /**
   * Helper-method for adding a name to the correct list of the map. If no list exists for the provided key, a new list is created and added to the map.
   * 
   * @param key The key to use to find the correct list.
   * @param value The value to put in the list.
   * @param map The map to find the correct list in.
   */
  private void addNameToList(String key, String value, Map<String, List<String>> map) {
    List<String> list = map.get(key);
    if (list == null) {
      list = new ArrayList<String>();
      map.put(key, list);
    }
    if (!list.contains(value)) {
      list.add(value);
    }
  }

  /**
   * Helper-method for retrieving all matching records in the provided map using the provided key and value.
   * 
   * @param key The key used to find the lists to search through.
   * @param value The value to lookup in the matching lists.
   * @param map The map containing the lists.
   * @return A list containing the matching strings. Returns an empty list if no matches are found.
   */
  private List<String> getMatchingNames(String key, String value, Map<String, List<String>> map) {
    List<String> matchingNames = new ArrayList<String>();

    List<String> matchingKeys = new ArrayList<String>();
    for (String currentKey : map.keySet()) {
      if (currentKey.toLowerCase().contains(key)) {
        matchingKeys.add(currentKey);
      }
    }

    for (String matchingKey : matchingKeys) {
      List<String> matchingValues = getMatchingValues(map.get(matchingKey), value);
      for (String matchingValue : matchingValues) {
        if (!matchingNames.contains(matchingValue)) {
          matchingNames.add(matchingValue);
        }
      }
    }

    Collections.sort(matchingNames, Collator.getInstance());

    return matchingNames;
  }

  /**
   * Helper-method for retrieving all matching values from a list of strings.
   * 
   * @param list The list to retrieve matching values from.
   * @param value The value to match.
   * @return A list of matching strings or an empty list if no matches were found.
   */
  private List<String> getMatchingValues(List<String> list, String value) {
    List<String> matchingValues = new ArrayList<String>();

    for (String string : list) {
      if (string.toLowerCase().contains(value)) {
        matchingValues.add(string);
      }
    }

    return matchingValues;
  }
}
