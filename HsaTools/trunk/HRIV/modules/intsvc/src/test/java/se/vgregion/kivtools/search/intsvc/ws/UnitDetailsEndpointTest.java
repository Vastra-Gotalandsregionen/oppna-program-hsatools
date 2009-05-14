package se.vgregion.kivtools.search.intsvc.ws;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import se.vgregion.kivtools.search.intsvc.ws.UnitDetailsService;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.ObjectFactory;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.UnitRequest;

public class UnitDetailsEndpointTest {
	UnitDetailsEndpoint unitDetailsEndpoint;
	
	@Test
	public void testInvoke() throws Exception{
		// Create a UnitRequest object 
		UnitRequest unitRequest = new UnitRequest();
		unitRequest.setHsaIdentity("hsaId1");
		Organization organization = (Organization) unitDetailsEndpoint.invokeInternal(unitRequest);
		Assert.assertEquals("hsaId1", organization.getUnit().get(0).getId());
	}

	// Setup a UnitDetailsEndpoint object
	@Before
	public void setup() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath("se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb");
		unitDetailsEndpoint = new UnitDetailsEndpoint(jaxb2Marshaller,jaxb2Marshaller);
		unitDetailsEndpoint.setUnitDetailsService(createUnitDetailsServiceMock("hsaId1"));
	}
	
	// Create a mock object with a containing a unit with the unitId parameter
	private UnitDetailsService<Organization> createUnitDetailsServiceMock(String unitId){
		ObjectFactory objectFactory = new ObjectFactory();
		Organization organization = objectFactory.createOrganization();
		Unit unit = objectFactory.createUnit();
		unit.setId(unitId);
		organization.getUnit().add(unit);
		UnitDetailsService<Organization> unitDetailsServiceMock = EasyMock.createMock(UnitDetailsService.class);
		EasyMock.expect(unitDetailsServiceMock.getUnitDetails(unitId)).andReturn(organization);
		EasyMock.replay(unitDetailsServiceMock);
		return unitDetailsServiceMock;
	}
}
