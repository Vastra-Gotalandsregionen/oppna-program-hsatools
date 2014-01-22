/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.kivtools.hriv.presentation;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalRegistrationException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalSigningException;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SearchService;
import se.vgregion.kivtools.search.svc.registration.CitizenRepository;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdvalVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdvalVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;
import se.vgregion.kivtools.search.util.EncryptionUtil;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;
import se.vgregion.signera.signature._1.SignatureEnvelope;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller class for the process when a citizen registers on a unit.
 */
public class RegisterOnUnitController implements Serializable {

    private static final long serialVersionUID = 1L;
    private String signatureServiceEndpoint;
    private String serviceUrl;
    private String externalApplicationURL;
    private final Log logger = LogFactory.getLog(this.getClass());
    private VardvalService vardValService;
    private SearchService searchService;
    private CitizenRepository citizenRepository;
    private final ResourceBundle bundle = ResourceBundle.getBundle("messagesVGR");
    private String ticketUrl = null;

    /**
     * Initializes the webservice endpoint for the signature service.
     */
    public void initEndpoint() {
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
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
     * Setter for the external application URL for the signature service to redirect back to after signing the
     * registration.
     *
     * @param externalApplicationURL The external application URL for the signature service to redirect back to after signing the
     *                               registration.
     */
    public void setExternalApplicationURL(String externalApplicationURL) {
        this.externalApplicationURL = externalApplicationURL;
    }

    /**
     * Retrieves the citizens current unit selection.
     *
     * @param externalContext The JSF external context.
     * @return A populated VardvalInfo object.
     * @throws VardvalRegistrationException If there is a problem looking up the citizens current unit selection.
     */
    public VardvalInfo getCurrentUnitRegistrationInformation(ExternalContext externalContext)
            throws VardvalRegistrationException {
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
            throw new VardvalRegistrationException(sfe);
        } catch (KivException e) {
            throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"), e);
        } catch (IVårdvalServiceGetVårdvalVårdvalServiceErrorFaultFaultMessage e) {
            throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"), e);
        }
        vardvalInfo.setName(name);
        vardvalInfo.setSsn(decryptedSsn);
        return vardvalInfo;
    }

