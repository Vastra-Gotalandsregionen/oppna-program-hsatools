package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPSClient;

public class FtpsClient implements FtpClient {

	private Log logger = LogFactory.getLog(this.getClass());
	private FTPSClient ftpsclient;
	private String hostname;
	private int port;
	private String username;
	private String password;

	public void setFtpsclient(FTPSClient ftpsclient) {
		this.ftpsclient = ftpsclient;
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
			ftpsclient.connect(hostname, port);
			ftpsclient.login(username, password);
			boolean storedSuccess = ftpsclient.storeFile(ftpDestinationFileName, fileInputStream);
			fileInputStream.close();
			// Logout from the FTP Server and disconnect
			ftpsclient.logout();
			ftpsclient.disconnect();
			return storedSuccess;
		} catch (Exception e) {
			logger.error("Error in SftpClient", e);
		}
		return false;
	}
}
