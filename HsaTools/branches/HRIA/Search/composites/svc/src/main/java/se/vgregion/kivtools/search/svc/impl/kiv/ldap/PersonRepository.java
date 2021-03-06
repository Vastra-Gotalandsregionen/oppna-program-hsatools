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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.PersonNameComparator;
import se.vgregion.kivtools.search.svc.domain.Unit;
import se.vgregion.kivtools.search.svc.domain.values.CodeTableName;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.util.Evaluator;
import se.vgregion.kivtools.search.util.Formatter;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class PersonRepository {
  public static final String KIV_SEARCH_BASE = "ou=Personal,o=vgr";
  private static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
  private static final String CLASS_NAME = PersonRepository.class.getName();
  private static final String LDAP_WILD_CARD = "*";
  // an "
  private static final String LDAP_EXACT_CARD = "\"";
  private String unitFkField;

  private LdapConnectionPool theConnectionPool;
  private CodeTablesService codeTablesService;

  public String getUnitFkField() {
    return unitFkField;
  }

  public void setUnitFkField(String unitFkField) {
    this.unitFkField = unitFkField;
  }

  public CodeTablesService getCodeTablesService() {
    return codeTablesService;
  }

  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
  }

  public void setLdapConnectionPool(LdapConnectionPool lp) {
    this.theConnectionPool = lp;
  }

  /**
   * Search person by dn.
   * 
   * @param dn The dn object to use to search person.
   * @return List of persons found in search.
   * @throws Exception .
   */
  public List<Person> searchPersons(DN dn) throws Exception {
    // Zero means all persons
    return searchPersons(dn, 0);
  }

  /**
   * Search person by dn with chosen max result.
   * 
   * @param dn The dn object to use to search person.
   * @param maxResult The maximum persons in the search result.
   * @return List of found persons.
   * @throws Exception .
   */
  public List<Person> searchPersons(DN dn, int maxResult) throws Exception {
    String searchFilter = "";
    return searchPersons(searchFilter, LDAPConnection.SCOPE_ONE, maxResult);
  }

  /**
   * Search for a persons.
   * 
   * @param givenName Name of person.
   * @param familyName Family name of person.
   * @param vgrId Unique id for person.
   * @param maxResult Maximum data rows in the result.
   * @return List of found persons.
   * @throws Exception .
   */
  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String vgrId, int maxResult) throws Exception {
    String searchFilter = createSearchPersonsFilter(givenName, familyName, vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_ONE, maxResult);
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
   * @param maxResult Maximum index in the search result list.
   * @return List of found persons.
   * @throws Exception .
   */
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws Exception {
    String searchFilter = createSearchPersonsFilterVgrId(vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_ONE, maxResult);
  }

  /**
   * Fetch a person by vgr id.
   * 
   * @param vgrId Unique id for person.
   * @return Found person.
   * @throws Exception .
   */
  public Person getPersonByVgrId(String vgrId) throws Exception {
    String searchFilter = "(objectclass=vgrUser)";
    return searchPerson("cn=" + vgrId + "," + KIV_SEARCH_BASE, LDAPConnection.SCOPE_BASE, searchFilter);
  }

  /**
   * Get all vgr id for all persons.
   * 
   * @return List of all vgr ids.
   * @throws Exception .
   */
  public List<String> getAllPersonsVgrId() throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    String searchFilter = "";
    String[] attributes = new String[1];
    attributes[0] = "vgr-id";
    List<String> result = new ArrayList<String>();

    LDAPConnection lc = getLDAPConnection();
    try {
      LDAPSearchResults searchResults = lc.search(KIV_SEARCH_BASE, LDAPConnection.SCOPE_ONE, searchFilter, attributes, false, constraints);
      // fill the list from the search result
      while (searchResults.hasMore()) {
        try {
          LDAPEntry nextEntry = searchResults.next();
          LDAPAttribute attribute = nextEntry.getAttribute(attributes[0]);
          if (attribute != null) {
            result.add(attribute.getStringValue());
          }
        } catch (LDAPException e) {
          if (e.getResultCode() == LDAPException.SIZE_LIMIT_EXCEEDED || e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
            break;
          } else {
            // take next Unit
            continue;
          }
        }
      }
    } finally {
      theConnectionPool.freeConnection(lc);
    }

    return result;
  }

  private Person searchPerson(String searchBase, int searchScope, String searchFilter) throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    Person result = new Person();

    // Get all attributes
    String[] attributes = null;

    LDAPConnection lc = getLDAPConnection();
    try {
      // return attributes and values
      result = extractSingleResult(lc.search(searchBase, searchScope, searchFilter, attributes, false, constraints));
    } finally {
      theConnectionPool.freeConnection(lc);
    }

    return result;
  }

  private SikSearchResultList<Person> searchPersons(String searchFilter, int searchScope, int maxResult) throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    // Get all attributes
    String[] attributes = null;

    LDAPConnection lc = getLDAPConnection();
    try {
      // return attributes and values
      result = extractResult(lc.search(KIV_SEARCH_BASE, searchScope, searchFilter, attributes, false, constraints), maxResult);
    } finally {
      theConnectionPool.freeConnection(lc);
    }

    return result;
  }

  private Person extractSingleResult(LDAPSearchResults searchResults) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    Person p = PersonFactory.reconstitute(searchResults.next());
    assignCodeTableValuesToPerson(p);
    return p;
  }

  private SikSearchResultList<Person> extractResult(LDAPSearchResults searchResults, int maxResult) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    while (searchResults.hasMore()) {
      try {
        Person p = PersonFactory.reconstitute(searchResults.next());
        assignCodeTableValuesToPerson(p);
        result.add(p);
      } catch (LDAPException e) {
        if (e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
          throw e;
        } else {
          continue;
        }
      }
    }
    Collections.sort(result, new PersonNameComparator());
    int resultCount = result.size();
    if (result.size() > maxResult && maxResult != 0) {
      result = new SikSearchResultList<Person>(result.subList(0, maxResult));
    }
    result.setTotalNumberOfFoundItems(resultCount);
    return result;
  }

  /**
   * Get Ldap connection using a pool.
   * 
   * @return
   * @throws LDAPException
   * @throws SikInternalException
   * @throws NoConnectionToServerException
   */
  private LDAPConnection getLDAPConnection() throws LDAPException, SikInternalException, NoConnectionToServerException {
    LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
    if (lc == null) {
      throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
    }
    return lc;
  }

  String createSearchPersonsFilter(String givenName, String familyName, String vgrId) throws Exception {
    List<String> filterList = new ArrayList<String>();

    String searchFilter = "(&(objectclass=vgrUser)";

    addSearchFilter(filterList, "vgr-id", vgrId);

    addMultipleAttributes(filterList, givenName, "givenName", "hsaNickName");

    // let's do some special handling of sn
    addMultipleAttributes(filterList, familyName, "sn", "hsaMiddleName");

    if (filterList.isEmpty()) {
      return null;
    }
    for (String s : filterList) {
      searchFilter += s;
    }
    searchFilter += ")";
    return searchFilter;
  }

  private String createSearchPersonsFilterVgrId(String vgrId) throws Exception {
    String searchFilter = "(&(objectclass=vgrUser)";
    searchFilter += createSearchFilterItem("vgr-id", vgrId);
    searchFilter += ")";
    return searchFilter;
  }

  /**
   * Add a search filter value to the filterList. e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * @throws Exception
   */
  private void addSearchFilter(List<String> filterList, String searchField, String searchValue) throws Exception {
    if (!Evaluator.isEmpty(searchValue)) {
      String value = createSearchFilterItem(searchField, searchValue);
      if (!Evaluator.isEmpty(value)) {
        filterList.add(value);
      }
    }
  }

  /**
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   * 
   * @throws Exception
   */
  private String createSearchFilterItem(String searchField, String searchValue) {
    if (!Evaluator.isEmpty(searchValue)) {
      searchValue = searchValue.trim();
      if (isExactMatchFilter(searchValue)) {
        // remove "
        searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, "");
        // exact match
        // filterList.add("(" + searchField + "=" + searchValue.trim() + ")");
        // exact match
        return "(" + searchField + "=" + searchValue.trim() + ")";
      } else {
        // change spaces to wildcards
        searchValue = Formatter.replaceStringInString(searchValue, " ", LDAP_WILD_CARD);
        searchValue = Formatter.replaceStringInString(searchValue, "-", LDAP_WILD_CARD);
        // filterList.add("(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")");
        return "(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")";
      }
    }
    return "";
  }

  private boolean isExactMatchFilter(String searchValue) {
    if (Evaluator.isEmpty(searchValue)) {
      return false;
    }
    // it has to be at least one character between the " e.g. "a" for an exact match
    if (searchValue.length() <= 2) {
      return false;
    }
    if (searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
      return true;
    }
    return false;
  }

  /**
   * Special handling for givenName example: attribute1=givenName, attribute2=hsaNickName
   * 
   * e.g. searchValue = "hasse" return (|(givenName="hasse")(hsaNickName="hasse"))
   * 
   * e.g. searchValue = hasse return (|(givenName=*hasse*)(hsaNickName=*hasse*))
   * 
   * e.g. searchValue = hasse hans return (&(|(givenName=*hasse*)(hsaNickName=*hasse*))(|(givenName=*hans*)(hsaNickName=*hans*)))
   * 
   * @param filterList
   * @param searchField
   * @param searchValue
   * @throws Exception
   */
  private void addMultipleAttributes(List<String> filterList, String searchValue, String attribute1, String attribute2) throws Exception {
    if (!Evaluator.isEmpty(searchValue)) {
      searchValue = searchValue.trim();
      if (isExactMatchFilter(searchValue)) {
        // handling of exact match
        // ***********************
        // remove exact match marker
        searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, "");
        String temp = "(|(" + attribute1 + "=" + searchValue + ")(" + attribute2 + "=" + searchValue + "))";
        filterList.add(temp);
        return;
      } else {
        if (searchValue.indexOf(" ") < 0) {
          // single value
          searchValue = "(|(" + attribute1 + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")" + "(" + attribute2 + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + "))";
          filterList.add(searchValue);
          return;
        }

        List<String> list = new ArrayList<String>();
        list = Formatter.chopUpStringToList(list, searchValue, " ");
        // List<String> list = Arrays.asList(searchValue.split(" "));
        int listSize = list.size();
        if (listSize == 0) {
          throw new Exception("Detected list.size==0 should never be possible! methodname=" + CLASS_NAME + "addPersonGivenName()");
        }
        if (listSize == 1) {
          throw new Exception("Detected list.size==1 should never be possible! methodname=" + CLASS_NAME + "addPersonGivenName()");
        }
        // not a single value! Search in fullname!
        String filterResult = "";

        StringBuffer buf = new StringBuffer("");
        for (String s : list) {
          buf.append("(|");

          // (attribute2=*value*)
          buf.append("(");
          buf.append(attribute2);
          buf.append("=");
          buf.append(LDAP_WILD_CARD);
          buf.append(s);
          buf.append(LDAP_WILD_CARD);
          buf.append(")");

          // (attribute1=*value*)
          buf.append("(");
          buf.append(attribute1);
          buf.append("=");
          buf.append(LDAP_WILD_CARD);
          buf.append(s);
          buf.append(LDAP_WILD_CARD);
          buf.append(")");

          buf.append(")");
        }
        String temp = buf.toString();
        filterResult = "(&" + temp + ")";
        filterList.add(filterResult);
      }
    }
  }

  /**
   * Uses code table service in order to lookup text value for "coded values".
   * 
   * @param p The person which should get looked up values assigned.
   */
  private void assignCodeTableValuesToPerson(Person p) {
    List<String> hsaSpecialityNames = new ArrayList<String>();
    for (String hsaSpecialityCode : p.getHsaSpecialityCode()) {
      String codeName = codeTablesService.getValueFromCode(CodeTableName.HSA_SPECIALITY_CODE, hsaSpecialityCode);
      hsaSpecialityNames.add(codeName);
    }
    p.setHsaSpecialityName(hsaSpecialityNames);
  }

  /**
   * Get persons for chosen units.
   * 
   * @param units The units to fetch persons from.
   * @param maxResult Maximum result of index in the list.
   * @return List of persons.
   * @throws Exception .
   */
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws Exception {
    SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
    // Generate or filter condition
    OrFilter filter = new OrFilter();
    for (Unit unit : units) {
      filter.or(new EqualsFilter(unitFkField, unit.getHsaIdentity()));
    }
    persons = searchPersons(filter.encode(), LDAPConnection.SCOPE_ONE, maxResult);
    return persons;
  }
}
