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

package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.DistinguishedName;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.mocks.ldap.LdapTemplateMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;

public class UnitRepositoryTest {
  private UnitRepository unitRepository;

  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    unitRepository = new UnitRepository();
    ldapTemplate = new LdapTemplateMock();
    unitRepository.setLdapTemplate(ldapTemplate);

    // Instantiate HealthcareTypeConditionHelper
    HealthcareTypeConditionHelper healthcareTypeConditionHelper = new HealthcareTypeConditionHelper() {
      {
        resetInternalCache();
      }
    };
    healthcareTypeConditionHelper.setImplResourcePath("basic_healthcaretypeconditionhelper");
  }

  @Test
  public void testSearchUnit() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitId("\"abc-123\"");
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    // Create ldapConnectionMock.

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(&(hsaIdentity=abc-123)(ou=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*))))(&(objectclass=organizationalRole)(&(hsaIdentity=abc-123)(cn=*unitName*)(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*)))))";

    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test
  public void testSearchUnitNameWithDash() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("Kvalitet- och säkerhetsavdelningen");

    // Create ldapConnectionMock.

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(ou=*Kvalitet*och*säkerhetsavdelningen*))(&(objectclass=organizationalRole)(cn=*Kvalitet*och*säkerhetsavdelningen*)))";
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test(expected = KivException.class)
  public void searchUnitsThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    unitRepository.searchUnits(searchUnitCriterions, 1);
  }

  @Test
  public void testSearchAdvancedUnit() throws KivException {
    Unit unit = new Unit();
    unit.setName("vårdcentral");

    String expectedFilterString = "(|(&(objectClass=organizationalUnit)(|(description=*vårdcentral*)(ou=*vårdcentral*)))(&(objectClass=organizationalRole)(|(description=*vårdcentral*)(cn=*vårdcentral*))))";

    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "1");
    entry1.addAttributeValue(Constants.LDAP_PROPERTY_UNIT_NAME, "abbesta");
    entry1.addAttributeValue(Constants.LDAP_PROPERTY_DESCRIPTION, "vårdcentral");
    entry1.addAttributeValue("businessClassificationCode", "1");
    this.ldapTemplate.addDirContextOperationForSearch(entry1);

    DirContextOperationsMock entry2 = new DirContextOperationsMock();
    entry2.addAttributeValue("hsaIdentity", "2");
    entry2.addAttributeValue(Constants.LDAP_PROPERTY_UNIT_NAME, "vårdcentral prästkragen");
    entry2.addAttributeValue(Constants.LDAP_PROPERTY_DESCRIPTION, "bla bla");
    entry2.addAttributeValue("businessClassificationCode", "1");
    this.ldapTemplate.addDirContextOperationForSearch(entry2);

    unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), Arrays.asList(1));
    ldapTemplate.assertSearchFilter(expectedFilterString);
  }

  @Test
  public void testSearchAdvancedUnitExactMatch() throws KivException {
    Unit unit = new Unit();
    unit.setDescription(Arrays.asList("\"description\""));
    unit.setHsaMunicipalityName("\"Kungsbacka\"");

    String expectedFilterString = "(|(&(objectClass=organizationalUnit)(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka))))(&(objectClass=organizationalRole)(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka)))))";

    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "1");
    this.ldapTemplate.addDirContextOperationForSearch(entry1);

    unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), Arrays.asList(1));
    ldapTemplate.assertSearchFilter(expectedFilterString);
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";

    List<String> allUnitsHsaIdentity = unitRepository.getAllUnitsHsaIdentity();

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }

  @Test
  public void testGetAllUnitsHsaIdentityBusinessClassificationCodesSpecified() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(businessClassificationCode=123)(businessClassificationCode=456)(businessClassificationCode=1500)(&(hsaIdentity=SE6460000000-E000000000222)(vgrAnsvarsnummer=12345))(&(hsaIdentity=SE2321000131-E000000000110)(|(vgrAO3kod=5a3)(vgrAO3kod=4d7)(vgrAO3kod=1xp))))(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";

    List<String> allUnitsHsaIdentity = unitRepository.getAllUnitsHsaIdentity(Arrays.asList(Integer.valueOf(123), Integer.valueOf(456)));

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }

  @Test(expected = KivException.class)
  public void getAllUnitsHsaIdentityThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    unitRepository.getAllUnitsHsaIdentity();
  }

  @Test
  public void testRemoveUnallowedUnits() throws KivException {
    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "abc-123");
    entry1.addAttributeValue("businessClassificationCode", "1");
    entry1.addAttributeValue("vgrAnsvarsnummer", "11223");
    this.ldapTemplate.addDirContextOperationForSearch(entry1);

    DirContextOperationsMock entry2 = new DirContextOperationsMock();
    entry2.addAttributeValue("hsaIdentity", "abc-456");
    entry2.addAttributeValue("businessClassificationCode", "1504");
    this.ldapTemplate.addDirContextOperationForSearch(entry2);

    DirContextOperationsMock entry3 = new DirContextOperationsMock();
    entry3.addAttributeValue("hsaIdentity", "SE6460000000-E000000000222");
    entry3.addAttributeValue("businessClassificationCode", "abc");
    this.ldapTemplate.addDirContextOperationForSearch(entry3);

    DirContextOperationsMock entry4 = new DirContextOperationsMock();
    entry4.addAttributeValue("hsaIdentity", "abc-789");
    entry4.addAttributeValue("businessClassificationCode", "1");
    entry4.addAttributeValue("vgrAnsvarsnummer", "12345");
    this.ldapTemplate.addDirContextOperationForSearch(entry4);

    HealthcareType healthcareType = new HealthcareType();
    healthcareType.addCondition("conditionKey", "value1,value2");

    Unit searchUnit = new Unit();
    searchUnit.setName("unitName");
    searchUnit.setHsaMunicipalityName("Göteborg");
    searchUnit.setHsaMunicipalityCode("10032");
    searchUnit.setHsaIdentity("hsaId-1");
    searchUnit.setHealthcareTypes(Arrays.asList(healthcareType));
    searchUnit.setVgrVardVal(true);

    int maxResults = 10;
    UnitNameComparator sortOrder = new UnitNameComparator();
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(searchUnit, maxResults, sortOrder, Arrays.asList(Integer.valueOf(1504)));
    assertNotNull(units);
    assertEquals(3, units.size());
  }

  @Test
  public void getUnitByHsaIdReturnEmptyUnitIfNoUnitIsFound() throws KivException {
    String expectedFilter = "(hsaIdentity=abc-123)";

    Unit unit = unitRepository.getUnitByHsaId("abc-123");

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertNull(unit.getHsaIdentity());
  }

  @Test
  public void getUnitByHsaIdReturnFoundUnitIfFound() throws KivException {
    createEntry("SE6460000000-E000000000222", "");
    String expectedFilter = "(hsaIdentity=abc-123)";

    Unit unit = unitRepository.getUnitByHsaId("abc-123");

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals("SE6460000000-E000000000222", unit.getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getUnitByHsaIdThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    unitRepository.getUnitByHsaId("abc-123");
  }

  @Test
  public void extractResultReturnNoDuplicateHsaIdentities() throws KivException {
    createEntry("SE6460000000-E000000000222", "");
    createEntry("SE6460000000-E000000000222", "");

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(unit, 10, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(1, units.size());
  }

  @Test
  public void extractResultReturnNoMoreThanMaxResultUnits() throws KivException {
    createEntry("abc-123", "1500");
    createEntry("def-456", "1500");
    createEntry("ghi-789", "1500");

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(unit, 2, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(2, units.size());
    assertEquals(3, units.getTotalNumberOfFoundItems());
  }

  private void createEntry(String hsaIdentity, String businessClassificationCode) {
    DirContextOperationsMock entry = new DirContextOperationsMock();
    entry.addAttributeValue("hsaIdentity", hsaIdentity);
    entry.addAttributeValue("businessClassificationCode", businessClassificationCode);
    this.ldapTemplate.addDirContextOperationForSearch(entry);
  }

  @Test
  public void testGetUnitByDN() throws KivException {
    DirContextOperationsMock dirContext = new DirContextOperationsMock();
    DistinguishedName dn = DistinguishedName.immutableDistinguishedName("ou=Vårdcentralen Halmstad,o=Landstinget Halland");
    dirContext.setDn(dn);
    dirContext.addAttributeValue("hsaIdentity", "abc-123");
    this.ldapTemplate.addBoundDN(DistinguishedName.immutableDistinguishedName("ou=Vårdcentralen Halmstad,o=Landstinget Halland"), dirContext);

    Unit unit = unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,o=Landstinget Halland"));
    assertNotNull(unit);
    assertEquals("abc-123", unit.getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getUnitByDNThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,ou=Landstinget Halland"));
  }

  @Test
  public void testGetAllUnitsBusinessClassificationCodesSpecified() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(businessClassificationCode=123)(businessClassificationCode=456)(businessClassificationCode=1500)(&(hsaIdentity=SE6460000000-E000000000222)(vgrAnsvarsnummer=12345))(&(hsaIdentity=SE2321000131-E000000000110)(|(vgrAO3kod=5a3)(vgrAO3kod=4d7)(vgrAO3kod=1xp))))(|(objectclass=organizationalUnit)(objectclass=organizationalRole)))";

    List<Unit> allUnits = unitRepository.getAllUnits(Arrays.asList(Integer.valueOf(123), Integer.valueOf(456)));

    ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnits.size());
    assertEquals("ABC-123", allUnits.get(0).getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getAllUnitsThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    unitRepository.getAllUnits(new ArrayList<Integer>());
  }
}
