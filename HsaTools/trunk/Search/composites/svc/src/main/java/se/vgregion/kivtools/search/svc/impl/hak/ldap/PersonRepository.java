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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.PersonNameComparator;
import se.vgregion.kivtools.search.svc.comparators.PersonNameWeightedComparator;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;

/**
 * @author Anders Asplund - KnowIT
 * 
 */
public class PersonRepository {
  private static final String LDAP_WILD_CARD = "*";
  // an "
  private static final String LDAP_EXACT_CARD = "\"";
  private static final DistinguishedName PERSON_SEARCH_BASE = DistinguishedName.immutableDistinguishedName(Constants.SEARCH_BASE);

  private Log log = LogFactory.getLog(this.getClass());
  private LdapTemplate ldapTemplate;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * 
   * @param dn To search for persons.
   * @param maxResult Maximum result of the result list.
   * @return List of found persons.
   * @throws KivException If something goes wrong.
   */
  public List<Person> searchPersonsByDn(String dn, int maxResult) throws KivException {
    Filter searchFilter = new EqualsFilter("objectClass", "hkatPerson");
    return searchPersons(DistinguishedName.immutableDistinguishedName(dn), searchFilter, maxResult, new PersonNameComparator());
  }

  /**
   * 
   * @param hsaIdentity To search for persons.
   * @param maxResult Maximum result of the result list.
   * @return List of found persons.
   * @throws KivException If something goes wrong.
   */
  public SikSearchResultList<Person> getAllPersonsInUnit(String hsaIdentity, int maxResult) throws KivException {
    Filter searchFilter = new EqualsFilter("hsaIdentity", hsaIdentity);
    return searchPersons(PERSON_SEARCH_BASE, searchFilter, maxResult, new PersonNameComparator());
  }

  /**
   * 
   * @param vgrId can be a complete or parts of a vgrId. That is why we can return a list od Persons
   * @param maxResult Maximum result of the result list.
   * @return List of found persons.
   * @throws KivException If something goes wrong
   */
  public SikSearchResultList<Person> searchPersons(String vgrId, int maxResult) throws KivException {
    Filter searchFilter = createSearchPersonsFilterVgrId(vgrId);
    return searchPersons(PERSON_SEARCH_BASE, searchFilter, maxResult, new PersonNameComparator());
  }

  /**
   * 
   * @param vgrId Id of the person to get.
   * @return Person.
   * @throws KivException If something goes wrong.
   */
  public Person getPersonByVgrId(String vgrId) throws KivException {
    AndFilter searchFilter = new AndFilter();
    searchFilter.and(new EqualsFilter("objectClass", "hkatPerson"));
    searchFilter.and(new EqualsFilter("regionName", vgrId));
    return searchPerson(DistinguishedName.immutableDistinguishedName(Constants.SEARCH_BASE), searchFilter);
  }

  /**
   * 
   * @return List of all persons ids.
   * @throws KivException If something goes wrong.
   */
  public List<String> getAllPersonsVgrId() throws KivException {
    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    Map<String, String> result = new HashMap<String, String>();
    Filter filter = new EqualsFilter("objectClass", "hkatPerson");
    do {
      // RegionNameMapper return a String so we are pretty certain that List<String> is ok.
      @SuppressWarnings("unchecked")
      List<String> resultList = this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, new SingleAttributeMapper("regionName"), control);
      // Put everything in a map to remove duplicates.
      for (String regionName : resultList) {
        result.put(regionName, regionName);
      }
    } while (control.getCookie().getCookie() != null);

