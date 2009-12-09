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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.control.PagedResultsCookie;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
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
import se.vgregion.kivtools.search.exceptions.SikInternalException;
import se.vgregion.kivtools.search.svc.SikSearchResultList;
import se.vgregion.kivtools.search.svc.comparators.UnitNameComparator;
import se.vgregion.kivtools.search.svc.ldap.LdapConnectionPool;
import se.vgregion.kivtools.search.svc.ldap.criterions.SearchUnitCriterions;
import se.vgregion.kivtools.search.util.Formatter;
import se.vgregion.kivtools.search.util.LdapParse;
import se.vgregion.kivtools.util.Arguments;
import se.vgregion.kivtools.util.StringUtil;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;

/**
 * @author Anders and Hans, Know IT
 * @author Jonas Liljenfeldt, Know IT
 */
public class UnitRepository {
  public static final String KIV_SEARCH_BASE = "OU=Landstinget Halland,DC=lthallandhsa,DC=se";

  private static final int POOL_WAIT_TIME_MILLISECONDS = 2000;
  private static final String LDAP_WILD_CARD = "*";
  private static final String LDAP_EXACT_CARD = "\"";
  private Log logger = LogFactory.getLog(this.getClass());
  private LdapConnectionPool theConnectionPool;
  private LdapTemplate ldapTemplate;

  public void setLdapConnectionPool(LdapConnectionPool lp) {
    this.theConnectionPool = lp;
  }

  public void setLdapTemplate(LdapTemplate ldapTemplate) {
    this.ldapTemplate = ldapTemplate;
  }

  /**
   * Advanced means that it also handles healthcareTypeConditions in the search filter.
   * 
   * @param unit Unit to base search filter on.
   * @param maxResult Maximum number of units to return.
   * @param sortOrder Sort order of the result list.
   * @param showUnitsWithTheseHsaBussinessClassificationCodes List only units with these bs codes.
   * @return List of matching units.
   * @throws KivException If something goes wrong.
   */
  public SikSearchResultList<Unit> searchAdvancedUnits(Unit unit, int maxResult, Comparator<Unit> sortOrder, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException {
    String searchFilter = createAdvancedSearchFilter(unit, showUnitsWithTheseHsaBussinessClassificationCodes);
    SikSearchResultList<Unit> units = searchUnits(searchFilter, LDAPConnection.SCOPE_SUB, maxResult, sortOrder);

    removeUnallowedUnits(units, showUnitsWithTheseHsaBussinessClassificationCodes);

    return units;
  }

  /**
   * Remove units that don't have at least one valid hsaBusinessClassificationCode.
   * 
   * @param units
   * @param showUnitsWithTheseHsaBussinessClassificationCodes
   */
  private void removeUnallowedUnits(SikSearchResultList<Unit> units, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) {

    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    for (int j = units.size() - 1; j >= 0; j--) {
      boolean found = false;
      for (String s : units.get(j).getHsaBusinessClassificationCode()) {
        try {
          if (showUnitsWithTheseHsaBussinessClassificationCodes.contains(Integer.parseInt(s))) {
            found = true;
          }
        } catch (NumberFormatException e) {
          // We simply ignore this. hsaBusinessClassificationCodes
          // should be integers, otherwise something is seriously
          // wrong.
          logger.error("Unable to parse hsaBusinessClassificationCode", e);
        }
      }

      // The unit might still be valid because of the unfiltered
      // healthcare types
      if (!found) {
        // If this unit does not match any unfiltered health care type,
        // don't include in search result
        unfilteredHealthCareTypesLoop: for (HealthcareType h : allUnfilteredHealthcareTypes) {
          for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
            String key = condition.getKey();
            key = key.substring(0, 1).toUpperCase() + key.substring(1);
            String[] conditionValues = condition.getValue().split(",");
            for (int i = 0; i < conditionValues.length; i++) {
              Method keyMethod;
              Object value = null;
              try {
                keyMethod = units.get(j).getClass().getMethod("get" + key, null);
                value = keyMethod.invoke(units.get(j), null);
              } catch (SecurityException e) {
                e.printStackTrace();
              } catch (NoSuchMethodException e) {
                e.printStackTrace();
              } catch (IllegalArgumentException e) {
                e.printStackTrace();
              } catch (IllegalAccessException e) {
                e.printStackTrace();
              } catch (InvocationTargetException e) {
                e.printStackTrace();
              }
              boolean conditionFulfilled = false;
              if (value instanceof String) {
                if (value != null) {
                  for (String v : conditionValues) {
                    if (v.equals(value)) {
                      conditionFulfilled = true;
                    }
                  }
                }
              } else if (value instanceof List) {
                for (String v : conditionValues) {
                  if (((List<String>) value).contains(v)) {
                    conditionFulfilled = true;
                  }
                }
              }
              if (conditionFulfilled) {
                found = true;
                break unfilteredHealthCareTypesLoop;
              }
            }
          }
        }
      }

      if (!found) {
        units.remove(units.get(j));
      }
    }
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
    String searchFilter = createSearchFilter(searchUnitCriterions);
    // String searchFilter = createUnitSearchFilter(unit);
    return searchUnits(searchFilter, LDAPConnection.SCOPE_SUB, maxResult, new UnitNameComparator());
  }

