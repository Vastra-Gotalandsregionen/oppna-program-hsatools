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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.vgregion.kivtools.util.StringUtil;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Implementation of FtpClient for the SFTP protocol.
 */
public class SftpClientImpl implements FtpClient {

  private Log logger = LogFactory.getLog(this.getClass());
  private String username;
  private String hostname;
  private String password;
  private JSch jsch;
  private int port;

  public void setUsername(String username) {
    this.username = username;
  }

  public void setHostname(String ftpHost) {
    this.hostname = ftpHost;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setJsch(JSch jsch) {
    this.jsch = jsch;
  }

  public void setPort(int port) {
    this.port = port;
  }

  /**
   * {@inheritDoc}
   */
  public boolean sendFile(String fileContent, String basename, String suffix) {
    try {
      InputStream inputStream = new ByteArrayInputStream(StringUtil.getBytes(fileContent, "UTF-8"));
      Session session = jsch.getSession(username, hostname, port);
      session.setPassword(password);
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
      ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
      channelSftp.connect();
      channelSftp.put(inputStream, basename + "." + suffix);
      channelSftp.disconnect();
      session.disconnect();
      return true;
    } catch (JSchException e) {
      logger.error("Error in SftpClient", e);
    } catch (SftpException e) {
      logger.error("Error in SftpClient", e);
    }
    return false;
  }
}