    return new ArrayList<String>(result.keySet());
  }

  private Person searchPerson(DistinguishedName baseDn, Filter filter) {
    PersonMapper mapper = new PersonMapper();
    this.ldapTemplate.search(baseDn, filter.encode(), mapper);

    return mapper.getFirstPerson();
  }

  private SikSearchResultList<Person> searchPersons(DistinguishedName baseDn, Filter filter, int maxResult, Comparator<Person> sortOrder) {
    SikSearchResultList<Person> result = new SikSearchResultList<Person>();

    PersonMapper mapper = new PersonMapper();
    this.ldapTemplate.search(baseDn, filter.encode(), mapper);

    if (mapper.getPersons().size() > 0) {
      result.addAll(mapper.getPersons());

      // Sort results
      Collections.sort(result, sortOrder);

      // Cut result to correct size
      int resultCount = result.size();
      if (result.size() > maxResult && maxResult != 0) {
        result = new SikSearchResultList<Person>(result.subList(0, maxResult));
      }

      result.setTotalNumberOfFoundItems(resultCount);
    }

    return result;
  }

  private Filter createSearchPersonsFilterVgrId(String vgrId) throws KivException {
    AndFilter searchFilter = new AndFilter();
    searchFilter.and(new EqualsFilter("objectClass", "hkatPerson"));
    searchFilter.and(new EqualsFilter("regionName", vgrId));
    return searchFilter;
  }

  private boolean isExactMatchFilter(String searchValue) {
    boolean exactMatch = false;

    // it has to be at least one character between the " e.g. "a" for an exact match
    if (searchValue.length() > 2 && searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
      exactMatch = true;
    }

    return exactMatch;
  }

  /**
   * Search for persons matching the provided SearchPersonCriterion.
   * 
   * @param person The SearchPersonCriterion to match persons against.
   * @param maxResult The maximum number of records to return.
   * @return A list of Person-objects or an empty list if no records where found.
   * @throws KivException If there is a problem during the search.
   */
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    AndFilter searchPersonFilter = generateFreeTextSearchPersonFilter(person);

    return searchPersons(PERSON_SEARCH_BASE, searchPersonFilter, maxResult, new PersonNameWeightedComparator(person.getGivenName(), person.getSurname()));
  }

  private AndFilter generateFreeTextSearchPersonFilter(SearchPersonCriterions person) {
    AndFilter userFilter = new AndFilter();
    userFilter.and(new EqualsFilter("objectclass", "hkatPerson"));
    if (!StringUtil.isEmpty(person.getUserId())) {
      OrFilter regionNameTitleOrTelephoneFilter = new OrFilter();
      String trimmedUserId = person.getUserId().trim();
      regionNameTitleOrTelephoneFilter.or(createSearchFilter("regionName", trimmedUserId));
      addTelephoneFilter(trimmedUserId, regionNameTitleOrTelephoneFilter);
      addTitleFilter(trimmedUserId, regionNameTitleOrTelephoneFilter);
      userFilter.and(regionNameTitleOrTelephoneFilter);
    }
    if (!StringUtil.isEmpty(person.getGivenName())) {
      OrFilter firstNameFilter = new OrFilter();
      firstNameFilter.or(createSearchFilter("givenName", person.getGivenName().trim()));
      firstNameFilter.or(createSearchFilter("rsvFirstNames", person.getGivenName().trim()));
      userFilter.and(firstNameFilter);
    }
    if (!StringUtil.isEmpty(person.getSurname())) {
      OrFilter surnameFilter = new OrFilter();
      surnameFilter.or(createSearchFilter("sn", person.getSurname().trim()));
      surnameFilter.or(createSearchFilter("middleName", person.getSurname().trim()));
      userFilter.and(surnameFilter);
    }

    return userFilter;
  }

  private void addTitleFilter(String title, OrFilter filter) {
    filter.or(createSearchFilter("title", title));
  }

  private void addTelephoneFilter(String telephone, OrFilter regionNameOrTelephoneFilter) {
    String trimmedTelephone = telephone.trim();
    String cleanedTelephone = cleanTelephoneNumber(telephone);
    String[] telephoneFields = new String[] { "facsimileTelephoneNumber", "hsaSwitchboardNumber", "telephoneNumber", "hsaTelephoneNumber", "mobile", "hsaInternalPagerNumber", "pager",
        "hsaTextPhoneNumber", };
    if (cleanedTelephone.length() >= 3 && !trimmedTelephone.matches("^\"*\\p{Alpha}{3}\\d{3}\"*")) {
      for (String field : telephoneFields) {
        regionNameOrTelephoneFilter.or(createSearchFilter(field, cleanedTelephone));
      }
    }
  }

  /**
   * Cleans up the provided telephone number to make it usable in a filter. Removes all but numeric values as well as leading zeroes.
   * 
   * @param telephone The telephone number to clean.
   * @return A string containing only numeric values and no leading zero.
   */
  private String cleanTelephoneNumber(String telephone) {
    String cleanedTelephoneNumber = telephone.replaceAll("\\D", "");
    cleanedTelephoneNumber = cleanedTelephoneNumber.replaceAll("^0", "");

    return cleanedTelephoneNumber;
  }

  private Filter createSearchFilter(String searchField, String searchValue) {
    String currentSearchValue = searchValue;
    Filter filter = null;
    if (!StringUtil.isEmpty(currentSearchValue)) {
      currentSearchValue = currentSearchValue.trim();
      if (isExactMatchFilter(currentSearchValue)) {
        // remove "
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, LDAP_EXACT_CARD, "");
        filter = new EqualsFilter(searchField, currentSearchValue.trim());
      } else {
        // change spaces to wildcards
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, " ", LDAP_WILD_CARD);
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, "-", LDAP_WILD_CARD);
        currentSearchValue = LDAP_WILD_CARD + currentSearchValue + LDAP_WILD_CARD;
        currentSearchValue = currentSearchValue.replaceAll("\\*\\*", "*");
        filter = new LikeFilter(searchField, currentSearchValue);
      }
    }
    return filter;
  }

  /**
   * Retrieves a person by the distinguished name and reconstitues the object.
   * 
   * @param dn The distinguished name of the person to retrieve.
   * @return A populated Person instance or null if no person was found with the provided distinguished name.
   * @throws KivException If something goes wrong when searching.
   */
  public Person getPersonByDn(String dn) throws KivException {
    DistinguishedName distinguishedName = new DistinguishedName(dn);

    PersonMapper personMapper = new PersonMapper();
    ldapTemplate.lookup(distinguishedName, personMapper);

    Person person = personMapper.getFirstPerson();

    return person;
  }

  /**
   * Retrieves a persons profile image from the LDAP directory by the distinguished name of the person.
   * 
   * @param dn The distinguished name of the person.
   * @return A byte-array with the raw image data.
   * @throws KivException If something goes wrong fetching the image.
   */
  public byte[] getProfileImageByDn(String dn) throws KivException {
    byte[] profileImage = null;

    try {
      profileImage = (byte[]) this.ldapTemplate.lookup(new DistinguishedName(dn), new ProfileImageMapper());
    } catch (NameNotFoundException e) {
      log.debug("Could not find entry in LDAP directory");
    }

    return profileImage;
  }
}