  /**
   * 
   * @param hsaId Id to search for.
   * @return Found unit object
   * @throws KivException If something goes wrong.
   */
  public Unit getUnitByHsaId(String hsaId) throws KivException {
    String searchFilter = "(hsaIdentity=" + hsaId + ")";
    return searchUnit(Constants.SEARCH_BASE, LDAPConnection.SCOPE_SUB, searchFilter);
  }

  /**
   * 
   * @param dn Dn to search for.
   * @return Found unit.
   * @throws KivException If something goes wrong.
   */
  public Unit getUnitByDN(DN dn) throws KivException {
    Unit u = null;
    String dnPath = dn.escape().toString();

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        u = UnitFactory.reconstitute(lc.read(dnPath));
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }
    return u;
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
   * @param showUnitsWithTheseHsaBussinessClassificationCodes Units to include to the result list.
   * @return List of all unit identity strings.
   * @throws KivException If something goes wrong.
   */
  public List<String> getAllUnitsHsaIdentity(List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException {
    // Get all health care types that are unfiltered
    HealthcareTypeConditionHelper htch = new HealthcareTypeConditionHelper();
    List<HealthcareType> allUnfilteredHealthcareTypes = htch.getAllUnfilteredHealthCareTypes();

    OrFilter objectClassFilter = new OrFilter();
    objectClassFilter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
    objectClassFilter.or(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_FUNCTION_SPECIFIC));

    Filter businessClassificationCodeFilter = createBusinessClassificationCodeFilter(showUnitsWithTheseHsaBussinessClassificationCodes, allUnfilteredHealthcareTypes);

    AndFilter filter = new AndFilter();
    filter.and(businessClassificationCodeFilter);
    filter.and(objectClassFilter);

    PagedResultsCookie cookie = null;
    PagedResultsDirContextProcessor control = new PagedResultsDirContextProcessor(100, cookie);
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    List<String> result = new ArrayList<String>();

    do {
      // HsaIdentityMapper return a String so we are pretty certain that List<String> is ok.
      @SuppressWarnings("unchecked")
      List<String> resultList = this.ldapTemplate.search(Constants.SEARCH_BASE, filter.encode(), searchControls, new HsaIdentityMapper(), control);
      // Put everything in a map to remove duplicates.
      for (String hsaIdentity : resultList) {
        result.add(hsaIdentity);
      }
    } while (control.getCookie().getCookie() != null);

    return result;
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

  protected SikSearchResultList<Unit> searchUnits(String searchFilter, int searchScope, int maxResult, Comparator<Unit> sortOrder) throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
    // Get all attributes, including operational attribute createTimeStamp
    String[] attributes = { LDAPConnection.ALL_USER_ATTRS, "createTimeStamp" };

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        result = extractResult(lc.search(Constants.SEARCH_BASE, searchScope, searchFilter, attributes, false, constraints), maxResult, sortOrder);
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;
  }

