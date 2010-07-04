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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.LessThanOrEqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.NotFilter;
import org.springframework.ldap.filter.NotPresentFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Person;
import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.CodeTableName;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.exceptions.KivNoDataFoundException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.codetables.CodeTablesService;
import se.vgregion.kivtools.search.svc.comparators.PersonNameComparator;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchPersonCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.time.TimeUtil;
import se.vgregion.kivtools.util.time.TimeUtil.DateTimeFormat;

/**
 * @author Anders Asplund - KnowIT
 */
public class PersonRepository {
  private static final DistinguishedName PERSON_SEARCH_BASE = new DistinguishedName("ou=Personal,o=vgr");
  private static final String LDAP_WILD_CARD = "*";
  // an "
  private static final String LDAP_EXACT_CARD = "\"";
  private LdapTemplate ldapTemplate;
  private String unitFkField;
  private CodeTablesService codeTablesService;

  private static final String CN_EQUALS = "cn=";

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  public void setUnitFkField(String unitFkField) {
    this.unitFkField = unitFkField;
  }

  public void setCodeTablesService(CodeTablesService codeTablesService) {
    this.codeTablesService = codeTablesService;
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
    return searchPersons(searchFilter, SearchControls.SUBTREE_SCOPE, maxResult);
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
    return searchPerson("cn=" + vgrId + "," + PERSON_SEARCH_BASE.toString(), SearchControls.OBJECT_SCOPE, searchFilter);
  }

  /**
   * Get all vgr id for all persons.
   * 
   * @return List of all vgr ids.
   * @throws KivException .
   */
  public List<String> getAllPersonsVgrId() throws KivException {
    String[] attributes = new String[1];
    attributes[0] = "vgr-id";
    List<String> result = new ArrayList<String>();

    AndFilter andFilter = new AndFilter();
    andFilter.and(new LikeFilter("vgr-id", "*"));

    try {
      // Since SingleAttributeMapper return a String we are certain that a cast to List<String> is ok
      @SuppressWarnings("unchecked")
      List<String> vgrIds = ldapTemplate.search(PERSON_SEARCH_BASE, andFilter.encode(), SearchControls.ONELEVEL_SCOPE, attributes, new SingleAttributeMapper("vgr-id"));

      if (vgrIds != null) {
        result.addAll(vgrIds);
      }
    } catch (NamingException e1) {
      throw new KivException("Ldap error");
    }
    return result;
  }

  /**
   * Retrieves a list of all persons.
   * 
   * @return a list of all persons.
   * @throws KivException if search cannot be performed.
   */
  public List<Person> getAllPersons() throws KivException {
    AndFilter filter = new AndFilter();
    filter.and(new LikeFilter("vgr-id", "*"));

    try {
      String[] attributes = new String[] { "*", "createTimestamp", "modifyTimestamp" };
      // Since UnitMapper returns Units we are certain that the cast to List<Unit> is ok.
      @SuppressWarnings("unchecked")
      List<Person> result = ldapTemplate.search(PERSON_SEARCH_BASE, filter.encode(), SearchControls.SUBTREE_SCOPE, attributes, new PersonMapper(codeTablesService));
      return result;
    } catch (NamingException e) {
      throw new KivException("Error getting persons from server: " + e.getMessage());
    }
  }

  private Person searchPerson(String searchBase, int searchScope, String searchFilter) throws KivException {
    try {
      String[] attributes = new String[] { "*", "createTimestamp", "modifyTimestamp" };
      // Since PersonMapper return Persons we are certain that the suppression is ok
      @SuppressWarnings("unchecked")
      List<Person> result = ldapTemplate.search(searchBase, searchFilter, searchScope, attributes, new PersonMapper(codeTablesService));
      if (result.size() == 0) {
        throw new KivNoDataFoundException("Error getting person from server");
      }
      return result.get(0);
    } catch (NamingException e) {
      throw new KivNoDataFoundException("Error getting person from server: " + e.getMessage());
    }
  }

