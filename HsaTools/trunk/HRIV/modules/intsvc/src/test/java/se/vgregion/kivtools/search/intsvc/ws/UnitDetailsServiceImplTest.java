package se.vgregion.kivtools.search.intsvc.ws;

import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;


public class UnitDetailsServiceImplTest {

	@Test
	public void testGetUnitDetails() throws Exception {
		UnitDetailsService unitDetailsService = new UnitDetailsServiceImpl();
		Node responseElement = unitDetailsService.getUnitDetails("hsaId1");
		
		//checkResponseData();
	}
	
	private void checkResponseData(Reader inputReader) throws Exception{
		JAXBContext context = JAXBContext.newInstance("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Organization organization= (Organization) unmarshaller.unmarshal(inputReader);
		Assert.assertEquals("vgr", organization.getName());
	}
}
