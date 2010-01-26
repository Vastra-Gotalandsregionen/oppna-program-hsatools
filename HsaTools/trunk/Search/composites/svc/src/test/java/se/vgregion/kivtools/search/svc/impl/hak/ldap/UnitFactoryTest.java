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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;

public class UnitFactoryTest {
  private static final String CN = "Unit cn";
  private static final String TEST = "Test";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String EXPECTED_DATE_TIME = "2009-01-01 12:01:02";
  private static final String EXPECTED_HOURS = "Måndag-Fredag 08:30-10:00";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private static final String TEST_TIMESTAMP = "20090101120102";

  private SimpleDateFormat dateFormat;
  private LDAPEntryMock ldapEntry;

  @Before
  public void setUp() throws Exception {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ldapEntry = new LDAPEntryMock();
    ldapEntry.addAttribute("businessClassificationCode", TEST);
    ldapEntry.addAttribute("cn", CN);
    ldapEntry.addAttribute("createTimeStamp", TEST_TIMESTAMP);
    ldapEntry.addAttribute("description", TEST);
    ldapEntry.addAttribute("dropInHours", TEST_TIME);
    ldapEntry.addAttribute("facsimileTelephoneNumber", TEST);
    ldapEntry.addAttribute("geographicalCoordinates", TEST);
    ldapEntry.addAttribute("hsaAdministrationForm", TEST);
    ldapEntry.addAttribute("hsaAdministrationFormText", TEST);
    ldapEntry.addAttribute("hsaCountyCode", TEST);
    ldapEntry.addAttribute("hsaCountyName", TEST);
    ldapEntry.addAttribute("hsaIdentity", TEST);
    ldapEntry.addAttribute("hsaInternalAddress", TEST);
    ldapEntry.addAttribute("hsaInternalPagerNumber", TEST);
    ldapEntry.addAttribute("hsaManagementCode", TEST);
    ldapEntry.addAttribute("hsaManagementName", TEST);
    ldapEntry.addAttribute("hsaMunicipalitySectionCode", TEST);
    ldapEntry.addAttribute("hsaMunicipalitySectionName", TEST);
    ldapEntry.addAttribute("postalAddress", TEST);
    ldapEntry.addAttribute("hsaDeliveryAddress", TEST);
    ldapEntry.addAttribute("hsaInvoiceAddress", TEST);
    ldapEntry.addAttribute("hsaConsigneeAddress", TEST);
    ldapEntry.addAttribute("hsaSmsTelephoneNumber", TEST);
    ldapEntry.addAttribute("hsaSwitchboardNumber", TEST);
    ldapEntry.addAttribute("hsaTextPhoneNumber", TEST);
    ldapEntry.addAttribute("hsaTelephoneNumber", "hsaTelephoneNumber");
    ldapEntry.addAttribute("hsaUnitPrescriptionCode", TEST);
    ldapEntry.addAttribute("hsaVisitingRuleAge", TEST);
    ldapEntry.addAttribute("hsaVisitingRules", TEST);
    ldapEntry.addAttribute("l", TEST);
    ldapEntry.addAttribute("labeledURI", TEST);
    ldapEntry.addAttribute("mail", TEST);
    ldapEntry.addAttribute("management", "1");
    ldapEntry.addAttribute("mobile", TEST);
    ldapEntry.addAttribute("municipalityCode", TEST);
    ldapEntry.addAttribute("municipalityName", TEST);
    ldapEntry.addAttribute("objectClass", TEST);
    ldapEntry.addAttribute("organizationalUnitNameShort", TEST);
    ldapEntry.addAttribute("ou", TEST);
    ldapEntry.addAttribute("pager", TEST);
    ldapEntry.addAttribute("postalAddress", TEST);
    ldapEntry.addAttribute("postalCode", TEST);
    ldapEntry.addAttribute("route", TEST);
    ldapEntry.addAttribute("street", TEST);
    ldapEntry.addAttribute("surgeryHours", TEST_TIME);
    ldapEntry.addAttribute("telephoneHours", TEST_TIME);
    ldapEntry.addAttribute("telephoneNumber", "telephoneNumber");
    ldapEntry.addAttribute("vgrAO3kod", TEST);
    ldapEntry.addAttribute("vgrAnsvarsnummer", TEST);
    ldapEntry.addAttribute("vgrCareType", TEST);
    ldapEntry.addAttribute("vgrEanCode", TEST);
    ldapEntry.addAttribute("vgrEdiCode", TEST);
    ldapEntry.addAttribute("vgrInternalSedfInvoiceAddress", TEST);
    ldapEntry.addAttribute("vgrModifyTimestamp", TEST_TIMESTAMP);
    ldapEntry.addAttribute("vgrRefInfo", TEST);
    ldapEntry.addAttribute("vgrTempInfo", TEST);
    ldapEntry.addAttribute("managerDN", "cn=Nina Kanin,ou=abc,ou=def");
  }

  @Test
  public void testInstantiation() {
    UnitFactory unitFactory = new UnitFactory();
    assertNotNull(unitFactory);
  }

  @Test
  public void testNullLDAPEntry() throws KivException {
    Unit unit = UnitFactory.reconstitute(null);
    assertNotNull(unit);
  }

