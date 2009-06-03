package se.vgregion.kivtools.search.intsvc.ws.sahlgrenska;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import se.vgregion.kivtools.search.intsvc.ws.domain.sahlgrenska.Organization;
import se.vgregion.kivtools.search.intsvc.ws.domain.sahlgrenska.UnitRequest;
import se.vgregion.kivtools.search.intsvc.ws.domain.sahlgrenska.UnitResponse;

public class UnitDetailsEndpoint extends AbstractMarshallingPayloadEndpoint {

	private UnitDetailsService<Organization> unitDetailsService;

	public void setUnitDetailsService(UnitDetailsService<Organization> unitDetailsService) {
		this.unitDetailsService = unitDetailsService;
	}

	public UnitDetailsEndpoint(Marshaller marshaller, Unmarshaller unmarshaller) {
		super(marshaller, unmarshaller);
	}

	@Override
	protected Object invokeInternal(Object request) throws Exception {
		UnitRequest unitRequest = (UnitRequest) request;
		Organization organization = unitDetailsService.getUnitDetails(unitRequest.getHsaIdentity());
		UnitResponse response = new UnitResponse();
		response.setOrganization(organization);
		return response;
	}

}
