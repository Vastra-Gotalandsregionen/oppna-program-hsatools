package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpClientImpl implements FtpClient {

	Log logger = LogFactory.getLog(this.getClass());
	private String username;
	private String hostname;
	private String password;
	private String ftpDestinationFileName;
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

	public void setFtpDestinationFileName(String ftpDestinationFileName) {
		this.ftpDestinationFileName = ftpDestinationFileName;
	}

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean sendFile(File file) {
		try {
			Session session = jsch.getSession(username, hostname, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();
			channelSftp.put(new FileInputStream(file), ftpDestinationFileName);
			channelSftp.disconnect();
			session.disconnect();
			return true;
		} catch (Exception e) {
			logger.error("Error in SftpClient", e);
		}
		return false;
	}

}
