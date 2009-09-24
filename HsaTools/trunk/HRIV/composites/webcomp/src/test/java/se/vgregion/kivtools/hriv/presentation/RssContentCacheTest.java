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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.mocks.file.FileUtilMock;
import se.vgregion.kivtools.util.file.FileUtilException;

public class RssContentCacheTest {
  private RssContentCache rssContentCache;
  private HttpFetcherMock httpFetcherMock;
  private FileUtilMock fileUtilMock;
  private File defaultHrivSettingsFolder;
  private File defaultCacheFile;
  private File userSpecifiedHrivSettingsFolder;
  private File userSpecifiedCacheFile;
  private static LogFactoryMock factory;

  @BeforeClass
  public static void beforeClass() {
    factory = LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() {
    defaultHrivSettingsFolder = new File(System.getProperty("user.home") + "/.hriv");
    defaultCacheFile = new File(defaultHrivSettingsFolder, "rssContentCache");
    userSpecifiedHrivSettingsFolder = new File("target/testCacheFolder");
    userSpecifiedCacheFile = new File(userSpecifiedHrivSettingsFolder, "rssContentCache");

    rssContentCache = new RssContentCache();
    httpFetcherMock = new HttpFetcherMock();
    rssContentCache.setHttpFetcher(httpFetcherMock);
    fileUtilMock = new FileUtilMock();
    rssContentCache.setFileUtil(fileUtilMock);
    rssContentCache.setRssUrl("http://testurl");
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Test
  public void testInstantiation() {
    RssContentCache rssContentCache = new RssContentCache();
    assertNotNull(rssContentCache);
  }

  @Test
  public void testGetRssContent() {
    httpFetcherMock.setContent("abc");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent();
    assertNotNull(content);
    assertEquals("abc", content);
    httpFetcherMock.assertLastUrlFetched("http://testurl");
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileWrite(defaultCacheFile);
    fileUtilMock.assertDirCreated(defaultHrivSettingsFolder);
  }

  @Test
  public void testGetRssContentSpecifiedDirectory() {
    httpFetcherMock.setContent("abc");
    rssContentCache.setRssContentCacheFolder(userSpecifiedHrivSettingsFolder);
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent();
    assertNotNull(content);
    assertEquals("abc", content);
    httpFetcherMock.assertLastUrlFetched("http://testurl");
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileWrite(userSpecifiedCacheFile);
  }

  @Test
  public void testGetRssContentNoDataToLoad() {
    httpFetcherMock.setContent("abc");
    rssContentCache.reloadRssCache();
    httpFetcherMock.setContent("");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent();
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
  }

  @Test
  public void testNoExistingCachedData() {
    fileUtilMock.setContent("abc");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent();
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileRead(defaultCacheFile);
    fileUtilMock.assertDirCreated(defaultHrivSettingsFolder);
  }

  @Test
  public void testNoExistingCachedDataSpecifiedDirectory() {
    fileUtilMock.setContent("abc");
    rssContentCache.setRssContentCacheFolder(userSpecifiedHrivSettingsFolder);
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent();
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileRead(userSpecifiedCacheFile);
  }

  @Test
  public void testExceptionHandling() {
    fileUtilMock.setExceptionToThrow(new FileUtilException());
    rssContentCache.reloadRssCache();
    assertEquals("Could not read RSS Content Cache from file\nCould not write RSS Content Cache to file\n", factory.getError(true));
  }
}
