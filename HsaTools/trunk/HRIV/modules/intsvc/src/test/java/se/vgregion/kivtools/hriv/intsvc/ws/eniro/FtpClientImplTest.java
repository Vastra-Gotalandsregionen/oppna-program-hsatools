package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;

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
    mockFtpClient.enterLocalPassiveMode();
    expect(mockFtpClient.login(USERNAME, PASSWORD)).andReturn(true);
    expect(mockFtpClient.getReplyString()).andReturn("FtpClient reply string");
    expect(mockFtpClient.deleteFile(FTPDESTINATIONFILENAME)).andReturn(true);
    // Add two more repliker
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
