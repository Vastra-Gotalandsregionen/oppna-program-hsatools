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

package se.vgregion.kivtools.hriv.presentation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.file.FileUtil;
import se.vgregion.kivtools.util.file.FileUtilException;
import se.vgregion.kivtools.util.http.HttpFetcher;

/**
 * Cache-implementation for RSS content.
 * 
 * @author David Bennehult & Joakim Olsson
 */
public class RssContentCache {
  private static final Log LOG = LogFactory.getLog(RssContentCache.class);

  private Map<String, String> nameToUrlMap = new HashMap<String, String>();
  private HttpFetcher httpFetcher;
  private FileUtil fileUtil;

  private String userSpecifiedCacheFolder;

  private File rssContentCacheFolder;

  private final Map<String, String> rssContentReference = new ConcurrentHashMap<String, String>();

  /**
   * Adds mappings from a name to an URL to be cached.
   * 
   * @param nameToUrlMap The map of names and corresponding URL's.
   */
  public void setNameToUrlMap(Map<String, String> nameToUrlMap) {
    this.nameToUrlMap.putAll(nameToUrlMap);
  }

  public void setHttpFetcher(HttpFetcher httpFetcher) {
    this.httpFetcher = httpFetcher;
  }

  public void setFileUtil(FileUtil fileUtil) {
    this.fileUtil = fileUtil;
  }

  public void setUserSpecifiedCacheFolder(String userSpecifiedCacheFolder) {
    this.userSpecifiedCacheFolder = userSpecifiedCacheFolder;
  }

  /**
   * Gets the RSS content from the cache.
   * 
   * @param name Name of the content to get.
   * @return The RSS content.
   */
  public String getRssContent(String name) {
    return rssContentReference.get(name);
  }

  /**
   * Reloads the cache and saves the content to a file in the file system for later use.
   */
  public void reloadRssCache() {
    loadRssContent();
    saveRssContentToFile();
  }

  /**
   * Loads the RSS content from the configured URL and stores it in memory.
   */
  private void loadRssContent() {
    for (String name : nameToUrlMap.keySet()) {
      String content = this.httpFetcher.fetchUrl(nameToUrlMap.get(name));
      if (!StringUtil.isEmpty(content)) {
        rssContentReference.put(name, content);
      } else if (StringUtil.isEmpty(rssContentReference.get(name))) {
        loadRssContentFromFile();
      }
    }
  }

  private void loadRssContentFromFile() {
    for (String name : nameToUrlMap.keySet()) {
      File cacheFile = getRssCacheFile(name);
      String content = "";
      try {
        content = fileUtil.readFile(cacheFile);
      } catch (FileUtilException e) {
        LOG.error("Could not read RSS Content Cache from file", e);
      }
      rssContentReference.put(name, content);
    }
  }

  private void saveRssContentToFile() {
    for (String name : nameToUrlMap.keySet()) {
      File cacheFile = getRssCacheFile(name);
      try {
        fileUtil.writeFile(cacheFile, rssContentReference.get(name));
      } catch (FileUtilException e) {
        LOG.error("Could not write RSS Content Cache to file", e);
      }
    }
  }

  private File getRssCacheFile(String name) {
    if (userSpecifiedCacheFolder == null) {
      rssContentCacheFolder = new File(getHrivSettingsFolder(), "rssContentCache");
    } else {
      rssContentCacheFolder = new File(getHrivSettingsFolder(), userSpecifiedCacheFolder);
    }

    fileUtil.createDirectoryIfNoExist(rssContentCacheFolder);

    File cacheFile = new File(rssContentCacheFolder, name);
    return cacheFile;
  }

  private File getHrivSettingsFolder() {
    String homeFolder = System.getProperty("user.home");
    File hrivSettingsFolder = new File(homeFolder, ".hriv");
    fileUtil.createDirectoryIfNoExist(hrivSettingsFolder);

    return hrivSettingsFolder;
  }
}
