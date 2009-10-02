package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

/**
 * 
 * @author david
 *
 */
public class FtpClientToFileWriter implements FtpClient {

  @Override
  public boolean sendFile(String fileContent) {
    System.out.println(fileContent);
    return true;
  }

}
