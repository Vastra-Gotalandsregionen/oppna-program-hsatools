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
package se.vgregion.kivtools.search.svc.impl.kiv.ldap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPConnectionMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPEntryMock;
import se.vgregion.kivtools.search.svc.impl.mock.LDAPSearchResultsMock;
import se.vgregion.kivtools.search.svc.impl.mock.LdapConnectionPoolMock;
import se.vgregion.kivtools.search.svc.impl.mock.SearchCondition;

import com.novell.ldap.LDAPConnection;

/**
 * @author hangy2 , Hans Gyllensten / KnowIT
 * 
 *         Test of the class PersonRepository
 * 
 */
public class TestPersonRepository {
  PersonRepository repo = null;

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
    repo = new PersonRepository();
    repo.setCodeTablesService(new CodeTablesServiceMock());
  }

  @After
  public void runAfterEveryTest() {
    // run for each time after every test cases
    repo = null;
  }

  /**
   * Combined test
   * 
   * @throws Exception
   */
  @Test
  public void testcreateSearchPersonsFilter1() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(objectclass=vgrUser)(vgr-id=*hanac*)(|(givenName=*hans*)(hsaNickName=*hans*))(|(sn=*ackerot*)(hsaMiddleName=*ackerot*)))");
    String temp = repo.createSearchPersonsFilter("hans", "ackerot", "hanac");
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Combined test
   * 
   * @throws Exception
   */
  @Test
  public void testcreateSearchPersonsFilter2() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(objectclass=vgrUser)(|(givenName=*hans*)(hsaNickName=*hans*)))");
    String temp = repo.createSearchPersonsFilter("hans", "", "");
    Assert.assertEquals(correctResult.toString(), temp);
  }

  /**
   * Combined test
   * 
   * @throws Exception
   */
  @Test
  public void testcreateSearchPersonsFilter3() throws Exception {
    StringBuffer correctResult = new StringBuffer();
    correctResult.append("(&(objectclass=vgrUser)(vgr-id=*ana*))");
    String temp = repo.createSearchPersonsFilter("", "", "ana");
    Assert.assertEquals(correctResult.toString(), temp);
  }

  @Test
  public void testGetPersonsForUnits() throws Exception {
    setLdapConnectionMock();
    List<Unit> units = generateTestUnitList();
    List<Person> persons = null;
    persons = repo.getPersonsForUnits(units, 5);
    Assert.assertFalse(persons.isEmpty());
  }

  private List<Unit> generateTestUnitList() {
    List<Unit> units = new ArrayList<Unit>();
    Unit unit = null;
    for (int i = 0; i < 5; i++) {
      unit = new Unit();
      unit.setHsaIdentity("unit" + i);
      units.add(unit);
    }
    return units;
  }

  private List<Person> generateTestPersonList() {
    List<Person> persons = new ArrayList<Person>();
    Person person = null;
    for (int i = 0; i < 10; i++) {
      person = new Person();
      person.setHsaIdentity("person" + i);
      persons.add(person);
    }
    return persons;
  }

  private void setLdapConnectionMock() {
    List<Unit> units = generateTestUnitList();
    List<Person> persons = generateTestPersonList();
    String searchCondition = generateUnitSearchCondition(units);
    LinkedList<LDAPEntryMock> ldapEntries = generatePersonLdapEntries(persons);
    LDAPConnectionMock connectionMock = new LDAPConnectionMock();
    LDAPSearchResultsMock ldapSearchResultsMock = new LDAPSearchResultsMock();
    for (LDAPEntryMock ldapEntryMock : ldapEntries) {
      ldapSearchResultsMock.addLDAPEntry(ldapEntryMock);
    }
    connectionMock.addLDAPSearchResults("(|(vgrOrgRel=unit0)(vgrOrgRel=unit1)(vgrOrgRel=unit2)(vgrOrgRel=unit3)(vgrOrgRel=unit4))", ldapSearchResultsMock);
    connectionMock.addLdapEntries(new SearchCondition(PersonRepository.KIV_SEARCH_BASE, LDAPConnection.SCOPE_ONE, searchCondition), ldapEntries);
    repo.setLdapConnectionPool(new LdapConnectionPoolMock(connectionMock));
    repo.setUnitFkField("vgrOrgRel");
  }

  private String generateUnitSearchCondition(List<Unit> units) {
    StringBuilder sb = new StringBuilder("(|");
    for (Unit unit : units) {
      sb.append("(vgrOrgRel=").append(unit.getHsaIdentity()).append(")");
    }
    sb.append(")");
    return sb.toString();
  }

  private LinkedList<LDAPEntryMock> generatePersonLdapEntries(List<Person> persons) {
    LinkedList<LDAPEntryMock> ldapEntries = new LinkedList<LDAPEntryMock>();
    LDAPEntryMock entryMock = null;
    for (Person person : persons) {
      entryMock = new LDAPEntryMock();
      entryMock.addAttribute("hsaIdentity", person.getHsaIdentity());
      ldapEntries.add(entryMock);
    }
    return ldapEntries;
  }

  class CodeTablesServiceMock implements CodeTablesService {
    @Override
    public String getValueFromCode(CodeTableName codeTableName, String string) {
      return "Translated " + string;
    }

    @Override
    public void init() {
    }

    @Override
    public List<String> getCodeFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }

    @Override
    public List<String> getValuesFromTextValue(CodeTableName codeTableName, String textValue) {
      return null;
    }
  }
}
