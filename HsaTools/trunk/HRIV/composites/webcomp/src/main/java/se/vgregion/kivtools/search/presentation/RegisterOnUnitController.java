package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.executor.jsf.JsfExternalContext;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImpl;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImplService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

public class RegisterOnUnitController implements Serializable {

	private static final long serialVersionUID = 1L;
	VardvalService vardValService;
	SearchService searchService;
	private static final String signatureserviceUrl = "https://test.signicat.com/signatureservice/services/signatureservice";
	private static final String serviceUrl = "https://test.signicat.com/std/method/vgr?method=sign"; //shared ska bytas till vgr
	SignatureEndpointImplService signatureEndpointImplService = new SignatureEndpointImplService();
	SignatureEndpointImpl signatureservice = signatureEndpointImplService.getSignatureservice();

	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	public void setVardValService(VardvalService vardValService) {
		this.vardValService = vardValService;
	}

	public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId) throws Exception {

		JsfExternalContext jsfExternalContext = (JsfExternalContext) externalContext;

		// WAS returns null. Maybe because the url is not protected in web.xml?
		// Principal remoteUser = externalContext.getUserPrincipal();

		Map<String, String> requestHeaderMap = jsfExternalContext.getFacesContext().getExternalContext().getRequestHeaderMap();

		String ssn = requestHeaderMap.get("iv-user");
		// String ldapPath = requestHeaderMap.get("iv-user-1");

		// FIXME Remove this line
		ssn = "194509259257";

		// Request information about the listing from Vårdvals system's
		// webservice
		VardvalInfo vardvalInfo = new VardvalInfo();
		try {
			vardvalInfo = vardValService.getVardval(ssn);

			// Lookup unit names in order to show real names instead of hsa ids
			// TODO Exception handling
			Unit selectedUnit = searchService.getUnitByHsaId(selectedUnitId);
			if (selectedUnit != null) {
				vardvalInfo.setSelectedUnitName(selectedUnit.getName());
			}
			vardvalInfo.setSelectedUnitId(selectedUnitId);
			Unit currentUnit = searchService.getUnitByHsaId(vardvalInfo.getCurrentHsaId());
			Unit upcomingUnit = searchService.getUnitByHsaId(vardvalInfo.getUpcomingHsaId());

			// Set values in DTO
			if (currentUnit != null) {
				vardvalInfo.setCurrentUnitName(currentUnit.getName());
			}
			if (upcomingUnit != null) {
				vardvalInfo.setUpcomingUnitName(upcomingUnit.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			// FIXME Proper exception handling. Show information page if we
			// could not find unit with given hsa id
		}

		vardvalInfo.setSsn(ssn);
		return vardvalInfo;
	}

	/**
	 * 
	 * @param vardvalInfo
	 * @param externalContext
	 * @return redirectUrl The url to redirect to
	 * @throws UnsupportedEncodingException
	 */
	public String preCommitRegistrationOnUnit(VardvalInfo vardvalInfo, ExternalContext externalContext) {
		String redirectUrl = "";
		Date date = new Date();
		try {
			// FIXME Should be a formal letter instead
			String registrationData = "Jag " + vardvalInfo.getSsn() + " listar mig härmed på vårdcentral " + vardvalInfo.getSelectedUnitName() + " (hsaid: " + vardvalInfo.getSelectedUnitId() + ").\nDatum: " + Constants.formateDateToZuluTime(date);
			byte[] base64encoded = Base64.encodeBase64(registrationData.getBytes("UTF-8"));
			String mimeType = "text/plain";
			String documentDescription = "Signature for Vardval registration";
			String artifact = signatureservice.registerDocument(new String(base64encoded), mimeType, documentDescription);
			System.out.println("Artifact recieved: " + artifact);
			// Save artifact
			externalContext.getSessionMap().put("artifact", artifact);

			// Construct redirect url
			JsfExternalContext jsfExternalContext = (JsfExternalContext) externalContext;
			HttpServletRequest httpServletRequest = (HttpServletRequest) jsfExternalContext.getFacesContext().getExternalContext().getRequest();
			String urlToThisPage = httpServletRequest.getRequestURL().toString();

			String targetUrl = urlToThisPage.substring(0, urlToThisPage.lastIndexOf("/") + 1) + "confirmationRegistrationChanges.jsf?_flowId=HRIV.registrationOnUnitPostSign-flow&ssn="
					+ vardvalInfo.getSsn() + "&selectedUnitId=" + vardvalInfo.getSelectedUnitId();

			// Redirect user to test.signicat.com
			System.out.println("Redirects the user to " + targetUrl);
			redirectUrl = serviceUrl + "&documentArtifact=" + artifact + "&target=" + URLEncoder.encode(targetUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			redirectUrl = "FELSIDA";
			// FIXME
		}
		return redirectUrl;
	}

	public VardvalInfo postCommitRegistrationOnUnit(String ssn, String selectedUnitId, ExternalContext externalContext) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage {

		String errorMessage;
		byte[] samlAssertionBytes = new byte[0];

		String artifact = (String) externalContext.getSessionMap().get("artifact");
		String samlAssertion = signatureservice.retrieveSaml(artifact);
		System.out.println("Saml Assertion recieved");
		if (samlAssertion == null || samlAssertion.length() == 0) {
			errorMessage = "SAML assertion was empty";
		}
		// if (errorMessage == null) {
		// samlAssertionBytes =
		// Base64.decodeBase64(samlAssertion.getBytes("utf-8"));
		System.out.println("Saml Assertion decoded");
		// }

		// signature should be byte[]
		vardValService.setVardval(ssn, selectedUnitId, "".getBytes());// vardvalInfo
		// .
		// getSignature
		// (
		// )
		// .
		// getBytes
		// (
		// )
		// )
		// ;
		
		VardvalInfo vardvalInfo = new VardvalInfo();
		vardvalInfo.setSsn(ssn);
		vardvalInfo.setSelectedUnitId(selectedUnitId);
		return vardvalInfo;
	}

}
