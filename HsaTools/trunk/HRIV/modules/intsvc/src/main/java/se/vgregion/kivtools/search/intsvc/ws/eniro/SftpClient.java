package se.vgregion.kivtools.search.intsvc.ws.eniro;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SftpClient implements FtpClient {

	Log logger = LogFactory.getLog(this.getClass());
	private String ftpUser;
	private String ftpHost;
	private String ftpPassword;
	private String ftpDestinationFileName;
	private JSch jsch;
	private int ftpPort;

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public void setFtpHost(String ftpHost) {
		this.ftpHost = ftpHost;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public void setFtpDestinationFileName(String ftpDestinationFileName) {
		this.ftpDestinationFileName = ftpDestinationFileName;
	}

	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public boolean sendFile(File file) {
		try {
			Session session = jsch.getSession(ftpUser, ftpHost, ftpPort);
			session.setPassword(ftpPassword);
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
