package se.vgregion.kivtools.search.svc.ws.vardval;

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;

/**
 * Test factory method in VardvalServiceFactory
 * @author david
 *
 */
public class VardvalServiceFactoryTest {
	
	/**
	 * Test that the factory method returns a IVårdvalService
	 */
	@Test
	public void testGetIVardvalserviceMethod(){
		IVårdvalService vårdvalService = VardvalServiceFactory.getIVardvalservice();
		Assert.assertNotNull(vårdvalService);
	}
	

}
