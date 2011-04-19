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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.KivLdapFilterHelper;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.UnitComposition;
import se.vgregion.kivtools.hriv.intsvc.ldap.eniro.vgr.EniroUnitMapperVGR;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Organization;
import se.vgregion.kivtools.hriv.intsvc.ws.domain.eniro.Unit;
import se.vgregion.kivtools.hriv.intsvc.ws.eniro.vgr.EniroOrganisationBuilderVGR;
import se.vgregion.kivtools.mocks.LogFactoryMock;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.util.StringUtil;

public class OrganisationPrinterTest {
  private OrganisationPrinter organisationPrinter;
  private StringWriter stringWriter;
  private static LogFactoryMock logFactoryMock;
  private final String[] allowedUnitBusinessClassificationCodes = "1000,1002,1003,1004,1005,1006,1007,1008,1009,1010,1011,1012,1013,1014,1015,1016,1016,1017,1018,1019,1020,1021,1022,1023,1024,1025,1026,1027,1028,1100,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1112,1113,1114,1115,1116,1116,1117,1118,1119,1120,1121,1122,1123,1124,1125,1126,1127,1128,1129,1130,1131,1132,1132,1133,1134,1135,1136,1137,1138,1139,1202,1203,1204,1205,1206,1207,1208,1209,1210,1211,1212,1213,1214,1215,1216,1217,1218,1218,1219,1220,1221,1222,1223,1224,1227,1228,1229,1230,1231,1232,1302,1303,1304,1306,1307,1308,1310,1311,1312,1313,1314,1315,1316,1317,1318,1319,1320,1320,1321,1322,1323,1324,1325,1326,1327,1328,1329,1330,1331,1332,1333,1334,1335,1336,1336,1337,1338,1339,1340,1341,1342,1343,1402,1403,1404,1406,1407,1408,1409,1410,1411,1412,1413,1414,1500,1502,1503,1504,1505,1506,1507,1512,1513,1514,1515,1516,1518,1519,1600,1603,1604,1605,1606,1607,1608,1609,1611,1614,1615,1616,1617,1618,1619,1702,1703,1704,1705,1706,1707,1708,1709,1710,1711,1712,1713,1714,1715,1716,1717,1718,1718,1719,1800,1801,1802,1803,1804,1805,1806,1807,1808,1809,1810,1811,1812,2002,2003,2004,2006,2007,2008,2009,2010,2011,2012,2016,2017,2018,2018,2019,2020,2021,2102,2103,2106,2107,2108,2202,2203,2204,2205,3022"
      .split(",");

  @BeforeClass
  public static void setupBeforeClass() {
    logFactoryMock = LogFactoryMock.createInstance();
  }

  @AfterClass
  public static void afterClass() {
    logFactoryMock.release();
  }

  @Before
  public void setup() {
    this.organisationPrinter = new OrganisationPrinter();
    this.stringWriter = new StringWriter();
  }

  @Test
  public void testPrintOrganisation() {
    this.organisationPrinter.printOrganisation(this.createOrganisation(), this.stringWriter);
    assertTrue(!StringUtil.isEmpty(this.stringWriter.toString()));
    System.out.println(this.stringWriter.toString());
  }

  @Test
  public void testIOExceptionHandling() {
    this.organisationPrinter.printOrganisation(this.createOrganisation(), new WriterExceptionMock());
    assertEquals("Failed to write out content\nFailed to write out content\n", logFactoryMock.getError(true));
  }

  private Organization createOrganisation() {
    Organization organization = new Organization();
    // Root units
    for (int i = 0; i < 5; i++) {
      Unit unit = new Unit();
      unit.setId("rootUnit" + i);
      unit.setName("rootUnitName" + i);
      organization.getUnit().add(unit);
    }

    Unit root = new Unit();
    root.setId("rootUnitWithChildren");
    root.setName("rootUnitWithChildrenName");
    for (int i = 0; i < 3; i++) {

      Unit childUnit = new Unit();
      childUnit.setId("childUnit" + i);
      childUnit.setName("childUnitName" + i);
      // leaf to child unit
      if (i == 0 || i == 2) {
        Unit leaf = new Unit();
        leaf.setId("leafUnit" + i);
        leaf.setName("leafUnitName" + i);
        childUnit.getUnit().add(leaf);
      }
      root.getUnit().add(childUnit);
    }
    organization.getUnit().add(root);

    return organization;
  }

  public static void main(String[] args) throws Exception {
    OrganisationPrinterTest organisationPrinter2 = new OrganisationPrinterTest();
    organisationPrinter2.printOutRealOrganisation();
  }

  public void printOutRealOrganisation() throws Exception {
    EniroOrganisationBuilderVGR eniroOrganisationBuilder = new EniroOrganisationBuilderVGR();
    eniroOrganisationBuilder.setCareCenter("Vårdcentral");
    eniroOrganisationBuilder.setOtherCare("Övrig primärvård");
    eniroOrganisationBuilder.setRootUnits(Arrays.asList("ou=Sahlgrenska Universitetssjukhuset"));
    Organization generateOrganisation = eniroOrganisationBuilder.generateOrganisation(this.getUnitsFromLdap(this.getLdapTemplate()), "Uddevalla");
    this.organisationPrinter = new OrganisationPrinter();
    this.stringWriter = new StringWriter();
    this.organisationPrinter.printOrganisation(generateOrganisation, this.stringWriter);
    System.out.println(this.stringWriter.toString());
  }

  private LdapTemplate getLdapTemplate() throws Exception {
    new HealthcareTypeConditionHelper().setImplResourcePath("se.vgregion.kivtools.search.svc.impl.kiv.ldap.search-composite-svc-healthcare-type-conditions");
    LdapContextSource ldapContextSource = new LdapContextSource();
    ldapContextSource.setUrl("ldap://kivldap01.vgregion.se:389");
    ldapContextSource.setBase("ou=Org,o=vgr");
    ldapContextSource.setUserDn("cn=sokso1,ou=Resurs,o=VGR");
    ldapContextSource.setPassword("6wuz8zab");
    ldapContextSource.setPooled(true);
    ldapContextSource.afterPropertiesSet();
    LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
    ldapTemplate.afterPropertiesSet();
    return ldapTemplate;
  }

  private List<UnitComposition> getUnitsFromLdap(LdapTemplate ldapTemplate) {
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper();
    Filter healthcareTypeFilter = KivLdapFilterHelper.createHealthcareTypeFilter(healthcareTypeConditionHelper.getAllHealthcareTypes());
    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("hsaMunicipalityCode", 1480));
    OrFilter orBusinessCodes = new OrFilter();
    for (String businessCode : this.allowedUnitBusinessClassificationCodes) {
      orBusinessCodes.or(new EqualsFilter("hsaBusinessClassificationCode", businessCode));
    }
    orBusinessCodes.or(healthcareTypeFilter);
    andFilter.and(orBusinessCodes);
    EniroUnitMapperVGR eniroUnitMapper = new EniroUnitMapperVGR("Göteborg",
        Arrays.asList("1519", "1413", "1505", "1504", "1411", "1121", "1516", "1109", "1402", "150", "1507", "1413", "1402", "1403"));
    @SuppressWarnings("unchecked")
    List<UnitComposition> unitsList = ldapTemplate.search("", andFilter.encode(), SearchControls.SUBTREE_SCOPE, eniroUnitMapper);
    return unitsList;
  }

  class WriterExceptionMock extends Writer {

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      throw new IOException();
    }
  }
}
