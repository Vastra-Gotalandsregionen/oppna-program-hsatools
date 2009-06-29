package se.vgregion.kivtools.search.svc.ws.vardval;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalEntry;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;

public class VardvalServiceImpl implements VardvalService {

	private VårdvalService vardvalService;
	private ObjectFactory objectFactory = new ObjectFactory();
	private String username, password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setVardvalService(VårdvalService vardvalService) {
		this.vardvalService = vardvalService;
	}

	@Override
	public VardvalInfo getVardval(String ssn) {
		GetVårdvalResponse response = getVardvalInfo(ssn);
		return generateVardvalInfo(response);
	}

	private VardvalInfo generateVardvalInfo(Object response) {
		VardvalInfo vardvalInfo = new VardvalInfo();
		JAXBElement<VårdvalEntry> currentVardval = null;
		JAXBElement<VårdvalEntry> upcomingVardval = null;
		// Check what sort of soap response
		if (response instanceof GetVårdvalResponse) {
			currentVardval = ((GetVårdvalResponse) response).getAktivtVårdval();
			upcomingVardval = ((GetVårdvalResponse) response).getKommandeVårdval();
		} else if (response instanceof SetVårdvalResponse) {
			currentVardval = ((SetVårdvalResponse) response).getAktivtVårdval();
			upcomingVardval = ((SetVårdvalResponse) response).getKommandeVårdval();
		}

		if (currentVardval != null && currentVardval.getValue() != null) {
			vardvalInfo.setCurrentHsaId(currentVardval.getValue().getVårdcentralHsaId());
			vardvalInfo.setCurrentValidFromDate(currentVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
		}

		if (upcomingVardval != null && upcomingVardval.getValue() != null) {
			vardvalInfo.setUpcomingHsaId(upcomingVardval.getValue().getVårdcentralHsaId());
			vardvalInfo.setUpcomingValidFromDate(upcomingVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
		}
		return vardvalInfo;
	}

	@Override
	public VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) {
		IVårdvalService service = vardvalService.getBasicHttpBindingIVårdvalService();
		assignCredentials(service);
		SetVårdvalRequest vardvalRequest = new SetVårdvalRequest();
		JAXBElement<String> soapSsn = objectFactory.createSetVårdvalRequestPersonnummer(ssn);
		JAXBElement<byte[]> soapSignature = objectFactory.createSetVårdvalRequestSigneringskod(signature);
		JAXBElement<String> soapHsaId = objectFactory.createSetVårdvalRequestVårdcentralHsaId(hsaId);
		vardvalRequest.setPersonnummer(soapSsn);
		vardvalRequest.setSigneringskod(soapSignature);
		vardvalRequest.setVårdcentralHsaId(soapHsaId);
		SetVårdvalResponse response = service.setVårdVal(vardvalRequest);
		return generateVardvalInfo(response);
	}

	private void assignCredentials(IVårdvalService service) {
		BindingProvider bindingProvider = (BindingProvider) service;
		bindingProvider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		bindingProvider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
	}

	/**
	 * 
	 * @param ssn
	 *            - Social Security Number
	 * @return - Soap response
	 */
	private GetVårdvalResponse getVardvalInfo(String ssn) {
		IVårdvalService service = vardvalService.getBasicHttpBindingIVårdvalService();
		assignCredentials(service);
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<String> soapSsn = objectFactory.createGetVårdvalRequestPersonnummer(ssn);
		GetVårdvalRequest getVardvalRequest = new GetVårdvalRequest();
		getVardvalRequest.setPersonnummer(soapSsn);
		GetVårdvalResponse getVardvalResponse = service.getVårdVal(getVardvalRequest);
		return getVardvalResponse;
	}

}
