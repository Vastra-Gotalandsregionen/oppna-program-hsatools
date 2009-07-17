package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

public class FtpClientImplTest {

  private FtpClientImpl ftpClientImpl = new FtpClientImpl();
  private FTPClient mockFtpClient;
  private static String FTPDESTINATIONFILENAME = "ftpDestinationFileName";
  private static String FILE_CONTENT = "This is file content";
  private static String HOSTNAME = "hostname";
  private static int PORT = 21;
  private static String USERNAME = "username";
  private static String PASSWORD = "password";

  @Before
  public void setup() throws SocketException, IOException {
    mockFtpClient = createMock(FTPClient.class);
    mockFtpClient.connect(HOSTNAME, PORT);
    expect(mockFtpClient.login(USERNAME, PASSWORD)).andReturn(true);
    expect(mockFtpClient.storeFile(eq(FTPDESTINATIONFILENAME), isA(InputStream.class))).andReturn(true);
    expect(mockFtpClient.logout()).andReturn(true);
    mockFtpClient.disconnect();
    replay(mockFtpClient);
    ftpClientImpl.setFtpclient(mockFtpClient);
    ftpClientImpl.setFtpDestinationFileName(FTPDESTINATIONFILENAME);
    ftpClientImpl.setHostname(HOSTNAME);
    ftpClientImpl.setPassword(PASSWORD);
    ftpClientImpl.setUsername(USERNAME);
    ftpClientImpl.setPort(PORT);
  }

  @Test
  public void testSendFile() {
    assertEquals(true, ftpClientImpl.sendFile(FILE_CONTENT));
  }

  @Test
  public void testSendFileWithException() {
    assertEquals(false, ftpClientImpl.sendFile(null));
  }
}
