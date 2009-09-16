package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;

public class FtpClientImplTest {
  private FtpClientImpl ftpClientImpl = new FtpClientImpl();
  private FtpClientMock mockFtpClient;
  private static String FTPDESTINATIONFILENAME = "ftpDestinationFileName";
  private static String FILE_CONTENT = "This is file content";
  private static String HOSTNAME = "hostname";
  private static int PORT = 21;
  private static String USERNAME = "username";
  private static String PASSWORD = "password";

  @Before
  public void setup() throws SocketException, IOException {
    mockFtpClient = new FtpClientMock();
    ftpClientImpl.setFtpclient(mockFtpClient);
    ftpClientImpl.setFtpDestinationFileName(FTPDESTINATIONFILENAME);
    ftpClientImpl.setHostname(HOSTNAME);
    ftpClientImpl.setPassword(PASSWORD);
    ftpClientImpl.setUsername(USERNAME);
    ftpClientImpl.setPort(PORT);
  }

  @Test
  public void testSendFile() {
    boolean result = ftpClientImpl.sendFile(FILE_CONTENT);

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
    ftpClientImpl.sendFile(null);
  }

  @Test
  public void testSendFileWithIOException() {
    mockFtpClient.throwIOException = true;
    assertFalse(ftpClientImpl.sendFile("hej"));
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
