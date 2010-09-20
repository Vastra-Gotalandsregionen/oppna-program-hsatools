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

package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.DirContextAdapter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.svc.impl.kiv.ldap.UnitMapperTest.CodeTablesServiceMock;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfFunction;
import se.vgregion.kivtools.search.svc.ws.domain.kivws.ArrayOfUnit;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

import com.thoughtworks.xstream.XStream;

public class KivwsUnitMapperTest {

  private KivwsUnitMapper kivwsUnitMapper;
  private static ArrayOfUnit kivwsObject;
  private static DirContextAdapter kivLdapObject;
  private static ArrayOfFunction kivwsFunctionObject;
  private static DirContextAdapter kivLdapFunctionObject;
  private UnitMapper kivLdapMapper;

  @BeforeClass
  public static void setupTestData() throws IOException, ClassNotFoundException {

    ClassPathResource kivwsUnit = new ClassPathResource("kivwsMajornasVardCentral.xml");
    ClassPathResource kivLdapUnit = new ClassPathResource("kivLdapMajornasVardcentral.xml");
    ClassPathResource kivLdapFunction = new ClassPathResource("kivLdapFunctionObject.xml");
    ClassPathResource kivwsFunction = new ClassPathResource("kivwsFunctionObject.xml");

    XStream xStream = new XStream();
    ObjectInputStream kivwsInputStream = xStream.createObjectInputStream(kivwsUnit.getInputStream());
    ObjectInputStream kivLdapInputStream = xStream.createObjectInputStream(kivLdapUnit.getInputStream());
    ObjectInputStream kivLdapFunctionInputStream = xStream.createObjectInputStream(kivLdapFunction.getInputStream());
    ObjectInputStream kivwsFunctionInputStream = xStream.createObjectInputStream(kivwsFunction.getInputStream());
    kivwsObject = (ArrayOfUnit) kivwsInputStream.readObject();
    kivwsFunctionObject = (ArrayOfFunction) kivwsFunctionInputStream.readObject();
    kivLdapObject = (DirContextAdapter) kivLdapInputStream.readObject();
    kivLdapFunctionObject = (DirContextAdapter) kivLdapFunctionInputStream.readObject();
  }

  @Before
  public void setUp() throws Exception {
    CodeTablesServiceMock codeTablesServiceMock = new UnitMapperTest.CodeTablesServiceMock();
    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    this.kivLdapMapper = new UnitMapper(codeTablesServiceMock, displayValueTranslator);
    this.kivwsUnitMapper = new KivwsUnitMapper(codeTablesServiceMock, displayValueTranslator);
  }

  @Test
  public void testMapFromContext() {
    // Test unit object.
    Unit kivLdapUnitResult = this.kivLdapMapper.mapFromContext(kivLdapObject);
    Unit kivwsUnitResult = this.kivwsUnitMapper.mapFromContext(kivwsObject.getUnit().get(0));
    this.assertResult(kivLdapUnitResult, kivwsUnitResult);
    // Test function object.
    kivLdapUnitResult = this.kivLdapMapper.mapFromContext(kivLdapFunctionObject);
    kivwsUnitResult = this.kivwsUnitMapper.mapFromContext(kivwsFunctionObject.getFunction().get(0));
    this.assertResult(kivLdapUnitResult, kivwsUnitResult);
  }

  @Test(expected = RuntimeException.class)
  public void testException() {
    this.kivwsUnitMapper.mapFromContext(new String());
  }

  private void assertResult(Unit kivLdapUnitResult, Unit kivwsUnitResult) {
    assertNotNull(kivLdapUnitResult);
    assertNotNull(kivwsUnitResult);
    // assertTrue("KivLdapUnit is not equal KivwsUnit",comapreUnits(kivwsUnitResult, kivLdapUnitResult));
  }

