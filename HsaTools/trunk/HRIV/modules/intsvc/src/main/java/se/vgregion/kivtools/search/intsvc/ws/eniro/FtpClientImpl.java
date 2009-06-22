package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;

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

	private String ftpDestinationFileName;

	public boolean sendFile(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ftpclient.connect(hostname, port);
			ftpclient.login(username, password);
			boolean storedSuccess = ftpclient.storeFile(ftpDestinationFileName, fileInputStream);
			fileInputStream.close();
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
