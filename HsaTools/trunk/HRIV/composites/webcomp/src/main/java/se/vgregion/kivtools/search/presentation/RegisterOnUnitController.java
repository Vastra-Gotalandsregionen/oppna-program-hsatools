package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;
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
	private static final String serviceUrl = "https://test.signicat.com/std/method/vgr?method=sign"; // shared
																										// ska
																										// bytas
																										// till
																										// vgr
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
		ssn = "190706195526";

		// Request information about the listing from Vårdvals system's
		// webservice
		VardvalInfo vardvalInfo = new VardvalInfo();
		try {
			// vardvalInfo = vardValService.getVardval(ssn);

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

		// For testing
		vardvalInfo.setSelectedUnitId("SE2321000131-E000000006727");
		vardvalInfo.setSelectedUnitName("angered");
		vardvalInfo.setSsn("190706195526");

		try {
			// FIXME Should be a formal letter instead
			String registrationData = "Jag " + vardvalInfo.getSsn() + " listar mig härmed på vårdcentral " + vardvalInfo.getSelectedUnitName() + " (hsaid: " + vardvalInfo.getSelectedUnitId()
					+ "). Datum: " + Constants.formateDateToZuluTime(date);
			byte[] base64encoded = Base64.encodeBase64(registrationData.getBytes("UTF-8"));
			String mimeType = "text/plain";
			String documentDescription = "Signature for Vardval registration";
			
		    // Stores signatureserviceUrl and mimetype in session so
		    // postprocessing.jsp can access them after the signature is done.
			externalContext.getSessionMap().put("signatureservice.url", signatureserviceUrl);
			externalContext.getSessionMap().put("mimeType", mimeType);
			
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

		String errorMessage = null;
		byte[] documentBytes = new byte[0];
		byte[] samlAssertionBytes = new byte[0];
		Map samlAttributes = null;
		String signersIdentity = null;
		String signatureMethod = null;

		try {

			// Reads information stored in the session
			SharedAttributeMap sessionMap = externalContext.getSessionMap();
			String artifact = (String) sessionMap.get("artifact");
			String url = (String) sessionMap.get("signatureservice.url");
			String mimeType = (String) sessionMap.get("mimeType");

			// Some logging
			System.out.println("Mime Type: " + mimeType);
			System.out.println("Artifact: " + artifact);
			System.out.println("URL: " + url);

			// Test the environment
			if (artifact == null) {
				errorMessage = "The artifact in the http session was null";
			}
			if (mimeType == null) {
				errorMessage = "The mime type in the http session was null";
			}
			if (url == null) {
				errorMessage = "The url in the http session was null";
			}

			documentBytes = null;
			samlAssertionBytes = null;
			samlAttributes = null;
			signersIdentity = null;
			signatureMethod = null;

			// Download and save file if everything is ok so far
			if (errorMessage == null) {

				// Locate Web Service

				// Get the SAML Assertion
				System.out.println("Calling web service to retrieve signed document...");
				String samlAssertion = signatureservice.retrieveSaml(artifact);
				System.out.println("Saml Assertion recieved");
				if (samlAssertion == null || samlAssertion.length() == 0) {
					errorMessage = "SAML assertion was empty";
				}

				if (errorMessage == null) {

					// We now have the SAML Assertion with the signed document.
					// This may be parsed in two ways.
					// You should choose either strategy 1 or strategy 2.

					//
					// Strategy 1; parse manually
					//
					samlAssertionBytes = Base64.decodeBase64(samlAssertion.getBytes("utf-8"));
					System.out.println("Saml Assertion decoded");
					//
					// Add your own code here to read the xml in
					// samlAssertionBytes
					//

					//
					// Strategy 2; parse using the SAML libraries provided in
					// this client kit
					//

					// Create the saml facade
					// // Properties configuration = new Properties();
					// // configuration.setProperty("debug", "false");
					// // configuration.setProperty("trustkeystore.file",
					// "C:/signicat/config/signicat-client-keystore");
					// // configuration.setProperty("trustkeystore.password",
					// "changeit");
					// // configuration.setProperty(
					// "asserting.party.certificate.subject.dn",
					// "CN=test.kantega.no/id, OU=Kantega AS, O=Kantega AS, L=Trondheim, ST=Norway, C=NO"
					// );
					// //
					// // // Uncomment this if you want to use the special
					// classloader
					// // // configuration.setProperty("classpath",
					// "C:/signicat/lib");
					// //
					// //// SamlFacadeFactory factory = new
					// SamlFacadeFactory(configuration);
					// //// SamlFacade samlFacade = factory.createSamlFacade();
					// // System.out.println("Saml facade created");
					// //
					// // // Parse the unsigned saml assertion
					// // try {
					// // samlAttributes =
					// samlFacade.readUnsignedAssertion(samlAssertion, new
					// URL(request.getRequestURL().toString()));
					// // System.out.println("Saml assertion parsed");
					// //
					// // //
					// // // The attributes and other values are now stored in
					// the samlAttributes map
					// // //
					// //
					// // // The identity of the signer is stored in the
					// attribute map. You should validate that
					// // // the signer actually is the one you belive it is.
					// // // You may use the saml.subject.nameidentifier.name
					// attribute or any of the other attributes to do this.
					// // signersIdentity = (String)
					// samlAttributes.get("saml.subject.nameidentifier.name");
					// //
					// // // The method used to sign the document is also
					// available...
					// // signatureMethod = (String)
					// samlAttributes.get("saml.authentication.authmethod");
					// //
					// // // The signed document is stored in the attribute that
					// ends with "document.data"
					// // Iterator i = samlAttributes.keySet().iterator();
					// // while (i.hasNext()) {
					// // String key = (String) i.next();
					// // if (key.endsWith("document.data")) {
					// // // The attribute is stored as a list, since
					// technically attributes may have more than one element
					// // List documentDataList = (List)
					// samlAttributes.get(key);
					// // String documentDataBase64 = (String)
					// documentDataList.get(0);
					// //
					// // // The document is base 64 encoded, so we have to
					// decode it
					// // documentBytes =
					// Base64.decodeBase64(documentDataBase64.
					// getBytes("utf-8"));
					// // }
					// // }
					// } catch (ScSecurityException e) {
					// errorMessage = "Failed while reading SAML Assertion";
					// }
				}
			}
		} catch (Exception e) {
			errorMessage = "Exception: " + e.getMessage() + " (" + e.getClass().getName() + "). Stacktrace was printed to standard err.";
			e.printStackTrace(System.err);
		}

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
