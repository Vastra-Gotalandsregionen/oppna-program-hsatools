/**
 * Copyright 2009 Västra Götalandsregionen
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
 */
package se.vgregion.kivtools.hriv.presentation;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalRegistrationException;
import se.vgregion.kivtools.hriv.presentation.exceptions.VardvalSigningException;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.registration.CitizenRepository;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.domain.vardval.IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage;
import se.vgregion.kivtools.search.svc.ws.signicat.signature.SignatureEndpointImpl;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalInfo;
import se.vgregion.kivtools.search.svc.ws.vardval.VardvalService;
import se.vgregion.kivtools.search.util.EncryptionUtil;
import se.vgregion.kivtools.util.StringUtil;

public class RegisterOnUnitControllerTest {

  RegisterOnUnitController registerOnUnitController;
  private final String SELECTED_UNIT_ID = "SE2321000131-E000000001302";
  private final String CURRENT_UNIT_ID = "SE2321000131-E000000001303";
  private final String UPCOMING_UNIT_ID = "SE2321000131-E000000001304";
  private final String SELECTED_UNIT_NAME = "Selected unit name";
  private final String CURRENT_UNIT_NAME = "Current unit name";
  private final String UPCOMING_UNIT_NAME = "Upcoming unit name";

  private final String SSN = "197702200101";
  private final Unit selectedUnit = new Unit();
  private final Unit currentUnit = new Unit();
  private final Unit upcomingUnit = new Unit();
  private MockServletContext servletContext;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private ServletExternalContext externalContext;
  private CitizenRepositoryMock citizenRepository;
  private VardvalServiceMock vardvalService;
  private SearchServiceMock searchService;
  private SignatureEndpointImplMock signatureEndpointImpl;

  @Before
  public void setup() throws Exception {
    registerOnUnitController = new RegisterOnUnitController();
    selectedUnit.setHsaIdentity(SELECTED_UNIT_ID);
    selectedUnit.setName(SELECTED_UNIT_NAME);
    currentUnit.setHsaIdentity(CURRENT_UNIT_ID);
    currentUnit.setName(CURRENT_UNIT_NAME);
    upcomingUnit.setHsaIdentity(UPCOMING_UNIT_ID);
    upcomingUnit.setName(UPCOMING_UNIT_NAME);

    servletContext = new MockServletContext();
    request = new MockHttpServletRequest(servletContext);
    response = new MockHttpServletResponse();
    externalContext = new ServletExternalContext(servletContext, request, response);

    citizenRepository = new CitizenRepositoryMock();

    vardvalService = new VardvalServiceMock();
    searchService = new SearchServiceMock();
    signatureEndpointImpl = new SignatureEndpointImplMock();

    System.setProperty(EncryptionUtil.KEY_PROPERTY, "ACME1234ACME1234QWERT123");
  }

  @Test
  public void testInitEndpoint() {
    registerOnUnitController.initEndpoint();

    registerOnUnitController.setSignatureServiceEndpoint("http://localhost");
    registerOnUnitController.initEndpoint();
  }

