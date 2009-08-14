package se.vgregion.kivtools.search.presentation;

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

import se.vgregion.kivtools.search.presentation.exceptions.VardvalRegistrationException;
import se.vgregion.kivtools.search.presentation.exceptions.VardvalSigningException;
import se.vgregion.kivtools.search.presentation.types.SigningInformation;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.Constants;
import se.vgregion.kivtools.search.svc.registration.CitizenRepository;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImpl;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImplService;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;
import se.vgregion.kivtools.search.util.EncryptionUtil;

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

  public void initEndpoint() {
    if (!"".equalsIgnoreCase(signatureServiceEndpoint)) {
      BindingProvider bindingProvider = (BindingProvider) signatureservice;
      Map<String, Object> requestContext = bindingProvider.getRequestContext();
      requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, signatureServiceEndpoint);
    }
  }

  public void setCitizenRepository(CitizenRepository citizenRepository) {
    this.citizenRepository = citizenRepository;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setVardValService(VardvalService vardValService) {
    this.vardValService = vardValService;
  }

  public void setSignatureServiceEndpoint(String signatureServiceEndpoint) {
    this.signatureServiceEndpoint = signatureServiceEndpoint;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public void setExternalApplicationURL(String externalApplicationURL) {
    this.externalApplicationURL = externalApplicationURL;
  }

  /**
   * Prepare "confirm step" in registration process.
   * 
   * @param externalContext
   * @param selectedUnitId
   * @return
   * @throws VardvalRegistrationException If there is a problem looking up the citizens current unit selection.
   */
  public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId) throws VardvalRegistrationException {

    String ssnEncodedEncrypted = externalContext.getRequestParameterMap().get("iv-user");

    String decryptedSsn = EncryptionUtil.decrypt(ssnEncodedEncrypted, "ACME1234ACME1234QWERT123");

    // Get name from LDAP
    String name = getNameFromSsn(decryptedSsn);

    // Request information about the listing from Vårdvalsystem
    VardvalInfo vardvalInfo = new VardvalInfo();

    try {
      vardvalInfo = vardValService.getVardval(decryptedSsn);

      // Lookup unit names in order to show real names instead of hsa ids
      Unit selectedUnit = searchService.getUnitByHsaId(selectedUnitId);
      if (selectedUnit != null) {
        vardvalInfo.setSelectedUnitName(selectedUnit.getName());
      }
      Unit currentUnit = null;
      Unit upcomingUnit = null;
      if (vardvalInfo.getCurrentHsaId() != null) {
        currentUnit = searchService.getUnitByHsaId(vardvalInfo.getCurrentHsaId());
      }
      if (vardvalInfo.getUpcomingHsaId() != null) {
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
      externalContext.getSessionMap().put("selectedUnitId", selectedUnitId);
      throw new VardvalRegistrationException(sfe.getMessage());
    } catch (Exception e) {
      externalContext.getSessionMap().put("selectedUnitId", selectedUnitId);
      throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"));
    }
    vardvalInfo.setName(name);
    vardvalInfo.setSsn(decryptedSsn);
    vardvalInfo.setSelectedUnitId(selectedUnitId);
    return vardvalInfo;
  }

  private String getNameFromSsn(String ssn) {
    return citizenRepository.getCitizenNameFromSsn(ssn);
  }

  /**
   * Prepare signature request.
   * 
   * @param vardvalInfo
   * @param externalContext
   * @return redirectUrl The url to redirect to
   * @throws UnsupportedEncodingException
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
    String targetUrl = externalApplicationURL + "/confirmationRegistrationChanges.jsf?_flowId=HRIV.registrationOnUnitPostSign-flow";

    // Redirect user to Signicat
    redirectUrl = serviceUrl + "&documentArtifact=" + artifact + "&target=" + encodeTargetUrl(targetUrl);
    return redirectUrl;
  }

  /**
   * Returning from signature service (ie Signicat). Process Saml response and set registration in Vårdval system.
   * 
   * @param externalContext
   * @return
   * @throws VardvalRegistrationException If there is a problem registering the citizens unit selection.
   * @throws VardvalSigningException
   */
  public VardvalInfo postCommitRegistrationOnUnit(ExternalContext externalContext) throws VardvalRegistrationException, VardvalSigningException {

    SigningInformation signingInformation = handleSamlResponse(externalContext);

    VardvalInfo vardvalInfo = new VardvalInfo();

    SharedAttributeMap sessionMap = externalContext.getSessionMap();
    String ssn = sessionMap.getString("ssn");
    String selectedUnitId = sessionMap.getString("selectedUnitId");
    String selectedUnitName = sessionMap.getString("selectedUnitName");
    String name = sessionMap.getString("name");

    // Set new registration if same ssn etc
    if (signingInformation.getNationalId() != null && signingInformation.getNationalId().equals(ssn) && signingInformation.getSamlResponse() != null) {
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
    try {
      return Base64.encodeBase64(registrationData.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      // Should not happen
      throw new RuntimeException(e);
    }
  }

  private SigningInformation handleSamlResponse(ExternalContext externalContext) {
    String errorMessage = null;
    byte[] samlAssertionBytes = new byte[0];
    SigningInformation signingInformation = null;

    try {
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
        if (samlAssertion == null || samlAssertion.length() == 0) {
          errorMessage = "SAML assertion was empty";
        }

        if (errorMessage == null) {
          samlAssertionBytes = Base64.decodeBase64(samlAssertion.getBytes("UTF-8"));
          String samlAssertionString = new String(samlAssertionBytes, "UTF-8");
          signingInformation = SamlResponseHelper.getSigningInformation(samlAssertionString);
        }
      }
    } catch (Exception e) {
      logger.error(errorMessage, e);
    }
    return signingInformation;
  }
}
