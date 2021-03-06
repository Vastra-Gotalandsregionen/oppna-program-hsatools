/**
 * Copyright 2009 Västa Götalandsregionen
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
/**
 * 
 */
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesTest;
import se.vgregion.kivtools.search.svc.codetables.impl.vgr.CodeTablesServiceImpl;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.HealthcareType;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.SearchCondition;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.util.DisplayValueTranslator;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Test of the class UnitRepository
 * 
 */
public class TestUnitRepository {
  UnitRepository ur = null;

  @BeforeClass
  public static void runBeforeClass() {
    // run for one time before all test cases
  }

  @AfterClass
  public static void runAfterClass() {
    // run for one time after all test cases
  }

  @Before
  public void runBeforeEveryTest() {
    // run for each time before every test cases
    ur = new UnitRepository();
  }

  @After
  public void runAfterEveryTest() {
    // run for each time after every test cases
    ur = null;
  }

  @Test
  public void testBuildAddressSearch() {
    String correctResult = "(|(hsaPostalAddress=*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*)" + "(hsaPostalAddress=*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*)"
        + "(hsaPostalAddress=*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))";
    String temp = ur.buildAddressSearch("hsaPostalAddress", "*uddevalla*");
    Assert.assertEquals(correctResult, temp);
  }

  /**
   * 
   * @throws Exception
   */
  @Test
  public void testcreateSearchFilter() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdoms*)");
    correctResult.append("(|(hsaMunicipalityName=*Borås*)");
    correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
    correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*)))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdoms*)");
    correctResult.append("(|(hsaMunicipalityName=*Borås*)");
    correctResult.append("(|(hsaPostalAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*Borås*$*$*$*$*)(hsaPostalAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*Borås*$*$*)(hsaPostalAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaPostalAddress=*$*$*$*$*$*Borås*))");
    correctResult.append("(|(hsaStreetAddress=*Borås*$*$*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*Borås*$*$*$*$*)(hsaStreetAddress=*$*$*Borås*$*$*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*Borås*$*$*)(hsaStreetAddress=*$*$*$*$*Borås*$*)");
    correctResult.append("(hsaStreetAddress=*$*$*$*$*$*Borås*))))))");

    Unit unit = new Unit();
    unit.setName("barn- och ungdoms");
    unit.setHsaMunicipalityName("Borås");
    String temp = ur.createSearchFilter(unit);
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Combined test
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter1() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(cn=*barn**och*ungdomsvård*)(|(hsaMunicipalityCode=*1490*)))))");
    Unit unit = new Unit();
    unit.setName("barn- och ungdomsvård");
    unit.setHsaMunicipalityCode("1490");
    String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Only hsamuncipalitycode
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter2() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540)))))");
    correctResult.append("(&(objectclass=vgrOrganizationalRole)(&(|(hsaMunicipalityCode=*1490*))(&(|(hsaBusinessClassificationCode=1540))))))");
    Unit unit = new Unit();
    unit.setHsaMunicipalityCode("1490");
    List<HealthcareType> healthcareTypeList = new ArrayList<HealthcareType>();
    HealthcareType ht = new HealthcareType();
    Map<String, String> conditions = new HashMap<String, String>();
    conditions.put("hsaBusinessClassificationCode", "1540");
    ht.setConditions(conditions);
    healthcareTypeList.add(ht);
    unit.setHealthcareTypes(healthcareTypeList);
    String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Only hsamuncipalitycode
   * 
   * @throws Exception
   */
  @Test
  public void testcreateAdvancedSearchFilter3() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(|(&(objectclass=vgrOrganizationalUnit)(&(ou=*ambulans*)))(&(objectclass=vgrOrganizationalRole)(&(cn=*ambulans*))))");
    Unit unit = new Unit();
    unit.setName("ambulans");
    String temp = ur.createAdvancedSearchFilter(unit, new ArrayList<Integer>());
    Assert.assertEquals(correctResult.toString(), temp);
  }

  @Test
  public void testAssignCodeTableValuesToUnit() {
    try {
      Unit u = new Unit();
      u.setHsaManagementCode("1");
      Method assignCodeTableValuesToUnitMethod = ur.getClass().getDeclaredMethod("assignCodeTableValuesToUnit", Unit.class);
      assignCodeTableValuesToUnitMethod.setAccessible(true);
      // Assign code table mock
      CodeTablesServiceImpl codeTablesService = new CodeTablesServiceImpl();
      codeTablesService.setLdapConnectionPool(new CodeTablesTest.LdapConnectionPoolMock());
      codeTablesService.init();
      ur.setCodeTablesService(codeTablesService);
      assignCodeTableValuesToUnitMethod.invoke(ur, u);
      Assert.assertEquals("Landsting/Region", u.getHsaManagementText());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Test fetching sub units for a chosen unit
  @Test
  public void testGetSubUnits() throws Exception {
    ur = new UnitRepository();
    UnitFactory unitFactory = new UnitFactory();
    ur.setUnitFactory(unitFactory);
    unitFactory.setCodeTablesService(new CodeTablesServiceImpl());
    DisplayValueTranslator displayValueTranslator = new DisplayValueTranslator();
    displayValueTranslator.setTranslationMap(new HashMap<String, String>());
    unitFactory.setDisplayValueTranslator(displayValueTranslator);
    String base = "ou=Folktandvården Fyrbodal,ou=Folktandvården Västra Götaland,ou=Org,o=vgr";
    String filter = "(objectClass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";
    Unit parentUnit = new Unit();
    parentUnit.setHsaIdentity("parent");
    parentUnit.setDn(DN.createDNFromString(base));
    // Set Ldap connetion mock for the UnitRepository
    ur.setLdapConnectionPool(new LdapConnectionPoolMock(generateLdapConnectionMock(base, filter)));

    SikSearchResultList<Unit> subUnits = new SikSearchResultList<Unit>();
    Unit subUnit1 = new Unit();
    subUnit1.setName("SubUnit1");
    subUnit1.setHsaIdentity("SubUnit1");
    Unit subUnit2 = new Unit();
    subUnit2.setName("SubUnit2");
    subUnit2.setHsaIdentity("SubUnit2");

    subUnits.add(subUnit1);
    subUnits.add(subUnit2);

    SikSearchResultList<Unit> subUnitsResult = null;
    ur.setCodeTablesService(new CodeTablesServiceImpl());
    subUnitsResult = ur.getSubUnits(parentUnit, 2);

    Unit returndUnit1 = subUnitsResult.get(0);
    Unit returndUnit2 = subUnitsResult.get(1);
    Assert.assertEquals(subUnit1.getName(), returndUnit1.getName());
    Assert.assertEquals(subUnit2.getName(), returndUnit2.getName());
  }

  private LDAPConnectionMock generateLdapConnectionMock(String base, String filter) {
    LDAPConnectionMock ldapConnectionMock = new LDAPConnectionMock();
    LinkedList<LDAPEntryMock> subUnitLdapEntries = new LinkedList<LDAPEntryMock>();

    // Sub unit entries
    LDAPEntryMock subUnitEntry1 = new LDAPEntryMock();
    LDAPEntryMock subUnitEntry2 = new LDAPEntryMock();
    // Sub entity 1
    subUnitEntry1.addAttribute("hsaIdentity", "1");
    subUnitEntry1.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "SubUnit1");
    subUnitEntry1.addAttribute("objectClass", Constants.OBJECT_CLASS_UNIT_SPECIFIC);
    // Sub entity 2
    subUnitEntry2.addAttribute("hsaIdentity", "2");
    subUnitEntry2.addAttribute(Constants.LDAP_PROPERTY_UNIT_NAME, "SubUnit2");
    subUnitEntry2.addAttribute("objectClass", Constants.OBJECT_CLASS_UNIT_SPECIFIC);

    subUnitLdapEntries.add(subUnitEntry1);
    subUnitLdapEntries.add(subUnitEntry2);

    ldapConnectionMock.addLdapEntries(new SearchCondition(base, LDAPConnection.SCOPE_SUB, filter), subUnitLdapEntries);

    return ldapConnectionMock;

  }

  public static class LdapConnectionPoolMock extends LdapConnectionPool {

    private LDAPConnectionMock connectionMock;

    public LdapConnectionPoolMock(LDAPConnectionMock connectionMock) {
      this.connectionMock = connectionMock;
    }

    @Override
    public synchronized LDAPConnection getConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
      return connectionMock;
    }
  }
}