  private SikSearchResultList<Person> searchPersons(String searchFilter, int searchScope, int maxResult) throws KivException {

    SearchControls searchControls = new SearchControls();
    searchControls.setCountLimit(0);
    searchControls.setSearchScope(searchScope);
    searchControls.setReturningObjFlag(true);
    searchControls.setReturningAttributes(new String[] { "*", "createTimestamp", "modifyTimestamp" });
    // Since PersonMapper returns a Person we are certain that a cast to List<Person> is ok
    @SuppressWarnings("unchecked")
    List<Person> persons = ldapTemplate.search(PERSON_SEARCH_BASE, searchFilter, searchControls, new PersonMapper(codeTablesService));
    SikSearchResultList<Person> result = null;

    if (persons == null) {
      result = new SikSearchResultList<Person>();
    } else {
      result = new SikSearchResultList<Person>(persons);
    }

    // Sort list
    Collections.sort(result, new PersonNameComparator());
    int resultCount = result.size();
    if (result.size() > maxResult && maxResult != 0) {
      result = new SikSearchResultList<Person>(result.subList(0, maxResult));
    }
    result.setTotalNumberOfFoundItems(resultCount);

    return result;
  }

  protected Filter getPersonDNsByEmployment(String searchFilter, int searchScope, int maxResult) throws KivException {
    OrFilter filter = new OrFilter();

    ContextMapper contextMapper = new ContextMapper() {
      @Override
      public String mapFromContext(Object ctx) {
        String returnValue = "";
        DirContextOperations dirContext = (DirContextOperations) ctx;
        Name dn = dirContext.getDn();
        if (dn != null) {
          DistinguishedName distinguishedName = new DistinguishedName(dn);
          distinguishedName.removeFirst(new DistinguishedName(PERSON_SEARCH_BASE));
          returnValue = distinguishedName.removeFirst().toString();
        }
        return returnValue;
      }
    };

    // Since the mapper return a String we are certain that the cast to List<String> is ok
    @SuppressWarnings("unchecked")
    List<String> personDNs = ldapTemplate.search(PERSON_SEARCH_BASE, searchFilter, contextMapper);
    for (String dn : personDNs) {
      filter.or(new EqualsFilter("vgr-id", dn.replace(CN_EQUALS, "")));
    }
    return filter;
  }

  private String createSearchPersonsFilterVgrId(String vgrId) throws KivException {
    String searchFilter = "(&(objectclass=vgrUser)";
    searchFilter += createSearchFilterItem("vgr-id", vgrId);
    searchFilter += ")";
    return searchFilter;
  }

