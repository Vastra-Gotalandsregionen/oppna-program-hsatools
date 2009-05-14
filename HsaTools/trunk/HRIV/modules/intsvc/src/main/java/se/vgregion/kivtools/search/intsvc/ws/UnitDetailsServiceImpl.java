package se.vgregion.kivtools.search.intsvc.ws;

import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitRepository;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Address;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.ObjectFactory;
import se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Organization;

;

public class UnitDetailsServiceImpl implements UnitDetailsService<Organization> {

	private UnitRepository unitRepository;
	private ObjectFactory objectFactory = new ObjectFactory();

	public void setUnitRepository(UnitRepository unitRepository) {
		this.unitRepository = unitRepository;
	}

	public Organization getUnitDetails(String hsaIdentity) {
		Organization organization = objectFactory.createOrganization();
		if (hsaIdentity != null && !"".equals(hsaIdentity)) {
			Unit unit = null;
			try {
				unit = unitRepository.getUnitByHsaId(hsaIdentity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			organization.getUnit().add(generateWebServiceUnit(unit));
		}
		return organization;
	}

	private se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit generateWebServiceUnit(Unit unit) {
		se.vgregion.kivtools.search.svc.push.impl.eniro.jaxb.Unit unitWs = objectFactory.createUnit();
		Address address = objectFactory.createAddress();
		unitWs.setId(unit.getHsaIdentity());
		unitWs.setName(unit.getName());

		// Set unit address
		address.setStreetName(unit.getHsaPostalAddress().getStreet());
		address.setCity(unit.getHsaPostalAddress().getCity());

		return unitWs;
	}

}
