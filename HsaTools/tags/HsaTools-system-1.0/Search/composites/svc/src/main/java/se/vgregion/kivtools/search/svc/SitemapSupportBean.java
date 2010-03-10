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

  /**
   * Constructs a new {@link SitemapSupportBean}.
   * 
   * @param sitemapCacheService The {@link SitemapCacheServiceImpl} to get the entries for the sitemap from.
   */
  public SitemapSupportBean(SitemapCacheServiceImpl sitemapCacheService) {
    this.sitemapCacheService = sitemapCacheService;
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
    StringBuilder output = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:hsa=\"http://www.vgregion.se/schemas/hsa_schema\">\n");

    for (SitemapEntry entry : entries) {
      output.append("<url>\n");
      output.append("<loc>").append(entry.getLocation()).append("</loc>\n");
      output.append("<lastmod>").append(entry.getLastModified()).append("</lastmod>\n");
      output.append("<changefreq>").append(entry.getChangeFrequency()).append("</changefreq>\n");
      output.append("<priority>0.5</priority>\n");
      for (SitemapEntry.ExtraInformation extraInformation : entry) {
        output.append("<hsa:").append(extraInformation.getName()).append(">").append(extraInformation.getValue()).append("</hsa:").append(extraInformation.getName()).append(">\n");
      }
      output.append("</url>\n");
    }

    output.append("</urlset>");

    return output.toString();
  }
}