  /**
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   */
  private String createSearchFilterItem(String searchField, String searchValue) {
    String searchFilter = "";
    String currentSearchValue = searchValue;
    if (!StringUtil.isEmpty(currentSearchValue)) {
      currentSearchValue = currentSearchValue.trim();
      if (isExactMatchFilter(currentSearchValue)) {
        // remove "
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, LDAP_EXACT_CARD, "");
        // exact match
        // filterList.add("(" + searchField + "=" + searchValue.trim() + ")");
        // exact match
        searchFilter = "(" + searchField + "=" + currentSearchValue.trim() + ")";
      } else {
        // change spaces to wildcards
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, " ", LDAP_WILD_CARD);
        currentSearchValue = Formatter.replaceStringInString(currentSearchValue, "-", LDAP_WILD_CARD);
        // filterList.add("(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")");
        searchFilter = "(" + searchField + "=" + LDAP_WILD_CARD + currentSearchValue + LDAP_WILD_CARD + ")";
      }
    }
    return searchFilter;
  }

  private boolean isExactMatchFilter(String searchValue) {
    boolean isExactMatch = false;
    // it has to be at least one character between the " e.g. "a" for an exact match
    if (searchValue.length() <= 2) {
      isExactMatch = false;
    } else if (searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
      isExactMatch = true;
    }
    return isExactMatch;
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
    AndFilter filter = new AndFilter();

    // Generate or filter condition
    OrFilter unitFkfilter = new OrFilter();
    for (Unit unit : units) {
      unitFkfilter.or(new EqualsFilter(unitFkField, unit.getHsaIdentity()));
    }

    filter.and(new NotFilter(new EqualsFilter("objectClass", "vgrAnstallning")));
    filter.and(unitFkfilter);

    persons = searchPersons(filter.encode(), SearchControls.SUBTREE_SCOPE, maxResult);
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
  public SikSearchResultList<Person> searchPersons(SearchPersonCriterions person, int maxResult) throws KivException {
    Filter employmentFilter = null;

    if (!StringUtil.isEmpty(person.getEmploymentTitle()) || !StringUtil.isEmpty(person.getEmploymentPosition()) || !StringUtil.isEmpty(person.getPhone())
        || !StringUtil.isEmpty(person.getDescription())) {
      employmentFilter = getPersonDNsByEmployment(generateFreeTextSearchEmploymentFilter(person).encode(), SearchControls.SUBTREE_SCOPE, Integer.MAX_VALUE);
      if (StringUtil.isEmpty(employmentFilter.encode())) {
        return new SikSearchResultList<Person>();
      }
    }

    AndFilter searchPersonFilter = generateFreeTextSearchPersonFilter(person);
    if (employmentFilter != null) {
      searchPersonFilter.and(employmentFilter);
    }

    return searchPersons(searchPersonFilter.encode(), SearchControls.SUBTREE_SCOPE, maxResult);
  }

  private Filter generateFreeTextSearchEmploymentFilter(SearchPersonCriterions person) {
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
    if (!StringUtil.isEmpty(person.getEmploymentTitle())) {
      employmentFilter.and(new LikeFilter(LDAPPersonAttributes.EMPLOYMENT_TITLE.toString(), "*" + person.getEmploymentTitle() + "*"));
    }

    if (!StringUtil.isEmpty(person.getPhone())) {
      OrFilter phonesFilter = new OrFilter();
      phonesFilter.or(new LikeFilter(LDAPEmploymentAttributes.HSA_TELEPHONE_NUMBER.toString(), "*" + person.getPhone() + "*"));
      phonesFilter.or(new LikeFilter(LDAPEmploymentAttributes.MOBILE_TELEPHONE_NUMBER.toString(), "*" + person.getPhone() + "*"));
      phonesFilter.or(new LikeFilter(LDAPEmploymentAttributes.HSA_INTERNAL_PAGER_NUMBER.toString(), "*" + person.getPhone() + "*"));
      phonesFilter.or(new LikeFilter(LDAPEmploymentAttributes.PAGER_TELEPHONE_NUMBER.toString(), "*" + person.getPhone() + "*"));
      phonesFilter.or(new LikeFilter(LDAPEmploymentAttributes.HSA_TEXT_PHONE_NUMBER.toString(), "*" + person.getPhone() + "*"));
      employmentFilter.and(phonesFilter);
    }

    if (!StringUtil.isEmpty(person.getDescription())) {
      employmentFilter.and(new LikeFilter(LDAPEmploymentAttributes.DESCRIPTION.toString(), "*" + person.getDescription() + "*"));
    }

    if (!StringUtil.isEmpty(person.getEmploymentPosition())) {
      addPaTitleCodeFilter(employmentFilter, person);
    }
    return employmentFilter;
  }

  private void addPaTitleCodeFilter(AndFilter employmentFilter, SearchPersonCriterions person) {
    List<String> paTitleCodeList = codeTablesService.getCodeFromTextValue(CodeTableName.PA_TITLE_CODE, person.getEmploymentPosition());
    OrFilter employmentPositionFilter = new OrFilter();
    if (paTitleCodeList != null && paTitleCodeList.size() > 0) {
      for (String code : paTitleCodeList) {
        employmentPositionFilter.or(new LikeFilter(LDAPEmploymentAttributes.PA_TITLE_CODE.toString(), code));
      }
      employmentFilter.and(employmentPositionFilter);
    } else {
      employmentPositionFilter.or(new LikeFilter(LDAPEmploymentAttributes.PA_TITLE_CODE.toString(), "NO_VALID_CODE_TABLE_CODE_FOUND"));
      employmentFilter.and(employmentPositionFilter);
    }

  }

  private AndFilter generateFreeTextSearchPersonFilter(SearchPersonCriterions person) {
    AndFilter userFilter = new AndFilter();
    userFilter.and(new EqualsFilter("objectclass", "vgrUser"));

    if (!StringUtil.isEmpty(person.getGivenName())) {
      OrFilter firstNameFilter = new OrFilter();
      firstNameFilter.or(new LikeFilter(LDAPPersonAttributes.GIVEN_NAME.toString(), "*" + person.getGivenName() + "*"));
      firstNameFilter.or(new LikeFilter("hsaNickName", "*" + person.getGivenName() + "*"));
      userFilter.and(firstNameFilter);
    }
    if (!StringUtil.isEmpty(person.getSurname())) {
      OrFilter surnameFilter = new OrFilter();
      surnameFilter.or(new LikeFilter(LDAPPersonAttributes.SURNAME.toString(), "*" + person.getSurname() + "*"));
      surnameFilter.or(new LikeFilter("hsaMiddleName", "*" + person.getSurname() + "*"));
      userFilter.and(surnameFilter);
    }
    if (!StringUtil.isEmpty(person.getUserId())) {
      userFilter.and(new LikeFilter(LDAPPersonAttributes.USER_ID.toString(), "*" + person.getUserId() + "*"));
    }
    if (!StringUtil.isEmpty(person.getEmployedAtUnit())) {
      userFilter.and(new LikeFilter(LDAPPersonAttributes.EMPLOYED_AT_UNIT.toString(), "*" + person.getEmployedAtUnit() + "*"));
    }
    if (!StringUtil.isEmpty(person.getSpecialityArea())) {
      userFilter.and(generateOrFilterFromList(CodeTableName.HSA_SPECIALITY_CODE, LDAPPersonAttributes.SPECIALITY_AREA_CODE, person.getSpecialityArea()));
    }
    if (!StringUtil.isEmpty(person.getProfession())) {
      userFilter.and(generateOrFilterFromList(CodeTableName.HSA_TITLE, LDAPPersonAttributes.PROFESSION, person.getProfession()));
    }
    if (!StringUtil.isEmpty(person.getEmail())) {
      userFilter.and(new LikeFilter(LDAPPersonAttributes.E_MAIL.toString(), "*" + person.getEmail() + "*"));
    }
    if (!StringUtil.isEmpty(person.getLanguageKnowledge())) {
      userFilter.and(generateOrFilterFromList(CodeTableName.HSA_LANGUAGE_KNOWLEDGE_CODE, LDAPPersonAttributes.LANGUAGE_KNOWLEDGE_CODE, person.getLanguageKnowledge()));
    }
    if (!StringUtil.isEmpty(person.getAdministration())) {
      userFilter.and(generateOrFilterFromList(CodeTableName.VGR_AO3_CODE, LDAPPersonAttributes.ADMINISTRATION, person.getAdministration()));
    }

    userFilter.and(new NotFilter(new EqualsFilter("vgrSecrMark", "J")));

    return userFilter;
  }

  private Filter generateOrFilterFromList(CodeTableName codeTableName, LDAPPersonAttributes criterion, String criterionValue) {
    List<String> codeFromTextValues = codeTablesService.getCodeFromTextValue(codeTableName, criterionValue);
    OrFilter orFilter = new OrFilter();
    if (codeFromTextValues.size() > 0) {
      for (String value : codeFromTextValues) {
        orFilter.or(new LikeFilter(criterion.toString(), value));
      }
    } else {
      orFilter.or(new EqualsFilter(criterion.toString(), "NO_VALID_CODE_TABLE_CODE_FOUND"));
    }
    return orFilter;
  }

}
