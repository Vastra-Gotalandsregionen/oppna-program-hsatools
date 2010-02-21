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
import java.util.Comparator;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextProcessor;
import org.springframework.ldap.core.LdapTemplate;

import se.vgregion.kivtools.mocks.ldap.DirContextOperationsMock;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

import com.novell.ldap.LDAPException;

public class UnitRepositoryTest {
  private UnitRepository unitRepository;
  private LDAPConnectionMock ldapConnectionMock;
  private LdapConnectionPoolMock ldapConnectionPoolMock;

  private LdapTemplateMock ldapTemplate;

  @Before
  public void setUp() throws Exception {
    unitRepository = new UnitRepository();
    ldapConnectionMock = new LDAPConnectionMock();
    ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);

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
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());

    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchUnitNameWithDash() throws KivException {
    // Create test unit.
    SearchUnitCriterions searchUnitCriterions = new SearchUnitCriterions();
    searchUnitCriterions.setUnitName("Kvalitet- och säkerhetsavdelningen");

    // Create ldapConnectionMock.

    String expectedFilter = "(|(&(objectclass=organizationalUnit)(ou=*Kvalitet*och*säkerhetsavdelningen*))(&(objectclass=organizationalRole)(cn=*Kvalitet*och*säkerhetsavdelningen*)))";
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, new LDAPSearchResultsMock());
    LdapConnectionPoolMock ldapConnectionPoolMock = new LdapConnectionPoolMock(ldapConnectionMock);
    unitRepository.setLdapConnectionPool(ldapConnectionPoolMock);
    SikSearchResultList<Unit> searchUnits = unitRepository.searchUnits(searchUnitCriterions, 0);
    ldapConnectionMock.assertFilter(expectedFilter);
    assertEquals(0, searchUnits.size());
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchAdvancedUnit() throws KivException {
    Unit unit = new Unit();
    unit.setName("vårdcentral");

    String expectedFilterString = "(|(&(objectclass=organizationalUnit)(&(|(description=*vårdcentral*)(ou=*vårdcentral*))))(&(objectclass=organizationalRole)(&(|(description=*vårdcentral*)(cn=*vårdcentral*)))))";

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapEntryMock1.addAttribute("hsaIdentity", "1");
    ldapEntryMock1.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "abbesta");
    ldapEntryMock1.addAttribute(Constants.LDAP_PROPERTY_DESCRIPTION, "vårdcentral");
    ldapEntryMock1.addAttribute("businessClassificationCode", "1");

    LDAPEntryMock ldapEntryMock2 = new LDAPEntryMock();
    ldapEntryMock1.addAttribute("hsaIdentity", "2");
    ldapEntryMock2.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "vårdcentral prästkragen");
    ldapEntryMock2.addAttribute(Constants.LDAP_PROPERTY_DESCRIPTION, "bla bla");
    ldapEntryMock2.addAttribute("businessClassificationCode", "1");

    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock2);
    ldapConnectionMock.addLDAPSearchResults(expectedFilterString, ldapSearchResultsMock);

    SikSearchResultList<Unit> searchUnits = unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), Arrays.asList(1));
    assertEquals("2", searchUnits.get(0).getHsaIdentity());
    ldapConnectionMock.assertFilter(expectedFilterString);
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test
  public void testSearchAdvancedUnitExactMatch() throws KivException {
    Unit unit = new Unit();
    unit.setDescription(Arrays.asList("\"description\""));
    unit.setHsaMunicipalityName("\"Kungsbacka\"");

    String expectedFilterString = "(|(&(objectclass=organizationalUnit)(&(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka)))))(&(objectclass=organizationalRole)(&(|(municipalityName=Kungsbacka)(|(postalAddress=Kungsbacka$*$*$*$*$*)(postalAddress=*$Kungsbacka$*$*$*$*)(postalAddress=*$*$Kungsbacka$*$*$*)(postalAddress=*$*$*$Kungsbacka$*$*)(postalAddress=*$*$*$*$Kungsbacka$*)(postalAddress=*$*$*$*$*$Kungsbacka))(|(streetAddress=Kungsbacka$*$*$*$*$*)(streetAddress=*$Kungsbacka$*$*$*$*)(streetAddress=*$*$Kungsbacka$*$*$*)(streetAddress=*$*$*$Kungsbacka$*$*)(streetAddress=*$*$*$*$Kungsbacka$*)(streetAddress=*$*$*$*$*$Kungsbacka))))))";

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapEntryMock1.addAttribute("hsaIdentity", "1");
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults(expectedFilterString, ldapSearchResultsMock);

    unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), Arrays.asList(1));
    ldapConnectionMock.assertFilter(expectedFilterString);
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
  }

  @Test(expected = KivException.class)
  public void testSearchAdvancedUnitExceptionHandling() throws KivException {
    Unit unit = new Unit();
    unit.setName("vårdcentral");

    String expectedFilterString = "(|(&(objectclass=organizationalUnit)(&(|(description=*vårdcentral*)(ou=*vårdcentral*))))(&(objectclass=organizationalRole)(&(|(description=*vårdcentral*)(cn=*vårdcentral*)))))";

    ldapConnectionMock.setLdapException(new LDAPException("error", LDAPException.AMBIGUOUS_RESPONSE, "server message"));
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults(expectedFilterString, ldapSearchResultsMock);

    unitRepository.searchAdvancedUnits(unit, 0, new UnitNameComparator(), Arrays.asList(1));
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
    ldapConnectionPoolMock.assertCorrectConnectionHandling();
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

  @Test
  public void testRemoveUnallowedUnits() throws KivException {
    final SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();

    Unit resultUnit1 = new Unit();
    resultUnit1.setHsaIdentity("abc-123");
    resultUnit1.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit1.setVgrAnsvarsnummer(Arrays.asList("11223"));

    Unit resultUnit2 = new Unit();
    resultUnit2.setHsaIdentity("abc-456");
    resultUnit2.setHsaBusinessClassificationCode(Arrays.asList("1504"));

    Unit resultUnit3 = new Unit();
    resultUnit3.setHsaIdentity("SE6460000000-E000000000222");
    resultUnit3.setHsaBusinessClassificationCode(Arrays.asList("abc"));

    Unit resultUnit4 = new Unit();
    resultUnit4.setHsaIdentity("abc-789");
    resultUnit4.setHsaBusinessClassificationCode(Arrays.asList("1"));
    resultUnit4.setVgrAnsvarsnummer(Arrays.asList("12345"));

    result.add(resultUnit1);
    result.add(resultUnit2);
    result.add(resultUnit3);
    result.add(resultUnit4);

    unitRepository = new UnitRepository() {
      @Override
      protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
        return result;
      }
    };

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
  public void testGetUnitByHsaId() throws KivException {
    String expectedFilter = "(hsaIdentity=abc-123)";

    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapEntryMock1.addAttribute("hsaIdentity", "1");

    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults(expectedFilter, ldapSearchResultsMock);

    unitRepository.getUnitByHsaId("abc-123");

    ldapConnectionMock.assertFilter(expectedFilter);
  }

  @Test(expected = KivException.class)
  public void testGetUnitByHsaIdExceptionHandling() throws KivException {
    ldapConnectionMock.setLdapException(new LDAPException("error", LDAPException.AMBIGUOUS_RESPONSE, "server message"));
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults("(hsaIdentity=abc-123)", ldapSearchResultsMock);

    unitRepository.getUnitByHsaId("abc-123");
  }

  @Test(expected = KivException.class)
  public void testExtractSingleResultExceptionHandling() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.setLdapException(new LDAPException("error", LDAPException.AMBIGUOUS_RESPONSE, "server message"));
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults("(hsaIdentity=abc-123)", ldapSearchResultsMock);

    unitRepository.getUnitByHsaId("abc-123");
  }

  @Test
  public void testExtractResultExceptionHandling() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.setLdapException(new LDAPException("error", LDAPException.AMBIGUOUS_RESPONSE, "server message"));
    LDAPEntryMock ldapEntryMock1 = new LDAPEntryMock();
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    ldapConnectionMock.addLDAPSearchResults("(|(&(objectclass=organizationalUnit)(&(hsaIdentity=*abc*123*)))(&(objectclass=organizationalRole)(&(hsaIdentity=*abc*123*))))", ldapSearchResultsMock);

    Unit unit = new Unit();
    unit.setHsaIdentity("abc-123");
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(unit, 1, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(0, units.size());

    ldapSearchResultsMock.setLdapException(new LDAPException("error", LDAPException.LDAP_TIMEOUT, "server message"));
    ldapConnectionMock.addLDAPSearchResults("(|(&(objectclass=organizationalUnit)(&(hsaIdentity=*abc*123*)))(&(objectclass=organizationalRole)(&(hsaIdentity=*abc*123*))))", ldapSearchResultsMock);
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    units = unitRepository.searchAdvancedUnits(unit, 1, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(0, units.size());

    ldapSearchResultsMock.setLdapException(new LDAPException("error", LDAPException.CONNECT_ERROR, "server message"));
    ldapConnectionMock.addLDAPSearchResults("(|(&(objectclass=organizationalUnit)(&(hsaIdentity=*abc*123*)))(&(objectclass=organizationalRole)(&(hsaIdentity=*abc*123*))))", ldapSearchResultsMock);
    ldapSearchResultsMock.addLDAPEntry(ldapEntryMock1);
    units = unitRepository.searchAdvancedUnits(unit, 1, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(0, units.size());
  }

  @Test
  public void extractResultReturnNoDuplicateHsaIdentities() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(createEntry("SE6460000000-E000000000222", ""));
    ldapSearchResultsMock.addLDAPEntry(createEntry("SE6460000000-E000000000222", ""));

    ldapConnectionMock
        .addLDAPSearchResults(
            "(|(&(objectclass=organizationalUnit)(&(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*)))))(&(objectclass=organizationalRole)(&(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*))))))",
            ldapSearchResultsMock);

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(unit, 10, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(1, units.size());
  }

  @Test
  public void extractResultReturnNoMoreThanMaxResultUnits() throws KivException {
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    ldapSearchResultsMock.addLDAPEntry(createEntry("abc-123", "1500"));
    ldapSearchResultsMock.addLDAPEntry(createEntry("def-456", "1500"));
    ldapSearchResultsMock.addLDAPEntry(createEntry("ghi-789", "1500"));

    ldapConnectionMock
        .addLDAPSearchResults(
            "(|(&(objectclass=organizationalUnit)(&(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*)))))(&(objectclass=organizationalRole)(&(|(municipalityName=*Kungsbacka*)(|(postalAddress=*Kungsbacka*$*$*$*$*$*)(postalAddress=*$*Kungsbacka*$*$*$*$*)(postalAddress=*$*$*Kungsbacka*$*$*$*)(postalAddress=*$*$*$*Kungsbacka*$*$*)(postalAddress=*$*$*$*$*Kungsbacka*$*)(postalAddress=*$*$*$*$*$*Kungsbacka*))(|(streetAddress=*Kungsbacka*$*$*$*$*$*)(streetAddress=*$*Kungsbacka*$*$*$*$*)(streetAddress=*$*$*Kungsbacka*$*$*$*)(streetAddress=*$*$*$*Kungsbacka*$*$*)(streetAddress=*$*$*$*$*Kungsbacka*$*)(streetAddress=*$*$*$*$*$*Kungsbacka*))))))",
            ldapSearchResultsMock);

    Unit unit = new Unit();
    unit.setHsaMunicipalityName("Kungsbacka");
    SikSearchResultList<Unit> units = unitRepository.searchAdvancedUnits(unit, 2, null, new ArrayList<Integer>());
    assertNotNull(units);
    assertEquals(2, units.size());
    assertEquals(3, units.getTotalNumberOfFoundItems());
  }

  private LDAPEntryMock createEntry(String hsaIdentity, String businessClassificationCode) {
    LDAPEntryMock ldapEntry = new LDAPEntryMock("hsaIdentity", hsaIdentity);
    ldapEntry.addAttribute("businessClassificationCode", businessClassificationCode);
    return ldapEntry;
  }

  @Test
  public void testGetUnitByDN() throws KivException {
    LDAPEntryMock ldapEntry = new LDAPEntryMock("hsaIdentity", "abc-123");
    ldapConnectionMock.addLDAPEntry("ou=Vårdcentralen Halmstad,o=Landstinget Halland", ldapEntry);

    Unit unit = unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,o=Landstinget Halland"));
    assertNotNull(unit);
    assertEquals("abc-123", unit.getHsaIdentity());
  }

  @Test(expected = KivException.class)
  public void testGetUnitByDNExceptionHandling() throws KivException {
    ldapConnectionMock.setLdapException(new LDAPException("error", LDAPException.AMBIGUOUS_RESPONSE, "server message"));
    LDAPEntryMock ldapEntry = new LDAPEntryMock("hsaIdentity", "abc-123");
    ldapConnectionMock.addLDAPEntry("ou=Vårdcentralen Halmstad,o=Landstinget Halland", ldapEntry);

    unitRepository.getUnitByDN(DN.createDNFromString("ou=Vårdcentralen Halmstad,o=Landstinget Halland"));
  }

  @Test(expected = KivException.class)
  public void testGetConnectionFromPoolNoConnection() throws KivException {
    this.unitRepository.setLdapConnectionPool(new LdapConnectionPoolMock(null));
    unitRepository.getUnitByHsaId("abc-123");
  }

  private static class LdapTemplateMock extends LdapTemplate {
    private String filter;
    private List<DirContextOperations> dirContextOperations = new ArrayList<DirContextOperations>();

    public void addDirContextOperationForSearch(DirContextOperations dirContextOperations) {
      this.dirContextOperations.add(dirContextOperations);
    }

    public void assertSearchFilter(String expectedFilter) {
      assertEquals(expectedFilter, this.filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List search(String base, String filter, SearchControls searchControls, ContextMapper mapper, DirContextProcessor dirContextProcessor) {
      this.filter = filter;
      List result = new ArrayList();
      for (DirContextOperations dirContextOperations : this.dirContextOperations) {
        result.add(mapper.mapFromContext(dirContextOperations));
      }
      // Use ReflectionUtil since there is no set-method for cookie.
      ReflectionUtil.setField(dirContextProcessor, "cookie", new PagedResultsCookie(null));
      return result;
    }
  }
}
