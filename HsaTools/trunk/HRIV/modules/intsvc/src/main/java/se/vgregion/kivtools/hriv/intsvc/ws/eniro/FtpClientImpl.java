package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;


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

  public boolean sendFile(String fileContent) {
    try {
      InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes("UTF-8"));
      ftpclient.connect(hostname, port);
      ftpclient.enterLocalPassiveMode();
      boolean loginSuccess = ftpclient.login(username, password);
      logger.debug("Unit details pusher: FTP login status: " + loginSuccess +". Server reply: " + ftpclient.getReplyString());
      // Try to remove old file
      boolean deleteSuccess = ftpclient.deleteFile(ftpDestinationFileName);
      if (deleteSuccess) {
    	  logger.debug("Unit details pusher: Deleted " + ftpDestinationFileName + " on server.");
      }
      boolean storedSuccess = ftpclient.storeFile(ftpDestinationFileName, inputStream);
      inputStream.close();
      // Logout from the FTP Server and disconnect
      ftpclient.logout();
      ftpclient.disconnect();
      return storedSuccess;
    } catch (Exception e) {
      logger.error("Error in SftpClient", e);
    }
    return false;
  }
}
