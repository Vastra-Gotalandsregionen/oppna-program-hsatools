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
 */
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.exceptions.KivException;

public class UnitMapperTest {
  private static final String RT90_COORDS = "X: 6389622, Y: 1357246";
  private static final String CN = "Unit cn";
  private static final String TEST = "Test";
  private static final String EXPECTED_LIST_RESULT = "[" + TEST + "]";
  private static final String EXPECTED_DATE_TIME = "2009-01-01 12:01:02";
  private static final String EXPECTED_DATE_TIME2 = "2010-02-17 12:01:02";
  private static final String EXPECTED_HOURS = "Måndag-Fredag 08:30-10:00";
  private static final String TEST_TIME = "1-5#08:30#10:00";
  private static final String TEST_TIMESTAMP = "20090101120102";
  private static final String TEST_TIMESTAMP2 = "20100217120102";

  private final UnitMapper mapper = new UnitMapper();
  private final DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Before
  public void setUp() {
    this.addUnitAttributes();
  }

  @Test
  public void mapPopulatedContextReturnPopulatedUnit() {
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals(EXPECTED_LIST_RESULT, unit.getBusinessClassificationCode().toString());
    assertEquals(TEST, unit.getName());
    assertEquals(EXPECTED_DATE_TIME, this.dateFormat.format(unit.getCreateTimestamp().asJavaUtilDate()));
    assertEquals(EXPECTED_LIST_RESULT, unit.getDescription().toString());
    assertEquals(EXPECTED_HOURS, unit.getHsaDropInHours().get(0).getDisplayValue());
    assertEquals(TEST, unit.getFacsimileTelephoneNumber().getPhoneNumber());
    assertEquals(RT90_COORDS, unit.getHsaGeographicalCoordinates());
    assertEquals(57.609, unit.getGeoCoordinate().getLatitude(), 0.001);
    assertEquals(13.416, unit.getGeoCoordinate().getLongitude(), 0.001);
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
    assertEquals(TEST, unit.getCareType());
    assertEquals(TEST, unit.getVgrEANCode());
    assertEquals(TEST, unit.getVgrEDICode());
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    assertEquals(EXPECTED_DATE_TIME2, this.dateFormat.format(unit.getModifyTimestamp().asJavaUtilDate()));
    assertEquals("Landsting/Region", unit.getHsaManagementText());
    assertEquals(TEST, unit.getName());
    assertEquals("cn=Nina Kanin,ou=abc,ou=def", unit.getManagerDN());
    assertEquals("Nina Kanin", unit.getManager());
    assertEquals("alternativ text", unit.getHsaAltText());
    assertEquals("mer om", unit.getHsaVpwInformation1());
    assertEquals("tillfällig info", unit.getHsaVpwInformation2());
  }

  @Test
  public void validGeoCoordinatesArePopulatedCorrectly() throws KivException {
    this.dirContextOperations.addAttributeValue("geographicalCoordinates", "X: 1234567, Y: 1234567");
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals(1234567, unit.getRt90X());
    assertEquals(1234567, unit.getRt90Y());
    assertEquals(11.159754999084681, unit.getWgs84Lat(), 0.0);
    assertEquals(13.376313261575913, unit.getWgs84Long(), 0.0);
  }

  @Test
  public void noManagerDNResultInNoManagerPopulated() throws KivException {
    this.dirContextOperations.addAttributeValue("managerDN", "");
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("", unit.getManagerDN());
    assertNull(unit.getManager());
  }

