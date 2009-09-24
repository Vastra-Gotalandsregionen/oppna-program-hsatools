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
package se.vgregion.kivtools.search.svc.impl.hak.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import se.vgregion.kivtools.search.exceptions.NoConnectionToServerException;
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.domain.Employment;
import se.vgregion.kivtools.search.svc.domain.Person;
import se.vgregion.kivtools.search.svc.domain.PersonNameComparator;
import se.vgregion.kivtools.search.svc.domain.values.AddressHelper;
import se.vgregion.kivtools.search.svc.domain.values.DN;
import se.vgregion.kivtools.search.svc.domain.values.PhoneNumber;
import se.vgregion.kivtools.search.svc.domain.values.WeekdayTime;
import se.vgregion.kivtools.search.svc.domain.values.ZipCode;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.ldap.LdapORMHelper;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;

import com.domainlanguage.time.TimePoint;
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
  private static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
  private static final String KIV_SEARCH_BASE = "OU=Landstinget Halland,DC=lthallandhsa,DC=se";
  private static final String CLASS_NAME = PersonRepository.class.getName();
  private static final String LDAP_WILD_CARD = "*";
  // an "
  private static final String LDAP_EXACT_CARD = "\"";
  private LdapConnectionPool theConnectionPool;
  // List for redundancy check of persons that contains in the LDAP search
  // result
  private List<String> personRedundancyCheck = new ArrayList<String>();
  // Map containing persons and employment list for that person.
  private Map<String, List<Employment>> personEmploymentsMap = new HashMap<String, List<Employment>>();

  public void setLdapConnectionPool(LdapConnectionPool lp) {
    this.theConnectionPool = lp;
  }

  public List<Person> searchPersonsByDn(String dn) throws Exception {
    // Zero means all persons
    return searchPersonsByDn(dn, 0);
  }

  public List<Person> searchPersonsByDn(String dn, int maxResult) throws Exception {
    String searchFilter = "(objectClass=hkatPerson)";
    return searchPersons(dn, searchFilter, LDAPConnection.SCOPE_SUB, maxResult);
  }

  public SikSearchResultList<Person> getAllPersonsInUnit(String hsaIdentity, int maxResult) throws Exception {
    return searchPersons("(hsaIdentity=" + hsaIdentity + ")", LDAPConnection.SCOPE_SUB, maxResult);
  }

  public SikSearchResultList<Person> searchPersons(String givenName, String familyName, String vgrId, int maxResult) throws Exception {
    String searchFilter = createSearchPersonsFilter(givenName, familyName, vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_SUB, maxResult);
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
   * @return
   * @throws Exception
   */
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws Exception {
    String searchFilter = createSearchPersonsFilterVgrId(vgrId);
    return searchPersons(searchFilter, LDAPConnection.SCOPE_SUB, maxResult);
  }

  public Person getPersonByVgrId(String vgrId) throws Exception {
    String searchFilter = "(&(objectclass=hkatPerson)(regionName=" + vgrId + "))";
    return searchPerson(KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter);
  }

  public List<String> getAllPersonsVgrId() throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    String searchFilter = "(objectClass=hkatPerson)";
    String[] attributes = new String[1];
    attributes[0] = "regionName";
    Map<String, String> result = new HashMap<String, String>();

    LDAPConnection lc = getLDAPConnection();
    try {
      LDAPSearchResults searchResults = lc.search(KIV_SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter, attributes, false, constraints);
      // fill the list from the search result
      while (searchResults.hasMore()) {
        try {
          LDAPEntry nextEntry = searchResults.next();
          LDAPAttribute attribute = nextEntry.getAttribute(attributes[0]);
          if (attribute != null) {
            result.put(attribute.getStringValue(), attribute.getStringValue());
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

    return new ArrayList<String>(result.keySet());
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
    return searchPersons(KIV_SEARCH_BASE, searchFilter, searchScope, maxResult);
  }

  private SikSearchResultList<Person> searchPersons(String baseDn, String searchFilter, int searchScope, int maxResult) throws Exception {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();
    // Get all attributes
    String[] attributes = null;

    LDAPConnection lc = getLDAPConnection();
    try {
      // return attributes and values
      result = extractResult(lc.search(baseDn, searchScope, searchFilter, attributes, false, constraints), maxResult);
    } finally {
      theConnectionPool.freeConnection(lc);
    }

    return result;
  }

  private Person extractSingleResult(LDAPSearchResults searchResults) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    return PersonFactory.reconstitute(searchResults.next());
  }

  private SikSearchResultList<Person> extractResult(LDAPSearchResults searchResults, int maxResult) throws LDAPException {
    if (searchResults == null) {
      return null;
    }
    clearCashedValues();
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();

    while (searchResults.hasMore()) {
      try {
        LDAPEntry entry = searchResults.next();
        Person person = PersonFactory.reconstitute(entry);
        // Set the current persons employment list
        extractEmploymentInfoFromEntry(entry);
        // Only add person once to the result list
        if (!isPersonAlreadyAdded(person)) {
          result.add(person);
        }
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
    // connect employment list to each person in the list
    connectEmploymentListToPerson(result);
    result.setTotalNumberOfFoundItems(resultCount);
    return result;
  }

  // Clear the list and map from old values
  private void clearCashedValues() {
    personRedundancyCheck.clear();
    personEmploymentsMap.clear();
  }

  private void connectEmploymentListToPerson(SikSearchResultList<Person> result) {
    // Get employment list from employments map for each person and set the
    // employment property for each person.
    for (Person person : result) {
      List<Employment> empLisr = personEmploymentsMap.get(person.getCn());
      person.setEmployments(empLisr);
    }
  }

  /**
   * Prevent a person to be added more than once in a list.
   * 
   * @param person
   * @return - true if person is already added to the list
   */
  private boolean isPersonAlreadyAdded(Person person) {
    boolean isAdded = personRedundancyCheck.contains(person.getCn());
    if (!isAdded) {
      personRedundancyCheck.add(person.getCn());
    }
    return isAdded;
  }

  /**
   * 
   * @param employments - Map with employments that belongs to a person, persons cn attribute is the key in the map.
   * @param person - person that extracted employment should belong to.
   * @param personEntry - LDAP entry to extract employment info from.
   */
  private void extractEmploymentInfoFromEntry(LDAPEntry personEntry) {
    // Set employment
    Employment employment = extractEmployment(personEntry);
    List<Employment> empList = personEmploymentsMap.get(employment.getCn());
    // Create new employment list and put it in the map, if there are none
    // for the current person.
    if (empList == null) {
      empList = new ArrayList<Employment>();
      personEmploymentsMap.put(employment.getCn(), empList);
    }
    empList.add(employment);
  }

  public static Employment extractEmployment(LDAPEntry personEntry) {
    Employment employment = new Employment();

    employment.setCn(LdapORMHelper.getSingleValue(personEntry.getAttribute("cn")));
    employment.setOu(LdapORMHelper.getSingleValue(personEntry.getAttribute("ou")));
    employment.setHsaPersonIdentityNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaIdentity")));
    employment.setHsaStreetAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(personEntry.getAttribute("street"))));
    employment.setHsaInternalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaInternalAddress"))));
    employment.setHsaPostalAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(personEntry.getAttribute("postalAddress"))));
    employment.setHsaSedfDeliveryAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaDeliveryAddress"))));
    employment.setHsaSedfInvoiceAddress(AddressHelper.convertToAddress(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaInvoiceAddress"))));
    employment.setFacsimileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("facsimileTelephoneNumber"))));
    employment.setLabeledUri(LdapORMHelper.getSingleValue(personEntry.getAttribute("labeledUri")));

    employment.setTitle(LdapORMHelper.getSingleValue(personEntry.getAttribute("title")));
    /*
     * Deprecated 2009-04-21 if (!"".equals(personEntry.getAttribute("title"))) employment.setTitle(LdapORMHelper.getSingleValue(personEntry.getAttribute("title")));
     */

    employment.setDescription(LdapORMHelper.getMultipleValues(personEntry.getAttribute("description")));
    employment.setHsaSedfSwitchboardTelephoneNo(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaSwitchboardNumber"))));
    employment.setName(LdapORMHelper.getSingleValue(personEntry.getAttribute("company")));
    employment.setHsaTelephoneNumbers(PhoneNumber.createPhoneNumberList(LdapORMHelper.getMultipleValues(personEntry.getAttribute("telephoneNumber"))));
    employment.setHsaPublicTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaPublicTelephoneNumber"))));
    employment.setMobileTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("mobile"))));
    employment.setHsaInternalPagerNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaInternalPagerNumber"))));
    // employment.setPagerTelephoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("pagerTelephoneNumber"))));
    employment.setHsaTextPhoneNumber(PhoneNumber.createPhoneNumber(LdapORMHelper.getSingleValue(personEntry.getAttribute("hsaTextPhoneNumber"))));
    employment.setModifyTimestamp(TimePoint.parseFrom(LdapORMHelper.getSingleValue(personEntry.getAttribute("whenChanged")), ""/* TODO Add pattern */, TimeZone.getDefault()));
    // employment.setModifyersName(LdapORMHelper.getSingleValue(personEntry.getAttribute("modifyersName")));
    employment.setHsaTelephoneTime(WeekdayTime.createWeekdayTimeList(LdapORMHelper.getMultipleValues(personEntry.getAttribute("hsaTelephoneTime"))));
    employment.setVgrStrukturPerson(DN.createDNFromString(LdapORMHelper.getSingleValue(personEntry.getAttribute("distinguishedName"))));
    employment.setZipCode(new ZipCode(LdapORMHelper.getSingleValue(personEntry.getAttribute("postalCode"))));
    return employment;
  }

  /**
   * Get Ldap connection using a pool.
   * 
   * @return
   * @throws LDAPException
   * @throws NoConnectionToServerException
   * @throws SikInternalException
   */
  private LDAPConnection getLDAPConnection() throws LDAPException, NoConnectionToServerException, SikInternalException {
    LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
    if (lc == null) {
      throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
    }
    return lc;
  }

  String createSearchPersonsFilter(String givenName, String familyName, String vgrId) throws Exception {
    List<String> filterList = new ArrayList<String>();

    String searchFilter = "(&(objectclass=hkatPerson)";

    addSearchFilter(filterList, "regionName", vgrId);

    addMultipleAttributes(filterList, givenName, "givenName", "rsvFirstNames");

    // let's do some special handling of sn
    addMultipleAttributes(filterList, familyName, "sn", "middleName");

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
    String searchFilter = "(&(objectclass=hkatPerson)";
    searchFilter += createSearchFilterItem("regionName", vgrId);
    searchFilter += ")";
    return searchFilter;
  }

  /**
   * Add a search filter value to the filterList. e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * @throws Exception
   */
  private void addSearchFilter(List<String> filterList, String searchField, String searchValue) throws Exception {
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
   * 
   */
  private String createSearchFilterItem(String searchField, String searchValue) {
    if (!StringUtil.isEmpty(searchValue)) {
      searchValue = searchValue.trim();
      if (isExactMatchFilter(searchValue)) {
        // remove "
        // exact match
        // filterList.add("(" + searchField + "=" + searchValue.trim() + ")");
        searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, "");
        // exact match
        return "(" + searchField + "=" + searchValue.trim() + ")";
      } else {
        // change spaces to wildcards
        searchValue = Formatter.replaceStringInString(searchValue, " ", LDAP_WILD_CARD);
        searchValue = Formatter.replaceStringInString(searchValue, "-", LDAP_WILD_CARD);
        // filterList.add("(" + searchField + "=" + LDAP_WILD_CARD +
        // searchValue + LDAP_WILD_CARD + ")");
        return "(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")";
      }
    }
    return "";
  }

  private boolean isExactMatchFilter(String searchValue) {
    if (StringUtil.isEmpty(searchValue)) {
      return false;
    }
    // it has to be at least one character between the " e.g. "a" for an
    // exact match
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
   * e.g. searchValue = hasse hans return (&(|(givenName=*hasse*)(hsaNickName=* hasse*))(|(givenName=*hans*)(hsaNickName=*hans*)))
   * 
   * @param filterList
   * @param searchField
   * @param searchValue
   * @throws Exception
   */
  private void addMultipleAttributes(List<String> filterList, String searchValue, String attribute1, String attribute2) throws Exception {
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
}