  @Test
  public void testReconstitute() throws KivException {
    Unit unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals(EXPECTED_LIST_RESULT, unit.getBusinessClassificationCode().toString());
    assertEquals(TEST, unit.getName());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getCreateTimestamp().asJavaUtilDate()));
    assertEquals(EXPECTED_LIST_RESULT, unit.getDescription().toString());
    assertEquals(EXPECTED_HOURS, unit.getHsaDropInHours().get(0).getDisplayValue());
    assertEquals(TEST, unit.getFacsimileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaGeographicalCoordinates());
    assertEquals(TEST, unit.getHsaAdministrationForm());
    assertEquals(TEST, unit.getHsaAdministrationFormText());
    assertEquals(TEST, unit.getHsaCountyCode());
    assertEquals(TEST, unit.getHsaCountyName());
    assertEquals(TEST, unit.getHsaIdentity());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaInternalAddress().getAdditionalInfo().toString());
    assertEquals(TEST, unit.getHsaInternalPagerNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaManagementCode());
    assertEquals(TEST, unit.getHsaManagementName());
    assertEquals(TEST, unit.getHsaMunicipalitySectionCode());
    assertEquals(TEST, unit.getHsaMunicipalitySectionName());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfDeliveryAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaSedfInvoiceAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaConsigneeAddress().getAdditionalInfo().toString());
    assertEquals(TEST, unit.getHsaSmsTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaSedfSwitchboardTelephoneNo().getPhoneNumber());
    assertEquals(TEST, unit.getHsaTextPhoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaUnitPrescriptionCode());
    assertEquals(TEST + " år", unit.getHsaVisitingRuleAge());
    assertEquals(TEST, unit.getHsaVisitingRules());
    assertEquals(TEST, unit.getLocality());
    assertEquals("http://" + TEST, unit.getLabeledURI());
    assertEquals(TEST, unit.getMail());
    assertEquals(TEST, unit.getMobileTelephoneNumber().getPhoneNumber());
    assertEquals(TEST, unit.getHsaMunicipalityCode());
    assertEquals(TEST, unit.getHsaMunicipalityName());
    assertEquals(TEST, unit.getOrganizationalUnitNameShort());
    assertEquals(TEST, unit.getOu());
    assertEquals(TEST, unit.getPagerTelephoneNumber().getPhoneNumber());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaPostalAddress().getAdditionalInfo().toString());
    assertEquals(EXPECTED_LIST_RESULT, unit.getHsaRoute().toString());
    // assertEquals(TEST, unit.getStreet());
    assertEquals(EXPECTED_HOURS, unit.getHsaSurgeryHours().get(0).getDisplayValue());
    assertEquals(EXPECTED_HOURS, unit.getHsaTelephoneTime().get(0).getDisplayValue());
    assertEquals("[telephoneNumber]", unit.getHsaPublicTelephoneNumber().toString());
    assertEquals("[hsaTelephoneNumber]", unit.getHsaTelephoneNumber().toString());
    assertEquals(TEST, unit.getVgrAO3kod());
    assertEquals(EXPECTED_LIST_RESULT, unit.getVgrAnsvarsnummer().toString());
    assertEquals(TEST, unit.getVgrCareType());
    assertEquals(TEST, unit.getVgrEANCode());
    assertEquals(TEST, unit.getVgrEDICode());
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getModifyTimestamp().asJavaUtilDate()));
    assertEquals(TEST, unit.getVgrRefInfo());
    assertEquals(TEST, unit.getVgrTempInfo());
    assertEquals("Landsting/Region", unit.getHsaManagementText());
    assertEquals(TEST, unit.getName());
    assertEquals("cn=Nina Kanin,ou=abc,ou=def", unit.getManagerDN());
    assertEquals("Nina Kanin", unit.getManager());
  }

  @Test
  public void testReconstituteValidGeoCoordinate() throws KivException {
    ldapEntry.addAttribute("geographicalCoordinates", "X: 1234567, Y: 1234567");
    Unit unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals(1234567, unit.getRt90X());
    assertEquals(1234567, unit.getRt90Y());
    assertEquals(11.159754999084681, unit.getWgs84Lat(), 0.0);
    assertEquals(13.376313261575913, unit.getWgs84Long(), 0.0);
  }

  @Test
  public void testReconstituteNoManagerDN() throws KivException {
    ldapEntry.addAttribute("managerDN", "");
    Unit unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("", unit.getManagerDN());
    assertNull(unit.getManager());
  }

  @Test
  public void testReconstituteOtherManagementDescriptions() throws KivException {
    ldapEntry.addAttribute("management", TEST);
    Unit unit = UnitFactory.reconstitute(ldapEntry);
    assertNull(unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "2");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Kommun", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "3");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Statlig", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "4");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Privat, v\u00E5rdavtal", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "5");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Privat, enl lag om l\u00E4karv\u00E5rdsers\u00E4ttning", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "6");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Privat, utan offentlig finansiering", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "7");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Kommunf\u00F6rbund/Kommunalf\u00F6rbund", unit.getHsaManagementText());
    ldapEntry.addAttribute("management", "9");
    unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("\u00D6vrigt", unit.getHsaManagementText());
  }

  @Test
  public void testUnitNameForCnUnits() throws KivException {
    ldapEntry.addAttribute("ou", "");
    Unit unit = UnitFactory.reconstitute(ldapEntry);
    assertEquals("Unit cn", unit.getName());
  }
}
