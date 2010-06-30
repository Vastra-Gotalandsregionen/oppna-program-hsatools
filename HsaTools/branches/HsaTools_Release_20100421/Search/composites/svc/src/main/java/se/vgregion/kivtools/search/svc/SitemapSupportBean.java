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

import java.util.List;

/**
 * Supporting bean for sitemap-servlets.
 */
public class SitemapSupportBean {
  private final SitemapCacheServiceImpl sitemapCacheService;
  private final SitemapGenerator sitemapGenerator;

  /**
   * Constructs a new {@link SitemapSupportBean}.
   * 
   * @param sitemapCacheService The {@link SitemapCacheServiceImpl} to get the entries for the sitemap from.
   * @param sitemapGenerator The SitemapGenerator implementation to use to generate the sitemap XML.
   */
  public SitemapSupportBean(SitemapCacheServiceImpl sitemapCacheService, SitemapGenerator sitemapGenerator) {
    this.sitemapCacheService = sitemapCacheService;
    this.sitemapGenerator = sitemapGenerator;
  }

  /**
   * Retrieves the content for the sitemap based on the units from the cache.
   * 
   * @return The content for the sitemap.
   */
  public String getSitemapContent() {
    List<SitemapEntry> entries = sitemapCacheService.getCache().getEntries();
    // Check if list of entries is populated, otherwise we fill it up!
    if (entries.size() < 1) {
      sitemapCacheService.reloadCache();
      entries = sitemapCacheService.getCache().getEntries();
    }

    return sitemapGenerator.generate(entries);
  }
}
