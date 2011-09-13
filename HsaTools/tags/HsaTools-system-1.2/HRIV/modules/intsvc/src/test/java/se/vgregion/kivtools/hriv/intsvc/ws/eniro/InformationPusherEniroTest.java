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

package se.vgregion.kivtools.hriv.intsvc.ws.eniro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ldap.core.ContextMapper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Address;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.EniroConfigurationVGR;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.EniroOrganisationBuilderVGR;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.UnitFetcherVGR;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.dom.DocumentHelper;

public class InformationPusherEniroTest {
  private final InformationPusherEniro informationPusher = new InformationPusherEniro();;
  private final FtpClientMock mockFtpClient = new FtpClientMock();
  private final LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
  private final EmptyLdapTemplate emptyLdapTemplate = new EmptyLdapTemplate();
  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() throws Exception {
    EniroOrganisationBuilderVGR eniroOrganisationBuilder = new EniroOrganisationBuilderVGR();
    eniroOrganisationBuilder.setRootUnits(Arrays.asList(""));
    eniroOrganisationBuilder.setCareCenter("Vårdcentral");
    eniroOrganisationBuilder.setOtherCare("Övrig primärvård");

    this.informationPusher.setUnitFetcher(new UnitFetcherVGR(this.ldapTemplateMock, new String[] { "1", "2" }, new String[] { "3", "4" }));
    this.informationPusher.setFtpClient(this.mockFtpClient);
    this.informationPusher.setEniroOrganisationBuilder(eniroOrganisationBuilder);
    this.informationPusher.setEniroConfiguration(new EniroConfigurationVGR());
  }

  @AfterClass
  public static void tearDown() {
    LogFactoryMock.resetInstance();
  }

  @Test
  public void testDoService() throws XPathExpressionException {
    this.informationPusher.doService();
    String fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Goteborg");
    Document document = DocumentHelper.getDocumentFromString(fileContent);

    XPath xPath = XPathFactory.newInstance().newXPath();
    XPathExpression expression = xPath.compile("//Organization/Unit");
    NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Göteborg']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Boras");
    document = DocumentHelper.getDocumentFromString(fileContent);

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Borås']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Uddevalla");
    document = DocumentHelper.getDocumentFromString(fileContent);

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Uddevalla']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Skovde");
    document = DocumentHelper.getDocumentFromString(fileContent);

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Skövde']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    assertEquals("Unit details pusher: Completed with success.\n", logFactoryMock.getInfo(true));
  }

  @Test
  public void testExceptionHandling() {
    this.mockFtpClient.returnValue = false;
    this.informationPusher.doService();
    assertEquals("Unit details pusher: Completed with failure.\n", logFactoryMock.getError(true));
  }

  @Test
  public void countryIdAndNameOfOrganisationIsPopulated() throws XPathExpressionException {
    this.informationPusher.doService();
    String fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Goteborg");
    Document document = DocumentHelper.getDocumentFromString(fileContent);

    XPath xPath = XPathFactory.newInstance().newXPath();
    XPathExpression expression = xPath.compile("//Organization/Country[text() = 'SE']");
    NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(1, nodes.getLength());

    expression = xPath.compile("//Organization/Id[text() = '232100-0131 VGR Göteborg']");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(1, nodes.getLength());

    expression = xPath.compile("//Organization/Name[text() = 'Västra Götalandsregionen Göteborg']");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(1, nodes.getLength());
  }

  @Test
  public void emptyTagsAreNotCreated() throws XPathExpressionException {
    this.informationPusher.doService();
    String fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Goteborg");
    Document document = DocumentHelper.getDocumentFromString(fileContent);

    XPath xPath = XPathFactory.newInstance().newXPath();
    XPathExpression expression = xPath.compile("//Unit/Address/City");
    NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(1, nodes.getLength());
  }

  @Test
  public void noFileIsSentIfNoUnitsAreFound() {
    this.informationPusher.setUnitFetcher(new UnitFetcherVGR(this.emptyLdapTemplate, new String[] { "1", "2" }, new String[] { "3", "4" }));

    this.informationPusher.doService();
    String fileContent = this.mockFtpClient.getFileContent("Vastra Gotalandsregionen Goteborg");
    assertNull("fileContent", fileContent);
  }

  // Helper-methods

  private static UnitComposition createUnit(String name, String identity, String dn, UnitType careType, String city) {
    UnitComposition unit = new UnitComposition();
    unit.setDn(dn);
    unit.getEniroUnit().setName(name);
    unit.getEniroUnit().setId(identity);
    unit.setCareType(careType);
    if (!StringUtil.isEmpty(city)) {
      Address address = new Address();
      address.setCity(city);
      unit.getEniroUnit().getTextOrImageOrAddress().add(address);
    }
    return unit;
  }

  // Mocks

  private static class FtpClientMock implements FtpClient {
    private final Map<String, String> fileContent = new HashMap<String, String>();
    private boolean returnValue = true;

    @Override
    public boolean sendFile(String fileContent, final String basename, final String suffix) {
      this.fileContent.put(basename, fileContent);
      return this.returnValue;
    }

    public String getFileContent(final String basename) {
      return this.fileContent.get(basename);
    }
  }

  private static class LdapTemplateMock extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    @Override
    public List<?> search(String base, String filter, int searchScope, ContextMapper mapper) {
      assertEquals(SearchControls.SUBTREE_SCOPE, searchScope);
      List<UnitComposition> unitslist = Arrays.asList(createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR", UnitType.CARE_CENTER, null),
          createUnit("unit2", "unit2-id", "ou=unit2,ou=unit1,ou=org,o=VGR", UnitType.OTHER_CARE, "Göteborg"));

      return unitslist;
    }
  }

  private static class EmptyLdapTemplate extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    @Override
    public List<?> search(String base, String filter, int searchScope, ContextMapper mapper) {
      return Collections.emptyList();
    }
  }
}
