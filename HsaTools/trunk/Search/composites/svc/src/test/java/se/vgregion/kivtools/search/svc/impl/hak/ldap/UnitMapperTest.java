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

import static org.junit.Assert.*;

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
  private DirContextOperationsMock dirContextOperations = new DirContextOperationsMock();
  private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Before
  public void setUp() {
    addUnitAttributes();
  }

  @Test
  public void mapPopulatedContextReturnPopulatedUnit() {
    Unit unit = mapper.mapFromContext(dirContextOperations);
    assertEquals(EXPECTED_LIST_RESULT, unit.getBusinessClassificationCode().toString());
    assertEquals(TEST, unit.getName());
    assertEquals(EXPECTED_DATE_TIME, dateFormat.format(unit.getCreateTimestamp().asJavaUtilDate()));
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
    assertEquals(TEST, unit.getVgrCareType());
    assertEquals(TEST, unit.getVgrEANCode());
    assertEquals(TEST, unit.getVgrEDICode());
    assertEquals(TEST, unit.getVgrInternalSedfInvoiceAddress());
    assertEquals(EXPECTED_DATE_TIME2, dateFormat.format(unit.getModifyTimestamp().asJavaUtilDate()));
    assertEquals(TEST, unit.getVgrRefInfo());
    assertEquals(TEST, unit.getVgrTempInfo());
    assertEquals("Landsting/Region", unit.getHsaManagementText());
    assertEquals(TEST, unit.getName());
    assertEquals("cn=Nina Kanin,ou=abc,ou=def", unit.getManagerDN());
    assertEquals("Nina Kanin", unit.getManager());
  }

  @Test
  public void validGeoCoordinatesArePopulatedCorrectly() throws KivException {
    dirContextOperations.addAttributeValue("geographicalCoordinates", "X: 1234567, Y: 1234567");
    Unit unit = mapper.mapFromContext(dirContextOperations);
    assertEquals(1234567, unit.getRt90X());
    assertEquals(1234567, unit.getRt90Y());
    assertEquals(11.159754999084681, unit.getWgs84Lat(), 0.0);
    assertEquals(13.376313261575913, unit.getWgs84Long(), 0.0);
  }

  @Test
  public void noManagerDNResultInNoManagerPopulated() throws KivException {
    dirContextOperations.addAttributeValue("managerDN", "");
    Unit unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("", unit.getManagerDN());
    assertNull(unit.getManager());
  }

  @Test
  public void otherManagementDescriptionsAreMappedCorrectly() throws KivException {
    dirContextOperations.addAttributeValue("management", TEST);
    Unit unit = mapper.mapFromContext(dirContextOperations);
    assertNull(unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "2");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Kommun", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "3");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Statlig", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "4");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Privat, v\u00E5rdavtal", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "5");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Privat, enl lag om l\u00E4karv\u00E5rdsers\u00E4ttning", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "6");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Privat, utan offentlig finansiering", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "7");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Kommunf\u00F6rbund/Kommunalf\u00F6rbund", unit.getHsaManagementText());
    dirContextOperations.addAttributeValue("management", "9");
    unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("\u00D6vrigt", unit.getHsaManagementText());
  }

  @Test
  public void cnIsUsedForUnitNameIfOuIsEmpty() throws KivException {
    dirContextOperations.addAttributeValue("ou", "");
    Unit unit = mapper.mapFromContext(dirContextOperations);
    assertEquals("Unit cn", unit.getName());
  }

  @Test
  public void hsaDestinationIndicatorMapsAllValues() {
    dirContextOperations.addAttributeValue("hsaDestinationIndicator", new String[] { "01", "03" });

    Unit unit = mapper.mapFromContext(dirContextOperations);
    List<String> hsaDestinationIndicator = unit.getHsaDestinationIndicator();
    assertEquals("hsaDestinationIndicator", 2, hsaDestinationIndicator.size());
    assertTrue("01 is not mapped", hsaDestinationIndicator.contains("01"));
    assertTrue("03 is not mapped", hsaDestinationIndicator.contains("03"));
  }

  private void addUnitAttributes() {
    dirContextOperations.setDn(DistinguishedName.immutableDistinguishedName("ou=Vårdcentralen Halmstad,ou=Landstinget Halland"));
    dirContextOperations.addAttributeValue("businessClassificationCode", TEST);
    dirContextOperations.addAttributeValue("cn", CN);
    dirContextOperations.addAttributeValue("createTimeStamp", TEST_TIMESTAMP);
    dirContextOperations.addAttributeValue("description", TEST);
    dirContextOperations.addAttributeValue("dropInHours", TEST_TIME);
    dirContextOperations.addAttributeValue("facsimileTelephoneNumber", TEST);
    dirContextOperations.addAttributeValue("geographicalCoordinates", RT90_COORDS);
    dirContextOperations.addAttributeValue("hsaAdministrationForm", TEST);
    dirContextOperations.addAttributeValue("hsaAdministrationFormText", TEST);
    dirContextOperations.addAttributeValue("hsaCountyCode", TEST);
    dirContextOperations.addAttributeValue("hsaCountyName", TEST);
    dirContextOperations.addAttributeValue("hsaIdentity", TEST);
    dirContextOperations.addAttributeValue("hsaInternalAddress", TEST);
    dirContextOperations.addAttributeValue("hsaInternalPagerNumber", TEST);
    dirContextOperations.addAttributeValue("hsaManagementCode", TEST);
    dirContextOperations.addAttributeValue("hsaManagementName", TEST);
    dirContextOperations.addAttributeValue("hsaMunicipalitySectionCode", TEST);
    dirContextOperations.addAttributeValue("hsaMunicipalitySectionName", TEST);
    dirContextOperations.addAttributeValue("postalAddress", TEST);
    dirContextOperations.addAttributeValue("hsaDeliveryAddress", TEST);
    dirContextOperations.addAttributeValue("hsaInvoiceAddress", TEST);
    dirContextOperations.addAttributeValue("hsaConsigneeAddress", TEST);
    dirContextOperations.addAttributeValue("hsaSmsTelephoneNumber", TEST);
    dirContextOperations.addAttributeValue("hsaSwitchboardNumber", TEST);
    dirContextOperations.addAttributeValue("hsaTextPhoneNumber", TEST);
    dirContextOperations.addAttributeValue("hsaTelephoneNumber", "hsaTelephoneNumber");
    dirContextOperations.addAttributeValue("hsaUnitPrescriptionCode", TEST);
    dirContextOperations.addAttributeValue("hsaVisitingRuleAge", TEST);
    dirContextOperations.addAttributeValue("hsaVisitingRules", TEST);
    dirContextOperations.addAttributeValue("l", TEST);
    dirContextOperations.addAttributeValue("labeledURI", TEST);
    dirContextOperations.addAttributeValue("mail", TEST);
    dirContextOperations.addAttributeValue("management", "1");
    dirContextOperations.addAttributeValue("mobile", TEST);
    dirContextOperations.addAttributeValue("municipalityCode", TEST);
    dirContextOperations.addAttributeValue("municipalityName", TEST);
    dirContextOperations.addAttributeValue("objectClass", TEST);
    dirContextOperations.addAttributeValue("organizationalUnitNameShort", TEST);
    dirContextOperations.addAttributeValue("ou", TEST);
    dirContextOperations.addAttributeValue("pager", TEST);
    dirContextOperations.addAttributeValue("postalAddress", TEST);
    dirContextOperations.addAttributeValue("postalCode", TEST);
    dirContextOperations.addAttributeValue("route", TEST);
    dirContextOperations.addAttributeValue("street", TEST);
    dirContextOperations.addAttributeValue("surgeryHours", TEST_TIME);
    dirContextOperations.addAttributeValue("telephoneHours", TEST_TIME);
    dirContextOperations.addAttributeValue("lthTelephoneNumber", "telephoneNumber");
    dirContextOperations.addAttributeValue("vgrAO3kod", TEST);
    dirContextOperations.addAttributeValue("vgrAnsvarsnummer", TEST);
    dirContextOperations.addAttributeValue("vgrCareType", TEST);
    dirContextOperations.addAttributeValue("vgrEanCode", TEST);
    dirContextOperations.addAttributeValue("vgrEdiCode", TEST);
    dirContextOperations.addAttributeValue("vgrInternalSedfInvoiceAddress", TEST);
    dirContextOperations.addAttributeValue("whenChanged", TEST_TIMESTAMP2);
    dirContextOperations.addAttributeValue("whenCreated", TEST_TIMESTAMP);
    dirContextOperations.addAttributeValue("vgrRefInfo", TEST);
    dirContextOperations.addAttributeValue("vgrTempInfo", TEST);
    dirContextOperations.addAttributeValue("managerDN", "cn=Nina Kanin,ou=abc,ou=def");
  }
}
