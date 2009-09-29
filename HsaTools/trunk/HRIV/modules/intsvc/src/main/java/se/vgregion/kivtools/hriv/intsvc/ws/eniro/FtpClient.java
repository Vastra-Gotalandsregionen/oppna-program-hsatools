package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

/**
 * Describes the methods an FtpClient needs to implement.
 * 
 * @author David Bennehult & Jonas Liljenfeldt.
 */
public interface FtpClient {

  /**
   * Sends the provided file content to the configured server.
   * 
   * @param fileContent The file content to send.
   * @return True if sending was successful, otherwise false.
   */
  boolean sendFile(String fileContent);
}
