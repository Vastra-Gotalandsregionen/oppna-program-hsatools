package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

/**
 * Service interface for an InformationPusher.
 * 
 * @author David Bennehult & Jonas Liljenfeldt.
 */
public interface InformationPusher {

  /**
   * Called when the service should execute.
   * 
   * @throws Exception if execution fails.
   */
  void doService() throws Exception;
}
