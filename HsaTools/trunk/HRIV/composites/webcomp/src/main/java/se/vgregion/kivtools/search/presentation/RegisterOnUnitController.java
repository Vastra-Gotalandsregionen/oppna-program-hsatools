package se.vgregion.kivtools.search.presentation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.executor.jsf.JsfExternalContext;

import se.vgregion.kivtools.search.presentation.exceptions.UnsuccessfullRegistrationException;
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

public class RegisterOnUnitController implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final String SIGNATURE_SERVICE_URL = "https://test.signicat.com/signatureservice/services/signatureservice";
  // Test -> prod: test -> id
  private static final String SERVICE_URL = "https://test.signicat.com/std/method/vgr?method=sign";
  // Test -> prod: test -> id

  private Log logger = LogFactory.getLog(this.getClass());
  private VardvalService vardValService;
  private SearchService searchService;
  private SignatureEndpointImplService signatureEndpointImplService = new SignatureEndpointImplService();
  private SignatureEndpointImpl signatureservice = signatureEndpointImplService.getSignatureservice();
  private CitizenRepository citizenRepository;
  private final ResourceBundle bundle = ResourceBundle.getBundle("messagesVGR");

  public void setCitizenRepository(CitizenRepository citizenRepository) {
    this.citizenRepository = citizenRepository;
  }

  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  public void setVardValService(VardvalService vardValService) {
    this.vardValService = vardValService;
  }

  /**
   * Prepare "confirm step" in registration process.
   * 
   * @param externalContext
   * @param selectedUnitId
   * @return
   * @throws Exception
   */
  public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId) throws Exception {

    String ssnEncodedEncrypted = externalContext.getRequestParameterMap().get("iv-user");

    String decryptedSsn = getDecryptedSsn(Base64.decodeBase64(ssnEncodedEncrypted.getBytes()));

    // FIXME Remove
    if (decryptedSsn == null) {
      decryptedSsn = "188803099368";
    }

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
    } catch (Exception e) {
      e.printStackTrace();
      throw new UnsuccessfullRegistrationException(bundle.getString("registrationInvalidUnit"));
    }
    vardvalInfo.setName(name);
    vardvalInfo.setSsn(decryptedSsn);
    vardvalInfo.setSelectedUnitId(selectedUnitId);
    return vardvalInfo;
  }

  protected String getDecryptedSsn(byte[] encryptedByteArray) {
    byte[] preSharedKey = "ACME1234ACME1234QWERT123".getBytes();
    SecretKey aesKey = new SecretKeySpec(preSharedKey, "DESede");
    byte[] ssnBytes = null;
    try {
      Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
      String initialVector = "vardval0";
      IvParameterSpec ivs = new IvParameterSpec(initialVector.getBytes());
      cipher.init(Cipher.DECRYPT_MODE, aesKey, ivs);
      ssnBytes = cipher.doFinal(encryptedByteArray);
    } catch (InvalidKeyException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (BadPaddingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvalidAlgorithmParameterException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Decrypt the cleartext
    String ssn = new String(ssnBytes);
    return ssn;
  }

  protected String getBase64DecodedSsn(String base64EncodedString) {
    byte[] base64Decoded = null;
    String base64DecodedString = null;
    base64Decoded = Base64.decodeBase64(base64EncodedString.getBytes());
    base64DecodedString = new String(base64Decoded);
    return base64DecodedString;
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
    externalContext.getSessionMap().put("signatureservice.url", SIGNATURE_SERVICE_URL);
    externalContext.getSessionMap().put("mimeType", mimeType);
    externalContext.getSessionMap().put("ssn", vardvalInfo.getSsn());
    externalContext.getSessionMap().put("name", vardvalInfo.getName());
    externalContext.getSessionMap().put("selectedUnitId", vardvalInfo.getSelectedUnitId());
    externalContext.getSessionMap().put("selectedUnitName", vardvalInfo.getSelectedUnitName());

    String artifact = signatureservice.registerDocument(new String(base64encoded), mimeType, documentDescription);
    externalContext.getSessionMap().put("artifact", artifact);

    // Construct redirect url
    JsfExternalContext jsfExternalContext = (JsfExternalContext) externalContext;
    HttpServletRequest httpServletRequest = (HttpServletRequest) jsfExternalContext.getFacesContext().getExternalContext().getRequest();
    String urlToThisPage = httpServletRequest.getRequestURL().toString();

    String targetUrl = urlToThisPage.substring(0, urlToThisPage.lastIndexOf("/") + 1) + "confirmationRegistrationChanges.jsf?_flowId=HRIV.registrationOnUnitPostSign-flow";

    // Redirect user to Signicat
    redirectUrl = SERVICE_URL + "&documentArtifact=" + artifact + "&target=" + encodeTargetUrl(targetUrl);
    return redirectUrl;
  }

  /**
   * Returning from signature service (ie Signicat). Process Saml response and set registration in Vårdval system.
   * 
   * @param externalContext
   * @return
   * @throws UnsuccessfullRegistrationException
   */
  public VardvalInfo postCommitRegistrationOnUnit(ExternalContext externalContext) throws UnsuccessfullRegistrationException {

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
        throw new UnsuccessfullRegistrationException(e.getMessage());
      }
    } else {
      throw new UnsuccessfullRegistrationException(bundle.getString("registrationSigningFailed"));
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
