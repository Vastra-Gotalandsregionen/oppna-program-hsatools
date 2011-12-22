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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;

public class FtpClientImplTest {
  private FtpClientImpl ftpClientImpl = new FtpClientImpl();
  private FtpClientMock mockFtpClient;
  private static String FTPDESTINATIONFILENAME = "ftpDestinationFileName";
  private static String FILE_CONTENT = "This is file content";
  private static String HOSTNAME = "hostname";
  private static int PORT = 21;
  private static String USERNAME = "username";
  private static String PASSWORD = "password";
  private static LogFactoryMock logFactoryMock;

  @Before
  public void setup() throws SocketException, IOException {
    mockFtpClient = new FtpClientMock();
    ftpClientImpl.setFtpclient(mockFtpClient);
    ftpClientImpl.setHostname(HOSTNAME);
    ftpClientImpl.setPassword(PASSWORD);
    ftpClientImpl.setUsername(USERNAME);
    ftpClientImpl.setPort(PORT);
  }

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

  @Test
  public void testSendFile() {
    boolean result = ftpClientImpl.sendFile(FILE_CONTENT, FTPDESTINATIONFILENAME, "xml");

    // Verify result from sendFile
    assertTrue(result);

    // Verify connection-information
    assertEquals(USERNAME, mockFtpClient.username);
    assertEquals(PASSWORD, mockFtpClient.password);
    assertEquals(HOSTNAME, mockFtpClient.hostname);
    assertEquals(PORT, mockFtpClient.port);

    // Verify filename of uploaded file
    assertEquals(FTPDESTINATIONFILENAME + "-uploading.xml", mockFtpClient.oldFilename);

    // Verify that uploaded file was renamed correctly
    assertTrue(mockFtpClient.newFilename.matches(FTPDESTINATIONFILENAME + "-[0-9]{14}\\.xml"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSendFileWithIllegalArgumentException() {
    ftpClientImpl.sendFile(null, null, null);
  }

  @Test
  public void testSendFileWithIOException() {
    mockFtpClient.throwIOException = true;
    assertFalse(ftpClientImpl.sendFile("hej", null, null));
    assertEquals("Error in FtpClient\n", logFactoryMock.getError(true));
  }

  class FtpClientMock extends FTPClient {
    String hostname, username, password, oldFilename, newFilename;
    int port;
    boolean throwIOException;

    @Override
    public void connect(String hostname, int port) throws SocketException, IOException {
      this.hostname = hostname;
      this.port = port;
    }

    @Override
    public void enterLocalPassiveMode() {
    }

    @Override
    public boolean login(String username, String password) throws IOException {
      this.username = username;
      this.password = password;
      return true;
    }

    @Override
    public String getReplyString() {
      return "FtpClient reply string";
    }

    @Override
    public boolean deleteFile(String pathname) throws IOException {
      return true;
    }

    @Override
    public boolean storeFile(String remote, InputStream local) throws IOException {
      if (throwIOException) {
        throw new IOException();
      }
      oldFilename = remote;
      return true;
    }

    @Override
    public boolean rename(String from, String to) throws IOException {
      oldFilename = from;
      newFilename = to;
      return true;
    }

    @Override
    public boolean logout() throws IOException {
      return true;
    }

    @Override
    public void disconnect() throws IOException {
    }
  }
}
