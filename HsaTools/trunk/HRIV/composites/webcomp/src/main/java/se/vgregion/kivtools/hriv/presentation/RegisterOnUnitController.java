package se.vgregion.kivtools.hriv.presentation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;

import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalRegistrationException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalSigningException;
import se.vgregion.kivtools.hriv.presentation.types.SigningInformation;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.registration.CitizenRepository;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImpl;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImplService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;
import se.vgregion.kivtools.search.util.EncryptionUtil;
import se.vgregion.kivtools.util.StringUtil;

/**
 * Controller class for the process when a citizen registers on a unit.
 */
public class RegisterOnUnitController implements Serializable {

  private static final long serialVersionUID = 1L;
  private String signatureServiceEndpoint;
  private String serviceUrl;
  private String externalApplicationURL;
  private Log logger = LogFactory.getLog(this.getClass());
  private VardvalService vardValService;
  private SearchService searchService;
  private SignatureEndpointImplService signatureEndpointImplService = new SignatureEndpointImplService();
  private SignatureEndpointImpl signatureservice = signatureEndpointImplService.getSignatureservice();
  private CitizenRepository citizenRepository;
  private final ResourceBundle bundle = ResourceBundle.getBundle("messagesVGR");

  /**
   * Initializes the webservice endpoint for the signature service.
   */
  public void initEndpoint() {
    if (!StringUtil.isEmpty(signatureServiceEndpoint)) {
      BindingProvider bindingProvider = (BindingProvider) signatureservice;
      Map<String, Object> requestContext = bindingProvider.getRequestContext();
      requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, signatureServiceEndpoint);
    }
  }

  /**
   * Setter for the CitizenRepository to use.
   * 
   * @param citizenRepository The CitizenRepository to use.
   */
  public void setCitizenRepository(CitizenRepository citizenRepository) {
    this.citizenRepository = citizenRepository;
  }

  /**
   * Setter for the SearchService to use.
   * 
   * @param searchService The SearchService to use.
   */
  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  /**
   * Setter for the VardvalService to use.
   * 
   * @param vardValService The VardvalService to use.
   */
  public void setVardValService(VardvalService vardValService) {
    this.vardValService = vardValService;
  }

  /**
   * Setter for the signature service endpoint to use.
   * 
   * @param signatureServiceEndpoint The signature service endpoint to use.
   */
  public void setSignatureServiceEndpoint(String signatureServiceEndpoint) {
    this.signatureServiceEndpoint = signatureServiceEndpoint;
  }

  /**
   * Setter for the service URL to redirect to for signing the registration.
   * 
   * @param serviceUrl The service URL to redirect to for signing the registration.
   */
  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  /**
   * Setter for the external application URL for the signature service to redirect back to after signing the registration.
   * 
   * @param externalApplicationURL The external application URL for the signature service to redirect back to after signing the registration.
   */
  public void setExternalApplicationURL(String externalApplicationURL) {
    this.externalApplicationURL = externalApplicationURL;
  }

  protected void setSignatureService(SignatureEndpointImpl signatureService) {
    this.signatureservice = signatureService;
  }

  /**
   * Retrieves the citizens current unit selection.
   * 
   * @param externalContext The JSF external context.
   * @return A populated VardvalInfo object.
   * @throws VardvalRegistrationException If there is a problem looking up the citizens current unit selection.
   */
  public VardvalInfo getCurrentUnitRegistrationInformation(ExternalContext externalContext) throws VardvalRegistrationException {
    String ssnEncodedEncrypted = externalContext.getRequestParameterMap().get("iv-user");

    String decryptedSsn = EncryptionUtil.decrypt(ssnEncodedEncrypted);

    // Get name from LDAP
    String name = getNameFromSsn(decryptedSsn);

    // Request information about the listing from Vårdvalsystem
    VardvalInfo vardvalInfo = new VardvalInfo();

    try {
      vardvalInfo = vardValService.getVardval(decryptedSsn);

      Unit currentUnit = null;
      Unit upcomingUnit = null;
      if (!StringUtil.isEmpty(vardvalInfo.getCurrentHsaId())) {
        currentUnit = searchService.getUnitByHsaId(vardvalInfo.getCurrentHsaId());
      }
      if (!StringUtil.isEmpty(vardvalInfo.getUpcomingHsaId())) {
        upcomingUnit = searchService.getUnitByHsaId(vardvalInfo.getUpcomingHsaId());
      }

      // Set values in DTO
      if (currentUnit != null) {
        vardvalInfo.setCurrentUnitName(currentUnit.getName());
      }
      if (upcomingUnit != null) {
        vardvalInfo.setUpcomingUnitName(upcomingUnit.getName());
      }
    } catch (SOAPFaultException sfe) {
      throw new VardvalRegistrationException(sfe.getMessage());
    } catch (KivException e) {
      throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"));
    } catch (IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage e) {
      throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"));
    }
    vardvalInfo.setName(name);
    vardvalInfo.setSsn(decryptedSsn);
    return vardvalInfo;
  }

  /**
   * Prepare "confirm step" in registration process.
   * 
   * @param externalContext The JSF external context.
   * @param selectedUnitId The hsaIdentity of the selected unit to register on.
   * @return A populated VardvalInfo object.
   * @throws VardvalRegistrationException If there is a problem looking up the citizens current unit selection.
   */
  public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId) throws VardvalRegistrationException {
    VardvalInfo vardvalInfo = getCurrentUnitRegistrationInformation(externalContext);

    try {
      // Lookup unit names in order to show real names instead of hsa ids
      Unit selectedUnit = searchService.getUnitByHsaId(selectedUnitId);
      if (selectedUnit != null) {
        vardvalInfo.setSelectedUnitName(selectedUnit.getName());
      }
    } catch (KivException e) {
      throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"));
    } finally {
      externalContext.getSessionMap().put("selectedUnitId", selectedUnitId);
    }

    vardvalInfo.setSelectedUnitId(selectedUnitId);

    return vardvalInfo;
  }

  private String getNameFromSsn(String ssn) {
    return citizenRepository.getCitizenNameFromSsn(ssn);
  }

  /**
   * Prepare signature request.
   * 
   * @param vardvalInfo A populated VardvalInfo object containing information on the citizen and the selected unit.
   * @param externalContext The JSF external context.
   * @return redirectUrl The url to redirect to.
   */
  public String preCommitRegistrationOnUnit(VardvalInfo vardvalInfo, ExternalContext externalContext) {
    String redirectUrl = "";
    Date date = new Date();
    String documentText = bundle.getString("registrationDocumentText");
    String registrationData = MessageFormat.format(documentText, vardvalInfo.getName(), vardvalInfo.getSsn(), vardvalInfo.getSelectedUnitName(), vardvalInfo.getSelectedUnitId(), Constants
        .formatDateToNormalTime(date));

    byte[] base64encoded = encodeRegistrationData(registrationData);
    String mimeType = "text/plain";
    String documentDescription = bundle.getString("registrationDocumentDescription");

    // Stores signatureserviceUrl and mimetype in session so
    externalContext.getSessionMap().put("signatureservice.url", signatureServiceEndpoint);
    externalContext.getSessionMap().put("mimeType", mimeType);
    externalContext.getSessionMap().put("ssn", vardvalInfo.getSsn());
    externalContext.getSessionMap().put("name", vardvalInfo.getName());
    externalContext.getSessionMap().put("selectedUnitId", vardvalInfo.getSelectedUnitId());
    externalContext.getSessionMap().put("selectedUnitName", vardvalInfo.getSelectedUnitName());

    String artifact = signatureservice.registerDocument(new String(base64encoded), mimeType, documentDescription);
    externalContext.getSessionMap().put("artifact", artifact);

    // Construct redirect url
    String targetUrl = externalApplicationURL + "/HRIV.registrationOnUnitPostSign-flow.flow";

    // Redirect user to Signicat
    redirectUrl = serviceUrl + "&documentArtifact=" + artifact + "&target=" + encodeTargetUrl(targetUrl);
    return redirectUrl;
  }

  /**
   * Returning from signature service (ie Signicat). Process Saml response and set registration in Vårdval system.
   * 
   * @param externalContext The JSF external context.
   * @return A populated VardvalInfo object containing information on the current and upcoming unit registration.
   * @throws VardvalException If there is a problem registering the citizens unit selection or during the signing phase of the process.
   */
  public VardvalInfo postCommitRegistrationOnUnit(ExternalContext externalContext) throws VardvalException {

    SigningInformation signingInformation = handleSamlResponse(externalContext);

    VardvalInfo vardvalInfo = new VardvalInfo();

    SharedAttributeMap sessionMap = externalContext.getSessionMap();
    String ssn = sessionMap.getString("ssn");
    String selectedUnitId = sessionMap.getString("selectedUnitId");
    String selectedUnitName = sessionMap.getString("selectedUnitName");
    String name = sessionMap.getString("name");

    // Set new registration if same ssn etc
    if (signingInformation.getNationalId() != null && signingInformation.getNationalId().equals(ssn)) {
      try {
        vardvalInfo = vardValService.setVardval(ssn, selectedUnitId, signingInformation.getSamlResponse().getBytes());
        vardvalInfo.setSsn(ssn);
        vardvalInfo.setName(name);
        vardvalInfo.setSelectedUnitName(selectedUnitName);
      } catch (IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage e) {
        throw new VardvalRegistrationException(e.getMessage());
      }
    } else {
      throw new VardvalSigningException();
    }
    return vardvalInfo;
  }

  private String encodeTargetUrl(String targetUrl) {
    try {
      return URLEncoder.encode(targetUrl, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Should not happen
      throw new RuntimeException(e);
    }
  }

  private byte[] encodeRegistrationData(String registrationData) {
		return Base64.encodeBase64(StringUtil.getBytes(registrationData, "UTF-8"));
  }

  private SigningInformation handleSamlResponse(ExternalContext externalContext) {
    String errorMessage = null;
    byte[] samlAssertionBytes = new byte[0];
    SigningInformation signingInformation = null;

    // Reads information stored in the session
    SharedAttributeMap sessionMap = externalContext.getSessionMap();
    String artifact = (String) sessionMap.get("artifact");
    String url = (String) sessionMap.get("signatureservice.url");
    String mimeType = (String) sessionMap.get("mimeType");

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

    samlAssertionBytes = null;

    // Download and save file if everything is ok so far
    if (errorMessage == null) {
      // Get the SAML Assertion
      String samlAssertion = signatureservice.retrieveSaml(artifact);
      if (StringUtil.isEmpty(samlAssertion)) {
        errorMessage = "SAML assertion was empty";
      }

      if (errorMessage == null) {
        String samlAssertionString;
				samlAssertionBytes = Base64.decodeBase64(StringUtil.getBytes(samlAssertion, "UTF-8"));
				samlAssertionString = StringUtil.getString(samlAssertionBytes, "UTF-8");
        signingInformation = SamlResponseHelper.getSigningInformation(samlAssertionString);
      } else {
        logger.error(errorMessage);
      }
    } else {
      logger.error(errorMessage);
    }
    return signingInformation;
  }
}
