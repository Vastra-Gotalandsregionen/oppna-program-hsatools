package se.vgregion.kivtools.hriv.intsvc.ws.eniro;
/**
 * 
 * @author David Bennehult
 *
 */
/**
 * Describes the methods an FtpClient needs to implement.
 * 
 * @author David Bennehult & Jonas Liljenfeldt.
 */
public interface FtpClient {
  /**
   * Send file to source.
   * @param fileContent content to send.
   * @return true if operation was successful.
   */
  /**
   * Sends the provided file content to the configured server.
   * 
   * @param fileContent The file content to send.
   * @return True if sending was successful, otherwise false.
   */
  boolean sendFile(String fileContent);
}