  @Test
  public void otherManagementDescriptionsAreMappedCorrectly() throws KivException {
    this.dirContextOperations.addAttributeValue("management", TEST);
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertNull(unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "2");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Kommun", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "3");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Statlig", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "4");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Privat, v\u00E5rdavtal", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "5");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Privat, enl lag om l\u00E4karv\u00E5rdsers\u00E4ttning", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "6");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Privat, utan offentlig finansiering", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "7");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Kommunf\u00F6rbund/Kommunalf\u00F6rbund", unit.getHsaManagementText());
    this.dirContextOperations.addAttributeValue("management", "9");
    unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("\u00D6vrigt", unit.getHsaManagementText());
  }

  @Test
  public void cnIsUsedForUnitNameIfOuIsEmpty() throws KivException {
    this.dirContextOperations.addAttributeValue("ou", "");
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertEquals("Unit cn", unit.getName());
  }

  @Test
  public void hsaDestinationIndicatorMapsAllValues() {
    this.dirContextOperations.addAttributeValue("hsaDestinationIndicator", new String[] { "01", "03" });

    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    List<String> hsaDestinationIndicator = unit.getHsaDestinationIndicator();
    assertEquals("hsaDestinationIndicator", 2, hsaDestinationIndicator.size());
    assertTrue("01 is not mapped", hsaDestinationIndicator.contains("01"));
    assertTrue("03 is not mapped", hsaDestinationIndicator.contains("03"));
  }

  @Test
  public void isShowAgeIntervalReturnFalseIfUnitIsAvailableForAllAges() {
    this.dirContextOperations.addAttributeValue("hsaVisitingRuleAge", "00-99");
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertFalse("show age interval", unit.isShowAgeInterval());
  }

  @Test
  public void isShowAgeIntervalReturnTrueForValidAgeRange() {
    this.dirContextOperations.addAttributeValue("hsaVisitingRuleAge", "18-24");
    Unit unit = this.mapper.mapFromContext(this.dirContextOperations);
    assertTrue("show age interval", unit.isShowAgeInterval());
  }

  private void addUnitAttributes() {
    this.dirContextOperations.setDn(DistinguishedName.immutableDistinguishedName("ou=Vårdcentralen Halmstad,ou=Landstinget Halland"));
    this.dirContextOperations.addAttributeValue("businessClassificationCode", TEST);
    this.dirContextOperations.addAttributeValue("cn", CN);
    this.dirContextOperations.addAttributeValue("createTimeStamp", TEST_TIMESTAMP);
    this.dirContextOperations.addAttributeValue("description", TEST);
    this.dirContextOperations.addAttributeValue("dropInHours", TEST_TIME);
    this.dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("geographicalCoordinates", RT90_COORDS);
    this.dirContextOperations.addAttributeValue("hsaAdministrationForm", TEST);
    this.dirContextOperations.addAttributeValue("hsaAdministrationFormText", TEST);
    this.dirContextOperations.addAttributeValue("hsaCountyCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaCountyName", TEST);
    this.dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    this.dirContextOperations.addAttributeValue("hsaInternalAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST);
    this.dirContextOperations.addAttributeValue("hsaManagementCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaManagementName", TEST);
    this.dirContextOperations.addAttributeValue("hsaMunicipalitySectionCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaMunicipalitySectionName", TEST);
    this.dirContextOperations.addAttributeValue("postalAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST);
    this.dirContextOperations.addAttributeValue("hsaSmsTelephoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST);
    this.dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST);
    this.dirContextOperations.addAttributeValue("hsaTelephoneNumber", "hsaTelephoneNumber");
    this.dirContextOperations.addAttributeValue("hsaUnitPrescriptionCode", TEST);
    this.dirContextOperations.addAttributeValue("hsaVisitingRuleAge", TEST);
    this.dirContextOperations.addAttributeValue("hsaVisitingRules", TEST);
    this.dirContextOperations.addAttributeValue("l", TEST);
    this.dirContextOperations.addAttributeValue("labeledURI", TEST);
    this.dirContextOperations.addAttributeValue("mail", TEST);
    this.dirContextOperations.addAttributeValue("management", "1");
    this.dirContextOperations.addAttributeValue("mobile", TEST);
    this.dirContextOperations.addAttributeValue("municipalityCode", TEST);
    this.dirContextOperations.addAttributeValue("municipalityName", TEST);
    this.dirContextOperations.addAttributeValue("objectClass", TEST);
    this.dirContextOperations.addAttributeValue("ouShort", TEST);
    this.dirContextOperations.addAttributeValue("ou", TEST);
    this.dirContextOperations.addAttributeValue("pager", TEST);
    this.dirContextOperations.addAttributeValue("postalAddress", TEST);
    this.dirContextOperations.addAttributeValue("postalCode", TEST);
    this.dirContextOperations.addAttributeValue("route", TEST);
    this.dirContextOperations.addAttributeValue("street", TEST);
    this.dirContextOperations.addAttributeValue("surgeryHours", TEST_TIME);
    this.dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    this.dirContextOperations.addAttributeValue("lthTelephoneNumber", "telephoneNumber");
    this.dirContextOperations.addAttributeValue("vgrAO3kod", TEST);
    this.dirContextOperations.addAttributeValue("vgrAnsvarsnummer", TEST);
    this.dirContextOperations.addAttributeValue("careType", TEST);
    this.dirContextOperations.addAttributeValue("vgrEanCode", TEST);
    this.dirContextOperations.addAttributeValue("vgrEdiCode", TEST);
    this.dirContextOperations.addAttributeValue("vgrInternalSedfInvoiceAddress", TEST);
    this.dirContextOperations.addAttributeValue("whenChanged", TEST_TIMESTAMP2);
    this.dirContextOperations.addAttributeValue("whenCreated", TEST_TIMESTAMP);
    this.dirContextOperations.addAttributeValue("managerDN", "cn=Nina Kanin,ou=abc,ou=def");
    this.dirContextOperations.addAttributeValue("hsaAltText", "alternativ text");
    this.dirContextOperations.addAttributeValue("hsaVpwInformation1", "mer om");
    this.dirContextOperations.addAttributeValue("hsaVpwInformation2", "tillfällig info");
  }
}
