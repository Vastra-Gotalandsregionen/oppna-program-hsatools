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

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.mocks.LogFactoryMock;

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
  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void setupClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    LogFactoryMock.resetInstance();
  }

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
    logFactoryMock.getError(true);
  }

  @Test
  public void testSendFile() {
    assertEquals(true, sftpClientImpl.sendFile(FILE_CONTENT));
  }

  @Test
  public void testSendFileWithJschException() throws Exception {
    mockJSch = createMock(JSch.class);
    expect(mockJSch.getSession(USERNAME, HOSTNAME, PORT)).andThrow(new JSchException());
    replay(mockJSch);

    sftpClientImpl.setJsch(mockJSch);

    assertEquals(false, sftpClientImpl.sendFile(FILE_CONTENT));
  }

  @Test
  public void testSendFileWithSftpException() throws Exception {
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
    expectLastCall().andThrow(new SftpException(1, "Test"));
    replay(mockJSch, mockSession, mockChannelSftp);

    sftpClientImpl.setJsch(mockJSch);

    assertEquals(false, sftpClientImpl.sendFile(FILE_CONTENT));
    assertEquals("Error in SftpClient\n", logFactoryMock.getError(true));
  }
}