    /**
     * Prepare "confirm step" in registration process.
     *
     * @param externalContext The JSF external context.
     * @param selectedUnitId  The hsaIdentity of the selected unit to register on.
     * @return A populated VardvalInfo object.
     * @throws VardvalRegistrationException If there is a problem looking up the citizens current unit selection.
     */
    public VardvalInfo getUnitRegistrationInformation(ExternalContext externalContext, String selectedUnitId)
            throws VardvalRegistrationException {
        VardvalInfo vardvalInfo = getCurrentUnitRegistrationInformation(externalContext);

        try {
            // Lookup unit names in order to show real names instead of hsa ids
            Unit selectedUnit = searchService.getUnitByHsaId(selectedUnitId);
            if (selectedUnit != null) {
                vardvalInfo.setSelectedUnitName(selectedUnit.getName());
            }
        } catch (KivException e) {
            throw new VardvalRegistrationException(bundle.getString("registrationInvalidUnit"), e);
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
     * @param vardvalInfo     A populated VardvalInfo object containing information on the citizen and the selected unit.
     * @param externalContext The JSF external context.
     */
    public void preCommitRegistrationOnUnit(VardvalInfo vardvalInfo, ExternalContext externalContext) {

        String documentText = bundle.getString("registrationDocumentText");
        String registrationData =
                MessageFormat.format(documentText, vardvalInfo.getName(), vardvalInfo.getSsn(),
                        vardvalInfo.getSelectedUnitName(), vardvalInfo.getSelectedUnitId(),
                        TimeUtil.getCurrentTimeFormatted(DateTimeFormat.NORMAL_TIME));

        String mimeType = "text/plain";

        // Stores signatureserviceUrl and mimetype in session so
        externalContext.getSessionMap().put("signatureservice.url", signatureServiceEndpoint);
        externalContext.getSessionMap().put("mimeType", mimeType);
        externalContext.getSessionMap().put("ssn", vardvalInfo.getSsn());
        externalContext.getSessionMap().put("name", vardvalInfo.getName());
        externalContext.getSessionMap().put("selectedUnitId", vardvalInfo.getSelectedUnitId());
        externalContext.getSessionMap().put("selectedUnitName", vardvalInfo.getSelectedUnitName());

        Map<String, String> vardvalInfoMap = new HashMap<String, String>();
        vardvalInfoMap.put("mimeType", mimeType);
        vardvalInfoMap.put("ssn", vardvalInfo.getSsn());
        vardvalInfoMap.put("name", vardvalInfo.getName());
        vardvalInfoMap.put("selectedUnitId", vardvalInfo.getSelectedUnitId());
        vardvalInfoMap.put("selectedUnitName", vardvalInfo.getSelectedUnitName());

        externalContext.getApplicationMap().put(vardvalInfo.getSsn(), vardvalInfoMap);

        // Construct postback url.
        String hrivHost = "hittavard.vgregion.se";

        String cipherTextStringBase64Encoded = EncryptionUtil.encrypt(vardvalInfo.getSsn());
        String signature = EncryptionUtil.createSignature(cipherTextStringBase64Encoded);

        String data = "ssn=" + cipherTextStringBase64Encoded + "&sign="+ signature;
        String encryptedData = EncryptionUtil.encrypt(data);
        String targetUrl = "http://" + hrivHost + "/hriv/HRIV.registrationOnUnitPostbackSign-flow.flow?data="
                        + encryptedData;

        try {
            String ticket = retrieveTicketFromSigningService();
            externalContext.getRequestMap().put("ticket", ticket);
        } catch (VardvalException e) {
            logger.error(e.getMessage(), e); // We don't do more for now since it might work without ticket.
        }

        externalContext.getRequestMap().put("tbs", registrationData);
        externalContext.getRequestMap().put("submitUri", targetUrl);

    }

    String retrieveTicketFromSigningService() throws VardvalException {
        org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();

        try {
            HttpResponse response = httpClient.execute(new HttpGet(ticketUrl));

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new VardvalException("Bad response from Signing Service when requesting ticket. Status code = "
                        + statusCode + ".");
            }

            InputStream content = response.getEntity().getContent();

            String ticket = IOUtils.toString(content);

            return ticket;
        } catch (IOException e) {
            throw new VardvalException(e);
        }
    }

    public void postbackSignRegistrationOnUnit(ExternalContext externalContext) throws VardvalException {

        HttpServletRequest servletRequest = (HttpServletRequest) externalContext.getNativeRequest();

        VardvalInfo vardvalInfo;

        // Extract the parameters from the encrypted chunk
        String encryptedData = externalContext.getRequestParameterMap().get("data");
        String data = EncryptionUtil.decrypt(encryptedData);
        String[] keyValuePairs = data.split("&");
        String encryptedSsn = keyValuePairs[0].split("=")[1];
        String signature = keyValuePairs[1].split("=")[1];

        // Validate that the cipher hasn't been changed with the signature
        boolean isOk = EncryptionUtil.verify(encryptedSsn, signature);
        if (!isOk) {
            throw new VardvalSigningException("The ssn parameter did not validate against the signature.");
        }

        String ssnFromRequest = EncryptionUtil.decrypt(encryptedSsn);

        try {
            Map<String, String> vardvalInfoMap =
                    (Map<String, String>) externalContext.getApplicationMap().get(ssnFromRequest);
            externalContext.getApplicationMap().remove(ssnFromRequest);

            if (vardvalInfoMap == null) {
                throw new VardvalException();
            }
            String ssn = vardvalInfoMap.get("ssn");
            String selectedUnitId = vardvalInfoMap.get("selectedUnitId");
            String selectedUnitName = vardvalInfoMap.get("selectedUnitName");
            String name = vardvalInfoMap.get("name");

            ServletInputStream inputStream =
                    servletRequest.getInputStream();

            JAXBContext jaxbContext = JAXBContext.newInstance(SignatureEnvelope.class);
            SignatureEnvelope signatureEnvelope =
                    (SignatureEnvelope) jaxbContext.createUnmarshaller().unmarshal(inputStream);

            try {
                vardvalInfo =
                        vardValService.setVardval(ssn, selectedUnitId, signatureEnvelope.getSignature()
                                .getBytes());
                vardvalInfo.setSsn(ssn);
                vardvalInfo.setName(name);
                vardvalInfo.setSelectedUnitName(selectedUnitName);
            } catch (IVårdvalServiceSetVårdvalVårdvalServiceErrorFaultFaultMessage e) {
                throw new VardvalSigningException(e);
            }
        } catch (JAXBException e1) {
            throw new VardvalSigningException(e1);
        } catch (IOException e1) {
            throw new VardvalSigningException(e1);
        } catch (NullPointerException e1) {
            throw new VardvalSigningException(e1);
        }
    }

    /**
     * Returning from signature service (ie Signicat). Process Saml response and set registration in Vårdval
     * system.
     *
     * @param externalContext The JSF external context.
     * @return A populated VardvalInfo object containing information on the current and upcoming unit registration.
     * @throws VardvalException If there is a problem registering the citizens unit selection or during the signing phase of the
     *                          process.
     */
    public VardvalInfo postCommitRegistrationOnUnit(ExternalContext externalContext) throws VardvalException {

        VardvalInfo vardvalInfo = new VardvalInfo();

        SharedAttributeMap sessionMap = externalContext.getSessionMap();
        String ssn = sessionMap.getString("ssn");
        String selectedUnitName = sessionMap.getString("selectedUnitName");
        String name = sessionMap.getString("name");

        vardvalInfo.setSsn(ssn);
        vardvalInfo.setName(name);
        vardvalInfo.setSelectedUnitName(selectedUnitName);

        return vardvalInfo;
    }

    private String encodeTargetUrl(String targetUrl) {
        return StringUtil.urlEncode(targetUrl, "UTF-8");
    }

    private byte[] encodeRegistrationData(String registrationData) {
        return Base64.encodeBase64(StringUtil.getBytes(registrationData, "UTF-8"));
    }

}
