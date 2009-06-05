package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.ftp.FTPSCommand;


public class FtpsClient implements FtpClient {

	private Log logger = LogFactory.getLog(this.getClass());
	private FTPSClient ftpsclient;
	private String hostname;
	private int port;
	private String username;
	private String password;
	

	public void setFtpclient(FTPSClient ftpsclient) {
		this.ftpsclient = ftpsclient;
	}

	public boolean sendFile(File file) {
		try {
			
			ftpsclient.connect(hostname, port);
			ftpsclient.login(username, password);
		    //ftpsclient.appendFile(remote, local)
			// Logout from the FTP Server and disconnect
			ftpsclient.logout();
			ftpsclient.disconnect();

		} catch (Exception e) {
			logger.error("Error in SftpClient", e);
		}
		return false;
	}
	public static void main(String[] args) {
		System.out.println(FTPSCommand.getCommand(FTPSCommand.PROT));
	}
}
