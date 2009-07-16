package se.vgregion.hsatools.testtools.signicatws.endpoints;

import java.text.MessageFormat;

import org.apache.commons.codec.binary.Base64;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import se.vgregion.hsatools.testtools.services.Service;
import se.vgregion.kivtools.testservice.signicatws.ws.domain.RegisterDocument;
import se.vgregion.kivtools.testservice.signicatws.ws.domain.RegisterDocumentResponse;
import se.vgregion.kivtools.testservice.signicatws.ws.domain.RetrieveSaml;
import se.vgregion.kivtools.testservice.signicatws.ws.domain.RetrieveSamlResponse;

public class SignicatEndpoint extends AbstractMarshallingPayloadEndpoint {
  
  private static final String saml = "<Response xmlns=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n" + " xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n"
  + " IssueInstant=\"2009-07-03T07:04:43.412Z\" MajorVersion=\"1\" MinorVersion=\"1\"\n" + "  ResponseID=\"cc3ddd5bde1abb52a7abc37feee1162e\">\n"
  + " <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" + " <ds:SignatureValue>dvIrlZe8cBvaNNu+paSgM9RuPBAnncj9pioi/HGLlitg9cGnoWXLMg==</ds:SignatureValue>\n"
  + "   <Assertion xmlns=\"urn:oasis:names:tc:SAML:1.0:assertion\"\n" + "     AssertionID=\"fc2575ebc739e358f0de661b1e35e82d\" IssueInstant=\"2009-07-03T07:04:43.412Z\"\n"
  + "     Issuer=\"test.signicat.com/std\" MajorVersion=\"1\" MinorVersion=\"1\">\n" + "      <Conditions NotBefore=\"2009-07-03T07:04:43.411Z\"\n"
  + "       NotOnOrAfter=\"2009-07-03T07:05:13.411Z\"></Conditions>\n" + "      <AuthenticationStatement\n" + "       AuthenticationInstant=\"2009-07-03T07:04:43.377Z\"\n"
  + "       AuthenticationMethod=\"urn:ksi:names:SAML:2.0:ac:BankID-SE\">\n" + "        <Subject>\n" + "          <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">{0}\n"
  + "         </NameIdentifier>\n" + "          <SubjectConfirmation>\n" + "            <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n"
  + "         </SubjectConfirmation>\n" + "       </Subject>\n" + "     </AuthenticationStatement>\n" + "     <AttributeStatement>\n" + "       <Subject>\n"
  + "         <NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">{0}\n" + "          </NameIdentifier>\n" + "          <SubjectConfirmation>\n"
  + "           <ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "            </ConfirmationMethod>\n" + "          </SubjectConfirmation>\n" + "       </Subject>\n"
  + "       <Attribute AttributeName=\"security-level\"\n" + "          AttributeNamespace=\"signicat\">\n" + "         <AttributeValue>3</AttributeValue>\n" + "       </Attribute>\n"
  + "       <Attribute AttributeName=\"bankid-se\" AttributeNamespace=\"unique-id\">\n" + "         <AttributeValue>{0}</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"se.persnr\" AttributeNamespace=\"national-id\">\n" + "         <AttributeValue>{0}</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"firstname\" AttributeNamespace=\"bankid-se\">\n" + "         <AttributeValue>Agda</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"lastname\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Andersson</AttributeValue>\n" + "       </Attribute>\n"
  + "       <Attribute AttributeName=\"plain-name\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Agda Andersson</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"fnr\" AttributeNamespace=\"bankid-se\">\n" + "         <AttributeValue>{0}</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"name\" AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>Agda Andersson-AB1AB</AttributeValue>\n" + "        </Attribute>\n"
  + "       <Attribute AttributeName=\"not-before-millis\"\n" + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>1124275605000</AttributeValue>\n" + "       </Attribute>\n"
  + "       <Attribute AttributeName=\"not-after-millis\"\n" + "          AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>1439675999000</AttributeValue>\n" + "       </Attribute>\n"
  + "       <Attribute AttributeName=\"security-levels\"\n" + "         AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>[A]</AttributeValue>\n" + "       </Attribute>\n"
  + "       <Attribute AttributeName=\"document.data.type\"\n" + "          AttributeNamespace=\"bankid-se\">\n" + "          <AttributeValue>text/xml</AttributeValue>\n" + "        </Attribute>\n"
  + "     </AttributeStatement>\n" + "    </Assertion>\n" + " </ds:Signature>\n" + "</Response>";
  
  public SignicatEndpoint(Marshaller marshaller, Unmarshaller unmarshaller) {
    super(marshaller, unmarshaller);
  }

  protected Object invokeInternal(Object requestObject) throws Exception {
    if (requestObject instanceof RegisterDocument) {
      RegisterDocumentResponse response = new RegisterDocumentResponse();
      String artifact = "artifact_" + System.nanoTime();
      response.setRegisterDocumentReturn(artifact);
      return response;
    } else {
      RetrieveSaml request = (RetrieveSaml) requestObject;
      String artifact = request.getArtifact();
      String ssn = Service.getSignature(artifact);
      String responseText = MessageFormat.format(saml, ssn);
      RetrieveSamlResponse response = new RetrieveSamlResponse();
      byte[] encodeBase64 = Base64.encodeBase64(responseText.getBytes("utf-8"));
      response.setRetrieveSamlReturn(new String(encodeBase64,"utf-8"));
      return response;
    }
  }
  
  
}
