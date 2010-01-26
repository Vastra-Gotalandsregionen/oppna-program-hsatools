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
package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

/**
 * Ftp client used for uploading files to ftp server.
 */
public class FtpClientImpl implements FtpClient {

  private Log logger = LogFactory.getLog(this.getClass());
  private FTPClient ftpclient;
  private String hostname;
  private int port;
  private String username;
  private String password;
  private String ftpDestinationFileName;

  public void setFtpclient(FTPClient ftpsclient) {
    this.ftpclient = ftpsclient;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setFtpDestinationFileName(String ftpDestinationFileName) {
    this.ftpDestinationFileName = ftpDestinationFileName;
  }

  /**
   * {@inheritDoc}
   */
  public boolean sendFile(String fileContent) {
    if (fileContent == null) {
      throw new IllegalArgumentException("Input string \"fileContent\" is null.");
    }
    boolean success;
    try {
      InputStream inputStream = new ByteArrayInputStream(StringUtil.getBytes(fileContent, "UTF-8"));
      ftpclient.connect(hostname, port);
      ftpclient.enterLocalPassiveMode();
      boolean loginSuccess = ftpclient.login(username, password);
      logger.debug("Unit details pusher: FTP login status: " + loginSuccess + ". Server reply: " + ftpclient.getReplyString());
      // Try to remove old file. Deletion should not be necessary but try to just in case something went wrong.
      boolean deleteSuccess = ftpclient.deleteFile(ftpDestinationFileName + "-uploading.xml");
      if (deleteSuccess) {
        logger.debug("Unit details pusher: Deleted " + ftpDestinationFileName + " on server.");
      }
      success = ftpclient.storeFile(ftpDestinationFileName + "-uploading.xml", inputStream);
      if (success) {
        success = ftpclient.rename(ftpDestinationFileName + "-uploading.xml", ftpDestinationFileName + "-" + TimeUtil.getCurrentTimeFormatted(DateTimeFormat.SCIENTIFIC_TIME) + ".xml");
      }
      inputStream.close();
      // Logout from the FTP Server and disconnect
      ftpclient.logout();
      ftpclient.disconnect();
      return success;
    } catch (IOException e) {
      logger.error("Error in FtpClient", e);
    }
    return false;
  }
}