  @Test
  public void testGetCurrentUnitRegistrationInformation() throws VardvalRegistrationException {
    try {
      registerOnUnitController.getCurrentUnitRegistrationInformation(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    request.setParameter("iv-user", "5V+qllX2lLqSyG1ctsNo4A==");
    registerOnUnitController.setCitizenRepository(citizenRepository);
    registerOnUnitController.setSearchService(searchService);
    registerOnUnitController.setVardValService(vardvalService);
    searchService.addExceptionToThrow(new KivException("Test"));
    VardvalInfo vardvalInfo = new VardvalInfo();
    vardvalInfo.setCurrentHsaId(CURRENT_UNIT_ID);
    vardvalInfo.setUpcomingHsaId(UPCOMING_UNIT_ID);
    this.vardvalService.setVardvalInfo(vardvalInfo);

    try {
      registerOnUnitController.getCurrentUnitRegistrationInformation(externalContext);
      fail("VardvalRegistrationException expected");
    } catch (VardvalRegistrationException e) {
      // Expected exception
    }

    searchService.clearExceptionsToThrow();
    VardvalInfo registrationInformation = registerOnUnitController.getCurrentUnitRegistrationInformation(externalContext);
    assertNotNull(registrationInformation);
    assertEquals("197407185656", registrationInformation.getSsn());
    assertNull(registrationInformation.getSelectedUnitId());
    assertNull(registrationInformation.getName());
  }

  @Test
  public void testGetUnitRegistrationInformation() throws VardvalRegistrationException {
    try {
      registerOnUnitController.getUnitRegistrationInformation(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    request.setParameter("iv-user", "5V+qllX2lLqSyG1ctsNo4A==");
    try {
      registerOnUnitController.getUnitRegistrationInformation(externalContext, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    registerOnUnitController.setCitizenRepository(citizenRepository);

    searchService.addExceptionToThrow(new KivException("Test"));

    registerOnUnitController.setSearchService(searchService);
    registerOnUnitController.setVardValService(vardvalService);
    try {
      registerOnUnitController.getUnitRegistrationInformation(externalContext, null);
      fail("VardvalRegistrationException expected");
    } catch (VardvalRegistrationException e) {
      // Expected exception
    }

    searchService.clearExceptionsToThrow();

    vardvalService.exceptionToThrow = new IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage("Test", null);
    try {
      registerOnUnitController.getUnitRegistrationInformation(externalContext, null);
      fail("VardvalRegistrationException expected");
    } catch (VardvalRegistrationException e) {
      // Expected exception
    }

    vardvalService.exceptionToThrow = null;

    VardvalInfo registrationInformation = registerOnUnitController.getUnitRegistrationInformation(externalContext, null);
    assertNotNull(registrationInformation);
    assertEquals("197407185656", registrationInformation.getSsn());
    assertNull(registrationInformation.getSelectedUnitId());
    assertNull(registrationInformation.getName());

    this.searchService.addUnit(selectedUnit);
    registrationInformation = registerOnUnitController.getUnitRegistrationInformation(externalContext, SELECTED_UNIT_ID);
    assertNotNull(registrationInformation);
    assertNotNull(registrationInformation.getSelectedUnitId());
    assertEquals(SELECTED_UNIT_ID, registrationInformation.getSelectedUnitId());
    assertEquals(SELECTED_UNIT_NAME, registrationInformation.getSelectedUnitName());
    assertNull(registrationInformation.getName());

    VardvalInfo vardvalInfo = new VardvalInfo();
    vardvalInfo.setCurrentHsaId(CURRENT_UNIT_ID);
    vardvalInfo.setUpcomingHsaId(UPCOMING_UNIT_ID);
    this.vardvalService.setVardvalInfo(vardvalInfo);

    this.searchService.addUnit(currentUnit);
    this.searchService.addUnit(upcomingUnit);
    registrationInformation = registerOnUnitController.getUnitRegistrationInformation(externalContext, SELECTED_UNIT_ID);
    assertNotNull(registrationInformation);
    assertNotNull(registrationInformation.getSelectedUnitId());
    assertEquals(SELECTED_UNIT_ID, registrationInformation.getSelectedUnitId());
    assertEquals(SELECTED_UNIT_NAME, registrationInformation.getSelectedUnitName());
    assertNull(registrationInformation.getName());

    this.vardvalService.setExceptionToThrow(new SOAPFaultException(new SOAPFaultMock()));
    try {
      registerOnUnitController.getUnitRegistrationInformation(externalContext, SELECTED_UNIT_ID);
      fail("VardvalRegistrationException expected");
    } catch (VardvalRegistrationException e) {
      // Expected exception
    }
  }

  @Test
  public void testPreCommitRegistrationOnUnit() {
    try {
      registerOnUnitController.preCommitRegistrationOnUnit(null, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    VardvalInfo vardvalInfo = new VardvalInfo();
    try {
      registerOnUnitController.preCommitRegistrationOnUnit(vardvalInfo, null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    registerOnUnitController.setExternalApplicationURL("http://localhost");
    registerOnUnitController.setServiceUrl("http://signicat.com");
    registerOnUnitController.setSignatureService(signatureEndpointImpl);
    String redirectUrl = registerOnUnitController.preCommitRegistrationOnUnit(vardvalInfo, externalContext);
    assertNotNull(redirectUrl);
    assertEquals("http://signicat.com&documentArtifact=dummy-artifact&target=http%3A%2F%2Flocalhost%2FHRIV.registrationOnUnitPostSign-flow.flow", redirectUrl);
  }

  @Test
  public void testPostCommitRegistrationOnUnit() throws VardvalException, UnsupportedEncodingException {
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(null);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    SharedAttributeMap sessionMap = externalContext.getSessionMap();
    sessionMap.put("artifact", "dummy-artifactid");
    sessionMap.put("signatureservice.url", "dummy-url");
    sessionMap.put("mimeType", "dummy/mimetype");
    registerOnUnitController.setSignatureService(signatureEndpointImpl);
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    signatureEndpointImpl.setSamlResponse(new String(Base64.encodeBase64(StringUtil.getBytes(getSamlResponseWithoutSsn(), "UTF-8"))));
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("VardvalSigningException expected");
    } catch (VardvalSigningException e) {
      // Expected exception
    }

    signatureEndpointImpl.setSamlResponse(new String(Base64.encodeBase64(StringUtil.getBytes(getCompleteSamlResponse(), "UTF-8"))));
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("VardvalSigningException expected");
    } catch (VardvalSigningException e) {
      // Expected exception
    }

    sessionMap.put("ssn", "188803099368");
    signatureEndpointImpl.setSamlResponse(new String(Base64.encodeBase64(StringUtil.getBytes(getCompleteSamlResponse(), "UTF-8"))));
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {
      // Expected exception
    }

    registerOnUnitController.setVardValService(vardvalService);
    registerOnUnitController.postCommitRegistrationOnUnit(externalContext);

    vardvalService.setExceptionToThrow(new IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage("", null));
    try {
      registerOnUnitController.postCommitRegistrationOnUnit(externalContext);
      fail("VardvalRegistrationException expected");
    } catch (VardvalRegistrationException e) {
      // Expected exception
    }
  }

  private String getCompleteSamlResponse() {
    return "<Response xmlns=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n" + " xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n"
        + " IssueInstant=\"2009-07-03T07:04:43.412Z\" MajorVersion=\"1\" MinorVersion=\"1\"\n" + "  ResponseID=\"cc3ddd5bde1abb52a7abc37feee1162e\">\n"
        + " <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" + " <ds:SignatureValue>dvIrlZe8cBvaNNu+paSgM9RuPBAnncj9pioi/HGLlitg9cGnoWXLMg==</ds:SignatureValue>\n"
        + "   <Assertion xmlns=\"urn:oasis:names:tc:SAML:1.0:assertion\"\n" + "     AssertionID=\"fc2575ebc739e358f0de661b1e35e82d\" IssueInstant=\"2009-07-03T07:04:43.412Z\"\n"
        + "     Issuer=\"test.signicat.com/std\" MajorVersion=\"1\" MinorVersion=\"1\">\n" + "      <Conditions NotBefore=\"2009-07-03T07:04:43.411Z\"\n"
        + "       NotOnOrAfter=\"2009-07-03T07:05:13.411Z\"></Conditions>\n" + "      <AuthenticationStatement\n" + "       AuthenticationInstant=\"2009-07-03T07:04:43.377Z\"\n"
        + "       AuthenticationMethod=\"urn:ksi:names:SAML:2.0:ac:BankID-SE\">\n" + "        <Subject>\n"
        + "          <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">188803099368\n" + "         </NameIdentifier>\n" + "          <SubjectConfirmation>\n"
        + "            <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n" + "         </SubjectConfirmation>\n" + "       </Subject>\n"
        + "     </AuthenticationStatement>\n" + "     <AttributeStatement>\n" + "       <Subject>\n" + "         <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">188803099368\n"
        + "          </NameIdentifier>\n" + "          <SubjectConfirmation>\n" + "           <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n"
        + "          </SubjectConfirmation>\n" + "       </Subject>\n" + "       <Attribute AttributeName=\"security-level\"\n" + "          AttributeNamespace=\"signicat\">\n"
        + "         <AttributeValue>3</AttributeValue>\n" + "       </Attribute>\n" + "       <Attribute AttributeName=\"bankid-se\" AttributeNamespace=\"unique-id\">\n"
        + "         <AttributeValue>188803099368</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"se.persnr\" AttributeNamespace=\"national-id\">\n"
        + "         <AttributeValue>188803099368</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"firstname\" AttributeNamespace=\"bankid-se\">\n"
        + "         <AttributeValue>Agda</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"lastname\" AttributeNamespace=\"bankid-se\">\n"
        + "          <AttributeValue>Andersson</AttributeValue>\n" + "       </Attribute>\n" + "       <Attribute AttributeName=\"plain-name\" AttributeNamespace=\"bankid-se\">\n"
        + "          <AttributeValue>Agda Andersson</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"fnr\" AttributeNamespace=\"bankid-se\">\n"
        + "         <AttributeValue>188803099368</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"name\" AttributeNamespace=\"bankid-se\">\n"
        + "          <AttributeValue>Agda Andersson-AB1AB</AttributeValue>\n" + "        </Attribute>\n" + "       <Attribute AttributeName=\"not-before-millis\"\n"
        + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>1124275605000</AttributeValue>\n" + "       </Attribute>\n"
        + "       <Attribute AttributeName=\"not-after-millis\"\n" + "          AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>1439675999000</AttributeValue>\n"
        + "       </Attribute>\n" + "       <Attribute AttributeName=\"security-levels\"\n" + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>[A]</AttributeValue>\n"
        + "       </Attribute>\n" + "       <Attribute AttributeName=\"document.data.type\"\n" + "          AttributeNamespace=\"bankid-se\">\n"
        + "          <AttributeValue>text/xml</AttributeValue>\n" + "        </Attribute>\n" + "     </AttributeStatement>\n" + "    </Assertion>\n" + " </ds:Signature>\n" + "</Response>";

  }

  private String getSamlResponseWithoutSsn() {
    return "<Response xmlns=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n" + " xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n"
        + " IssueInstant=\"2009-07-03T07:04:43.412Z\" MajorVersion=\"1\" MinorVersion=\"1\"\n" + "  ResponseID=\"cc3ddd5bde1abb52a7abc37feee1162e\">\n"
        + " <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" + " <ds:SignatureValue>dvIrlZe8cBvaNNu+paSgM9RuPBAnncj9pioi/HGLlitg9cGnoWXLMg==</ds:SignatureValue>\n"
        + "   <Assertion xmlns=\"urn:oasis:names:tc:SAML:1.0:assertion\"\n" + "     AssertionID=\"fc2575ebc739e358f0de661b1e35e82d\" IssueInstant=\"2009-07-03T07:04:43.412Z\"\n"
        + "     Issuer=\"test.signicat.com/std\" MajorVersion=\"1\" MinorVersion=\"1\">\n" + "      <Conditions NotBefore=\"2009-07-03T07:04:43.411Z\"\n"
        + "       NotOnOrAfter=\"2009-07-03T07:05:13.411Z\"></Conditions>\n" + "      <AuthenticationStatement\n" + "       AuthenticationInstant=\"2009-07-03T07:04:43.377Z\"\n"
        + "       AuthenticationMethod=\"urn:ksi:names:SAML:2.0:ac:BankID-SE\">\n" + "        <Subject>\n" + "          <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">\n"
        + "         </NameIdentifier>\n" + "          <SubjectConfirmation>\n" + "            <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n"
        + "         </SubjectConfirmation>\n" + "       </Subject>\n" + "     </AuthenticationStatement>\n" + "     <AttributeStatement>\n" + "       <Subject>\n"
        + "         <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">\n" + "          </NameIdentifier>\n" + "          <SubjectConfirmation>\n"
        + "           <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n" + "          </SubjectConfirmation>\n" + "       </Subject>\n"
        + "       <Attribute AttributeName=\"security-level\"\n" + "          AttributeNamespace=\"signicat\">\n" + "         <AttributeValue>3</AttributeValue>\n" + "       </Attribute>\n"
        + "       <Attribute AttributeName=\"bankid-se\" AttributeNamespace=\"unique-id\">\n" + "         <AttributeValue></AttributeValue>\n" + "        </Attribute>\n"
        + "       <Attribute AttributeName=\"firstname\" AttributeNamespace=\"bankid-se\">\n" + "         <AttributeValue>Agda</AttributeValue>\n" + "        </Attribute>\n"
        + "       <Attribute AttributeName=\"lastname\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Andersson</AttributeValue>\n" + "       </Attribute>\n"
        + "       <Attribute AttributeName=\"plain-name\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Agda Andersson</AttributeValue>\n" + "        </Attribute>\n"
        + "       <Attribute AttributeName=\"fnr\" AttributeNamespace=\"bankid-se\">\n" + "         <AttributeValue></AttributeValue>\n" + "        </Attribute>\n"
        + "       <Attribute AttributeName=\"name\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Agda Andersson-AB1AB</AttributeValue>\n" + "        </Attribute>\n"
        + "       <Attribute AttributeName=\"not-before-millis\"\n" + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>1124275605000</AttributeValue>\n"
        + "       </Attribute>\n" + "       <Attribute AttributeName=\"not-after-millis\"\n" + "          AttributeNamespace=\"bankid-se\">\n"
        + "          <AttributeValue>1439675999000</AttributeValue>\n" + "       </Attribute>\n" + "       <Attribute AttributeName=\"security-levels\"\n"
        + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>[A]</AttributeValue>\n" + "       </Attribute>\n" + "       <Attribute AttributeName=\"document.data.type\"\n"
        + "          AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>text/xml</AttributeValue>\n" + "        </Attribute>\n" + "     </AttributeStatement>\n" + "    </Assertion>\n"
        + " </ds:Signature>\n" + "</Response>";

  }

  static class SOAPFaultMock implements SOAPFault {
    @Override
    public Detail addDetail() throws SOAPException {
      return null;
    }

    @Override
    public void addFaultReasonText(String text, Locale locale) throws SOAPException {
    }

    @Override
    public void appendFaultSubcode(QName subcode) throws SOAPException {
    }

    @Override
    public Detail getDetail() {
      return null;
    }

    @Override
    public String getFaultActor() {
      return null;
    }

    @Override
    public String getFaultCode() {
      return null;
    }

    @Override
    public Name getFaultCodeAsName() {
      return null;
    }

    @Override
    public QName getFaultCodeAsQName() {
      return null;
    }

    @Override
    public String getFaultNode() {
      return null;
    }

    @Override
    public Iterator getFaultReasonLocales() throws SOAPException {
      return null;
    }

    @Override
    public String getFaultReasonText(Locale locale) throws SOAPException {
      return null;
    }

    @Override
    public Iterator getFaultReasonTexts() throws SOAPException {
      return null;
    }

    @Override
    public String getFaultRole() {
      return null;
    }

    @Override
    public String getFaultString() {
      return null;
    }

    @Override
    public Locale getFaultStringLocale() {
      return null;
    }

    @Override
    public Iterator getFaultSubcodes() {
      return null;
    }

    @Override
    public boolean hasDetail() {
      return false;
    }

    @Override
    public void removeAllFaultSubcodes() {
    }

    @Override
    public void setFaultActor(String faultActor) throws SOAPException {
    }

    @Override
    public void setFaultCode(Name faultCodeQName) throws SOAPException {
    }

    @Override
    public void setFaultCode(QName faultCodeQName) throws SOAPException {
    }

    @Override
    public void setFaultCode(String faultCode) throws SOAPException {
    }

    @Override
    public void setFaultNode(String uri) throws SOAPException {
    }

    @Override
    public void setFaultRole(String uri) throws SOAPException {
    }

    @Override
    public void setFaultString(String faultString) throws SOAPException {
    }

    @Override
    public void setFaultString(String faultString, Locale locale) throws SOAPException {
    }

    @Override
    public SOAPElement addAttribute(Name name, String value) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addAttribute(QName qname, String value) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(Name name) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(QName qname) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(String localName) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(SOAPElement element) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(String localName, String prefix) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addChildElement(String localName, String prefix, String uri) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addNamespaceDeclaration(String prefix, String uri) throws SOAPException {
      return null;
    }

    @Override
    public SOAPElement addTextNode(String text) throws SOAPException {
      return null;
    }

    @Override
    public QName createQName(String localName, String prefix) throws SOAPException {
      return null;
    }

    @Override
    public Iterator getAllAttributes() {
      return null;
    }

    @Override
    public Iterator getAllAttributesAsQNames() {
      return null;
    }

    @Override
    public String getAttributeValue(Name name) {
      return null;
    }

    @Override
    public String getAttributeValue(QName qname) {
      return null;
    }

    @Override
    public Iterator getChildElements() {
      return null;
    }

    @Override
    public Iterator getChildElements(Name name) {
      return null;
    }

    @Override
    public Iterator getChildElements(QName qname) {
      return null;
    }

    @Override
    public Name getElementName() {
      return null;
    }

    @Override
    public QName getElementQName() {
      return null;
    }

    @Override
    public String getEncodingStyle() {
      return null;
    }

    @Override
    public Iterator getNamespacePrefixes() {
      return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
      return null;
    }

    @Override
    public Iterator getVisibleNamespacePrefixes() {
      return null;
    }

    @Override
    public boolean removeAttribute(Name name) {
      return false;
    }

    @Override
    public boolean removeAttribute(QName qname) {
      return false;
    }

    @Override
    public void removeContents() {
    }

    @Override
    public boolean removeNamespaceDeclaration(String prefix) {
      return false;
    }

    @Override
    public SOAPElement setElementQName(QName newName) throws SOAPException {
      return null;
    }

    @Override
    public void setEncodingStyle(String encodingStyle) throws SOAPException {
    }

    @Override
    public void detachNode() {
    }

    @Override
    public SOAPElement getParentElement() {
      return null;
    }

    @Override
    public String getValue() {
      return null;
    }

    @Override
    public void recycleNode() {
    }

    @Override
    public void setParentElement(SOAPElement parent) throws SOAPException {
    }

    @Override
    public void setValue(String value) {
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
      return null;
    }

    @Override
    public Node cloneNode(boolean deep) {
      return null;
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
      return 0;
    }

    @Override
    public NamedNodeMap getAttributes() {
      return null;
    }

    @Override
    public String getBaseURI() {
      return null;
    }

    @Override
    public NodeList getChildNodes() {
      return null;
    }

    @Override
    public Object getFeature(String feature, String version) {
      return null;
    }

    @Override
    public Node getFirstChild() {
      return null;
    }

    @Override
    public Node getLastChild() {
      return null;
    }

    @Override
    public String getLocalName() {
      return null;
    }

    @Override
    public String getNamespaceURI() {
      return null;
    }

    @Override
    public Node getNextSibling() {
      return null;
    }

    @Override
    public String getNodeName() {
      return null;
    }

    @Override
    public short getNodeType() {
      return 0;
    }

    @Override
    public String getNodeValue() throws DOMException {
      return null;
    }

    @Override
    public Document getOwnerDocument() {
      return null;
    }

    @Override
    public Node getParentNode() {
      return null;
    }

    @Override
    public String getPrefix() {
      return null;
    }

    @Override
    public Node getPreviousSibling() {
      return null;
    }

    @Override
    public String getTextContent() throws DOMException {
      return null;
    }

    @Override
    public Object getUserData(String key) {
      return null;
    }

    @Override
    public boolean hasAttributes() {
      return false;
    }

    @Override
    public boolean hasChildNodes() {
      return false;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
      return null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
      return false;
    }

    @Override
    public boolean isEqualNode(Node arg) {
      return false;
    }

    @Override
    public boolean isSameNode(Node other) {
      return false;
    }

    @Override
    public boolean isSupported(String feature, String version) {
      return false;
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
      return null;
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
      return null;
    }

    @Override
    public void normalize() {
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
      return null;
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
      return null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
      return null;
    }

    @Override
    public String getAttribute(String name) {
      return null;
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
      return null;
    }

    @Override
    public Attr getAttributeNode(String name) {
      return null;
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
      return null;
    }

    @Override
    public NodeList getElementsByTagName(String name) {
      return null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
      return null;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
      return null;
    }

    @Override
    public String getTagName() {
      return null;
    }

    @Override
    public boolean hasAttribute(String name) {
      return false;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
      return false;
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
      return null;
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
      return null;
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
      return null;
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
    }
  }

  static class SignatureEndpointImplMock implements SignatureEndpointImpl {
    private String samlResponse;

    public void setSamlResponse(String samlResponse) {
      this.samlResponse = samlResponse;
    }

    @Override
    public String registerDocument(String data, String mimeType, String documentDescription) {
      return "dummy-artifact";
    }

    @Override
    public String retrieveSaml(String artifact) {
      return samlResponse;
    }

    @Override
    public String retrieveSignedDocument(String artifact) {
      return null;
    }
  }

  static class CitizenRepositoryMock extends CitizenRepository {
    private String citizenName;

    public void setCitizenName(String citizenName) {
      this.citizenName = citizenName;
    }

    @Override
    public String getCitizenNameFromSsn(String ssn) {
      return this.citizenName;
    }
  }

  static class VardvalServiceMock implements VardvalService {
    private VardvalInfo vardvalInfo = new VardvalInfo();
    private Exception exceptionToThrow;

    public void setVardvalInfo(VardvalInfo vardvalInfo) {
      this.vardvalInfo = vardvalInfo;
    }

    public void setExceptionToThrow(Exception exceptionToThrow) {
      this.exceptionToThrow = exceptionToThrow;
    }

    @Override
    public VardvalInfo getVardval(String ssn) throws IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage {
      if (exceptionToThrow != null) {
        if (exceptionToThrow instanceof IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage) {
          throw (IVårdvalServiceGetVårdValVårdvalServiceErrorFaultFaultMessage) exceptionToThrow;
        } else if (exceptionToThrow instanceof SOAPFaultException) {
          throw (SOAPFaultException) exceptionToThrow;
        }
      }
      vardvalInfo.setSsn(ssn);
      return vardvalInfo;
    }

    @Override
    public VardvalInfo setVardval(String ssn, String hsaId, byte[] signature) throws IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage {
      if (exceptionToThrow != null) {
        throw (IVårdvalServiceSetVårdValVårdvalServiceErrorFaultFaultMessage) exceptionToThrow;
      }
      vardvalInfo.setSsn(ssn);
      vardvalInfo.setUpcomingHsaId(hsaId);
      vardvalInfo.setSignature(new String(signature));
      return vardvalInfo;
    }
  }
}
