package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.executor.jsf.FlowExecutionKeyStateHolder;
import org.springframework.webflow.executor.jsf.JsfExternalContext;
import org.springframework.webflow.executor.mvc.FlowController;

import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImpl;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImplService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;

public class RegisterOnUnitController implements Serializable {

	private static final long serialVersionUID = 1L;
	VardvalService vardValService;
	SearchService searchService;
	String signatureserviceUrl = "https://test.signicat.com/signatureservice/services/signatureservice";
	String serviceUrl = "https://test.signicat.com/std/method/shared?method=sign&profile=default";
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
			////vardvalInfo = vardValService.getVardval(ssn);

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
		try {

			String registrationData = "197407185656_" + "SE2321000131-E000000006727" + "_20090626094900";
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
			String eventId = "Action.postChangeRegistration";
			
			
			// TODO Extract flowExecutionKey in a nicer way
			Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
			String flowExecutionKey = "";
			while (parameterNames.hasMoreElements()) {
				String paramName = parameterNames.nextElement();
				if (paramName.indexOf("_flowExecutionKey") > -1) {
					flowExecutionKey = httpServletRequest.getParameter(paramName); 
				}
			}
			
			String targetUrl = urlToThisPage.substring(0, urlToThisPage.lastIndexOf("/") + 1) + "confirmationRegistrationChanges.jsf?_flowId=HRIV.registrationOnUnit-flow&_eventId=" + eventId
					+ "&_flowExecutionKey=" + flowExecutionKey + "&redirectedFromSignatureService=true";

			// Redirect user to test.signicat.com
			System.out.println("Redirects the user to " + targetUrl);
			redirectUrl = serviceUrl + "&documentArtifact=" + artifact + "&target=" + URLEncoder.encode(targetUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			redirectUrl = "FELSIDA";
			// FIXME
		}
		return redirectUrl;
	}

	public String postCommitRegistrationOnUnit(VardvalInfo vardvalInfo, ExternalContext externalContext) {

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
		// vardValService.setVardval(vardvalInfo.getSsn(),
		// vardvalInfo.getSelectedUnitId(), "".getBytes());// vardvalInfo
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
		return "success";
	}

}
