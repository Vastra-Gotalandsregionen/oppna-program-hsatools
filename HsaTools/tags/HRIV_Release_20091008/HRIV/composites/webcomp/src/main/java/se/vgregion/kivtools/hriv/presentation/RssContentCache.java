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

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

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

  private String rssUrl;
  private HttpFetcher httpFetcher;
  private FileUtil fileUtil;

  private File rssContentCacheFolder;

  private final AtomicReference<String> rssContentReference = new AtomicReference<String>();

  public void setRssUrl(String rssUrl) {
    this.rssUrl = rssUrl;
  }

  public void setHttpFetcher(HttpFetcher httpFetcher) {
    this.httpFetcher = httpFetcher;
  }

  public void setFileUtil(FileUtil fileUtil) {
    this.fileUtil = fileUtil;
  }

  public void setRssContentCacheFolder(File rssContentCacheFolder) {
    this.rssContentCacheFolder = rssContentCacheFolder;
  }

  /**
   * Gets the RSS content from the cache.
   * 
   * @return The RSS content.
   */
  public String getRssContent() {
    return rssContentReference.get();
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
    String content = this.httpFetcher.fetchUrl(rssUrl);
    if (!StringUtil.isEmpty(content)) {
      rssContentReference.set(content);
    } else if (StringUtil.isEmpty(rssContentReference.get())) {
      loadRssContentFromFile();
    }
  }

  private void loadRssContentFromFile() {
    File cacheFile = getRssCacheFile();
    String content = "";
    try {
      content = fileUtil.readFile(cacheFile);
    } catch (FileUtilException e) {
      LOG.error("Could not read RSS Content Cache from file", e);
    }
    rssContentReference.set(content);
  }

  private void saveRssContentToFile() {
    File cacheFile = getRssCacheFile();
    try {
      fileUtil.writeFile(cacheFile, rssContentReference.get());
    } catch (FileUtilException e) {
      LOG.error("Could not write RSS Content Cache to file", e);
    }
  }

  private File getRssCacheFile() {
    if (rssContentCacheFolder == null) {
      rssContentCacheFolder = getHrivSettingsFolder();
    }

    File cacheFile = new File(rssContentCacheFolder, "rssContentCache");
    return cacheFile;
  }

  private File getHrivSettingsFolder() {
    String homeFolder = System.getProperty("user.home");
    File hrivSettingsFolder = new File(homeFolder, ".hriv");
    fileUtil.createDirectoryIfNoExist(hrivSettingsFolder);

    return hrivSettingsFolder;
  }
}
