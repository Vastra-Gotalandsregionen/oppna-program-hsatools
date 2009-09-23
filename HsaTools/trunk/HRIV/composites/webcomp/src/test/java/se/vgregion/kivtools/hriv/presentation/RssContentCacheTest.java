/**
 * Copyright 2009 Västa Götalandsregionen
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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.file.FileUtilMock;
import se.vgregion.kivtools.util.file.FileUtilException;

public class RssContentCacheTest {
  private static LogMock logMock = new LogMock();

  private RssContentCache rssContentCache;
  private HttpFetcherMock httpFetcherMock;
  private FileUtilMock fileUtilMock;
  private File defaultHrivSettingsFolder;
  private File defaultCacheFile;
  private File userSpecifiedHrivSettingsFolder;
  private File userSpecifiedCacheFile;

  @BeforeClass
  public static void beforeClass() {
    System.setProperty("org.apache.commons.logging.LogFactory", "se.vgregion.kivtools.hriv.presentation.LogFactoryMock");
    LogFactory.releaseAll();
    LogFactoryMock factory = (LogFactoryMock) LogFactory.getFactory();
    factory.setLog(logMock);
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

  @After
  public void tearDown() {
    RssContentCacheTest.logMock.messageObject.clear();
  }

  @AfterClass
  public static void afterClass() {
    System.clearProperty("org.apache.commons.logging.LogFactory");
    LogFactory.releaseAll();
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
    assertEquals(2, logMock.messageObject.size());
    assertEquals("Could not read RSS Content Cache from file", logMock.messageObject.get(0));
    assertEquals("Could not write RSS Content Cache to file", logMock.messageObject.get(1));
  }

  static class LogMock implements Log {
    private List<Object> messageObject = new ArrayList<Object>();

    @Override
    public void error(Object message, Throwable t) {
      this.messageObject.add(message);
    }

    // Not implemented methods

    @Override
    public void debug(Object message) {
    }

    @Override
    public void debug(Object message, Throwable t) {
    }

    @Override
    public void error(Object message) {
    }

    @Override
    public void fatal(Object message) {
    }

    @Override
    public void fatal(Object message, Throwable t) {
    }

    @Override
    public void info(Object message) {
    }

    @Override
    public void info(Object message, Throwable t) {
    }

    @Override
    public boolean isDebugEnabled() {
      return true;
    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }

    @Override
    public boolean isFatalEnabled() {
      return true;
    }

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    @Override
    public boolean isTraceEnabled() {
      return true;
    }

    @Override
    public boolean isWarnEnabled() {
      return true;
    }

    @Override
    public void trace(Object message) {
    }

    @Override
    public void trace(Object message, Throwable t) {
    }

    @Override
    public void warn(Object message) {
    }

    @Override
    public void warn(Object message, Throwable t) {
    }
  }
}
