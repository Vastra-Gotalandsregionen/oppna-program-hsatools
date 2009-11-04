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
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.mocks.file.FileUtilMock;
import se.vgregion.kivtools.util.file.FileUtilException;

public class RssContentCacheTest {
  private static final String TEST = "test";
  private RssContentCache rssContentCache;
  private HttpFetcherMock httpFetcherMock;
  private FileUtilMock fileUtilMock;
  private File defaultHrivSettingsFolder;
  private File defaultCacheFolder;
  private File userSpecifiedCacheFolder;
  private static LogFactoryMock factory;

  @BeforeClass
  public static void beforeClass() {
    factory = LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() {
    defaultHrivSettingsFolder = new File(System.getProperty("user.home") + "/.hriv");
    defaultCacheFolder = new File(defaultHrivSettingsFolder, "rssContentCache");
    userSpecifiedCacheFolder = new File("target/testCacheFolder/rssContentCache");

    rssContentCache = new RssContentCache();
    httpFetcherMock = new HttpFetcherMock();
    rssContentCache.setHttpFetcher(httpFetcherMock);
    fileUtilMock = new FileUtilMock();
    rssContentCache.setFileUtil(fileUtilMock);
    Map<String, String> nameToUrlMap = new HashMap<String, String>();
    nameToUrlMap.put(TEST, "http://testurl");
    rssContentCache.setNameToUrlMap(nameToUrlMap);
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
    httpFetcherMock.addContent("http://testurl", "abc");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent(TEST);
    assertNotNull(content);
    assertEquals("abc", content);
    httpFetcherMock.assertUrlsFetched("http://testurl");
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileWrite(new File(defaultCacheFolder, TEST));
    fileUtilMock.assertDirCreated(defaultCacheFolder);
  }

  @Test
  public void testGetRssContentSpecifiedDirectory() {
    httpFetcherMock.addContent("http://testurl", "abc");
    rssContentCache.setRssContentCacheFolder(userSpecifiedCacheFolder);
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent(TEST);
    assertNotNull(content);
    assertEquals("abc", content);
    httpFetcherMock.assertUrlsFetched("http://testurl");
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileWrite(new File(userSpecifiedCacheFolder, TEST));
  }

  @Test
  public void testGetRssContentNoDataToLoad() {
    httpFetcherMock.addContent("http://testurl", "abc");
    rssContentCache.reloadRssCache();
    httpFetcherMock.addContent("http://testurl", "");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent(TEST);
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
  }

  @Test
  public void testNoExistingCachedData() {
    fileUtilMock.setContent("abc");
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent(TEST);
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileRead(new File(defaultCacheFolder, TEST));
    fileUtilMock.assertDirCreated(defaultCacheFolder);
  }

  @Test
  public void testNoExistingCachedDataSpecifiedDirectory() {
    fileUtilMock.setContent("abc");
    rssContentCache.setRssContentCacheFolder(userSpecifiedCacheFolder);
    rssContentCache.reloadRssCache();

    String content = rssContentCache.getRssContent(TEST);
    assertEquals("abc", content);
    fileUtilMock.assertContent("abc");
    fileUtilMock.assertFileRead(new File(userSpecifiedCacheFolder, TEST));
  }

  @Test
  public void testExceptionHandling() {
    fileUtilMock.setExceptionToThrow(new FileUtilException());
    rssContentCache.reloadRssCache();
    assertEquals("Could not read RSS Content Cache from file\nCould not write RSS Content Cache to file\n", factory.getError(true));
  }

  @Test
  public void testTwoCachedFiles() {
    httpFetcherMock.addContent("http://testurl", "abc");
    httpFetcherMock.addContent("http://secondurl", "def");

    Map<String, String> nameToUrlMap = new HashMap<String, String>();
    nameToUrlMap.put("second", "http://secondurl");
    rssContentCache.setNameToUrlMap(nameToUrlMap);
    rssContentCache.reloadRssCache();
    assertEquals("abc", rssContentCache.getRssContent(TEST));
    assertEquals("def", rssContentCache.getRssContent("second"));
  }
}
