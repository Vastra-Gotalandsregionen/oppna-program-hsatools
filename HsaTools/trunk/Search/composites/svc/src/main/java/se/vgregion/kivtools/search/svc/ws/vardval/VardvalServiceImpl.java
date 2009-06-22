package se.vgregion.kivtools.search.svc.ws.vardval;

import javax.xml.bind.JAXBElement;

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

		if (currentVardval != null) {
			vardvalInfo.setCurrentHsaId(currentVardval.getValue().getVårdcentralHsaId());
			vardvalInfo.setCurrentValidFromDate(currentVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
		}

		if (upcomingVardval != null) {
			vardvalInfo.setUpcomingHsaId(upcomingVardval.getValue().getVårdcentralHsaId());
			vardvalInfo.setUpcomingValidFromDate(upcomingVardval.getValue().getGiltigFrån().toGregorianCalendar().getTime());
		}
		return vardvalInfo;
	}

	@Override
	public VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) {
		IVårdvalService service = vardvalService.getBasicHttpBindingIVårdvalService();
		SetVårdvalRequest vardvalRequest = new SetVårdvalRequest();
		JAXBElement<String> soapSsn = objectFactory.createGetVårdvalRequestPersonnummer(ssn);
		JAXBElement<byte[]> soapSignature = objectFactory.createBase64Binary(signature);
		JAXBElement<String> soapHsaId = objectFactory.createString(hsaId);
		vardvalRequest.setPersonnummer(soapSsn);
		vardvalRequest.setSigneringskod(soapSignature);
		vardvalRequest.setVårdcentralHsaId(soapHsaId);
		SetVårdvalResponse response = service.setVårdVal(vardvalRequest);
		return generateVardvalInfo(response);
	}

	/**
	 * 
	 * @param ssn
	 *            - Social Security Number
	 * @return - Soap response
	 */
	private GetVårdvalResponse getVardvalInfo(String ssn) {
		IVårdvalService service = vardvalService.getBasicHttpBindingIVårdvalService();
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<String> soapSsn = objectFactory.createGetVårdvalRequestPersonnummer(ssn);
		GetVårdvalRequest getVardvalRequest = new GetVårdvalRequest();
		getVardvalRequest.setPersonnummer(soapSsn);
		GetVårdvalResponse getVardvalResponse = service.getVårdVal(getVardvalRequest);
		return getVardvalResponse;
	}

}