  private Unit searchUnit(String searchBase, int searchScope, String searchFilter) throws KivException {
    LDAPSearchConstraints constraints = new LDAPSearchConstraints();
    constraints.setMaxResults(0);
    Unit result = new Unit();
    // Get all attributes, including operational attribute createTimeStamp
    String[] attributes = { LDAPConnection.ALL_USER_ATTRS, "createTimeStamp" };

    try {
      LDAPConnection lc = getLDAPConnection();
      try {
        result = extractSingleResult(lc.search(searchBase, searchScope, searchFilter, attributes, false, constraints));
      } finally {
        theConnectionPool.freeConnection(lc);
      }
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }

    return result;
  }

  private LDAPConnection getLDAPConnection() throws KivException {
    LDAPConnection lc = theConnectionPool.getConnection(POOL_WAIT_TIME_MILLISECONDS);
    if (lc == null) {
      throw new SikInternalException(this, "getLDAPConnection()", "Could not get a connection after waiting " + POOL_WAIT_TIME_MILLISECONDS + " ms.");
    }
    return lc;
  }

  private Unit extractSingleResult(LDAPSearchResults searchResults) throws KivException {
    try {
      return UnitFactory.reconstitute(searchResults.next());
    } catch (LDAPException e) {
      throw new KivException("An error occured in communication with the LDAP server. Message: " + e.getMessage());
    }
  }

  private SikSearchResultList<Unit> extractResult(LDAPSearchResults searchResults, int maxResult, Comparator<Unit> sortOrder) throws KivException {
    SikSearchResultList<Unit> result = new SikSearchResultList<Unit>();
    while (searchResults.hasMore()) {
      try {
        result.add(UnitFactory.reconstitute(searchResults.next()));
      } catch (LDAPException e) {
        if (e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR) {
          break;
        } else {
          continue;
        }
      }
    }

    // Make sure we don't return duplicates
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

    if (sortOrder == null) {
      // No sort order was supplied, default to sorting on unit name.
      sortOrder = new UnitNameComparator();
    }

    Collections.sort(resultNoDuplicates, sortOrder);
    int resultCount = resultNoDuplicates.size();
    if (resultNoDuplicates.size() > maxResult && maxResult != 0) {
      resultNoDuplicates = new SikSearchResultList<Unit>(resultNoDuplicates.subList(0, maxResult));
    }
    resultNoDuplicates.setTotalNumberOfFoundItems(resultCount);

    return resultNoDuplicates;
  }

