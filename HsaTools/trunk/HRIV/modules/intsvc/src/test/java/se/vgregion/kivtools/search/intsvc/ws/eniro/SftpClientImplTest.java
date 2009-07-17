package se.vgregion.kivtools.search.intsvc.ws.eniro;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpClientImplTest {
  private SftpClientImpl sftpClientImpl = new SftpClientImpl();
  private static String FILE_CONTENT = "This is file content";
  private static String FTPDESTINATIONFILENAME = "ftpDestinationFileName";
  private static String HOSTNAME = "hostname";
  private static int PORT = 21;
  private static String USERNAME = "username";
  private static String PASSWORD = "password";
  private static String STRICTHOSTKEYCHECKING = "StrictHostKeyChecking";
  private static String STRICTHOSTKEYCHECKING_VALUE = "no";
  private JSch mockJSch;
  private Session mockSession;
  private ChannelSftp mockChannelSftp;

  @Before
  public void setup() throws JSchException, SftpException {
    mockJSch = createMock(JSch.class);
    mockSession = createMock(Session.class);
    mockChannelSftp = createMock(ChannelSftp.class);
    expect(mockJSch.getSession(USERNAME, HOSTNAME, PORT)).andReturn(mockSession);
    mockSession.setPassword(PASSWORD);
    mockSession.setConfig(STRICTHOSTKEYCHECKING, STRICTHOSTKEYCHECKING_VALUE);
    mockSession.connect();
    expect(mockSession.openChannel("sftp")).andReturn(mockChannelSftp);
    mockChannelSftp.connect();
    mockChannelSftp.put(isA(InputStream.class), eq(FTPDESTINATIONFILENAME));
    mockChannelSftp.disconnect();
    mockSession.disconnect();
    replay(mockJSch, mockSession, mockChannelSftp);
    sftpClientImpl.setJsch(mockJSch);
    sftpClientImpl.setFtpDestinationFileName(FTPDESTINATIONFILENAME);
    sftpClientImpl.setHostname(HOSTNAME);
    sftpClientImpl.setPassword(PASSWORD);
    sftpClientImpl.setUsername(USERNAME);
    sftpClientImpl.setPort(PORT);
  }

  @Test
  public void testSendFile() {
    assertEquals(true, sftpClientImpl.sendFile(FILE_CONTENT));
  }
  
  @Test
  public void testSendFileWithException() {
    assertEquals(false, sftpClientImpl.sendFile(null));
  }
}
