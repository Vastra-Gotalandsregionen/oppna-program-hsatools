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
import java.util.List;
import java.util.Map;

import javax.naming.Name;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.ldap.filter.OrFilter;

import se.vgregion.kivtools.search.domain.Unit;
import se.vgregion.kivtools.search.domain.values.DN;
import se.vgregion.kivtools.search.domain.values.HealthcareType;
import se.vgregion.kivtools.search.domain.values.HealthcareTypeConditionHelper;
import se.vgregion.kivtools.search.exceptions.KivException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.impl.SingleAttributeMapper;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.util.Arguments;
import se.vgregion.kivtools.util.StringUtil;
import se.vgregion.kivtools.util.reflection.ReflectionUtil;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class UnitRepository {
  private static final DistinguishedName UNIT_SEARCH_BASE = DistinguishedName.immutableDistinguishedName(Constants.SEARCH_BASE);

  private static final String LDAP_WILD_CARD = "*";
  private static final String LDAP_EXACT_CARD = "\"";
  private Log logger = LogFactory.getLog(this.getClass());
  private LdapTemplate ldapTemplate;

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Advanced means that it also handles healthcareTypeConditions in the search filter.
   * 
   * @param unit Unit to base search filter on.
   * @param maxResult Maximum number of units to return.
   * @param sortOrder Sort order of the result list.
   * @param showUnitsWithTheseHsaBusinessClassificationCodes List only units with these bs codes.
   * @return List of matching units.
   * @throws KivException If something goes wrong.
   */
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) throws KivException {
    Filter searchFilter = createAdvancedSearchFilter(unit, showUnitsWithTheseHsaBusinessClassificationCodes);
    SikSearchResultList<Unit> units = searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE, maxResult, sortOrder);

    removeUnallowedUnits(units, showUnitsWithTheseHsaBusinessClassificationCodes);

    return units;
  }

  /**
   * Remove units that don't have at least one valid hsaBusinessClassificationCode.
   * 
   * @param units
   * @param showUnitsWithTheseHsaBusinessClassificationCodes
   */
  private void removeUnallowedUnits(SikSearchResultList<Unit> units, List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) {

    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    for (int j = units.size() - 1; j >= 0; j--) {
      List<String> businessClassificationCodes = units.get(j).getHsaBusinessClassificationCode();
      boolean found = unitHasValidBusinessClassificationCode(showUnitsWithTheseHsaBusinessClassificationCodes, businessClassificationCodes);

      // The unit might still be valid because of the unfiltered
      // healthcare types
      if (!found) {
        // If this unit does not match any unfiltered health care type, don't include in search result
        found = unitMatchesUnfilteredHealtcareType(units.get(j), allUnfilteredHealthcareTypes);
      }

      if (!found) {
        units.remove(units.get(j));
      }
    }
  }

  private boolean unitMatchesUnfilteredHealtcareType(Unit unit, List<HealthcareType> allUnfilteredHealthcareTypes) {
    boolean found = false;
    for (HealthcareType h : allUnfilteredHealthcareTypes) {
      for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
        String key = condition.getKey();
        String[] conditionValues = condition.getValue().split(",");
        Object value = ReflectionUtil.getProperty(unit, key);

        boolean conditionFulfilled = false;
        if (value instanceof String) {
          for (String v : conditionValues) {
            if (v.equals(value)) {
              conditionFulfilled = true;
            }
          }
        } else if (value instanceof List<?>) {
          for (String v : conditionValues) {
            if (((List<?>) value).contains(v)) {
              conditionFulfilled = true;
            }
          }
        }
        if (conditionFulfilled) {
          found = true;
          break;
        }
      }
    }
    return found;
  }

  private boolean unitHasValidBusinessClassificationCode(List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes, List<String> businessClassificationCodes) {
    boolean found = false;
    for (String s : businessClassificationCodes) {
      try {
        if (showUnitsWithTheseHsaBusinessClassificationCodes.contains(Integer.parseInt(s))) {
          found = true;
        }
      } catch (NumberFormatException e) {
        logger.debug(e);
        // We simply ignore this. hsaBusinessClassificationCodes
        // should be integers, otherwise something is seriously
        // wrong.
      }
    }
    return found;
  }

  /**
   * Search for selected unit.
   * 
   * @param searchUnitCriterions The unit to search for.
   * @param maxResult Max number of units to contain in the result.
   * @return List of found units.
   * @throws KivException .
   */
  public SikSearchResultList<Unit> searchUnits(SearchUnitCriterions searchUnitCriterions, int maxResult) throws KivException {
    Filter searchFilter = createSearchFilter(searchUnitCriterions);
    // String searchFilter = createUnitSearchFilter(unit);
    return searchUnits(searchFilter, SearchControls.SUBTREE_SCOPE, maxResult, new UnitNameComparator());
  }

  /**
   * 
   * @param hsaId Id to search for.
   * @return Found unit object
   * @throws KivException If something goes wrong.
   */
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    Filter searchFilter = new EqualsFilter("hsaIdentity", hsaId);
    return searchUnit(UNIT_SEARCH_BASE, SearchControls.SUBTREE_SCOPE, searchFilter);
  }

  /**
   * 
   * @param dn Dn to search for.
   * @return Found unit.
   * @throws KivException If something goes wrong.
   */
  public Unit getUnitByDN(DN dn) throws KivException {
    DistinguishedName distinguishedName = new DistinguishedName(dn.toString());
    UnitMapper unitMapper = new UnitMapper();

    // UnitMapper return a unit so we are certain that the cast is ok
    Unit unit = (Unit) ldapTemplate.lookup(distinguishedName, unitMapper);

    return unit;
  }

  /**
   * 
   * @return List of all unit identity strings.
   * @throws KivException If something goes wrong.
   */
  public List<String> getAllUnitsHsaIdentity() throws KivException {
    return getAllUnitsHsaIdentity(new ArrayList<Integer>());
  }

  /**
   * 
   * @param showUnitsWithTheseHsaBusinessClassificationCodes Units to include to the result list.
   * @return List of all unit identity strings.
   * @throws KivException If something goes wrong.
   */
  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) throws KivException {
    Filter filter = createAllUnitsFilter(showUnitsWithTheseHsaBusinessClassificationCodes);

    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    List<String> result = new ArrayList<String>();

    do {
      // SingleAttributeMapper return a String so we are pretty certain that List<String> is ok.
      @SuppressWarnings("unchecked")
      List<String> resultList = this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, new SingleAttributeMapper("hsaIdentity"), control);
      // Put everything in a map to remove duplicates.
      for (String hsaIdentity : resultList) {
        result.add(hsaIdentity);
      }
    } while (control.getCookie().getCookie() != null);

    return result;
  }

  /**
   * Retrieves a list of all Units and functions filtered with showUnitsWithTheseHsaBusinessClassificationCodes.
   * 
   * @param showUnitsWithTheseHsaBusinessClassificationCodes Only select units from search that has business codes from this list.
   * @return A list of units.
   */
  public List<Unit> getAllUnits(List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) {
    Filter filter = createAllUnitsFilter(showUnitsWithTheseHsaBusinessClassificationCodes);

    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    UnitMapper unitMapper = new UnitMapper();
    List<Unit> result = new ArrayList<Unit>();

    do {
      // UnitMapper return a Unit so we are pretty certain that List<Unit> is ok.
      @SuppressWarnings("unchecked")
      List<Unit> resultList = this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, unitMapper, control);
      // Put everything in a map to remove duplicates.
      for (Unit unit : resultList) {
        result.add(unit);
      }
    } while (control.getCookie().getCookie() != null);

    return result;
  }

  private Filter createAllUnitsFilter(List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) {
    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    OrFilter objectClassFilter = new OrFilter();
    objectClassFilter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
    objectClassFilter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_FUNCTION_SPECIFIC));

    Filter businessClassificationCodeFilter = createBusinessClassificationCodeFilter(showUnitsWithTheseHsaBusinessClassificationCodes, allUnfilteredHealthcareTypes);

    AndFilter filter = new AndFilter();
    filter.and(businessClassificationCodeFilter);
    filter.and(objectClassFilter);
    return filter;
  }

  /**
   * Creates a filter for the provided business classification codes and the provided healthcare types.
   * 
   * @param businessClassificationCodes The list of business classification codes to include.
   * @param healthcareTypes The list of health care types to include.
   * @return A populated filter.
   */
  private Filter createBusinessClassificationCodeFilter(List<Integer> businessClassificationCodes, List<HealthcareType> healthcareTypes) {
    OrFilter filter = new OrFilter();

    if (businessClassificationCodes.size() > 0) {
      for (Integer businessClassificationCode : businessClassificationCodes) {
        filter.or(new EqualsFilter("businessClassificationCode", businessClassificationCode));
      }

      for (HealthcareType healthcareType : healthcareTypes) {
        filter.or(createHealthCareTypeFilter(healthcareType));
      }
    }

    return filter;
  }

  /**
   * Creates a filter for the provided health care type.
   * 
   * @param healthcareType The health care type to create a filter for.
   * @return A populated filter.
   */
  private Filter createHealthCareTypeFilter(HealthcareType healthcareType) {
    Arguments.notNull("healthcareType", healthcareType);

    AndFilter filter = new AndFilter();
    for (Map.Entry<String, String> condition : healthcareType.getConditions().entrySet()) {
      OrFilter conditionValueFilter = new OrFilter();
      String[] conditionValues = condition.getValue().split(",");
      for (String conditionValue : conditionValues) {
        conditionValueFilter.or(new EqualsFilter(condition.getKey(), conditionValue));
      }

      filter.and(conditionValueFilter);
    }

    return filter;
  }

  private SikSearchResultList<Unit> searchUnits(Filter searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) {
    SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
    UnitMapper unitMapper = new UnitMapper();
    // Since UnitMapper returns unit the assignment to List<Unit> is safe
    @SuppressWarnings("unchecked")
    List<Unit> searchResult = ldapTemplate.search(UNIT_SEARCH_BASE, searchFilter.encode(), searchScope, unitMapper);
    result.addAll(searchResult);

    // Make sure we don't return duplicates
    SikSearchResultList<Unit> resultNoDuplicates = deduplicateUnits(result);

    Comparator<Unit> effectiveSortOrder = sortOrder;
    if (effectiveSortOrder == null) {
      // No sort order was supplied, default to sorting on unit name.
      effectiveSortOrder = new UnitNameComparator();
    }

    Collections.sort(resultNoDuplicates, effectiveSortOrder);

    int resultCount = resultNoDuplicates.size();
    if (resultNoDuplicates.size() > maxResult && maxResult != 0) {
      resultNoDuplicates = new SikSearchResultList<Unit>(resultNoDuplicates.subList(0, maxResult));
    }
    resultNoDuplicates.setTotalNumberOfFoundItems(resultCount);

    return resultNoDuplicates;
  }

  private Unit searchUnit(Name searchBase, int searchScope, Filter searchFilter) {
    // UnitMapper return a Unit so we are pretty certain that List<Unit> is ok.
    @SuppressWarnings("unchecked")
    List<Unit> searchResult = ldapTemplate.search(searchBase, searchFilter.encode(), searchScope, new UnitMapper());

    Unit result = new Unit();
    if (searchResult.size() > 0) {
      result = searchResult.get(0);
    }
    return result;
  }

  private SikSearchResultList<Unit> deduplicateUnits(SikSearchResultList<Unit> result) {
    SikSearchResultList<Unit> resultNoDuplicates = new SikSearchResultList<Unit>();
    for (Unit u : result) {
      // Would like to use "contains" which uses equals (where you could
      // test for same hsa-id) but that would break the searching.
      boolean alreadyExists = false;
      for (Unit uND : resultNoDuplicates) {
        if (u.getHsaIdentity().equals(uND.getHsaIdentity())) {
          alreadyExists = true;
          break;
        }
      }
      if (!alreadyExists) {
        resultNoDuplicates.add(u);
      }
    }

    return resultNoDuplicates;
  }

  /**
   * create search filter that search for both Units (and Functions).
   * 
   * @param unit
   * @return
   * @throws Exception
   */
  private Filter createSearchFilter(SearchUnitCriterions unit) throws KivException {
    // create a plain unit search filter
    Filter unitSearchFilter = createUnitSearchFilter(unit, Constants.OBJECT_CLASS_UNIT_SPECIFIC, Constants.LDAP_PROPERTY_UNIT_NAME);

    // create a plain function search filter
    Filter functionSearchFilter = createUnitSearchFilter(unit, Constants.OBJECT_CLASS_FUNCTION_SPECIFIC, Constants.LDAP_PROPERTY_FUNCTION_NAME);

    OrFilter searchFilter = new OrFilter();
    searchFilter.or(unitSearchFilter);
    searchFilter.or(functionSearchFilter);
    return searchFilter;
  }

  /**
   * create search filter that search for both Units (and Functions).
   * 
   * @param unit
   * @param showUnitsWithTheseHsaBusinessClassificationCodes
   * @return
   * @throws Exception
   */
  private Filter createAdvancedSearchFilter(Unit unit, List<Integer> showUnitsWithTheseHsaBusinessClassificationCodes) throws KivException {
    Filter unitSearch = createAdvancedUnitSearchFilter(unit, Constants.OBJECT_CLASS_UNIT_SPECIFIC, Constants.LDAP_PROPERTY_UNIT_NAME);
    Filter functionSearch = createAdvancedUnitSearchFilter(unit, Constants.OBJECT_CLASS_FUNCTION_SPECIFIC, Constants.LDAP_PROPERTY_FUNCTION_NAME);

    OrFilter searchFilter = new OrFilter();
    searchFilter.or(unitSearch);
    searchFilter.or(functionSearch);

    return searchFilter;
  }

  private Filter createUnitSearchFilter(SearchUnitCriterions searchUnitCriterions, String objectClass, String unitNameProperty) throws KivException {
    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("objectclass", objectClass));
    AndFilter andFilter2 = new AndFilter();

    // Create or hsaIdentity
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitId())) {
      OrFilter orHsaIdentity = new OrFilter();
      orHsaIdentity.or(createSearchFilter("hsaIdentity", searchUnitCriterions.getUnitId()));
      andFilter2.and(orHsaIdentity);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitName())) {
      OrFilter orUnitName = new OrFilter();
      orUnitName.or(createSearchFilter(unitNameProperty, searchUnitCriterions.getUnitName()));
      andFilter2.and(orUnitName);
    }

    // create or criteria
    if (!StringUtil.isEmpty(searchUnitCriterions.getLocation())) {
      OrFilter orMunicipalityAndAddresses = new OrFilter();
      OrFilter orMunicipalityName = new OrFilter();
      orMunicipalityName.or(createSearchFilter("municipalityName", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation())));
      Filter orPostalAddress = createSearchFilter("postalAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      Filter orStreetAddress = createSearchFilter("streetAddress", LdapParse.escapeLDAPSearchFilter(searchUnitCriterions.getLocation()));
      orMunicipalityAndAddresses.or(orMunicipalityName).or(orPostalAddress).or(orStreetAddress);
      andFilter2.and(orMunicipalityAndAddresses);
    }
    andFilter.and(andFilter2);
    return andFilter;
  }

  private Filter createAdvancedUnitSearchFilter(Unit unit, String objectClass, String unitNameProperty) throws KivException {
    AndFilter searchFilter = new AndFilter();
    searchFilter.and(new EqualsFilter("objectClass", objectClass));

    OrFilter additionalCriterias = new OrFilter();
    if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
      additionalCriterias.or(createSearchFilter("municipalityName", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName())));
    }
    if (!StringUtil.isEmpty(unit.getHsaMunicipalityCode())) {
      additionalCriterias.or(createSearchFilter("municipalityCode", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityCode())));
    }
    if (!StringUtil.isEmpty(unit.getHsaMunicipalityName())) {
      additionalCriterias.or(addAddressSearchFilter("postalAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName())));
      additionalCriterias.or(addAddressSearchFilter("streetAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName())));
    }
    // Add like search for unit name in description field
    if (!StringUtil.isEmpty(unit.getName())) {
      additionalCriterias.or(createSearchFilter(Constants.LDAP_PROPERTY_DESCRIPTION, unit.getName()));
      additionalCriterias.or(createSearchFilter(unitNameProperty, unit.getName()));
    }

    searchFilter.and(additionalCriterias);
    if (!StringUtil.isEmpty(unit.getHsaIdentity())) {
      searchFilter.and(createSearchFilter("hsaIdentity", unit.getHsaIdentity()));
    }

    // Take all health care type conditions into consideration...
    if (unit.getHealthcareTypes() != null && unit.getHealthcareTypes().size() > 0) {
      searchFilter.and(createHealtCareTypeConditions(unit.getHealthcareTypes()));
    }

    return searchFilter;
  }

  private Filter createHealtCareTypeConditions(List<HealthcareType> healthcareTypes) {
    OrFilter healthCareTypeFilter = new OrFilter();

    for (HealthcareType healthcareType : healthcareTypes) {
      AndFilter conditionFilter = new AndFilter();
      // All conditions for the unfiltered health care type must be taken
      // into consideration
      for (Map.Entry<String, String> condition : healthcareType.getConditions().entrySet()) {
        OrFilter valueFilter = new OrFilter();
        String[] conditionValues = condition.getValue().split(",");
        for (int i = 0; i < conditionValues.length; i++) {
          valueFilter.or(new EqualsFilter(condition.getKey(), conditionValues[i]));
        }
        conditionFilter.and(valueFilter);
      }
      healthCareTypeFilter.or(conditionFilter);
    }

    return healthCareTypeFilter;
  }

  /**
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   */
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
   * e.g. searchField=hsaPostalAddress searchValue="uddevalla" result= (|(hsaPostalAddress =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*) (hsaPostalAddress
   * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*) (hsaPostalAddress =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
   * 
   * @throws KivException
   */
  private Filter addAddressSearchFilter(String searchField, String searchValue) {
    String searchValueCurrent = searchValue;
    Filter temp = null;
    if (!StringUtil.isEmpty(searchValueCurrent)) {
      searchValueCurrent = searchValueCurrent.trim();
      if (isExactMatchFilter(searchValueCurrent)) {
        // remove "
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, LDAP_EXACT_CARD, "");
        temp = buildAddressSearch(searchField, searchValueCurrent);
        // exact match
      } else {
        // change spaces to wildcards
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, " ", LDAP_WILD_CARD);
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, "-", LDAP_WILD_CARD);
        searchValueCurrent = LDAP_WILD_CARD + searchValueCurrent + LDAP_WILD_CARD;
        temp = buildAddressSearch(searchField, searchValueCurrent);
      }
    }
    return temp;
  }

  /**
   * e.g. searchField=hsaPostalAddress searchValue="*uddevalla*" result= (|(hsaPostalAddress =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*) (hsaPostalAddress
   * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*) (hsaPostalAddress =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
   **/
  private Filter buildAddressSearch(String searchField, String searchValue) {
    // StringBuffer buf = new StringBuffer();
    OrFilter orFilter = new OrFilter();
    for (int i = 0; i < 12; i += 2) {
      StringBuffer buf2 = new StringBuffer("*$*$*$*$*$*");
      buf2.replace(i, i + 1, searchValue);
      orFilter.or(new LikeFilter(searchField, buf2.toString()));
    }

    return orFilter;
  }

  private boolean isExactMatchFilter(String searchValue) {
    boolean exactMatch = false;

    // it has to be at least one character between the " e.g. "a" for an exact match
    if (searchValue.length() > 2 && searchValue.startsWith(LDAP_EXACT_CARD) && searchValue.endsWith(LDAP_EXACT_CARD)) {
      exactMatch = true;
    }

    return exactMatch;
  }
}