  /**
   * Creates a search string valid for functions from a search string valid for Units Input.
   * 
   * @param unitSearchString a full search criteria for unitSearch
   * @return A LDAP search string for search among functions
   * @throws Exception
   */
  String makeFunctionSearchStringFromUnitSearchString(String unitSearchString) throws KivException {
    String functionSearchString = Formatter.replaceStringInString(unitSearchString, Constants.OBJECT_CLASS_UNIT_SPECIFIC, Constants.OBJECT_CLASS_FUNCTION_SPECIFIC);
    functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.OBJECT_CLASS_UNIT_STANDARD, Constants.OBJECT_CLASS_FUNCTION_STANDARD);
    functionSearchString = Formatter.replaceStringInString(functionSearchString, Constants.LDAP_PROPERTY_UNIT_NAME + "=", Constants.LDAP_PROPERTY_FUNCTION_NAME + "=");
    return functionSearchString;
  }

  /**
   * create search filter that search for both Units (and Functions).
   * 
   * @param unit
   * @return
   * @throws Exception
   */
  String createSearchFilter(SearchUnitCriterions unit) throws KivException {
    // create a plain unit search filter
    String unitSearchString = createUnitSearchFilter(unit);

    // create a plain function search filter
    String functionSearchString = makeFunctionSearchStringFromUnitSearchString(unitSearchString);

    // add both filters together
    List<String> filterList = new ArrayList<String>();
    filterList.add(unitSearchString);
    filterList.add(functionSearchString);
    // (|(par1=value1)(par2=value2))
    String fullSearchString = makeOr(filterList);
    return fullSearchString;
  }

  /**
   * create search filter that search for both Units (and Functions).
   * 
   * @param unit
   * @param showUnitsWithTheseHsaBussinessClassificationCodes
   * @return
   * @throws Exception
   */
  String createAdvancedSearchFilter(Unit unit, List<Integer> showUnitsWithTheseHsaBussinessClassificationCodes) throws KivException {
    // create a plain unit search filter
    String unitSearchString = createAdvancedUnitSearchFilter(unit);

    // create a plain function search filter
    String functionSearchString = makeFunctionSearchStringFromUnitSearchString(unitSearchString);

    // add filters together
    List<String> filterList = new ArrayList<String>();
    if (!"".equals(unitSearchString)) {
      filterList.add(unitSearchString);
    }
    if (!"".equals(functionSearchString)) {
      filterList.add(functionSearchString);
    }
    // (|(par1=value1)(par2=value2))
    String orCriterias = makeOr(filterList);
    return orCriterias;
  }

  String createUnitSearchFilter(SearchUnitCriterions searchUnitCriterions) throws KivException {
    AndFilter andFilter = new AndFilter();
    andFilter.and(new EqualsFilter("objectclass", Constants.OBJECT_CLASS_UNIT_SPECIFIC));
    AndFilter andFilter2 = new AndFilter();

    // Create or hsaIdentity
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitId())) {
      OrFilter orHsaIdentity = new OrFilter();
      orHsaIdentity.or(createSearchFilter("hsaIdentity", searchUnitCriterions.getUnitId()));
      andFilter2.and(orHsaIdentity);
    }
    if (!StringUtil.isEmpty(searchUnitCriterions.getUnitName())) {
      OrFilter orUnitName = new OrFilter();
      orUnitName.or(createSearchFilter(Constants.LDAP_PROPERTY_UNIT_NAME, searchUnitCriterions.getUnitName()));
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
    return andFilter.encode();
  }

  String createAdvancedUnitSearchFilter(Unit unit) throws KivException {
    List<String> filterList = new ArrayList<String>();

    String searchFilter = "(&(objectclass=" + Constants.OBJECT_CLASS_UNIT_SPECIFIC + ")";

    // or criterias
    addSearchFilter(filterList, "municipalityName", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
    addSearchFilter(filterList, "municipalityCode", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityCode()));

    // a bit special...
    addAddressSearchFilter(filterList, "postalAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));

    addAddressSearchFilter(filterList, "streetAddress", LdapParse.escapeLDAPSearchFilter(unit.getHsaMunicipalityName()));
    // (|(par1=value1)(par2=value2))
    String orCriterias = makeOr(filterList);

    filterList = new ArrayList<String>();
    addSearchFilter(filterList, Constants.LDAP_PROPERTY_UNIT_NAME, unit.getName());
    if (!StringUtil.isEmpty(orCriterias)) {
      filterList.add(orCriterias);
    }

    addSearchFilter(filterList, "hsaIdentity", unit.getHsaIdentity());

    // Take all health care type conditions into consideration...
    if (unit.getHealthcareTypes() != null && unit.getHealthcareTypes().size() > 0) {
      addHealthCareTypeConditions(filterList, unit.getHealthcareTypes());
    }

    // (&(par3=value3)(par4=value4
    String andCriterias = makeAnd(filterList);
    // ))
    /*
     * BT 090528 Do not return null, it can be a search for all units if (Evaluator.isEmpty(andCriterias)) { return null; }
     */
    searchFilter = searchFilter + andCriterias + ")";
    return searchFilter;
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
   * e.g. searchField=givenName searchValue=hans result=(givenName=*hans*)
   * 
   * e.g. searchField=givenName searchValue=hans-erik result=(givenName=*hans*erik*)
   * 
   * e.g. searchField=givenName searchValue="hans-erik" result=(givenName=hans-erik)
   * 
   * @throws KivException
   */
  private void addSearchFilter(List<String> filterList, String searchField, String searchValue) throws KivException {
    if (!StringUtil.isEmpty(searchValue)) {
      searchValue = searchValue.trim();
      if (isExactMatchFilter(searchValue)) {
        // remove "
        searchValue = Formatter.replaceStringInString(searchValue, LDAP_EXACT_CARD, "");
        // exact match
        filterList.add("(" + searchField + "=" + searchValue.trim() + ")");
      } else {
        // change spaces to wildcards
        searchValue = Formatter.replaceStringInString(searchValue, " ", LDAP_WILD_CARD);
        searchValue = Formatter.replaceStringInString(searchValue, "-", LDAP_WILD_CARD);
        filterList.add("(" + searchField + "=" + LDAP_WILD_CARD + searchValue + LDAP_WILD_CARD + ")");
      }
    }
  }

  /**
   * e.g. searchField=hsaPostalAddress searchValue="uddevalla" result= (|(hsaPostalAddress =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*) (hsaPostalAddress
   * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*) (hsaPostalAddress =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
   * 
   * @throws KivException
   */
  Filter addAddressSearchFilter(List<String> filterList, String searchField, String searchValue) {
    String searchValueCurrent = searchValue;
    Filter temp = null;
    if (!StringUtil.isEmpty(searchValueCurrent)) {
      searchValueCurrent = searchValueCurrent.trim();
      if (isExactMatchFilter(searchValueCurrent)) {
        // remove "
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, LDAP_EXACT_CARD, "");
        temp = buildAddressSearch(searchField, searchValueCurrent);
        // exact match
        filterList.add(temp.encode());
      } else {
        // change spaces to wildcards
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, " ", LDAP_WILD_CARD);
        searchValueCurrent = Formatter.replaceStringInString(searchValueCurrent, "-", LDAP_WILD_CARD);
        searchValueCurrent = LDAP_WILD_CARD + searchValueCurrent + LDAP_WILD_CARD;
        temp = buildAddressSearch(searchField, searchValueCurrent);
        // wildcard match
        filterList.add(temp.encode());
      }
    }
    return temp;
  }

  /**
   * e.g. searchField=hsaPostalAddress searchValue="*uddevalla*" result= (|(hsaPostalAddress =*uddevalla*$*$*$*$*$*)(hsaPostalAddress=*$*uddevalla*$*$*$*$*) (hsaPostalAddress
   * =*$*$*uddevalla*$*$*$*)(hsaPostalAddress=*$*$*$*uddevalla*$*$*) (hsaPostalAddress =*$*$*$*$*uddevalla*$*)(hsaPostalAddress=*$*$*$*$*$*uddevalla*))
   **/
  Filter buildAddressSearch(String searchField, String searchValue) {
    // StringBuffer buf = new StringBuffer();
    OrFilter orFilter = new OrFilter();
    for (int i = 0; i < 12; i += 2) {
      StringBuffer buf2 = new StringBuffer("*$*$*$*$*$*");
      buf2.replace(i, i + 1, searchValue);
      if (isExactMatchFilter(searchValue)) {
        orFilter.or(new EqualsFilter(searchField, buf2.toString()));
      } else {
        orFilter.or(new LikeFilter(searchField, buf2.toString()));
      }
    }

    return orFilter;
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

  private String makeOr(List<String> orFilterList) {
    String orCriterias = "";
    if (!orFilterList.isEmpty()) {
      orCriterias += "(|";
      for (String s : orFilterList) {
        orCriterias += s;
      }
      orCriterias += ")";
    }
    return orCriterias;
  }

  private String makeAnd(List<String> orFilterList) {
    String orCriterias = "";
    if (!orFilterList.isEmpty()) {
      orCriterias += "(&";
      for (String s : orFilterList) {
        orCriterias += s;
      }
      orCriterias += ")";
    }
    return orCriterias;
  }

  /**
   * Add conditions generated from supplied health care types to (also) supplied filterList.
   * 
   * @param filterList
   * @param allUnfilteredHealthcareTypes
   */
  private void addHealthCareTypeConditions(List<String> filterList, List<HealthcareType> allUnfilteredHealthcareTypes) {
    for (HealthcareType h : allUnfilteredHealthcareTypes) {

      // All conditions for the unfiltered health care type must be taken
      // into consideration
      List<String> conditionComponents = new ArrayList<String>();
      for (Map.Entry<String, String> condition : h.getConditions().entrySet()) {
        List<String> conditionComponentFilter = new ArrayList<String>();
        String[] conditionValues = condition.getValue().split(",");
        for (int i = 0; i < conditionValues.length; i++) {
          conditionComponentFilter.add("(" + condition.getKey() + "=" + conditionValues[i] + ")");
        }
        String conditionComponentsQuery = makeOr(conditionComponentFilter);
        conditionComponents.add(conditionComponentsQuery);
      }
      String healthCareTypeCondition = makeAnd(conditionComponents);

      // Add to top level, should be okay in same way as a valid
      // hsaBusinessClassificationCode
      filterList.add(healthCareTypeCondition);
    }
  }
}
