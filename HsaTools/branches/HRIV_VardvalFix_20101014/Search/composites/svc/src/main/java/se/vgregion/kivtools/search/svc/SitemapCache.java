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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for sitemap entries.
 * 
 * @author Joakim Olsson
 */
public class SitemapCache {
  /**
   * Enumeration of the different entry types that can be stored in the sitemap cache.
   */
  public enum EntryType {
    PERSON, UNIT;
  }

  private final Map<EntryType, List<SitemapEntry>> entryMap = new HashMap<SitemapCache.EntryType, List<SitemapEntry>>();

  /**
   * Retrieves the specified type of entries or all entries if no entry type is provided.
   * 
   * @param entryType The type of entries to retrieve from the cache.
   * @return the specified type of entries or all entries if no entry type was provided.
   */
  public List<SitemapEntry> getEntries(EntryType entryType) {
    List<SitemapEntry> entries;
    if (entryType != null) {
      entries = getEntryList(entryType);
    } else {
      entries = new ArrayList<SitemapEntry>();
      entries.addAll(getEntryList(EntryType.UNIT));
      entries.addAll(getEntryList(EntryType.PERSON));
    }
    return Collections.unmodifiableList(entries);
  }

  /**
   * Adds a new sitemap entry to the cache.
   * 
   * @param entry The sitemap entry to add to the cache.
   * @param entryType The type of entry to add to the cache.
   */
  public void add(SitemapEntry entry, EntryType entryType) {
    Arguments.notNull("entry", entry);
    Arguments.notNull("entryType", entryType);

    List<SitemapEntry> entries = getEntryList(entryType);

    if (!entries.contains(entry)) {
      entries.add(entry);
    }
  }

  private List<SitemapEntry> getEntryList(EntryType entryType) {
    List<SitemapEntry> entries = entryMap.get(entryType);
    if (entries == null) {
      entries = new ArrayList<SitemapEntry>();
      entryMap.put(entryType, entries);
    }
    return entries;
  }
}
