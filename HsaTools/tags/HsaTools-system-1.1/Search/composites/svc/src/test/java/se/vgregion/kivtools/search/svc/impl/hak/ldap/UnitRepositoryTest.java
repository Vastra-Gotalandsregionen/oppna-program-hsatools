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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    this.unitRepository = new UnitRepository();
    this.ldapTemplate = new LdapTemplateMock();
    this.unitRepository.setLdapTemplate(this.ldapTemplate);

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
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitId("\"abc-123\"");
    searchUnitCriterions.setUnitName("unitName");
    searchUnitCriterions.setLocation("municipalityName");

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(&(hsaIdentity=abc-123)(|(ou=*unitName*)(description=*unitName*))(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*))))(&(objectclass=organizationalRole)(&(hsaIdentity=abc-123)(|(cn=*unitName*)(description=*unitName*))(|(municipalityName=*municipalityName*)(postalAddress=*municipalityName*)(streetAddress=*municipalityName*)))))";

    SikSearchResultList<Unit> searchUnits = this.unitRepository.searchUnits(searchUnitCriterions, 0);
    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test
  public void testSearchUnitNameWithDash() throws KivException {
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("Kvalitet- och säkerhetsavdelningen");

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(|(ou=*Kvalitet*och*säkerhetsavdelningen*)(description=*Kvalitet*och*säkerhetsavdelningen*)))(&(objectclass=organizationalRole)(|(cn=*Kvalitet*och*säkerhetsavdelningen*)(description=*Kvalitet*och*säkerhetsavdelningen*))))";
    SikSearchResultList<Unit> searchUnits = this.unitRepository.searchUnits(searchUnitCriterions, 0);
    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test
  public void searchUnitMatchesProvidedUnitNameAgainstDescriptionAsWellAsUnitName() throws KivException {
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("teknik");

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(|(ou=*teknik*)(description=*teknik*)))(&(objectclass=organizationalRole)(|(cn=*teknik*)(description=*teknik*))))";
    SikSearchResultList<Unit> searchUnits = this.unitRepository.searchUnits(searchUnitCriterions, 0);
    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
  }

  @Test(expected = KivException.class)
  public void searchUnitsThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    this.unitRepository.searchUnits(searchUnitCriterions, 1);
  }

  @Test
  public void testSearchAdvancedUnit() throws KivException {
    Unit unit = new Unit();
    unit.setName("vårdcentral");

    String expectedFilterString = "(&(|(&(objectClass=organizationalUnit)(|(description=*vårdcentral*)(ou=*vårdcentral*)))(&(objectClass=organizationalRole)(|(description=*vårdcentral*)(cn=*vårdcentral*))))(hsaDestinationIndicator=03))";

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

    this.unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), true);
    this.ldapTemplate.assertSearchFilter(expectedFilterString);
  }

  @Test
  public void providingBothNameAndMunicipalityToSearchAdvancedUnitsResultInAndAndQuery() throws KivException {
    Unit unit = new Unit();
    unit.setName("vårdcentral");
    unit.setHsaMunicipalityName("Kungsbacka");

    String expectedFilterString = "(&(|(&(objectClass=organizationalUnit)(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*)))(|(description=*vårdcentral*)(ou=*vårdcentral*)))(&(objectClass=organizationalRole)(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*)))(|(description=*vårdcentral*)(cn=*vårdcentral*))))(hsaDestinationIndicator=03))";

    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "1");
    entry1.addAttributeValue(Constants.LDAP_PROPERTY_UNIT_NAME, "abbesta");
    entry1.addAttributeValue(Constants.LDAP_PROPERTY_DESCRIPTION, "vårdcentral");
    entry1.addAttributeValue("businessClassificationCode", "1");
    this.ldapTemplate.addDirContextOperationForSearch(entry1);

    this.unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), true);
    this.ldapTemplate.assertSearchFilter(expectedFilterString);
  }

  @Test
  public void testSearchAdvancedUnitExactMatch() throws KivException {
    Unit unit = new Unit();
    unit.addDescription(Arrays.asList("\"description\""));
    unit.setHsaMunicipalityName("\"Kungsbacka\"");

    String expectedFilterString = "(&(|(&(objectClass=organizationalUnit)(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka))))(&(objectClass=organizationalRole)(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka)))))(hsaDestinationIndicator=03))";

    DirContextOperationsMock entry1 = new DirContextOperationsMock();
    entry1.addAttributeValue("hsaIdentity", "1");
    this.ldapTemplate.addDirContextOperationForSearch(entry1);

    this.unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), true);
    this.ldapTemplate.assertSearchFilter(expectedFilterString);
  }

  @Test
  public void testGetAllUnitsHsaIdentity() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(|(objectclass=organizationalUnit)(objectclass=organizationalRole))";

    List<String> allUnitsHsaIdentity = this.unitRepository.getAllUnitsHsaIdentity();

    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }

  @Test
  public void testGetAllUnitsHsaIdentityBusinessClassificationCodesSpecified() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(objectclass=organizationalUnit)(objectclass=organizationalRole))(hsaDestinationIndicator=03))";

    List<String> allUnitsHsaIdentity = this.unitRepository.getAllUnitsHsaIdentity(true);

    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnitsHsaIdentity.size());
    assertEquals("ABC-123", allUnitsHsaIdentity.get(0));
  }

  @Test(expected = KivException.class)
  public void getAllUnitsHsaIdentityThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.unitRepository.getAllUnitsHsaIdentity();
  }

  @Test
  public void getUnitByHsaIdReturnEmptyUnitIfNoUnitIsFound() throws KivException {
    String expectedFilter = "(hsaIdentity=abc-123)";

    Unit unit = this.unitRepository.getUnitByHsaId("abc-123");

    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertNull(unit.getHsaIdentity());
  }

  @Test
  public void getUnitByHsaIdReturnFoundUnitIfFound() throws KivException {
    this.createEntry("SE6460000000-E000000000222", "");
    String expectedFilter = "(hsaIdentity=abc-123)";

    Unit unit = this.unitRepository.getUnitByHsaId("abc-123");

    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals("SE6460000000-E000000000222", unit.getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getUnitByHsaIdThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.unitRepository.getUnitByHsaId("abc-123");
  }

  @Test
  public void extractResultReturnNoDuplicateHsaIdentities() throws KivException {
    this.createEntry("SE6460000000-E000000000222", "");
    this.createEntry("SE6460000000-E000000000222", "");

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = this.unitRepository.searchAdvancedUnits(unit, 10, null, false);
    assertNotNull(units);
    assertEquals(1, units.size());
  }

  @Test
  public void extractResultReturnNoMoreThanMaxResultUnits() throws KivException {
    this.createEntry("abc-123", "1500");
    this.createEntry("def-456", "1500");
    this.createEntry("ghi-789", "1500");

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = this.unitRepository.searchAdvancedUnits(unit, 2, null, false);
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

    Unit unit = this.unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,o=Landstinget Halland"));
    assertNotNull(unit);
    assertEquals("abc-123", unit.getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getUnitByDNThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,ou=Landstinget Halland"));
  }

  @Test
  public void testGetAllUnitsBusinessClassificationCodesSpecified() throws KivException {
    DirContextOperationsMock hsaIdentity1 = new DirContextOperationsMock();
    hsaIdentity1.addAttributeValue("hsaIdentity", "ABC-123");
    this.ldapTemplate.addDirContextOperationForSearch(hsaIdentity1);

    String expectedFilter = "(&(|(objectclass=organizationalUnit)(objectclass=organizationalRole))(hsaDestinationIndicator=03))";

    List<Unit> allUnits = this.unitRepository.getAllUnits(true);

    this.ldapTemplate.assertSearchFilter(expectedFilter);
    assertEquals(1, allUnits.size());
    assertEquals("ABC-123", allUnits.get(0).getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void getAllUnitsThrowsKivExceptionOnNamingException() throws KivException {
    this.ldapTemplate.setExceptionToThrow(new CommunicationException(null));
    this.unitRepository.getAllUnits(false);
  }
}
