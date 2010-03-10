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
import java.util.List;

import se.vgregion.kivtools.util.Arguments;

/**
 * A cache for sitemap entries.
 * 
 * @author Joakim Olsson
 */
public class SitemapCache {
  private final List<SitemapEntry> entries = new ArrayList<SitemapEntry>();

  public List<SitemapEntry> getEntries() {
    return Collections.unmodifiableList(entries);
  }

  /**
   * Adds a new sitemap entry to the cache.
   * 
   * @param entry The sitemap entry to add to the cache.
   */
  public void add(SitemapEntry entry) {
    Arguments.notNull("entry", entry);

    if (!this.entries.contains(entry)) {
      this.entries.add(entry);
    }
  }
}
