package se.vgregion.kivtools.search.intsvc.ws;

import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.Address;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

public class UnitDetailsServiceImplTest {

	
	private UnitDetailsService<Organization> unitDetailsService;

	@Before
	public void setup() throws Exception{
		unitDetailsService = new UnitDetailsServiceImpl();
		((UnitDetailsServiceImpl)unitDetailsService).setUnitRepository(generateUnitRepositoryMock());
	}
	
	@Test
	public void testGetUnitDetails(){
		Organization organization = unitDetailsService.getUnitDetails("hsaId1");
		Assert.assertEquals("hsaId1", organization.getUnit().get(0).getId());
	}
	
	@Test
	public void testGetUnitDetailsWithNullAndEmptyString(){
		Assert.assertNotNull(unitDetailsService.getUnitDetails(null));
		Assert.assertNotNull(unitDetailsService.getUnitDetails(""));
	}

	private UnitRepository generateUnitRepositoryMock() throws Exception {
		UnitRepository unitRepositoryMock = EasyMock.createMock(UnitRepository.class);
		EasyMock.expect(unitRepositoryMock.getUnitByHsaId("hsaId1")).andReturn(createUnitMock("hsaId1"));
		EasyMock.replay(unitRepositoryMock);
		return unitRepositoryMock;
	}

	private Unit createUnitMock(String unitId) {
		Unit unit = new Unit();
		unit.setName("unitName");
		unit.setHsaIdentity(unitId);
		Address address = new Address();
		unit.setHsaPostalAddress(address);
		return unit;
	}
}