  private boolean comapreUnits(Unit kivwsUnit, Unit kivLdapUnit) {
    if (kivLdapUnit.getAccessibilityDatabaseId() == null) {
      if (kivwsUnit.getAccessibilityDatabaseId() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getAccessibilityDatabaseId().equals(kivwsUnit.getAccessibilityDatabaseId())) {
      return false;
    }
    if (kivLdapUnit.getAccessibilityInformation() == null) {
      if (kivwsUnit.getAccessibilityInformation() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getAccessibilityInformation().equals(kivwsUnit.getAccessibilityInformation())) {
      return false;
    }
    if (kivLdapUnit.getCareType() == null) {
      if (kivwsUnit.getCareType() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getCareType().equals(kivwsUnit.getCareType())) {
      return false;
    }
    if (kivLdapUnit.getCareTypeText() == null) {
      if (kivwsUnit.getCareTypeText() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getCareTypeText().equals(kivwsUnit.getCareTypeText())) {
      return false;
    }
    if (kivLdapUnit.getContractCode() == null) {
      if (kivwsUnit.getContractCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getContractCode().equals(kivwsUnit.getContractCode())) {
      return false;
    }
    if (kivLdapUnit.getCreateTimestamp() == null) {
      if (kivwsUnit.getCreateTimestamp() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getCreateTimestamp().equals(kivwsUnit.getCreateTimestamp())) {
      return false;
    }
    if (kivLdapUnit.getDescription() == null) {
      if (kivwsUnit.getDescription() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getDescription().equals(kivwsUnit.getDescription())) {
      return false;
    }
    if (kivLdapUnit.getDistanceToTarget() == null) {
      if (kivwsUnit.getDistanceToTarget() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getDistanceToTarget().equals(kivwsUnit.getDistanceToTarget())) {
      return false;
    }
    if (kivLdapUnit.getDn() == null) {
      if (kivwsUnit.getDn() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getDn().equals(kivwsUnit.getDn())) {
      return false;
    }
    if (kivLdapUnit.getFacsimileTelephoneNumber() == null) {
      if (kivwsUnit.getFacsimileTelephoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getFacsimileTelephoneNumber().equals(kivwsUnit.getFacsimileTelephoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getGeoCoordinate() == null) {
      if (kivwsUnit.getGeoCoordinate() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getGeoCoordinate().equals(kivwsUnit.getGeoCoordinate())) {
      return false;
    }
    if (kivLdapUnit.getHealthcareTypes() == null) {
      if (kivwsUnit.getHealthcareTypes() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHealthcareTypes().equals(kivwsUnit.getHealthcareTypes())) {
      return false;
    }
    if (kivLdapUnit.getHsaAdministrationForm() == null) {
      if (kivwsUnit.getHsaAdministrationForm() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaAdministrationForm().equals(kivwsUnit.getHsaAdministrationForm())) {
      return false;
    }
    if (kivLdapUnit.getHsaAdministrationFormText() == null) {
      if (kivwsUnit.getHsaAdministrationFormText() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaAdministrationFormText().equals(kivwsUnit.getHsaAdministrationFormText())) {
      return false;
    }
    if (kivLdapUnit.getHsaBusinessClassificationCode() == null) {
      if (kivwsUnit.getHsaBusinessClassificationCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaBusinessClassificationCode().equals(kivwsUnit.getHsaBusinessClassificationCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaBusinessClassificationText() == null) {
      if (kivwsUnit.getHsaBusinessClassificationText() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaBusinessClassificationText().equals(kivwsUnit.getHsaBusinessClassificationText())) {
      return false;
    }
    if (kivLdapUnit.getHsaBusinessType() == null) {
      if (kivwsUnit.getHsaBusinessType() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaBusinessType().equals(kivwsUnit.getHsaBusinessType())) {
      return false;
    }
    if (kivLdapUnit.getHsaConsigneeAddress() == null) {
      if (kivwsUnit.getHsaConsigneeAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaConsigneeAddress().equals(kivwsUnit.getHsaConsigneeAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaCountyCode() == null) {
      if (kivwsUnit.getHsaCountyCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaCountyCode().equals(kivwsUnit.getHsaCountyCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaCountyName() == null) {
      if (kivwsUnit.getHsaCountyName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaCountyName().equals(kivwsUnit.getHsaCountyName())) {
      return false;
    }
    if (kivLdapUnit.getHsaDestinationIndicator() == null) {
      if (kivwsUnit.getHsaDestinationIndicator() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaDestinationIndicator().equals(kivwsUnit.getHsaDestinationIndicator())) {
      return false;
    }
    if (kivLdapUnit.getHsaDropInHours() == null) {
      if (kivwsUnit.getHsaDropInHours() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaDropInHours().equals(kivwsUnit.getHsaDropInHours())) {
      return false;
    }
    if (kivLdapUnit.getHsaEndDate() == null) {
      if (kivwsUnit.getHsaEndDate() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaEndDate().equals(kivwsUnit.getHsaEndDate())) {
      return false;
    }
    if (kivLdapUnit.getHsaGeographicalCoordinates() == null) {
      if (kivwsUnit.getHsaGeographicalCoordinates() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaGeographicalCoordinates().equals(kivwsUnit.getHsaGeographicalCoordinates())) {
      return false;
    }
    if (kivLdapUnit.getHsaIdentity() == null) {
      if (kivwsUnit.getHsaIdentity() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaIdentity().equals(kivwsUnit.getHsaIdentity())) {
      return false;
    }
    if (kivLdapUnit.getHsaInternalAddress() == null) {
      if (kivwsUnit.getHsaInternalAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaInternalAddress().equals(kivwsUnit.getHsaInternalAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaInternalPagerNumber() == null) {
      if (kivwsUnit.getHsaInternalPagerNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaInternalPagerNumber().equals(kivwsUnit.getHsaInternalPagerNumber())) {
      return false;
    }
    if (kivLdapUnit.getHsaManagementCode() == null) {
      if (kivwsUnit.getHsaManagementCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaManagementCode().equals(kivwsUnit.getHsaManagementCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaManagementName() == null) {
      if (kivwsUnit.getHsaManagementName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaManagementName().equals(kivwsUnit.getHsaManagementName())) {
      return false;
    }
    if (kivLdapUnit.getHsaManagementText() == null) {
      if (kivwsUnit.getHsaManagementText() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaManagementText().equals(kivwsUnit.getHsaManagementText())) {
      return false;
    }
    if (kivLdapUnit.getHsaMunicipalityCode() == null) {
      if (kivwsUnit.getHsaMunicipalityCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaMunicipalityCode().equals(kivwsUnit.getHsaMunicipalityCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaMunicipalityName() == null) {
      if (kivwsUnit.getHsaMunicipalityName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaMunicipalityName().equals(kivwsUnit.getHsaMunicipalityName())) {
      return false;
    }
    if (kivLdapUnit.getHsaMunicipalitySectionCode() == null) {
      if (kivwsUnit.getHsaMunicipalitySectionCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaMunicipalitySectionCode().equals(kivwsUnit.getHsaMunicipalitySectionCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaMunicipalitySectionName() == null) {
      if (kivwsUnit.getHsaMunicipalitySectionName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaMunicipalitySectionName().equals(kivwsUnit.getHsaMunicipalitySectionName())) {
      return false;
    }
    if (kivLdapUnit.getHsaPatientVisitingRules() == null) {
      if (kivwsUnit.getHsaPatientVisitingRules() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaPatientVisitingRules().equals(kivwsUnit.getHsaPatientVisitingRules())) {
      return false;
    }
    if (kivLdapUnit.getHsaPostalAddress() == null) {
      if (kivwsUnit.getHsaPostalAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaPostalAddress().equals(kivwsUnit.getHsaPostalAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaPublicTelephoneNumber() == null) {
      if (kivwsUnit.getHsaPublicTelephoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaPublicTelephoneNumber().equals(kivwsUnit.getHsaPublicTelephoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getHsaRoute() == null) {
      if (kivwsUnit.getHsaRoute() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaRoute().equals(kivwsUnit.getHsaRoute())) {
      return false;
    }
    if (kivLdapUnit.getHsaRouteConcatenated() == null) {
      if (kivwsUnit.getHsaRouteConcatenated() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaRouteConcatenated().equals(kivwsUnit.getHsaRouteConcatenated())) {
      return false;
    }
    if (kivLdapUnit.getHsaSedfDeliveryAddress() == null) {
      if (kivwsUnit.getHsaSedfDeliveryAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaSedfDeliveryAddress().equals(kivwsUnit.getHsaSedfDeliveryAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaSedfInvoiceAddress() == null) {
      if (kivwsUnit.getHsaSedfInvoiceAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaSedfInvoiceAddress().equals(kivwsUnit.getHsaSedfInvoiceAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaSedfSwitchboardTelephoneNo() == null) {
      if (kivwsUnit.getHsaSedfSwitchboardTelephoneNo() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaSedfSwitchboardTelephoneNo().equals(kivwsUnit.getHsaSedfSwitchboardTelephoneNo())) {
      return false;
    }
    if (kivLdapUnit.getHsaStreetAddress() == null) {
      if (kivwsUnit.getHsaStreetAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaStreetAddress().equals(kivwsUnit.getHsaStreetAddress())) {
      return false;
    }
    if (kivLdapUnit.getHsaSurgeryHours() == null) {
      if (kivwsUnit.getHsaSurgeryHours() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaSurgeryHours().equals(kivwsUnit.getHsaSurgeryHours())) {
      return false;
    }
    if (kivLdapUnit.getHsaTelephoneNumber() == null) {
      if (kivwsUnit.getHsaTelephoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaTelephoneNumber().equals(kivwsUnit.getHsaTelephoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getHsaTelephoneTime() == null) {
      if (kivwsUnit.getHsaTelephoneTime() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaTelephoneTime().equals(kivwsUnit.getHsaTelephoneTime())) {
      return false;
    }
    if (kivLdapUnit.getHsaTextPhoneNumber() == null) {
      if (kivwsUnit.getHsaTextPhoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaTextPhoneNumber().equals(kivwsUnit.getHsaTextPhoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getHsaUnitPrescriptionCode() == null) {
      if (kivwsUnit.getHsaUnitPrescriptionCode() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaUnitPrescriptionCode().equals(kivwsUnit.getHsaUnitPrescriptionCode())) {
      return false;
    }
    if (kivLdapUnit.getHsaVisitingRuleAge() == null) {
      if (kivwsUnit.getHsaVisitingRuleAge() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaVisitingRuleAge().equals(kivwsUnit.getHsaVisitingRuleAge())) {
      return false;
    }
    if (kivLdapUnit.getHsaVisitingRules() == null) {
      if (kivwsUnit.getHsaVisitingRules() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getHsaVisitingRules().equals(kivwsUnit.getHsaVisitingRules())) {
      return false;
    }
    if (kivLdapUnit.getInternalWebsite() == null) {
      if (kivwsUnit.getInternalWebsite() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getInternalWebsite().equals(kivwsUnit.getInternalWebsite())) {
      return false;
    }
    if (kivLdapUnit.getIsUnit() != kivwsUnit.getIsUnit()) {
      return false;
    }
    if (kivLdapUnit.getLabeledURI() == null) {
      if (kivwsUnit.getLabeledURI() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getLabeledURI().equals(kivwsUnit.getLabeledURI())) {
      return false;
    }
    if (kivLdapUnit.getLdapDistinguishedName() == null) {
      if (kivwsUnit.getLdapDistinguishedName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getLdapDistinguishedName().equals(kivwsUnit.getLdapDistinguishedName())) {
      return false;
    }
    if (kivLdapUnit.getLocality() == null) {
      if (kivwsUnit.getLocality() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getLocality().equals(kivwsUnit.getLocality())) {
      return false;
    }
    if (kivLdapUnit.getMail() == null) {
      if (kivwsUnit.getMail() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getMail().equals(kivwsUnit.getMail())) {
      return false;
    }
    if (kivLdapUnit.getManager() == null) {
      if (kivwsUnit.getManager() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getManager().equals(kivwsUnit.getManager())) {
      return false;
    }
    if (kivLdapUnit.getManagerDN() == null) {
      if (kivwsUnit.getManagerDN() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getManagerDN().equals(kivwsUnit.getManagerDN())) {
      return false;
    }
    if (kivLdapUnit.getMobileTelephoneNumber() == null) {
      if (kivwsUnit.getMobileTelephoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getMobileTelephoneNumber().equals(kivwsUnit.getMobileTelephoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getModifyTimestamp() == null) {
      if (kivwsUnit.getModifyTimestamp() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getModifyTimestamp().equals(kivwsUnit.getModifyTimestamp())) {
      return false;
    }
    if (kivLdapUnit.getMvkCaseTypes() == null) {
      if (kivwsUnit.getMvkCaseTypes() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getMvkCaseTypes().equals(kivwsUnit.getMvkCaseTypes())) {
      return false;
    }
    if (kivLdapUnit.getName() == null) {
      if (kivwsUnit.getName() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getName().equals(kivwsUnit.getName())) {
      return false;
    }
    if (kivLdapUnit.getObjectClass() == null) {
      if (kivwsUnit.getObjectClass() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getObjectClass().equals(kivwsUnit.getObjectClass())) {
      return false;
    }
    if (kivLdapUnit.getOrganizationalUnitNameShort() == null) {
      if (kivwsUnit.getOrganizationalUnitNameShort() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getOrganizationalUnitNameShort().equals(kivwsUnit.getOrganizationalUnitNameShort())) {
      return false;
    }
    if (kivLdapUnit.getOu() == null) {
      if (kivwsUnit.getOu() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getOu().equals(kivwsUnit.getOu())) {
      return false;
    }
    if (kivLdapUnit.getPagerTelephoneNumber() == null) {
      if (kivwsUnit.getPagerTelephoneNumber() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getPagerTelephoneNumber().equals(kivwsUnit.getPagerTelephoneNumber())) {
      return false;
    }
    if (kivLdapUnit.getRt90X() != kivwsUnit.getRt90X()) {
      return false;
    }
    if (kivLdapUnit.getRt90Y() != kivwsUnit.getRt90Y()) {
      return false;
    }
    if (kivLdapUnit.isShowAgeInterval() != kivwsUnit.isShowAgeInterval()) {
      return false;
    }
    if (kivLdapUnit.isShowVisitingRules() != kivwsUnit.isShowVisitingRules()) {
      return false;
    }
    if (kivLdapUnit.getVgrAO3kod() == null) {
      if (kivwsUnit.getVgrAO3kod() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrAO3kod().equals(kivwsUnit.getVgrAO3kod())) {
      return false;
    }
    if (kivLdapUnit.getVgrAO3kodText() == null) {
      if (kivwsUnit.getVgrAO3kodText() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrAO3kodText().equals(kivwsUnit.getVgrAO3kodText())) {
      return false;
    }
    if (kivLdapUnit.getVgrAnsvarsnummer() == null) {
      if (kivwsUnit.getVgrAnsvarsnummer() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrAnsvarsnummer().equals(kivwsUnit.getVgrAnsvarsnummer())) {
      return false;
    }
    if (kivLdapUnit.getVgrInternalSedfInvoiceAddress() == null) {
      if (kivwsUnit.getVgrInternalSedfInvoiceAddress() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrInternalSedfInvoiceAddress().equals(kivwsUnit.getVgrInternalSedfInvoiceAddress())) {
      return false;
    }
    if (kivLdapUnit.getVgrOrganizationalRole() == null) {
      if (kivwsUnit.getVgrOrganizationalRole() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrOrganizationalRole().equals(kivwsUnit.getVgrOrganizationalRole())) {
      return false;
    }
    if (kivLdapUnit.getVgrRefInfo() == null) {
      if (kivwsUnit.getVgrRefInfo() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrRefInfo().equals(kivwsUnit.getVgrRefInfo())) {
      return false;
    }
    if (kivLdapUnit.getVgrTempInfo() == null) {
      if (kivwsUnit.getVgrTempInfo() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrTempInfo().equals(kivwsUnit.getVgrTempInfo())) {
      return false;
    }
    if (kivLdapUnit.getVgrTempInfoBody() == null) {
      if (kivwsUnit.getVgrTempInfoBody() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrTempInfoBody().equals(kivwsUnit.getVgrTempInfoBody())) {
      return false;
    }
    if (kivLdapUnit.getVgrTempInfoEnd() == null) {
      if (kivwsUnit.getVgrTempInfoEnd() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrTempInfoEnd().equals(kivwsUnit.getVgrTempInfoEnd())) {
      return false;
    }
    if (kivLdapUnit.getVgrTempInfoStart() == null) {
      if (kivwsUnit.getVgrTempInfoStart() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVgrTempInfoStart().equals(kivwsUnit.getVgrTempInfoStart())) {
      return false;
    }
    if (kivLdapUnit.isVgrVardVal() != kivwsUnit.isVgrVardVal()) {
      return false;
    }
    if (kivLdapUnit.getVisitingHours() == null) {
      if (kivwsUnit.getVisitingHours() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVisitingHours().equals(kivwsUnit.getVisitingHours())) {
      return false;
    }
    if (kivLdapUnit.getVisitingRuleReferral() == null) {
      if (kivwsUnit.getVisitingRuleReferral() != null) {
        return false;
      }
    } else if (!kivLdapUnit.getVisitingRuleReferral().equals(kivwsUnit.getVisitingRuleReferral())) {
      return false;
    }
    if (Double.doubleToLongBits(kivLdapUnit.getWgs84Lat()) != Double.doubleToLongBits(kivwsUnit.getWgs84Lat())) {
      return false;
    }
    if (Double.doubleToLongBits(kivLdapUnit.getWgs84Long()) != Double.doubleToLongBits(kivwsUnit.getWgs84Long())) {
      return false;
    }
    return true;
  }
}
