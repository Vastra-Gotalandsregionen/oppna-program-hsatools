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
package se.vgregion.kivtools.search.svc;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for titles.
 * 
 * @author Joakim Olsson
 */
public class TitleCache {
  private final List<String> titles = new ArrayList<String>();

  /**
   * Retrieves a list of all matching titles from the cache.
   * 
   * @param title The title to match against the cache.
   * @return A list of all matching titles in the cache or an empty list if no matches were found.
   */
  public List<String> getMatchingTitles(String title) {
    Arguments.notNull("title", title);
    String searchString = title.trim().toLowerCase();
    List<String> result = new ArrayList<String>();

    for (String currentTitle : titles) {
      if (currentTitle.toLowerCase().contains(searchString)) {
        result.add(currentTitle);
      }
    }

    Collections.sort(result, Collator.getInstance());

    return result;
  }

  /**
   * Adds a new title to the cache.
   * 
   * @param title The title to add to the cache.
   */
  public void add(String title) {
    Arguments.notNull("title", title);

    String trimmedTitle = title.trim();
    if (!this.titles.contains(trimmedTitle)) {
      this.titles.add(trimmedTitle);
    }
  }
}
