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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

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

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.EniroOrganisationBuilder;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition.UnitType;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.util.dom.DocumentHelper;

public class InformationPusherEniroTest {
  private InformationPusherEniro informationPusher = new InformationPusherEniro();;
  private FtpClientMock mockFtpClient = new FtpClientMock();
  private LdapTemplateMock ldapTemplateMock = new LdapTemplateMock();
  private static LogFactoryMock logFactoryMock;

  @BeforeClass
  public static void beforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @Before
  public void setUp() throws Exception {
    EniroOrganisationBuilder eniroOrganisationBuilder = new EniroOrganisationBuilder();
    eniroOrganisationBuilder.setRootUnits(Arrays.asList(""));
    eniroOrganisationBuilder.setCareCenter("Vårdcentral");
    eniroOrganisationBuilder.setOtherCare("Övrig primärvård");

    informationPusher.setLdapTemplate(ldapTemplateMock);
    informationPusher.setFtpClient(mockFtpClient);
    informationPusher.setEniroOrganisationBuilder(eniroOrganisationBuilder);
    informationPusher.setAllowedUnitBusinessClassificationCodes(new String[] { "1", "2" });
    informationPusher.setOtherCareTypeBusinessCodes(new String[] { "3", "4" });
  }

  @AfterClass
  public static void tearDown() {
    LogFactoryMock.resetInstance();
  }

  @Test
  public void testDoService() throws XPathExpressionException {
    informationPusher.doService();
    String fileContent = mockFtpClient.getFileContent();
    Document document = DocumentHelper.getDocumentFromString(fileContent);

    XPath xPath = XPathFactory.newInstance().newXPath();
    XPathExpression expression = xPath.compile("//Organization/Unit");
    NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(8, nodes.getLength());

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Borås']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Göteborg']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Uddevalla']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    expression = xPath.compile("//Organization/Unit[child::Locality[text() = 'Skövde']]");
    nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
    assertEquals(2, nodes.getLength());

    assertEquals("Unit details pusher: Completed with success.\n", logFactoryMock.getInfo(true));
  }

  @Test
  public void testExceptionHandling() {
    mockFtpClient.returnValue = false;
    informationPusher.doService();
    assertEquals("Unit details pusher: Completed with failure.\n", logFactoryMock.getError(true));
  }

  // Helper-methods

  private static UnitComposition createUnit(String name, String identity, String dn, UnitType careType) {
    UnitComposition unit = new UnitComposition();
    unit.setDn(dn);
    unit.getEniroUnit().setName(name);
    unit.getEniroUnit().setId(identity);
    unit.setCareType(careType);
    return unit;
  }

  // Mocks

  private static class FtpClientMock implements FtpClient {
    private String fileContent;
    private boolean returnValue = true;

    @Override
    public boolean sendFile(String fileContent) {
      this.fileContent = fileContent;
      return this.returnValue;
    }

    public String getFileContent() {
      return this.fileContent;
    }
  }

  private static class LdapTemplateMock extends se.vgregion.kivtools.mocks.ldap.LdapTemplateMock {
    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, int searchScope, ContextMapper mapper) {
      assertEquals(SearchControls.SUBTREE_SCOPE, searchScope);
      List<UnitComposition> unitslist = Arrays.asList(createUnit("unit1", "unit1-id", "ou=unit1,ou=org,o=VGR", UnitType.CARE_CENTER), createUnit("unit2", "unit2-id", "ou=unit2,ou=unit1,ou=org,o=VGR",
          UnitType.OTHER_CARE));

      return unitslist;
    }
  }
}
