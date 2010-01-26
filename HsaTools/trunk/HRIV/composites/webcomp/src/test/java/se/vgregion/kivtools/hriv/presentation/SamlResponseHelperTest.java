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

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.kivtools.hriv.presentation.types.SigningInformation;

public class SamlResponseHelperTest {

  @Test
  public void testInstantiation() {
    SamlResponseHelper samlResponseHelper = new SamlResponseHelper();
    assertNotNull(samlResponseHelper);
  }

  @Test
  public void testGetNationalIdFromSaml() {
    String samlAssertionString = getSaml();
    SigningInformation signingInformation = SamlResponseHelper.getSigningInformation(samlAssertionString);
    Assert.assertEquals("188803099368", signingInformation.getNationalId());
    Assert.assertNotNull("Saml response was null", signingInformation.getSamlResponse());
  }

  @Test
  public void testGetNationalIdFromSamlAttributeWithoutAttributeName() {
    String samlAssertionString = getSamlAttributeWithoutAttributeName();
    SigningInformation signingInformation = SamlResponseHelper.getSigningInformation(samlAssertionString);
    assertNotNull(signingInformation);
    assertNull(signingInformation.getNationalId());
  }

  @Test
  public void testGetNationalIdFromSamlAttributeWithoutChildNodes() {
    String samlAssertionString = getSamlAttributeWithoutChildNodes();
    SigningInformation signingInformation = SamlResponseHelper.getSigningInformation(samlAssertionString);
    assertNotNull(signingInformation);
    assertNull(signingInformation.getNationalId());
  }

  private String getSaml() {
    return "<Response xmlns=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n" + "	xmlns:saml=\"urn:oasis:names:tc:SAML:1.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:1.0:protocol\"\n"
        + "	IssueInstant=\"2009-07-03T07:04:43.412Z\" MajorVersion=\"1\" MinorVersion=\"1\"\n" + "	ResponseID=\"cc3ddd5bde1abb52a7abc37feee1162e\">\n"
        + "	<ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" + "	<ds:SignatureValue>dvIrlZe8cBvaNNu+paSgM9RuPBAnncj9pioi/HGLlitg9cGnoWXLMg==</ds:SignatureValue>\n"
        + "		<Assertion xmlns=\"urn:oasis:names:tc:SAML:1.0:assertion\"\n" + "			AssertionID=\"fc2575ebc739e358f0de661b1e35e82d\" IssueInstant=\"2009-07-03T07:04:43.412Z\"\n"
        + "			Issuer=\"test.signicat.com/std\" MajorVersion=\"1\" MinorVersion=\"1\">\n" + "			<Conditions NotBefore=\"2009-07-03T07:04:43.411Z\"\n"
        + "				NotOnOrAfter=\"2009-07-03T07:05:13.411Z\"></Conditions>\n" + "			<AuthenticationStatement\n" + "				AuthenticationInstant=\"2009-07-03T07:04:43.377Z\"\n"
        + "				AuthenticationMethod=\"urn:ksi:names:SAML:2.0:ac:BankID-SE\">\n" + "				<Subject>\n" + "					<NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">188803099368\n"
        + "					</NameIdentifier>\n" + "					<SubjectConfirmation>\n" + "						<ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "						</ConfirmationMethod>\n"
        + "					</SubjectConfirmation>\n" + "				</Subject>\n" + "			</AuthenticationStatement>\n" + "			<AttributeStatement>\n" + "				<Subject>\n"
        + "					<NameIdentifier Format=\"urn:kantega:ksi:3.0:nameid-format:fnr\">188803099368\n" + "					</NameIdentifier>\n" + "					<SubjectConfirmation>\n"
        + "						<ConfirmationMethod>urn:oasis:names:tc:SAML:1.0:cm:bearer\n" + "						</ConfirmationMethod>\n" + "					</SubjectConfirmation>\n" + "				</Subject>\n"
        + "				<Attribute AttributeName=\"security-level\"\n" + "					AttributeNamespace=\"signicat\">\n" + "					<AttributeValue>3</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"bankid-se\" AttributeNamespace=\"unique-id\">\n" + "					<AttributeValue>188803099368</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"se.persnr\" AttributeNamespace=\"national-id\">\n" + "					<AttributeValue>188803099368</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"firstname\" AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>Agda</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"lastname\" AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>Andersson</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"plain-name\" AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>Agda Andersson</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"fnr\" AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>188803099368</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"name\" AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>Agda Andersson-AB1AB</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"not-before-millis\"\n" + "					AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>1124275605000</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"not-after-millis\"\n" + "					AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>1439675999000</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"security-levels\"\n" + "					AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>[A]</AttributeValue>\n" + "				</Attribute>\n"
        + "				<Attribute AttributeName=\"document.data.type\"\n" + "					AttributeNamespace=\"bankid-se\">\n" + "					<AttributeValue>text/xml</AttributeValue>\n" + "				</Attribute>\n"
        + "			</AttributeStatement>\n" + "		</Assertion>\n" + "	</ds:Signature>\n" + "</Response>";
  }

  private String getSamlAttributeWithoutAttributeName() {
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
        + "          </SubjectConfirmation>\n" + "       </Subject>\n" + "       <Attribute \n" + "          AttributeNamespace=\"signicat\">\n" + "         <AttributeValue>3</AttributeValue>\n"
        + "       </Attribute>\n" + "     </AttributeStatement>\n" + "    </Assertion>\n" + " </ds:Signature>\n" + "</Response>";
  }

  private String getSamlAttributeWithoutChildNodes() {
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
        + "          </SubjectConfirmation>\n" + "       </Subject>\n" + "       <Attribute AttributeName=\"se.persnr\" \n" + "          AttributeNamespace=\"signicat\">" + "</Attribute>\n"
        + "     </AttributeStatement>\n" + "    </Assertion>\n" + " </ds:Signature>\n" + "</Response>";
  }
}
