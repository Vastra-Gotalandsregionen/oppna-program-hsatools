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
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.LessThanOrEqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotPresentFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.exceptions.KivException;
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
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterion;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterion.SearchCriterion;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * @author Anders Asplund - KnowIT
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
   * @throws KivException .
   */
  public List<Person> searchPersons(DN dn) throws KivException {
    // Zero means all persons
    return searchPersons(dn, 0);
  }

  /**
   * Search person by dn with chosen max result.
   * 
   * @param dn The dn object to use to search person.
   * @param maxResult The maximum persons in the search result.
   * @return List of found persons.
   * @throws KivException .
   */
  public List<Person> searchPersons(DN dn, int maxResult) throws KivException {
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
   * @throws KivException .
   */
  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String vgrId, int maxResult) throws KivException {
    String searchFilter = createSearchPersonsFilter(givenName, familyName, vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_ONE, maxResult);
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
   * @param maxResult Maximum index in the search result list.
   * @return List of found persons.
   * @throws KivException .
   */
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws KivException {
    String searchFilter = createSearchPersonsFilterVgrId(vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_ONE, maxResult);
  }

  /**
   * Fetch a person by vgr id.
   * 
   * @param vgrId Unique id for person.
   * @return Found person.
   * @throws KivException .
   */
  public Person getPersonByVgrId(String vgrId) throws KivException {
    String searchFilter = "(objectclass=vgrUser)";
    return searchPerson("cn=" + vgrId + "," + KIV_SEARCH_BASE, LDAPConnection.SCOPE_BASE, searchFilter);
  }

  /**
   * Get all vgr id for all persons.
   * 
   * @return List of all vgr ids.
   * @throws KivException .
   */
  public List<String> getAllPersonsVgrId() throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    String searchFilter = "";
    String[] attributes = new String[1];
    attributes[0] = "vgr-id";
    List<String> result = new ArrayList<String>();

    try {
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
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;
  }

  private Person searchPerson(String searchBase, int searchScope, String searchFilter) throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    Person result = new Person();

    // Get all attributes
    String[] attributes = null;

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        // return attributes and values
        result = extractSingleResult(lc.search(searchBase, searchScope, searchFilter, attributes, false, constraints));
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;
  }

  private SikSearchResultList<Person> searchPersons(String searchFilter, int searchScope, int maxResult) throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    // Get all attributes
    String[] attributes = null;

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        // return attributes and values
        result = extractResult(lc.search(KIV_SEARCH_BASE, searchScope, searchFilter, attributes, false, constraints), maxResult);
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;
  }

  private Filter getPersonDNsByEmployment(String searchFilter, int searchScope, int maxResult) throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    OrFilter filter = new OrFilter();
    // Get all attributes
    String[] attributes = null;

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        // return attributes and values
        List<String> personDNs = extractDNStrings(lc.search(KIV_SEARCH_BASE, searchScope, searchFilter, attributes, false, constraints), maxResult);
        for (String dn : personDNs) {
          DistinguishedName personDn = new DistinguishedName(dn);
          personDn.removeLast();
          filter.or(new EqualsFilter("vgr-id", personDn.removeLast().getValue()));
        }
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return filter;
  }

  private ArrayList<String> extractDNStrings(LDAPSearchResults searchResults, int maxResult) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    ArrayList<String> result = new ArrayList<String>();
    while (searchResults.hasMore()) {
      try {
        String p = searchResults.next().getDN();
        result.add(p);
      } catch (LDAPException e) {
        if (e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
          throw e;
        } else {
          continue;
        }
      }
    }
    return result;
  }

  private Person extractSingleResult(LDAPSearchResults searchResults) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    Person p = PersonFactory.reconstitute(searchResults.next(), codeTablesService);
    return p;
  }

  private SikSearchResultList<Person> extractResult(LDAPSearchResults searchResults, int maxResult) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    while (searchResults.hasMore()) {
      try {
        Person p = PersonFactory.reconstitute(searchResults.next(), codeTablesService);
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

  String createSearchPersonsFilter(String givenName, String familyName, String vgrId) throws KivException {
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

  private String createSearchPersonsFilterVgrId(String vgrId) throws KivException {
    String searchFilter = "(&(objectclass=vgrUser)";
    searchFilter += createSearchFilterItem("vgr-id", vgrId);
    searchFilter += ")";
    return searchFilter;
  }

  /**
   * Add a search filter value to the filterList. e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * @throws KivException
   */
  private void addSearchFilter(List<String> filterList, String searchField, String searchValue) throws KivException {
    if (!StringUtil.isEmpty(searchValue)) {
      String value = createSearchFilterItem(searchField, searchValue);
      if (!StringUtil.isEmpty(value)) {
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
   */
  private String createSearchFilterItem(String searchField, String searchValue) {
    if (!StringUtil.isEmpty(searchValue)) {
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
    if (StringUtil.isEmpty(searchValue)) {
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
   * @throws KivException
   */
  private void addMultipleAttributes(List<String> filterList, String searchValue, String attribute1, String attribute2) throws KivException {
    if (!StringUtil.isEmpty(searchValue)) {
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
          throw new KivException("Detected list.size==0 should never be possible! methodname=" + CLASS_NAME + "addPersonGivenName()");
        }
        if (listSize == 1) {
          throw new KivException("Detected list.size==1 should never be possible! methodname=" + CLASS_NAME + "addPersonGivenName()");
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
   * Get persons for chosen units.
   * 
   * @param units The units to fetch persons from.
   * @param maxResult Maximum result of index in the list.
   * @return List of persons.
   * @throws KivException .
   */
  public SikSearchResultList<Person> getPersonsForUnits(List<Unit> units, int maxResult) throws KivException {
    SikSearchResultList<Person> persons = new SikSearchResultList<Person>();
    // Generate or filter condition
    OrFilter filter = new OrFilter();
    for (Unit unit : units) {
      filter.or(new EqualsFilter(unitFkField, unit.getHsaIdentity()));
    }
    persons = searchPersons(filter.encode(), LDAPConnection.SCOPE_ONE, maxResult);
    return persons;
  }

  /**
   * Search for persons matching the provided SearchPersonCriterion.
   * 
   * @param person The SearchPersonCriterion to match persons against.
   * @param maxResult The maximum number of records to return.
   * @return A list of Person-objects or an empty list if no records where found.
   * @throws KivException If there is a problem during the search.
   */
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterion person, int maxResult) throws KivException {
    Filter employmentFilter = null;
    if (person.getAllSearchCriterions().containsKey(SearchCriterion.EMPLOYMENT_TITEL)) {
      employmentFilter = getPersonDNsByEmployment(generateFreeTextSearchEmploymentFilter(person).encode(), LDAPConnection.SCOPE_SUB, Integer.MAX_VALUE);
    }

    AndFilter searchPersonFilter = generateFreeTextSearchPersonFilter(person);
    if (employmentFilter != null) {
      searchPersonFilter.and(employmentFilter);
    }

    return searchPersons(searchPersonFilter.encode(), LDAPConnection.SCOPE_ONE, maxResult);
  }

  private Filter generateFreeTextSearchEmploymentFilter(SearchPersonCriterion person) {
    AndFilter employmentFilter = new AndFilter();
    employmentFilter.and(new EqualsFilter("objectclass", "vgrAnstallning"));
    // Start date today or earlier
    String today = TimeUtil.getCurrentTimeFormatted(DateTimeFormat.ZULU_TIME);
    employmentFilter.and(new LessThanOrEqualsFilter("hsaStartDate", today));

    OrFilter employmentEndDateFilter = new OrFilter();
    // Either no end date
    employmentEndDateFilter.or(new NotPresentFilter("hsaEndDate"));
    // Or end date either today or late
    employmentEndDateFilter.or(new GreaterThanOrEqualsFilter("hsaEndDate", today));

    employmentFilter.and(employmentEndDateFilter);

    // Add title to employmentFilter instead of andFilter since it's an employment attribute
    employmentFilter.and(new LikeFilter(SearchCriterion.EMPLOYMENT_TITEL.toString(), "*" + person.getAllSearchCriterions().get(SearchCriterion.EMPLOYMENT_TITEL) + "*"));

    return employmentFilter;
  }

  private AndFilter generateFreeTextSearchPersonFilter(SearchPersonCriterion person) {
    AndFilter userFilter = new AndFilter();
    userFilter.and(new EqualsFilter("objectclass", "vgrUser"));
    for (Entry<SearchCriterion, String> criterion : person.getAllSearchCriterions().entrySet()) {
      switch (criterion.getKey()) {
        case GIVEN_NAME:
          OrFilter firstNameFilter = new OrFilter();
          firstNameFilter.or(new LikeFilter(criterion.getKey().toString(), "*" + criterion.getValue() + "*"));
          firstNameFilter.or(new LikeFilter("hsaNickName", "*" + criterion.getValue() + "*"));
          userFilter.and(firstNameFilter);
          break;
        case SURNAME:
          OrFilter surnameFilter = new OrFilter();
          surnameFilter.or(new LikeFilter(criterion.getKey().toString(), "*" + criterion.getValue() + "*"));
          surnameFilter.or(new LikeFilter("hsaMiddleName", "*" + criterion.getValue() + "*"));
          userFilter.and(surnameFilter);
          break;
        case SPECIALITY_AREA_CODE:
          userFilter.and(generateOrFilterFromList(CodeTableName.HSA_SPECIALITY_CODE, criterion.getKey(), criterion.getValue()));
          break;
        case PROFESSION:
          userFilter.and(generateOrFilterFromList(CodeTableName.HSA_TITLE, criterion.getKey(), criterion.getValue()));
          break;
        case LANGUAGE_KNOWLEDGE_CODE:
          userFilter.and(generateOrFilterFromList(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, criterion.getKey(), criterion.getValue()));
          break;
        case ADMINISTRATION:
          userFilter.and(generateOrFilterFromList(CodeTableName.VGR_AO3_CODE, criterion.getKey(), criterion.getValue()));
          break;
        case EMPLOYMENT_TITEL:
          // Do nothing, since employment title is located on vgrAnstallning.
          break;
        default:
          userFilter.and(new LikeFilter(criterion.getKey().toString(), "*" + criterion.getValue() + "*"));
      }
    }
    return userFilter;
  }

  private Filter generateOrFilterFromList(CodeTableName codeTableName, SearchCriterion criterion, String criterionValue) {
    List<String> codeFromTextValues = codeTablesService.getCodeFromTextValue(codeTableName, criterionValue);
    OrFilter orFilter = new OrFilter();
    for (String value : codeFromTextValues) {
      orFilter.or(new LikeFilter(criterion.toString(), value));
    }
    return orFilter;
  }
}
