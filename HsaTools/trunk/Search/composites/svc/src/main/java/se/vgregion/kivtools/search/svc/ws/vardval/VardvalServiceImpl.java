package se.vgregion.kivtools.search.svc.ws.vardval;

import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;

import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.GetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalService;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.ObjectFactory;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalRequest;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.SetVårdvalResponse;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalEntry;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.VårdvalService;

public class VardvalServiceImpl implements VardvalService {

	private VårdvalService vardvalService;
	private ObjectFactory objectFactory = new ObjectFactory();
	private String username, password;
	private String webserviceEndpoint;
	private String keystoreLocation;
	private String keystoreType;
	private String keystorePassword;
	private String truststoreLocation;
	private String truststoreType;
	private String truststorePassword;

	private void setup() {
		Map<String, Object> requestContext = ((BindingProvider) vardvalService.getBasicHttpBindingIVårdvalService()).getRequestContext();
		requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceEndpoint);
		// Set system properties for ssl use
		System.setProperty("javax.net.ssl.keyStoreType", keystoreType);
		System.setProperty("javax.net.ssl.trustStoreType", truststoreType);
		System.setProperty("javax.net.ssl.keyStore", keystoreLocation);
		System.setProperty("javax.net.ssl.trustStore", truststoreLocation);
		System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
		System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);
	}

	public void setKeystoreLocation(String keystoreLocation) {
		this.keystoreLocation = keystoreLocation;
	}

	public void setKeystoreType(String keystoreType) {
		this.keystoreType = keystoreType;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public void setTruststoreLocation(String truststoreLocation) {
		this.truststoreLocation = truststoreLocation;
	}

	public void setTruststoreType(String truststoreType) {
		this.truststoreType = truststoreType;
	}

	public void setTruststorePassword(String truststorePassword) {
		this.truststorePassword = truststorePassword;
	}

	public void setWebserviceEndpoint(String webserviceEndpoint) {
		this.webserviceEndpoint = webserviceEndpoint;
	}

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
	public VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage {
		IVårdvalService service = vardvalService.getBasicHttpBindingIVårdvalService();
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
